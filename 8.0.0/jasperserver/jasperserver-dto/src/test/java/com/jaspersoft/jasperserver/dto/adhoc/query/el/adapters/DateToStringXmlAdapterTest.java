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

import com.jaspersoft.jasperserver.dto.adhoc.CommonAdhocDateFormats;
import com.jaspersoft.jasperserver.dto.utils.TestsValuesProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class DateToStringXmlAdapterTest {

    private DateToStringXmlAdapter objectUnderTest = new DateToStringXmlAdapter();

    private static final String TEST_DATE_STRING = "2017-02-12";

    /*
     * unmarshal
     */

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
                ParseException.class,
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
        Date expected = createTestDate(TEST_DATE_STRING);

        Date actual = objectUnderTest.unmarshal(TEST_DATE_STRING);
        assertEquals(expected, actual);
    }

    /*
     * marshal
     */

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
        String actual = objectUnderTest.marshal(createTestDate(TEST_DATE_STRING));
        assertEquals(TEST_DATE_STRING, actual);
    }

    /*
     * Helpers
     */

    private Date createTestDate(String dateAsString) {
        return TestsValuesProvider.provideTestDateWithPattern(
                dateAsString,
                CommonAdhocDateFormats.DATE_PATTERN
        );
    }

}