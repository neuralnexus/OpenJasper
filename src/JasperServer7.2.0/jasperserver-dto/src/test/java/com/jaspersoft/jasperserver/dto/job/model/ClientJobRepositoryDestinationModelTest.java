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
import com.jaspersoft.jasperserver.dto.job.ClientJobFtpInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobRepositoryDestinationModelTest extends BaseDTOTest<ClientJobRepositoryDestinationModel> {

    private static final String TEST_FOLDER_URI = "TEST_FOLDER_URI";
    private static final String TEST_FOLDER_URI_1 = "TEST_FOLDER_URI_1";

    private static final String TEST_OUTPUT_DESCRIPTION = "TEST_OUTPUT_DESCRIPTION";
    private static final String TEST_OUTPUT_DESCRIPTION_1 = "TEST_OUTPUT_DESCRIPTION_1";

    private static final Boolean TEST_OVERWRITE_FILES = true;
    private static final Boolean TEST_OVERWRITE_FILES_1 = false;

    private static final Boolean TEST_SEQUNTIAL_FILENAMES = true;
    private static final Boolean TEST_SEQUNTIAL_FILENAMES_1 = false;

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

    private static final ClientJobFTPInfoModel TEST_OUTPUT_FTP_INFO_MODEL = new ClientJobFTPInfoModel().setUserName("USERNAME");
    private static final ClientJobFTPInfoModel TEST_OUTPUT_FTP_INFO_MODEL_1 = new ClientJobFTPInfoModel().setUserName("USERNAME_1");

    @Test
    public void setOutputFTPInfoModel() {
        ClientJobRepositoryDestinationModel instance = createFullyConfiguredInstance();
        instance.setOutputFTPInfoModel(TEST_OUTPUT_FTP_INFO_MODEL);
        assertEquals(TEST_OUTPUT_FTP_INFO, instance.getOutputFTPInfo());
    }

    @Test
    public void testGetters() {
        ClientJobRepositoryDestinationModel instance = createFullyConfiguredInstance();
        assertTrue(instance.isFolderURIModified());
        assertTrue(instance.isSequentialFilenamesModified());
        assertTrue(instance.isOverwriteFilesModified());
        assertTrue(instance.isOutputDescriptionModified());
        assertTrue(instance.isTimestampPatternModified());
        assertTrue(instance.isSaveToRepositoryModified());
        assertTrue(instance.isDefaultReportOutputFolderURIModified());
        assertTrue(instance.isUsingDefaultReportOutputFolderURIModified());
        assertTrue(instance.isOutputFTPInfoModified());
        assertTrue(instance.isOutputLocalFolderModified());
    }

    @Override
    protected List<ClientJobRepositoryDestinationModel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                (ClientJobRepositoryDestinationModel)createFullyConfiguredInstance().setVersion(2),
                (ClientJobRepositoryDestinationModel)createFullyConfiguredInstance().setId(2L),
                createFullyConfiguredInstance().setFolderURI(TEST_FOLDER_URI_1),
                createFullyConfiguredInstance().setSequentialFilenames(TEST_SEQUNTIAL_FILENAMES_1),
                createFullyConfiguredInstance().setOverwriteFiles(TEST_OVERWRITE_FILES_1),
                createFullyConfiguredInstance().setOutputDescription(TEST_OUTPUT_DESCRIPTION_1),
                createFullyConfiguredInstance().setTimestampPattern(TEST_TIMESTAMP_PATTERN_1),
                createFullyConfiguredInstance().setSaveToRepository(TEST_SAVE_TO_REPOSITORY_1),
                createFullyConfiguredInstance().setDefaultReportOutputFolderURI(TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI_1),
                createFullyConfiguredInstance().setUsingDefaultReportOutputFolderURI(TEST_USING_DEFAULT_REPORT_OUTPUT_FOLDER_URI_1),
                createFullyConfiguredInstance().setOutputFTPInfo(TEST_OUTPUT_FTP_INFO_1),
                createFullyConfiguredInstance().setOutputLocalFolder(TEST_OUTPUT_LOCAL_FOLDER_1),
                // null values
                createFullyConfiguredInstance().setFolderURI(null),
                createFullyConfiguredInstance().setSequentialFilenames(null),
                createFullyConfiguredInstance().setOverwriteFiles(null),
                createFullyConfiguredInstance().setOutputDescription(null),
                createFullyConfiguredInstance().setTimestampPattern(null),
                createFullyConfiguredInstance().setSaveToRepository(null),
                createFullyConfiguredInstance().setDefaultReportOutputFolderURI(null),
                createFullyConfiguredInstance().setUsingDefaultReportOutputFolderURI(null),
                createFullyConfiguredInstance().setOutputFTPInfo(null),
                createFullyConfiguredInstance().setOutputLocalFolder(null)
        );
    }

    @Override
    protected ClientJobRepositoryDestinationModel createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setFolderURI(TEST_FOLDER_URI)
                .setSequentialFilenames(TEST_SEQUNTIAL_FILENAMES)
                .setOverwriteFiles(TEST_OVERWRITE_FILES)
                .setOutputDescription(TEST_OUTPUT_DESCRIPTION)
                .setTimestampPattern(TEST_TIMESTAMP_PATTERN)
                .setSaveToRepository(TEST_SAVE_TO_REPOSITORY)
                .setDefaultReportOutputFolderURI(TEST_DEFAULT_REPORT_OUTPUT_FOLDER_URI)
                .setUsingDefaultReportOutputFolderURI(TEST_USING_DEFAULT_REPORT_OUTPUT_FOLDER_URI)
                .setOutputFTPInfo(TEST_OUTPUT_FTP_INFO)
                .setOutputLocalFolder(TEST_OUTPUT_LOCAL_FOLDER);
    }

    @Override
    protected ClientJobRepositoryDestinationModel createInstanceWithDefaultParameters() {
        return new ClientJobRepositoryDestinationModel();
    }

    @Override
    protected ClientJobRepositoryDestinationModel createInstanceFromOther(ClientJobRepositoryDestinationModel other) {
        return new ClientJobRepositoryDestinationModel(other);
    }

}
