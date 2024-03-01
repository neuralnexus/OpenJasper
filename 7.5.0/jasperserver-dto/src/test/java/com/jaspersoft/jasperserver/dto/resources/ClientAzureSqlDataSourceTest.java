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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientAzureSqlDataSourceTest extends BaseDTOPresentableTest<ClientAzureSqlDataSource> {

    private static final String TEST_DB_NAME = "TEST_DB_NAME";
    private static final String TEST_DB_NAME_1 = "TEST_DB_NAME_1";

    private static final String TEST_KEY_STORE_PASSWORD = "TEST_KEY_STORE_PASSWORD";
    private static final String TEST_KEY_STORE_PASSWORD_1 = "TEST_KEY_STORE_PASSWORD_1";

    private static final String TEST_KEY_STORE_URI = "TEST_KEY_STORE_URI";
    private static final String TEST_KEY_STORE_URI_1 = "TEST_KEY_STORE_URI_1";

    private static final String TEST_SERVER_NAME = "TEST_SERVER_NAME";
    private static final String TEST_SERVER_NAME_1 = "TEST_SERVER_NAME_1";

    private static final String TEST_SUBSCRIPTION_ID = "TEST_SUBSCRIPTION_ID";
    private static final String TEST_SUBSCRIPTION_ID_1 = "TEST_SUBSCRIPTION_ID_1";

    // Base class fields (AbstractClientJdbcDataSource)

    private static final String TEST_CONNECTION_URL = "TEST_CONNECTION_URL";
    private static final String TEST_CONNECTION_URL_1 = "TEST_CONNECTION_URL_1";

    private static final String TEST_DRIVER_CLASS = "TEST_DRIVER_CLASS";
    private static final String TEST_DRIVER_CLASS_1 = "TEST_DRIVER_CLASS_1";

    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_PASSWORD_1 = "TEST_PASSWORD_1";

    private static final String TEST_TIMEZONE = "TEST_TIMEZONE";
    private static final String TEST_TIMEZONE_1 = "TEST_TIMEZONE_1";

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

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
    protected List<ClientAzureSqlDataSource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDbName(TEST_DB_NAME_1),
                createFullyConfiguredInstance().setKeyStorePassword(TEST_KEY_STORE_PASSWORD_1),
                createFullyConfiguredInstance().setKeyStoreUri(TEST_KEY_STORE_URI_1),
                createFullyConfiguredInstance().setServerName(TEST_SERVER_NAME_1),
                createFullyConfiguredInstance().setSubscriptionId(TEST_SUBSCRIPTION_ID_1),
                // base class fields
                createFullyConfiguredInstance().setConnectionUrl(TEST_CONNECTION_URL_1),
                createFullyConfiguredInstance().setDriverClass(TEST_DRIVER_CLASS_1),
                createFullyConfiguredInstance().setTimezone(TEST_TIMEZONE_1),
                createFullyConfiguredInstance().setUsername(TEST_USERNAME_1),
                createFullyConfiguredInstance().setPassword(TEST_PASSWORD_1),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                // fields with null values
                createFullyConfiguredInstance().setDbName(null),
                createFullyConfiguredInstance().setKeyStorePassword(null),
                createFullyConfiguredInstance().setKeyStoreUri(null),
                createFullyConfiguredInstance().setServerName(null),
                createFullyConfiguredInstance().setSubscriptionId(null),
                // base class fields
                createFullyConfiguredInstance().setConnectionUrl(null),
                createFullyConfiguredInstance().setDriverClass(null),
                createFullyConfiguredInstance().setTimezone(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setPassword(null),
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
    protected ClientAzureSqlDataSource createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setDbName(TEST_DB_NAME)
                .setKeyStorePassword(TEST_KEY_STORE_PASSWORD)
                .setKeyStoreUri(TEST_KEY_STORE_URI)
                .setServerName(TEST_SERVER_NAME)
                .setSubscriptionId(TEST_SUBSCRIPTION_ID)
                // base class fields
                .setConnectionUrl(TEST_CONNECTION_URL)
                .setDriverClass(TEST_DRIVER_CLASS)
                .setTimezone(TEST_TIMEZONE)
                .setUsername(TEST_USERNAME)
                .setPassword(TEST_PASSWORD)
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
    protected ClientAzureSqlDataSource createInstanceWithDefaultParameters() {
        return new ClientAzureSqlDataSource();
    }

    @Override
    protected ClientAzureSqlDataSource createInstanceFromOther(ClientAzureSqlDataSource other) {
        return new ClientAzureSqlDataSource(other);
    }

}