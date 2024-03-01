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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * A set of SimpleDateFormat's using the format's defined in datarator-el
 *
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
public class DomELCommonSimpleDateFormats {

    public static final String DATE_RANGE_PATTERN_STRING = "^(DAY|WEEK|MONTH|QUARTER|SEMI|YEAR)([\\+|-][\\d]{1,9})?$";
    public static final Pattern DATE_RANGE_PATTERN = Pattern.compile(DATE_RANGE_PATTERN_STRING);

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String TIMESTAMP_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String TIMESTAMP_FORMAT_STRING_NO_MILLISECONDS = "yyyy-MM-dd HH:mm:ss";
    public static final String ISO_TIMESTAMP_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String ISO_TIMESTAMP_FORMAT_STRING_NO_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIME_FORMAT_STRING = "HH:mm:ss.SSS";
    public static final String TIME_FORMAT_STRING_WITHOUT_MILLISECONDS = "HH:mm:ss";

    public static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat(DATE_FORMAT_STRING);
    }

    public static SimpleDateFormat timeFormat() {
        return new SimpleDateFormat(TIME_FORMAT_STRING);
    }

    public static SimpleDateFormat timestampFormat() {
        return new SimpleDateFormat(TIMESTAMP_FORMAT_STRING);
    }

    public static SimpleDateFormat isoTimestampFormat() {
        return new SimpleDateFormat(ISO_TIMESTAMP_FORMAT_STRING);
    }

    public static SimpleDateFormat isoTimestampFormatNoMilliSeconds() {
        return new SimpleDateFormat(ISO_TIMESTAMP_FORMAT_STRING_NO_MILLISECONDS);
    }
}
