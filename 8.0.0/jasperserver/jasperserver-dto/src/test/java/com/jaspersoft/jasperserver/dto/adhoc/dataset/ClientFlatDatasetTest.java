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

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientFlatDatasetTest extends BaseDTOJSONPresentableTest<ClientFlatDataset> {

    private static final int TEST_COUNTS = 100;
    private static final int TEST_COUNTS_ALT = 1001;

    private static final String[] TEST_ROW = new String[] {"ROW"};
    private static final List<String[]> TEST_ROWS = Collections.singletonList(TEST_ROW);

    private static final String[] TEST_ROW_ALT = new String[] {"ROW_ALT"};
    private static final List<String[]> TEST_ROWS_ALT = Collections.singletonList(TEST_ROW_ALT);

    private static final List<String[]> TEST_ROWS_ALT_WITH_DIFF_SIZE = Arrays.asList(
            TEST_ROW,
            TEST_ROW_ALT
    );

    private static final List<ClientFlatDatasetFieldReference> TEST_FIELDS = Collections.singletonList(
            new ClientFlatDatasetFieldReference()
    );
    private static final List<ClientFlatDatasetFieldReference> TEST_FIELDS_ALT = Collections.singletonList(
            new ClientFlatDatasetFieldReference().setKind("TEST_KIND")
    );


    @Override
    protected List<ClientFlatDataset> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setCounts(TEST_COUNTS_ALT),
                createFullyConfiguredInstance().setFields(TEST_FIELDS_ALT),
                createFullyConfiguredInstance().setRows(TEST_ROWS_ALT),
                createFullyConfiguredInstance().setRows(TEST_ROWS_ALT_WITH_DIFF_SIZE),
                createFullyConfiguredInstance().setFields(null),
                createFullyConfiguredInstance().setRows(null)
        );
    }

    @Override
    protected ClientFlatDataset createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setCounts(TEST_COUNTS)
                .setRows(TEST_ROWS)
                .setFields(TEST_FIELDS);
    }

    @Override
    protected ClientFlatDataset createInstanceWithDefaultParameters() {
        return new ClientFlatDataset();
    }

    @Override
    protected ClientFlatDataset createInstanceFromOther(ClientFlatDataset other) {
        return new ClientFlatDataset(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientFlatDataset expected, ClientFlatDataset actual) {
        assertNotSameCollection(expected.getFields(), actual.getFields());
        assertNotSameCollection(expected.getRows(), actual.getRows());
    }
}