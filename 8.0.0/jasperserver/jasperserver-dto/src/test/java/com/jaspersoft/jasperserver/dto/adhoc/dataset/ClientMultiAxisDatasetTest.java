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

class ClientMultiAxisDatasetTest extends BaseDTOJSONPresentableTest<ClientMultiAxisDataset> {

    private static final List<Integer> TEST_COUNTS = Collections.singletonList(100);
    private static final List<Integer> TEST_COUNTS_ALT = Collections.singletonList(1001);

    private static final String[] TEST_DATA_ITEM = new String[] {"TEST_DATA_ITEM"};
    private static final List<String[]> TEST_DATA = Collections.singletonList(TEST_DATA_ITEM);

    private static final String[] TEST_DATA_ITEM_ALT = new String[] {"TEST_DATA_ITEM_ALT"};
    private static final List<String[]> TEST_DATA_ALT = Collections.singletonList(TEST_DATA_ITEM_ALT);

    private static final List<String[]> TEST_DATA_ALT_WITH_DIFF_SIZE = Arrays.asList(
            TEST_DATA_ITEM,
            TEST_DATA_ITEM_ALT
    );
    private static final List<ClientAxis> TEST_AXES = Collections.singletonList(new ClientAxis());
    private static final List<ClientAxis> TEST_AXES_ALT = Collections.singletonList(new ClientAxis().setAxisNode(new ClientAxisNode()));

    @Override
    protected List<ClientMultiAxisDataset> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setCounts(TEST_COUNTS_ALT),
                createFullyConfiguredInstance().setData(TEST_DATA_ALT),
                createFullyConfiguredInstance().setData(TEST_DATA_ALT_WITH_DIFF_SIZE),
                createFullyConfiguredInstance().setAxes(TEST_AXES_ALT),
                createFullyConfiguredInstance().setCounts(null),
                createFullyConfiguredInstance().setData(null),
                createFullyConfiguredInstance().setAxes(null)
        );
    }

    @Override
    protected ClientMultiAxisDataset createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setCounts(TEST_COUNTS)
                .setData(TEST_DATA)
                .setAxes(TEST_AXES);
    }

    @Override
    protected ClientMultiAxisDataset createInstanceWithDefaultParameters() {
        return new ClientMultiAxisDataset();
    }

    @Override
    protected ClientMultiAxisDataset createInstanceFromOther(ClientMultiAxisDataset other) {
        return new ClientMultiAxisDataset(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiAxisDataset expected, ClientMultiAxisDataset actual) {
        assertNotSame(expected.getCounts(), actual.getCounts());
        assertNotSameCollection(expected.getAxes(), actual.getAxes());
        assertNotSame(expected.getData(), actual.getData());
    }
}