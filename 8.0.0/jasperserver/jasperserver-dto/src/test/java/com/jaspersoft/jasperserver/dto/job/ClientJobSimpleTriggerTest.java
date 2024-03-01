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

import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_NOW;
import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_SCHEDULE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobSimpleTriggerTest extends BaseDTOTest<ClientJobSimpleTrigger> {

    private static final Integer TEST_OCCURENCE_COUNT = 100;
    private static final Integer TEST_OCCURENCE_COUNT_1 = 1001;

    private static final Integer TEST_RECCURENCE_INTERVAL = 101;
    private static final Integer TEST_RECCURENCE_INTERVAL_1 = 1011;

    private static final ClientIntervalUnitType TEST_RECCURENCE_INTERVAL_UNIT = ClientIntervalUnitType.HOUR;
    private static final ClientIntervalUnitType TEST_RECCURENCE_INTERVAL_UNIT_1 = ClientIntervalUnitType.DAY;

    private static final Long TEST_ID = 100L;
    private static final Long TEST_ID_1 = 1001L;

    private static final Integer TEST_VERSION = 2;
    private static final Integer TEST_VERSION_1 = 3;

    private static final String TEST_TIMEZONE = "Europe/Dublin";
    private static final String TEST_TIMEZONE_1 = "Africa/Lagos";

    private static final String TEST_TIME = "2010-05-23T09:01:02";
    private static final String TEST_TIME_1 = "2014-07-21T09:01:02";

    private static final String TEST_CALENDAR_NAME = "TEST_CALENDAR_NAME";
    private static final String TEST_CALENDAR_NAME_1 = "TEST_CALENDAR_NAME_1";

    private static final Date TEST_START_DATE = TestsValuesProvider.provideTestDate(TEST_TIMEZONE, TEST_TIME);
    private static final Date TEST_START_DATE_1 = TestsValuesProvider.provideTestDate(TEST_TIMEZONE_1, TEST_TIME_1);

    private static final Date TEST_END_DATE = TestsValuesProvider.provideTestDate(TEST_TIMEZONE, TEST_TIME);
    private static final Date TEST_END_DATE_1 = TestsValuesProvider.provideTestDate(TEST_TIMEZONE_1, TEST_TIME_1);

    private static final Integer TEST_MISFIRE_INSTRUCTION = 0;
    private static final Integer TEST_MISFIRE_INSTRUCTION_1 = 1;

    @Test
    public void testGetters() {
        ClientJobSimpleTrigger fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getOccurrenceCount(), TEST_OCCURENCE_COUNT);
        assertEquals(fullyConfiguredInstance.getRecurrenceInterval(), TEST_RECCURENCE_INTERVAL);
        assertEquals(fullyConfiguredInstance.getRecurrenceIntervalUnit(), TEST_RECCURENCE_INTERVAL_UNIT);
        assertEquals(fullyConfiguredInstance.getId(), TEST_ID);
        assertEquals(fullyConfiguredInstance.getVersion(), TEST_VERSION);
        assertEquals(fullyConfiguredInstance.getTimezone(), TEST_TIMEZONE);
        assertEquals(fullyConfiguredInstance.getCalendarName(), TEST_CALENDAR_NAME);
        assertEquals(fullyConfiguredInstance.getStartDate(), TEST_START_DATE);
        assertEquals(fullyConfiguredInstance.getStartType(), START_TYPE_NOW);
        assertEquals(fullyConfiguredInstance.getEndDate(), TEST_END_DATE);
        assertEquals(fullyConfiguredInstance.getMisfireInstruction(), TEST_MISFIRE_INSTRUCTION);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobSimpleTrigger> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setOccurrenceCount(TEST_OCCURENCE_COUNT_1),
                createFullyConfiguredInstance().setRecurrenceInterval(TEST_RECCURENCE_INTERVAL_1),
                createFullyConfiguredInstance().setRecurrenceIntervalUnit(TEST_RECCURENCE_INTERVAL_UNIT_1),
                // parent fields
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setId(TEST_ID_1),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setTimezone(TEST_TIMEZONE_1),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setCalendarName(TEST_CALENDAR_NAME_1),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setStartDate(TEST_START_DATE_1),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setStartType(START_TYPE_SCHEDULE),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setEndDate(TEST_END_DATE_1),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setMisfireInstruction(TEST_MISFIRE_INSTRUCTION_1),
                // null values
                createFullyConfiguredInstance().setOccurrenceCount(null),
                createFullyConfiguredInstance().setRecurrenceInterval(null),
                createFullyConfiguredInstance().setRecurrenceIntervalUnit(null),
                // parent fields
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setId(null),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setVersion(null),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setTimezone(null),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setCalendarName(null),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setStartDate(null),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setStartType(0),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setEndDate(null),
                (ClientJobSimpleTrigger) createFullyConfiguredInstance().setMisfireInstruction(null)
        );
    }

    @Override
    protected ClientJobSimpleTrigger createFullyConfiguredInstance() {
        ClientJobSimpleTrigger instance = new ClientJobSimpleTrigger()
                .setOccurrenceCount(TEST_OCCURENCE_COUNT)
                .setRecurrenceInterval(TEST_RECCURENCE_INTERVAL)
                .setRecurrenceIntervalUnit(TEST_RECCURENCE_INTERVAL_UNIT);
        // parent fields
        return (ClientJobSimpleTrigger)instance
                .setId(TEST_ID)
                .setVersion(TEST_VERSION)
                .setTimezone(TEST_TIMEZONE)
                .setCalendarName(TEST_CALENDAR_NAME)
                .setStartDate(TEST_START_DATE)
                .setStartType(START_TYPE_NOW)
                .setEndDate(TEST_END_DATE)
                .setMisfireInstruction(TEST_MISFIRE_INSTRUCTION);
    }

    @Override
    protected ClientJobSimpleTrigger createInstanceWithDefaultParameters() {
        return new ClientJobSimpleTrigger();
    }

    @Override
    protected ClientJobSimpleTrigger createInstanceFromOther(ClientJobSimpleTrigger other) {
        return new ClientJobSimpleTrigger(other);
    }

}
