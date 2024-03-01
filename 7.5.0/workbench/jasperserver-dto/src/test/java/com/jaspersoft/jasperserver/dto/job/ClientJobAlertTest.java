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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobAlertTest extends BaseDTOPresentableTest<ClientJobAlert> {

    private static final Long TEST_ID = 100L;
    private static final Long TEST_ID_1 = 101L;

    private static final Integer TEST_VERSION = 2;
    private static final Integer TEST_VERSION_1 = 3;

    private static final ClientJobAlertRecipient TEST_RECIPIENT = ClientJobAlertRecipient.ADMIN;
    private static final ClientJobAlertRecipient TEST_RECIPIENT_1 = ClientJobAlertRecipient.OWNER;

    private static final ClientJobAlertState TEST_JOB_STATE = ClientJobAlertState.ALL;
    private static final ClientJobAlertState TEST_JOB_STATE_1 = ClientJobAlertState.FAIL_ONLY;

    private static final String TEST_MESSAGE_TEXT = "TEST_MESSAGE_TEXT";
    private static final String TEST_MESSAGE_TEXT_1 = "TEST_MESSAGE_TEXT_1";

    private static final String TEST_MESSAGE_TEXT_WHEN_JOB_FAILS = "TEST_MESSAGE_TEXT_WHEN_JOB_FAILS";
    private static final String TEST_MESSAGE_TEXT_WHEN_JOB_FAILS_1 = "TEST_MESSAGE_TEXT_WHEN_JOB_FAILS_1";

    private static final String TEST_SUBJECT = "TEST_SUBJECT";
    private static final String TEST_SUBJECT_1 = "TEST_SUBJECT_1";

    private static final Boolean TEST_INCLUDING_STACK_TRACE = true;
    private static final Boolean TEST_INCLUDING_STACK_TRACE_1 = false;

    private static final Boolean TEST_INCLUDING_REPORT_JOB_INFO = true;
    private static final Boolean TEST_INCLUDING_REPORT_JOB_INFO_1 = false;

    private static final String TEST_TO_ADDRESS = "TEST_TO_ADDRESS";
    private static final String TEST_TO_ADDRESS_1 = "TEST_TO_ADDRESS_1";

    private static final List<String> TEST_TO_ADDRESSES = Collections.singletonList(TEST_TO_ADDRESS);
    private static final List<String> TEST_TO_ADDRESSES_1 = Collections.singletonList(TEST_TO_ADDRESS_1);

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobAlert expected, ClientJobAlert actual) {
        assertNotSame(expected.getToAddresses(), actual.getToAddresses());
    }

    @Test
    public void testGetters() {
        ClientJobAlert fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getId(), TEST_ID);
        assertEquals(fullyConfiguredInstance.getVersion(), TEST_VERSION);
        assertEquals(fullyConfiguredInstance.getRecipient(), TEST_RECIPIENT);
        assertEquals(fullyConfiguredInstance.getJobState(), TEST_JOB_STATE);
        assertEquals(fullyConfiguredInstance.getMessageText(), TEST_MESSAGE_TEXT);
        assertEquals(fullyConfiguredInstance.getMessageTextWhenJobFails(), TEST_MESSAGE_TEXT_WHEN_JOB_FAILS);
        assertEquals(fullyConfiguredInstance.getSubject(), TEST_SUBJECT);
        assertEquals(fullyConfiguredInstance.isIncludingStackTrace(), TEST_INCLUDING_STACK_TRACE);
        assertEquals(fullyConfiguredInstance.isIncludingReportJobInfo(), TEST_INCLUDING_REPORT_JOB_INFO);
        assertEquals(fullyConfiguredInstance.getToAddresses(), TEST_TO_ADDRESSES);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobAlert> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setRecipient(TEST_RECIPIENT_1),
                createFullyConfiguredInstance().setJobState(TEST_JOB_STATE_1),
                createFullyConfiguredInstance().setMessageText(TEST_MESSAGE_TEXT_1),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(TEST_MESSAGE_TEXT_WHEN_JOB_FAILS_1),
                createFullyConfiguredInstance().setSubject(TEST_SUBJECT_1),
                createFullyConfiguredInstance().setIncludingStackTrace(TEST_INCLUDING_STACK_TRACE_1),
                createFullyConfiguredInstance().setIncludingReportJobInfo(TEST_INCLUDING_REPORT_JOB_INFO_1),
                createFullyConfiguredInstance().setToAddresses(TEST_TO_ADDRESSES_1),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setRecipient(null),
                createFullyConfiguredInstance().setJobState(null),
                createFullyConfiguredInstance().setMessageText(null),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(null),
                createFullyConfiguredInstance().setSubject(null),
                createFullyConfiguredInstance().setIncludingStackTrace(null),
                createFullyConfiguredInstance().setIncludingReportJobInfo(null),
                createFullyConfiguredInstance().setToAddresses(null)
        );
    }

    @Override
    protected ClientJobAlert createFullyConfiguredInstance() {
        return new ClientJobAlert()
                .setId(TEST_ID)
                .setVersion(TEST_VERSION)
                .setRecipient(TEST_RECIPIENT)
                .setJobState(TEST_JOB_STATE)
                .setMessageText(TEST_MESSAGE_TEXT)
                .setMessageTextWhenJobFails(TEST_MESSAGE_TEXT_WHEN_JOB_FAILS)
                .setSubject(TEST_SUBJECT)
                .setIncludingStackTrace(TEST_INCLUDING_STACK_TRACE)
                .setIncludingReportJobInfo(TEST_INCLUDING_REPORT_JOB_INFO)
                .setToAddresses(TEST_TO_ADDRESSES);
    }

    @Override
    protected ClientJobAlert createInstanceWithDefaultParameters() {
        return new ClientJobAlert();
    }

    @Override
    protected ClientJobAlert createInstanceFromOther(ClientJobAlert other) {
        return new ClientJobAlert(other);
    }
}
