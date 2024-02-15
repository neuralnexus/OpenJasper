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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.dto.resources.AbstractClientDataSourceHolder;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: DataSourceHolderResourceConverter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class DataSourceHolderResourceConverter<ResourceType extends Resource, ClientType extends AbstractClientDataSourceHolder<ClientType>>
        extends ResourceConverterImpl<ResourceType, ClientType> {
    @javax.annotation.Resource
    protected ResourceReferenceConverterProvider resourceReferenceConverterProvider;

    protected abstract void setDataSourceToResource(ResourceReference dataSourceReference, ResourceType resource);

    protected abstract ResourceReference getDataSourceFromResource(ResourceType resource);

    @Override
    protected ResourceType genericFieldsToServer(ClientType clientObject, ResourceType resultToUpdate, ToServerConversionOptions options)
            throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        resultToUpdate = super.genericFieldsToServer(clientObject, resultToUpdate, options);
        ResourceReference dataSourceReference = resourceReferenceConverterProvider
                .getConverterForType(ClientReferenceableDataSource.class)
                .toServer(clientObject.getDataSource(), getDataSourceFromResource(resultToUpdate), options);
        setDataSourceToResource(dataSourceReference, resultToUpdate);
        return resultToUpdate;
    }

    @Override
    protected ClientType genericFieldsToClient(ClientType client, ResourceType serverObject, ToClientConversionOptions options) {
        final ResourceReference dataSource = getDataSourceFromResource(serverObject);
        client.setDataSource(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableDataSource.class)
                .toClient(dataSource, options));
        return super.genericFieldsToClient(client, serverObject, options);
    }
}
