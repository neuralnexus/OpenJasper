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

package com.jaspersoft.jasperserver.dto.importexport;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class StateTest extends BaseDTOPresentableTest<State> {

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final String TEST_MESSAGE = "TEST_MESSAGE";
    private static final String TEST_MESSAGE_1 = "TEST_MESSAGE_1";

    private static final String TEST_PHASE = "TEST_PHASE";
    private static final String TEST_PHASE_1 = "TEST_PHASE_1";

    private static final List<WarningDescriptor> TEST_WARNINGS = Collections.singletonList(new WarningDescriptor().setCode("TEST_CODE"));
    private static final List<WarningDescriptor> TEST_WARNINGS_1 = Collections.singletonList(new WarningDescriptor().setCode("TEST_CODE_1"));
    private static final List<WarningDescriptor> TEST_WARNINGS_EMPTY = new ArrayList<WarningDescriptor>();

    private static final ErrorDescriptor TEST_ERROR_DESCRIPTION = new ErrorDescriptor().setMessage("TEST_MESSAGE");
    private static final ErrorDescriptor TEST_ERROR_DESCRIPTION_1 = new ErrorDescriptor().setMessage("TEST_MESSAGE_1");

    @Override
    protected void assertFieldsHaveUniqueReferences(State expected, State actual) {
        assertNotSame(expected.getWarnings(), actual.getWarnings());
        assertNotSame(expected.getError(), actual.getError());
    }

    /*
     * Preparing
     */

    @Override
    protected List<State> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setMessage(TEST_MESSAGE_1),
                createFullyConfiguredInstance().setPhase(TEST_PHASE_1),
                createFullyConfiguredInstance().setWarnings(TEST_WARNINGS_1),
                createFullyConfiguredInstance().setWarnings(TEST_WARNINGS_EMPTY),
                createFullyConfiguredInstance().setError(TEST_ERROR_DESCRIPTION_1),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setMessage(null),
                createFullyConfiguredInstance().setPhase(null),
                createFullyConfiguredInstance().setWarnings(null),
                createFullyConfiguredInstance().setError(null)
        );
    }

    @Override
    protected State createFullyConfiguredInstance() {
        return new State()
                .setId(TEST_ID)
                .setMessage(TEST_MESSAGE)
                .setPhase(TEST_PHASE)
                .setWarnings(TEST_WARNINGS)
                .setError(TEST_ERROR_DESCRIPTION);
    }

    @Override
    protected State createInstanceWithDefaultParameters() {
        return new State();
    }

    @Override
    protected State createInstanceFromOther(State other) {
        return new State(other);
    }
}
