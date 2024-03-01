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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import com.jaspersoft.jasperserver.dto.utils.TestsValuesProvider;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobStateTest extends BaseDTOTest<ClientJobState> {

    private static final String TEST_TIMEZONE = "Europe/Dublin";
    private static final String TEST_TIMEZONE_1 = "Africa/Lagos";

    private static final String TEST_TIME = "2010-05-23T09:01:02";
    private static final String TEST_TIME_1 = "2014-07-21T09:01:02";

    private static final Date TEST_PREVIOUS_FIRE_TIME = TestsValuesProvider.provideTestDate(TEST_TIMEZONE, TEST_TIME);
    private static final Date TEST_PREVIOUS_FIRE_TIME_1 = TestsValuesProvider.provideTestDate(TEST_TIMEZONE_1, TEST_TIME_1);

    private static final Date TEST_NEXT_FIRE_TIME = TestsValuesProvider.provideTestDate(TEST_TIMEZONE, TEST_TIME);
    private static final Date TEST_NEXT_FIRE_TIME_1 = TestsValuesProvider.provideTestDate(TEST_TIMEZONE_1, TEST_TIME_1);

    private static final ClientJobStateType TEST_STATE = ClientJobStateType.COMPLETE;
    private static final ClientJobStateType TEST_STATE_1 = ClientJobStateType.EXECUTING;

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobState expected, ClientJobState actual) {
        assertNotSame(expected.getPreviousFireTime(), actual.getPreviousFireTime());
        assertNotSame(expected.getNextFireTime(), actual.getNextFireTime());
    }

    @Test
    public void testGetters() {
        ClientJobState fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getPreviousFireTime(), TEST_PREVIOUS_FIRE_TIME);
        assertEquals(fullyConfiguredInstance.getNextFireTime(), TEST_NEXT_FIRE_TIME);
        assertEquals(fullyConfiguredInstance.getState(), TEST_STATE);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobState> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setPreviousFireTime(TEST_PREVIOUS_FIRE_TIME_1),
                createFullyConfiguredInstance().setNextFireTime(TEST_NEXT_FIRE_TIME_1),
                createFullyConfiguredInstance().setState(TEST_STATE_1),
                // null values
                createFullyConfiguredInstance().setPreviousFireTime(null),
                createFullyConfiguredInstance().setNextFireTime(null),
                createFullyConfiguredInstance().setState(null)
        );
    }

    @Override
    protected ClientJobState createFullyConfiguredInstance() {
        return new ClientJobState()
                .setPreviousFireTime(TEST_PREVIOUS_FIRE_TIME)
                .setNextFireTime(TEST_NEXT_FIRE_TIME)
                .setState(TEST_STATE);
    }

    @Override
    protected ClientJobState createInstanceWithDefaultParameters() {
        return new ClientJobState();
    }

    @Override
    protected ClientJobState createInstanceFromOther(ClientJobState other) {
        return new ClientJobState(other);
    }

}
