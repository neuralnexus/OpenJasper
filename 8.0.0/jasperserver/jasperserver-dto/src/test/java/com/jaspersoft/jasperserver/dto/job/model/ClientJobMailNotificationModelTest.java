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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import com.jaspersoft.jasperserver.dto.job.ClientMailNotificationSendType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobMailNotificationModelTest extends BaseDTOTest<ClientJobMailNotificationModel> {

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
    protected List<ClientJobMailNotificationModel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                //flags on
                createFullyConfiguredInstance().setBccAddresses(TEST_BCC_ADDRESSES_1),
                createFullyConfiguredInstance().setCcAddresses(TEST_CC_ADDRESSES_1),
                createFullyConfiguredInstance().setIncludingStackTraceWhenJobFails(TEST_INCLUDING_STACK_TRACE_WHEN_JOB_FAILS_1),
                createFullyConfiguredInstance().setMessageText(TEST_MESSAGE_TEXT_1),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(TEST_MESSAGE_TEXT_WHEN_JOB_FAILS_1),
                createFullyConfiguredInstance().setResultSendType(TEST_RESULT_SEND_TYPE_1),
                createFullyConfiguredInstance().setSkipEmptyReports(TEST_SKIP_EMPTY_REPORTS_1),
                createFullyConfiguredInstance().setSkipNotificationWhenJobFails(TEST_SKIP_NOTIFICATION_WHEN_JOB_FAILS_1),
                createFullyConfiguredInstance().setSubject(TEST_SUBJECT_1),
                createFullyConfiguredInstance().setToAddresses(TEST_TO_ADDRESSES_1),
                // null values
                createFullyConfiguredInstance().setBccAddresses(null),
                createFullyConfiguredInstance().setCcAddresses(null),
                createFullyConfiguredInstance().setIncludingStackTraceWhenJobFails(null),
                createFullyConfiguredInstance().setMessageText(null),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(null),
                createFullyConfiguredInstance().setResultSendType(null),
                createFullyConfiguredInstance().setSkipEmptyReports(null),
                createFullyConfiguredInstance().setSkipNotificationWhenJobFails(null),
                createFullyConfiguredInstance().setSubject(null),
                createFullyConfiguredInstance().setToAddresses(null)
        );
    }

    @Override
    protected ClientJobMailNotificationModel createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setBccAddresses(TEST_BCC_ADDRESSES)
                .setCcAddresses(TEST_CC_ADDRESSES)
                .setIncludingStackTraceWhenJobFails(TEST_INCLUDING_STACK_TRACE_WHEN_JOB_FAILS)
                .setMessageText(TEST_MESSAGE_TEXT)
                .setMessageTextWhenJobFails(TEST_MESSAGE_TEXT_WHEN_JOB_FAILS)
                .setResultSendType(TEST_RESULT_SEND_TYPE)
                .setSkipEmptyReports(TEST_SKIP_EMPTY_REPORTS)
                .setSkipNotificationWhenJobFails(TEST_SKIP_NOTIFICATION_WHEN_JOB_FAILS)
                .setSubject(TEST_SUBJECT)
                .setToAddresses(TEST_TO_ADDRESSES);
    }

    @Override
    protected ClientJobMailNotificationModel createInstanceWithDefaultParameters() {
        return new ClientJobMailNotificationModel();
    }

    @Override
    protected ClientJobMailNotificationModel createInstanceFromOther(ClientJobMailNotificationModel other) {
        return new ClientJobMailNotificationModel(other);
    }

}
