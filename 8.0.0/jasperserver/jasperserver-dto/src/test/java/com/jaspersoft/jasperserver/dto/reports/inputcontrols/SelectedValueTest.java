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

public class SelectedValueTest extends BaseDTOPresentableTest<SelectedValue> {
    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final List<InputControlOption> TEST_OPTIONS = Collections.singletonList(new InputControlOption().setLabel("TEST_LABEL").setValue("TEST_VALUE"));
    private static final List<InputControlOption> TEST_OPTIONS_1 = Collections.singletonList(new InputControlOption().setLabel("TEST_LABEL_1").setValue("TEST_VALUE_1"));
    private static final List<InputControlOption> TEST_OPTIONS_EMPTY = new ArrayList<InputControlOption>();


    /*
     * BaseDTOPresentableTests
     */

    @Override
    protected List<SelectedValue> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setOptions(TEST_OPTIONS_1),
                createFullyConfiguredInstance().setOptions(TEST_OPTIONS_EMPTY),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setOptions(null)
        );
    }

    @Override
    protected SelectedValue createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setId(TEST_ID)
                .setOptions(TEST_OPTIONS);
    }

    @Override
    protected SelectedValue createInstanceWithDefaultParameters() {
        return new SelectedValue();
    }

    @Override
    protected SelectedValue createInstanceFromOther(SelectedValue other) {
        return new SelectedValue(other);
    }
}
