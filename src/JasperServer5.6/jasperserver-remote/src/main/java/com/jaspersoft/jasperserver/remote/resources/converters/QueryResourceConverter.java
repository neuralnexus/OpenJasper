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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.dto.resources.ClientQuery;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: QueryResourceConverter.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class QueryResourceConverter extends DataSourceHolderResourceConverter<Query, ClientQuery> {

    @Override
    protected Query resourceSpecificFieldsToServer(ClientQuery clientObject, Query resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        resultToUpdate.setSql(clientObject.getValue());
        resultToUpdate.setLanguage(clientObject.getLanguage());
        return resultToUpdate;
    }

    @Override
    protected ClientQuery resourceSpecificFieldsToClient(ClientQuery client, Query serverObject, ToClientConversionOptions options) {
        client.setLanguage(serverObject.getLanguage());
        client.setValue(serverObject.getSql());
        return client;
    }

    @Override
    protected void setDataSourceToResource(ResourceReference dataSourceReference, Query resource) {
        resource.setDataSource(dataSourceReference);
    }

    @Override
    protected ResourceReference getDataSourceFromResource(Query resource) {
        return resource.getDataSource();
    }
}
