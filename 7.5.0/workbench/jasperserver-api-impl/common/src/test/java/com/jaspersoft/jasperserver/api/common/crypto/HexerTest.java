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