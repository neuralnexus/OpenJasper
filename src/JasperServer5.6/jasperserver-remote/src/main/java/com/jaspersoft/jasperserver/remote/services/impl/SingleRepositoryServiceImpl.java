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

package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ContentResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceVersionNotMatchException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.FolderAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.FolderNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.NotAFileException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.ResourceInUseException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.VersionNotMatchException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.resources.converters.ToClientConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import com.jaspersoft.jasperserver.remote.services.SingleRepositoryService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.hibernate.JDBCException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.SpringSecurityException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component("singleRepositoryService")
@Transactional(rollbackFor = Exception.class)
public class SingleRepositoryServiceImpl implements SingleRepositoryService {

    private Pattern nameWithNumber = Pattern.compile("^.*_\\d+$", Pattern.CASE_INSENSITIVE);

    @javax.annotation.Resource
    private ConfigurationBean configurationBean;

    @javax.annotation.Resource
    private UriHardModifyProtectionChecker uriHardModifyProtectionChecker;

    @javax.annotation.Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;

    @javax.annotation.Resource
    protected Set<String> fileResourceTypes;
    @javax.annotation.Resource
    private ResourceConverterProvider resourceConverterProvider;
    @javax.annotation.Resource
    private SearchCriteriaFactory searchCriteriaFactory;

    @Override
    public Resource getResource(String uri) {
        Resource resource = repositoryService.getResource(null, uri);
        if (resource == null) {
            resource = repositoryService.getFolder(null, uri);
        }
        return resource;
    }

    @Override
    public FileResourceData getFileResourceData(String uri) {
        Resource resource = repositoryService.getResource(null, uri);
        if (resource == null) {
            throw new IllegalStateException("Resource " + uri + " not found");
        }
        return getFileResourceData(resource);
    }

    @Override
    public FileResourceData getFileResourceData(Resource resource) {
        if (resource instanceof FileResource) {
            return repositoryService.getResourceData(null, resource.getURIString());
        }
        if (resource instanceof ContentResource) {
            return repositoryService.getContentResourceData(null, resource.getURIString());
        }
        throw new IllegalStateException(resource.getURIString() + " is not a file");
    }

    @Override
    public void deleteResource(String uri) throws IllegalParameterValueException, AccessDeniedException {
        if (uri == null || "".equals(uri)) {
            throw new IllegalParameterValueException("uri", uri);
        }
        if(uriHardModifyProtectionChecker.isHardModifyProtected(uri)){
            throw new AccessDeniedException("", uri);
        }

        try {
            Resource resource = repositoryService.getResource(null, uri);
            if (resource != null) {
                List<ResourceLookup> dependentResources = repositoryService.getDependentResources(null, uri, searchCriteriaFactory, 0, 0);
                if (dependentResources == null || dependentResources.isEmpty()){
                    repositoryService.deleteResource(null, uri);
                } else {
                    throw new ResourceInUseException(dependentResources);
                }
            } else {
                resource = repositoryService.getFolder(null, uri);
                if (resource != null) {
                    repositoryService.deleteFolder(null, uri);
                }
            }
        } catch (SpringSecurityException sse) {
            throw new AccessDeniedException(uri);
        } catch (JSExceptionWrapper w){
            throw getRootException(w);
        }
    }

    @Override
    public Resource createResource(Resource serverResource, String parentUri, boolean createFolders) throws RemoteException {
        if (createFolders) {
            ensureFolderUri(parentUri);
        } else if (!repositoryService.folderExists(null, parentUri)) {
            throw new FolderNotFoundException(parentUri);
        }

        serverResource.setParentFolder(parentUri);
        if (serverResource.getName() == null || "".equals(serverResource.getName())) {
            serverResource.setName(generateName(parentUri, serverResource.getLabel()));
        }
        try {
            if (serverResource instanceof Folder) {
                repositoryService.saveFolder(null, (Folder) serverResource);
            } else {
                repositoryService.saveResource(null, serverResource);
            }
        } catch (JSExceptionWrapper w) {
            throw getRootException(w);
        }

        return getResource(serverResource.getURIString());
    }

    public ClientResource saveOrUpdate(ClientResource clientResource, boolean overwrite, boolean createFolders) throws RemoteException {
        final String uri = clientResource.getUri();
        Resource resource = getResource(uri);

        // asking not toServerConverter, but toClientConverter for types compatibility to avoid NullPointerException,
        // because BinaryDataResourceConverter returns null as serverResourceType
        if (resource != null) {
            // is it different type of resource?
            if (!resourceConverterProvider.getToClientConverter(resource).getClientResourceType()
                    .equals(ClientTypeHelper.extractClientType(clientResource.getClass()))) {
                if (overwrite) {
                    deleteResource(uri);
                    resource = null;
                } else {
                    throw new ResourceAlreadyExistsException(uri);
                }
            } else {
                if (!new Integer(resource.getVersion()).equals(clientResource.getVersion())){
                    throw new VersionNotMatchException();
                }
            }
        }
        resource = ((ToServerConverter<ClientResource, Resource>)resourceConverterProvider.getToServerConverter(clientResource))
                .toServer(clientResource, resource, ToServerConversionOptions.getDefault().setOwnersUri(uri));
        if(resource.isNew()){
            resource = createResource(resource, resource.getParentPath(), createFolders);
        } else {
            resource = updateResource(resource);
        }
        return resourceConverterProvider.getToClientConverter(resource).toClient(resource, ToClientConversionOptions.getDefault());
    }

    @Override
    public Resource updateResource(Resource resource) throws ResourceNotFoundException, VersionNotMatchException {
        try{
            if (resource instanceof Folder) {
                repositoryService.saveFolder(null, (Folder) resource);
            } else {
                repositoryService.saveResource(null, resource);
            }
            return getResource(resource.getURIString());
        } catch (JSResourceVersionNotMatchException e){
            throw new VersionNotMatchException();
        } catch (JSExceptionWrapper w) {
                throw getRootException(w);
        }
    }

    @Override
    public void copyResource(String sourceUri, String destinationUri, boolean createFolders, boolean overwrite) throws ResourceNotFoundException, AccessDeniedException, ResourceAlreadyExistsException, IllegalParameterValueException {
        Resource resource = prepareToOperation(sourceUri, destinationUri, createFolders, overwrite);
        // underlying service requires full uri, not just parent uri
        // after setting new parent uri we obtain the new uri of resource
        resource.setParentFolder(destinationUri);
        if (resource instanceof Folder) {
            if (destinationUri.startsWith(sourceUri + Folder.SEPARATOR)){
                throw new IllegalParameterValueException("sourceUri", sourceUri);
            }
            repositoryService.copyFolder(null, sourceUri, resource.getURIString());
        } else {
            repositoryService.copyResource(null, sourceUri, resource.getURIString());
        }
    }

    @Override
    public void moveResource(String sourceUri, String destinationUri, boolean createFolders, boolean overwrite) throws ResourceNotFoundException, AccessDeniedException, ResourceAlreadyExistsException, IllegalParameterValueException {
        Resource resource = prepareToOperation(sourceUri, destinationUri, createFolders, overwrite);
        if (resource instanceof Folder) {
            if (destinationUri.startsWith(sourceUri + Folder.SEPARATOR)){
                throw new IllegalParameterValueException("sourceUri", sourceUri);
            }
            repositoryService.moveFolder(null, sourceUri, destinationUri);
        } else {
            repositoryService.moveResource(null, sourceUri, destinationUri);
        }
    }

    @Override
    public Resource createFileResource(InputStream stream, String parentFolderUri, String name, String label, String description, String type, boolean createFolders) throws RemoteException {
        Resource file = fileResourceTypes.contains(type) ? new FileResourceImpl() : new ContentResourceImpl();
        file.setLabel(label);
        file.setName(name);
        file.setDescription(description);
        file.setCreationDate(new Date());

        if (fileResourceTypes.contains(type)) {
            ((FileResource) file).readData(stream);
            ((FileResource) file).setFileType(type);
        } else {
            ((ContentResource) file).readData(stream);
            ((ContentResource) file).setFileType(type);
        }

        return createResource(file, parentFolderUri, createFolders);
    }

    @Override
    public Resource updateFileResource(InputStream stream, String parentUri, String name, String label, String description, String type) throws RemoteException {
        String uri = parentUri.endsWith(Folder.SEPARATOR) ? parentUri + name : parentUri + Folder.SEPARATOR + name;
        Resource file = getResource(uri);

        if (file instanceof FileResource) {
            ((FileResource) file).readData(stream);
            ((FileResource) file).setFileType(type);
            ((FileResource) file).setReferenceURI(null);
        } else if (file instanceof ContentResource) {
            ((ContentResource) file).readData(stream);
            ((ContentResource) file).setFileType(type);
        } else {
            throw new NotAFileException(parentUri + Folder.SEPARATOR + name);
        }

        file.setLabel(label == null ? name : label);
        file.setDescription(description);

        return updateResource(file);
    }

    @Override
    public String getUniqueName(String parenUri, String name) throws MandatoryParameterNotFoundException{
        if (parenUri == null ){
            throw new MandatoryParameterNotFoundException("parentUri");
        }

        if (name == null ){
            throw new MandatoryParameterNotFoundException("label");
        }
        return generateName(parenUri, name);
    }

    private Resource prepareToOperation(String sourceUri, String destinationUri, boolean createFolders, boolean overwrite) throws ResourceNotFoundException, AccessDeniedException, ResourceAlreadyExistsException, IllegalParameterValueException {
        if (sourceUri == null || "".equals(sourceUri)) {
            throw new IllegalParameterValueException("sourceUri", sourceUri);
        }

        if(uriHardModifyProtectionChecker.isHardModifyProtected(sourceUri)){
            throw new AccessDeniedException("", sourceUri);
        }

        Resource resource = getResource(sourceUri), existing = null;
        if (resource == null) {
            throw new ResourceNotFoundException(sourceUri);
        }

        String newUri = (destinationUri.endsWith(Folder.SEPARATOR) ? destinationUri : destinationUri + Folder.SEPARATOR) + resource.getName();
        existing = getResource(newUri);

        if (existing != null) {
            if (overwrite) {
                deleteResource(newUri);
            } else {
                throw new ResourceAlreadyExistsException(newUri);
            }
        }

        if (createFolders) {
            ensureFolderUri(destinationUri);
        } else if (!repositoryService.folderExists(null, destinationUri)) {
            throw new FolderNotFoundException(destinationUri);
        }

        return resource;
    }

    private Folder ensureFolderUri(String uri) throws AccessDeniedException, ResourceAlreadyExistsException, IllegalParameterValueException {
        try {
            uri = "".equals(uri) ? Folder.SEPARATOR : uri;
            Folder folder = repositoryService.getFolder(null, uri);
            if (folder == null) {
                if (repositoryService.getResource(null, uri) != null) {
                    throw new ResourceAlreadyExistsException(uri);
                }

                int lastSeparator = uri.lastIndexOf(Folder.SEPARATOR);
                String label = uri.substring(lastSeparator + 1, uri.length());
                if (!label.equals(transformLabelToName(label))){
                    throw new IllegalParameterValueException("folder.name", label);
                }

                folder = new FolderImpl();
                folder.setParentFolder(ensureFolderUri(uri.substring(0, lastSeparator)));
                folder.setName(label);
                folder.setLabel(label);

                repositoryService.saveFolder(null, folder);
            } else {
                // /Public and /public should be different folders
                if (!folder.getURIString().equals(uri)){
                    throw new FolderAlreadyExistsException(uri, folder.getURIString());
                }
            }
            return folder;
        } catch (SpringSecurityException spe) {
            throw new AccessDeniedException("Access denied", uri);
        }
    }

    private String generateName(String parentUri, String label){
        String name = transformLabelToName(label);
        String uri = parentUri + Folder.SEPARATOR + name;
        Resource resource = repositoryService.getResource(null, uri);
        if (resource == null) {
            resource = repositoryService.getFolder(null, uri);
        }
        if (resource != null){
            if (nameWithNumber.matcher(name).matches()){
                int divider = label.lastIndexOf("_");
                Integer number = Integer.parseInt(name.substring(divider + 1)) + 1;
                name = name.substring(0, divider + 1) + number.toString();
            }
            else {
                name = name.concat("_1");
            }
            name = generateName(parentUri, name);
        }
        return name;
    }

    private String transformLabelToName(String label){
        return label.replaceAll(configurationBean.getResourceIdNotSupportedSymbols(), "_");
    }

    protected static RuntimeException getRootException(JSExceptionWrapper w) {
        Exception original = w.getOriginalException();
        if (original != null) {
            // Workaround:  Progress Oracle driver throws simple java.sql.Exception for data integrity violation
            if (original instanceof DataIntegrityViolationException) {
				return (DataIntegrityViolationException) original;
            } else if (original.getMessage() != null) {
                String originalMessage = original.getMessage().toLowerCase();
                if ((originalMessage.indexOf("integrity") >= 0) && (originalMessage.indexOf("violate") >= 0)) {
                    return new DataIntegrityViolationException(original.getMessage(), original);
                }
            }
            if (original instanceof RuntimeException) {
                if (original instanceof JDBCException) {
                    return new DataIntegrityViolationException(original.getMessage(), original);
                } else {
                    return (RuntimeException) original;
                }
            }
        }
        return w;
    }


}