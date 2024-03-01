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
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.token.FilterResolver;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NOTHING_SUBSTITUTION_LABEL;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NOTHING_SUBSTITUTION_VALUE;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.TOTAL_COUNT;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.CRITERIA;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.INCLUDE_TOTAL_COUNT;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.SKIP_FETCHING_IC_VALUES_FROM_DB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class QueryValuesLoaderTest {

    private static final String INPUT_CONTROL_NAME = "inputControlName";

    @Spy
    @InjectMocks
    private QueryValuesLoader valuesLoader;
    @Mock
    private AuditContext concreteAuditContext;
    @Mock
    private CachedRepositoryService cachedRepositoryService;
    @Mock
    private FilterResolver filterResolver;
    @Mock
    private ValuesLoaderStrategy queryValuesLoaderStrategy;
    @Mock
    private ValuesLoaderStrategy parametersValuesLoaderStrategy;

    private final InputControl inputControl = mock(InputControl.class);

    private final ResourceReference dataSource = mock(ResourceReference.class);

    private final ReportInputControlInformation info = mock(ReportInputControlInformation.class);

    private final Map<String, Object> parameters = new HashMap<>();

    private final Map<String, Class<?>> parameterTypes = new HashMap<>();

    @Before
    public void setUp() throws CascadeResourceNotFoundException {
        doReturn(INPUT_CONTROL_NAME).when(inputControl).getName();
        doReturn("").when(valuesLoader).extractLabelFromResults(
                any(InputControl.class),
                any(ReportInputControlInformation.class),
                any(Object[].class)
        );
    }

    @Test
    public void getListOfValuesItems_validateTotalLimit() throws CascadeResourceNotFoundException {
        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
        }};

        List<ListOfValuesItem> actualResult = valuesLoader.getListOfValuesItems(inputControl, info, null, results);
        assertEquals(3, actualResult.size());
        assertEquals(actualResult.get(0).getValue(), "USA");
    }

    @Test
    public void getListOfValuesItems_NothingValueShouldBeOnTop() throws CascadeResourceNotFoundException {
        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
            put(NOTHING_SUBSTITUTION_VALUE, new Object[]{NOTHING_SUBSTITUTION_LABEL});
        }};

        List<ListOfValuesItem> actualResult = valuesLoader.getListOfValuesItems(inputControl, info, null, results);
        assertEquals(4, actualResult.size());
        assertEquals(NOTHING_SUBSTITUTION_LABEL, actualResult.get(0).getLabel());
    }

    @Test
    public void loadValues_withDataAndIncludeTotalCount_returnsValuesIncludingNothingLabel() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);

        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
        }};

        mockLoadResults(results);
        mockExtractLabelFromResults("USA", "USA|LABEL");

        List<ListOfValuesItem> actualResult = valuesLoader.loadValues(inputControl, dataSource, parameters, parameterTypes, info, true);
        assertEquals(4, actualResult.size());
        assertEquals(NOTHING_SUBSTITUTION_LABEL, actualResult.get(0).getLabel());
        assertEquals("USA|LABEL", actualResult.get(1).getLabel());
        assertEquals(4, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_withDataAndExcludeTotalCount_returnsValuesWithoutTotalCount() throws CascadeResourceNotFoundException {
        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
        }};

        mockLoadResults(results);

        List<ListOfValuesItem> actualResult = valuesLoader.loadValues(inputControl, dataSource, parameters, parameterTypes, info, true);
        assertEquals(4, actualResult.size());
        assertEquals(NOTHING_SUBSTITUTION_LABEL, actualResult.get(0).getLabel());
        assertNull(parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_multiSelectWithDataAndIncludeTotalCount_returnsValuesWithoutNothingLabel() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);

        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
        }};

        mockLoadResults(results);
        mockExtractLabelFromResults("USA", "USA|LABEL");

        List<ListOfValuesItem> actualResult = valuesLoader.loadValues(inputControl, dataSource, parameters, parameterTypes, info, false);
        assertEquals(3, actualResult.size());
        assertEquals("USA|LABEL", actualResult.get(0).getLabel());
        assertEquals(3, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_withDataAndCriteria_returnsFilteredValuesAndNothingLabelBeingExcluded() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);
        setCriteria(parameters, "A");

        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
        }};

        mockLoadResults(results);
        mockExtractLabelFromResults("USA", "USA|LABEL");
        mockExtractLabelFromResults("Canada", "Canada|LABEL");

        List<ListOfValuesItem> actualResult = valuesLoader.loadValues(inputControl, dataSource, parameters, parameterTypes, info, true);
        assertEquals(2, actualResult.size());
        assertEquals("USA|LABEL", actualResult.get(0).getLabel());
        assertEquals("Canada|LABEL", actualResult.get(1).getLabel());
        assertEquals(2, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_withDataAndCriteria_returnsFilteredValuesAndNothingLabelBeingIncluded() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);
        setCriteria(parameters, "---");

        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
        }};

        mockLoadResults(results);

        mockExtractLabelFromResults(NOTHING_SUBSTITUTION_LABEL, NOTHING_SUBSTITUTION_LABEL);

        List<ListOfValuesItem> actualResult = valuesLoader.loadValues(inputControl, dataSource, parameters, parameterTypes, info, true);
        assertEquals(1, actualResult.size());
        assertEquals(NOTHING_SUBSTITUTION_LABEL, actualResult.get(0).getLabel());
        assertEquals(1, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void loadValues_withSkipCriteriaSearch_unfilteredValues() throws CascadeResourceNotFoundException {
        setIncludeTotalCount(parameters, true);
        setCriteria(parameters, "---");

        OrderedMap results = new LinkedMap() {{
            put("USA", new Object[]{"USA"});
            put("Canada", new Object[]{"Canada"});
            put("Mexico", new Object[]{"Mexico"});
        }};

        mockLoadResults(results, true);
        mockExtractLabelFromResults("USA", "USA|LABEL");

        List<ListOfValuesItem> actualResult = valuesLoader.loadValues(inputControl, dataSource, parameters, parameterTypes, info, true);
        assertEquals(4, actualResult.size());
        assertEquals(NOTHING_SUBSTITUTION_LABEL, actualResult.get(0).getLabel());
        assertEquals("USA|LABEL", actualResult.get(1).getLabel());
        assertEquals(4, parameters.get(TOTAL_COUNT));
    }

    @Test
    public void getLoaderStrategy_parametersWithSkipFetchingAttribute_parametersValuesLoaderStrategy() {
        parameters.put(SKIP_FETCHING_IC_VALUES_FROM_DB, Boolean.TRUE.toString());
        ValuesLoaderStrategy result = valuesLoader.getLoaderStrategy(parameters);
        assertEquals(parametersValuesLoaderStrategy, result);
    }

    @Test
    public void getLoaderStrategy_emptyParameters_queryValuesLoaderStrategy() {
        ValuesLoaderStrategy result = valuesLoader.getLoaderStrategy(parameters);
        assertEquals(queryValuesLoaderStrategy, result);
    }

    private void setIncludeTotalCount(Map<String, Object> parameters, boolean include) {
        parameters.put(INCLUDE_TOTAL_COUNT, String.valueOf(include));
    }

    private void setCriteria(Map<String, Object> parameters, String criteria) {
        parameters.put(INPUT_CONTROL_NAME + "_" + CRITERIA, criteria);
    }

    private void mockLoadResults(OrderedMap orderedMap) throws CascadeResourceNotFoundException {
        mockLoadResults(orderedMap, false);
    }

    private void mockLoadResults(OrderedMap orderedMap, boolean skipCriteriaSearch) throws CascadeResourceNotFoundException {
        ResultsOrderedMap.Builder builder = new ResultsOrderedMap.Builder();
        builder.setOrderedMap(orderedMap);
        builder.setSkipCriteriaSearch(skipCriteriaSearch);

        doReturn(builder.build()).when(queryValuesLoaderStrategy).resultsOrderedMap(
                any(InputControl.class),
                nullable(ResourceReference.class),
                nullable(String.class),
                anyMap(),
                anyMap()
        );
    }

    private void mockExtractLabelFromResults(final String value, final String label) throws CascadeResourceNotFoundException {
        doReturn(label).when(valuesLoader).extractLabelFromResults(
                any(InputControl.class),
                any(ReportInputControlInformation.class),
                argThat(objects -> objects.length > 0 && value.equals(objects[0]))
        );
    }

}
