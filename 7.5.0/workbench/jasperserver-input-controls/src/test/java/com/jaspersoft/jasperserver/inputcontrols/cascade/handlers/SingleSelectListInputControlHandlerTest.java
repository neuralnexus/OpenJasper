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
package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.common.util.diagnostic.DiagnosticSnapshotPropertyHelper;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters.DataConverterService;
import org.apache.commons.collections.set.ListOrderedSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.entry;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.listOfOptions;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.listOfValues;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleSelectListInputControlHandlerTest {
    @InjectMocks
    private SingleSelectListInputControlHandler handler;

    @Mock
    private ValuesLoader loader;

    @Mock
    protected DataConverterService dataConverterService;

    @Mock
    private InputControl inputControl;

    @Mock
    private ReportInputControlInformation reportInputControlInformation;

    /**
     * Incoming value is of type Integer.
     * List of values for input control contains values ot type BigDecimal.
     * Integer value has corresponding BigDecimal equivalent in list of values and corresponding option become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfBigDecimalAndIntegerValue() throws Exception {
        final String parameterName = "testName";
        // incoming value is of type Integer
        final Integer integerValue = Integer.valueOf(2);
        // BigDecimal equivalent of incoming value
        final BigDecimal bigDecimalValue = BigDecimal.valueOf(2);

        doReturn(parameterName).when(inputControl).getName();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, integerValue));

        // building list of values from BigDecimals
        final List<ListOfValuesItem> listOfValues = listOfValues(BigDecimal.valueOf(0), BigDecimal.valueOf(1), bigDecimalValue, BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        doReturn(listOfValues).when(loader).loadValues(eq(inputControl), nullable(ResourceReference.class), eq(inputParameters), eq(parameterTypes), eq(reportInputControlInformation));

        // dummy data conversion
        doReturn(bigDecimalValue.toString()).when(dataConverterService).formatSingleValue(eq(bigDecimalValue), eq(inputControl), eq(reportInputControlInformation));

        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, reportInputControlInformation).getOptions();
        // state should have options
        assertNotNull(options);
        // options number is one item more then values because of nothing selected item.
        assertEquals(listOfValues.size() + 1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
        // selected item is string representation of BigDecimal, which is equivalent to incoming Integer value
        assertEquals(selectedOptions.get(0).getValue(), bigDecimalValue.toString());
    }

    /**
     * Incoming value is of type BigDecimal.
     * List of values for input control contains values ot type Integer.
     * BigDecimal value has corresponding Integer equivalent in list of values and corresponding option become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfIntegerAndBigDecimalValue() throws Exception {
        final String parameterName = "testName";
        // incoming value is of type BigDecimal
        final BigDecimal bigDecimalValue = BigDecimal.valueOf(3);
        // Integer equivalent of incoming value
        final Integer integerValue = 3;

        doReturn(parameterName).when(inputControl).getName();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, bigDecimalValue));
        // building list of values from Integers
        final List<ListOfValuesItem> listOfValues = listOfValues(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), integerValue, Integer.valueOf(4));
        doReturn(listOfValues).when(loader).loadValues(eq(inputControl), nullable(ResourceReference.class), eq(inputParameters), eq(parameterTypes), eq(reportInputControlInformation));
        // dummy data conversion
        doReturn(integerValue.toString()).when(dataConverterService).formatSingleValue(eq(integerValue), eq(inputControl), eq(reportInputControlInformation));
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, reportInputControlInformation).getOptions();
        // state should have options
        assertNotNull(options);
        // options number is one item more then values because of nothing selected item.
        assertEquals(listOfValues.size() + 1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
        // selected item is string representation of Integer, which is equivalent to incoming BigDecimal value
        assertEquals(selectedOptions.get(0).getValue(), integerValue.toString());
    }

    /**
     * Input control options ot type Long.
     * Checks if Numbers matcher works well for Long type. Covers the bugzilla #33225 case.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfLong() throws Exception {
        final String parameterName = "testName";
        // Selected value is of type Long
        final Long selectedValue = Long.valueOf("49539595901085460");

        doReturn(parameterName).when(inputControl).getName();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, selectedValue));
        // building list of values from Longs
        final List<ListOfValuesItem> listOfValues = listOfValues(Long.valueOf("0"),Long.valueOf("49539595901085457"),
                Long.valueOf("49539595901085458"), Long.valueOf("49539595901085459"), selectedValue,  Long.valueOf("49539595901085461"));

        doReturn(listOfValues).when(loader).loadValues(eq(inputControl), nullable(ResourceReference.class), eq(inputParameters), eq(parameterTypes), eq(reportInputControlInformation));

        doAnswer(invocationOnMock ->
                invocationOnMock.getArguments()[0].toString()
        ).when(dataConverterService).formatSingleValue(nullable(Object.class), any(InputControl.class), eq(reportInputControlInformation));

        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, reportInputControlInformation).getOptions();
        // state should have options
        assertNotNull(options);
        // options number is one item more then values because of nothing selected item.
        assertEquals(listOfValues.size() + 1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
        // selected item is string representation of Integer, which is equivalent to incoming BigDecimal value
        assertEquals(selectedOptions.get(0).getValue(), selectedValue.toString());
    }

    /**
     *  diagnostic datasnapshot contains IC default values
     *  chosen by user when run report under diagnostic
     *
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlFromSingleDefaultValueOfDiagnosticDatasnapshot() throws Exception {
        final String parameterName = "testName";
        final String valueForDefault = "Diagnostic";

        final List<InputControlOption> defaultItems = new ArrayList<InputControlOption>() {{
            add(new InputControlOption(valueForDefault, valueForDefault, true));
        }};

        doReturn(parameterName).when(inputControl).getName();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(
                entry(DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT, Boolean.TRUE),
                entry(parameterName, valueForDefault));

        doReturn(valueForDefault).when(reportInputControlInformation).getDefaultValue();

        final List<ListOfValuesItem> listOfValues = listOfValues(
                Integer.valueOf(-2), Integer.valueOf(-1),
                Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));

        doReturn(valueForDefault).when(dataConverterService).formatSingleValue(eq(valueForDefault), eq(inputControl), eq(reportInputControlInformation));
        doReturn(valueForDefault).when(dataConverterService).convertSingleValue(eq(valueForDefault), eq(inputControl), eq(reportInputControlInformation));
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, reportInputControlInformation).getOptions();
        // state should have options
        assertNotNull(options);
        assertFalse("options not same list from db", options.size() == listOfValues.size() + 1);
        assertTrue("option same list of defaults", options.contains(defaultItems.get(0)));

        // options number is one item more then values because of nothing selected item.
        assertEquals(defaultItems.size()+1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
    }

    @Test
    public void getStateOfInputControlFromMultiDefaultValueOfDiagnosticDatasnapshot() throws Exception {
        final String parameterName = "testName";

        final List<String> valuesForDefault = new ArrayList<String>() {{
            add("Diag_01");
            add("Diag_02");
        }};

        final ListOrderedSet defaultICValues = new ListOrderedSet(){{
            addAll(valuesForDefault);
        }};

        final List<InputControlOption> defaultItems = listOfOptions(valuesForDefault, true);

        doReturn(parameterName).when(inputControl).getName();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(
                entry(DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT, Boolean.TRUE),
                entry(parameterName, valuesForDefault.get(1)));

        doReturn(defaultICValues).when(reportInputControlInformation).getDefaultValue();

        final List<ListOfValuesItem> listOfValues = listOfValues(
                Integer.valueOf(-2), Integer.valueOf(-1),
                Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
        lenient().doReturn(listOfValues).when(loader).loadValues(eq(inputControl), nullable(ResourceReference.class), eq(inputParameters), eq(parameterTypes), eq(reportInputControlInformation));

        // dummy data conversion
        doReturn(valuesForDefault.get(0)).when(dataConverterService).formatSingleValue(eq(valuesForDefault.get(0)), eq(inputControl), eq(reportInputControlInformation));
        doReturn(valuesForDefault.get(1)).when(dataConverterService).formatSingleValue(eq(valuesForDefault.get(1)), eq(inputControl), eq(reportInputControlInformation));
        doReturn(valuesForDefault.get(0)).when(dataConverterService).convertSingleValue(eq(valuesForDefault.get(0)), eq(inputControl), eq(reportInputControlInformation));
        doReturn(valuesForDefault.get(1)).when(dataConverterService).convertSingleValue(eq(valuesForDefault.get(1)), eq(inputControl), eq(reportInputControlInformation));
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, reportInputControlInformation).getOptions();
        // state should have options
        assertNotNull(options);
        assertFalse("options not same list from db", options.size() == listOfValues.size() + 1);
        assertTrue("option same list of defaults", options.contains(defaultItems.get(1)));

        // options number is one item more then values because of nothing selected item.
        assertEquals(defaultItems.size()+1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
    }

}
