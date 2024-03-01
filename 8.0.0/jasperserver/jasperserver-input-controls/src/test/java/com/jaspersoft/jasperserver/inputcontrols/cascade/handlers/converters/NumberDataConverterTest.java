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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters;


import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class NumberDataConverterTest {
    NumberDataConverter numberDataConverter;

    @Before
    public void setup() {
        numberDataConverter = new NumberDataConverter();
    }


    @Test
    public void stringToValue() throws ParseException {
        assertEquals(numberDataConverter.stringToValue(null), null);

        int expected = 10;
        Number actual = numberDataConverter.stringToValue("10");
        assertThat(expected, is(actual.intValue()));
    }

    @Test
    public void valueToString() {
        assertEquals(numberDataConverter.valueToString(null), "");

        String expected = "10";
        Number value = 10;
        String actual = numberDataConverter.valueToString(value);
        assertThat(expected, is(actual));
    }
}