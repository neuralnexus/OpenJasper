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

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class JndiJdbcDataSourceResourceConverter extends ResourceConverterImpl<JndiJdbcReportDataSource, ClientJndiJdbcDataSource> {

    @Autowired
    private ProfileAttributesResolver profileAttributesResolver;

	@Override
    protected JndiJdbcReportDataSource resourceSpecificFieldsToServer(ExecutionContext ctx, ClientJndiJdbcDataSource clientObject, JndiJdbcReportDataSource resultToUpdate, List<Exception> exceptions, ToServerConversionOptions options) throws IllegalParameterValueException {
        resultToUpdate.setJndiName(clientObject.getJndiName());
        resultToUpdate.setTimezone(clientObject.getTimezone());
        return resultToUpdate;
    }

    @Override
    protected ClientJndiJdbcDataSource resourceSpecificFieldsToClient(ClientJndiJdbcDataSource client, JndiJdbcReportDataSource serverObject, ToClientConversionOptions options) {

        if (options != null && options.getIncludes() != null && options.getIncludes().contains("profileAttributesResolved")) {
        	JndiJdbcReportDataSource updatedJndiJdbcDataSource = profileAttributesResolver.mergeResource(serverObject);
            if (updatedJndiJdbcDataSource != null) {
                serverObject = (JndiJdbcReportDataSource) updatedJndiJdbcDataSource;
            }
//            client.setPassword(serverObject.getPassword());
        }

    	client.setJndiName(serverObject.getJndiName());
        client.setTimezone(serverObject.getTimezone());
        return client;
    }

    public ProfileAttributesResolver getProfileAttributesResolver() {
        return profileAttributesResolver;
    }

    public void setProfileAttributesResolver(ProfileAttributesResolver profileAttributesResolver) {
        this.profileAttributesResolver = profileAttributesResolver;
    }
}
