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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientMultiLevelDatasetTest extends BaseDTOJSONPresentableTest<ClientMultiLevelDataset> {

    private static final Integer TEST_COUNTS = 100;
    private static final Integer TEST_COUNTS_ALT = 1001;

    private static final List<AbstractClientDatasetLevel> TEST_LEVELS = Collections.singletonList(
            (AbstractClientDatasetLevel)new ClientDatasetAllLevel()
    );
    private static final List<AbstractClientDatasetLevel> TEST_LEVELS_ALT = Collections.singletonList(
            (AbstractClientDatasetLevel)new ClientDatasetAllLevel().setFieldRefs(Collections.singletonList(new ClientDatasetFieldReference()))
    );
    private static final List<AbstractClientDatasetLevel> TEST_LEVELS_ALT_1 = Collections.singletonList(
            (AbstractClientDatasetLevel)new ClientDatasetDetailLevel().setFieldRefs(Collections.singletonList(new ClientDatasetFieldReference()))
    );
    private static final List<AbstractClientDatasetLevel> TEST_LEVELS_ALT_2 = Collections.singletonList(
            (AbstractClientDatasetLevel)new ClientDatasetGroupLevel().setFieldRefs(Collections.singletonList(new ClientDatasetFieldReference()))
    );

    private static final List<AbstractClientDatasetLevelNode> TEST_LEVEL_DATA_NODES = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetAllLevelNode()
    );
    private static final List<AbstractClientDatasetLevelNode> TEST_LEVEL_DATA_NODES_ALT = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetAllLevelNode().setData(Collections.singletonList("DATA"))
    );
    private static final List<AbstractClientDatasetLevelNode> TEST_LEVEL_DATA_NODES_ALT_1 = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetDetailLevelNode()
    );
    private static final List<AbstractClientDatasetLevelNode> TEST_LEVEL_DATA_NODES_ALT_2 = Collections.singletonList(
            (AbstractClientDatasetLevelNode)new ClientDatasetGroupLevelNode()
    );

    @Override
    protected List<ClientMultiLevelDataset> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setCounts(TEST_COUNTS_ALT),
                createFullyConfiguredInstance().setLevels(TEST_LEVELS_ALT),
                createFullyConfiguredInstance().setLevels(TEST_LEVELS_ALT_1),
                createFullyConfiguredInstance().setLevels(TEST_LEVELS_ALT_2),
                createFullyConfiguredInstance().setLevelDataNodes(TEST_LEVEL_DATA_NODES_ALT),
                createFullyConfiguredInstance().setLevelDataNodes(TEST_LEVEL_DATA_NODES_ALT_1),
                createFullyConfiguredInstance().setLevelDataNodes(TEST_LEVEL_DATA_NODES_ALT_2),
                createFullyConfiguredInstance().setCounts(null),
                createFullyConfiguredInstance().setLevels(null),
                createFullyConfiguredInstance().setLevelDataNodes(null)
        );
    }

    @Override
    protected ClientMultiLevelDataset createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setCounts(TEST_COUNTS)
                .setLevels(TEST_LEVELS)
                .setLevelDataNodes(TEST_LEVEL_DATA_NODES);
    }

    @Override
    protected ClientMultiLevelDataset createInstanceWithDefaultParameters() {
        return new ClientMultiLevelDataset();
    }

    @Override
    protected ClientMultiLevelDataset createInstanceFromOther(ClientMultiLevelDataset other) {
        return new ClientMultiLevelDataset(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiLevelDataset expected, ClientMultiLevelDataset actual) {
        assertNotSameCollection(expected.getLevels(), actual.getLevels());
        assertNotSameCollection(expected.getLevelDataNodes(), actual.getLevelDataNodes());
    }
}