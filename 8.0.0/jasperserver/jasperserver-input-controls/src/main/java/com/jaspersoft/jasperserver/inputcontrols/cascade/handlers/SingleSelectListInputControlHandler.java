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

import com.jaspersoft.jasperserver.api.common.util.diagnostic.DiagnosticSnapshotPropertyHelper;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters.DataConverterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;


import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.NumberUtils.toBigDecimal;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.SELECTED_ONLY_INTERNAL;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class SingleSelectListInputControlHandler extends BasicInputControlHandler {

    public static final String SELECT = "select";

    private static final Log log = LogFactory.getLog(SingleSelectListInputControlHandler.class);

    private ValuesLoader loader;

    public ValuesLoader getLoader() {
        return loader;
    }

    public void setLoader(ValuesLoader loader) {
        this.loader = loader;
    }

    @Override
    public ReportInputControl buildReportInputControl(InputControl inputControl, String uiType, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        final ReportInputControl reportInputControl = super.buildReportInputControl(inputControl, uiType, dataSource);
        final Set<String> masterDependencies = loader.getMasterDependencies(inputControl, dataSource);
        if (masterDependencies != null){
            reportInputControl.getMasterDependencies().addAll(masterDependencies);
        }
        return reportInputControl;
    }

    @Override
    public Set<String> getMasterDependencies(InputControl control, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        return loader.getMasterDependencies(control, dataSource);
    }

    @Override
    protected Object internalConvertParameterValueFromRawData(String[] rawData, InputControl inputControl, ReportInputControlInformation info) throws CascadeResourceNotFoundException, InputControlValidationException {
        Object value;
        try {
            value = super.internalConvertParameterValueFromRawData(rawData, inputControl, info);
        } catch (InputControlValidationException e) {
            // Explicitly set value for single select list to Null, thus we will recognize later that
            // this error doesn't need to be added to input control state
            e.getValidationError().setInvalidValue(null);
            throw e;
        }
        return value;
    }

    @Override
    protected void internalValidateValue(Object value, InputControl inputControl, ReportInputControlInformation info) throws InputControlValidationException, CascadeResourceNotFoundException {
        final DataType dataType = inputControl.getDataType() != null ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
        try {
            validateSingleValue(value, dataType);
        } catch (InputControlValidationException e) {
            // Explicitly set value for single select list to Null, thus we will recognize later that
            // this error doesn't need to be added to input control state
            e.getValidationError().setInvalidValue(null);
            throw e;
        }
    }

    /**
     * @param inputControl - input control
     * @param dataSource   - data source
     * @param parameters   - incoming parameters
     * @param info         - input control information (contains input control default value)
     * @param parameterTypes
     * @throws CascadeResourceNotFoundException
     *          in case if some required for cascade logic resource isn't found.
     */
    @Override
    protected Map<String,List<InputControlOption>> fillSelectedValue(InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, ReportInputControlInformation info, Map<String, Class<?>> parameterTypes) throws CascadeResourceNotFoundException {
        Map<String, List<InputControlOption>> selectedValuesMap;

        List<InputControlOption> selectedValues = new ArrayList<>();

        // this list contains values to be added to the parameters list, which is used in querying slave dependencies.
        List<Object> selectedValuesList = new ArrayList<>();

        final DataType dataType = inputControl.getDataType() != null ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
        Object defaultValue = info.getDefaultValue();
        final String controlName = inputControl.getName();
        boolean isNothingSelected = isNothingSelected(controlName, parameters);

        //JS-57171 In order to avoid selected values to access DB/cache to fetch the labels for the values
        // we check this condition to see if the selected values should include label.
        if(parameters.containsKey(WITH_NO_LABEL)) {
            selectedValues = populateSelectedValuesWithNoLabel(inputControl, info, selectedValues, selectedValuesList, defaultValue);
        } else {
            List<ListOfValuesItem> values = getListOfValuesItems(inputControl, dataSource, parameters, info, parameterTypes);
            selectedValues = populateSelectedValuesList(inputControl, info, selectedValues, selectedValuesList, dataType, values, defaultValue, isNothingSelected);
        }

        if(selectedValues.isEmpty()) {
            internalApplyNothingSubstitution(controlName, parameters);
            return null;
        } else {
            selectedValuesMap = new HashMap<>();
            selectedValuesMap.put(inputControl.getName(), selectedValues);
            // selected values are exist, update incoming parameters for cascading ICs
            parameters.put(controlName, getConcreteSelectedValue(selectedValuesList));
            return selectedValuesMap;
        }
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
        String value = dataConverterService.formatSingleValue(defaultValue, inputControl, info);
        inputControlOption = buildInputControlOption(null, value);
        inputControlOption.setSelected(null);
        selectedValues.add(inputControlOption);
        selectedValuesList.add(dataConverterService.convertSingleValue(value, inputControl, info));

        //if there is no selected values and if the IC is not mandatory, then include "Nothing" value only in case of single select.
        getDummyValues(inputControl, selectedValues);
        return selectedValues;
    }

    protected List<InputControlOption> populateSelectedValuesList(InputControl inputControl, ReportInputControlInformation info, List<InputControlOption> selectedValues, List<Object> selectedValuesList,  DataType dataType, List<ListOfValuesItem> values, Object defaultValue, boolean isNothingSelected) throws CascadeResourceNotFoundException {
        // construct selected values dictionary as incoming values can be large sometimes
        SelectedValuesDict defaultValueDict = createSelectedValuesDict(defaultValue);
        InputControlOption inputControlOption;
        if (isNotEmpty(values)) {
            // iterate over values and match with the default values
            for (ListOfValuesItem currentItem : values) {
                if(validateInputControlValues(currentItem, dataType)) {
                    Object currentItemValue = getCurrentItemValue(inputControl, info, currentItem);

                    if (!isNothingSelected && defaultValueDict.checkMatch(currentItemValue)) {
                        String value = dataConverterService.formatSingleValue(currentItem.getValue(), inputControl, info);
                        inputControlOption = buildInputControlOption(currentItem.getLabel(), value);
                        inputControlOption.setSelected(null);
                        selectedValues.add(inputControlOption);
                        selectedValuesList.add(currentItem.getValue());
                    }
                }
            }

            if(selectedValues.isEmpty()) {
                // default values are not found in values list
                // this control is mandatory, first value should be selected
                getMandatoryValues(inputControl, info, selectedValues, selectedValuesList, values.get(0), isNothingSelected);
            }
        }

        //if there is no selected values and if the IC is not mandatory, then include "Nothing" value only in case of single select.
        getDummyValues(inputControl, selectedValues);
        return selectedValues;
    }

    protected void getDummyValues(InputControl inputControl, List<InputControlOption> selectedValues) {
        InputControlOption inputControlOption;
        if(CollectionUtils.isEmpty(selectedValues) && shouldAddNothinSelectedOption(inputControl)) {
            inputControlOption = buildInputControlOption(NOTHING_SUBSTITUTION_LABEL, NOTHING_SUBSTITUTION_VALUE);
            inputControlOption.setSelected(null);
            selectedValues.add(inputControlOption);
        }
    }

    protected void getMandatoryValues(InputControl inputControl,
                                      ReportInputControlInformation info,
                                      List<InputControlOption> selectedValues,
                                      List<Object> selectedValuesList,
                                      ListOfValuesItem item,
                                      boolean isNothingSelected) throws CascadeResourceNotFoundException {
        if (inputControl.isMandatory() && !isNothingSelected) {
            final Object currentValue = getCurrentItemValue(inputControl, info, item);
            final String formattedValue = dataConverterService.formatSingleValue(currentValue, inputControl, info);

            InputControlOption inputControlOption = buildInputControlOption(item.getLabel(), formattedValue);
            inputControlOption.setSelected(null);
            selectedValues.add(inputControlOption);
            selectedValuesList.add(currentValue);
        }
    }

    private boolean validateInputControlValues(ListOfValuesItem currentItem, DataType dataType) {
        boolean optionIsValid = true;
        try {
            validateSingleValue(currentItem.getValue(), dataType);
        } catch (InputControlValidationException e) {
            optionIsValid = false;
        }
        return optionIsValid;
    }

    /**
     * @param state        - state object to fill
     * @param inputControl - input control
     * @param dataSource   - data source
     * @param parameters   - incoming parameters
     * @param info         - input control information (contains input control default value)
     * @param parameterTypes
     * @throws CascadeResourceNotFoundException
     *          in case if some required for cascade logic resource isn't found.
     */
    @Override
    protected void fillStateValue(InputControlState state, InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, ReportInputControlInformation info, Map<String, Class<?>> parameterTypes) throws CascadeResourceNotFoundException {
        boolean selectAll;
        List<ListOfValuesItem> values = getListOfValuesItems(inputControl, dataSource, parameters, info, parameterTypes);
        final DataType dataType = inputControl.getDataType() != null ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
        List<InputControlOption> options = new ArrayList<>();

        InputControlOption nothingOption = null;
        Pair<InputControlOption, Object> firstValidValue = null;

        // default options will be collected to defaultOptions map
        // default values are collected to a map because of option object and typed value are both required
        Map<InputControlOption, Object> defaultOptions = new HashMap<>();
        // selected values will be collected to selectedValues list
        List<Object> selectedValues = new ArrayList<>();
        final String controlName = inputControl.getName();

        boolean isNothingSelected = isNothingSelected(controlName, parameters);
        boolean showSelectedOnly = isShowSelectedOnlyValues(parameters);

        Object incomingValue = setIncomingValue(controlName, parameters, info);

        // construct selected values dictionary as incoming values can be large sometimes
        SelectedValuesDict incomingValueDict = createSelectedValuesDict(incomingValue);

        Object defaultValue = info.getDefaultValue();
        // construct default values dictionary
        SelectedValuesDict defaultValueDict = createSelectedValuesDict(defaultValue);

        // check if the select parameter value is "allValues"
        selectAll = isAllValues(inputControl, parameters);

        Map<String, String> errors = new HashMap<>();

        final int limit = inputControlPagination.getLimit(inputControl, parameters, errors);
        final int offset = inputControlPagination.getOffset(
                inputControl, parameters, values != null ? values.size() : 0, errors
        );

        inputControlPagination.checkLimitOffsetRange(errors);

        if (values != null && !values.isEmpty()) {
            int position = 0;
            int actualTotalSize = values.size();

            // iterate over values
            for (ListOfValuesItem currentItem : values) {
                // Add option only if it meets validation rules of the dataType
                if (!validateInputControlValues(currentItem, dataType)) {
                    // Re-validate offset each time because of "validateInputControlValues" might fail
                    // and number of options will be less then number of actual values
                    inputControlPagination.validateOffset(offset, --actualTotalSize, null);
                    continue;
                }

                final Object currentItemValue = getCurrentItemValue(inputControl, info, currentItem);

                // check to see which values to be selected and when selectAll is true, return true for all the values.
                boolean isSelected = selectAll || (!isNothingSelected && incomingValueDict.checkMatch(currentItemValue));
                if (isSelected) {
                    // collect selected values to update parameters with selected data
                    selectedValues.add(currentItemValue);
                }

                final boolean showSelectedValuesAndValueIsSelected = showSelectedOnly && isSelected;
                final boolean paginationIsNotIgnoredAndOptionsWithinLimitAndOffset = !showSelectedOnly
                        && position >= offset
                        && options.size() < limit;

                InputControlOption option = null;
                // If we showSelectedOnly is true (meaning we need to return only "selected" values) then we need
                // to ignore pagination completely. Otherwise check if options are within limit and offset
                if (showSelectedValuesAndValueIsSelected || paginationIsNotIgnoredAndOptionsWithinLimitAndOffset) {
                    option = getOrBuildInputControlOption(inputControl, info, currentItem, currentItemValue, null);
                    option.setSelected(isSelected);
                    options.add(option);
                }
                position++;

                // Collect default options if no selected values are found and not a case that nothing is selected
                if (selectedValues.isEmpty()
                        && !isNothingSelected
                        && defaultValueDict.checkMatch(currentItemValue)) {
                        option = getOrBuildInputControlOption(inputControl, info, currentItem, currentItemValue, option);
                        defaultOptions.put(option, currentItemValue);
                }
                // Remember a first valid value and build input control option. It is required later if
                // we have 0 selected values, and input control is mandatory - then we need to "select" 1-st value
                if (firstValidValue == null) {
                    firstValidValue = Pair.create(
                            getOrBuildInputControlOption(inputControl, info, currentItem, currentItemValue, option),
                            currentItemValue
                    );
                }
                if (isNothingValue(currentItemValue) || isNothingInputControlOption(option)) {
                    nothingOption = getOrBuildInputControlOption(inputControl, info, currentItem, currentItemValue, option);
                }
            }
        }

        if (selectedValues.isEmpty()) {
            // incoming value isn't found in values list
            if (!defaultOptions.isEmpty()) {
                // default values found. So, they should be selected
                for (InputControlOption defaultOption : defaultOptions.keySet()) {
                    defaultOption.setSelected(true);
                }
                // put default values to selected values list for further update of incoming parameters map
                selectedValues = new ArrayList<>(defaultOptions.values());

                // If we don't have selected and options - set options to default values
                if (optionsAreRequired(options, showSelectedOnly)) {
                    options.addAll(defaultOptions.keySet());
                }
            // Default values are not found in values list
            // This control is mandatory, first value should be selected if offset is null or 0.
            } else if (inputControl.isMandatory() &&
                    !isNothingSelected &&
                    firstValidValue != null &&
                    offset == 0) {
                firstValidValue.getKey().setSelected(true);
                selectedValues.add(firstValidValue.getValue());

                // If we don't have selected, options and default values - set options to be the first valid option
                if (optionsAreRequired(options, showSelectedOnly)) {
                    options.add(firstValidValue.getKey());
                }
            }
        }

        // update incoming parameters map with selected values
        if (selectedValues.isEmpty()) {
            if (inputControl.isMandatory()){
                doMandatoryValidation(null, state);// mandatory error should appears in this case
            }

            // if control had some value but this value isn't in options (i.e. no such value in result set), then applyNothingSelected() should be called here too.
            applyNothingSelected(controlName, parameters);
            if (nothingOption != null) {
                nothingOption.setSelected(true);

                // If we don't have options - set options to contains selected "nothing" option
                if (optionsAreRequired(options, showSelectedOnly)) {
                    options.add(nothingOption);
                }
            }

            internalApplyNothingSubstitution(controlName, parameters);
        } else {
            // selected values are exist, update incoming parameters
            parameters.put(controlName, getConcreteSelectedValue(selectedValues));
        }

        state.setOptions(options);
        setStateTotalCount(state, parameters);
    }

    private boolean optionsAreRequired(List<InputControlOption> options, boolean showSelectedOnly) {
        return showSelectedOnly && options.isEmpty();
    }

    private boolean isNothingInputControlOption(InputControlOption option) {
        return option != null
                && isNothingValue(option.getValue())
                && isNothingLabel(option.getLabel());
    }

    private boolean isShowSelectedOnlyValues(Map<String, Object> parameters) {
        return parameters.containsKey(SELECTED_ONLY_INTERNAL) &&
                Boolean.parseBoolean((String) parameters.get(SELECTED_ONLY_INTERNAL));
    }

    private InputControlOption getOrBuildInputControlOption(InputControl inputControl,
                                                            ReportInputControlInformation info,
                                                            ListOfValuesItem currentItem,
                                                            Object currentItemValue,
                                                            InputControlOption option) throws CascadeResourceNotFoundException {
        if (option != null) return option;
        return buildInputControlOption(currentItem.getLabel(), dataConverterService.formatSingleValue(currentItemValue, inputControl, info));
    }

    protected SelectedValuesDict createSelectedValuesDict(Object incomingValue) {
        SelectedValuesDict incomingValueStruct = new SelectedValuesDict();
        incomingValueStruct.initSelectedValuesSet();
        incomingValueStruct.addValues(incomingValue, this instanceof MultiSelectListInputControlHandler);
        return incomingValueStruct;
    }

    protected boolean isAllValues(InputControl inputControl, Map<String, Object> parameters) {
        Object selectedValue = parameters.get(inputControl.getName()+"_"+SELECT);
        return  (selectedValue != null && ((String)selectedValue).equalsIgnoreCase(ALL_VALUES));
    }

    protected void setStateTotalCount(InputControlState state, Map<String, Object> parameters) {
        if (parameters.get(TOTAL_COUNT) != null) {
                state.setTotalCount(parameters.get(TOTAL_COUNT).toString());
        }
    }

    protected Object setIncomingValue(String controlName, Map<String, Object> parameters, ReportInputControlInformation info) {
        Object incomingValue = parameters.get(controlName);
        Object selectedValue = parameters.get(controlName + "_" + SELECT);
        boolean isSelectDefaultValues = (selectedValue != null && ((String)selectedValue).equalsIgnoreCase(SELECTED_VALUES));
        boolean isIncomingValueEmpty = isNothingValue(incomingValue);

        if( isSelectDefaultValues || isIncomingValueEmpty) {
            incomingValue = info.getDefaultValue();
        }

        return incomingValue;
    }

    protected Object getCurrentItemValue(InputControl inputControl, ReportInputControlInformation info, ListOfValuesItem currentItem) throws CascadeResourceNotFoundException {
        Object currentItemValue = currentItem.getValue();
        if (currentItemValue instanceof String) {
            // if incoming value is a String, then convert it to corresponding Object for correct further comparison
            try {
                currentItemValue = dataConverterService.convertSingleValue((String) currentItemValue, inputControl, info);
            } catch (InputControlValidationException e) {
                // this exception doesn't depend on user's input. So, throw technical runtime exception
                throw new IllegalStateException(e.getValidationError().getDefaultMessage());
            }
        }
        return currentItemValue;
    }

    protected List<ListOfValuesItem> getListOfValuesItems(InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, ReportInputControlInformation info, Map<String, Class<?>> parameterTypes) throws CascadeResourceNotFoundException {
        List<ListOfValuesItem> values;
        boolean isSingleSelect = !(this instanceof MultiSelectListInputControlHandler);
        if (DiagnosticSnapshotPropertyHelper.isDiagSnapshotSet(parameters)) {
            //  If datasnapshot is available then default values for IC
            // will be loaded from datasnapshot parameters, this logic is counting on that.
            String criteria = loader.getCriteria(inputControl, parameters);
            values = generateValuesFromDefaultValues(info, criteria);
            inputControl.setReadOnly(true);
        } else {
            values = loader.loadValues(inputControl, dataSource, parameters, parameterTypes, info, isSingleSelect);
        }
        return values;
    }


    protected List<ListOfValuesItem> generateValuesFromDefaultValues(ReportInputControlInformation info, String criteria) {
        List<ListOfValuesItem> result = new ArrayList<>();
        List<Object> mid = getDefaultValuesList(info);

        Object value;
        String formattedValue;
        for (int i = 0; i < mid.size(); i++) {
            value = mid.get(i);
            if (null != value) {
                formattedValue = value.toString();
            } else {
                // just for tolerance ...
                formattedValue = "Null";
            }
            ListOfValuesItem item = new ListOfValuesItemImpl();
            item.setLabel(formattedValue);
            item.setValue(formattedValue);

            /**
             * Filter the results based on the totalLimit
             * and filter by search criteria when provided.
             */
            result = loader.checkCriteriaAndAddItem(criteria, result, item);
        }

        return result;
    }

    protected List<Object> getDefaultValuesList(ReportInputControlInformation info) {
        List<Object> mid;
        if (info.getDefaultValue() instanceof Collection<?>) {
            mid =  ((ListOrderedSet)info.getDefaultValue()).asList();
        } else {
            mid = Arrays.asList(new Object[] {info.getDefaultValue()});
        }
        return mid;
    }


    protected boolean shouldAddNothinSelectedOption(InputControl inputControl) {
        return !inputControl.isMandatory();
    }

    protected boolean isNothingSelected(String inputControlName, Map<String, Object> incomingParameters) {
        return !incomingParameters.containsKey(inputControlName);
    }

    /**
     * @param selectedValues list of selected values
     * @return first item of selected values list (singleselect)
     */
    protected Object getConcreteSelectedValue(List<Object> selectedValues) {
        return selectedValues != null && !selectedValues.isEmpty() ? selectedValues.get(0) : null;
    }

    /**
     * Check if given value is selected
     *
     * @param valueToCheck value to check
     * @param selected     selected value
     * @return true if both values are null or equals
     */
    @Deprecated
    protected boolean matches(Object valueToCheck, Object selected) {

        // areBothNulls
        if (valueToCheck == null && selected == null) {
            return true;
        }

        boolean isEqualityByStringFormats = false;
        if (selected != null) {

            // isDirectEquality
            if (selected.equals(valueToCheck)){
                return true;
            }

            try {
                if (valueToCheck != null) {

                    // isWorkaroudForNumbers
                    if (selected instanceof Number
                            && valueToCheck instanceof Number
                            && toBigDecimal((Number) valueToCheck).compareTo(toBigDecimal((Number) selected)) == 0
                            ) {
                        return true;
                    }

                    // isEqualityByStringFormats
                    String valueToCheckInStringFormat = dataConverterService.formatSingleValue(valueToCheck, null, (Class) null);
                    String selectedInStringFormat = dataConverterService.formatSingleValue(selected, null, (Class) null);
                    if (valueToCheckInStringFormat != null && selectedInStringFormat != null) {
                        isEqualityByStringFormats = selectedInStringFormat.equals(valueToCheckInStringFormat);
                    }
                }
            } catch (IllegalStateException e){
                log.warn(MessageFormat.format(
                        "Can't convert incoming value type '{0}'  or  selected value type '{1}' to string format. Add converters to register",
                        valueToCheck != null ? valueToCheck.getClass() : valueToCheck,
                        selected != null ? selected.getClass() : selected)
                );
            }
        }
        return isEqualityByStringFormats;
    }

    protected InputControlOption buildInputControlOption(String label, String value) {
        return new InputControlOption(value, label);
    }

    protected void setDataConverterService(DataConverterService dataConverterService) {
        this.dataConverterService = dataConverterService;
    }

     // This class is to save the default selected values as dictionary and
     // also this can match those values with the parameter values
     protected class SelectedValuesDict {
        HashSet<Object> objectSet;
        TreeSet<BigDecimal> bigDecimalSet;
        HashSet<String> stringSet;

        protected void initSelectedValuesSet() {
            objectSet = new HashSet<>();
            bigDecimalSet = new TreeSet<>();
            stringSet = new HashSet<>();
        }

        protected void clearSet() {
            objectSet.clear();
            bigDecimalSet.clear();
            stringSet.clear();
        }

        protected void addValues(Object defaultValue, boolean isMultiSelect) {

            if(defaultValue instanceof Collection<?>) {
                Collection<?> selectedValues = (Collection<?>) defaultValue;
                for (Object currentSelectedValue : selectedValues){
                    addValueToSet(preprocessValue(currentSelectedValue));
                }
            } else if(!isMultiSelect) {
                addValueToSet(defaultValue);
            }
        }

        protected void addValueToSet(Object value) {
            try{
                if (value == null) {
                    // do nothing
                } else if (value instanceof Number) {
                    bigDecimalSet.add(toBigDecimal((Number) value));
                } else {
                    String valueInStringFormat = dataConverterService.formatSingleValue(value, null, (Class) null);
                    stringSet.add(valueInStringFormat);
                }

                //add everything in Object set
                objectSet.add(value);
            } catch(IllegalStateException e) {
                log.warn(MessageFormat.format(
                        "Can't convert incoming value type '{0}' to String/BigDecimal format. Add converters to register",
                        value != null ? value.getClass() : value)
                );
            }
        }

        protected boolean checkMatch(Object valueToCheck) {

            if(objectSet.contains(valueToCheck)) {
                return true;
            }

            if(valueToCheck == null) {
                return false;
            }


            try {
                // Workaround For Numbers
                if(valueToCheck instanceof Number && bigDecimalSet.contains(toBigDecimal((Number) valueToCheck))) {
                    return true;
                }

                // check equality by StringFormats
                String valueInStringFormat = dataConverterService.formatSingleValue(valueToCheck, null, (Class) null);
                if(valueInStringFormat != null && stringSet.contains(valueInStringFormat)) {
                    return true;
                }
            } catch(IllegalStateException e) {
                log.warn(MessageFormat.format(
                        "Can't convert incoming value type '{0}' to String/BigDecimal format. Add converters to register",
                        valueToCheck != null ? valueToCheck.getClass() : valueToCheck)
                );
            }

            //when no matching value in default values / incoming values dictionary return false
            return false;
        }
    }

    /**
     * To remove the delimiter and to address null values in the multi-select value
     * @param value
     * @return Object
     */
    protected Object preprocessValue(Object value) {
        if(value instanceof String) {
            if(value.equals("null")) {
                value = null;
            } else {
                value = ((String) value).replace("\\,",",");
            }
        }
        return value;
    }

    private boolean isNothingValue(Object value) {
        return NOTHING_SUBSTITUTION_VALUE.equals(value);
    }

    private boolean isNothingLabel(Object label) {
        return NOTHING_SUBSTITUTION_LABEL.equals(label);
    }

}
