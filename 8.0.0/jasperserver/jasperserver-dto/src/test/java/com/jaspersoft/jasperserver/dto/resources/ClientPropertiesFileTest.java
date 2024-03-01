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
import java.util.LinkedList;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientPropertiesFileTest extends BaseDTOPresentableTest<ClientPropertiesFile> {

    private static final LinkedList<ClientProperty> TEST_PROPERTIES = new LinkedList<ClientProperty>(Arrays.asList(
            new ClientProperty().setKey("TEST_KEY"),
            new ClientProperty().setValue("TEST_VALUE")
    ));
    private static final LinkedList<ClientProperty> TEST_PROPERTIES_1 = new LinkedList<ClientProperty>(Arrays.asList(
            new ClientProperty().setKey("TEST_KEY_1"),
            new ClientProperty().setValue("TEST_VALUE_1")
    ));

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
    protected List<ClientPropertiesFile> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setProperties(TEST_PROPERTIES_1),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                // fields with null values
                createFullyConfiguredInstance().setProperties(null),
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
    protected ClientPropertiesFile createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setProperties(TEST_PROPERTIES)
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
    protected ClientPropertiesFile createInstanceWithDefaultParameters() {
        return new ClientPropertiesFile();
    }

    @Override
    protected ClientPropertiesFile createInstanceFromOther(ClientPropertiesFile other) {
        return new ClientPropertiesFile(other);
    }
}