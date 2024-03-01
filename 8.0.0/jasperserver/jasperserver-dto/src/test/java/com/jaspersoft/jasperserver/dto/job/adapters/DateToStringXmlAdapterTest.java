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

import com.jaspersoft.jasperserver.dto.utils.TestsValuesProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class DateToStringXmlAdapterTest {

    private DateToStringXmlAdapter objectUnderTest = new DateToStringXmlAdapter();

    private static final String TEST_TIME_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String TEST_DATE_STRING = "2017-02-12";
    private static final String TEST_TIME_STRING = "00:00:00";

    private static final String TEST_TIMEZONE_ID = "America/Los_Angeles";
    private static final String TEST_TIMEZONE_SHIFT = "-08:00";

    private TimeZone currentTimeZone;

    @BeforeEach
    public void setUp() {
        currentTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone(TEST_TIMEZONE_ID));
    }

    @AfterEach
    public void tearDown() {
        TimeZone.setDefault(currentTimeZone);
    }

    @Test
    public void unmarshal_nullValue_throws_NullPointerException() {
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
    public void unmarshal_emptyDateString_throws_IllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        objectUnderTest.unmarshal("");
                    }
                }
        );
    }

    @Test
    public void unmarshal_someDateString_correctDateObject() throws Exception {
        Date expected = createTestDate();

        Date actual = objectUnderTest.unmarshal(TEST_DATE_STRING);
        assertEquals(expected, actual);
    }

    @Test
    public void marshal_nullValue_throws_NullPointerException() {
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
    public void marshal_someDate_wrapper() throws Exception {
        String expected = createTestDateString();
        String actual = objectUnderTest.marshal(createTestDate());

        assertEquals(expected, actual);
    }

    /*
     * Helpers
     */

    private Date createTestDate() {
        return TestsValuesProvider.provideTestDateWithPattern(
                TEST_DATE_STRING,
                TEST_TIME_FORMAT_PATTERN
        );
    }

    private String createTestDateString() {
        // 2017-02-12'T'00:00:00-08:00
        String separator = "T";
        return TEST_DATE_STRING + separator + TEST_TIME_STRING + TEST_TIMEZONE_SHIFT;
    }

}