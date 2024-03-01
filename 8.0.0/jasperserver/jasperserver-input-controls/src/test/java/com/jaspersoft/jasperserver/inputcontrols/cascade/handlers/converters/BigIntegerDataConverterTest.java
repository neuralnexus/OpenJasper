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

import java.math.BigInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class BigIntegerDataConverterTest {
    BigIntegerDataConverter bigIntegerDataConverter;
    @Before
    public void setup() {
        bigIntegerDataConverter = new BigIntegerDataConverter();
    }

    @Test
    public void stringToValue() {
        assertEquals(bigIntegerDataConverter.stringToValue(null), null);

        BigInteger expected = BigInteger.valueOf(10);
        BigInteger actual = bigIntegerDataConverter.stringToValue("10");
        assertThat(expected, is(actual));
    }

    @Test
    public void valueToString() {
        assertEquals(bigIntegerDataConverter.valueToString(null), "");

        String expected = "10";
        BigInteger value = BigInteger.valueOf(10);
        String actual = bigIntegerDataConverter.valueToString(value);
        assertThat(expected, is(actual));
    }
}