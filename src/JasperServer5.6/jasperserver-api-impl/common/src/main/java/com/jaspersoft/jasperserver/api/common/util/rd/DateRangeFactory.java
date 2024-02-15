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
package com.jaspersoft.jasperserver.api.common.util.rd;

import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.DateRangeBuilder;
import net.sf.jasperreports.types.date.DateRangeExpression;
import net.sf.jasperreports.types.date.InvalidDateRangeExpressionException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Factory which returns proper instance of {@link DateRange}</p>
 *
 * @author Sergey Prilukin
 * @version $Id: DateRangeFactory.java 25242 2012-10-15 14:11:08Z sergey.prilukin $
 */
public class DateRangeFactory {

    public static DateRange getInstance(final String expression) throws InvalidDateRangeExpressionException {
        return getInstance(expression, null);
    }

    public static DateRange getInstance(final Date date) {
        return new DateRangeBuilder(date).toDateRange();
    }

    public static DateRange getInstance(final Date date, final Class<? extends Date> valueClass) {
        return new DateRangeBuilder(date).set(valueClass).toDateRange();
    }

    public static DateRange getInstance(final String expression, final Class<? extends Date> valueClass)
            throws InvalidDateRangeExpressionException {
        TimeZone tz = (valueClass != null && valueClass.equals(Timestamp.class)) ?
                TimeZoneContextHolder.getTimeZone() : null;

        return getInstance(expression, tz, valueClass);
    }

    public static DateRange getInstance(final String expression, final Class<? extends Date> valueClass, String datePattern)
            throws InvalidDateRangeExpressionException {
        DateRangeBuilder builder = new DateRangeBuilder(expression).set(valueClass).set(datePattern);
        if (valueClass != null && valueClass.equals(Timestamp.class)) {
            builder.set(TimeZoneContextHolder.getTimeZone());
        }
        return builder.toDateRange();
    }

    public static DateRange getInstance(final String expression, TimeZone timeZone, final Class<? extends Date> valueClass)
            throws InvalidDateRangeExpressionException {
        return new DateRangeBuilder(expression).set(valueClass).set(timeZone).toDateRange();
    }

    public static DateRange getInstanceForExpression(final String expression, final Class<? extends Date> valueClass)
            throws InvalidDateRangeExpressionException {
        if (expression == null || "null".equals(expression)) {
            return null;
        }

        DateRange testDateRange = getInstance(expression);
        TimeZone tz = TimeZone.getDefault();

        if (testDateRange instanceof DateRangeExpression) {
            tz = (valueClass != null && valueClass.equals(Timestamp.class)) ?
                    TimeZoneContextHolder.getTimeZone() : null;
        }

        return getInstance(expression, tz, valueClass);
    }
}
