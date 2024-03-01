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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.common.timezone.ClientTimezoneFormattingRulesResolver;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import com.jaspersoft.jasperserver.inputcontrols.util.IsoCalendarFormatProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimestampDataConverterTest {

    @InjectMocks
    private TimestampDataConverter timestampDataConverter;

    @Mock
    protected CalendarFormatProvider calendarFormatProvider;

    @Mock
    private ClientTimezoneFormattingRulesResolver clientTimezoneFormattingRulesResolver;

    @Before
    public void setUp() throws Exception {
        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(any(Class.class))).thenReturn(true);
        when(calendarFormatProvider.getDatetimeFormat()).thenReturn(new IsoCalendarFormatProvider().getDatetimeFormat());
        when(calendarFormatProvider.getDateFormat()).thenReturn(new IsoCalendarFormatProvider().getDateFormat());
    }

    @Test
    public void stringToValue_rawDataIsEmptyString_returnNull() throws ParseException {
        assertNull(timestampDataConverter.stringToValue(""));
    }

    @Test
    public void stringToValue_rawDataIsNull_returnNull() throws ParseException {
        assertNull(timestampDataConverter.stringToValue(null));
    }

    @Test(expected = ParseException.class)
    public void stringToValue_valueIsNotAcceptable_ExceptionThrown() throws ParseException {
        assertNull(timestampDataConverter.stringToValue("test"));
    }

    @Test
    public void stringToValue_rawDataIsTimestampString_returnTimestamp() throws ParseException {
        assertEquals(timestampDataConverter.stringToValue("2018-05-11 11:00:00"), Timestamp.valueOf("2018-05-11 11:00:00"));
    }

    @Test
    public void stringToValue_rawDataIsTimestampStringWithTSeparator_returnTimestamp() throws ParseException {
        assertEquals(timestampDataConverter.stringToValue("2018-05-11T11:00:00"), Timestamp.valueOf("2018-05-11 11:00:00"));
    }


    @Test
    public void stringToValue_rawDataIsTimeStringAndIsApplyClientTimezoneIsFalse_returnTime() throws ParseException {
        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(any(Class.class))).thenReturn(false);

        assertEquals(timestampDataConverter.stringToValue("2018-05-11 11:00:00"), Timestamp.valueOf("2018-05-11 11:00:00"));
    }

    @Test
    public void valueToString_valueIsNull_returnEmptyString() {
        assertEquals(timestampDataConverter.valueToString(null), "");
    }

    @Test
    public void valueToString_valueIsTimestamp_returnString() {
        assertEquals(timestampDataConverter.valueToString(Timestamp.valueOf("2018-05-11 11:00:00")), "2018-05-11T11:00:00");
    }

    @Test
    public void valueToString_valueIsTimestampAndIsClientTimezoneIsFalse_returnString() {
        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(any(Class.class))).thenReturn(false);

        assertEquals(timestampDataConverter.valueToString(Timestamp.valueOf("2018-05-11 11:00:00")), "2018-05-11T11:00:00");
    }

    @Test(expected = ParseException.class)
    public void parsDate_valueIsNotAcceptable_ExceptionThrown() throws Exception {
        assertEquals(timestampDataConverter.parsDate("test"), Timestamp.valueOf("2018-05-11 00:00:00"));
    }

    @Test
    public void parsDate_valueIsNull_returnNull() throws Exception {
        assertNull(timestampDataConverter.parsDate(null));
    }

    @Test
    public void parsDate_valueIsTimestampString_returnTimestamp() throws Exception {
        assertEquals(timestampDataConverter.parsDate("2018-05-11"), Timestamp.valueOf("2018-05-11 00:00:00"));
    }

    @Test
    public void dateToString_valueIsNull_returnNull() {
        assertEquals(timestampDataConverter.dateToString(null), "");
    }

    @Test
    public void dateToString_valueIsTimestamp_returnString() {
        assertEquals(timestampDataConverter.dateToString(Timestamp.valueOf("2018-05-11 00:00:00")), "2018-05-11");
    }
}