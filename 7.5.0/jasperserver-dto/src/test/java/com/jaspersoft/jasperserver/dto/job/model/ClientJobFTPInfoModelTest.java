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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobFTPInfoModelTest extends BaseDTOTest<ClientJobFTPInfoModel> {

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_PASSWORD_1 = "TEST_PASSWORD_1";

    private static final String TEST_FOLDER_PATH = "TEST_FOLDER_PATH";
    private static final String TEST_FOLDER_PATH_1 = "TEST_FOLDER_PATH_1";

    private static final String TEST_SERVER_NAME = "TEST_SERVER_NAME";
    private static final String TEST_SERVER_NAME_1 = "TEST_SERVER_NAME_1";

    private static final Map<String, String> TEST_PROPERTIES_MAP = createMap("TEST_KEY", "TEST_VALUE");
    private static final Map<String, String> TEST_PROPERTIES_MAP_1 = createMap("TEST_KEY_1", "TEST_VALUE_1");

    private static Map<String, String> createMap(String key, String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    };

    @Override
    protected List<ClientJobFTPInfoModel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setUserName(TEST_USERNAME_1),
                createFullyConfiguredInstance().setPassword(TEST_PASSWORD_1),
                createFullyConfiguredInstance().setFolderPath(TEST_FOLDER_PATH_1),
                createFullyConfiguredInstance().setServerName(TEST_SERVER_NAME_1),
                createFullyConfiguredInstance().setPropertiesMap(TEST_PROPERTIES_MAP_1),
                // null values
                createFullyConfiguredInstance().setUserName(null),
                createFullyConfiguredInstance().setPassword(null),
                createFullyConfiguredInstance().setFolderPath(null),
                createFullyConfiguredInstance().setServerName(null),
                createFullyConfiguredInstance().setPropertiesMap(null)
        );
    }

    @Override
    protected ClientJobFTPInfoModel createFullyConfiguredInstance() {
        return new ClientJobFTPInfoModel()
                .setUserName(TEST_USERNAME)
                .setPassword(TEST_PASSWORD)
                .setFolderPath(TEST_FOLDER_PATH)
                .setServerName(TEST_SERVER_NAME)
                .setPropertiesMap(TEST_PROPERTIES_MAP);
    }

    @Override
    protected ClientJobFTPInfoModel createInstanceWithDefaultParameters() {
        return new ClientJobFTPInfoModel();
    }

    @Override
    protected ClientJobFTPInfoModel createInstanceFromOther(ClientJobFTPInfoModel other) {
        return new ClientJobFTPInfoModel(other);
    }

}
