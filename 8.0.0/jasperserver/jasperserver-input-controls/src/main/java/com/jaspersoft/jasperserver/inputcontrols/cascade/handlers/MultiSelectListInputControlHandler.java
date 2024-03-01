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
package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MultiSelectListInputControlHandler extends SingleSelectListInputControlHandler {

    @Override
    protected Object internalConvertParameterValueFromRawData(String[] rawData, InputControl inputControl, ReportInputControlInformation info) throws CascadeResourceNotFoundException, InputControlValidationException {
        Object result = null;
        if (rawData != null) {
            List<Object> values = new ArrayList<Object>();
            for (String currentRawValue : rawData) {
                try {
                    values.add(dataConverterService.convertSingleValue(currentRawValue, inputControl, info));
                } catch (InputControlValidationException e) {
                    // Do not add this value to the list
                }
            }
            result = values;
        }
        return result;
    }

    @Override
    protected void internalValidateValue(Object value, InputControl inputControl, ReportInputControlInformation info) throws InputControlValidationException, CascadeResourceNotFoundException {
        if (value instanceof Collection) {
            final Collection<Object> collection = (Collection<Object>) value;
            List<Object> invalidValues = new ArrayList<Object>();
            for (Object o : collection) {
                try {
                    super.internalValidateValue(o, inputControl, info);
                } catch (InputControlValidationException e) {
                    invalidValues.add(o);
                }
            }
            // Reject invalid values from list
            collection.removeAll(invalidValues);
        }
    }

    @Override
    protected Boolean hasValue(Object value) {
        return value instanceof Collection<?> && !((Collection<?>) value).isEmpty();
    }

    @Override
    public String[] formatValue(Object typedValue, InputControl control, ReportInputControlInformation info) throws CascadeResourceNotFoundException {
        if (typedValue instanceof Collection) {
            @SuppressWarnings("unchecked") final Collection<Object> values = (Collection) typedValue;
            List<String> strings = new ArrayList<String>();
            for (Object currentValue : values) {
                strings.add(dataConverterService.formatSingleValue(currentValue, control, info));
            }
            return strings.toArray(new String[strings.size()]);
        } else if (typedValue != null) {
            // JS-26987 after changing adhoc view filter from EQUALS to IS ONE OF, existing scheduled report jobs start failing
            // workaround:  we convert single parameter to a list which contains a single item
            List<Object> values = Arrays.asList(typedValue);
            return formatValue(values, control, info);
        } else {
            return new String[0];
        }
    }

    /**
     * Check if selected values collection contains given value
     *
     * @param valueToCheck value to check
     * @param selected     selected value
     * @return true if selected values collection contains given value
     */
    @Deprecated
    protected boolean matches(Object valueToCheck, Object selected) {
        Boolean result = false;
        if(selected instanceof Collection<?>){
            Collection<?> selectedValues = (Collection<?>) selected;
            for (Object currentSelectedValue : selectedValues){
                if(super.matches(valueToCheck,preprocessValue(currentSelectedValue))){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     *
     * @param inputControl
     * @param info
     * @param selectedValues
     * @param selectedValuesList
     * @param defaultValue
     * @return
     * @throws CascadeResourceNotFoundException
     */

    protected List<InputControlOption> populateSelectedValuesWithNoLabel(InputControl inputControl, ReportInputControlInformation info, List<InputControlOption> selectedValues, List<Object> selectedValuesList, Object defaultValue) throws CascadeResourceNotFoundException {
        InputControlOption inputControlOption;
        if (defaultValue != null) {
            if (defaultValue instanceof Collection<?>) {
                Collection<?> defaultValuesList = (Collection<?>) defaultValue;
                for (Object currentSelectedValue : defaultValuesList) {
                    inputControlOption = buildInputControlOption(null, dataConverterService.formatSingleValue(currentSelectedValue, inputControl, info));
                    inputControlOption.setSelected(null);
                    selectedValues.add(inputControlOption);
                    selectedValuesList.add(currentSelectedValue);
                }
            }
        }
        return selectedValues;
    }


    @Override
    protected boolean isNothingSelected(String inputControlName, Map<String, Object> incomingParameters) {
        Object incomingValue = incomingParameters.get(inputControlName);
        return (incomingValue instanceof Collection<?>) && (((Collection<?>) incomingValue).isEmpty());
    }

    @Override
    public boolean isNothingSelected(String[] rawData) {
        //Still need to keep [~NOTHING~] notation for mulstiselect in order to allow
        //nothing selection set for parameter in URL
        return (rawData != null && rawData.length == 0) || super.isNothingSelected(rawData);
    }

    @Override
    public void applyNothingSelected(String controlName, Map<String, Object> parameters) {
        parameters.put(controlName, Collections.emptyList());
    }

    @Override
    protected boolean shouldAddNothinSelectedOption(InputControl inputControl) {
        return false;
    }

    /**
     * @param selectedValues list of selected values
     * @return whole list of values as value (multiselect)
     */
    @Override
    protected Object getConcreteSelectedValue(List<Object> selectedValues) {
        return selectedValues;
    }

    @Override
    protected void internalApplyNothingSubstitution(String controlName, Map<String, Object> parameters) {
        // Multiselect input control has full support of nothing selection.
        // Therefore this handler don't need to perform any actions in this deprecated method
    }
}

