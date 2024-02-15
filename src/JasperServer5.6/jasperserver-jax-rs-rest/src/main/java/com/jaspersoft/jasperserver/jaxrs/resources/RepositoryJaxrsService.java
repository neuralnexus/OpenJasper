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

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.dto.common.PatchDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceListWrapper;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.jaxrs.common.PATCH;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.services.BatchRepositoryService;
import com.jaspersoft.jasperserver.remote.services.SingleRepositoryService;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.service.RepositorySearchResult;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: RepositoryJaxrsService.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
@Service
@Path("/resources")
@Transactional(rollbackFor = Exception.class)
public class RepositoryJaxrsService {

    @Resource
    protected BatchRepositoryService batchRepositoryService;
    @Resource
    protected ResourceDetailsJaxrsService resourceDetailsJaxrsService;
    @Resource
    private SingleRepositoryService singleRepositoryService;
    @Resource
    private ResourceConverterProvider resourceConverterProvider;
    @Context
    private Providers providers;
    @Context
    private HttpHeaders httpHeaders;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, ResourceMediaType.FOLDER_XML, ResourceMediaType.FOLDER_JSON})
    public Response getResources(
            @QueryParam(RestConstants.QUERY_PARAM_SEARCH_QUERY) String q,
            @QueryParam("folderUri") String folderUri,
            @QueryParam("type") List<String> type,
            @QueryParam("accessType") String accessTypeString,
            @QueryParam(RestConstants.QUERY_PARAM_OFFSET) Integer start,
            @QueryParam(RestConstants.QUERY_PARAM_LIMIT) Integer limit,
            @QueryParam("recursive") @DefaultValue("true") Boolean recursive,
            @QueryParam("showHiddenItems") @DefaultValue("false") Boolean showHiddenItems,
            @QueryParam("forceTotalCount") @DefaultValue("false") Boolean forceTotalCount,
            @QueryParam(RestConstants.QUERY_PARAM_SORT_BY) String sortBy,
            @QueryParam(RestConstants.QUERY_PARAM_EXPANDED) Boolean expanded,
            @QueryParam("forceFullPage") @DefaultValue("false") Boolean forceFullPage,
            @HeaderParam(HttpHeaders.ACCEPT)String accept) throws RemoteException {

        Response res;
        if (ResourceMediaType.FOLDER_JSON.equals(accept) || ResourceMediaType.FOLDER_XML.equals(accept)) {
            res = getResourceDetails("", accept, expanded);
        } else {
            AccessType accessType =  AccessType.ALL;
            if (accessTypeString != null && !"".equals(accessTypeString)){
                if (AccessType.MODIFIED.name().equalsIgnoreCase(accessTypeString)){
                    accessType = AccessType.MODIFIED;
                }
                if (AccessType.VIEWED.name().equalsIgnoreCase(accessTypeString)){
                    accessType = AccessType.VIEWED;
                }
            }

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            RepositorySearchResult<ClientResourceLookup> result =
                    batchRepositoryService.getResources(q, folderUri, type,
                            start, limit,
                            recursive, showHiddenItems,
                            sortBy, accessType, user,
                            forceFullPage);

            final int iStart = result.getClientOffset();
            final int iLimit = result.getClientLimit();
            Response.ResponseBuilder response = new ResponseBuilderImpl();

            int realResultSize = result.size();
            boolean isForceTotalCount = (forceTotalCount != null && forceTotalCount);

            if (realResultSize != 0) {
                response.status(Response.Status.OK).entity(new ClientResourceListWrapper(result.getItems()));

                response.header(RestConstants.HEADER_START_INDEX, iStart)
                        .header(RestConstants.HEADER_RESULT_COUNT, realResultSize);

                int totalCount = realResultSize;
                if (isForceTotalCount || iStart == 0 || forceFullPage) {
                    totalCount = forceFullPage
                            ? (((realResultSize < iLimit) && !isForceTotalCount) ? realResultSize : result.getTotalCount())
                            : batchRepositoryService.getResourcesCount(q, folderUri, type, recursive, showHiddenItems, accessType, user);
                }

                if (iStart == 0 || isForceTotalCount || iLimit == 0 || forceFullPage) {
                    response.header(RestConstants.HEADER_TOTAL_COUNT, totalCount);
                }

                if (forceFullPage && (result.getNextOffset() < result.getTotalCount())) {
                    response.header(RestConstants.HEADER_NEXT_OFFSET, result.getNextOffset());
                }
            } else {
                response.status(Response.Status.NO_CONTENT);
                response.header(RestConstants.HEADER_TOTAL_COUNT, 0);
            }

            res = response.build();
        }
        return res;
    }

    @DELETE
    public Response deleteResources(@QueryParam("resourceUri") List<String> uris) throws RemoteException {
        if (uris == null || uris.isEmpty()) {
            //no URI is specified as a parameter. It means delete request on root resource.
            // Call single delete logic. And let it forbid such a request.
            return deleteResource("");
        }
        batchRepositoryService.deleteResources(uris);
        return Response.noContent().build();
    }

    @GET
    @Path("/{uri: .+}")
    public Response getResourceDetails(@PathParam(ResourceDetailsJaxrsService.PATH_PARAM_URI)String uri, 
            @HeaderParam(HttpHeaders.ACCEPT)String accept,
            @QueryParam(RestConstants.QUERY_PARAM_EXPANDED) Boolean expanded) throws RemoteException {
       return resourceDetailsJaxrsService.getResourceDetails(Folder.SEPARATOR + uri.replaceAll("/$", ""),accept,expanded);
    }

    @DELETE
    @Path("/{uri: .+}")
    public Response deleteResource(@PathParam(ResourceDetailsJaxrsService.PATH_PARAM_URI) String uri) throws RemoteException {
        return resourceDetailsJaxrsService.deleteResource(Folder.SEPARATOR + uri.replaceAll("/$", ""));
    }

    @POST
    @Path("/{uri: .+}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response defaultPostHandler(InputStream stream,
            @PathParam(ResourceDetailsJaxrsService.PATH_PARAM_URI) String _uri,
            @HeaderParam(HttpHeaders.CONTENT_LOCATION) String sourceUri,
            @HeaderParam("Content-Disposition")String disposition,
            @HeaderParam("Content-Description")String description,
            @HeaderParam(HttpHeaders.CONTENT_TYPE)MediaType mediaType,
            @HeaderParam(HttpHeaders.ACCEPT)String accept,
            @QueryParam(RestConstants.QUERY_PARAM_CREATE_FOLDERS)@DefaultValue("true")Boolean createFolders,
            @QueryParam("overwrite")@DefaultValue("false")Boolean overwrite) throws RemoteException, IOException {
        String uri = Folder.SEPARATOR + _uri.replaceAll("/$", "");
        ClientResource resourceLookup = null;
        Response response = null;
        try{
            resourceLookup = parseEntity(stream, mediaType);
        }catch (IllegalParameterValueException e){
            // wrong media type for resource creation request, let's try default post handler
            response = resourceDetailsJaxrsService.defaultPostHandler(stream, uri, sourceUri,
                    disposition, description, mediaType != null ? mediaType.toString() : null, accept, createFolders, overwrite);
        }
        if(response == null){
            final ClientResource createdResource = resourceDetailsJaxrsService
                    .createResource(resourceLookup, uri, createFolders);
            response = Response.status(Response.Status.CREATED).entity(createdResource).build();
        }
        return response;
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response defaultPostHandlerForRoot(InputStream stream,
            @HeaderParam(HttpHeaders.CONTENT_LOCATION) String sourceUri,
            @HeaderParam("Content-Disposition")String disposition,
            @HeaderParam("Content-Description")String description,
            @HeaderParam(HttpHeaders.CONTENT_TYPE)MediaType rawMimeType,
            @HeaderParam(HttpHeaders.ACCEPT)String accept,
            @QueryParam(RestConstants.QUERY_PARAM_CREATE_FOLDERS)@DefaultValue("true")Boolean createFolders,
            @QueryParam("overwrite")@DefaultValue("false")Boolean overwrite) throws RemoteException, IOException{
        return defaultPostHandler(stream, "", sourceUri,disposition, description, rawMimeType, accept, createFolders, overwrite);
    }

    /**
     *
     * @param entityStream - entity input stream
     * @param mediaType - the entity media type
     * @return client entity
     * @throws IllegalParameterValueException if media type string doesn't correspond to repository resource media type pattern.
     */
    // As far as clientTypeClass represents <? extents ClientResource> cast to ClientResource is safe.
    @SuppressWarnings("unchecked")
    protected ClientResource parseEntity(InputStream entityStream, MediaType mediaType) throws IllegalParameterValueException {
        final Class<? extends ClientResource> clientTypeClass = resourceConverterProvider.getClientTypeClass(extractClientResourceType(mediaType));
        // code below comes from com.sun.jersey.multipart.BodyPart#getEntityAs(Class<T> clazz)
        Annotation annotations[] = new Annotation[0];
        MessageBodyReader reader =
                providers.getMessageBodyReader(clientTypeClass, clientTypeClass, annotations, mediaType);
        if (reader == null) {
            throw new IllegalArgumentException("No available MessageBodyReader for class " + clientTypeClass.getName() + " and media type " + mediaType);
        }
        try {
            return (ClientResource) reader.readFrom(clientTypeClass, clientTypeClass, annotations, mediaType, httpHeaders.getRequestHeaders(),
                    entityStream);
        } catch (IOException e) {
            throw new JSExceptionWrapper(e);
        }
    }

    /**
     * Extracts client resource type prom media type string.
     * @param mediaType - the entity media type
     * @return entity client type
     * @throws IllegalParameterValueException if media type string doesn't correspond to repository resource media type pattern.
     */
    protected String extractClientResourceType(MediaType mediaType) throws IllegalParameterValueException {
        final String clientResourceType;
        Matcher matcher = Pattern.compile(ResourceMediaType.RESOURCE_MEDIA_TYPE_PREFIX + "([^+]+)").matcher(mediaType != null ? mediaType.toString() : "");
        if(matcher.find()){
            clientResourceType = matcher.group(1);
        } else {
            throw new IllegalParameterValueException("resource Media-Type", mediaType != null ? mediaType.toString() : "null");
        }
        return clientResourceType;
    }

    @POST    
    @Path("/{uri: .+}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createFileViaForm(
            FormDataMultiPart multiPart,
            @PathParam(ResourceDetailsJaxrsService.PATH_PARAM_URI) String uri,
            @QueryParam(RestConstants.QUERY_PARAM_CREATE_FOLDERS)@DefaultValue("true")Boolean createFolders) throws RemoteException{
        final ClientResource result = resourceDetailsJaxrsService.createResourceViaForm(multiPart, Folder.SEPARATOR + uri.replaceAll("/$", ""), createFolders);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createFileViaFormInRoot(
            FormDataMultiPart multiPart,
            @QueryParam(RestConstants.QUERY_PARAM_CREATE_FOLDERS)@DefaultValue("true")Boolean createFolders) throws RemoteException{
       return createFileViaForm(multiPart, "", createFolders);
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response defaultPutHandlerForRoot(InputStream stream,
            @HeaderParam(HttpHeaders.CONTENT_LOCATION) String sourceUri,
            @HeaderParam("Content-Disposition")String disposition,
            @HeaderParam("Content-Description")String description,
            @HeaderParam(HttpHeaders.CONTENT_TYPE)MediaType mediaType,
            @HeaderParam(HttpHeaders.ACCEPT)String accept,
            @QueryParam(RestConstants.QUERY_PARAM_CREATE_FOLDERS)@DefaultValue("true")Boolean createFolders,
            @QueryParam("overwrite")@DefaultValue("false")Boolean overwrite) throws RemoteException, IOException {
        return defaultPutHandler(stream, "", sourceUri, disposition, description, mediaType, accept, createFolders, overwrite);
    }

    @PUT
    @Path("/{uri: .+}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response defaultPutHandler(InputStream stream,
            @PathParam(ResourceDetailsJaxrsService.PATH_PARAM_URI) String _uri,
            @HeaderParam(HttpHeaders.CONTENT_LOCATION) String sourceUri,
            @HeaderParam("Content-Disposition")String disposition,
            @HeaderParam("Content-Description")String description,
            @HeaderParam(HttpHeaders.CONTENT_TYPE)MediaType mediaType,
            @HeaderParam(HttpHeaders.ACCEPT)String accept,
            @QueryParam(RestConstants.QUERY_PARAM_CREATE_FOLDERS)@DefaultValue("true")Boolean createFolders,
            @QueryParam("overwrite")@DefaultValue("false")Boolean overwrite) throws RemoteException, IOException {
        String uri = Folder.SEPARATOR + _uri.replaceAll("/$", "");
        ClientResource resourceLookup = null;
        Response response = null;
        try{
            resourceLookup = parseEntity(stream, mediaType);
        }catch (IllegalParameterValueException e){
            // wrong media type for resource creation request, let's try default put handler
            response = resourceDetailsJaxrsService.defaultPutHandler(stream, uri, sourceUri,
                    disposition, description, mediaType != null ? mediaType.toString() : null, accept, createFolders, overwrite);
        }
        if(response == null){
            if(resourceLookup == null){
                throw new MandatoryParameterNotFoundException("resource body");
            }
            resourceLookup.setUri(uri);
            final ClientResource updatedResource =  singleRepositoryService.saveOrUpdate(resourceLookup, overwrite,
                    createFolders);
            int createdVersion = com.jaspersoft.jasperserver.api.metadata.common.domain.Resource.VERSION_NEW +1;
            // if current version is '0' (new version for the resource to be created is '-1') and previous version isn't '0',
            // then send 201 (Created), otherwise - 200 (OK)
            response = updatedResource.getVersion() == createdVersion
                    && (resourceLookup.getVersion() == null || resourceLookup.getVersion().intValue() != createdVersion)
                    ? Response.status(Response.Status.CREATED).entity(updatedResource).build()
                    : Response.ok(updatedResource).build();
        }
        return response;
    }

    @PUT
    @Path("/{uri: .+}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateFileViaForm(
            @FormDataParam("data")InputStream stream,
            @PathParam(ResourceDetailsJaxrsService.PATH_PARAM_URI) String uri,
            @FormDataParam("label")String label,
            @FormDataParam("description")String description,
            @FormDataParam("type")String type) throws RemoteException{
          return resourceDetailsJaxrsService.updateFileViaForm(stream, uri, label, description, type);
    }

    @PATCH
    @Path("/{uri: .+}")
    public Response patchResource(PatchDescriptor descriptor,
            @PathParam(ResourceDetailsJaxrsService.PATH_PARAM_URI) String uri) throws RemoteException{
       return resourceDetailsJaxrsService.patchResource(descriptor, Folder.SEPARATOR + uri.replaceAll("/$", ""));
    }
}
