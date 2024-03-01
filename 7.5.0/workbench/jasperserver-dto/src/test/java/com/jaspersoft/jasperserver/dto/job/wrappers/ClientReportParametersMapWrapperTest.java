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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientReportParametersMapWrapperTest extends BaseDTOPresentableTest<ClientReportParametersMapWrapper> {

    private static final String TEST_KEY = "TEST_KEY";
    private static final String[] TEST_VALUE = new String[]{"TEST_VALUE"};
    private static final HashMap<String, String[]> TEST_PARAMETER_VALUES = createTestHashMap(TEST_KEY, TEST_VALUE);

    private static final String TEST_KEY_1 = "TEST_KEY_1";
    private static final String[] TEST_VALUE_1 = new String[]{"TEST_VALUE_1"};
    private static final HashMap<String, String[]> TEST_PARAMETER_VALUES_1 = createTestHashMap(TEST_KEY_1, TEST_VALUE_1);

    private static HashMap<String, String[]> createTestHashMap(String key, String[] value) {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        map.put(key, value);
        return map;
    }

    @Override
    protected List<ClientReportParametersMapWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setParameterValues(TEST_PARAMETER_VALUES_1),
                // null values
                createFullyConfiguredInstance().setParameterValues(null)
        );
    }

    @Override
    protected ClientReportParametersMapWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setParameterValues(TEST_PARAMETER_VALUES);
    }

    @Override
    protected ClientReportParametersMapWrapper createInstanceWithDefaultParameters() {
        return new ClientReportParametersMapWrapper();
    }

    @Override
    protected ClientReportParametersMapWrapper createInstanceFromOther(ClientReportParametersMapWrapper other) {
        return new ClientReportParametersMapWrapper(other);
    }
}