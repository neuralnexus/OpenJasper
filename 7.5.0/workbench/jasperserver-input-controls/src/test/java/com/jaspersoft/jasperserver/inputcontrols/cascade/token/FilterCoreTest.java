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

package com.jaspersoft.jasperserver.inputcontrols.cascade.token;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.BuiltInParameterProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRQueryExecuterAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.set;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author Anton Fomin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterCoreTest {

    @InjectMocks
    private FilterCore filterCore;

    @Test
    public void getCacheKeyList() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Country", new ArrayList<String>(Arrays.asList("USA", "Mexico", "Spain", "Andorra", "Ukraine")));
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}", parameters);

        assertEquals("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}; {Country=[\"USA\", \"Mexico\", \"Spain\", \"Andorra\", \"Ukraine\"], State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeyEmptyList() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Country", new ArrayList<String>());
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}", parameters);

        assertEquals("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}; {Country=[], State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));

    }

    @Test
    public void getCacheKeyEmptyStringInList() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Country", new ArrayList<String>(Arrays.asList("")));
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}", parameters);

        assertEquals("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}; {Country=[\"\"], State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeyNullInList() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        Collection<Object> country = new ArrayList<Object>();
        country.add(null);
        parameters.put("Country", country);
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}", parameters);

        assertEquals("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}; {Country=[null], State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeyNullStringInList() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        Collection<Object> country = new ArrayList<Object>();
        country.add("null");
        parameters.put("Country", country);
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}", parameters);

        assertEquals("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}; {Country=[\"null\"], State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeySpaceStringInList() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Country", new ArrayList<String>(Arrays.asList(" ")));
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}", parameters);

        assertEquals("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}; {Country=[\" \"], State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeySpaceAndEmptyStringInList() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Country", new ArrayList<String>(Arrays.asList(" ", "")));
        parameters.put("State", "");
        parameters.put("Name", " ");

        Object key = filterCore.getCacheKey("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}", parameters);

        assertEquals("select * from ololo where $X{IN, country_col, Country} and state_col = $P{State} and $X{EQUAL, name_col, Name}; {Country=[\" \", \"\"], State=\"\", Name=\" \"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeyStandardAndIncludeParameters() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Country", "Test");
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from test where $P!{Country} and state_col = $P{State} and $P!{Name}", parameters);

        assertEquals("select * from test where $P!{Country} and state_col = $P{State} and $P!{Name}; {Country=\"Test\", State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeyIncludeAndDynamicParameters() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Country", "Test");
        parameters.put("State", "CA");
        parameters.put("Name", "Vasya");

        Object key = filterCore.getCacheKey("select * from test where $P!{Country} and state_col = $X{EQUAL, state, State} and $P!{Name}", parameters);

        assertEquals("select * from test where $P!{Country} and state_col = $X{EQUAL, state, State} and $P!{Name}; {Country=\"Test\", State=\"CA\", Name=\"Vasya\"}",
                String.valueOf(key));
    }

    @Test
    public void getCacheKeyBetweenDynamicParameter() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Start", 1);
        parameters.put("End", 5);

        Object key = filterCore.getCacheKey("select * from test where $X{[BETWEEN], name, Start, End}", parameters);

        assertEquals("select * from test where $X{[BETWEEN], name, Start, End}; {Start=\"1\", End=\"5\"}", String.valueOf(key));
    }

    @Test
    public void getCacheKeyBetweenAndEscapedParameter() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("Start", 1);
        parameters.put("End", 5);

        Object key = filterCore.getCacheKey("select * from test where $X{[BETWEEN], name, Start, End} and $$X{IN, col, param}", parameters);

        assertEquals("select * from test where $X{[BETWEEN], name, Start, End} and $$X{IN, col, param}; {Start=\"1\", End=\"5\"}", String.valueOf(key));
    }

    @Test
    public void getCacheKeyOnlyParameter() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("test", 1);

        Object key = filterCore.getCacheKey("$P{test}", parameters);

        assertEquals("$P{test}; {test=\"1\"}", String.valueOf(key));
    }

    @Test
    public void getCacheKeyManyParameters() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("param1", 1);
        parameters.put("param2", 2);
        parameters.put("param3", new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4)));
        parameters.put("param4", 4);

        Object key = filterCore.getCacheKey("$X{SOME_FUNC, col, param1, param2, param3, param1, param4}", parameters);

        assertEquals("$X{SOME_FUNC, col, param1, param2, param3, param1, param4}; {param1=\"1\", param2=\"2\", " +
                "param3=[\"1\", \"2\", \"3\", \"4\"], param4=\"4\"}", String.valueOf(key));
    }

    @Test
    public void getCacheKeyExcessParameters() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("param1", 1);
        parameters.put("param2", 2);     // this parameter is not used in the query. Should not be present in cache key.
        parameters.put("param3", 3);

        Object key = filterCore.getCacheKey("$X{SOME_FUNC, col, param1, param3, param3, param1}", parameters);

        assertEquals("$X{SOME_FUNC, col, param1, param3, param3, param1}; {param1=\"1\", " + "param3=\"3\"}", String.valueOf(key));
    }

    @Test
    public void getCacheKeyMissingParameters() {

        setUpBuiltInParameterProvider();

        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("param1", 1);
        parameters.put("param3", 3);

        Object key = filterCore.getCacheKey("$X{SOME_FUNC, col, param1, param3, param4, param1}", parameters);

        assertNull(key);
    }

    @Test
    public void getCacheKeyWithoutParameters() {
        String query = "select * from something";

        Object key = filterCore.getCacheKey(query, new LinkedHashMap<String, Object>());

        assertEquals(query, key);
    }

    @Test
    public void getCacheKeyQueryNull() {
        Object key = filterCore.getCacheKey(null, new LinkedHashMap<String, Object>());

        assertNull(key);
    }

    @Test
    public void getCacheKeyQueryEmptyString() {
        Object key = filterCore.getCacheKey("", new LinkedHashMap<String, Object>());

        assertNull(key);
    }

    @Test
    public void getCacheKeyParametersEmpty() {
        setUpBuiltInParameterProvider();

        Object key = filterCore.getCacheKey("$P{test}", new LinkedHashMap<String, Object>());

        assertNull(key);
    }

    @Test
    public void getCacheKeyParametersNull() {
        setUpBuiltInParameterProvider();

        Object key = filterCore.getCacheKey("$P{test}", null);

        assertNull(key);
    }

    @Test
    public void getCacheKeyNullNull() {
        setUpBuiltInParameterProvider();

        Object key = filterCore.getCacheKey(null, null);

        assertNull(key);
    }

    /**
     * Checks the correct CacheKey generation when using built-in parameters.
     * Test case: query contains multiple parameters (three) including  two built-in ones, one built-in parameter is
     * also included to provided parameters with null value (should be rewritten with a real value)
     */
    @Test
    public void getCacheKeyBuildInParameters() {

        // set up BuiltInParameterProvider
        Map<String,Object> builtInParameters = new HashMap<String, Object>();
        builtInParameters.put("LoggedInUsername","jasperadmin");
        builtInParameters.put("LoggedInUserFullname","JasperAdmin");
        setUpBuiltInParameterProvider(builtInParameters);

        String query = "select fullname from jiuser where username=$P{LoggedInUsername} and fullname=$P{LoggedInUserFullname} and enabled=$P{active}";

        // prepare the provided parameters
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("LoggedInUsername", null);   // This parameter is expected to be rewritten with a real value
        parameters.put("active", true);             // This parameter covers more complex case of query

        Object key = filterCore.getCacheKey(query, parameters);

        assertEquals(query + "; {LoggedInUsername=\"jasperadmin\", LoggedInUserFullname=\"JasperAdmin\", active=\"true\"}", String.valueOf(key));
    }


    /**
     * Checks the correct CacheKey generation when using different key- and value columns of the same query.
     * Test case: query contains value column.
     */
    @Test
    public void getCacheKeyValueColumn() {

        String query = "select country,state from accounts";

        Object key = filterCore.getCacheKey(query, new HashMap(), "state", new String[0]);

        assertEquals(query + "; state; ", String.valueOf(key));
    }


    /**
     * Checks the correct CacheKey generation when using different key- and value columns of the same query.
     * Test case: query contains value column and a few visible columns.
     */
    @Test
    public void getCacheKeyValueAndVisibleColumns() {

        String query = "select country,state from accounts";

        Object key = filterCore.getCacheKey(query, new HashMap(), "state", new String[]{"country", "state"});

        assertEquals(query + "; state; country;state", String.valueOf(key));
    }


    private void setUpBuiltInParameterProvider() {
        Map<String,Object> parameters = new HashMap<String, Object>();
        parameters.put("",new ArrayList<String>());
        setUpBuiltInParameterProvider(parameters);
    }

    private void setUpBuiltInParameterProvider(Map<String,Object> parameters) {
        BuiltInParameterProvider builtInParameterProvider = mock(BuiltInParameterProvider.class);

        for (Iterator<String> iterator = parameters.keySet().iterator(); iterator.hasNext(); ) {
            String parameterName =  iterator.next();
            Object[] result = new Object[] {JRQueryExecuterAdapter.makeParameter(parameterName, Collection.class), parameters.get(parameterName)};
            doReturn(result).when(builtInParameterProvider).getParameter(
                    nullable(ExecutionContext.class), nullable(List.class), nullable(Map.class), eq(parameterName)
            );
        }

        filterCore.builtInParameterProviders = new ArrayList();
        filterCore.builtInParameterProviders.add(builtInParameterProvider);
    }

    @Test
    public void resolveCascadingOrder_StraightRightOrderWithEmptySet() {
        Map<String, Set<String>> inputMap = new LinkedHashMap<String, java.util.Set<String>>();
        inputMap.put("A", new HashSet<String>());
        inputMap.put("B", set("A"));
        inputMap.put("C", set("B"));

        LinkedHashSet<String> orderedNames = filterCore.resolveCascadingOrder(inputMap);

        assertEquals(set("A", "B", "C"), orderedNames);
    }

    @Test
    public void resolveCascadingOrder_StraightWrongOrderWithNull() {
        Map<String, Set<String>> inputMap = new LinkedHashMap<String, Set<String>>();
        inputMap.put("C", set("B"));
        inputMap.put("B", set("A"));
        inputMap.put("A", null);

        LinkedHashSet<String> orderedNames = filterCore.resolveCascadingOrder(inputMap);

        assertThat(orderedNames, contains("A", "B", "C"));
    }

    @Test(expected = JSException.class)
    public void resolveCascadingOrder_CircularWrongOrderWithNull() {
        Map<String, Set<String>> inputMap = new LinkedHashMap<String, Set<String>>();
        inputMap.put("B", set("A", "C"));
        inputMap.put("C", set("B", "A"));
        inputMap.put("A", null);

        filterCore.resolveCascadingOrder(inputMap);
    }

    @Test
    public void resolveCascadingOrder_ComplicatedDependencies() {
        Map<String, Set<String>> inputMap = new LinkedHashMap<String, Set<String>>();
        inputMap.put("A", null);
        inputMap.put("B", set("A", "F"));
        inputMap.put("C", set("A", "B"));
        inputMap.put("D", set("A", "B", "C"));
        inputMap.put("E", set("C", "F"));
        inputMap.put("F", null);

        LinkedHashSet<String> orderedNames = filterCore.resolveCascadingOrder(inputMap);

        assertThat(orderedNames, contains("F", "A", "B", "C", "D", "E"));
    }

    @Test(expected = JSException.class)
    public void resolveCascadingOrder_CircularComplicatedDependencies() {
        Map<String, Set<String>> inputMap = new LinkedHashMap<String, Set<String>>();
        inputMap.put("A", set("E"));
        inputMap.put("B", set("A", "F"));
        inputMap.put("C", set("A", "B"));
        inputMap.put("D", set("A", "B", "C"));
        inputMap.put("E", set("C", "F"));
        inputMap.put("F", null);

        filterCore.resolveCascadingOrder(inputMap);
    }
}
