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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addIllegalParameterValueError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addMandatoryParameterNotFoundError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class JdbcDataSourceResourceValidator extends GenericResourceValidator<JdbcReportDataSource> {
    @Resource
    private JdbcDriverService jdbcDriverService;

    @Override
    protected void internalValidate(JdbcReportDataSource resource, ValidationErrors errors) {
        if (empty(resource.getDriverClass())){
            addMandatoryParameterNotFoundError(errors, "DriverClass");
        } else {
            if (!jdbcDriverService.isRegistered(resource.getDriverClass())){
                addIllegalParameterValueError(errors, "DriverClass", resource.getDriverClass(), "Specified driver class is not registered");
            }
        }
        if (empty(resource.getConnectionUrl())){
            addMandatoryParameterNotFoundError(errors, "ConnectionUrl");
        } else {
            if (!resource.getConnectionUrl().trim().startsWith("jdbc:")){
                addIllegalParameterValueError(errors, "ConnectionUrl", resource.getConnectionUrl(), "The JDBC URI must start with 'jdbc:'");
            }
        }
        if (!empty(resource.getTimezone()) && !resource.getTimezone().matches("^[a-zA-Z/_]+$")){
            addIllegalParameterValueError(errors, "Timezone", resource.getTimezone(), "The timezone value contains not permitted characters");
        }
    }
}
