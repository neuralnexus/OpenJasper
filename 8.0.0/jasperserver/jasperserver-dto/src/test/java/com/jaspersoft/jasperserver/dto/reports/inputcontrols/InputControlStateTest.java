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

package com.jaspersoft.jasperserver.dto.reports.inputcontrols;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class InputControlStateTest extends BaseDTOPresentableTest<InputControlState> {
    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final String TEST_VALUE = "TEST_VALUE";
    private static final String TEST_VALUE_1 = "TEST_VALUE_1";

    private static final String TEST_ERROR = "TEST_ERROR";
    private static final String TEST_ERROR_1 = "TEST_ERROR_1";

    private static final String TEST_TOTAL_COUNT = "TEST_TOTAL_COUNT";
    private static final String TEST_TOTAL_COUNT_1 = "TEST_TOTAL_COUNT_1";

    private static final List<InputControlOption> TEST_OPTIONS = Collections.singletonList(new InputControlOption().setLabel("TEST_LABEL"));
    private static final List<InputControlOption> TEST_OPTIONS_1 = Collections.singletonList(new InputControlOption().setLabel("TEST_LABEL_1"));
    private static final List<InputControlOption> TEST_OPTIONS_EMPTY = new ArrayList<InputControlOption>();


    @Override
    protected List<InputControlState> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setValue(TEST_VALUE_1),
                createFullyConfiguredInstance().setError(TEST_ERROR_1),
                createFullyConfiguredInstance().setTotalCount(TEST_TOTAL_COUNT_1),
                createFullyConfiguredInstance().setOptions(TEST_OPTIONS_1),
                createFullyConfiguredInstance().setOptions(TEST_OPTIONS_EMPTY),
                // null values
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setValue(null),
                createFullyConfiguredInstance().setError(null),
                createFullyConfiguredInstance().setTotalCount(null),
                createFullyConfiguredInstance().setOptions(null)
        );
    }

    @Override
    protected InputControlState createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setUri(TEST_URI)
                .setId(TEST_ID)
                .setValue(TEST_VALUE)
                .setError(TEST_ERROR)
                .setTotalCount(TEST_TOTAL_COUNT)
                .setOptions(TEST_OPTIONS);
    }

    @Override
    protected InputControlState createInstanceWithDefaultParameters() {
        return new InputControlState();
    }

    @Override
    protected InputControlState createInstanceFromOther(InputControlState other) {
        return new InputControlState(other);
    }
}
