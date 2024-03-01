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
import java.util.Collections;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class XlsFileMetadataTest extends BaseDTOPresentableTest<XlsFileMetadata> {

    private static final List<XlsSheet> TEST_SHEETS = Arrays.asList(
            new XlsSheet().setName("TEST_NAME"),
            new XlsSheet().setColumns(Collections.singletonList("TEST_COLUMN"))
    );
    private static final List<XlsSheet> TEST_SHEETS_1 = Arrays.asList(
            new XlsSheet().setName("TEST_NAME_1"),
            new XlsSheet().setColumns(Collections.singletonList("TEST_COLUMN_1"))
    );

    @Override
    protected List<XlsFileMetadata> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setSheets(TEST_SHEETS_1)
        );
    }

    @Override
    protected XlsFileMetadata createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setSheets(TEST_SHEETS);
    }

    @Override
    protected XlsFileMetadata createInstanceWithDefaultParameters() {
        return new XlsFileMetadata();
    }

    @Override
    protected XlsFileMetadata createInstanceFromOther(XlsFileMetadata other) {
        return new XlsFileMetadata(other);
    }

}
