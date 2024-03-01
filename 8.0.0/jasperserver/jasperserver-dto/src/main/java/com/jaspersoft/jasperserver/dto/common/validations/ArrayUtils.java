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
package com.jaspersoft.jasperserver.dto.common.validations;

import java.util.Arrays;

/**
 * @author Volodya Sabadosh
 */
public class ArrayUtils {

    /**
     * Provide a string value of (invalid) value that caused the exception.
     *
     * @param invalidValue invalid value causing BV exception.
     * @return string value of given object or {@code null}.
     */
    public static String objectToString(final Object invalidValue) {
        if (invalidValue == null) {
            return null;
        }

        if (invalidValue.getClass().isArray()) {
            if (invalidValue instanceof Object[]) {
                return Arrays.toString((Object[]) invalidValue);
            } else if (invalidValue instanceof boolean[]) {
                return Arrays.toString((boolean[]) invalidValue);
            } else if (invalidValue instanceof byte[]) {
                return Arrays.toString((byte[]) invalidValue);
            } else if (invalidValue instanceof char[]) {
                return Arrays.toString((char[]) invalidValue);
            } else if (invalidValue instanceof double[]) {
                return Arrays.toString((double[]) invalidValue);
            } else if (invalidValue instanceof float[]) {
                return Arrays.toString((float[]) invalidValue);
            } else if (invalidValue instanceof int[]) {
                return Arrays.toString((int[]) invalidValue);
            } else if (invalidValue instanceof long[]) {
                return Arrays.toString((long[]) invalidValue);
            } else if (invalidValue instanceof short[]) {
                return Arrays.toString((short[]) invalidValue);
            }
        }

        return invalidValue.toString();
    }

    /**
     * Convert object array to corresponding string array.
     *
     * @param objArray object array.
     * @return string value of given object or {@code null}.
     */
    public static String[] objArrayToStringArray(final Object... objArray) {
        String[] strArray = null;
        if (objArray != null) {
            strArray = new String[objArray.length];
            for (int i = 0; i < objArray.length; i++) {
                strArray[i] = objectToString(objArray[i]);
            }
        }
        return strArray;
    }

    /**
     * Check length of two arrays.
     * @param first first array
     * @param second second array
     * @return true of false if lengths are the same or not respectively
     */
    public static boolean checkArraysLength(Object[] first, Object[] second) {
        int lengthOfFirstArray = first != null ? first.length : 0;
        int lengthOfSecondArray = second != null ? second.length : 0;

        return lengthOfFirstArray == lengthOfSecondArray;
    }

}
