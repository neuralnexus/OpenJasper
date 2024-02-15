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

package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.utils.CustomAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class AdhocFilterAvailableValuesTest extends BaseDTOJSONPresentableTest<AdhocFilterAvailableValues> {

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_ALT = "TEST_LABEL_ALT";

    private static final List<InputControlOption> TEST_SINGLE_DATA = Collections.singletonList(new InputControlOption());
    private static final List<InputControlOption> TEST_MULTIPLE_DATA = Arrays.asList(
            new InputControlOption(),
            new InputControlOption().setLabel(TEST_LABEL),
            new InputControlOption().setLabel(TEST_LABEL_ALT)
    );
    private static final Integer TEST_TOTAL = 100;
    private static final Integer TEST_TOTAL_ALT = 1001;

    @Override
    protected List<AdhocFilterAvailableValues> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setData(TEST_MULTIPLE_DATA),
                createFullyConfiguredInstance().setData(Collections.<InputControlOption>emptyList()),
                createFullyConfiguredInstance().setTotal(TEST_TOTAL_ALT),
                createFullyConfiguredInstance().setData(null),
                createFullyConfiguredInstance().setTotal(null)
        );
    }

    @Override
    protected AdhocFilterAvailableValues createFullyConfiguredInstance() {
        AdhocFilterAvailableValues instance = new AdhocFilterAvailableValues();
        return instance
                .setData(TEST_SINGLE_DATA)
                .setTotal(TEST_TOTAL);
    }

    @Override
    protected AdhocFilterAvailableValues createInstanceWithDefaultParameters() {
        return new AdhocFilterAvailableValues();
    }

    @Override
    protected AdhocFilterAvailableValues createInstanceFromOther(AdhocFilterAvailableValues other) {
        return new AdhocFilterAvailableValues(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(AdhocFilterAvailableValues expected, AdhocFilterAvailableValues actual) {
        CustomAssertions.assertNotSameCollection(expected.getData(), actual.getData());
        assertNotSame(expected.getData().get(0), actual.getData().get(0));
    }

    @Test
    public void defaultConstructor() {
        AdhocFilterAvailableValues instance = new AdhocFilterAvailableValues();
        assertNull(instance.getTotal());
        assertNull(instance.getData());
    }

    @Test
    public void constructorWithParameters() {
        AdhocFilterAvailableValues instance = new AdhocFilterAvailableValues(TEST_TOTAL, TEST_MULTIPLE_DATA);
        assertEquals(TEST_TOTAL, instance.getTotal());
        assertEquals(TEST_MULTIPLE_DATA, instance.getData());
    }
}