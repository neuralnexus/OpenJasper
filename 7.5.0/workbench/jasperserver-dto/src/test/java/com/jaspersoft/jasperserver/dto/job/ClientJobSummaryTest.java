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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobSummaryTest extends BaseDTOPresentableTest<ClientJobSummary> {

    private static final Long TEST_ID = 100L;
    private static final Long TEST_ID_1 = 1000L;

    private static final Long TEST_VERSION = 101L;
    private static final Long TEST_VERSION_1 = 1010L;

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_REPORT_UNIT_URI = "TEST_REPORT_UNIT_URI";
    private static final String TEST_REPORT_UNIT_URI_1 = "TEST_REPORT_UNIT_URI_1";

    private static final String TEST_REPORT_LABEL = "TEST_REPORT_LABEL";
    private static final String TEST_REPORT_LABEL_1 = "TEST_REPORT_LABEL_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

    private static final ClientJobState TEST_STATE = new ClientJobState();
    private static final ClientJobState TEST_STATE_1 = new ClientJobState().setNextFireTime(new Date());


    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobSummary expected, ClientJobSummary actual) {
        assertNotSame(expected.getState(), actual.getState());
    }

    @Test
    public void testGetters() {
        ClientJobSummary fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getId(), TEST_ID);
        assertEquals(fullyConfiguredInstance.getVersion(), TEST_VERSION);
        assertEquals(fullyConfiguredInstance.getLabel(), TEST_LABEL);
        assertEquals(fullyConfiguredInstance.getReportUnitURI(), TEST_REPORT_UNIT_URI);
        assertEquals(fullyConfiguredInstance.getReportLabel(), TEST_REPORT_LABEL);
        assertEquals(fullyConfiguredInstance.getUsername(), TEST_USERNAME);
        assertEquals(fullyConfiguredInstance.getDescription(), TEST_DESCRIPTION);
        assertEquals(fullyConfiguredInstance.getState(), TEST_STATE);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobSummary> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setReportUnitURI(TEST_REPORT_UNIT_URI_1),
                createFullyConfiguredInstance().setReportLabel(TEST_REPORT_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUsername(TEST_USERNAME_1),
                createFullyConfiguredInstance().setState(TEST_STATE_1),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setReportUnitURI(null),
                createFullyConfiguredInstance().setReportLabel(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setState(null)
        );
    }

    @Override
    protected ClientJobSummary createFullyConfiguredInstance() {
        return new ClientJobSummary()
                .setId(TEST_ID)
                .setVersion(TEST_VERSION)
                .setLabel(TEST_LABEL)
                .setReportUnitURI(TEST_REPORT_UNIT_URI)
                .setReportLabel(TEST_REPORT_LABEL)
                .setUsername(TEST_USERNAME)
                .setDescription(TEST_DESCRIPTION)
                .setState(TEST_STATE);
    }

    @Override
    protected ClientJobSummary createInstanceWithDefaultParameters() {
        return new ClientJobSummary();
    }

    @Override
    protected ClientJobSummary createInstanceFromOther(ClientJobSummary other) {
        return new ClientJobSummary(other);
    }
}
