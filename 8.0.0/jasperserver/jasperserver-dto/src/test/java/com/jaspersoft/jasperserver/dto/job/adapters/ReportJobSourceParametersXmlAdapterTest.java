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

package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.job.wrappers.ClientReportParametersMapWrapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ReportJobSourceParametersXmlAdapterTest {


    private static final String TEST_KEY = "TEST_KEY";
    private static final String[] TEST_VALUE = new String[] {"TEST_VALUE"};
    private static final HashMap<String, String[]> TEST_PARAMETER_VALUES = createTestParameters(TEST_KEY, TEST_VALUE);

    private static HashMap<String, String[]> createTestParameters(String key, String[] value) {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        map.put(key, value);
        return map;
    }

    private ReportJobSourceParametersXmlAdapter objectUnderTest = new ReportJobSourceParametersXmlAdapter();

    @Test
    public void unmarshal_nullValue_nullValue() throws Exception {
        Map<String, String[]> actual = objectUnderTest.unmarshal(null);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithNullParameters_nullValue() throws Exception {
        ClientReportParametersMapWrapper wrapper = createClientReportParametersMapWrapperWithNullParameters();

        Map<String, String[]> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithoutParameters_nullValue() throws Exception {
        ClientReportParametersMapWrapper wrapper = createClientReportParametersMapWrapperWithoutParameters();

        Map<String, String[]> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithSomeParameters_SomeParameters() throws Exception {
        ClientReportParametersMapWrapper wrapper = createClientReportParametersMapWrapperWithParameters();

        Map<String, String[]> actual = objectUnderTest.unmarshal(wrapper);
        assertEquals(TEST_PARAMETER_VALUES, actual);
    }

    @Test
    public void marshal_nullValue_nullValue() throws Exception {
        ClientReportParametersMapWrapper actual = objectUnderTest.marshal(null);
        assertNull(actual);
    }

    @Test
    public void marshal_empty_nullValue() throws Exception {
        ClientReportParametersMapWrapper actual = objectUnderTest.marshal(new HashMap<String, String[]>());
        assertNull(actual);
    }

    @Test
    public void marshal_someParameters_wrapper() throws Exception {
        ClientReportParametersMapWrapper expected = createClientReportParametersMapWrapperWithParameters();
        ClientReportParametersMapWrapper actual = objectUnderTest.marshal(TEST_PARAMETER_VALUES);

        assertEquals(expected, actual);
    }

    /*
     * Helpers
     */

    private ClientReportParametersMapWrapper createClientReportParametersMapWrapperWithParameters() {
        return new ClientReportParametersMapWrapper().setParameterValues(TEST_PARAMETER_VALUES);
    }

    private ClientReportParametersMapWrapper createClientReportParametersMapWrapperWithoutParameters() {
        return new ClientReportParametersMapWrapper().setParameterValues(new HashMap<String, String[]>());
    }

    private ClientReportParametersMapWrapper createClientReportParametersMapWrapperWithNullParameters() {
        return new ClientReportParametersMapWrapper().setParameterValues(null);
    }

}