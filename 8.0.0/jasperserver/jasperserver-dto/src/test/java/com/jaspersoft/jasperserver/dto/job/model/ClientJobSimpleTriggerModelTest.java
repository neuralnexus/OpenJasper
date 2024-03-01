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
import com.jaspersoft.jasperserver.dto.job.ClientIntervalUnitType;
import com.jaspersoft.jasperserver.dto.utils.TestsValuesProvider;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_NOW;
import static com.jaspersoft.jasperserver.dto.job.ClientJobTrigger.START_TYPE_SCHEDULE;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobSimpleTriggerModelTest extends BaseDTOTest<ClientJobSimpleTriggerModel> {

    private static final Integer TEST_OCCURENCE_COUNT = 100;
    private static final Integer TEST_OCCURENCE_COUNT_1 = 1001;

    private static final Integer TEST_RECCURENCE_INTERVAL = 101;
    private static final Integer TEST_RECCURENCE_INTERVAL_1 = 1011;

    private static final ClientIntervalUnitType TEST_RECCURENCE_INTERVAL_UNIT = ClientIntervalUnitType.HOUR;
    private static final ClientIntervalUnitType TEST_RECCURENCE_INTERVAL_UNIT_1 = ClientIntervalUnitType.DAY;

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
    protected List<ClientJobSimpleTriggerModel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setOccurrenceCount(TEST_OCCURENCE_COUNT_1),
                createFullyConfiguredInstance().setRecurrenceInterval(TEST_RECCURENCE_INTERVAL_1),
                createFullyConfiguredInstance().setRecurrenceIntervalUnit(TEST_RECCURENCE_INTERVAL_UNIT_1),
                createFullyConfiguredInstance().setStartDate(TEST_START_DATE_1),
                createFullyConfiguredInstance().setStartType(START_TYPE_SCHEDULE),
                createFullyConfiguredInstance().setEndDate(TEST_END_DATE_1),
                createFullyConfiguredInstance().setTimezone(TEST_TIMEZONE_1),
                createFullyConfiguredInstance().setCalendarName(TEST_CALENDAR_NAME_1),
                createFullyConfiguredInstance().setMisfireInstruction(TEST_MISFIRE_INSTRUCTION_1),
                // null values
                createFullyConfiguredInstance().setOccurrenceCount(null),
                createFullyConfiguredInstance().setRecurrenceInterval(null),
                createFullyConfiguredInstance().setRecurrenceIntervalUnit(null),
                createFullyConfiguredInstance().setStartDate(null),
                createFullyConfiguredInstance().setStartType(0),
                createFullyConfiguredInstance().setEndDate(null),
                createFullyConfiguredInstance().setTimezone(null),
                createFullyConfiguredInstance().setCalendarName(null),
                createFullyConfiguredInstance().setMisfireInstruction(null)
        );
    }

    @Override
    protected ClientJobSimpleTriggerModel createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setOccurrenceCount(TEST_OCCURENCE_COUNT)
                .setRecurrenceInterval(TEST_RECCURENCE_INTERVAL)
                .setRecurrenceIntervalUnit(TEST_RECCURENCE_INTERVAL_UNIT)
                .setStartDate(TEST_START_DATE)
                .setStartType(START_TYPE_NOW)
                .setEndDate(TEST_END_DATE)
                .setTimezone(TEST_TIMEZONE)
                .setCalendarName(TEST_CALENDAR_NAME)
                .setMisfireInstruction(TEST_MISFIRE_INSTRUCTION);
    }

    @Override
    protected ClientJobSimpleTriggerModel createInstanceWithDefaultParameters() {
        return new ClientJobSimpleTriggerModel();
    }

    @Override
    protected ClientJobSimpleTriggerModel createInstanceFromOther(ClientJobSimpleTriggerModel other) {
        return new ClientJobSimpleTriggerModel(other);
    }

}
