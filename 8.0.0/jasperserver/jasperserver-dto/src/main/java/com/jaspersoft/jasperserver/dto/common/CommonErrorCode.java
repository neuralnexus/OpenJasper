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
package com.jaspersoft.jasperserver.dto.common;

import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.Params.ERROR_MESSAGE;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.Params.INVALID_PARAM_NAME;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.Params.INVALID_PARAM_VALUE;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.Params.PROPERTY_PATH;
import static com.jaspersoft.jasperserver.dto.common.validations.ArrayUtils.checkArraysLength;
import static com.jaspersoft.jasperserver.dto.common.validations.ArrayUtils.objArrayToStringArray;
import static com.jaspersoft.jasperserver.dto.common.validations.ArrayUtils.objectToString;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public enum CommonErrorCode implements ErrorDescriptorTemplate {
    MANDATORY_PARAMETER_ERROR(Codes.MANDATORY_PARAMETER_ERROR,
            paramNames(PROPERTY_PATH)),
    EXCEPTION_REMOTE_ILLEGAL_PARAMETER_VALUE_ERROR(Codes.EXCEPTION_REMOTE_ILLEGAL_PARAMETER_VALUE_ERROR,
            paramNames(INVALID_PARAM_NAME, INVALID_PARAM_VALUE, ERROR_MESSAGE));

    String code;
    String[] paramNames;

    CommonErrorCode(String code, String[] paramNames) {
        this.code = code;
        this.paramNames = paramNames;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String[] getParamNames() {
        return this.paramNames;
    }

    @Override
    public ErrorDescriptor createDescriptor(Object... propValues) {
        return new ErrorDescriptor().setErrorCode(this.code)
                //For backport compatibilities we should keep parameters.
                .setParameters(objArrayToStringArray(propValues))
                .setProperties(buildProperties(paramNames, propValues));
    }

    @Override
    public String toString() {
        return this.code;
    }

    public interface Codes {
        String MANDATORY_PARAMETER_ERROR = "mandatory.parameter.error";
        String EXCEPTION_REMOTE_ILLEGAL_PARAMETER_VALUE_ERROR = "exception.remote.illegal.parameter.value.error";
    }

    public interface Params {
        String PROPERTY_PATH = "propertyPath";
        String INVALID_PARAM_NAME = "invalidParamName";
        String INVALID_PARAM_VALUE = "invalidParamValue";
        String ERROR_MESSAGE = "errorMessage";
    }

    public static String[] paramNames(String... paramNames) {
        return paramNames;
    }

    public static List<ClientProperty> buildProperties(String[] paramNames, Object[] paramValues) {
        checkPropValues(paramNames, paramValues);

        List<ClientProperty> properties = null;
        if (paramNames != null) {
            int index = 0;
            properties = new ArrayList<>();
            for (String paramName : paramNames) {
                properties.add(new ClientProperty().setKey(paramName).setValue(objectToString(paramValues[index])));
                index++;
            }
        }
        return properties;
    }

    public static String[] toPropertyValues(List<ClientProperty> properties) {
        if (properties != null) {
            String[] propertyValues = new String[properties.size()];
            int index = 0;
            for (ClientProperty property : properties) {
                propertyValues[index++] = property.getValue();
            }
            return propertyValues;
        }
        return null;
    }

    public static void checkPropValues(String[] paramNames, Object[] paramValues) {
        if (!checkArraysLength(paramNames, paramValues)) {
            throw new IllegalArgumentException("The length arrays of parameter values is" +
                    " not equals to parameter names length");
        }
    }

}
