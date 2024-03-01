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

package com.jaspersoft.jasperserver.dto.connection;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class FtpConnectionTest extends BaseDTOPresentableTest<FtpConnection> {

    private static final String TEST_HOLDER = "TEST_HOLDER";
    private static final String TEST_HOLDER_1 = "TEST_HOLDER_1";

    private static final String TEST_HOST = "TEST_HOST";
    private static final String TEST_HOST_1 = "TEST_HOST_1";

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_PASSWORD_1 = "TEST_PASSWORD_1";

    private static final String TEST_FOLDER_PATH = "TEST_FOLDER_PATH";
    private static final String TEST_FOLDER_PATH_1 = "TEST_FOLDER_PATH_1";

    private static final FtpConnection.FtpType TEST_TYPE = FtpConnection.FtpType.ftp;
    private static final FtpConnection.FtpType TEST_TYPE_1 = FtpConnection.FtpType.ftps;

    private static final String TEST_PROTOCOL = "TEST_PROTOCOL";
    private static final String TEST_PROTOCOL_1 = "TEST_PROTOCOL_1";

    private static final Integer TEST_PORT = 100;
    private static final Integer TEST_PORT_1 = 1001;

    private static final Boolean TEST_IMPLICIT = true;
    private static final Boolean TEST_IMPLICIT_1 = false;

    private static final String TEST_SSH_KEY = "TEST_SSH_KEY";
    private static final String TEST_SSH_KEY_1 = "TEST_SSH_KEY_1";

    private static final String TEST_SSH_PASSPHRASE = "TEST_SSH_PASSPHRASE";
    private static final String TEST_SSH_PASSPHRASE_1 = "TEST_SSH_PASSPHRASE_1";

    private static final String TEST_PROT = "TEST_PROT";
    private static final String TEST_PROT_1 = "TEST_PROT_1";

    private static final Long TEST_PBSZ = 101L;
    private static final Long TEST_PBSZ_1 = 1011L;

    /*
     * Preparing
     */

    @Override
    protected List<FtpConnection> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setHolder(TEST_HOLDER_1),
                createFullyConfiguredInstance().setHost(TEST_HOST_1),
                createFullyConfiguredInstance().setUserName(TEST_USERNAME_1),
                createFullyConfiguredInstance().setUserName(""),
                createFullyConfiguredInstance().setPassword(TEST_PASSWORD_1),
                createFullyConfiguredInstance().setFolderPath(TEST_FOLDER_PATH_1),
                createFullyConfiguredInstance().setType(TEST_TYPE_1),
                createFullyConfiguredInstance().setProtocol(TEST_PROTOCOL_1),
                createFullyConfiguredInstance().setPort(TEST_PORT_1),
                createFullyConfiguredInstance().setImplicit(TEST_IMPLICIT_1),
                createFullyConfiguredInstance().setSshKey(TEST_SSH_KEY_1),
                createFullyConfiguredInstance().setSshPassphrase(TEST_SSH_PASSPHRASE_1),
                createFullyConfiguredInstance().setProt(TEST_PROT_1),
                createFullyConfiguredInstance().setPbsz(TEST_PBSZ_1),
                createFullyConfiguredInstance().setHolder(null),
                createFullyConfiguredInstance().setHost(null),
                createFullyConfiguredInstance().setUserName(null),
                createFullyConfiguredInstance().setPassword(null),
                createFullyConfiguredInstance().setFolderPath(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setProtocol(null),
                createFullyConfiguredInstance().setPort(null),
                createFullyConfiguredInstance().setImplicit(null),
                createFullyConfiguredInstance().setSshKey(null),
                createFullyConfiguredInstance().setSshPassphrase(null),
                createFullyConfiguredInstance().setProt(null),
                createFullyConfiguredInstance().setPbsz(null)
        );
    }

    @Override
    protected FtpConnection createFullyConfiguredInstance() {
        return new FtpConnection()
                .setHolder(TEST_HOLDER)
                .setHost(TEST_HOST)
                .setUserName(TEST_USERNAME)
                .setPassword(TEST_PASSWORD)
                .setFolderPath(TEST_FOLDER_PATH)
                .setType(TEST_TYPE)
                .setProtocol(TEST_PROTOCOL)
                .setPort(TEST_PORT)
                .setImplicit(TEST_IMPLICIT)
                .setSshKey(TEST_SSH_KEY)
                .setSshPassphrase(TEST_SSH_PASSPHRASE)
                .setProt(TEST_PROT)
                .setPbsz(TEST_PBSZ);
    }

    @Override
    protected FtpConnection createInstanceWithDefaultParameters() {
        return new FtpConnection();
    }

    @Override
    protected FtpConnection createInstanceFromOther(FtpConnection other) {
        return new FtpConnection(other);
    }

}
