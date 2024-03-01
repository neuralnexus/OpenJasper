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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.*;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenko
 * @version $Id$
 */
@Component
public class ListOfValuesValidator extends GenericResourceValidator<ListOfValues> {
    private final Pattern forbiddenCharacters = Pattern.compile("[\"<>]+");

    @Override
    protected void internalValidate(ListOfValues resource, List<Exception> errors, Map<String, String[]> additionalParameters) {
        Set<String> names = new HashSet<String>();
        for (ListOfValuesItem item : resource.getValues()){
            if (empty(item.getLabel())) {
                errors.add(new MandatoryParameterNotFoundException("listOfValuesItem.label"));
            } else if (names.contains(item.getLabel())) {
                errors.add(new IllegalParameterValueException("The label " + item.getLabel() + " already exist in the list of values", "listOfValuesItem.label", item.getLabel()));
            } else {
                names.add(item.getLabel());
                if (item.getLabel().length() > 255){
                    errors.add(new IllegalParameterValueException("The label " + item.getLabel() + " is longer than 255 characters", "listOfValuesItem.label", item.getLabel()));
                }
                if (forbiddenCharacters.matcher(item.getLabel()).matches()){
                    errors.add(new IllegalParameterValueException("The label " + item.getLabel() + " should not contain symbols \"<>", "listOfValuesItem.label", item.getLabel()));
                }
            }

            if (empty(item.getValue())){
                errors.add(new MandatoryParameterNotFoundException("items."+ item.getLabel()));
            } else {
                if (item.getValue() instanceof String){
                    String value = (String)item.getValue();

                    if (forbiddenCharacters.matcher(value).matches()){
                        errors.add(new IllegalParameterValueException("The value " + value
                                + " should not contain symbols \"<>", "listOfValuesItem.value", value));
                    }
                }
            }
        }
    }
}
