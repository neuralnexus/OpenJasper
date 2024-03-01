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

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientAxisNodeTest extends BaseDTOPresentableTest<ClientAxisNode> {

    private static final boolean TEST_IS_ALL = true;
    private static final boolean TEST_IS_ALL_ALT = false;

    private static final List<ClientAxisNode> TEST_CHILDREN = Collections.singletonList(
            new ClientAxisNode()
    );
    private static final List<ClientAxisNode> TEST_CHILDREN_ALT = Collections.singletonList(
            new ClientAxisNode().setAll(TEST_IS_ALL)
    );

    private static final Integer TEST_MEMBER_IDX = 100;
    private static final Integer TEST_MEMBER_IDX_ALT = 1001;

    private static final Integer TEST_DATA_IDX = 200;
    private static final Integer TEST_DATA_IDX_ALT = 2001;


    @Override
    protected List<ClientAxisNode> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAll(TEST_IS_ALL_ALT),
                createFullyConfiguredInstance().setChildren(TEST_CHILDREN_ALT),
                createFullyConfiguredInstance().setMemberIdx(TEST_MEMBER_IDX_ALT),
                createFullyConfiguredInstance().setDataIdx(TEST_DATA_IDX_ALT),
                createFullyConfiguredInstance().setChildren(null),
                createFullyConfiguredInstance().setMemberIdx(null),
                createFullyConfiguredInstance().setDataIdx(null)
        );
    }

    @Override
    protected ClientAxisNode createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setAll(TEST_IS_ALL)
                .setChildren(TEST_CHILDREN)
                .setMemberIdx(TEST_MEMBER_IDX)
                .setDataIdx(TEST_DATA_IDX);
    }

    @Override
    protected ClientAxisNode createInstanceWithDefaultParameters() {
        return new ClientAxisNode();
    }

    @Override
    protected ClientAxisNode createInstanceFromOther(ClientAxisNode other) {
        return new ClientAxisNode(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientAxisNode expected, ClientAxisNode actual) {
        assertNotSameCollection(expected.getChildren(), actual.getChildren());
    }
}