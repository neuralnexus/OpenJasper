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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class TimeZoneXmlAdapterTest {

    private static final String TEST_TIMEZONE_ID = "America/Los_Angeles";

    private TimeZoneXmlAdapter objectUnderTest = new TimeZoneXmlAdapter();

    @Test
    public void unmarshal_nullValue_throwsException_NullPointerException() {
        assertThrows(
                NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        objectUnderTest.unmarshal(null);
                    }
                }
        );
    }

    @Test
    public void unmarshal_someDateString_someTimeZone() throws Exception {
        TimeZone expected = TimeZone.getTimeZone(TEST_TIMEZONE_ID);
        TimeZone actual = objectUnderTest.unmarshal(TEST_TIMEZONE_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void marshal_nullValue_throwsException_NullPointerException() throws Exception {
        assertThrows(
                NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        objectUnderTest.marshal(null);
                    }
                }
        );
    }

    @Test
    public void marshal_someTimeZone_string_representation() throws Exception {
        String actual = objectUnderTest.marshal(TimeZone.getTimeZone(TEST_TIMEZONE_ID));

        assertEquals(TEST_TIMEZONE_ID, actual);
    }

}