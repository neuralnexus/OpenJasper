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

package com.jaspersoft.jasperserver.api.common.crypto;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HexerTest {
    String s;
    String sr;
    byte[] b;
    @Before
    public void setUp() throws Exception {
        s = "1BB51857";
        sr =           "0x1b 0xb5 0x18 0x57";
        b = new byte[] {27, -75, 24, 87};
    }


    @Test
    public void hexify() {
        assertEquals("Representation of a byte array in hex", s, Hexer.hexify(b));
    }

    @Test
    public void hexifyWithRadix() {
        assertEquals("Representation of a byte array in hex with radix and space delimited", sr, Hexer.stringify(b));
    }

    @Test
    public void dehexify() {
        assertArrayEquals("Hex string to byte array", b, Hexer.dehexify(s));
    }

    @Test
    public void equality() {
        assertArrayEquals("byte array to hex string and back", b, Hexer.dehexify(Hexer.hexify(b)));
    }
}