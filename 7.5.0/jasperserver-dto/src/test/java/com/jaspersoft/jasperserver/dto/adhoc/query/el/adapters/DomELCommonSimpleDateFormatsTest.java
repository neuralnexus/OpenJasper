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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters;

import com.jaspersoft.jasperserver.dto.adhoc.CommonAdhocDateFormats;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class DomELCommonSimpleDateFormatsTest {

    @Test
    public void createInstance() {
        // only to satisfy coverage
        new DomELCommonSimpleDateFormats();
    }

    @Test
    public void dateFormat_return_SimpleDateFormat_based_on_DatePattern() {
        assertEquals(CommonAdhocDateFormats.DATE_PATTERN, DomELCommonSimpleDateFormats.dateFormat().toPattern());
    }

    @Test
    public void timeFormat_return_SimpleDateFormat_based_on_TimePattern_without_timezone() {
        assertEquals(CommonAdhocDateFormats.TIME_WITHOUT_TIMEZONE_PATTERN, DomELCommonSimpleDateFormats.timeFormat().toPattern());
    }

    @Test
    public void timestampFormat_return_SimpleDateFormat_based_on_DateTimePattern_without_timezone() {
        assertEquals(CommonAdhocDateFormats.DOMEL_DATE_TIME_PATTERN_WITHOUT_TIMEZONE, DomELCommonSimpleDateFormats.timestampFormat().toPattern());
    }

    @Test
    public void pattern_DAY() {
        assertTrue(DomELCommonSimpleDateFormats.DATE_RANGE_PATTERN.matcher("DAY").matches());
    }

    @Test
    public void pattern_WEEK() {
        assertTrue(DomELCommonSimpleDateFormats.DATE_RANGE_PATTERN.matcher("WEEK").matches());
    }

    @Test
    public void pattern_MONTH() {
        assertTrue(DomELCommonSimpleDateFormats.DATE_RANGE_PATTERN.matcher("MONTH").matches());
    }

    @Test
    public void pattern_QUARTER() {
        assertTrue(DomELCommonSimpleDateFormats.DATE_RANGE_PATTERN.matcher("QUARTER").matches());
    }

    @Test
    public void pattern_SEMI() {
        assertTrue(DomELCommonSimpleDateFormats.DATE_RANGE_PATTERN.matcher("SEMI").matches());
    }

    @Test
    public void pattern_YEAR() {
        assertTrue(DomELCommonSimpleDateFormats.DATE_RANGE_PATTERN.matcher("YEAR").matches());
    }

}