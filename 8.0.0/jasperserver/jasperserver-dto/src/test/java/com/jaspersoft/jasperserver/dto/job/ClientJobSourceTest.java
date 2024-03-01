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

package com.jaspersoft.jasperserver.dto.job;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobSourceTest extends BaseDTOPresentableTest<ClientJobSource> {

    private static final String TEST_REPORT_UNIT_URI = "TEST_REPORT_UNIT_URI";
    private static final String TEST_REPORT_UNIT_URI_1 = "TEST_REPORT_UNIT_URI_1";

    private static final Map<String, String[]> TEST_PARAMETERS = createTestParameters("KEY", "VALUE");
    private static final Map<String, String[]> TEST_PARAMETERS_1 = createTestParameters("KEY_1", "VALUE_1");
    private static final Map<String, String[]> TEST_PARAMETERS_2 = createTestParameters("KEY", "VALUE_1");

    private static final Integer TEST_REFERENCE_HEIGHT = 1;
    private static final Integer TEST_REFERENCE_HEIGHT_1 = 11;

    private static final Integer TEST_REFERENCE_WIDTH = 1;
    private static final Integer TEST_REFERENCE_WIDTH_1 = 11;

    private static Map<String, String[]> createTestParameters(String key, String value) {
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put(key, new String[]{value});
        return map;
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobSource expected, ClientJobSource actual) {
        assertNotSame(expected.getParameters(), actual.getParameters());
    }

    @Test
    public void testGetters() {
        ClientJobSource fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getReportUnitURI() ,TEST_REPORT_UNIT_URI);
        assertEquals(fullyConfiguredInstance.getParameters() ,TEST_PARAMETERS);
        assertEquals(fullyConfiguredInstance.getReferenceHeight() ,TEST_REFERENCE_HEIGHT);
        assertEquals(fullyConfiguredInstance.getReferenceWidth() ,TEST_REFERENCE_WIDTH);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobSource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(

                createFullyConfiguredInstance().setParameters(new HashMap<String, String[]>()),
                createFullyConfiguredInstance().setParameters(TEST_PARAMETERS_2),

                // flags on
                createFullyConfiguredInstance().setReportUnitURI(TEST_REPORT_UNIT_URI_1),
                createFullyConfiguredInstance().setParameters(TEST_PARAMETERS_1),
                createFullyConfiguredInstance().setReferenceHeight(TEST_REFERENCE_HEIGHT_1),
                createFullyConfiguredInstance().setReferenceWidth(TEST_REFERENCE_WIDTH_1),
                // null values
                createFullyConfiguredInstance().setReportUnitURI(null),
                createFullyConfiguredInstance().setParameters(null),
                createFullyConfiguredInstance().setReferenceHeight(null),
                createFullyConfiguredInstance().setReferenceWidth(null)
        );
    }

    @Override
    protected ClientJobSource createFullyConfiguredInstance() {
        return new ClientJobSource()
                .setReportUnitURI(TEST_REPORT_UNIT_URI)
                .setParameters(TEST_PARAMETERS)
                .setReferenceHeight(TEST_REFERENCE_HEIGHT)
                .setReferenceWidth(TEST_REFERENCE_WIDTH);
    }

    @Override
    protected ClientJobSource createInstanceWithDefaultParameters() {
        return new ClientJobSource();
    }

    @Override
    protected ClientJobSource createInstanceFromOther(ClientJobSource other) {
        return new ClientJobSource(other);
    }
}
