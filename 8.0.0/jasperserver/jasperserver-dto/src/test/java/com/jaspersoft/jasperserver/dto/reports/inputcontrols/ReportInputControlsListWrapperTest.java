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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ReportInputControlsListWrapperTest extends BaseDTOPresentableTest<ReportInputControlsListWrapper> {
    private static final List<ReportInputControl> TEST_INPUT_PARAMETERS = Arrays.asList(
            new ReportInputControl().setId("TEST_ID"),
            new ReportInputControl().setLabel("TEST_LABEL")
    );
    private static final List<ReportInputControl> TEST_INPUT_PARAMETERS_1 = Arrays.asList(
            new ReportInputControl().setId("TEST_ID_1"),
            new ReportInputControl().setLabel("TEST_LABEL_1")
    );
    private static final List<ReportInputControl> TEST_INPUT_PARAMETERS_EMPTY = new ArrayList<ReportInputControl>();

    @Test
    public void testConstructor() {
        ReportInputControlsListWrapper instance = new ReportInputControlsListWrapper(TEST_INPUT_PARAMETERS);
        assertEquals(instance.getInputParameters(), TEST_INPUT_PARAMETERS);
    }

    @Override
    protected List<ReportInputControlsListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setInputParameters(TEST_INPUT_PARAMETERS_1),
                createFullyConfiguredInstance().setInputParameters(TEST_INPUT_PARAMETERS_EMPTY),
                // null values
                createFullyConfiguredInstance().setInputParameters(null)
        );
    }

    @Override
    protected ReportInputControlsListWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setInputParameters(TEST_INPUT_PARAMETERS);
    }

    @Override
    protected ReportInputControlsListWrapper createInstanceWithDefaultParameters() {
        return new ReportInputControlsListWrapper();
    }

    @Override
    protected ReportInputControlsListWrapper createInstanceFromOther(ReportInputControlsListWrapper other) {
        return new ReportInputControlsListWrapper(other);
    }
}
