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

package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.job.wrappers.ClientMonthsSortedSetWrapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class MonthsByteXmlAdapterTest {

    private static final String TEST_MONTH_AS_STRING = "10";
    private static final SortedSet<String> TEST_MONTHS_AS_STRINGS = new TreeSet<String>(Collections.singletonList(TEST_MONTH_AS_STRING));

    private static final Byte TEST_MONTH_AS_BYTE = Byte.valueOf(TEST_MONTH_AS_STRING);
    private static final SortedSet<Byte> TEST_MONTH_AS_BYTES = new TreeSet<Byte>(Collections.singletonList(TEST_MONTH_AS_BYTE));

    private MonthsByteXmlAdapter objectUnderTest = new MonthsByteXmlAdapter();

    @Test
    public void unmarshal_nullValue_nullValue() throws Exception {
        SortedSet<Byte> actual = objectUnderTest.unmarshal(null);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithNullDays_nullValue() throws Exception {
        ClientMonthsSortedSetWrapper wrapper = createClientDaysSortedSetWrapperWithNullMonths();

        SortedSet<Byte> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithoutMonths_nullValue() throws Exception {
        ClientMonthsSortedSetWrapper wrapper = createClientDaysSortedSetWrapperWithoutMonths();

        SortedSet<Byte> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithSomeMonths_someMonths() throws Exception {
        ClientMonthsSortedSetWrapper wrapper = createClientMonthsSortedSetWrapperWithMonths();

        SortedSet<Byte> actual = objectUnderTest.unmarshal(wrapper);
        assertEquals(TEST_MONTH_AS_BYTES, actual);
    }

    @Test
    public void marshal_nullValue_nullValue() throws Exception {
        ClientMonthsSortedSetWrapper actual = objectUnderTest.marshal(null);
        assertNull(actual);
    }

    @Test
    public void marshal_empty_nullValue() throws Exception {
        ClientMonthsSortedSetWrapper actual = objectUnderTest.marshal(new TreeSet<Byte>());
        assertNull(actual);
    }

    @Test
    public void marshal_someDays_wrapper() throws Exception {
        ClientMonthsSortedSetWrapper expected = createClientMonthsSortedSetWrapperWithMonths();
        ClientMonthsSortedSetWrapper actual = objectUnderTest.marshal(TEST_MONTH_AS_BYTES);

        assertEquals(expected, actual);
    }

    /*
     * Helpers
     */

    private ClientMonthsSortedSetWrapper createClientMonthsSortedSetWrapperWithMonths() {
        return new ClientMonthsSortedSetWrapper().setMongths(TEST_MONTHS_AS_STRINGS);
    }

    private ClientMonthsSortedSetWrapper createClientDaysSortedSetWrapperWithNullMonths() {
        return new ClientMonthsSortedSetWrapper().setMongths(new TreeSet<String>());
    }

    private ClientMonthsSortedSetWrapper createClientDaysSortedSetWrapperWithoutMonths() {
        return new ClientMonthsSortedSetWrapper().setMongths(null);
    }

}