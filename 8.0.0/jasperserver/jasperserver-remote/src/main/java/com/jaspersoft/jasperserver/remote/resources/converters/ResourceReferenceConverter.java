/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.RepositoryConfiguration;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceable;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientUriHolder;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.ReferencedResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.getRestrictedRuntimeExecutionContext;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ResourceReferenceConverter.java 28864 2013-02-21 11:27:18Z ykovalchyk $
 */
public class ResourceReferenceConverter<T extends ClientReferenceable> {
    protected final ResourceConverterProvider resourceConverterProvider;
    protected final RepositoryService repositoryService;
    protected final List<ClientReferenceRestriction> restrictions = new ArrayList<ClientReferenceRestriction>();
    protected RepositoryConfiguration configuration;
    protected PermissionsService permissionsService;

    public ResourceReferenceConverter(ResourceConverterProvider resourceConverterProvider,
            RepositoryService repositoryService, PermissionsService permissionsService, RepositoryConfiguration configuration, ClientReferenceRestriction... restriction) {
        this.repositoryService = repositoryService;
        this.permissionsService = permissionsService;
        this.resourceConverterProvider = resourceConverterProvider;
        if (restriction != null) {
            restrictions.addAll(Arrays.asList(restriction));
        }
        this.configuration = configuration;
    }

    public ResourceReferenceConverter<T> addReferenceRestriction(ClientReferenceRestriction restriction) {
        restrictions.add(restriction);
        return this;
    }

    /**
     * Converts ResoruceReferrence to client type object.
     *
     *
     * @param serverObject ResourceReference instance
     * @param options - to client conversion options
     * @return client referenced object
     * @throws ClassCastException if server side local resource of given ResourceReference is of wrong type.
     */
    @SuppressWarnings("unchecked")
    public T toClient(ResourceReference serverObject, ToClientConversionOptions options) throws ClassCastException {
        ClientUriHolder result = null;
        if (serverObject != null) {
            if (options != null && options.isExpansionEnabled(serverObject.isLocal())) {
                try {
                    Resource localResource;
                    if (serverObject.isLocal()) {
                        localResource = serverObject.getLocalResource();
                    } else {
                        localResource = repositoryService.getResource(getRestrictedRuntimeExecutionContext(),
                                serverObject.getReferenceURI());
                    }

                    String clientType = resourceConverterProvider.getToClientConverter(localResource).getClientResourceType();
                    // Some repository resources, like Domain can have multiple client representations(for Domain there are
                    // semanticLayerDataSource and domain client types). In expansion by type client can declare required
                    // representation type. So, the idea of this part of code try to find client converter, that matches
                    // pair (target resource server type) - (expand client type).
                    if (options.isExpansionByType(serverObject.isLocal())) {
                        clientType = options.getExpandTypes().stream().filter(((expandClientType) ->
                                isClientConverterExist(localResource.getResourceType(), expandClientType)))
                               .findFirst().orElse(clientType);
                    }

                    if (options.isExpanded(clientType, serverObject.isLocal())) {
                        result = resourceConverterProvider.
                                getToClientConverter(localResource.getResourceType(), clientType)
                                .toClient(localResource, options);
                    } else {
                        result = new ClientReference(serverObject.getTargetURI(), serverObject.getVersion());
                    }
                } catch (AccessDeniedException e) {
                    result = new ClientReference(serverObject.getTargetURI(), serverObject.getVersion());
                }
            } else {
                result = new ClientReference(serverObject.getTargetURI(), serverObject.getVersion());
            }
        }
        return (T) result;
    }

    private boolean isClientConverterExist(String serverType, String clientType) {
        return resourceConverterProvider.getToClientConverter(serverType, clientType) != null;
    }

    public ResourceReference toServer(ExecutionContext ctx, T clientObject, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        return toServer(ctx, clientObject, null, options);
    }

    public ResourceReference toServer(ExecutionContext ctx, T clientObject, ResourceReference resultToUpdate, ToServerConversionOptions options)
            throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ResourceReference resourceReference;
        if (clientObject == null) {
            resourceReference = null;
        } else if (clientObject.getClass() == ClientReference.class) {
            resourceReference = toServerReference(ctx, clientObject, resultToUpdate, options);
        } else if (clientObject instanceof ClientResource) {
            if (options == null || !options.isAllowReferencesOnly()) {
                if (restrictions != null){
                    for (ClientReferenceRestriction restriction : restrictions) {
                        restriction.validateReference((ClientResource)clientObject);
                    }
                }
                resourceReference = toServerLocalResource(ctx,  (ClientResource) clientObject, resultToUpdate, options);
            } else {
                throw new IllegalParameterValueException("reference", clientObject.toString());
            }
        } else {
            // shouldn't happen
            throw new IllegalParameterValueException("References of type "
                    + ClientTypeUtility.extractClientType(clientObject.getClass()) + " are not supported");
        }
        return resourceReference;
    }

    protected ResourceReference toServerReference(ExecutionContext ctx, T clientObject, ResourceReference resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        ResourceReference result;
        final String uriFromClient = clientObject.getUri();
        if (resultToUpdate != null && resultToUpdate.getTargetURI().equals(uriFromClient)) {
            // no update is needed. Existing ResourceReference points to the same URI
            result = resultToUpdate;
        } else {
            // if it is local resource, reference with local resource must be returned or update will fail
            // NOTE: it is always preferable to pass resultToUpdate to get actual resource from there to avoid rereading and possible errors.
            String ownersUri = options == null ? null : options.getOwnersUri();
            Resource referencedResource = validateAndGetReference(ctx, uriFromClient, ownersUri);
            if(options!=null && options.getAdditionalProperties()!=null && options.getAdditionalProperties().get("source")!=null)   {
                return new ResourceReference(referencedResource);
            }
            final boolean local = ownersUri != null && uriFromClient.startsWith(ownersUri + "_files" + Folder.SEPARATOR);
            result = resultToUpdate == null ? new ResourceReference(referencedResource) : resultToUpdate;
            if (!local) {
                result.setReference(uriFromClient);
            }
        }
        return result;
    }

    protected Resource validateAndGetReference(ExecutionContext ctx, String referenceUri, String ownersUri) throws IllegalParameterValueException {
        if (referenceUri == null) {
            throw new MandatoryParameterNotFoundException("resourceReference.uri");
        }

        if (!isAssignable(ctx, ownersUri, referenceUri)){
            throw new IllegalParameterValueException("resourceReference.uri", referenceUri);
        }
        // we need to update reference
        // but first check if URI is valid before fetching it
        if (referenceUri.isEmpty() || !referenceUri.startsWith(Folder.SEPARATOR)) {
            throw new ReferencedResourceNotFoundException(referenceUri, "uri");
        }

        final Resource resource = repositoryService.getResource(ctx, referenceUri);
        if (resource == null) {
            // resource with such URI doesn't exist
            throw new ReferencedResourceNotFoundException(referenceUri, "uri");
        } else if (!restrictions.isEmpty()) {
            final ClientResource clientTargetObject = resourceConverterProvider.getToClientConverter(resource).toClient(resource, null);
            for (ClientReferenceRestriction restriction : restrictions) {
                restriction.validateReference(clientTargetObject);
            }
        }
        return resource;
    }

    protected ResourceReference toServerLocalResource(ExecutionContext ctx,ClientResource clientObject, ResourceReference resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        ResourceReference result;
        Resource localResource = resourceConverterProvider.getToServerConverter(clientObject).toServer(ctx, clientObject, options);
        if (localResource.getName() == null && clientObject.getLabel() != null  && configuration.getResourceIdNotSupportedSymbols()!=null) {
            localResource.setName(clientObject.getLabel().replaceAll(configuration.getResourceIdNotSupportedSymbols(), "_"));
        }
        if (options != null && options.isResetVersion()){
            localResource.setVersion(Resource.VERSION_NEW);
        }
        if (resultToUpdate != null) {
            result = resultToUpdate;
            result.setLocalResource(localResource);
        } else {
            result = new ResourceReference(localResource);
        }
        return result;
    }

    protected final boolean isAssignable(ExecutionContext ctx, String parent, String child){
        boolean res = true;
        if (parent != null){
            int suffixIndex = child.lastIndexOf("_files");
            if (suffixIndex > 0){
                String possibleParentUri = child.substring(0, suffixIndex);
                if (!possibleParentUri.equals(parent)){
                    res = repositoryService.getResource(ctx, possibleParentUri) == null;
                }
            }
        }
        return res;
    }

    public static class ReferenceClassRestriction implements ClientReferenceRestriction {
        protected final Class<? extends ClientReferenceable> targetClientClass;

        public ReferenceClassRestriction(Class<? extends ClientReferenceable> targetClientClass) {
            this.targetClientClass = targetClientClass;
        }

        @Override
        public void validateReference(ClientResource clientResource) throws IllegalParameterValueException {
            if (!targetClientClass.isAssignableFrom(clientResource.getClass())) {
                throw new IllegalParameterValueException("Reference target is of wrong type",
                        "resourceReference.uri",
                        clientResource.getUri(),
                        ClientTypeUtility.extractClientType(clientResource.getClass()));
            }
        }
    }

    public static class FileTypeRestriction implements ClientReferenceRestriction{
        private final List<ClientFile.FileType> fileTypes;
        private final String fieldName;

        public FileTypeRestriction(ClientFile.FileType fileType){
            this(fileType, null);
        }

        public FileTypeRestriction(ClientFile.FileType fileType, String fieldName){
            this(fieldName, fileType);
        }

        public FileTypeRestriction(String fieldName, ClientFile.FileType ... fileTypes){
            this.fileTypes = Arrays.asList(fileTypes);
            this.fieldName = fieldName;
        }

        public List<ClientFile.FileType> getFileTypes() {
            return fileTypes;
        }

        @Override
        public void validateReference(ClientResource clientResource) throws IllegalParameterValueException {
            if (clientResource instanceof ClientFile) {
                final ClientFile.FileType type = ((ClientFile) clientResource).getType();
                if(type == null){
                    throw new MandatoryParameterNotFoundException("type");
                }
                if(!fileTypes.contains(type)){
                    final IllegalParameterValueException illegalParameterValueException =
                            new IllegalParameterValueException("Referenced " + (fieldName != null ? fieldName : "file") +
                                    " is of wrong type. File type is expected to be " + fileTypes + " but is ["
                                    + type + "]",
                            fieldName != null ? fieldName : "file.type",
                            type.name(),
                                    fileTypes.size() > 1 ? fileTypes.toString() : fileTypes.get(0).toString());
                    illegalParameterValueException.getErrorDescriptor().setErrorCode("incompatible.file.type");
                    throw illegalParameterValueException;
                }
            }
        }
    }
}
