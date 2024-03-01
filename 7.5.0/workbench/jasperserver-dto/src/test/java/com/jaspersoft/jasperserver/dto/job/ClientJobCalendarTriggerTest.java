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
import com.jaspersoft.jasperserver.dto.utils.TestsValuesProvider;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_NOW;
import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_SCHEDULE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobCalendarTriggerTest extends BaseDTOTest<ClientJobCalendarTrigger> {

    private static final String TEST_MINUTES = "TEST_MINUTES";
    private static final String TEST_MINUTES_1 = "TEST_MINUTES_1";

    private static final String TEST_HOURS = "TEST_HOURS";
    private static final String TEST_HOURS_1 = "TEST_HOURS_1";

    private static final ClientCalendarDaysType TEST_DAYS_TYPE = ClientCalendarDaysType.ALL;
    private static final ClientCalendarDaysType TEST_DAYS_TYPE_1 = ClientCalendarDaysType.MONTH;

    private static final SortedSet<Byte> TEST_WEEK_DAYS = new TreeSet<Byte>(
            Collections.singleton(
                    Byte.valueOf("1")
            )
    );
    private static final SortedSet<Byte> TEST_WEEK_DAYS_1 = new TreeSet<Byte>(
            Collections.singleton(
                    Byte.valueOf("2")
            )
    );

    private static final String TEST_MONTH_DAYS = "TEST_MONTH_DAYS";
    private static final String TEST_MONTH_DAYS_1 = "TEST_MONTH_DAYS_1";

    private static final SortedSet<Byte> TEST_MONTHS = new TreeSet<Byte>(
            Collections.singleton(
                    Byte.valueOf("1")
            )
    );
    private static final SortedSet<Byte> TEST_MONTHS_1 = new TreeSet<Byte>(
            Collections.singleton(
                    Byte.valueOf("2")
            )
    );
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

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobCalendarTrigger expected, ClientJobCalendarTrigger actual) {
        assertNotSame(expected.getWeekDays(), actual.getWeekDays());
        assertNotSame(expected.getMonths(), actual.getMonths());
        assertNotSame(expected.getStartDate(), actual.getStartDate());
        assertNotSame(expected.getEndDate(), actual.getEndDate());
    }

    @Test
    public void testGetters() {
        ClientJobCalendarTrigger fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getMinutes(), TEST_MINUTES);
        assertEquals(fullyConfiguredInstance.getHours(), TEST_HOURS);
        assertEquals(fullyConfiguredInstance.getDaysType(), TEST_DAYS_TYPE);
        assertEquals(fullyConfiguredInstance.getWeekDays(), TEST_WEEK_DAYS);
        assertEquals(fullyConfiguredInstance.getMonthDays(), TEST_MONTH_DAYS);
        assertEquals(fullyConfiguredInstance.getMonths(), TEST_MONTHS);
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
    protected List<ClientJobCalendarTrigger> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setMinutes(TEST_MINUTES_1),
                createFullyConfiguredInstance().setHours(TEST_HOURS_1),
                createFullyConfiguredInstance().setDaysType(TEST_DAYS_TYPE_1),
                createFullyConfiguredInstance().setWeekDays(TEST_WEEK_DAYS_1),
                createFullyConfiguredInstance().setMonthDays(TEST_MONTH_DAYS_1),
                createFullyConfiguredInstance().setMonths(TEST_MONTHS_1),
                // parent fields
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setId(TEST_ID_1),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setTimezone(TEST_TIMEZONE_1),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setCalendarName(TEST_CALENDAR_NAME_1),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setStartDate(TEST_START_DATE_1),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setStartType(START_TYPE_SCHEDULE),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setEndDate(TEST_END_DATE_1),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setMisfireInstruction(TEST_MISFIRE_INSTRUCTION_1),
                // null values
                createFullyConfiguredInstance().setMinutes(null),
                createFullyConfiguredInstance().setHours(null),
                createFullyConfiguredInstance().setDaysType(null),
                createFullyConfiguredInstance().setWeekDays(null),
                createFullyConfiguredInstance().setMonthDays(null),
                createFullyConfiguredInstance().setMonths(null),
                // parent fields
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setId(null),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setVersion(null),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setTimezone(null),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setCalendarName(null),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setStartDate(null),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setStartType(0),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setEndDate(null),
                (ClientJobCalendarTrigger) createFullyConfiguredInstance().setMisfireInstruction(null)
        );
    }

    @Override
    protected ClientJobCalendarTrigger createFullyConfiguredInstance() {
        ClientJobCalendarTrigger instance = new ClientJobCalendarTrigger()
                .setMinutes(TEST_MINUTES)
                .setHours(TEST_HOURS)
                .setDaysType(TEST_DAYS_TYPE)
                .setWeekDays(TEST_WEEK_DAYS)
                .setMonthDays(TEST_MONTH_DAYS)
                .setMonths(TEST_MONTHS);
        // Parent class's fields
        return (ClientJobCalendarTrigger)instance
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
    protected ClientJobCalendarTrigger createInstanceWithDefaultParameters() {
        return new ClientJobCalendarTrigger();
    }

    @Override
    protected ClientJobCalendarTrigger createInstanceFromOther(ClientJobCalendarTrigger other) {
        return new ClientJobCalendarTrigger(other);
    }

}
