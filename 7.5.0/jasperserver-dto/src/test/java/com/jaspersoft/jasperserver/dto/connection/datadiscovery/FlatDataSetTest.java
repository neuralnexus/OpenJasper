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

package com.jaspersoft.jasperserver.dto.connection.datadiscovery;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class FlatDataSetTest extends BaseDTOPresentableTest<FlatDataSet> {

    private static final List<String[]> TEST_DATA = Arrays.asList(
            new String[]{"TEST_DATA_COLUMN_A_ROW_A", "TEST_DATA_COLUMN_B_ROW_A"},
            new String[]{"TEST_DATA_COLUMN_A_ROW_B", "TEST_DATA_COLUMN_B_ROW_B"}
            );
    private static final List<String[]> TEST_DATA_1 = Arrays.asList(
            new String[]{"TEST_DATA_COLUMN_A_ROW_A_1", "TEST_DATA_COLUMN_B_ROW_A_1"},
            new String[]{"TEST_DATA_COLUMN_A_ROW_B_1", "TEST_DATA_COLUMN_B_ROW_B_1"}
            );
    private static final List<String[]> TEST_DATA_2 = Arrays.asList(
            new String[]{},
            new String[]{"TEST_DATA_COLUMN_A_ROW_A_1"},
            new String[]{"TEST_DATA_COLUMN_A_ROW_B_1", "TEST_DATA_COLUMN_B_ROW_B_1"},
            new String[]{"TEST_DATA_COLUMN_A_ROW_C_1", "TEST_DATA_COLUMN_B_ROW_C_1", "TES_DATA_COLUMN_C_ROW_C_1"}
            );
    private static final ResourceGroupElement TEST_METADATA = new ResourceGroupElement().setKind("TEST_KIND");
    private static final ResourceGroupElement TEST_METADATA_1 = new ResourceGroupElement().setKind("TEST_KIND_1");

    @Override
    protected List<FlatDataSet> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setData(TEST_DATA_1),
                createFullyConfiguredInstance().setData(TEST_DATA_2),
                createFullyConfiguredInstance().setMetadata(TEST_METADATA_1)
        );
    }

    @Override
    protected FlatDataSet createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setData(TEST_DATA)
                .setMetadata(TEST_METADATA);
    }

    @Override
    protected FlatDataSet createInstanceWithDefaultParameters() {
        return new FlatDataSet();
    }

    @Override
    protected FlatDataSet createInstanceFromOther(FlatDataSet other) {
        return new FlatDataSet(other);
    }

}
