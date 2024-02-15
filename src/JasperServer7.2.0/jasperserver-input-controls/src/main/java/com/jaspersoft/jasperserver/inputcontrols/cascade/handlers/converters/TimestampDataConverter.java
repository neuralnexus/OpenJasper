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
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class TimestampDataConverter implements DataConverter<Timestamp>, DateParser<Timestamp>{
    @Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @Resource
    private ClientTimezoneFormattingRulesResolver clientTimezoneFormattingRulesResolver;

    @Override
    public Timestamp stringToValue(String rawData) throws ParseException {
        try{
            return StringUtils.isNotEmpty(rawData) ? new Timestamp(getDateTimeFormatter().parse(rawData).getTime()) : null;
        } catch (ParseException e){
            try {
               return Timestamp.valueOf(rawData);
            } catch (IllegalArgumentException ex){
                throw e;
            }
        }
    }

    @Override
    public String valueToString(Timestamp value) {
        return value != null ? getDateTimeFormatter().format(value) : "";
    }

    @Override
    public Timestamp parsDate(String rawData) throws Exception {
        return StringUtils.isNotEmpty(rawData) ? new Timestamp(getDateFormatter().parse(rawData).getTime()) : null;
    }

    @Override
    public String dateToString(Timestamp value) {
        return value != null ? getDateFormatter().format(value) : "";
    }

    private DateFormat getDateFormatter() {
        return getDateFormatWithTimeZone(calendarFormatProvider.getDateFormat());
    }

    private DateFormat getDateTimeFormatter() {
        if (clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Timestamp.class)) {
            return getDateFormatWithTimeZone(calendarFormatProvider.getDatetimeFormat());
        }
        return calendarFormatProvider.getDatetimeFormat();
    }

    public static DateFormat getDateFormatWithTimeZone(DateFormat dateFormat) {
        dateFormat.setTimeZone(TimeZoneContextHolder.getTimeZone());
        return dateFormat;
    }
}
