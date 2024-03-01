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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.jaspersoft.jasperserver.dto.common.validations.ArrayUtils.checkArraysLength;
import static com.jaspersoft.jasperserver.dto.common.validations.ArrayUtils.objArrayToStringArray;
import static com.jaspersoft.jasperserver.dto.common.validations.ArrayUtils.objectToString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.paramNames;
import static org.junit.Assert.assertTrue;

/**
 * @author Volodya Sabadosh
 */
public class ArrayUtilsTest {

    @Test
    public void objectToString_nullValue_returnNull() {
        assertNull(objectToString(null));
    }

    @Test
    public void objectToString_objectArray_returnStr() {
        Object[] arrays = new Object[] {new SomeObj("test_1"), new SomeObj("test_2"),
                new SomeObj("test_3")};
        assertEquals("[test_1, test_2, test_3]", objectToString(arrays));
    }

    @Test
    public void objectToString_objectArrayWithNullValue_returnStr() {
        Object[] arrays = new Object[] {new SomeObj("test_1"), new SomeObj("test_2"), null};
        assertEquals("[test_1, test_2, null]", objectToString(arrays));
    }

    @Test
    public void objectToString_collectionOfObjects_returnStr() {
        Collection<SomeObj> collection = new ArrayList<SomeObj>(Arrays.asList(new SomeObj("test_1"), new SomeObj("test_2"),
                new SomeObj("test_3")));
        assertEquals("[test_1, test_2, test_3]", objectToString(collection));
    }


    @Test
    public void objectToString_booleanArray_returnStr() {
        boolean[] arrays = new boolean[] {true, false, true};
        assertEquals("[true, false, true]", objectToString(arrays));
    }

    @Test
    public void objectToString_byteArray_returnStr() {
        byte[] arrays = new byte[] {1, 2, 3};
        assertEquals("[1, 2, 3]", objectToString(arrays));
    }

    @Test
    public void objectToString_charArray_returnStr() {
        char[] arrays = new char[] {'a', '1', 'b'};
        assertEquals("[a, 1, b]", objectToString(arrays));
    }

    @Test
    public void objectToString_doubleArray_returnStr() {
        double[] arrays = new double[] {1.0, 2.1, 3.01};
        assertEquals("[1.0, 2.1, 3.01]", objectToString(arrays));
    }

    @Test
    public void objectToString_floatArray_returnStr() {
        float[] arrays = new float[] {1.0f, 2.1f, 3.01f};
        assertEquals("[1.0, 2.1, 3.01]", objectToString(arrays));
    }

    @Test
    public void objectToString_intArray_returnStr() {
        int[] arrays = new int[] {1, 2, 3};
        assertEquals("[1, 2, 3]", objectToString(arrays));
    }

    @Test
    public void objectToString_longArray_returnStr() {
        long[] arrays = new long[] {1, 2, 3};
        assertEquals("[1, 2, 3]", objectToString(arrays));
    }

    @Test
    public void objectToString_shortArray_returnStr() {
        short[] arrays = new short[] {1, 2, 3};
        assertEquals("[1, 2, 3]", objectToString(arrays));
    }

    @Test
    public void objectToString_someObject_returnStr() {
        SomeObj someObj = new SomeObj("test");
        assertEquals("test", objectToString(someObj));
    }

    @Test
    public void objectArrayToStringArray_someObjectArray_returnStrArray() {
        Object[] objArray = new Object[] {new SomeObj("test_1"), new SomeObj("test_2"),
                new SomeObj("test_3")};
        assertArrayEquals(new String[]{"test_1", "test_2", "test_3"}, objArrayToStringArray(objArray));
    }

    @Test
    public void objectArrayToStringArray_null_returnNull() {
        assertArrayEquals(null, objArrayToStringArray((Object[])null));
    }

    @Test
    public void array_stringVarArgs_returnStringArray() {
        assertArrayEquals(new String[]{"one", "two"}, paramNames("one", "two"));
    }

    @Test
    public void checkArraysLength_arraysAreNull_returnTrue() {
        assertTrue(checkArraysLength(null, null));
    }

    @Test
    public void checkArraysLength_firstNull_returnFalse() {
        assertFalse(checkArraysLength(null, new String[] {"second"}));
    }

    @Test
    public void checkArraysLength_secondNull_returnFalse() {
        assertFalse(checkArraysLength(new String[] {"first"}, null));
    }

    @Test
    public void checkArraysLength_DiffLength_returnFalse() {
        Object[] first = new String[]{"one", "two"};
        Object[] second = new String[]{"one"};
        assertFalse(checkArraysLength(first, second));
    }

    @Test
    public void checkArraysLength_SameLength_returnTrue() {
        Object[] first = new String[]{"one", "two"};
        Object[] second = new String[]{"one", "two"};
        assertTrue(checkArraysLength(first, second));
    }

    @Test
    public void array_null_returnNull() {
        assertArrayEquals(null, paramNames((String[])null));
    }

    public static class SomeObj {
        private String testStr;

        SomeObj(String testStr) {
            this.testStr = testStr;
        }

        @Override
        public String toString() {
            return this.testStr;
        }
    }

}
