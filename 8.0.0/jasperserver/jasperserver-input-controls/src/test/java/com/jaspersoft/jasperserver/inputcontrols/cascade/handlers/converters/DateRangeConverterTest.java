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

import com.jaspersoft.jasperserver.api.common.util.rd.DateRangeFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.FixedDate;
import net.sf.jasperreports.types.date.RelativeDateRange;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.injectDependencyToPrivateField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DateRangeConverterTest {

    @Test
    public void ensureRelativeDateRangeReturnedForRangeExpressions() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("WEEK-1");
        assertNotNull(dr);
        assertEquals(RelativeDateRange.class, dr.getClass());
    }

    @Test
    public void ensureSingleRelativeReturnedDateForDateExpressions() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("DAY+1");
        assertNotNull(dr);
        assertEquals(RelativeDateRange.class, dr.getClass());
    }

    @Test
    public void ensureFixedDateReturnedForDateExpressions() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("2012-07-01");
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());

        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1).getTime(), dr.getStart());
        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1).getTime(), dr.getEnd());
    }

    @Test
    public void ensureNullReturnedForEmptyString() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("");
        assertNull(dr);
    }

    @Test
    public void ensureEmptyStringReturnedForNull() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        String str = dateRangeConverter.valueToString(null);
        assertEquals(str, "");
    }

    @Test
    public void ensureRelativeDateRangeExpressionMatches() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        String expression = "WEEK-1";
        String str = dateRangeConverter.valueToString(DateRangeFactory.getInstance(expression));
        assertEquals(expression, str);
    }

    @Test
    public void ensureSingleRelativeDateExpressionMatches() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        String expression = "DAY-100";
        String str = dateRangeConverter.valueToString(DateRangeFactory.getInstance(expression));
        assertEquals(expression, str);
    }

    @Test
    public void ensureFixedDateExpressionMatches() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        String expression = "2012-07-12";
        String str = dateRangeConverter.valueToString(DateRangeFactory.getInstance(expression));
        assertEquals(expression, str);
    }

    @Test
    public void ensureFixedDatetimeExpressionMatches() throws Exception {
        DateRangeDataConverter dateRangeConverter = getDateRangeConverter();
        String expression = "2012-07-12 12:20:01";
        String dateConverterFormat = "2012-07-12";
        String str = dateRangeConverter.valueToString(DateRangeFactory.getInstance(expression));
        assertEquals(dateConverterFormat, str);
    }

    private DateRangeDataConverter getDateRangeConverter() {
        DateRangeDataConverter dateRangeConverter = new DateRangeDataConverter();
        injectDependencyToPrivateField(dateRangeConverter, "calendarFormatProvider", createCalendarFormatProvider());

        return dateRangeConverter;
    }

    public static CalendarFormatProvider createCalendarFormatProvider() {
        CalendarFormatProvider calendarFormatProvider = mock(CalendarFormatProvider.class);
        doReturn(new SimpleDateFormat("yyyy-MM-dd")).when(calendarFormatProvider).getDateFormat();
        doReturn(new SimpleDateFormat("yyyy-MM-dd HH:mm")).when(calendarFormatProvider).getDatetimeFormat();
        return calendarFormatProvider;
    }

}
