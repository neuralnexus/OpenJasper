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
import com.jaspersoft.jasperserver.dto.job.ClientCalendarDaysType;
import com.jaspersoft.jasperserver.dto.utils.TestsValuesProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_NOW;
import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_SCHEDULE;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobCalendarTriggerModelTest extends BaseDTOTest<ClientJobCalendarTriggerModel> {

    private static final String TEST_MINUTES = "TEST_MINUTES";
    private static final String TEST_MINUTES_1 = "TEST_MINUTES_1";

    private static final String TEST_HOURS = "TEST_HOURS";
    private static final String TEST_HOURS_1 = "TEST_HOURS_1";

    private static final ClientCalendarDaysType TEST_DAYS_TYPE = ClientCalendarDaysType.ALL;
    private static final ClientCalendarDaysType TEST_DAYS_TYPE_1 = ClientCalendarDaysType.MONTH;

    private static final String TEST_MONTH_DAYS = "TEST_MONTH_DAYS";
    private static final String TEST_MONTH_DAYS_1 = "TEST_MONTH_DAYS_1";

    private static final SortedSet<Byte> TEST_MONTHS = new TreeSet<Byte>(
            Collections.singletonList(
                    Byte.valueOf("1")
            )
    );
    private static final SortedSet<Byte> TEST_MONTHS_1 = new TreeSet<Byte>(
            Collections.singletonList(
                    Byte.valueOf("2")
            )
    );

    private static final SortedSet<Byte> TEST_WEEK_DAYS = new TreeSet<Byte>(
            Collections.singletonList(
                    Byte.valueOf("1")
            )
    );
    private static final SortedSet<Byte> TEST_WEEK_DAYS_1 = new TreeSet<Byte>(
            Collections.singletonList(
                    Byte.valueOf("2")
            )
    );

    private static final String TEST_TIMEZONE = "Europe/Dublin";
    private static final String TEST_TIMEZONE_1 = "Africa/Lagos";

    private static final String TEST_TIME = "2010-05-23T09:01:02";
    private static final String TEST_TIME_1 = "2014-07-21T09:01:02";

    private static final Date TEST_START_DATE = TestsValuesProvider.provideTestDate(TEST_TIMEZONE, TEST_TIME);
    private static final Date TEST_START_DATE_1 = TestsValuesProvider.provideTestDate(TEST_TIMEZONE_1, TEST_TIME_1);

    private static final Date TEST_END_DATE = TestsValuesProvider.provideTestDate(TEST_TIMEZONE, TEST_TIME);
    private static final Date TEST_END_DATE_1 = TestsValuesProvider.provideTestDate(TEST_TIMEZONE_1, TEST_TIME_1);

    private static final String TEST_CALENDAR_NAME = "TEST_CALENDAR_NAME";
    private static final String TEST_CALENDAR_NAME_1 = "TEST_CALENDAR_NAME_1";

    private static final Integer TEST_MISFIRE_INSTRUCTION = 1;
    private static final Integer TEST_MISFIRE_INSTRUCTION_1 = 0;

    @Override
    protected List<ClientJobCalendarTriggerModel> prepareInstancesWithAlternativeParameters() {
        return  Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setMinutes(TEST_MINUTES_1),
                createFullyConfiguredInstance().setHours(TEST_HOURS_1),
                createFullyConfiguredInstance().setDaysType(TEST_DAYS_TYPE_1),
                createFullyConfiguredInstance().setMonthDays(TEST_MONTH_DAYS_1),
                createFullyConfiguredInstance().setMonths(TEST_MONTHS_1),
                createFullyConfiguredInstance().setWeekDays(TEST_WEEK_DAYS_1),
                createFullyConfiguredInstance().setStartDate(TEST_START_DATE_1),
                createFullyConfiguredInstance().setStartType(START_TYPE_SCHEDULE),
                createFullyConfiguredInstance().setEndDate(TEST_END_DATE_1),
                createFullyConfiguredInstance().setTimezone(TEST_TIMEZONE_1),
                createFullyConfiguredInstance().setCalendarName(TEST_CALENDAR_NAME_1),
                createFullyConfiguredInstance().setMisfireInstruction(TEST_MISFIRE_INSTRUCTION_1),
                // null values
                createFullyConfiguredInstance().setMinutes(null),
                createFullyConfiguredInstance().setHours(null),
                createFullyConfiguredInstance().setDaysType(null),
                createFullyConfiguredInstance().setMonthDays(null),
                createFullyConfiguredInstance().setMonths(null),
                createFullyConfiguredInstance().setWeekDays(null),
                createFullyConfiguredInstance().setStartDate(null),
                createFullyConfiguredInstance().setStartType(0),
                createFullyConfiguredInstance().setEndDate(null),
                createFullyConfiguredInstance().setTimezone(null),
                createFullyConfiguredInstance().setCalendarName(null),
                createFullyConfiguredInstance().setMisfireInstruction(null)
        );
    }

    @Override
    protected ClientJobCalendarTriggerModel createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setMinutes(TEST_MINUTES)
                .setHours(TEST_HOURS)
                .setDaysType(TEST_DAYS_TYPE)
                .setMonthDays(TEST_MONTH_DAYS)
                .setMonths(TEST_MONTHS)
                .setWeekDays(TEST_WEEK_DAYS)
                .setStartDate(TEST_START_DATE)
                .setStartType(START_TYPE_NOW)
                .setEndDate(TEST_END_DATE)
                .setTimezone(TEST_TIMEZONE)
                .setCalendarName(TEST_CALENDAR_NAME)
                .setMisfireInstruction(TEST_MISFIRE_INSTRUCTION);
    }

    @Override
    protected ClientJobCalendarTriggerModel createInstanceWithDefaultParameters() {
        return new ClientJobCalendarTriggerModel();
    }

    @Override
    protected ClientJobCalendarTriggerModel createInstanceFromOther(ClientJobCalendarTriggerModel other) {
        return new ClientJobCalendarTriggerModel(other);
    }

}
