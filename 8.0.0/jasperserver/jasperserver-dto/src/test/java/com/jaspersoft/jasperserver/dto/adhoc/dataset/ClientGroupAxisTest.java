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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientGroupAxisTest extends BaseDTOJSONPresentableTest<ClientGroupAxis> {

    private static final String[] TEST_LEVEL = new String[] {"LEVEL"};
    private static final List<String[]> TEST_LEVELS = Collections.singletonList(TEST_LEVEL);

    private static final String[] TEST_LEVEL_ALT = new String[] {"LEVEL_ALT"};
    private static final List<String[]> TEST_LEVELS_ALT = Collections.singletonList(TEST_LEVEL_ALT);

    private static final List<String[]> TEST_LEVELS_ALT_WITH_DIFF_SIZE = Arrays.asList(
            TEST_LEVEL,
            TEST_LEVEL_ALT
    );

    @Override
    protected List<ClientGroupAxis> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setLevel(TEST_LEVELS_ALT),
                createFullyConfiguredInstance().setLevel(TEST_LEVELS_ALT_WITH_DIFF_SIZE),
                createFullyConfiguredInstance().setLevel(null)
        );
    }

    @Override
    protected ClientGroupAxis createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setLevel(TEST_LEVELS);
    }

    @Override
    protected ClientGroupAxis createInstanceWithDefaultParameters() {
        return new ClientGroupAxis();
    }

    @Override
    protected ClientGroupAxis createInstanceFromOther(ClientGroupAxis other) {
        return new ClientGroupAxis(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientGroupAxis expected, ClientGroupAxis actual) {
        assertNotSame(expected.getLevel(), actual.getLevel());
    }
}