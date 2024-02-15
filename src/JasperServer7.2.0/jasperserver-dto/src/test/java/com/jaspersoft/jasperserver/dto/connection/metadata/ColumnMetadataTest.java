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

public class ColumnMetadataTest extends BaseDTOPresentableTest<ColumnMetadata> {

    private static final String TEST_NAME = "TEST_NAME";
    private static final String TEST_NAME_1 = "TEST_NAME_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_JAVA_TYPE = "TEST_JAVA_TYPE";
    private static final String TEST_JAVA_TYPE_1 = "TEST_JAVA_TYPE_1";

    @Override
    protected List<ColumnMetadata> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(TEST_NAME_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setJavaType(TEST_JAVA_TYPE_1)
        );
    }

    @Override
    protected ColumnMetadata createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setName(TEST_NAME)
                .setLabel(TEST_LABEL)
                .setJavaType(TEST_JAVA_TYPE);
    }

    @Override
    protected ColumnMetadata createInstanceWithDefaultParameters() {
        return new ColumnMetadata();
    }

    @Override
    protected ColumnMetadata createInstanceFromOther(ColumnMetadata other) {
        return new ColumnMetadata(other);
    }

}
