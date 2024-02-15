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
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class ValidationHelper {
    private ValidationHelper(){ }

    public static void addError(ValidationErrors validationErrors, String errorCode, String field, String defaultMessage, Object... arguments) {
        final ValidationErrorImpl error = new ValidationErrorImpl();
        error.setErrorCode(errorCode);
        error.setField(field);
        error.setErrorArguments(arguments);
        error.setDefaultMessage(defaultMessage);
        validationErrors.add(error);
    }

    public static void addMandatoryParameterNotFoundError(ValidationErrors validationErrors, String field) {
        addError(validationErrors, MandatoryParameterNotFoundException.MANDATORY_PARAMETER_ERROR, field, "A value for the field " + field + " must be set");
    }

    public static void addIllegalParameterValueError(ValidationErrors validationErrors, String field, String value, String defaultMessage) {
        addError(validationErrors, IllegalParameterValueException.ERROR_CODE, field, defaultMessage, value);
    }

    public static boolean empty(Object value) {
        return value == null || "".equals(value) || (value instanceof String && "".equals(((String) value).replaceAll("\\s", "")))
                || (value.getClass().isArray() && Array.getLength(value) == 0)
                || (value instanceof Collection && ((Collection)value).isEmpty())
                || (value instanceof Map && ((Map)value).isEmpty())
                || (value instanceof ResourceReference && empty(((ResourceReference) value).getTargetURI()));
    }
}