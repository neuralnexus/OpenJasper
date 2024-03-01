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

class ClientMondrianConnectionTest extends BaseDTOPresentableTest<ClientMondrianConnection> {

    // Base class fields (AbstractClientMondrianConnection)

    private static final ClientReferenceableFile TEST_SCHEMA = new ClientFile().setContent("TEST_CONTENT");
    private static final ClientReferenceableFile TEST_SCHEMA_1 = new ClientFile().setContent("TEST_CONTENT_1");

    // Base class fields (AbstractClientDataSourceHolder)

    private static final ClientReferenceableDataSource TEST_DATA_SOURCE = new ClientCustomDataSource().setDataSourceName("TEST_DATA_SOURCE_NAME");
    private static final ClientReferenceableDataSource TEST_DATA_SOURCE_1 = new ClientCustomDataSource().setDataSourceName("TEST_DATA_SOURCE_NAME_1");

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
    protected List<ClientMondrianConnection> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // base class fields (AbstractClientMondrianConnection)
                createFullyConfiguredInstance().setSchema(TEST_SCHEMA_1),
                // base class fields (AbstractClientDataSourceHolder)
                createFullyConfiguredInstance().setDataSource(TEST_DATA_SOURCE_1),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                // fields with null values
                // base class fields (AbstractClientMondrianConnection)
                createFullyConfiguredInstance().setSchema(null),
                // base class fields (AbstractClientDataSourceHolder)
                createFullyConfiguredInstance().setDataSource(null),
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
    protected ClientMondrianConnection createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                // base class fields (AbstractClientMondrianConnection)
                .setSchema(TEST_SCHEMA)
                // base class fields (AbstractClientDataSourceHolder)
                .setDataSource(TEST_DATA_SOURCE)
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
    protected ClientMondrianConnection createInstanceWithDefaultParameters() {
        return new ClientMondrianConnection();
    }

    @Override
    protected ClientMondrianConnection createInstanceFromOther(ClientMondrianConnection other) {
        return new ClientMondrianConnection(other);
    }
}