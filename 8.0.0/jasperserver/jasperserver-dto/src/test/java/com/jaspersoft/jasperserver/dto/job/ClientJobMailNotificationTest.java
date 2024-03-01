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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobMailNotificationTest extends BaseDTOPresentableTest<ClientJobMailNotification> {

    private static final String TEST_BCC_ADDRESS = "TEST_BCC_ADDRESS";
    private static final String TEST_BCC_ADDRESS_1 = "TEST_BCC_ADDRESS_1";
    private static final List<String> TEST_BCC_ADDRESSES = Collections.singletonList(TEST_BCC_ADDRESS);
    private static final List<String> TEST_BCC_ADDRESSES_1 = Collections.singletonList(TEST_BCC_ADDRESS_1);

    private static final String TEST_CC_ADDRESS = "TEST_CC_ADDRESS";
    private static final String TEST_CC_ADDRESS_1 = "TEST_CC_ADDRESS_1";
    private static final List<String> TEST_CC_ADDRESSES = Collections.singletonList(TEST_CC_ADDRESS);
    private static final List<String> TEST_CC_ADDRESSES_1 = Collections.singletonList(TEST_CC_ADDRESS_1);

    private static final String TEST_TO_ADDRESS = "TEST_TO_ADDRESS";
    private static final String TEST_TO_ADDRESS_1 = "TEST_TO_ADDRESS_1";
    private static final List<String> TEST_TO_ADDRESSES = Collections.singletonList(TEST_TO_ADDRESS);
    private static final List<String> TEST_TO_ADDRESSES_1 = Collections.singletonList(TEST_TO_ADDRESS_1);

    private static final Integer TEST_VERSION = 2;
    private static final Integer TEST_VERSION_1 = 3;

    private static final Long TEST_ID = 100L;
    private static final Long TEST_ID_1 = 101L;

    private static final Boolean TEST_INCLUDING_STACK_TRACE_WHEN_JOB_FAILS = true;
    private static final Boolean TEST_INCLUDING_STACK_TRACE_WHEN_JOB_FAILS_1 = false;

    private static final String TEST_MESSAGE_TEXT = "TEST_MESSAGE_TEXT";
    private static final String TEST_MESSAGE_TEXT_1 = "TEST_MESSAGE_TEXT_1";

    private static final ClientMailNotificationSendType TEST_RESULT_SEND_TYPE = ClientMailNotificationSendType.SEND;
    private static final ClientMailNotificationSendType TEST_RESULT_SEND_TYPE_1 = ClientMailNotificationSendType.SEND_ATTACHMENT;

    private static final Boolean TEST_SKIP_EMPTY_REPORTS = true;
    private static final Boolean TEST_SKIP_EMPTY_REPORTS_1 = false;

    private static final Boolean TEST_SKIP_NOTIFICATION_WHEN_JOB_FAILS = true;
    private static final Boolean TEST_SKIP_NOTIFICATION_WHEN_JOB_FAILS_1 = false;

    private static final String TEST_SUBJECT = "TEST_SUBJECT";
    private static final String TEST_SUBJECT_1 = "TEST_SUBJECT_1";

    private static final String TEST_MESSAGE_TEXT_WHEN_JOB_FAILS = "TEST_MESSAGE_TEXT_WHEN_JOB_FAILS";
    private static final String TEST_MESSAGE_TEXT_WHEN_JOB_FAILS_1 = "TEST_MESSAGE_TEXT_WHEN_JOB_FAILS_1";

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobMailNotification expected, ClientJobMailNotification actual) {
        assertNotSame(expected.getBccAddresses(), actual.getBccAddresses());
        assertNotSame(expected.getCcAddresses(), actual.getCcAddresses());
        assertNotSame(expected.getToAddresses(), actual.getToAddresses());
    }

    @Test
    public void testGetters() {
        ClientJobMailNotification fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getBccAddresses(), TEST_BCC_ADDRESSES);
        assertEquals(fullyConfiguredInstance.getCcAddresses(), TEST_CC_ADDRESSES);
        assertEquals(fullyConfiguredInstance.getToAddresses(), TEST_TO_ADDRESSES);
        assertEquals(fullyConfiguredInstance.getVersion(), TEST_VERSION);
        assertEquals(fullyConfiguredInstance.getId(), TEST_ID);
        assertEquals(fullyConfiguredInstance.isIncludingStackTraceWhenJobFails(), TEST_INCLUDING_STACK_TRACE_WHEN_JOB_FAILS);
        assertEquals(fullyConfiguredInstance.getMessageText(), TEST_MESSAGE_TEXT);
        assertEquals(fullyConfiguredInstance.getResultSendType(), TEST_RESULT_SEND_TYPE);
        assertEquals(fullyConfiguredInstance.isSkipEmptyReports(), TEST_SKIP_EMPTY_REPORTS);
        assertEquals(fullyConfiguredInstance.isSkipNotificationWhenJobFails(), TEST_SKIP_NOTIFICATION_WHEN_JOB_FAILS);
        assertEquals(fullyConfiguredInstance.getSubject(), TEST_SUBJECT);
        assertEquals(fullyConfiguredInstance.getMessageTextWhenJobFails(), TEST_MESSAGE_TEXT_WHEN_JOB_FAILS);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobMailNotification> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setBccAddresses(TEST_BCC_ADDRESSES_1),
                createFullyConfiguredInstance().setCcAddresses(TEST_CC_ADDRESSES_1),
                createFullyConfiguredInstance().setToAddresses(TEST_TO_ADDRESSES_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setIncludingStackTraceWhenJobFails(TEST_INCLUDING_STACK_TRACE_WHEN_JOB_FAILS_1),
                createFullyConfiguredInstance().setMessageText(TEST_MESSAGE_TEXT_1),
                createFullyConfiguredInstance().setResultSendType(TEST_RESULT_SEND_TYPE_1),
                createFullyConfiguredInstance().setSkipEmptyReports(TEST_SKIP_EMPTY_REPORTS_1),
                createFullyConfiguredInstance().setSkipNotificationWhenJobFails(TEST_SKIP_NOTIFICATION_WHEN_JOB_FAILS_1),
                createFullyConfiguredInstance().setSubject(TEST_SUBJECT_1),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(TEST_MESSAGE_TEXT_WHEN_JOB_FAILS_1),
                // null values
                createFullyConfiguredInstance().setBccAddresses(null),
                createFullyConfiguredInstance().setCcAddresses(null),
                createFullyConfiguredInstance().setToAddresses(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setIncludingStackTraceWhenJobFails(null),
                createFullyConfiguredInstance().setMessageText(null),
                createFullyConfiguredInstance().setResultSendType(null),
                createFullyConfiguredInstance().setSkipEmptyReports(null),
                createFullyConfiguredInstance().setSkipNotificationWhenJobFails(null),
                createFullyConfiguredInstance().setSubject(null),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(null)
        );
    }

    @Override
    protected ClientJobMailNotification createFullyConfiguredInstance() {
        return new ClientJobMailNotification()
                .setBccAddresses(TEST_BCC_ADDRESSES)
                .setCcAddresses(TEST_CC_ADDRESSES)
                .setToAddresses(TEST_TO_ADDRESSES)
                .setVersion(TEST_VERSION)
                .setId(TEST_ID)
                .setIncludingStackTraceWhenJobFails(TEST_INCLUDING_STACK_TRACE_WHEN_JOB_FAILS)
                .setMessageText(TEST_MESSAGE_TEXT)
                .setResultSendType(TEST_RESULT_SEND_TYPE)
                .setSkipEmptyReports(TEST_SKIP_EMPTY_REPORTS)
                .setSkipNotificationWhenJobFails(TEST_SKIP_NOTIFICATION_WHEN_JOB_FAILS)
                .setSubject(TEST_SUBJECT)
                .setMessageTextWhenJobFails(TEST_MESSAGE_TEXT_WHEN_JOB_FAILS);
    }

    @Override
    protected ClientJobMailNotification createInstanceWithDefaultParameters() {
        return new ClientJobMailNotification();
    }

    @Override
    protected ClientJobMailNotification createInstanceFromOther(ClientJobMailNotification other) {
        return new ClientJobMailNotification(other);
    }

}
