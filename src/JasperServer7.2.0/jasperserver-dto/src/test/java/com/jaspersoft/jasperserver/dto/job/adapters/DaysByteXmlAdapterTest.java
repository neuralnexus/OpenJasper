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

package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.job.wrappers.ClientDaysSortedSetWrapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class DaysByteXmlAdapterTest {

    private static final String TEST_DAY_AS_STRING = "10";
    private static final SortedSet<String> TEST_DAYS_AS_STRINGS = new TreeSet<String>(Collections.singletonList(TEST_DAY_AS_STRING));

    private static final Byte TEST_DAY_AS_BYTE = Byte.valueOf(TEST_DAY_AS_STRING);
    private static final SortedSet<Byte> TEST_DAYS_AS_BYTES = new TreeSet<Byte>(Collections.singletonList(TEST_DAY_AS_BYTE));

    private DaysByteXmlAdapter objectUnderTest = new DaysByteXmlAdapter();

    @Test
    public void unmarshal_nullValue_nullValue() throws Exception {
        SortedSet<Byte> actual = objectUnderTest.unmarshal(null);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithNullDays_nullValue() throws Exception {
        ClientDaysSortedSetWrapper wrapper = createClientDaysSortedSetWrapperWithNullDays();

        SortedSet<Byte> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithoutDays_nullValue() throws Exception {
        ClientDaysSortedSetWrapper wrapper = createClientDaysSortedSetWrapperWithoutDays();

        SortedSet<Byte> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithSomeDays_datesAsByties() throws Exception {
        ClientDaysSortedSetWrapper wrapper = createClientDaysSortedSetWrapperWithDays();

        SortedSet<Byte> actual = objectUnderTest.unmarshal(wrapper);
        assertEquals(TEST_DAYS_AS_BYTES, actual);
    }

    @Test
    public void marshal_nullValue_nullValue() throws Exception {
        ClientDaysSortedSetWrapper actual = objectUnderTest.marshal(null);
        assertNull(actual);
    }

    @Test
    public void marshal_empty_nullValue() throws Exception {
        ClientDaysSortedSetWrapper actual = objectUnderTest.marshal(new TreeSet<Byte>());
        assertNull(actual);
    }

    @Test
    public void marshal_someDays_wrapper() throws Exception {
        ClientDaysSortedSetWrapper expected = createClientDaysSortedSetWrapperWithDays();
        ClientDaysSortedSetWrapper actual = objectUnderTest.marshal(TEST_DAYS_AS_BYTES);

        assertEquals(expected, actual);
    }

    /*
     * Helpers
     */

    private ClientDaysSortedSetWrapper createClientDaysSortedSetWrapperWithDays() {
        return new ClientDaysSortedSetWrapper().setDays(TEST_DAYS_AS_STRINGS);
    }

    private ClientDaysSortedSetWrapper createClientDaysSortedSetWrapperWithNullDays() {
        return new ClientDaysSortedSetWrapper().setDays(new TreeSet<String>());
    }

    private ClientDaysSortedSetWrapper createClientDaysSortedSetWrapperWithoutDays() {
        return new ClientDaysSortedSetWrapper().setDays(null);
    }

}