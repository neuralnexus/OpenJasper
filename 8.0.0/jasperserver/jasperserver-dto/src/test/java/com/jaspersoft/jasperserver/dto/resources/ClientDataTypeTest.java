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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientDataTypeTest extends BaseDTOPresentableTest<ClientDataType> {

    private static final ClientDataType.TypeOfDataType TEST_TYPE = ClientDataType.TypeOfDataType.number;
    private static final ClientDataType.TypeOfDataType TEST_TYPE_1 = ClientDataType.TypeOfDataType.date;

    private static final String TEST_PATTERN = "TEST_PATTERN";
    private static final String TEST_PATTERN_1 = "TEST_PATTERN_1";

    private static final String TEST_MAX_VALUE = "TEST_MAX_VALUE";
    private static final String TEST_MAX_VALUE_1 = "TEST_MAX_VALUE_1";

    private static final Boolean TEST_STRICT_MAX = true;
    private static final Boolean TEST_STRICT_MAX_1 = false;

    private static final String TEST_MIN_VALUE = "TEST_MIN_VALUE";
    private static final String TEST_MIN_VALUE_1 = "TEST_MIN_VALUE_1";

    private static final Boolean TEST_STRICT_MIN = true;
    private static final Boolean TEST_STRICT_MIN_1 = false;

    private static final Integer TEST_MAX_LENGHT = 100;
    private static final Integer TEST_MAX_LENGHT_1 = 101;

    // Base class fields (ClientResource)

    private static final Integer TEST_VERSION = 101;
    private static final Integer TEST_VERSION_1 = 1011;

    private static final Integer TEST_PERMISSION_MASK = 100;
    private static final Integer TEST_PERMISSION_MASK_1 = 1001;

    private static final String TEST_CREATION_DATE = "TEST_CREATION_DATE";
    private static final String TEST_CREATION_DATE_1 = "TEST_CREATION_DATE_1";

    private static final String TEST_UDPATE_DATE = "TEST_UPDATE_DATE";
    private static final String TEST_UDPATE_DATE_1 = "TEST_UPDATE_DATE_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";


    @Override
    protected List<ClientDataType> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setType(TEST_TYPE_1),
                createFullyConfiguredInstance().setPattern(TEST_PATTERN_1),
                createFullyConfiguredInstance().setMaxValue(TEST_MAX_VALUE_1),
                createFullyConfiguredInstance().setStrictMax(TEST_STRICT_MAX_1),
                createFullyConfiguredInstance().setMinValue(TEST_MIN_VALUE_1),
                createFullyConfiguredInstance().setStrictMin(TEST_STRICT_MIN_1),
                createFullyConfiguredInstance().setMaxLength(TEST_MAX_LENGHT_1),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                // fields with null values
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setPattern(null),
                createFullyConfiguredInstance().setMaxValue(null),
                createFullyConfiguredInstance().setStrictMax(null),
                createFullyConfiguredInstance().setMinValue(null),
                createFullyConfiguredInstance().setStrictMin(null),
                createFullyConfiguredInstance().setMaxLength(null),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setUpdateDate(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setUri(null)
        );
    }

    @Override
    protected ClientDataType createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setType(TEST_TYPE)
                .setPattern(TEST_PATTERN)
                .setMaxValue(TEST_MAX_VALUE)
                .setStrictMax(TEST_STRICT_MAX)
                .setMinValue(TEST_MIN_VALUE)
                .setStrictMin(TEST_STRICT_MIN)
                .setMaxLength(TEST_MAX_LENGHT)
                // base class fields (ClientResource)
                .setVersion(TEST_VERSION)
                .setPermissionMask(TEST_PERMISSION_MASK)
                .setCreationDate(TEST_CREATION_DATE)
                .setUpdateDate(TEST_UDPATE_DATE)
                .setLabel(TEST_LABEL)
                .setDescription(TEST_DESCRIPTION)
                .setUri(TEST_URI);
    }

    @Override
    protected ClientDataType createInstanceWithDefaultParameters() {
        return new ClientDataType();
    }

    @Override
    protected ClientDataType createInstanceFromOther(ClientDataType other) {
        return new ClientDataType(other);
    }

}