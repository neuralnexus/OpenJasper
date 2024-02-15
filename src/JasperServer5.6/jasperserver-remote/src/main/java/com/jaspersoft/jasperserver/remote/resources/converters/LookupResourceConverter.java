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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.search.common.ResourceDetails;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: LookupResourceConverter.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class LookupResourceConverter extends ResourceConverterImpl<ResourceLookup, ClientResourceLookup> {

    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    @Override
    public ResourceDetails toServer(ClientResourceLookup clientObject, ResourceLookup resultToUpdate, ToServerConversionOptions options) {
        throw new IllegalStateException("ResourceLookup is read only object. ToServer conversion isn't supported");
    }

    @Override
    protected ResourceLookup resourceSpecificFieldsToServer(ClientResourceLookup clientObject, ResourceLookup resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        throw new IllegalStateException("ResourceLookup is read only object. ToServer conversion isn't supported");
    }

    @Override
    public ResourceLookup toServer(ClientResourceLookup clientObject, ToServerConversionOptions options) {
        throw new IllegalStateException("ResourceLookup is read only object. ToServer conversion isn't supported");
    }

    @Override
    protected ClientResourceLookup resourceSpecificFieldsToClient(ClientResourceLookup client, ResourceLookup serverObject, ToClientConversionOptions options) {
        client.setResourceType(toClientResourceType(serverObject.getResourceType()));
        return client;
    }

    protected String toClientResourceType(String serverResourceType){
        final ToClientConverter typeProcessor = genericTypeProcessorRegistry.getTypeProcessor(serverResourceType, ToClientConverter.class, false);
        return typeProcessor != null ? typeProcessor.getClientResourceType() : "unknown";
    }
}
