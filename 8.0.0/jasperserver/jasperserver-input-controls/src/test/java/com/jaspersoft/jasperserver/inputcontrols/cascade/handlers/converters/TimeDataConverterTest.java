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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.common.timezone.ClientTimezoneFormattingRulesResolver;
import com.jaspersoft.jasperserver.api.common.timezone.TimeZoneTransformer;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import com.jaspersoft.jasperserver.inputcontrols.util.IsoCalendarFormatProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Time;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimeDataConverterTest {

    @InjectMocks
    private TimeDataConverter timeDataConverter;

    @Mock
    private CalendarFormatProvider calendarFormatProvider;

    @Mock
    private ClientTimezoneFormattingRulesResolver clientTimezoneFormattingRulesResolver;

    @Mock
    private TimeZoneTransformer timeZoneTransformer;

    @Before
    public void setUp() throws Exception {
        when(calendarFormatProvider.getTimeFormat()).thenReturn(new IsoCalendarFormatProvider().getTimeFormat());

    }

    @Test
    public void stringToValue_rawDataIsNull_returnNull() throws ParseException {
        assertNull(timeDataConverter.stringToValue(null));
    }

    @Test
    public void stringToValue_rawDataIsEmptyString_returnEmptyString() throws ParseException {
        assertEquals(timeDataConverter.stringToValue(""), null);
    }

    @Test
    public void stringToValue_rawDataIs11hours_returnTimeObject() throws ParseException {
        when(timeZoneTransformer.toServerTimeZone(any())).thenReturn(Time.valueOf("11:00:00"));
        assertEquals(timeDataConverter.stringToValue("11:00:00"), Time.valueOf("11:00:00"));
    }

    @Test
    public void valueToString_valueIsNull_returnEmptyString() {
        assertEquals(timeDataConverter.valueToString(null), "");
    }

    @Test
    public void valueToString_timeIs11hours_returnString() {
        when(timeZoneTransformer.toClientTimezone(any())).thenReturn(Time.valueOf("11:00:00"));
        assertEquals(timeDataConverter.valueToString(Time.valueOf("11:00:00")), "11:00:00");
    }

    @Test
    public void valueToString_timeIs11hoursWithClientTimezone_returnString() {
        when(timeZoneTransformer.toClientTimezone(any())).thenReturn(Time.valueOf("13:00:00"));
        assertEquals(timeDataConverter.valueToString(Time.valueOf("11:00:00")), "13:00:00");
    }
}