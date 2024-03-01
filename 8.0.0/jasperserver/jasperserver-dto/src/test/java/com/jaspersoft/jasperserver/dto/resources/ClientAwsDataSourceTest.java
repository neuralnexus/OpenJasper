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

class ClientAwsDataSourceTest extends BaseDTOPresentableTest<ClientAwsDataSource> {

    private static final String TEST_ACCESS_KEY = "TEST_ACCESS_KEY";
    private static final String TEST_ACCESS_KEY_1 = "TEST_ACCESS_KEY_1";

    private static final String TEST_DB_INSTANCE_IDENTIFIER = "TEST_DB_INSTANCE_IDENTIFIER";
    private static final String TEST_DB_INSTANCE_IDENTIFIER_1 = "TEST_DB_INSTANCE_IDENTIFIER_1";

    private static final String TEST_DB_NAME = "TEST_DB_NAME";
    private static final String TEST_DB_NAME_1 = "TEST_DB_NAME_1";

    private static final String TEST_DB_SERVICE = "TEST_DB_SERVICE";
    private static final String TEST_DB_SERVICE_1 = "TEST_DB_SERVICE_1";

    private static final String TEST_REGION = "TEST_REGION";
    private static final String TEST_REGION_1 = "TEST_REGION_1";

    private static final String TEST_ROLE_ARN = "TEST_ROLE_ARN";
    private static final String TEST_ROLE_ARN_1 = "TEST_ROLE_ARN_1";

    private static final String TEST_SECRET_KEY = "TEST_SECRET_KEY";
    private static final String TEST_SECRET_KEY_1 = "TEST_SECRET_KEY_1";

    // Base class fields (AbstractClientJdbcDataSource)

    private static final String TEST_CREATION_DATE = "TEST_CREATION_DATE";
    private static final String TEST_CREATION_DATE_1 = "TEST_CREATION_DATE_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final Integer TEST_PERMISSION_MASK = 100;
    private static final Integer TEST_PERMISSION_MASK_1 = 1001;

    private static final String TEST_UDPATE_DATE = "TEST_UPDATE_DATE";
    private static final String TEST_UDPATE_DATE_1 = "TEST_UPDATE_DATE_1";

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final Integer TEST_VERSION = 101;
    private static final Integer TEST_VERSION_1 = 1011;

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


    @Override
    protected List<ClientAwsDataSource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAccessKey(TEST_ACCESS_KEY_1),
                createFullyConfiguredInstance().setDbInstanceIdentifier(TEST_DB_INSTANCE_IDENTIFIER_1),
                createFullyConfiguredInstance().setDbName(TEST_DB_NAME_1),
                createFullyConfiguredInstance().setDbService(TEST_DB_SERVICE_1),
                createFullyConfiguredInstance().setRegion(TEST_REGION_1),
                createFullyConfiguredInstance().setRoleArn(TEST_ROLE_ARN_1),
                createFullyConfiguredInstance().setSecretKey(TEST_SECRET_KEY_1),
                // base class fields
                createFullyConfiguredInstance().setConnectionUrl(TEST_CONNECTION_URL_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setDriverClass(TEST_DRIVER_CLASS_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setPassword(TEST_PASSWORD_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setTimezone(TEST_TIMEZONE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setUsername(TEST_USERNAME_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                // fields with null values
                createFullyConfiguredInstance().setAccessKey(null),
                createFullyConfiguredInstance().setDbInstanceIdentifier(null),
                createFullyConfiguredInstance().setDbName(null),
                createFullyConfiguredInstance().setDbService(null),
                createFullyConfiguredInstance().setRegion(null),
                createFullyConfiguredInstance().setRoleArn(null),
                createFullyConfiguredInstance().setSecretKey(null),
                // base class fields
                createFullyConfiguredInstance().setConnectionUrl(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setDriverClass(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPassword(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setTimezone(null),
                createFullyConfiguredInstance().setUpdateDate(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setVersion(null)
        );
    }

    @Override
    protected ClientAwsDataSource createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setAccessKey(TEST_ACCESS_KEY)
                .setDbInstanceIdentifier(TEST_DB_INSTANCE_IDENTIFIER)
                .setDbName(TEST_DB_NAME)
                .setDbService(TEST_DB_SERVICE)
                .setRegion(TEST_REGION)
                .setRoleArn(TEST_ROLE_ARN)
                .setSecretKey(TEST_SECRET_KEY)
                // base class fields
                .setConnectionUrl(TEST_CONNECTION_URL)
                .setCreationDate(TEST_CREATION_DATE)
                .setDescription(TEST_DESCRIPTION)
                .setDriverClass(TEST_DRIVER_CLASS)
                .setLabel(TEST_LABEL)
                .setPassword(TEST_PASSWORD)
                .setPermissionMask(TEST_PERMISSION_MASK)
                .setTimezone(TEST_TIMEZONE)
                .setUpdateDate(TEST_UDPATE_DATE)
                .setUri(TEST_URI)
                .setUsername(TEST_USERNAME)
                .setVersion(TEST_VERSION);
    }

    @Override
    protected ClientAwsDataSource createInstanceWithDefaultParameters() {
        return new ClientAwsDataSource();
    }

    @Override
    protected ClientAwsDataSource createInstanceFromOther(ClientAwsDataSource other) {
        return new ClientAwsDataSource(other);
    }

}