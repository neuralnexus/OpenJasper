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
package com.jaspersoft.jasperserver.war.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author schubar
 * @author agodovan
 */
public class DateTimeUtil {
    public static final String STANDARD_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd";
    public static final String STANDARD_TIME_FORMAT = "HH:mm:ss";
    public static final String STANDARD_TIMEWITHMILLISEC_FORMAT = "HH:mm:ss.SSS";
    public static final String STANDARD_DATETIMEWITHMILLISEC_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ISO_DATETIMEWITHMILLISEC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String[] dateTimeFormats = {STANDARD_TIME_FORMAT, STANDARD_DATE_FORMAT, STANDARD_DATETIME_FORMAT,
            "yyyy-MM-dd'T'HH:mm:ss.SSS", STANDARD_TIMEWITHMILLISEC_FORMAT, STANDARD_DATETIMEWITHMILLISEC_FORMAT};

    public static java.util.Date parseDate(String pattern, String date) throws ParseException {
        return parseDate(pattern, date, null);
    }

    public static java.util.Date parseDate(String pattern, String date, TimeZone timeZone) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        ParsePosition parseIndex = new ParsePosition(0);
        Date result = formatter.parse(date, parseIndex);
        if (parseIndex.getIndex() == date.length()) {
            return result;
        } else {
            throw new ParseException("Can't parse " + date, parseIndex.getIndex());
        }
    }

    public static Date parseDateTime(String dateTime) {
        try {
            return DateUtils.parseDate(dateTime, dateTimeFormats);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Can't parse date '" + dateTime);
        }
    }

    public static java.util.Date parseDate(String date) throws ParseException {
        return parseDate(STANDARD_DATETIME_FORMAT, date, null);
    }

    public static java.sql.Date parseSQLDate(String pattern, String date) throws ParseException {
        return new java.sql.Date(parseDate(pattern, date).getTime());
    }

    public static java.sql.Date parseSQLDate(String date) throws ParseException {
        return parseSQLDate(STANDARD_DATE_FORMAT, date);
    }

    public static java.sql.Timestamp parseTimestamp(String pattern, String dateTime) throws ParseException {
        return parseTimestamp(pattern, dateTime, null);
    }

    public static java.sql.Timestamp parseTimestamp(String pattern, String dateTime, TimeZone timeZone) throws ParseException {
        return new java.sql.Timestamp(parseDate(pattern, dateTime, timeZone).getTime());
    }

    public static java.sql.Timestamp parseTimestamp(String dateTime, TimeZone timeZone) throws ParseException {
        // check whether the time string is in hh:mm:ss.SSS format
        // if there is contains ".", millisecond is included
        boolean haveMillisecondField = dateTime.indexOf(".") > 0;
        // also support missing time field
        final boolean containsSpace = dateTime.indexOf(" ") > 0;
        final boolean containsT = dateTime.indexOf("T") > 0;
        boolean haveTimeField = containsSpace || containsT;
        String format;
        if (haveMillisecondField) {
            format= containsSpace ? STANDARD_DATETIMEWITHMILLISEC_FORMAT : ISO_DATETIMEWITHMILLISEC_FORMAT;
        } else if (haveTimeField) {
            format= containsSpace ? STANDARD_DATETIME_FORMAT : ISO_DATETIME_FORMAT;
        } else {
            format= STANDARD_DATE_FORMAT;
        }

        return parseTimestamp(format, dateTime, timeZone);
    }
    public static java.sql.Time parseTime(String pattern, String time) throws ParseException {
        return parseTime(pattern, time, null);
    }

    public static java.sql.Time parseTime(String pattern, String time, TimeZone timeZone) throws ParseException {
        return new java.sql.Time(parseDate(pattern, time, timeZone).getTime());
    }

    public static java.sql.Time parseTime(String time) throws ParseException {
        // check whether the time string is in hh:mm:ss.SSS format
        // if there is contains ".", millisecond is included
        boolean haveMillisecondField = time.indexOf(".") > 0;
        String format = haveMillisecondField ? STANDARD_TIMEWITHMILLISEC_FORMAT : STANDARD_TIME_FORMAT;

        return parseTime(format, time, null);
    }

}
