/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author schubar
 */
public class DateTimeUtil {
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    public static java.util.Date parseDate(String pattern, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Can't parse date '" + date + "' using pattern '" + pattern + "'.");
        }
    }

    public static java.util.Date parseDate(String date) {
        return parseDate(DEFAULT_DATE_TIME_PATTERN, date);
    }

    public static java.sql.Date parseSQLDate(String pattern, String date) {
        return new java.sql.Date(parseDate(pattern, date).getTime());
    }

    public static java.sql.Date parseSQLDate(String date) {
        return parseSQLDate(DEFAULT_DATE_PATTERN, date);
    }

    public static java.sql.Timestamp parseTimestamp(String pattern, String dateTime) {
        return new java.sql.Timestamp(parseDate(pattern, dateTime).getTime());
    }

    public static java.sql.Timestamp parseTimestamp(String dateTime) {
        return parseTimestamp(DEFAULT_DATE_TIME_PATTERN, dateTime);
    }

    public static java.sql.Time parseTime(String pattern, String time) {
        return new java.sql.Time(parseDate(pattern, time).getTime());
    }

    public static java.sql.Time parseTime(String time) {
        return parseTime(DEFAULT_TIME_PATTERN, time);
    }
}
