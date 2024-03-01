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

package com.jaspersoft.jasperserver.dto.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class TestsValuesProvider {

    private static final String TEST_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static Calendar provideCalendarInstance(String timezoneId, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(provideTestDate(timezoneId, time));
        calendar.setTimeZone(provideTimeZone(timezoneId));
        return calendar;
    }

    public static Date provideTestDate(String timezoneId, String time) {
        SimpleDateFormat isoFormat = new SimpleDateFormat(TEST_TIME_FORMAT_PATTERN);
        isoFormat.setTimeZone(TimeZone.getTimeZone(timezoneId));
        Date date = null;
        try {
            date = isoFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date provideTestDateWithPattern(String time, String pattern) {
        SimpleDateFormat isoFormat = new SimpleDateFormat(pattern);
        isoFormat.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = isoFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static TimeZone provideTimeZone(String id) {
        return TimeZone.getTimeZone(id);
    }

}
