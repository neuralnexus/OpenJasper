/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jaxrs.resources;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.SelfCleaningFileResourceDataWrapper;
import com.jaspersoft.jasperserver.dto.common.PatchDescriptor;
import com.jaspersoft.jasperserver.dto.common.PatchItem;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientUriHolder;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.dto.resources.ResourceMultipartConstants;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.ModificationNotAllowedException;
import com.jaspersoft.jasperserver.remote.exception.PatchException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.VersionNotMatchException;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.resources.converters.ToClientConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToClientConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import com.jaspersoft.jasperserver.remote.services.SingleRepositoryService;
import com.jaspersoft.jasperserver.remote.spel.ReferenceableTypeConverter;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ResourceDetailsJaxrsService.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
@Component
public class ResourceDetailsJaxrsService {
    public static final String PATH_PARAM_URI = "uri";
    @javax.annotation.Resource
    private ResourceConverterProvider resourceConverterProvider;
    @javax.annotation.Resource
    private SingleRepositoryService singleRepositoryService;
    @javax.annotation.Resource
    private Map<String, String> contentTypeMapping;

    public Response getResourceDetails(String uri, String accept, Boolean _expanded) throws RemoteException {
        boolean expanded = _expanded != null ? _expanded : false;
        Resource resource = singleRepositoryService.getResource(uri);
        if (resource == null) {
            throw new ResourceNotFoundException(uri);
        }
        Response response;

        if ((resource instanceof FileResource || resource instanceof ContentResource) && !ResourceMediaType.FILE_XML.equals(accept) && !ResourceMediaType.FILE_JSON.equals(accept)) {
            FileResourceData data = singleRepositoryService.getFileResourceData(resource);
            FileResourceData wrapper = new SelfCleaningFileResourceDataWrapper(data);
            String type = resource instanceof FileResource ? ((FileResource) resource).getFileType() : ((ContentResource) resource).getFileType();
            response = toResponse(wrapper, resource.getName(), type);
        } else {
            final ToClientConverter<? super Resource, ? extends ClientResource> toClientConverter =
                    resourceConverterProvider.getToClientConverter(resource);
            final ClientResource clientResource = toClientConverter.toClient(resource, ToClientConversionOptions.getDefault().setExpanded(expanded));
            String contentTypeTemplate = accept != null && accept.endsWith("json") ? ResourceMediaType.RESOURCE_JSON_TEMPLATE : ResourceMediaType.RESOURCE_XML_TEMPLATE;
            response = Response.ok(clientResource)
                    .header(HttpHeaders.CONTENT_TYPE,
                            contentTypeTemplate.replace(ResourceMediaType.RESOURCE_TYPE_PLACEHOLDER,
                                    toClientConverter.getClientResourceType()))
                    .build();
        }

        return response;
    }

    public Response deleteResource(String uri) throws RemoteException {
        singleRepositoryService.deleteResource(uri);
        return Response.noContent().build();
    }

    public ClientResource createResourceViaForm(FormDataMultiPart multiPart, String parentUri, Boolean createFolders) throws RemoteException {
        final FormDataBodyPart resourcePart = multiPart.getField(ResourceMultipartConstants.RESOURCE_PART_NAME);
        if (resourcePart != null) {
            final String clientResourceType;
            Matcher matcher = Pattern.compile(ResourceMediaType.RESOURCE_MEDIA_TYPE_PREFIX + "([^+]+)").matcher(resourcePart.getMediaType().toString());
            if (matcher.find()) {
                clientResourceType = matcher.group(1);
            } else {
                throw new IllegalParameterValueException("resource Media-Type", resourcePart.getMediaType().toString());
            }
            final Class<? extends ClientResource> clientTypeClass = resourceConverterProvider.getClientTypeClass(clientResourceType);
            ClientResource clientObject = resourcePart.getEntityAs(clientTypeClass);
            Map<String, InputStream> partsMap = new HashMap<String, InputStream>();
            for (String currentPartName : multiPart.getFields().keySet()) {
                if (ResourceMultipartConstants.RESOURCE_PART_NAME.equals(currentPartName)) {
                    continue;
                }
                partsMap.put(currentPartName, multiPart.getField(currentPartName).getEntityAs(InputStream.class));
            }

            return createResource(clientObject, parentUri, createFolders, partsMap);
        } else {
            ClientResource result = createFileViaForm(
                    multiPart.getField("data") != null ? multiPart.getField("data").getEntityAs(InputStream.class) : null, parentUri,
                    multiPart.getField("label") != null ? multiPart.getField("label").getValue() : null,
                    multiPart.getField("description") != null ? multiPart.getField("description").getValue() : null,
                    multiPart.getField("type") != null ? multiPart.getField("type").getValue() : null, createFolders);
            return result;
        }

    }

    protected ClientResource createFileViaForm(InputStream stream, String uri, String label, String description,
            String type, Boolean createFolders) throws RemoteException {

        if (stream == null) {
            throw new MandatoryParameterNotFoundException("data");
        }
        if (uri == null || "".equals(uri)) {
            throw new MandatoryParameterNotFoundException(PATH_PARAM_URI);
        }
        if (label == null || "".equals(label)) {
            throw new MandatoryParameterNotFoundException("label");
        }
        if (type == null || "".equals(type)) {
            throw new MandatoryParameterNotFoundException("type");
        }

        Resource createdFile = singleRepositoryService.createFileResource(stream, uri, label, label, description, type, createFolders);
        return resourceConverterProvider.getToClientConverter(createdFile).toClient(createdFile, null);
    }

    public Response defaultPostHandler(InputStream stream, String uri, String sourceUri, String disposition, String description, String rawMimeType, String accept, Boolean createFolders, Boolean overwrite) throws RemoteException, IOException {
        Response response;
        if (sourceUri != null) {
            singleRepositoryService.copyResource(sourceUri, uri, createFolders, overwrite);
            // if user copies file, we have to return descriptor, not its content
            // See ResourceDetailsJaxrsService#getResourceDetails
            if (MediaType.APPLICATION_JSON.equals(accept)) {
                accept = ResourceMediaType.FILE_JSON;
            } else {
                accept = ResourceMediaType.FILE_XML;
            }
            String newUri = (uri.endsWith(Folder.SEPARATOR)? uri : uri + Folder.SEPARATOR) + sourceUri.substring(sourceUri.lastIndexOf(Folder.SEPARATOR) + 1);
            response = getResourceDetails(newUri, accept, false);
        } else {
            if (disposition == null || !disposition.contains("filename=") || disposition.endsWith("filename=")) {
                throw new IllegalParameterValueException("Content-Disposition", disposition);
            }

            String name = disposition.split("filename=")[1];
            String type = extractType(rawMimeType, name);

            Resource createdFile = singleRepositoryService.createFileResource(stream, uri, name, name, description, type, createFolders);
            ClientResource clientCreatedFile = resourceConverterProvider.getToClientConverter(createdFile).toClient(createdFile, null);
            response = Response.status(Response.Status.CREATED).entity(clientCreatedFile).build();
        }

        return response;
    }

    public Response updateFileViaForm(InputStream stream, String uri, String label, String description, String type) throws RemoteException {

        if (stream == null) {
            throw new MandatoryParameterNotFoundException("data");
        }
        // uri - uri of resource
        if (uri == null || "".equals(uri)) {
            throw new MandatoryParameterNotFoundException(PATH_PARAM_URI);
        } else if (uri.endsWith(Folder.SEPARATOR)) {
            throw new IllegalParameterValueException(PATH_PARAM_URI, uri);
        }
        if (type == null || "".equals(type)) {
            throw new MandatoryParameterNotFoundException("type");
        }

        uri = Folder.SEPARATOR + uri;
        int lastSeparator = uri.lastIndexOf(Folder.SEPARATOR);
        String name = uri.substring(lastSeparator + Folder.SEPARATOR_LENGTH);
        String parentFolderUri = uri.substring(0, lastSeparator);
        parentFolderUri = parentFolderUri.equals("") ? Folder.SEPARATOR : parentFolderUri;

        Resource file;
        Response.Status status = Response.Status.OK;
        if (singleRepositoryService.getResource(uri) == null) {
            file = singleRepositoryService.createFileResource(stream, parentFolderUri, name, label == null ? name : label, description, type, true);
            status = Response.Status.CREATED;
        } else {
            file = singleRepositoryService.updateFileResource(stream, parentFolderUri, name, label == null ? name : label, description, type);
        }
        ClientResource clientFile = resourceConverterProvider.getToClientConverter(file).toClient(file, null);
        return Response.status(status).entity(clientFile).build();
    }

    public Response defaultPutHandler(InputStream stream, String uri, String sourceUri, String disposition, String description, String rawMimeType, String accept, Boolean createFolders, Boolean overwrite) throws RemoteException {
        Response response;
        if (sourceUri != null) {
            // uri - parent folder uri
            singleRepositoryService.moveResource(sourceUri, uri, createFolders, overwrite);
            // if user copies file, we have to return descriptor, not its content
            // (matters only '+json' or '+xml' part, so file here works for all kinds of resources))
            if (MediaType.APPLICATION_JSON.equals(accept)) {
                accept = ResourceMediaType.FILE_JSON;
            } else {
                accept = ResourceMediaType.FILE_XML;
            }
            String newUri = (uri.endsWith(Folder.SEPARATOR) ? uri : uri + Folder.SEPARATOR) + sourceUri.substring(sourceUri.lastIndexOf(Folder.SEPARATOR) + 1);
            response = getResourceDetails(newUri, accept, false);
        } else {
            // uri - uri of resource
            if (uri == null || uri.endsWith(Folder.SEPARATOR)) {
                throw new IllegalParameterValueException(PATH_PARAM_URI, uri);
            }

            int lastSeparator = uri.lastIndexOf(Folder.SEPARATOR);
            String name = uri.substring(lastSeparator + Folder.SEPARATOR_LENGTH);
            String parentFolderUri = uri.substring(0, lastSeparator);
            parentFolderUri = parentFolderUri.equals("") ? Folder.SEPARATOR : parentFolderUri;
            String type = extractType(rawMimeType, name);

            Resource file;
            Response.Status status = Response.Status.OK;
            String label = name;
            if (disposition != null) {
                if (!disposition.contains("filename=") || disposition.endsWith("filename=")) {
                    throw new IllegalParameterValueException("Content-Disposition", disposition);
                } else {
                    label = disposition.split("filename=")[1];
                }
            }

            if (singleRepositoryService.getResource(uri) == null) {
                file = singleRepositoryService.createFileResource(stream, parentFolderUri, name, label, description, type, true);
                status = Response.Status.CREATED;
            } else {
                file = singleRepositoryService.updateFileResource(stream, parentFolderUri, name, label, description, type);
            }
            ClientResource clientFile = resourceConverterProvider.getToClientConverter(file).toClient(file, null);
            response = Response.status(status).entity(clientFile).build();
        }

        return response;
    }

    public Response patchResource(PatchDescriptor descriptor, String uri) throws RemoteException {
        Resource resource = singleRepositoryService.getResource(uri);
        if (resource == null) {
            throw new ResourceNotFoundException(uri);
        }

        if (resource.getVersion() != descriptor.getVersion()) {
            throw new VersionNotMatchException();
        }

        ClientResource clientResource = resourceConverterProvider.getToClientConverter(resource).toClient(resource, null);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext(clientResource);
        context.setTypeConverter(new ReferenceableTypeConverter());

        for (PatchItem item : descriptor.getItems()) {
            try {
                parser.parseExpression(item.toString()).getValue(context);
            } catch (ExpressionException spel) {
                throw new PatchException(spel.getLocalizedMessage(), item);
            }
        }

        clientResource.setUri(uri);
        resource = ((ToServerConverter<ClientUriHolder, Resource>) resourceConverterProvider.getToServerConverter(clientResource))
                .toServer(clientResource, resource, null);
        singleRepositoryService.updateResource(resource);

        return Response.ok(clientResource).build();
    }

    public ClientResource createResource(ClientResource resourceLookup, String parentUri, boolean createFolders) throws RemoteException {
        return createResource(resourceLookup, parentUri, createFolders, null);
    }

    public ClientResource createResource(ClientResource resourceLookup, String parentUri, boolean createFolders, Map<String, InputStream> attachments) throws RemoteException {
        String uniqueName = singleRepositoryService.getUniqueName(parentUri, resourceLookup.getLabel());
        String ownersUri = (Folder.SEPARATOR.equals(parentUri) ? parentUri : parentUri + Folder.SEPARATOR) + uniqueName;
        resourceLookup.setUri(ownersUri);
        ToServerConversionOptions serverConversionOptions = ToServerConversionOptions.getDefault().setOwnersUri(ownersUri).setResetVersion(true).setAttachments(attachments);
        Resource serverResource = resourceConverterProvider.getToServerConverter(resourceLookup).toServer(resourceLookup, serverConversionOptions);
        serverResource = singleRepositoryService.createResource(serverResource, parentUri, createFolders);
        return resourceConverterProvider.getToClientConverter(serverResource).toClient(serverResource, null);
    }

    public ClientResource updateResource(ClientResource resourceLookup, String uri) throws IllegalParameterValueException, MandatoryParameterNotFoundException, ResourceNotFoundException, ModificationNotAllowedException, VersionNotMatchException {
        Resource resource = singleRepositoryService.getResource(uri);
        if (resource == null) {
            // creation is forbidden
            throw new IllegalParameterValueException(PATH_PARAM_URI, uri);
        } else {
            resourceLookup.setVersion(resource.getVersion());
            ToServerConverter<ClientResource, Resource> converter = (ToServerConverter<ClientResource, Resource>) resourceConverterProvider.getToServerConverter(resourceLookup);
            resource = converter.toServer(resourceLookup, resource, ToServerConversionOptions.getDefault().setAllowReferencesOnly(true).setOwnersUri(uri));
        }
        resource.setURIString(uri);

        final Resource serverResource = singleRepositoryService.updateResource(resource);
        return resourceConverterProvider.getToClientConverter(serverResource).toClient(serverResource, ToClientConversionOptions.getDefault().setExpanded(false));
    }

    protected Response toResponse(FileResourceData data, String name, String fileType) {
        if(!data.hasData()){
            return Response.noContent().build();
        }
        Response.ResponseBuilder builder = Response.ok(data.getDataStream());
        builder.header("Pragma", "").header("Cache-Control", "no-store");

        if (ContentResource.TYPE_XLS.equals(fileType) || ContentResource.TYPE_XLSX.equals(fileType) || ContentResource.TYPE_DOCX.equals(fileType) || ContentResource.TYPE_PPTX.equals(fileType)) {
            builder.header("Content-Disposition", "inline");
        }

        String contentType = contentTypeMapping.get(fileType);
        if (contentType == null) {
            if (name.contains(".") && !name.endsWith(".")) {
                contentType = contentTypeMapping.get(name.substring(name.indexOf(".") + 1));
            }
        }

        if (contentType == null && ContentResource.TYPE_IMAGE.equals(fileType)) {
            try {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(data.getDataStream()));
                String format = null;
                while (readers.hasNext()) {
                    format = readers.next().getFormatName();
                }
                contentType = "image/" + (format == null ? "*" : format.toLowerCase());
            } catch (Exception e) {
                // Some unknown file, which pretend to be an image. Ignore it.
            }
        }

        return builder.type(contentType == null ? MediaType.APPLICATION_OCTET_STREAM : contentType).build();
    }

    protected String extractType(String mimeType, String name) {
        String type = null;
        if (mimeType.contains(";")) {
            mimeType = mimeType.split(";")[0].trim();
        }

        if (mimeType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
            type = ContentResource.TYPE_UNSPECIFIED;
        } else {
            String wildcardMimeType = mimeType.replaceFirst("/.*", "/*");
            for (ClientFile.FileType eType : ClientFile.FileType.values()) {
                if (eType.getMimeType().equalsIgnoreCase(mimeType) || eType.getMimeType()
                        .equalsIgnoreCase(wildcardMimeType)) {
                    type = eType.toString();
                }
            }

            // let's try to analyse extension
            if (type == null && name.contains(".") && !name.endsWith(".")) {
                String extension = name.substring(name.lastIndexOf(".") + 1);
                if (contentTypeMapping.containsKey(extension)) {
                    type = extension;
                }
            }
        }

        return type == null ? ContentResource.TYPE_UNSPECIFIED : type;
    }
}
