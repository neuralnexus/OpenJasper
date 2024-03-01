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
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientQueryTest extends BaseDTOPresentableTest<ClientQuery> {

    private static final String TEST_LANGUAGE = "TEST_LANGUAGE";
    private static final String TEST_LANGUAGE_1 = "TEST_LANGUAGE_1";

    private static final String TEST_VALUE = "TEST_VALUE";
    private static final String TEST_VALUE_1 = "TEST_VALUE_1";

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
    protected List<ClientQuery> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setLanguage(TEST_LANGUAGE_1),
                createFullyConfiguredInstance().setValue(TEST_VALUE_1),
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
                createFullyConfiguredInstance().setLanguage(null),
                createFullyConfiguredInstance().setValue(null),
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
    protected ClientQuery createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setLanguage(TEST_LANGUAGE)
                .setValue(TEST_VALUE)
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
    protected ClientQuery createInstanceWithDefaultParameters() {
        return new ClientQuery();
    }

    @Override
    protected ClientQuery createInstanceFromOther(ClientQuery other) {
        return new ClientQuery(other);
    }

    @Test
    public void nullTest() {
        Exception ex = null;
        try {
            ClientQuery clientQuery = new ClientQuery((ClientQuery) null);
        } catch (Exception ex2) {
            ex = ex2;
        }
        assertTrue(ex != null);
    }
}