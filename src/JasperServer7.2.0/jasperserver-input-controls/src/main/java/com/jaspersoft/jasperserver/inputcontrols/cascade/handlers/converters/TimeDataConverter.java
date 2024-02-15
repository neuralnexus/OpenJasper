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
import com.jaspersoft.jasperserver.api.common.timezone.TimeZoneTransformer;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class TimeDataConverter implements DataConverter<Time> {
    @Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @Resource
    private ClientTimezoneFormattingRulesResolver clientTimezoneFormattingRulesResolver;

    @Resource
    private TimeZoneTransformer timeZoneTransformer;

    @Override
    public Time stringToValue(String rawData) throws ParseException {
        if (!StringUtils.isNotEmpty(rawData)) return null;

        Time parsedTime = new Time(getFormatter().parse(rawData).getTime());

        return (Time) timeZoneTransformer.toServerTimeZone(parsedTime);
    }

    @Override
    public String valueToString(Time value) {
        return value != null ? getFormatter().format(timeZoneTransformer.toClientTimezone(value)) : "";
    }

    private DateFormat getFormatter() {
        return calendarFormatProvider.getTimeFormat();
    }

}
