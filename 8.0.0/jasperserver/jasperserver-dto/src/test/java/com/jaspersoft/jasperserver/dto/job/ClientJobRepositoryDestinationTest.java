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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobRepositoryDestinationTest extends BaseDTOPresentableTest<ClientJobRepositoryDestination> {

    private static final String TEST_FOLDER_URI = "TEST_FOLDER_URI";
    private static final String TEST_FOLDER_URI_1 = "TEST_FOLDER_URI_1";

    private static final Long TEST_ID = 100L;
    private static final Long TEST_ID_1 = 1001L;

    private static final String TEST_OUTPUT_DESCRIPTION = "TEST_OUTPUT_DESCRIPTION";
    private static final String TEST_OUTPUT_DESCRIPTION_1 = "TEST_OUTPUT_DESCRIPTION_1";

    private static final Boolean TEST_OVERWRITE_FILES = true;
    private static final Boolean TEST_OVERWRITE_FILES_1 = false;

    private static final Boolean TEST_SEQUNTIAL_FILENAMES = true;
    private static final Boolean TEST_SEQUNTIAL_FILENAMES_1 = false;

    private static final Integer TEST_VERSION = 101;
    private static final Integer TEST_VERSION_1 = 1011;

    private static final String TEST_TIMESTAMP_PATTERN = "TEST_TIMESTAMP_PATTERN";
    private static final String TEST_TIMESTAMP_PATTERN_1 = "TEST_TIMESTAMP_PATTERN_1";

    private static final Boolean TEST_SAVE_TO_REPOSITORY = true;
    private static final Boolean TEST_SAVE_TO_REPOSITORY_1 = false;

    private static final String TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI = "TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI";
    private static final String TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI_1 = "TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI_1";

    private static final Boolean TEST_USING_DEFAULT_REPORT_OUTPUT_FOLDER_URI = true;
    private static final Boolean TEST_USING_DEFAULT_REPORT_OUTPUT_FOLDER_URI_1 = false;

    private static final String TEST_OUTPUT_LOCAL_FOLDER = "TEST_OUTPUT_LOCAL_FOLDER";
    private static final String TEST_OUTPUT_LOCAL_FOLDER_1 = "TEST_OUTPUT_LOCAL_FOLDER_1";

    private static final ClientJobFtpInfo TEST_OUTPUT_FTP_INFO = new ClientJobFtpInfo().setUserName("USERNAME");
    private static final ClientJobFtpInfo TEST_OUTPUT_FTP_INFO_1 = new ClientJobFtpInfo().setUserName("USERNAME_1");

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobRepositoryDestination expected, ClientJobRepositoryDestination actual) {
        assertNotSame(expected.getOutputFTPInfo(), actual.getOutputFTPInfo());
    }

    @Test
    public void testGetters() {
        ClientJobRepositoryDestination fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getFolderURI(), TEST_FOLDER_URI);
        assertEquals(fullyConfiguredInstance.getId(), TEST_ID);
        assertEquals(fullyConfiguredInstance.getOutputDescription(), TEST_OUTPUT_DESCRIPTION);
        assertEquals(fullyConfiguredInstance.isOverwriteFiles(), TEST_OVERWRITE_FILES);
        assertEquals(fullyConfiguredInstance.isSequentialFilenames(), TEST_SEQUNTIAL_FILENAMES);
        assertEquals(fullyConfiguredInstance.getVersion(), TEST_VERSION);
        assertEquals(fullyConfiguredInstance.getTimestampPattern(), TEST_TIMESTAMP_PATTERN);
        assertEquals(fullyConfiguredInstance.isSaveToRepository(), TEST_SAVE_TO_REPOSITORY);
        assertEquals(fullyConfiguredInstance.getDefaultReportOutputFolderURI(), TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI);
        assertEquals(fullyConfiguredInstance.isUsingDefaultReportOutputFolderURI(), TEST_USING_DEFAULT_REPORT_OUTPUT_FOLDER_URI);
        assertEquals(fullyConfiguredInstance.getOutputLocalFolder(), TEST_OUTPUT_LOCAL_FOLDER);
        assertEquals(fullyConfiguredInstance.getOutputFTPInfo(), TEST_OUTPUT_FTP_INFO);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobRepositoryDestination> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setFolderURI(TEST_FOLDER_URI_1),
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setOutputDescription(TEST_OUTPUT_DESCRIPTION_1),
                createFullyConfiguredInstance().setOverwriteFiles(TEST_OVERWRITE_FILES_1),
                createFullyConfiguredInstance().setSequentialFilenames(TEST_SEQUNTIAL_FILENAMES_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setTimestampPattern(TEST_TIMESTAMP_PATTERN_1),
                createFullyConfiguredInstance().setSaveToRepository(TEST_SAVE_TO_REPOSITORY_1),
                createFullyConfiguredInstance().setDefaultReportOutputFolderURI(TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI_1),
                createFullyConfiguredInstance().setUsingDefaultReportOutputFolderURI(TEST_USING_DEFAULT_REPORT_OUTPUT_FOLDER_URI_1),
                createFullyConfiguredInstance().setOutputLocalFolder(TEST_OUTPUT_LOCAL_FOLDER_1),
                createFullyConfiguredInstance().setOutputFTPInfo(TEST_OUTPUT_FTP_INFO_1),
                // null values
                createFullyConfiguredInstance().setFolderURI(null),
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setOutputDescription(null),
                createFullyConfiguredInstance().setOverwriteFiles(null),
                createFullyConfiguredInstance().setSequentialFilenames(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setTimestampPattern(null),
                createFullyConfiguredInstance().setSaveToRepository(null),
                createFullyConfiguredInstance().setDefaultReportOutputFolderURI(null),
                createFullyConfiguredInstance().setUsingDefaultReportOutputFolderURI(null),
                createFullyConfiguredInstance().setOutputLocalFolder(null),
                createFullyConfiguredInstance().setOutputFTPInfo(null)
        );
    }

    @Override
    protected ClientJobRepositoryDestination createFullyConfiguredInstance() {
        return new ClientJobRepositoryDestination()
                .setFolderURI(TEST_FOLDER_URI)
                .setId(TEST_ID)
                .setOutputDescription(TEST_OUTPUT_DESCRIPTION)
                .setOverwriteFiles(TEST_OVERWRITE_FILES)
                .setSequentialFilenames(TEST_SEQUNTIAL_FILENAMES)
                .setVersion(TEST_VERSION)
                .setTimestampPattern(TEST_TIMESTAMP_PATTERN)
                .setSaveToRepository(TEST_SAVE_TO_REPOSITORY)
                .setDefaultReportOutputFolderURI(TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI)
                .setUsingDefaultReportOutputFolderURI(TEST_USING_DEFAULT_REPORT_OUTPUT_FOLDER_URI)
                .setOutputLocalFolder(TEST_OUTPUT_LOCAL_FOLDER)
                .setOutputFTPInfo(TEST_OUTPUT_FTP_INFO);
    }

    @Override
    protected ClientJobRepositoryDestination createInstanceWithDefaultParameters() {
        return new ClientJobRepositoryDestination();
    }

    @Override
    protected ClientJobRepositoryDestination createInstanceFromOther(ClientJobRepositoryDestination other) {
        return new ClientJobRepositoryDestination(other);
    }

}
