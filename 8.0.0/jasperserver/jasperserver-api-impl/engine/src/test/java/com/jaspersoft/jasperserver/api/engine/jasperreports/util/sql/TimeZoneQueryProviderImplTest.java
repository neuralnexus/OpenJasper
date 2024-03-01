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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util.sql;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
public class TimeZoneQueryProviderImplTest {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("America/New_York");
    private static final String PRODUCT_NAME = "postgesql";
    private static final String QUERY = "SET LOCAL TIMEZONE='{TimeZone}'";

    private TimeZoneQueryProviderImpl timeZoneQueryProvider = new TimeZoneQueryProviderImpl();

    @Before
    public void setUp() {
        timeZoneQueryProvider.setProductNameToQuery(Collections.singletonMap(PRODUCT_NAME, QUERY));
        timeZoneQueryProvider.setTimeZonePlaceholder("{TimeZone}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAlterQuery_productNameIsNull_exception() {
        timeZoneQueryProvider.getAlterQuery(null, TIME_ZONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAlterQuery_timeZoneIsNull_exception() {
        timeZoneQueryProvider.getAlterQuery(PRODUCT_NAME, null);
    }

    @Test
    public void getAlterQuery_nonSupportableProduct_null() {
        assertNull(timeZoneQueryProvider.getAlterQuery("oracle", TIME_ZONE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAlterQuery_wrongTimeZonePlaceholder_exception() {
        timeZoneQueryProvider.setTimeZonePlaceholder("{WrongPlaceholder}");
        timeZoneQueryProvider.getAlterQuery(PRODUCT_NAME, TIME_ZONE);
    }

    @Test
    public void getAlterQuery_rightData_alterQuery() {
        String result = timeZoneQueryProvider.getAlterQuery(PRODUCT_NAME, TIME_ZONE);
        assertEquals("SET LOCAL TIMEZONE='America/New_York'", result);
    }
}
