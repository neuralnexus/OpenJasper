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

package com.jaspersoft.jasperserver.ws.axis2.repository;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverterService;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;

import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id: DataTypeHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class DataTypeHandler extends RepositoryResourceHandler {

    private DataConverterService dataConverterService;

    public DataConverterService getDataConverterService() {
        return dataConverterService;
    }

    public void setDataConverterService(DataConverterService dataConverterService) {
        this.dataConverterService = dataConverterService;
    }

    public Class getResourceType() {
        return DataType.class;
    }

    protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
            Map arguments, RepositoryServiceContext serviceContext) {
        DataType fileResource = (DataType) resource;
        descriptor.setWsType(ResourceDescriptor.TYPE_DATA_TYPE);
        descriptor.setHasData(false);
        descriptor.setIsReference(false);

        descriptor.setDataType(fileResource.getType());
        descriptor.setPattern(fileResource.getRegularExpr());
        descriptor.setMaxValue(fileResource.getMaxValue() == null ? null : dataConverterService.formatSingleValue(
                        fileResource.getMaxValue(), fileResource, fileResource.getMaxValue().getClass()));
        descriptor.setMinValue(fileResource.getMinValue() == null ? null : dataConverterService.formatSingleValue(
                fileResource.getMinValue(), fileResource, fileResource.getMinValue().getClass()));
        descriptor.setStrictMax(fileResource.isStrictMax());
        descriptor.setStrictMin(fileResource.isStrictMin());
    }

    protected void updateResource(Resource resource,
            ResourceDescriptor descriptor, RepositoryServiceContext serviceContext) {
        DataType dataType = (DataType) resource;
        // Validations should be done in the save method...
        dataType.setType(descriptor.getDataType());
        dataType.setRegularExpr(descriptor.getPattern());
        final String rawMaxValue = descriptor.getMaxValue();
        final String rawMinValue = descriptor.getMinValue();
        try {
            dataType.setMaxValue(rawMaxValue != null ? (Comparable) dataConverterService.convertSingleValue(rawMaxValue, dataType) : null);
            dataType.setMinValue(rawMinValue != null ? (Comparable) dataConverterService.convertSingleValue(rawMinValue, dataType) : null);
        } catch (InputControlValidationException e) {
            throw new RuntimeException(e.getValidationError() != null && e.getValidationError().getDefaultMessage() != null ?
                    e.getValidationError().getDefaultMessage() : e.getMessage(), e);
        }
        dataType.setStrictMax(descriptor.isStrictMax());
        dataType.setStrictMin(descriptor.isStrictMin());
    }

}
