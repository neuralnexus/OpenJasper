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
import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.job.ClientCalendarDaysType;
import com.jaspersoft.jasperserver.dto.job.ClientJobAlert;
import com.jaspersoft.jasperserver.dto.job.ClientJobCalendarTrigger;
import com.jaspersoft.jasperserver.dto.job.ClientJobMailNotification;
import com.jaspersoft.jasperserver.dto.job.ClientJobRepositoryDestination;
import com.jaspersoft.jasperserver.dto.job.ClientJobSource;
import com.jaspersoft.jasperserver.dto.job.ClientJobTrigger;
import com.jaspersoft.jasperserver.dto.job.ClientReportJob;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientReportJobModelTest extends BaseDTOTest<ClientReportJobModel> {

    private static final Long TEST_ID = 100L;
    private static final Integer TEST_VERSION = 2;
    private static final String TEST_OUTPUT_TIMEZONE = "TEST_OUTPUT_TIMEZONE";

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final Timestamp TEST_CREATION_DATE = new Timestamp(100);
    private static final Timestamp TEST_CREATION_DATE_1 = new Timestamp(101);

    private static final ClientJobTrigger TEST_TRIGGER = new ClientJobCalendarTrigger().setDaysType(ClientCalendarDaysType.ALL);
    private static final ClientJobTrigger TEST_TRIGGER_1 = new ClientJobCalendarTrigger().setDaysType(ClientCalendarDaysType.MONTH);

    private static final ClientJobSource TEST_SOURCE = new ClientJobSource().setReportUnitURI("TEST_REPORT_UNIT_URI");
    private static final ClientJobSource TEST_SOURCE_1 = new ClientJobSource().setReportUnitURI("TEST_REPORT_UNIT_URI_1");

    private static final String TEST_BASE_OUTPUT_FILENAME = "TEST_BASE_OUTPUT_FILENAME";
    private static final String TEST_BASE_OUTPUT_FILENAME_1 = "TEST_BASE_OUTPUT_FILENAME_1";

    private static final Set<OutputFormat> TEST_OUTPUT_FORMATS = new HashSet<OutputFormat>(Collections.singletonList(OutputFormat.HTML));
    private static final Set<OutputFormat> TEST_OUTPUT_FORMATS_1 = new HashSet<OutputFormat>(Collections.singletonList(OutputFormat.DOCX));

    private static final String TEST_OUTPUT_LOCALE = "TEST_OUTPUT_LOCALE";
    private static final String TEST_OUTPUT_LOCALE_1 = "TEST_OUTPUT_LOCALE_1";

    private static final ClientJobRepositoryDestination TEST_REPOSITORY_DESTINATION = new ClientJobRepositoryDestination().setFolderURI("TEST_FOLDER_URI");
    private static final ClientJobRepositoryDestination TEST_REPOSITORY_DESTINATION_1 = new ClientJobRepositoryDestination().setFolderURI("TEST_FOLDER_URI_1");

    private static final ClientJobMailNotification TEST_MAIL_NOTIFICATION = new ClientJobMailNotification().setSkipNotificationWhenJobFails(true);
    private static final ClientJobMailNotification TEST_MAIL_NOTIFICATION_1 = new ClientJobMailNotification().setSkipNotificationWhenJobFails(false);

    private static final ClientJobAlert TEST_ALERT = new ClientJobAlert().setIncludingReportJobInfo(true);
    private static final ClientJobAlert TEST_ALERT_1 = new ClientJobAlert().setIncludingReportJobInfo(false);

    private static final ClientJobTrigger TEST_TRIGGER_MODEL = new ClientJobCalendarTrigger().setDaysType(ClientCalendarDaysType.ALL);
    private static final ClientJobTrigger TEST_TRIGGER_MODEL_1 = new ClientJobCalendarTrigger().setDaysType(ClientCalendarDaysType.MONTH);

    private static final ClientJobMailNotificationModel TEST_MAIL_NOTIFICATION_MODEL = new ClientJobMailNotificationModel().setSkipNotificationWhenJobFails(true);
    private static final ClientJobMailNotificationModel TEST_MAIL_NOTIFICATION_MODEL_1 = new ClientJobMailNotificationModel().setSkipNotificationWhenJobFails(false);

    private static final ClientJobAlertModel TEST_ALERT_MODEL = new ClientJobAlertModel().setIncludingReportJobInfo(true);
    private static final ClientJobAlertModel TEST_ALERT_MODEL_1 = new ClientJobAlertModel().setIncludingReportJobInfo(false);

    private static final ClientJobRepositoryDestinationModel TEST_REPOSITORY_DESTINATION_MODEL = new ClientJobRepositoryDestinationModel().setFolderURI("TEST_FOLDER_URI");
    private static final ClientJobRepositoryDestinationModel TEST_REPOSITORY_DESTINATION_MODEL_1 = new ClientJobRepositoryDestinationModel().setFolderURI("TEST_FOLDER_URI_1");

    private static final ClientJobSourceModel TEST_SOURCE_MODEL = new ClientJobSourceModel().setReportUnitURI("TEST_REPORT_UNIT_URI");

    @Test
    public void setRepositoryDestinationModel() {
        ClientReportJobModel instance = createInstanceWithDefaultParameters();
        instance.setRepositoryDestinationModel(TEST_REPOSITORY_DESTINATION_MODEL);
        assertEquals(TEST_REPOSITORY_DESTINATION, instance.getRepositoryDestination());
    }

    @Test
    public void setAlertModel() {
        ClientReportJobModel instance = createInstanceWithDefaultParameters();
        instance.setAlertModel(TEST_ALERT_MODEL);
        assertEquals(TEST_ALERT, instance.getAlert());
    }

    @Test
    public void setMailNotificationModel() {
        ClientReportJobModel instance = createInstanceWithDefaultParameters();
        instance.setMailNotificationModel(TEST_MAIL_NOTIFICATION_MODEL);
        assertEquals(TEST_MAIL_NOTIFICATION, instance.getMailNotification());
    }

    @Test
    public void setTriggerModel() {
        ClientReportJobModel instance = createInstanceWithDefaultParameters();
        instance.setTriggerModel(TEST_TRIGGER_MODEL);
        assertEquals(TEST_TRIGGER, instance.getTrigger());
    }

    @Test
    public void setSourceModel() {
        ClientReportJobModel instance = createInstanceWithDefaultParameters();
        instance.setSourceModel(TEST_SOURCE_MODEL);
        assertEquals(TEST_SOURCE, instance.getSource());
    }

    @Test
    public void testGetters() {
        ClientReportJobModel instance = createFullyConfiguredInstance();
        assertTrue(instance.isSourceModified());
        assertTrue(instance.isTriggerModified());
        assertTrue(instance.isMailNotificationModified());
        assertTrue(instance.isAlertModified());
        assertTrue(instance.isContentRespositoryDestinationModified());
        assertTrue(instance.isDescriptionModified());
        assertTrue(instance.isCreationDateModified());
        assertTrue(instance.isLabelModified());
        assertTrue(instance.isBaseOutputFileNameModified());
        assertTrue(instance.isOutputFormatsModified());
        assertTrue(instance.isUsernameModified());
        assertTrue(instance.isOutputLocaleModified());
    }

    @Test
    public void testConstructor() {
        ClientReportJob clientReportJob = new ClientReportJob()
                .setId(TEST_ID)
                .setVersion(TEST_VERSION)
                .setUsername(TEST_USERNAME)
                .setLabel(TEST_LABEL)
                .setDescription(TEST_DESCRIPTION)
                .setCreationDate(TEST_CREATION_DATE)
                .setTrigger(TEST_TRIGGER)
                .setSource(TEST_SOURCE)
                .setBaseOutputFilename(TEST_BASE_OUTPUT_FILENAME)
                .setOutputFormats(TEST_OUTPUT_FORMATS)
                .setOutputLocale(TEST_OUTPUT_LOCALE)
                .setOutputTimeZone(TEST_OUTPUT_TIMEZONE)
                .setRepositoryDestination(TEST_REPOSITORY_DESTINATION)
                .setMailNotification(TEST_MAIL_NOTIFICATION)
                .setAlert(TEST_ALERT);
        ClientReportJobModel instance = new ClientReportJobModel(clientReportJob);
        assertEquals(TEST_SOURCE, instance.getSource());
        assertEquals(TEST_TRIGGER, instance.getTrigger());
        assertEquals(TEST_MAIL_NOTIFICATION, instance.getMailNotification());
        assertEquals(TEST_ALERT, instance.getAlert());
        assertEquals(TEST_REPOSITORY_DESTINATION, instance.getRepositoryDestination());
        assertEquals(TEST_DESCRIPTION, instance.getDescription());
        assertEquals(TEST_CREATION_DATE, instance.getCreationDate());
        assertEquals(TEST_LABEL, instance.getLabel());
        assertEquals(TEST_BASE_OUTPUT_FILENAME, instance.getBaseOutputFilename());
        assertEquals(TEST_OUTPUT_FORMATS, instance.getOutputFormats());
        assertEquals(TEST_USERNAME, instance.getUsername());
        assertEquals(TEST_OUTPUT_LOCALE, instance.getOutputLocale());
    }

    @Override
    protected List<ClientReportJobModel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setSource(TEST_SOURCE_1),
                createFullyConfiguredInstance().setTrigger(TEST_TRIGGER_1),
                createFullyConfiguredInstance().setMailNotification(TEST_MAIL_NOTIFICATION_1),
                createFullyConfiguredInstance().setAlert(TEST_ALERT_1),
                createFullyConfiguredInstance().setRepositoryDestination(TEST_REPOSITORY_DESTINATION_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setBaseOutputFilename(TEST_BASE_OUTPUT_FILENAME_1),
                createFullyConfiguredInstance().setOutputFormats(TEST_OUTPUT_FORMATS_1),
                createFullyConfiguredInstance().setUsername(TEST_USERNAME_1),
                createFullyConfiguredInstance().setOutputLocale(TEST_OUTPUT_LOCALE_1),
                // null values
                createFullyConfiguredInstance().setSource(null),
                createFullyConfiguredInstance().setTrigger(null),
                createFullyConfiguredInstance().setMailNotification(null),
                createFullyConfiguredInstance().setAlert(null),
                createFullyConfiguredInstance().setRepositoryDestination(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setBaseOutputFilename(null),
                createFullyConfiguredInstance().setOutputFormats(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setOutputLocale(null)
        );
    }

    @Override
    protected ClientReportJobModel createFullyConfiguredInstance() {
        // parent properties
        ClientReportJobModel instance = createInstanceWithDefaultParameters()
                .setSource(TEST_SOURCE)
                .setTrigger(TEST_TRIGGER)
                .setMailNotification(TEST_MAIL_NOTIFICATION)
                .setAlert(TEST_ALERT)
                .setRepositoryDestination(TEST_REPOSITORY_DESTINATION)
                .setDescription(TEST_DESCRIPTION)
                .setCreationDate(TEST_CREATION_DATE)
                .setLabel(TEST_LABEL)
                .setBaseOutputFilename(TEST_BASE_OUTPUT_FILENAME)
                .setOutputFormats(TEST_OUTPUT_FORMATS)
                .setUsername(TEST_USERNAME)
                .setOutputLocale(TEST_OUTPUT_LOCALE);
        return instance;
    }

    @Override
    protected ClientReportJobModel createInstanceWithDefaultParameters() {
        return new ClientReportJobModel();
    }

    @Override
    protected ClientReportJobModel createInstanceFromOther(ClientReportJobModel other) {
        return new ClientReportJobModel(other);
    }

}
