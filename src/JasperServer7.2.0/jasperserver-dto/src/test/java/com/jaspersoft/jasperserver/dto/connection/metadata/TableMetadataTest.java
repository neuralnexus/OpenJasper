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

package com.jaspersoft.jasperserver.dto.connection.metadata;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class TableMetadataTest extends BaseDTOPresentableTest<TableMetadata> {

    private static final String TEST_QUERY_LANGUAGE = "TEST_QUERY_LANGUAGE";
    private static final String TEST_QUERY_LANGUAGE_1 = "TEST_QUERY_LANGUAGE_1";

    private static final List<ColumnMetadata> TEST_COLUMNS = Arrays.asList(
            new ColumnMetadata().setName("TEST_NAME"),
            new ColumnMetadata().setLabel("TEST_LABEL")
    );
    private static final List<ColumnMetadata> TEST_COLUMNS_1 = Arrays.asList(
            new ColumnMetadata().setName("TEST_NAME_1"),
            new ColumnMetadata().setLabel("TEST_LABEL_1")
    );

    @Override
    protected List<TableMetadata> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setQueryLanguage(TEST_QUERY_LANGUAGE_1),
                createFullyConfiguredInstance().setColumns(TEST_COLUMNS_1)
        );
    }

    @Override
    protected TableMetadata createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setQueryLanguage(TEST_QUERY_LANGUAGE)
                .setColumns(TEST_COLUMNS);
    }

    @Override
    protected TableMetadata createInstanceWithDefaultParameters() {
        return new TableMetadata();
    }

    @Override
    protected TableMetadata createInstanceFromOther(TableMetadata other) {
        return new TableMetadata(other);
    }

}
