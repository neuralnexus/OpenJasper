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
import com.jaspersoft.jasperserver.dto.job.ClientJobAlertRecipient;
import com.jaspersoft.jasperserver.dto.job.ClientJobAlertState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobAlertModelTest extends BaseDTOTest<ClientJobAlertModel> {
    private static final ClientJobAlertRecipient TEST_RECIPIENT = ClientJobAlertRecipient.ADMIN;
    private static final ClientJobAlertRecipient TEST_RECIPIENT_1 = ClientJobAlertRecipient.OWNER;

    private static final List<String> TEST_TO_ADDRESSES = Collections.singletonList("TEST_ADDRESS");
    private static final List<String> TEST_TO_ADDRESSES_1 = Collections.singletonList("TEST_ADDRESS_1");

    private static final ClientJobAlertState TEST_JOB_STATE = ClientJobAlertState.ALL;
    private static final ClientJobAlertState TEST_JOB_STATE_1 = ClientJobAlertState.NONE;

    private static final String TEST_MESSAGE_TEXT = "TEST_MESSAGE_TEXT";
    private static final String TEST_MESSAGE_TEXT_1 = "TEST_MESSAGE_TEXT_1";

    private static final String TEST_MESSAGE_WHEN_JOB_FAILS = "TEST_MESSAGE_WHEN_JOB_FAILS";
    private static final String TEST_MESSAGE_WHEN_JOB_FAILS_1 = "TEST_MESSAGE_WHEN_JOB_FAILS_1";

    private static final String TEST_SUBJECT = "TEST_SUBJECT";
    private static final String TEST_SUBJECT_1 = "TEST_SUBJECT_1";

    private static final Boolean TEST_INCLUDE_STACK_TRACE = true;
    private static final Boolean TEST_INCLUDE_STACK_TRACE_1 = false;

    private static final Boolean TEST_INCLUDE_REPORT_JOB_INFO = true;
    private static final Boolean TEST_INCLUDE_REPORT_JOB_INFO_1 = false;

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobAlertModel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setRecipient(TEST_RECIPIENT_1),
                createFullyConfiguredInstance().setToAddresses(TEST_TO_ADDRESSES_1),
                createFullyConfiguredInstance().setJobState(TEST_JOB_STATE_1),
                createFullyConfiguredInstance().setMessageText(TEST_MESSAGE_TEXT_1),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(TEST_MESSAGE_WHEN_JOB_FAILS_1),
                createFullyConfiguredInstance().setSubject(TEST_SUBJECT_1),
                createFullyConfiguredInstance().setIncludingStackTrace(TEST_INCLUDE_STACK_TRACE_1),
                createFullyConfiguredInstance().setIncludingReportJobInfo(TEST_INCLUDE_REPORT_JOB_INFO_1),
                // null values
                createFullyConfiguredInstance().setRecipient(null),
                createFullyConfiguredInstance().setToAddresses(null),
                createFullyConfiguredInstance().setJobState(null),
                createFullyConfiguredInstance().setMessageText(null),
                createFullyConfiguredInstance().setMessageTextWhenJobFails(null),
                createFullyConfiguredInstance().setSubject(null),
                createFullyConfiguredInstance().setIncludingStackTrace(null),
                createFullyConfiguredInstance().setIncludingReportJobInfo(null)
        );
    }

    @Override
    protected ClientJobAlertModel createFullyConfiguredInstance() {
        return new ClientJobAlertModel()
                .setRecipient(TEST_RECIPIENT)
                .setToAddresses(TEST_TO_ADDRESSES)
                .setJobState(TEST_JOB_STATE)
                .setMessageText(TEST_MESSAGE_TEXT)
                .setMessageTextWhenJobFails(TEST_MESSAGE_WHEN_JOB_FAILS)
                .setSubject(TEST_SUBJECT)
                .setIncludingStackTrace(TEST_INCLUDE_STACK_TRACE)
                .setIncludingReportJobInfo(TEST_INCLUDE_REPORT_JOB_INFO);
    }

    @Override
    protected ClientJobAlertModel createInstanceWithDefaultParameters() {
        return new ClientJobAlertModel();
    }

    @Override
    protected ClientJobAlertModel createInstanceFromOther(ClientJobAlertModel other) {
        return new ClientJobAlertModel(other);
    }

}
