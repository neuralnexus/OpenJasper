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

package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.job.wrappers.ClientExcludeDaysWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ExcludeDaysXmlAdapterTest {

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private static final String TEST_EXCLUDE_DAY = "2017-02-12";
    private static final List<String> TEST_EXCLUDE_DAYS = Collections.singletonList(TEST_EXCLUDE_DAY);

    private static final String TEST_TIMEZONE_ID = "America/Los_Angeles";

    private TimeZone currentTimeZone;

    private ExcludeDaysXmlAdapter objectUnderTest = new ExcludeDaysXmlAdapter();

    @BeforeEach
    public void setUp() {
        currentTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone(TEST_TIMEZONE_ID));
    }

    @AfterEach
    public void tearDown() {
        TimeZone.setDefault(currentTimeZone);
    }

    @Test
    public void unmarshal_nullValue_nullValue() throws Exception {
        ArrayList<Calendar> actual = objectUnderTest.unmarshal(null);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithNullDays_nullValue() throws Exception {
        ClientExcludeDaysWrapper wrapper = createClientExcludeDaysWrapperWithNullDays();

        ArrayList<Calendar> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithoutDays_nullValue() throws Exception {
        ClientExcludeDaysWrapper wrapper = createClientExcludeDaysWrapperWithoutDays();

        ArrayList<Calendar> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithSomeCalendars_someCalendars() throws Exception {
        ClientExcludeDaysWrapper wrapper = createClientExcludeDaysWrapperWithDays();

        ArrayList<Calendar> expected = (ArrayList<Calendar>) createCalendars();

        ArrayList<Calendar> actual = objectUnderTest.unmarshal(wrapper);
        assertEquals(expected, actual);
    }

    @Test
    public void marshal_nullValue_nullValue() throws Exception {
        ClientExcludeDaysWrapper actual = objectUnderTest.marshal(null);
        assertNull(actual);
    }

    @Test
    public void marshal_empty_nullValue() throws Exception {
        ClientExcludeDaysWrapper actual = objectUnderTest.marshal(new ArrayList<Calendar>());
        assertNull(actual);
    }

    @Test
    public void marshal_someAddress_wrapper() throws Exception {
        ClientExcludeDaysWrapper expected = createClientExcludeDaysWrapperWithDays();
        ClientExcludeDaysWrapper actual = objectUnderTest.marshal((ArrayList<Calendar>) createCalendars());

        assertEquals(expected, actual);
    }

    /*
     * Helpers
     */

    private ClientExcludeDaysWrapper createClientExcludeDaysWrapperWithDays() {
        return new ClientExcludeDaysWrapper().setExcludeDays(TEST_EXCLUDE_DAYS);
    }

    private ClientExcludeDaysWrapper createClientExcludeDaysWrapperWithoutDays() {
        return new ClientExcludeDaysWrapper().setExcludeDays(new ArrayList<String>());
    }

    private ClientExcludeDaysWrapper createClientExcludeDaysWrapperWithNullDays() {
        return new ClientExcludeDaysWrapper();
    }

    private List<Calendar> createCalendars() {
        List<Calendar> calendars = new ArrayList<Calendar>();
        try {
            Date date = format.parse(TEST_EXCLUDE_DAY);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(date);
            calendars.add(currentCalendar);
        } catch (ParseException e) {
            throw new RuntimeException("Should not be this exception: " + e);
        }
        return calendars;
    }

}