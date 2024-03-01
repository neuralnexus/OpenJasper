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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientAxisTest extends BaseDTOPresentableTest<ClientAxis> {

    private static final ClientAxisNode TEST_AXIS_NODE = new ClientAxisNode();
    private static final ClientAxisNode TEST_AXIS_NODE_ALT = new ClientAxisNode().setAll(true);

    private static final List<ClientMultiAxisDatasetLevel> TEST_LEVELS = Arrays.asList(
            (ClientMultiAxisDatasetLevel)new ClientMultiAxisGroupLevel(),
            (ClientMultiAxisDatasetLevel)new ClientMultiAxisAggregationLevel()
    );
    private static final List<ClientMultiAxisDatasetLevel> TEST_LEVELS_ALT = Arrays.asList(
            (ClientMultiAxisDatasetLevel)new ClientMultiAxisGroupLevel().setType("TYPE"),
            (ClientMultiAxisDatasetLevel)new ClientMultiAxisAggregationLevel().setMembers(Collections.singletonList("MEMBER"))
    );

    @Override
    protected List<ClientAxis> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAxisNode(TEST_AXIS_NODE_ALT),
                createFullyConfiguredInstance().setLevels(TEST_LEVELS_ALT),
                createFullyConfiguredInstance().setAxisNode(null),
                createFullyConfiguredInstance().setLevels(null)
        );
    }

    @Override
    protected ClientAxis createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setAxisNode(TEST_AXIS_NODE)
                .setLevels(TEST_LEVELS);
    }

    @Override
    protected ClientAxis createInstanceWithDefaultParameters() {
        return new ClientAxis();
    }

    @Override
    protected ClientAxis createInstanceFromOther(ClientAxis other) {
        return new ClientAxis(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientAxis expected, ClientAxis actual) {
        assertNotSame(expected.getLevels(), actual.getLevels());
        assertNotSame(expected.getLevels().get(0), actual.getLevels().get(0));
    }
}