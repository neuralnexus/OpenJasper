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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BigDecimalDataConverterTest {

    @Test
    public void valueToString_rawData() throws ParseException {
        BigDecimalDataConverter bigD = new BigDecimalDataConverter();
        assertNull(bigD.stringToValue(null));
        assertNull(bigD.stringToValue(""));
        assertEquals(new BigDecimal("100"), bigD.stringToValue("100"));
        assertEquals(new BigDecimal("100"), bigD.stringToValue("100.00"));
        assertEquals(new BigDecimal("-100"), bigD.stringToValue("-100.00"));
        assertEquals(new BigDecimal("0"), bigD.stringToValue("00.00"));
        assertEquals(new BigDecimal("1001.001").stripTrailingZeros(), bigD.stringToValue("1001.001"));
    }
}
