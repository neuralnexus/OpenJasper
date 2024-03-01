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

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.entry;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.list;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.listOfValues;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MultiSelectListInputControlHandlerTest extends BaseInputControlHandlerTest {

    private static final String INPUT_CONTROL_NAME = "inputControl";

    @InjectMocks
    private MultiSelectListInputControlHandler handler;

    @Before
    public void setUp() throws CascadeResourceNotFoundException {
        values = getListOfValuesItem();
        state = new InputControlState();

        doReturn(INPUT_CONTROL_NAME).when(inputControl).getName();
        doAnswer(invocation -> invocation.getArgument(0)).when(dataConverterService)
                .convertSingleValue(anyString(), any(InputControl.class), any());
        doAnswer(invocation -> invocation.getArgument(0)).when(dataConverterService)
                .formatSingleValue(anyString(), any(InputControl.class), any());
    }

    /**
     * Incoming value is of type Collection<Integer>.
     * List of values for input control contains values ot type BigDecimal.
     * Each Integer value has corresponding BigDecimal equivalent in list of values and corresponding options become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfBigDecimalAndIntegerValues() throws Exception {
        final String parameterName = "testName";
        // incoming values is a Collection of Integer items
        final Collection<Integer> integerValues = list(Integer.valueOf(2), Integer.valueOf(5), Integer.valueOf(7));
        // string representation of integers
        final Collection<String> selectedValuesToCheck = list("2", "5", "7");

        // BigDecimal equivalent of incoming integers
        final BigDecimal bigDecimalValue2 = BigDecimal.valueOf(2);
        final BigDecimal bigDecimalValue5 = BigDecimal.valueOf(5);
        final BigDecimal bigDecimalValue7 = BigDecimal.valueOf(7);

        doReturn(parameterName).when(inputControl).getName();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, integerValues));
        // building list of values from BigDecimals
        final List<ListOfValuesItem> listOfValues = listOfValues(BigDecimal.valueOf(0), BigDecimal.valueOf(1), bigDecimalValue2, BigDecimal.valueOf(3), BigDecimal.valueOf(4), bigDecimalValue5, BigDecimal.valueOf(6), bigDecimalValue7);
        doReturn(listOfValues).when(loader).loadValues(
                eq(inputControl), nullable(ResourceReference.class), eq(inputParameters), eq(parameterTypes), eq(reportInputControlInformation)
                , anyBoolean());
        // dummy data conversion
        doReturn(bigDecimalValue2.toString()).when(dataConverterService).formatSingleValue(eq(bigDecimalValue2), eq(inputControl), eq(reportInputControlInformation));
        doReturn(bigDecimalValue5.toString()).when(dataConverterService).formatSingleValue(eq(bigDecimalValue5), eq(inputControl), eq(reportInputControlInformation));
        doReturn(bigDecimalValue7.toString()).when(dataConverterService).formatSingleValue(eq(bigDecimalValue7), eq(inputControl), eq(reportInputControlInformation));
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, reportInputControlInformation).getOptions();
        // state should have options
        assertNotNull(options);
        // options count equals to listOfValues items count
        assertEquals(listOfValues.size(), options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for(InputControlOption currentOption : options){
            if(currentOption.isSelected()) {
                selectedOptions.add(currentOption);
                // selected option value is one of predefined values to be selected
                assertTrue(selectedValuesToCheck.contains(currentOption.getValue()));
            }
        }
        // selected options count equals to incoming integers count
        assertEquals(selectedOptions.size(), integerValues.size());
    }

    /**
     * Incoming value is of type Collection<BigDecimal>.
     * List of values for input control contains values ot type Integer.
     * Each BigDecimal value has corresponding Integer equivalent in list of values and corresponding options become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfIntegerAndBigDecimalValues() throws Exception {
        final String parameterName = "testName";
        // incoming values is a Collection of BigDecimal items
        final Collection<BigDecimal> bigDecimalValues = list(BigDecimal.valueOf(3), BigDecimal.valueOf(4), BigDecimal.valueOf(6));
        // string representation of integers
        final Collection<String> selectedValuesToCheck = list("3", "4", "6");

        // Integer equivalent of incoming integers
        final Integer integerValue3 = Integer.valueOf(3);
        final Integer integerValue4 = Integer.valueOf(4);
        final Integer integerValue6 = Integer.valueOf(6);

        doReturn(parameterName).when(inputControl).getName();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, bigDecimalValues));

        // building list of values from Integers
        final List<ListOfValuesItem> listOfValues = listOfValues(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), integerValue3, integerValue4, Integer.valueOf(5), integerValue6, Integer.valueOf(7), Integer.valueOf(8));
        doReturn(listOfValues).when(loader).loadValues(eq(inputControl), nullable(ResourceReference.class), eq(inputParameters), eq(parameterTypes), eq(reportInputControlInformation), anyBoolean());
        // dummy data conversion
        doReturn(integerValue3.toString()).when(dataConverterService).formatSingleValue(eq(integerValue3), eq(inputControl), eq(reportInputControlInformation));
        doReturn(integerValue4.toString()).when(dataConverterService).formatSingleValue(eq(integerValue4), eq(inputControl), eq(reportInputControlInformation));
        doReturn(integerValue6.toString()).when(dataConverterService).formatSingleValue(eq(integerValue6), eq(inputControl), eq(reportInputControlInformation));
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, reportInputControlInformation).getOptions();
        // state should have options
        assertNotNull(options);
        // options count equals to listOfValues items count
        assertEquals(listOfValues.size(), options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for(InputControlOption currentOption : options){
            if(currentOption.isSelected()) {
                selectedOptions.add(currentOption);
                // selected option value is one of predefined values to be selected
                assertTrue(selectedValuesToCheck.contains(currentOption.getValue()));
            }
        }
        // selected options count equals to incoming BigDecimals count
        assertEquals(selectedOptions.size(), bigDecimalValues.size());
    }

    @Test
    public void preprocessValue_withDifferentValues() {
        assertNull(handler.preprocessValue("null"));
        assertEquals("something,s",handler.preprocessValue("something\\,s"));
        assertEquals("",handler.preprocessValue(""));
        assertEquals("test\\,s",handler.preprocessValue("test\\\\,s"));
        assertEquals(0,handler.preprocessValue(0));
    }

    @Test
    public void SelectedValuesDict_withSingleValue_shouldFail() {

        Object defaultValue = "String";
        SingleSelectListInputControlHandler.SelectedValuesDict selectedValuesDict =  handler.createSelectedValuesDict(defaultValue);
        assertFalse(selectedValuesDict.checkMatch("String"));
    }

    @Test
    public void SelectedValuesDict_withEmptyCollection_shouldFail() {

        List<Object> defaultValue = Arrays.asList();
        SingleSelectListInputControlHandler.SelectedValuesDict selectedValuesDict =  handler.createSelectedValuesDict(defaultValue);
        assertFalse(selectedValuesDict.checkMatch("String"));
    }

    @Test
    public void selectedOnly_fillStateValue_selectedOptions() throws CascadeResourceNotFoundException{
        Map<String, Object> parameters = new HashMap<>();

        setIncomingValue(parameters, Collections.singletonList("Canada"), inputControl);
        setSelectedOnly(parameters, true);

        mockLoadValues(values);

        handler.fillStateValue(state, inputControl, dataSource, parameters, reportInputControlInformation, Collections.emptyMap());

        assertEquals(1, state.getOptions().size());
        assertEquals("Canada", state.getOptions().get(0).getValue());
    }

    @Test
    public void selectedOnlyWithLimitAndOffset_fillStateValue_allSelectedOptionsIgnoringPagination() throws CascadeResourceNotFoundException{
        Map<String, Object> parameters = new HashMap<>();

        setIncomingValue(parameters, Arrays.asList("USA", "Canada", "Mexico"), inputControl);
        setSelectedOnly(parameters, true);
        setLimit(parameters, 1);
        setOffset(parameters, 1);

        mockLoadValues(values);

        handler.fillStateValue(state, inputControl, dataSource, parameters, reportInputControlInformation, Collections.emptyMap());

        assertEquals(3, state.getOptions().size());
    }

}
