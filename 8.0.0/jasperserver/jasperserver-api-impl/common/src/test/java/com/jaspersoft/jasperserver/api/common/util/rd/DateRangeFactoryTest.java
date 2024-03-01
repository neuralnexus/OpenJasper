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

package com.jaspersoft.jasperserver.api.common.util.rd;

import com.jaspersoft.jasperserver.api.common.timezone.ClientTimezoneFormattingRulesResolver;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.FixedTimestamp;
import net.sf.jasperreports.types.date.RelativeDateRange;
import net.sf.jasperreports.types.date.FixedDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DateRangeFactoryTest {
    private ApplicationContext currentApplicationContext;

    @Before
    public void init() {
        currentApplicationContext = StaticApplicationContext.getApplicationContext();

        ClientTimezoneFormattingRulesResolver clientTimezoneFormattingRulesResolver = Mockito.mock(ClientTimezoneFormattingRulesResolver.class);
        when(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Mockito.any(Class.class))).thenReturn(true);

        ApplicationContext applicationContextMock = Mockito.mock(ApplicationContext.class);
        when(applicationContextMock.getBean(ClientTimezoneFormattingRulesResolver.class)).thenReturn(clientTimezoneFormattingRulesResolver);

        StaticApplicationContext.setApplicationContext(applicationContextMock);

        TimeZoneContextHolder.setTimeZone(TimeZone.getDefault());
    }

    @After
    public void tearDown() {
        StaticApplicationContext.setApplicationContext(currentApplicationContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNullCannotBeUsedAsExpression() {
        DateRangeFactory.getInstance((String) null);
    }

    @Test
    public void ensureFixedDateCreatedFromProperPattern() {
        DateRange dr = DateRangeFactory.getInstance("2012-08-01");
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());
    }

    @Test
    public void ensureFixedDateCreatedFromProperPatternInChicago() {
        TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

        DateRange dr = DateRangeFactory.getInstance("2012-08-01");
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, Calendar.AUGUST, 1).getTime(), dr.getStart());
    }

    @Test
    public void ensureFixedDateCreatedFromProperPatternInVietnam() {
        TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        DateRange dr = DateRangeFactory.getInstance("2012-08-01", Date.class);
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, Calendar.AUGUST, 1).getTime(), dr.getStart());
    }

    @Test
    public void ensureSingleRelativeDateCreatedFromProperPattern() {
        DateRange dr = DateRangeFactory.getInstance("DAY-1");
        assertNotNull(dr);
        assertEquals(RelativeDateRange.class, dr.getClass());
    }

    @Test
    public void ensureRelativeDateRangeCreatedFromProperPattern() {
        DateRange dr = DateRangeFactory.getInstance("WEEK-1");
        assertNotNull(dr);
        assertEquals(RelativeDateRange.class, dr.getClass());
    }

    @Test
    public void ensureProperFixedDateIsCreatedFromPassedDate() {
        DateRange dr = DateRangeFactory.getInstance(new GregorianCalendar(2012, Calendar.AUGUST, 2).getTime());
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, Calendar.AUGUST, 2).getTime(), dr.getStart());
    }

    @Test
    public void ensureFixedDateCreatedFromUsingDatePattern() {
        DateRange dr = DateRangeFactory.getInstance("2012/08/01", null, "yyyy/MM/dd");
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, Calendar.AUGUST, 1).getTime(), dr.getStart());
    }

    @Test
    public void ensureFixedDateCreatedFromUsingDatePatternInChicago() {
        TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

        DateRange dr = DateRangeFactory.getInstance("2012/08/01", null, "yyyy/MM/dd");
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, Calendar.AUGUST, 1).getTime(), dr.getStart());
    }

    @Test
    public void ensureFixedDateCreatedFromUsingDatePatternInVietnam() {
        TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        DateRange dr = DateRangeFactory.getInstance("2012/08/01", Date.class, "yyyy/MM/dd");
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, Calendar.AUGUST, 1).getTime(), dr.getStart());
    }

    @Test
    public void ensureFixedTimestampCreatedFromUsingDatePattern() {
        DateRange dr = DateRangeFactory.getInstance("2012/08/01 00:00:00", Timestamp.class, "yyyy/MM/dd HH:mm:ss");
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, Calendar.AUGUST, 1).getTimeInMillis(), dr.getStart().getTime());
    }

    @Test
    public void ensureFixedTimestampCreatedFromUsingDatePatternInChicago() {
        TimeZone systemTz = setSystemTimeZone("GMT+2");
        TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

        DateRange dr = DateRangeFactory.getInstance("2012/08/01 00:00:00", Timestamp.class, "yyyy/MM/dd HH:mm:ss");
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());
        assertEquals(-7, diff(timestamp(2012, Calendar.AUGUST, 1), dr.getStart()));

        setSystemTimeZone(systemTz.getID());
    }

    @Test
    public void ensureFixedTimestampCreatedFromUsingDatePatternInVietnam() {
        TimeZone systemTz = setSystemTimeZone("GMT+2");
        TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

        DateRange dr = DateRangeFactory.getInstance("2012/08/01 00:00:00", Timestamp.class, "yyyy/MM/dd HH:mm:ss");
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());

        assertEquals(-7, diff(timestamp(2012, Calendar.AUGUST, 1), dr.getStart()));

        setSystemTimeZone(systemTz.getID());
    }

    private Timestamp timestamp(int year, int month, int d) {
        return new Timestamp(new GregorianCalendar(year, month, d).getTimeInMillis());
    }

    private long diff(Date d1, Date d2) {
        return TimeUnit.MILLISECONDS.toHours(d1.getTime() - d2.getTime());
    }

    private TimeZone setSystemTimeZone(String id) {
        TimeZone tz = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone(id));
        return tz;
    }

}
