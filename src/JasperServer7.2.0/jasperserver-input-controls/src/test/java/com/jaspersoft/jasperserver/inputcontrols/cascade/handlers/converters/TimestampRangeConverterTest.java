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
import com.jaspersoft.jasperserver.api.common.util.rd.DateRangeFactory;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import com.jaspersoft.jasperserver.inputcontrols.util.IsoCalendarFormatProvider;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.FixedTimestamp;
import net.sf.jasperreports.types.date.RelativeTimestampRange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimestampRangeConverterTest extends UnitilsJUnit4 {

    @InjectMocks
    private TimestampRangeDataConverter dateRangeConverter;

    @Mock
    protected CalendarFormatProvider calendarFormatProvider;

    @Mock
    private ClientTimezoneFormattingRulesResolver clientTimezoneFormattingRulesResolver;

    private ApplicationContext currentApplicationContext;

    @Before
    public void setUp() throws Exception {
        currentApplicationContext = StaticApplicationContext.getApplicationContext();

        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Mockito.any(Class.class))).thenReturn(true);
        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Mockito.any(Object.class))).thenReturn(true);
        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Mockito.anyString())).thenReturn(true);

        ApplicationContext applicationContextMock = Mockito.mock(ApplicationContext.class);
        when(applicationContextMock.getBean(ClientTimezoneFormattingRulesResolver.class)).thenReturn(clientTimezoneFormattingRulesResolver);

        StaticApplicationContext.setApplicationContext(applicationContextMock);

        when(calendarFormatProvider.getDateFormat()).thenReturn(new IsoCalendarFormatProvider().getDateFormat());
        when(calendarFormatProvider.getDatetimeFormat()).thenReturn(new IsoCalendarFormatProvider().getDatetimeFormat());
    }

    @After
    public void tearDown() {
        StaticApplicationContext.setApplicationContext(currentApplicationContext);
    }

    @Test
    public void ensureRelativeDateRangeReturnedForRangeExpressions() throws Exception {
        DateRange dr = dateRangeConverter.stringToValue("WEEK-1");
        assertNotNull(dr);
        assertEquals(RelativeTimestampRange.class, dr.getClass());
    }

    @Test
    public void ensureSingleRelativeReturnedDateForDateExpressions() throws Exception {
        DateRange dr = dateRangeConverter.stringToValue("DAY+1");
        assertNotNull(dr);
        assertEquals(RelativeTimestampRange.class, dr.getClass());
    }

    @Test
    public void ensureFixedDateReturnedForDateExpressions() throws Exception {
        DateRange dr = dateRangeConverter.stringToValue("2012-07-01T12:34:00");
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());

        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 34, 0).getTime().getTime(), dr.getStart().getTime());
        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 34, 0).getTime().getTime(), dr.getEnd().getTime());
    }

    @Test
    public void ensureFixedDateReturnedForDatetimeExpressions() throws Exception {
        DateRange dr = dateRangeConverter.stringToValue("2012-07-01T12:20:00");
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());

        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 20).getTime(), dr.getStart());
        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 20).getTime(), dr.getEnd());
    }

    @Test
    public void ensureNullReturnedForEmptyString() throws Exception {
        DateRange dr = dateRangeConverter.stringToValue("");
        assertNull(dr);
    }

    @Test
    public void ensureEmptyStringReturnedForNull() throws Exception {
        String str = dateRangeConverter.valueToString(null);
        assertEquals(str, "");
    }

    @Test
    public void ensureRelativeDateRangeExpressionMatches() throws Exception {
        String expression = "WEEK-1";
        String str = dateRangeConverter.valueToString((RelativeTimestampRange) DateRangeFactory
                .getInstance(expression, Timestamp.class, null));
        assertEquals(expression, str);
    }

    @Test
    public void ensureSingleRelativeDateExpressionMatches() throws Exception {
        String expression = "DAY-100";
        String str = dateRangeConverter.valueToString((RelativeTimestampRange) DateRangeFactory
                .getInstance(expression, Timestamp.class, null));
        assertEquals(expression, str);
    }

    @Test
    public void ensureFixedDateTimeExpressionMatches() throws Exception {
        String expression = "2012-07-12 12:20:01";
        String dateConverterFormat = "2012-07-12T12:20:01";
        String str = dateRangeConverter.valueToString((FixedTimestamp) DateRangeFactory
                .getInstance(expression, Timestamp.class, null));
        assertEquals(dateConverterFormat, str);
    }

    @Test
    public void valueToString_isApplyClientTimezoneFalse_returnString() {
        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(any(Object.class))).thenReturn(false);

        String expression = "2012-07-12 12:20:01";
        String str = dateRangeConverter.valueToString((FixedTimestamp) DateRangeFactory
                .getInstance(expression, Timestamp.class, null));

        assertEquals(str, "2012-07-12T12:20:01");
    }
}
