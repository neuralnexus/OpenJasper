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

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class DataTypeResourceValidator extends GenericResourceValidator<DataType> {
    @Override
    protected void internalValidate(DataType resource, List<Exception> errors, Map<String, String[]> additionalParameters) {
        if (!empty(resource.getMaxValue()) && !empty(resource.getMinValue()) && resource.getMinValue().compareTo(resource.getMaxValue()) > 0){
            errors.add(new IllegalParameterValueException("The max value must be greater than min value", "maxValue", resource.getMaxValue().toString()));
        }

        if (resource.getDataTypeType() == 0){
            errors.add(new MandatoryParameterNotFoundException("Type"));
        }

        if (resource.getMaxLength() != null && resource.getMaxLength() <= 0){
            errors.add(new IllegalParameterValueException("The value of the maxLength property must be more than 0", "maxLength", resource.getMaxLength().toString()));
        }

        if (!empty(resource.getRegularExpr())){
            try {
                Pattern.compile(resource.getRegularExpr());
            } catch (Exception e) {
                errors.add(new IllegalParameterValueException(e.getMessage(), "pattern", resource.getRegularExpr()));
            }
        }
    }
}
