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

package com.jaspersoft.jasperserver.dto.job;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.utils.TestsValuesProvider;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientReportJobTest extends BaseDTOTest<ClientReportJob> {

    private static final Long TEST_ID = 100L;
    private static final Long TEST_ID_1 = 1001L;

    private static final Integer TEST_VERSION = 2;
    private static final Integer TEST_VERSION_1 = 3;

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

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

    private static final String TEST_OUTPUT_TIMEZONE = "TEST_OUTPUT_TIMEZONE";
    private static final String TEST_OUTPUT_TIMEZONE_1 = "TEST_OUTPUT_TIMEZONE_1";

    private static final ClientJobRepositoryDestination TEST_REPOSITORY_DESTINATION = new ClientJobRepositoryDestination().setOverwriteFiles(true);
    private static final ClientJobRepositoryDestination TEST_REPOSITORY_DESTINATION_1 = new ClientJobRepositoryDestination().setOverwriteFiles(false);

    private static final ClientJobMailNotification TEST_MAIL_NOTIFICATION = new ClientJobMailNotification().setSkipNotificationWhenJobFails(true);
    private static final ClientJobMailNotification TEST_MAIL_NOTIFICATION_1 = new ClientJobMailNotification().setSkipNotificationWhenJobFails(false);

    private static final ClientJobAlert TEST_ALERT = new ClientJobAlert().setIncludingStackTrace(true);
    private static final ClientJobAlert TEST_ALERT_1 = new ClientJobAlert().setIncludingStackTrace(false);

    private static final String TEST_TIMEZONE_ID = "Europe/Dublin";
    private static final String TEST_TIMEZONE_ID_1 = "Africa/Lagos";

    private static final String TEST_TIME = "2010-05-23T09:01:02";
    private static final String TEST_TIME_1 = "2014-07-21T09:01:02";

    private static final Timestamp TEST_CREATION_DATE = new Timestamp(TestsValuesProvider.provideTestDate(TEST_TIMEZONE_ID, TEST_TIME).getTime());
    private static final Timestamp TEST_CREATION_DATE_1 = new Timestamp(TestsValuesProvider.provideTestDate(TEST_TIMEZONE_ID_1, TEST_TIME_1).getTime());

    @Test
    public void testGetters() {
        ClientReportJob fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getId(), TEST_ID);
        assertEquals(fullyConfiguredInstance.getVersion(), TEST_VERSION);
        assertEquals(fullyConfiguredInstance.getUsername(), TEST_USERNAME);
        assertEquals(fullyConfiguredInstance.getLabel(), TEST_LABEL);
        assertEquals(fullyConfiguredInstance.getDescription(), TEST_DESCRIPTION);
        assertEquals(fullyConfiguredInstance.getCreationDate(), TEST_CREATION_DATE);
        assertEquals(fullyConfiguredInstance.getTrigger(), TEST_TRIGGER);
        assertEquals(fullyConfiguredInstance.getSource(), TEST_SOURCE);
        assertEquals(fullyConfiguredInstance.getBaseOutputFilename(), TEST_BASE_OUTPUT_FILENAME);
        assertEquals(fullyConfiguredInstance.getOutputFormats(), TEST_OUTPUT_FORMATS);
        assertEquals(fullyConfiguredInstance.getOutputLocale(), TEST_OUTPUT_LOCALE);
        assertEquals(fullyConfiguredInstance.getOutputTimeZone(), TEST_OUTPUT_TIMEZONE);
        assertEquals(fullyConfiguredInstance.getRepositoryDestination(), TEST_REPOSITORY_DESTINATION);
        assertEquals(fullyConfiguredInstance.getMailNotification(), TEST_MAIL_NOTIFICATION);
        assertEquals(fullyConfiguredInstance.getAlert(), TEST_ALERT);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientReportJob> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setUsername(TEST_USERNAME_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setTrigger(TEST_TRIGGER_1),
                createFullyConfiguredInstance().setSource(TEST_SOURCE_1),
                createFullyConfiguredInstance().setBaseOutputFilename(TEST_BASE_OUTPUT_FILENAME_1),
                createFullyConfiguredInstance().setOutputFormats(TEST_OUTPUT_FORMATS_1),
                createFullyConfiguredInstance().setOutputLocale(TEST_OUTPUT_LOCALE_1),
                createFullyConfiguredInstance().setOutputTimeZone(TEST_OUTPUT_TIMEZONE_1),
                createFullyConfiguredInstance().setRepositoryDestination(TEST_REPOSITORY_DESTINATION_1),
                createFullyConfiguredInstance().setMailNotification(TEST_MAIL_NOTIFICATION_1),
                createFullyConfiguredInstance().setAlert(TEST_ALERT_1),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setTrigger(null),
                createFullyConfiguredInstance().setSource(null),
                createFullyConfiguredInstance().setBaseOutputFilename(null),
                createFullyConfiguredInstance().setOutputFormats(null),
                createFullyConfiguredInstance().setOutputLocale(null),
                createFullyConfiguredInstance().setOutputTimeZone(null),
                createFullyConfiguredInstance().setRepositoryDestination(null),
                createFullyConfiguredInstance().setMailNotification(null),
                createFullyConfiguredInstance().setAlert(null)
        );
    }

    @Override
    protected ClientReportJob createFullyConfiguredInstance() {
        return new ClientReportJob()
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
    }

    @Override
    protected ClientReportJob createInstanceWithDefaultParameters() {
        return new ClientReportJob();
    }

    @Override
    protected ClientReportJob createInstanceFromOther(ClientReportJob other) {
        return new ClientReportJob(other);
    }

}
