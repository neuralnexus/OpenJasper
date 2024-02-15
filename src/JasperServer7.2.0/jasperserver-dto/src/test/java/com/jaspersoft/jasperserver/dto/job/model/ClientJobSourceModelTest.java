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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobSourceModelTest extends BaseDTOTest<ClientJobSourceModel> {

    private static final String TEST_REPORT_UNIT_URI = "TEST_REPORT_UNIT_URI";
    private static final String TEST_REPORT_UNIT_URI_1 = "TEST_REPORT_UNIT_URI_1";

    private static final Map<String, String[]> TEST_PARAMETERS = createTestParameters("KEY", "VALUE");
    private static final Map<String, String[]> TEST_PARAMETERS_1 = createTestParameters("KEY_1", "VALUE_1");

    private static Map<String, String[]> createTestParameters(String key, String value) {
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put(key, new String[]{value});
        return map;
    }

    @Override
    protected List<ClientJobSourceModel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setReportUnitURI(TEST_REPORT_UNIT_URI_1),
                createFullyConfiguredInstance().setParameters(TEST_PARAMETERS_1),
                createInstanceWithDefaultParameters().setReportUnitURI(TEST_REPORT_UNIT_URI_1),
                createInstanceWithDefaultParameters().setParameters(TEST_PARAMETERS_1),
                // null values
                createFullyConfiguredInstance().setReportUnitURI(null),
                createFullyConfiguredInstance().setParameters(null),
                createInstanceWithDefaultParameters().setReportUnitURI(null),
                createInstanceWithDefaultParameters().setParameters(null)
        );
    }

    @Override
    protected ClientJobSourceModel createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setReportUnitURI(TEST_REPORT_UNIT_URI)
                .setParameters(TEST_PARAMETERS);
    }

    @Override
    protected ClientJobSourceModel createInstanceWithDefaultParameters() {
        return new ClientJobSourceModel();
    }

    @Override
    protected ClientJobSourceModel createInstanceFromOther(ClientJobSourceModel other) {
        return new ClientJobSourceModel(other);
    }

}
