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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SelectedValuesListWrapperTest extends BaseDTOPresentableTest<SelectedValuesListWrapper> {
    private static final List<SelectedValue> TEST_SELECTED_VALUES = Collections.singletonList(
            new SelectedValue()
                    .setId("TEST_ID")
                    .setOptions(Collections.singletonList(new InputControlOption().setLabel("TEST_LABEL").setValue("TEST_VALUE")))
    );
    private static final List<SelectedValue> TEST_SELECTED_VALUES_1 = Collections.singletonList(
            new SelectedValue()
                    .setId("TEST_ID_1")
                    .setOptions(Collections.singletonList(new InputControlOption().setLabel("TEST_LABEL_1").setValue("TEST_VALUE_1")))
    );
    private static final List<SelectedValue> TEST_SELECTED_VALUES_EMPTY = new ArrayList<SelectedValue>();

    @Test
    public void testConstructor() {
        SelectedValuesListWrapper instance = new SelectedValuesListWrapper(TEST_SELECTED_VALUES);
        assertEquals(TEST_SELECTED_VALUES, instance.getSelectedValues());
    }

    @Override
    protected List<SelectedValuesListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setSelectedValues(TEST_SELECTED_VALUES_1),
                createFullyConfiguredInstance().setSelectedValues(TEST_SELECTED_VALUES_EMPTY),
                // null values
                createFullyConfiguredInstance().setSelectedValues(null)
        );
    }

    @Override
    protected SelectedValuesListWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters().setSelectedValues(TEST_SELECTED_VALUES);
    }

    @Override
    protected SelectedValuesListWrapper createInstanceWithDefaultParameters() {
        return new SelectedValuesListWrapper();
    }

    @Override
    protected SelectedValuesListWrapper createInstanceFromOther(SelectedValuesListWrapper other) {
        return new SelectedValuesListWrapper(other);
    }
}
