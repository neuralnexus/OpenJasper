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
package com.jaspersoft.jasperserver.war.cascade.handlers.converters;

import com.jaspersoft.jasperserver.war.util.CalendarFormatProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: TimeDataConverter.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
@Service
public class TimeDataConverter implements DataConverter<Time> {
    @Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @Override
    public Time stringToValue(String rawData) throws ParseException {
        return StringUtils.isNotEmpty(rawData) ? new Time(getFormatter().parse(rawData).getTime()) : null;
    }

    @Override
    public String valueToString(Time value) {
        return value != null ? getFormatter().format(value) : "";
    }

    private DateFormat getFormatter() {
        return TimestampDataConverter.getDateFormatWithTimeZone(calendarFormatProvider.getTimeFormat());
    }
}
