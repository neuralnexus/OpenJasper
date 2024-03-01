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
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ValuesLoaderTest {

    private ValuesLoader valuesLoader;

    @Before
    public void setup() {
        valuesLoader = spy(ValuesLoader.class);
    }

    @Test
    public void checkCriteriaAndAddItem_withAnyParameterValue() {
        List<ListOfValuesItem> result = new ArrayList<>();
        ListOfValuesItem item = new ListOfValuesItemImpl();
        item.setLabel("USA");

        valuesLoader.checkCriteriaAndAddItem("USA", result, item);
        assertEquals(result.get(0).getLabel(), "USA");

        result.clear();
        valuesLoader.checkCriteriaAndAddItem("Mexico", result, item);
        assertTrue(result.isEmpty());
    }

    @Test
    public void checkCriteriaAndAddItem_withNullCriteria_addedItem() {
        List<ListOfValuesItem> result = new ArrayList<>();
        ListOfValuesItem item = new ListOfValuesItemImpl();
        item.setLabel("USA");

        valuesLoader.checkCriteriaAndAddItem(null, result, item);

        assertEquals(item.getLabel(), result.get(0).getLabel());
    }

    @Test
    public void getCriteria_withAnyParameterValue() {
        Map<String, Object> parameters = new HashMap<>();
        InputControl inputControl = mock(InputControl.class);
        when(inputControl.getName()).thenReturn("Country");

        doCallRealMethod().when(valuesLoader).getCriteria(any(InputControl.class), any(Map.class));
        assertNull(valuesLoader.getCriteria(inputControl, parameters));

        parameters.put("Country_criteria", "USA");
        assertEquals("USA", valuesLoader.getCriteria(inputControl, parameters));
    }

    @Test
    public void addTotalCountToParameters_withIncludeTotalCount() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("includeTotalCount", "true");

        doCallRealMethod().when(valuesLoader).addTotalCountToParameters(anyMap(), anyInt());
        valuesLoader.addTotalCountToParameters(parameters, 10);
        assertEquals(10, parameters.get("totalCount"));
    }

}
