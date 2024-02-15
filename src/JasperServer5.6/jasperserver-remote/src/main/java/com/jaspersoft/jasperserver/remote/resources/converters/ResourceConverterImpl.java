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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.attachments.AttachmentsProcessor;
import com.jaspersoft.jasperserver.remote.resources.validation.BasicResourceValidator;
import com.jaspersoft.jasperserver.remote.resources.validation.ResourceValidator;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.war.helper.GenericParametersHelper;
import com.jaspersoft.jasperserver.war.util.CalendarFormatProvider;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ResourceConverterImpl.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
public abstract class ResourceConverterImpl<ResourceType extends Resource, ClientType extends ClientResource<ClientType>> implements ResourceConverter<ResourceType, ClientType> {
    @javax.annotation.Resource(name = "mappingResourceFactory")
    protected ResourceFactory objectFactory;

    @javax.annotation.Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @javax.annotation.Resource(name = "concretePermissionsService")
    private PermissionsService permissionsService;

    @javax.annotation.Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    private Class<ClientType> clientTypeClass;

    private String serverResourceType;

    private String clientResourceType;

    private final ResourceValidator defaultValidator = new BasicResourceValidator();

    // object factory returns correct type of resource. So, cast below is safe
    @SuppressWarnings("unchecked")
    protected ResourceType getNewResourceInstance() {
        return (ResourceType) objectFactory.newResource(null, getServerResourceType());
    }

    protected DateFormat getDateTimeFormat() {
        return calendarFormatProvider.getDatetimeFormat();
    }

    public String getServerResourceType() {
        if (serverResourceType == null) {
            final Class<?> serverResourceTypeClass = GenericParametersHelper.getGenericTypeArgument(this.getClass(), ResourceConverter.class, 0);
            if (serverResourceTypeClass != null) {
                serverResourceType = serverResourceTypeClass.getName();
            } else {
                throw new IllegalStateException("Unable to identify serverResourceType. It can happen because " +
                        getClass().getName() + " is raw implementation of " + ResourceConverter.class.getName());
            }

        }
        return serverResourceType;
    }

    @Override
    public ResourceType toServer(ClientType clientObject, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        return toServer(clientObject, null, options);
    }

    @Override
    public ResourceType toServer(ClientType clientObject, ResourceType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        ResourceType resource = genericFieldsToServer(clientObject, resultToUpdate, options);
        resource = resourceSpecificFieldsToServer(clientObject, resource, options);
        if (options != null && options.getAttachments() != null) {
            // impossible to build
            @SuppressWarnings("unchecked")
            AttachmentsProcessor<ResourceType> attachmentsProcessor = genericTypeProcessorRegistry.getTypeProcessor(getServerResourceType(), AttachmentsProcessor.class, false);
            if (attachmentsProcessor != null) {
                resource = attachmentsProcessor.processAttachments(resource, options.getAttachments());
            }
        }
        validateResource(resource);
        return resource;
    }

    protected void validateResource(ResourceType resource) {
        ResourceValidator<ResourceType> validator = genericTypeProcessorRegistry.getTypeProcessor(resource.getResourceType(), ResourceValidator.class, false);
        (validator != null ? validator : defaultValidator).validate(resource);
    }

    protected ResourceType genericFieldsToServer(ClientType clientObject, ResourceType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        if (clientObject.getLabel() == null || "".equals(clientObject.getLabel())) {
            throw new MandatoryParameterNotFoundException(getClientResourceType() + ".label");
        }
        if (resultToUpdate == null) {
            resultToUpdate = getNewResourceInstance();
            resultToUpdate.setVersion(Resource.VERSION_NEW);
        } else {
            resultToUpdate.setVersion(clientObject.getVersion() == null || (options != null && options.isResetVersion()) ?
                    Resource.VERSION_NEW : clientObject.getVersion());
        }
        resultToUpdate.setURIString(clientObject.getUri());
        final DateFormat dateTimeFormatter = getDateTimeFormat();
        dateTimeFormatter.setTimeZone(TimeZoneContextHolder.getTimeZone());
        if (clientObject.getCreationDate() != null) {
            try {
                resultToUpdate.setCreationDate(dateTimeFormatter.parse(clientObject.getCreationDate()));
            } catch (ParseException ex) {
                throw new IllegalParameterValueException("creationDate", clientObject.getCreationDate());
            }
        }
        if (clientObject.getUpdateDate() != null) {
            try {
                resultToUpdate.setUpdateDate(dateTimeFormatter.parse(clientObject.getUpdateDate()));
            } catch (ParseException ex) {
                throw new IllegalParameterValueException("updateDate", clientObject.getUpdateDate());
            }
        }
        resultToUpdate.setDescription(clientObject.getDescription());
        resultToUpdate.setLabel(clientObject.getLabel());
        return resultToUpdate;
    }

    protected abstract ResourceType resourceSpecificFieldsToServer(ClientType clientObject, ResourceType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException;

    @Override
    public ClientType toClient(ResourceType serverObject, ToClientConversionOptions options) {
        final ClientType client = genericFieldsToClient(getNewClientObjectInstance(), serverObject, options);
        return resourceSpecificFieldsToClient(client, serverObject, options);
    }

    protected ClientType genericFieldsToClient(ClientType client, ResourceType serverObject, ToClientConversionOptions options) {
        final DateFormat dateTimeFormatter = getDateTimeFormat();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        dateTimeFormatter.setTimeZone(TimeZoneContextHolder.getTimeZone());
        if (serverObject.getCreationDate() != null) {
            client.setCreationDate(dateTimeFormatter.format(serverObject.getCreationDate()));
        }
        client.setDescription(serverObject.getDescription());
        client.setLabel(serverObject.getLabel());
        if (serverObject.getUpdateDate() != null) {
            client.setUpdateDate(dateTimeFormatter.format(serverObject.getUpdateDate()));
        }
        client.setUri(serverObject.getURIString());
        client.setVersion(serverObject.getVersion());
        client.setPermissionMask(permissionsService.getEffectivePermission(serverObject, authentication).getPermissionMask());
        return client;
    }

    protected abstract ClientType resourceSpecificFieldsToClient(ClientType client, ResourceType serverObject, ToClientConversionOptions options);

    protected ClientType getNewClientObjectInstance() {
        try {
            return getClientTypeClass().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't instantiate client object", e);
        }
    }

    // Client object class is extracted from real implementation class by reflection. So, cast is safe.
    @SuppressWarnings("unchecked")
    public Class<ClientType> getClientTypeClass() {
        if (clientTypeClass == null) {
            clientTypeClass = (Class) GenericParametersHelper.getGenericTypeArgument(this.getClass(), ResourceConverter.class, 1);
            if (clientTypeClass == null) {
                throw new IllegalStateException("Unable to identify clientTypeClass. It can happen because " +
                        getClass().getName() + " is raw implementation of " + ResourceConverter.class.getName());
            }
        }
        return clientTypeClass;
    }

    protected final ResourceReference findReference(List<ResourceReference> references, String uri) {
        ResourceReference result = null;
        if (references != null) {
            for (ResourceReference reference : references) {
                if (reference.getTargetURI().equals(uri)) {
                    result = reference;
                }
            }
        }
        return result;
    }

    public String getClientResourceType() {
        if (clientResourceType == null) {
            clientResourceType = ClientTypeHelper.extractClientType(getClientTypeClass());
        }
        return clientResourceType;
    }
}
