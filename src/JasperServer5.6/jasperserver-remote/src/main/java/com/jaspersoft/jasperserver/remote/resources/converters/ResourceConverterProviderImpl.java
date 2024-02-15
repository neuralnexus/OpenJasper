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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ResourceConverterProviderImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service("resourceConverterProvider")
public class ResourceConverterProviderImpl implements ResourceConverterProvider {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private BinaryDataResourceConverter binaryDataResourceConverter;
    private Map<String, ToClientConverter<? super Resource, ? extends ClientResource>> toClientConverters;
    private Map<String, ToServerConverter<? super ClientResource, ? extends Resource>> toServerConverters;
    private volatile boolean initialized = false;

    public ToClientConverter<? super Resource, ? extends ClientResource> getToClientConverter(String serverType) throws IllegalParameterValueException {
        prepareConverters();
        final ToClientConverter<? super Resource, ? extends ClientResource> toClientConverter = toClientConverters.get(serverType);
        if(toClientConverter == null){
            throw new IllegalParameterValueException("type", serverType);
        }
        return toClientConverter;
    }

    @Override
    public ToClientConverter<? super Resource, ? extends ClientResource> getToClientConverter(Resource serverObject) {
        try {
            return getToClientConverter(serverObject instanceof ResourceLookup ? ResourceLookup.class.getName() : serverObject.getResourceType());
        } catch (IllegalParameterValueException e) {
            throw new IllegalStateException("Couldn't find converter for " + serverObject.getResourceType());
        }
    }

    @Override
    public ToServerConverter<? super ClientResource, ? extends Resource> getToServerConverter(ClientResource clientObject) throws IllegalParameterValueException {
        return getToServerConverter(ClientTypeHelper.extractClientType(clientObject.getClass()));
    }

    public ToServerConverter<? super ClientResource, ? extends Resource> getToServerConverter(String clientType) throws IllegalParameterValueException {
        prepareConverters();
        final ToServerConverter<? super ClientResource, ? extends Resource> toServerConverter = toServerConverters.get(clientType != null ? clientType.toLowerCase() : null);
        if(toServerConverter == null){
            throw new IllegalParameterValueException("type", clientType);
        }
        return toServerConverter;
    }

    @Override
    public Class<? extends ClientResource> getClientTypeClass(String clientType) throws IllegalParameterValueException {
        final ResourceConverter<? extends Resource, ? extends ClientResource> resourceConverter =
                (ResourceConverter<? extends Resource, ? extends ClientResource>) getToServerConverter(clientType);
        return resourceConverter.getClientTypeClass();
    }

    // corresponding resourceType of converter assures type safety in further usage
    @SuppressWarnings("unchecked")
    protected void prepareConverters() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    toClientConverters = new HashMap<String, ToClientConverter<? super Resource, ? extends ClientResource>>();
                    toServerConverters = new HashMap<String, ToServerConverter<? super ClientResource, ? extends Resource>>();
                    final List<ResourceConverter<? super Resource, ? extends ClientResource>> converters = getConverters();
                    if (getConverters() != null) {
                        for (ResourceConverter currentConverter : converters) {
                            toClientConverters.put(currentConverter.getServerResourceType(), currentConverter);
                            toServerConverters.put(currentConverter.getClientResourceType().toLowerCase(), currentConverter);
                        }
                    }
                    toServerConverters.put(binaryDataResourceConverter.getClientResourceType(), (ToServerConverter)binaryDataResourceConverter);
                    initialized = true;
                }
            }
        }
    }

    // cast is safe, spring application context assure safety
    @SuppressWarnings("unchecked")
    protected List<ResourceConverter<? super Resource, ? extends ClientResource>> getConverters() {
        final Map<String, ResourceConverter> convertersMap = context.getBeansOfType(ResourceConverter.class);
        return (List) new ArrayList<ResourceConverter>(convertersMap.values());
    }
}
