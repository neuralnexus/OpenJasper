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

/**
 * Extracted from Cipher
 */
public class Hexer {

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Convert a byte array response to a hex string
     */
    public static String hexify(byte[] data) {
        StringBuffer hex = new StringBuffer();

        for (int i = 0; i < data.length; i++) {
            int highBits = ((int) data[i] & 0x000000F0) >> 4;
            int lowBits = ((int) data[i] & 0x0000000F);
            hex.append(hexChars[highBits]).append(hexChars[lowBits]);
        }

        return (hex.toString());
    }

    /**
     * Convert a byte array response to a string where each byte is in hex separated by space
     */
    public static String stringify(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("0x%02x ", b & 0xff));
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Converts string of space separated hex values into the byte array
     * @param stringOfSpaceSeparatedHex
     * @return bytes array
     */
    public static byte[] parse(final String stringOfSpaceSeparatedHex) {
        if (stringOfSpaceSeparatedHex == null || stringOfSpaceSeparatedHex.isEmpty()) {
            return new byte[0];
        }

        String[] hexValues = stringOfSpaceSeparatedHex.split("\\s+");
        return parse(hexValues);
    }

    public static byte[] parse(final String[] hexValues) {
        if (hexValues.length == 0) {
            return new byte[0];
        }

        int length = hexValues.length;

        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = Integer.decode(hexValues[i]).byteValue();
        }

        return bytes;
    }

    /**
     * Convert a hex string response to a byte array
     */
    public static byte[] dehexify(String data) {
        byte[] bytes = new byte[data.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(data.substring(2 * i, (2 * i) + 2), 16);
        }

        return bytes;
    }

}
