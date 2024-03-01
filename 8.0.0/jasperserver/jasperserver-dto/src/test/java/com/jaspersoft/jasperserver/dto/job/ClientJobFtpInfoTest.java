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

package com.jaspersoft.jasperserver.dto.job;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.connection.FtpConnection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobFtpInfoTest extends BaseDTOPresentableTest<ClientJobFtpInfo> {

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_PASSWORD_1 = "TEST_PASSWORD_1";

    private static final String TEST_FOLDER_PATH = "TEST_FOLDER_PATH";
    private static final String TEST_FOLDER_PATH_1 = "TEST_FOLDER_PATH_1";

    private static final String TEST_SERVER_NAME = "TEST_SERVER_NAME";
    private static final String TEST_SERVER_NAME_1 = "TEST_SERVER_NAME_1";

    private static final FtpConnection.FtpType TEST_TYPE = FtpConnection.FtpType.ftp;
    private static final FtpConnection.FtpType TEST_TYPE_1 = FtpConnection.FtpType.ftps;

    private static final String TEST_PROTOCOL = "TEST_PROTOCOL";
    private static final String TEST_PROTOCOL_1 = "TEST_PROTOCOL_1";

    private static final Integer TEST_PORT = 100;
    private static final Integer TEST_PORT_1 = 1001;

    private static final Boolean TEST_IMPLICIT = true;
    private static final Boolean TEST_IMPLICIT_1 = false;

    private static final Long TEST_PBSZ = 101L;
    private static final Long TEST_PBSZ_1 = 1011L;

    private static final String TEST_PROT = "TEST_PROT";
    private static final String TEST_PROT_1 = "TEST_PROT_1";

    private static final Map<String, String> TEST_PROPERTIES_MAP = createPropertiesMap("KEY", "VALUE");
    private static final Map<String, String> TEST_PROPERTIES_MAP_1 = createPropertiesMap("KEY_1", "VALUE_1");

    private static final String TEST_SSH_KEY = "TEST_SSH_KEY";
    private static final String TEST_SSH_KEY_1 = "TEST_SSH_KEY_1";

    private static final String TEST_SSH_PASSPHRASE = "TEST_SSH_PASSPHRASE";
    private static final String TEST_SSH_PASSPHRASE_1 = "TEST_SSH_PASSPHRASE_1";

    private static Map<String, String> createPropertiesMap(String key, String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    }


    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobFtpInfo expected, ClientJobFtpInfo actual) {
        assertNotSame(expected.getPropertiesMap(), actual.getPropertiesMap());
    }

    @Test
    public void testGetters() {
        ClientJobFtpInfo fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getUserName(), TEST_USERNAME);
        assertEquals(fullyConfiguredInstance.getPassword(), TEST_PASSWORD);
        assertEquals(fullyConfiguredInstance.getFolderPath(), TEST_FOLDER_PATH);
        assertEquals(fullyConfiguredInstance.getServerName(), TEST_SERVER_NAME);
        assertEquals(fullyConfiguredInstance.getType(), TEST_TYPE);
        assertEquals(fullyConfiguredInstance.getProtocol(), TEST_PROTOCOL);
        assertEquals(fullyConfiguredInstance.getPort(), TEST_PORT);
        assertEquals(fullyConfiguredInstance.getImplicit(), TEST_IMPLICIT);
        assertEquals(fullyConfiguredInstance.getPbsz(), TEST_PBSZ);
        assertEquals(fullyConfiguredInstance.getProt(), TEST_PROT);
        assertEquals(fullyConfiguredInstance.getPropertiesMap(), TEST_PROPERTIES_MAP);
        assertEquals(fullyConfiguredInstance.getSshKey(), TEST_SSH_KEY);
        assertEquals(fullyConfiguredInstance.getSshPassphrase(), TEST_SSH_PASSPHRASE);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobFtpInfo> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setUserName(TEST_USERNAME_1),
                createFullyConfiguredInstance().setPassword(TEST_PASSWORD_1),
                createFullyConfiguredInstance().setFolderPath(TEST_FOLDER_PATH_1),
                createFullyConfiguredInstance().setServerName(TEST_SERVER_NAME_1),
                createFullyConfiguredInstance().setType(TEST_TYPE_1),
                createFullyConfiguredInstance().setProtocol(TEST_PROTOCOL_1),
                createFullyConfiguredInstance().setPort(TEST_PORT_1),
                createFullyConfiguredInstance().setImplicit(TEST_IMPLICIT_1),
                createFullyConfiguredInstance().setPbsz(TEST_PBSZ_1),
                createFullyConfiguredInstance().setProt(TEST_PROT_1),
                createFullyConfiguredInstance().setPropertiesMap(TEST_PROPERTIES_MAP_1),
                createFullyConfiguredInstance().setSshKey(TEST_SSH_KEY_1),
                createFullyConfiguredInstance().setSshPassphrase(TEST_SSH_PASSPHRASE_1),
                // null values
                createFullyConfiguredInstance().setUserName(null),
                createFullyConfiguredInstance().setPassword(null),
                createFullyConfiguredInstance().setFolderPath(null),
                createFullyConfiguredInstance().setServerName(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setProtocol(null),
                createFullyConfiguredInstance().setPort(null),
                createFullyConfiguredInstance().setImplicit(null),
                createFullyConfiguredInstance().setPbsz(null),
                createFullyConfiguredInstance().setProt(null),
                createFullyConfiguredInstance().setPropertiesMap(null),
                createFullyConfiguredInstance().setSshKey(null),
                createFullyConfiguredInstance().setSshPassphrase(null)
        );
    }

    @Override
    protected ClientJobFtpInfo createFullyConfiguredInstance() {
        return new ClientJobFtpInfo()
                .setUserName(TEST_USERNAME)
                .setPassword(TEST_PASSWORD)
                .setFolderPath(TEST_FOLDER_PATH)
                .setServerName(TEST_SERVER_NAME)
                .setType(TEST_TYPE)
                .setProtocol(TEST_PROTOCOL)
                .setPort(TEST_PORT)
                .setImplicit(TEST_IMPLICIT)
                .setPbsz(TEST_PBSZ)
                .setProt(TEST_PROT)
                .setPropertiesMap(TEST_PROPERTIES_MAP)
                .setSshKey(TEST_SSH_KEY)
                .setSshPassphrase(TEST_SSH_PASSPHRASE);
    }

    @Override
    protected ClientJobFtpInfo createInstanceWithDefaultParameters() {
        return new ClientJobFtpInfo();
    }

    @Override
    protected ClientJobFtpInfo createInstanceFromOther(ClientJobFtpInfo other) {
        return new ClientJobFtpInfo(other);
    }

}
