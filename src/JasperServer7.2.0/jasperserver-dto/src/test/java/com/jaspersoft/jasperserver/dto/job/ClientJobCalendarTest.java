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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobCalendarTest extends BaseDTOTest<ClientJobCalendar> {

    private static final ClientJobCalendar.Type TEST_CALENDAR_TYPE = ClientJobCalendar.Type.base;
    private static final ClientJobCalendar.Type TEST_CALENDAR_TYPE_1 = ClientJobCalendar.Type.annual;

    private static final ClientJobCalendar TEST_BASE_CALENDAR = new ClientJobCalendar();
    private static final ClientJobCalendar TEST_BASE_CALENDAR_1 = new ClientJobCalendar().setDescription("Some Description");

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_TIMEZONE_ID = "Europe/Dublin";
    private static final TimeZone TEST_TIME_ZONE = TestsValuesProvider.provideTimeZone(TEST_TIMEZONE_ID);

    private static final String TEST_TIMEZONE_ID_1 = "Africa/Lagos";
    private static final TimeZone TEST_TIME_ZONE_1 = TestsValuesProvider.provideTimeZone(TEST_TIMEZONE_ID_1);

    private static final String TEST_TIME = "2010-05-23T09:01:02";
    private static final String TEST_TIME_1 = "2014-07-21T09:01:02";

    private static final Calendar TEST_EXCLUDE_DAY = TestsValuesProvider.provideCalendarInstance(TEST_TIMEZONE_ID, TEST_TIME);
    private static final Calendar TEST_EXCLUDE_DAY_1 = TestsValuesProvider.provideCalendarInstance(TEST_TIMEZONE_ID_1, TEST_TIME_1);

    private static final ArrayList<Calendar> TEST_EXCLUDE_DAYS = new ArrayList<Calendar>(Collections.singletonList(TEST_EXCLUDE_DAY));
    private static final ArrayList<Calendar> TEST_EXCLUDE_DAYS_1 = new ArrayList<Calendar>(Collections.singletonList(TEST_EXCLUDE_DAY_1));

    private static final Boolean TEST_DATA_SORTED = true;
    private static final Boolean TEST_DATA_SORTED_1 = false;

    private static final String TEST_CRON_EXPRESSION = "TEST_CRON_EXPRESSION";
    private static final String TEST_CRON_EXPRESSION_1 = "TEST_CRON_EXPRESSION_1";

    private static final Calendar TEST_RANGE_STARTING_CALENDAR = TestsValuesProvider.provideCalendarInstance(TEST_TIMEZONE_ID, TEST_TIME);
    private static final Calendar TEST_RANGE_STARTING_CALENDAR_1 = TestsValuesProvider.provideCalendarInstance(TEST_TIMEZONE_ID_1, TEST_TIME_1);

    private static final Calendar TEST_RANGE_ENDING_CALENDAR = TestsValuesProvider.provideCalendarInstance(TEST_TIMEZONE_ID, TEST_TIME);
    private static final Calendar TEST_RANGE_ENDING_CALENDAR_1 = TestsValuesProvider.provideCalendarInstance(TEST_TIMEZONE_ID_1, TEST_TIME_1);

    private static final Boolean TEST_INVERT_TIME_RANGE = true;
    private static final Boolean TEST_INVERT_TIME_RANGE_1 = false;

    private static final boolean[] TEST_EXCLUDE_DAYS_FLAGS = new boolean[]{true};
    private static final boolean[] TEST_EXCLUDE_DAYS_FLAGS_1 = new boolean[]{false};

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientJobCalendar expected, ClientJobCalendar actual) {
        assertNotSame(expected.getBaseCalendar(), actual.getBaseCalendar());
        assertNotSame(expected.getTimeZone(), actual.getTimeZone());
        assertNotSame(expected.getExcludeDays(), actual.getExcludeDays());
        assertNotSame(expected.getExcludeDays().get(0), actual.getExcludeDays().get(0));
        assertNotSame(expected.getRangeStartingCalendar(), actual.getRangeStartingCalendar());
        assertNotSame(expected.getRangeEndingCalendar(), actual.getRangeEndingCalendar());
        assertNotSame(expected.getExcludeDaysFlags(), actual.getExcludeDaysFlags());
    }

    @Test
    public void testGetters() {
        ClientJobCalendar fullyConfiguredInstance = createFullyConfiguredInstance();
        assertEquals(fullyConfiguredInstance.getCalendarType(), TEST_CALENDAR_TYPE);
        assertEquals(fullyConfiguredInstance.getBaseCalendar(), TEST_BASE_CALENDAR);
        assertEquals(fullyConfiguredInstance.getDescription(), TEST_DESCRIPTION);
        assertEquals(fullyConfiguredInstance.getTimeZone(), TEST_TIME_ZONE);
        assertEquals(fullyConfiguredInstance.getExcludeDays(), TEST_EXCLUDE_DAYS);
        assertEquals(fullyConfiguredInstance.isDataSorted(), TEST_DATA_SORTED);
        assertEquals(fullyConfiguredInstance.getCronExpression(), TEST_CRON_EXPRESSION);
        assertEquals(fullyConfiguredInstance.getRangeStartingCalendar(), TEST_RANGE_STARTING_CALENDAR);
        assertEquals(fullyConfiguredInstance.getRangeEndingCalendar(), TEST_RANGE_ENDING_CALENDAR);
        assertEquals(fullyConfiguredInstance.isInvertTimeRange(), TEST_INVERT_TIME_RANGE);
        assertEquals(fullyConfiguredInstance.getExcludeDaysFlags(), TEST_EXCLUDE_DAYS_FLAGS);
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientJobCalendar> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // flags on
                createFullyConfiguredInstance().setCalendarType(TEST_CALENDAR_TYPE_1),
                createFullyConfiguredInstance().setBaseCalendar(TEST_BASE_CALENDAR_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setTimeZone(TEST_TIME_ZONE_1),
                createFullyConfiguredInstance().setExcludeDays(TEST_EXCLUDE_DAYS_1),
                createFullyConfiguredInstance().setDataSorted(TEST_DATA_SORTED_1),
                createFullyConfiguredInstance().setCronExpression(TEST_CRON_EXPRESSION_1),
                createFullyConfiguredInstance().setRangeStartingCalendar(TEST_RANGE_STARTING_CALENDAR_1),
                createFullyConfiguredInstance().setRangeEndingCalendar(TEST_RANGE_ENDING_CALENDAR_1),
                createFullyConfiguredInstance().setInvertTimeRange(TEST_INVERT_TIME_RANGE_1),
                createFullyConfiguredInstance().setExcludeDaysFlags(TEST_EXCLUDE_DAYS_FLAGS_1),
                // null values
                createFullyConfiguredInstance().setCalendarType(null),
                createFullyConfiguredInstance().setBaseCalendar(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setTimeZone(null),
                createFullyConfiguredInstance().setExcludeDays(null),
                createFullyConfiguredInstance().setDataSorted(null),
                createFullyConfiguredInstance().setCronExpression(null),
                createFullyConfiguredInstance().setRangeStartingCalendar(null),
                createFullyConfiguredInstance().setRangeEndingCalendar(null),
                createFullyConfiguredInstance().setInvertTimeRange(null),
                createFullyConfiguredInstance().setExcludeDaysFlags(null)
        );
    }

    @Override
    protected ClientJobCalendar createFullyConfiguredInstance() {
        return new ClientJobCalendar()
                .setCalendarType(TEST_CALENDAR_TYPE)
                .setBaseCalendar(TEST_BASE_CALENDAR)
                .setDescription(TEST_DESCRIPTION)
                .setTimeZone(TEST_TIME_ZONE)
                .setExcludeDays(TEST_EXCLUDE_DAYS)
                .setDataSorted(TEST_DATA_SORTED)
                .setCronExpression(TEST_CRON_EXPRESSION)
                .setRangeStartingCalendar(TEST_RANGE_STARTING_CALENDAR)
                .setRangeEndingCalendar(TEST_RANGE_ENDING_CALENDAR)
                .setInvertTimeRange(TEST_INVERT_TIME_RANGE)
                .setExcludeDaysFlags(TEST_EXCLUDE_DAYS_FLAGS);
    }

    @Override
    protected ClientJobCalendar createInstanceWithDefaultParameters() {
        return new ClientJobCalendar();
    }

    @Override
    protected ClientJobCalendar createInstanceFromOther(ClientJobCalendar other) {
        return new ClientJobCalendar(other);
    }
}
