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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NOTHING_SUBSTITUTION_LABEL;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.TOTAL_COUNT;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.CRITERIA;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.INCLUDE_TOTAL_COUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ListOvValuesLoaderTest {

    private static final String INPUT_CONTROL_NAME = "inputControlName";

    @Spy
    @InjectMocks
    private ListOvValuesLoader valuesLoader;
    @Mock
    private CachedRepositoryService cachedRepositoryService;

    private final InputControl inputControl = mock(InputControl.class);

    private final ResourceReference dataSource = mock(ResourceReference.class);

    private final ReportInputControlInformation info = mock(ReportInputControlInformation.class);

    private final Map<String, Object> parameters = new HashMap<>();

    private final Map<String, Class<?>> parameterTypes = new HashMap<>();

    @Before
    public void setUp() throws CascadeResourceNotFoundException {
        doReturn(INPUT_CONTROL_NAME).when(inputControl).getName();
    }

    @Test
    public void loadValues_withDataIncludeTotalCount_returnsValuesWithNothingLabel() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);

        List<ListOfValuesItem> values = Arrays.asList(
                createListOfValuesItem("USA", "USA"),
                createListOfValuesItem("Canada", "Canada")
        );

        mockGetListOfValues(values);

        List<ListOfValuesItem> result = valuesLoader.loadValues(
                inputControl, dataSource, parameters, parameterTypes, info, true
        );

        assertEquals(3, result.size());
        assertEquals(NOTHING_SUBSTITUTION_LABEL, result.get(0).getLabel());
        assertEquals("USA", result.get(1).getLabel());
        assertEquals("Canada", result.get(2).getLabel());
        assertEquals(3, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_multiSelectWithDataIncludeTotalCount_returnsValuesWithoutNothingLabel() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);

        List<ListOfValuesItem> values = Arrays.asList(
                createListOfValuesItem("USA", "USA"),
                createListOfValuesItem("Canada", "Canada")
        );

        mockGetListOfValues(values);

        List<ListOfValuesItem> result = valuesLoader.loadValues(
                inputControl, dataSource, parameters, parameterTypes, info, false
        );

        assertEquals(2, result.size());
        assertEquals("USA", result.get(0).getLabel());
        assertEquals("Canada", result.get(1).getLabel());
        assertEquals(2, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_withDataWithoutTotalCount_returnsValuesWithNothingLabel() throws CascadeResourceNotFoundException {
        mockGetListOfValues(Collections.singletonList(
                createListOfValuesItem("USA", "USA")
        ));

        List<ListOfValuesItem> result = valuesLoader.loadValues(
                inputControl, dataSource, parameters, parameterTypes, info, true
        );

        assertEquals(2, result.size());
        assertNull(parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_withDataAndCriteria_returnsFilteredValuesWithoutNothingLabel() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);
        setCriteria(parameters, "S");

        List<ListOfValuesItem> values = Arrays.asList(
                createListOfValuesItem("USA", "USA"),
                createListOfValuesItem("Canada", "Canada")
        );

        mockGetListOfValues(values);

        List<ListOfValuesItem> result = valuesLoader.loadValues(
                inputControl, dataSource, parameters, parameterTypes, info, true
        );

        assertEquals(1, result.size());
        assertEquals(1, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_withDataAndCriteria_returnsFilteredValuesWithNothingLabel() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);
        setCriteria(parameters, "-");

        List<ListOfValuesItem> values = Arrays.asList(
                createListOfValuesItem("-USA-", "USA"),
                createListOfValuesItem("Canada", "Canada")
        );

        mockGetListOfValues(values);

        List<ListOfValuesItem> result = valuesLoader.loadValues(
                inputControl, dataSource, parameters, parameterTypes, info, true
        );

        assertEquals(2, result.size());
        assertEquals(NOTHING_SUBSTITUTION_LABEL, result.get(0).getLabel());
        assertEquals("-USA-", result.get(1).getLabel());
        assertEquals(2, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void filterListOfValuesItems_withLimitOfOneValue() {
        InputControl inputControl = mock(InputControl.class);
        ReportInputControlInformation info = mock(ReportInputControlInformation.class);

        List<ListOfValuesItem> values = new ArrayList<>();
        ListOfValuesItem listOfValuesItem1 = new ListOfValuesItemImpl();
        listOfValuesItem1.setLabel("USA");
        listOfValuesItem1.setValue("USA");
        values.add(listOfValuesItem1);
        ListOfValuesItem listOfValuesItem2 = new ListOfValuesItemImpl();
        listOfValuesItem2.setLabel("Canada");
        listOfValuesItem2.setValue("Canada");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Country", "USA");

        List<ListOfValuesItem> actualResult = valuesLoader.filterListOfValuesItems(values, parameters, inputControl);
        assertEquals(actualResult.size(), 1);
        assertEquals(actualResult.get(0).getValue(), "USA");
    }

    private void mockGetListOfValues(List<ListOfValuesItem> listOfValuesItems) throws CascadeResourceNotFoundException {
        ListOfValues listOfValues = mock(ListOfValues.class);
        doReturn(listOfValuesItems.toArray(new ListOfValuesItem[0])).when(listOfValues).getValues();
        doReturn(listOfValues).when(cachedRepositoryService).getResource(eq(ListOfValues.class), nullable(ResourceReference.class));
    }

    private ListOfValuesItem createListOfValuesItem(String label, String value) {
        ListOfValuesItem item = new ListOfValuesItemImpl();
        item.setLabel(label);
        item.setValue(value);
        return item;
    }

    private void setIncludeTotalCount(Map<String, Object> parameters, boolean include) {
        parameters.put(INCLUDE_TOTAL_COUNT, String.valueOf(include));
    }

    private void setCriteria(Map<String, Object> parameters, String criteria) {
        parameters.put(INPUT_CONTROL_NAME + "_" + CRITERIA, criteria);
    }

}
