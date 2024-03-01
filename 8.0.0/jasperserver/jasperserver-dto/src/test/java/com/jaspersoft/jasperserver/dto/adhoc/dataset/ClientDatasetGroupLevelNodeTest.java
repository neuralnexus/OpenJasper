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

class ClientDatasetGroupLevelNodeTest extends BaseDTOJSONPresentableTest<ClientDatasetGroupLevelNode> {

    private static final List<String> TEST_DATA = Collections.singletonList("TEST_DATA");
    private static final List<String> TEST_DATA_ALT = Collections.singletonList("TEST_DATA_ALT");

    private static final int TEST_MEMBER_ID = 100;
    private static final int TEST_MEMBER_ID_ALT = 1001;

    private static final List<AbstractClientDatasetLevelNode> TEST_CHILDREN = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetGroupLevelNode()
    );
    private static final List<AbstractClientDatasetLevelNode> TEST_CHILDREN_ALT = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetGroupLevelNode().setData(TEST_DATA)
    );

    @Override
    protected List<ClientDatasetGroupLevelNode> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setData(TEST_DATA_ALT),
                createFullyConfiguredInstance().setMemberIdx(TEST_MEMBER_ID_ALT),
                createFullyConfiguredInstance().setChildren(TEST_CHILDREN_ALT),
                createFullyConfiguredInstance().setData(null),
                createFullyConfiguredInstance().setChildren(null)
        );
    }

    @Override
    protected ClientDatasetGroupLevelNode createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setData(TEST_DATA)
                .setMemberIdx(TEST_MEMBER_ID)
                .setChildren(TEST_CHILDREN);
    }

    @Override
    protected ClientDatasetGroupLevelNode createInstanceWithDefaultParameters() {
        return new ClientDatasetGroupLevelNode();
    }

    @Override
    protected ClientDatasetGroupLevelNode createInstanceFromOther(ClientDatasetGroupLevelNode other) {
        return new ClientDatasetGroupLevelNode(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientDatasetGroupLevelNode expected, ClientDatasetGroupLevelNode actual) {
        assertNotSame(expected.getData(), actual.getData());
        assertNotSameCollection(expected.getChildren(), actual.getChildren());
    }
}