/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class CustomDataSourceResourceValidator extends GenericResourceValidator<CustomReportDataSource> {
    @Resource(name = "customDataSourceServiceFactory")
    private CustomReportDataSourceServiceFactory customDataSourceFactory;

    @Override
    protected void internalValidate(CustomReportDataSource resource, List<Exception> errors, Map<String, String[]> additionalParameters) {
        if (empty(resource.getServiceClass()) && empty(resource.getDataSourceName())){
            errors.add(new MandatoryParameterNotFoundException("ServiceClass or DataSourceName"));
        } else {
            CustomDataSourceDefinition definition = customDataSourceFactory.getDefinition(resource);
            if (definition == null){
                errors.add(new IllegalParameterValueException(
                        "ServiceClass or DataSourceName", resource.getDataSourceName(), null));
            } else {
                if (empty(resource.getServiceClass())){
                    resource.setServiceClass(definition.getServiceClassName());
                }
                if (empty(resource.getServiceClass())){
                    errors.add(new MandatoryParameterNotFoundException("ServiceClass"));
                }

                if (definition.getValidator() != null) {
                    if (resource.getPropertyMap() == null){
                        resource.setPropertyMap(new HashMap());
                    }
                    definition.getValidator().validatePropertyValues(resource, new ErrorsAdapter(errors, "CustomDataSource"));
                }
            }
        }
    }
}
