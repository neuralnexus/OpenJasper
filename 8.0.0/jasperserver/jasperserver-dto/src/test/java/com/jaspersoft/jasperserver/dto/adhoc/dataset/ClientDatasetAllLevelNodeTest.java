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

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientDatasetAllLevelNodeTest extends BaseDTOJSONPresentableTest<ClientDatasetAllLevelNode> {

    private static final List<String> TEST_DATA = Collections.singletonList("DATA");
    private static final List<String> TEST_DATA_ALT = Collections.singletonList("DATA_ALT");

    private static final List<AbstractClientDatasetLevelNode> TEST_CHILDREN = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetAllLevelNode()
    );

    private static final List<AbstractClientDatasetLevelNode> TEST_CHILDREN_ALT = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetAllLevelNode().setData(TEST_DATA_ALT)
    );

    @Override
    protected List<ClientDatasetAllLevelNode> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setData(TEST_DATA_ALT),
                createFullyConfiguredInstance().setChildren(TEST_CHILDREN_ALT),
                createFullyConfiguredInstance().setData(null),
                createFullyConfiguredInstance().setChildren(null)
        );
    }

    @Override
    protected ClientDatasetAllLevelNode createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setData(TEST_DATA)
                .setChildren(TEST_CHILDREN);
    }

    @Override
    protected ClientDatasetAllLevelNode createInstanceWithDefaultParameters() {
        return new ClientDatasetAllLevelNode();
    }

    @Override
    protected ClientDatasetAllLevelNode createInstanceFromOther(ClientDatasetAllLevelNode other) {
        return new ClientDatasetAllLevelNode(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientDatasetAllLevelNode expected, ClientDatasetAllLevelNode actual) {
        assertNotSame(expected.getData(), actual.getData());
        assertNotSameCollection(expected.getChildren(), actual.getChildren());
    }
}