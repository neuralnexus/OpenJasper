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

package com.jaspersoft.jasperserver.dto.discovery;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ParameterTest extends BaseDTOPresentableTest<Parameter> {

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_VALUE_TYPE = "TEST_VALUE_TYPE";
    private static final String TEST_VALUE_TYPE_1 = "TEST_VALUE_TYPE_1";

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final boolean TEST_MULTIPLE_VALUES = true;
    private static final boolean TEST_MULTIPLE_VALUES_1 = false;

    /*
     * Preparing
     */

    @Override
    protected List<Parameter> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setValueType(TEST_VALUE_TYPE_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setMultipleValues(TEST_MULTIPLE_VALUES_1),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setValueType(null),
                createFullyConfiguredInstance().setUri(null)
        );
    }

    @Override
    protected Parameter createFullyConfiguredInstance() {
        return new Parameter()
                .setId(TEST_ID)
                .setLabel(TEST_LABEL)
                .setValueType(TEST_VALUE_TYPE)
                .setUri(TEST_URI)
                .setMultipleValues(TEST_MULTIPLE_VALUES);
    }

    @Override
    protected Parameter createInstanceWithDefaultParameters() {
        return new Parameter();
    }

    @Override
    protected Parameter createInstanceFromOther(Parameter other) {
        return new Parameter(other);
    }

}
