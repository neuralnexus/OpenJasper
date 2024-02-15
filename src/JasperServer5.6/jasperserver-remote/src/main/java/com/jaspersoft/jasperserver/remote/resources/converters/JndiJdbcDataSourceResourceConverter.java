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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: JndiJdbcDataSourceResourceConverter.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class JndiJdbcDataSourceResourceConverter extends ResourceConverterImpl<JndiJdbcReportDataSource, ClientJndiJdbcDataSource> {
    @Override
    protected JndiJdbcReportDataSource resourceSpecificFieldsToServer(ClientJndiJdbcDataSource clientObject, JndiJdbcReportDataSource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        resultToUpdate.setJndiName(clientObject.getJndiName());
        resultToUpdate.setTimezone(clientObject.getTimezone());
        return resultToUpdate;
    }

    @Override
    protected ClientJndiJdbcDataSource resourceSpecificFieldsToClient(ClientJndiJdbcDataSource client, JndiJdbcReportDataSource serverObject, ToClientConversionOptions options) {
        client.setJndiName(serverObject.getJndiName());
        client.setTimezone(serverObject.getTimezone());
        return client;
    }
}
