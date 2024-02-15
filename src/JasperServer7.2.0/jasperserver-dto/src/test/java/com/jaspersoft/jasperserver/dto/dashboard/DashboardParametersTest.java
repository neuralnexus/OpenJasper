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
package com.jaspersoft.jasperserver.dto.dashboard;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.reports.ReportParameter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DashboardParametersTest extends BaseDTOPresentableTest<DashboardParameters> {

    private static final List<ReportParameter> TEST_DASHBOARD_PARAMETERS = Collections.singletonList(
            new ReportParameter().setName("TEST_NAME").setValues(Collections.singletonList("TEST_VALUE"))
    );
    private static final List<ReportParameter> TEST_DASHBOARD_PARAMETERS_1 = Collections.singletonList(
            new ReportParameter().setName("TEST_NAME_1").setValues(Collections.singletonList("TEST_VALUE_1"))
    );
    private static final List<ReportParameter> TEST_DASHBOARD_PARAMETERS_EMPTY = new ArrayList<ReportParameter>();

    @Test
    public void testConstructor() {
        DashboardParameters instance = new DashboardParameters(TEST_DASHBOARD_PARAMETERS);
        assertEquals(instance.getDashboardParameters(), TEST_DASHBOARD_PARAMETERS);
    }

    @Test
    public void testGetRawParameters_full() {
        DashboardParameters instance = createFullyConfiguredInstance();
        Map<String, String[]> expected = new HashMap<String, String[]>();
        expected.put("TEST_NAME", new String[]{"TEST_VALUE"});
        assertTrue(isMapsEquals(expected, instance.getRawParameters()));
    }

    @Test
    public void testGetRawParameters_empty() {
        DashboardParameters instance = createInstanceWithDefaultParameters();
        Map<String, String[]> expected = new HashMap<String, String[]>();
        assertTrue(isMapsEquals(expected, instance.getRawParameters()));
    }

    private boolean isMapsEquals(Map<String, String[]> first, Map<String, String[]> second) {
        if (first == null && second == null) {
            return true;
        }

        if (first != null && second == null || first == null) {
            return false;
        }

        if (first.size() != second.size())
            return false;

        if (!(first.keySet().containsAll(second.keySet())))
            return false;

        for (String key : first.keySet()) {
            String[] firstValues = first.get(key);
            String[] secondValues = second.get(key);
            if (!Arrays.equals(firstValues, secondValues))
                return false;
        }

        return true;
    }

    /*
     * Preparing
     */

    @Override
    protected List<DashboardParameters> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDashboardParameters(TEST_DASHBOARD_PARAMETERS_1),
                createFullyConfiguredInstance().setDashboardParameters(TEST_DASHBOARD_PARAMETERS_EMPTY),
                // null values
                createFullyConfiguredInstance().setDashboardParameters(null)
        );
    }

    @Override
    protected DashboardParameters createFullyConfiguredInstance() {
        return new DashboardParameters()
                .setDashboardParameters(TEST_DASHBOARD_PARAMETERS);
    }

    @Override
    protected DashboardParameters createInstanceWithDefaultParameters() {
        return new DashboardParameters();
    }

    @Override
    protected DashboardParameters createInstanceFromOther(DashboardParameters other) {
        return new DashboardParameters(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(DashboardParameters expected, DashboardParameters actual) {
        assertNotSameCollection(expected.getDashboardParameters(), actual.getDashboardParameters());
    }
}
