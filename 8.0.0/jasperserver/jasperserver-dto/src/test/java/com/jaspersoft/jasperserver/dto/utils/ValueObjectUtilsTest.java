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

package com.jaspersoft.jasperserver.dto.utils;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.resources.domain.AbstractResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */
class ValueObjectUtilsTest {

    @Test
    public void copyOf_null_null() {
        assertNull(copyOf(null));
    }

    @Test
    public void copyOf_Object_SameObject() {
        Object expected = new Object();

        assertSame(expected, copyOf(expected));
    }

    @Test
    public void copyOf_DeepCloneable_DeepCloneable() {
        DeepCloneable forCopy = mock(DeepCloneable.class);
        DeepCloneable expected = mock(DeepCloneable.class);

        when(forCopy.deepClone()).thenReturn(expected);

        DeepCloneable actual = copyOf(forCopy);

        assertSame(expected, actual);
    }

    /*
     * lists
     */

    @Test
    public void copyOf_EmptyArrayList_NewEmptyArrayList() {
        List expected = new ArrayList();
        List actual = copyOf(expected);

        assertTrue(actual instanceof ArrayList);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    public void copyOf_EmptyLinkedList_NewEmptyLinkedList() {
        List expected = new LinkedList();
        List actual = copyOf(expected);

        assertTrue(actual instanceof LinkedList);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    public void copyOf_ArrayListOfDeepCloneable_NewArrayListOfDeepCloneable() {
        DeepCloneable expectedElement = mock(DeepCloneable.class);
        DeepCloneable forCopy = mock(DeepCloneable.class);
        when(forCopy.deepClone()).thenReturn(expectedElement);

        List originalList = new ArrayList(asList(
                forCopy
        ));

        List actual = copyOf(originalList);

        assertTrue(actual instanceof ArrayList);
        assertNotSame(originalList, actual);
        assertSame(expectedElement, actual.get(0));
    }

    @Test
    public void copyOf_LinkedListOfDeepCloneable_NewLinkedListOfDeepCloneable() {
        DeepCloneable expectedElement = mock(DeepCloneable.class);
        DeepCloneable forCopy = mock(DeepCloneable.class);
        when(forCopy.deepClone()).thenReturn(expectedElement);

        List originalList = new LinkedList(asList(
                forCopy
        ));

        List actual = copyOf(originalList);

        assertTrue(actual instanceof LinkedList);
        assertNotSame(originalList, actual);
        assertSame(expectedElement, actual.get(0));
    }

    @Test
    public void copyOf_ArrayListOfNull_NewArrayListOfNull() {
        List originalList = new ArrayList();
        originalList.add(null);

        List actual = copyOf(originalList);

        assertTrue(actual instanceof ArrayList);
        assertNotSame(originalList, actual);
        assertNull(actual.get(0));
    }

    @Test
    public void copyOf_LinkedListOfNull_NewLinkedListOfNull() {
        List originalList = new LinkedList();
        originalList.add(null);

        List actual = copyOf(originalList);

        assertTrue(actual instanceof LinkedList);
        assertNotSame(originalList, actual);
        assertNull(actual.get(0));
    }

    /*
     * maps
     */


    @Test
    public void copyOf_EmptyHashMap_NewEmptyHaspMap() {
        Map expected = new HashMap();
        Map actual = copyOf(expected);

        assertTrue(actual instanceof HashMap);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    public void copyOf_EmptyLinkedHashMap_NewEmptyLinkedHashMap() {
        Map expected = new LinkedHashMap();
        Map actual = copyOf(expected);

        assertTrue(actual instanceof LinkedHashMap);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    public void copyOf_HashMapWithDeepCloneable_NewHashMapWithDeepCloneable() {
        DeepCloneable expectedElement = mock(DeepCloneable.class);
        DeepCloneable forCopy = mock(DeepCloneable.class);
        when(forCopy.deepClone()).thenReturn(expectedElement);

        Map originalMap = new HashMap();
        originalMap.put("key", forCopy);

        Map actual = copyOf(originalMap);

        assertTrue(actual instanceof HashMap);
        assertNotSame(originalMap, actual);
        assertSame(expectedElement, actual.get("key"));
    }

    @Test
    public void copyOf_LinkedHasMapWithDeepCloneable_NewLinkedHashMapWithDeepCloneable() {
        DeepCloneable expectedElement = mock(DeepCloneable.class);
        DeepCloneable forCopy = mock(DeepCloneable.class);
        when(forCopy.deepClone()).thenReturn(expectedElement);

        Map originalMap = new LinkedHashMap();
        originalMap.put("key", forCopy);

        Map actual = copyOf(originalMap);

        assertTrue(actual instanceof LinkedHashMap);
        assertNotSame(originalMap, actual);
        assertSame(expectedElement, actual.get("key"));
    }

    @Test
    public void copyOf_HashMapWithNull_NewHashMapWithNull() {
        Map originalMap = new HashMap();
        originalMap.put("key", null);

        Map actual = copyOf(originalMap);

        assertTrue(actual instanceof HashMap);
        assertNotSame(originalMap, actual);
        assertNull(actual.get("key"));
    }

    @Test
    public void copyOf_LinkedHashMapWithNull_NewLinkedHashMapWithNull() {
        Map originalMap = new LinkedHashMap();
        originalMap.put("key", null);

        Map actual = copyOf(originalMap);

        assertTrue(actual instanceof LinkedHashMap);
        assertNotSame(originalMap, actual);
        assertNull(actual.get("key"));
    }

    /*
     * set
     */

    @Test
    public void copyOf_EmptySortedSet_NewEmptySortedSet() {
        SortedSet expected = new TreeSet();
        SortedSet actual = copyOf(expected);

        assertTrue(actual instanceof TreeSet);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    public void copyOf_EmptyHashSet_NewEmptyHashSet() {
        Set expected = new HashSet();
        Set actual = copyOf(expected);

        assertTrue(actual instanceof HashSet);
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    public void copyOf_HashSetWithDeepCloneable_NewHashSetWithDeepCloneable() {
        DeepCloneable expectedElement = mock(DeepCloneable.class);
        DeepCloneable forCopy = mock(DeepCloneable.class);
        when(forCopy.deepClone()).thenReturn(expectedElement);

        Set originalSet = new HashSet();
        originalSet.add(forCopy);

        Set actual = copyOf(originalSet);

        assertTrue(actual instanceof HashSet);
        assertNotSame(originalSet, actual);
        assertTrue(actual.contains(expectedElement));
    }

    @Test
    public void copyOf_HashSetWithNull_NewHashSetWithNull() {
        Set originalSet = new HashSet();
        originalSet.add(null);

        Set actual = copyOf(originalSet);

        assertTrue(actual instanceof HashSet);
        assertNotSame(originalSet, actual);
        assertTrue(actual.contains(null));
    }

    /*
     * arrays
     */

    @Test
    public void copyOf_EmptyArray_NewEmptyArray() {
        DeepCloneable[] expected = new DeepCloneable[0];
        DeepCloneable[] actual = copyOf(expected);

        assertTrue(actual instanceof DeepCloneable[]);
        assertTrue(Arrays.equals(expected, actual));
        assertNotSame(expected, actual);
    }

    @Test
    public void copyOf_ArrayWithDeepCloneable_NewArrayWithDeepCloneable() {
        DeepCloneable expectedElement = mock(DeepCloneable.class);
        DeepCloneable forCopy = mock(DeepCloneable.class);
        when(forCopy.deepClone()).thenReturn(expectedElement);

        DeepCloneable[] originalArray = new DeepCloneable[1];
        originalArray[0] = forCopy;

        DeepCloneable[] actual = copyOf(originalArray);

        assertTrue(actual instanceof DeepCloneable[]);
        assertNotSame(originalArray, actual);
        assertSame(expectedElement, actual[0]);
    }
    private List<SchemaElement> getSchemaElementList(String arg1, String arg2, boolean needNullObject) {
        List<SchemaElement> l1 = new ArrayList<SchemaElement>();
        AbstractResourceGroupElement r1 = new ResourceGroupElement();
        AbstractResourceGroupElement r2 = new ResourceGroupElement();
        r1.setName(arg1);
        r2.setName(arg2);
        l1.add(r1);
        l1.add(r2);
        if(needNullObject){
            l1.add(null);
        }
        return l1;
    }
    @Test
    public void equalGroupElements_test() {
        List<SchemaElement> l1 = getSchemaElementList("test1","test2",true);
        List<SchemaElement> l2 = getSchemaElementList("test2","test1",true);

        assertTrue(ValueObjectUtils.equalGroupElements(l1,l2));
    }

    @Test
    public void sortResourceGroupElement_test() {
        List<SchemaElement> l1 = getSchemaElementList("test1","test2",false);
        List<SchemaElement> l2 = getSchemaElementList("test2","test1", false);

        assertEquals(l1.hashCode(),ValueObjectUtils.sortResourceGroupElement(l2).hashCode());

    }
}
