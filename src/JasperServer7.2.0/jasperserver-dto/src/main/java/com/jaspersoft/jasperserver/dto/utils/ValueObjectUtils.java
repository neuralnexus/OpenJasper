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

package com.jaspersoft.jasperserver.dto.utils;

import com.jaspersoft.jasperserver.dto.resources.domain.AbstractResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ConstantsResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

public class ValueObjectUtils {

    private static String NULL_OBJECT_ERROR_MESSAGE = "Null value is not permitted as input value of copy-constructor.";

    private static CloneOperationsContext context = new CloneOperationsContext();

    public static <T> T copyOf(T element) {
        if (element == null) {
            return null;
        }

        CloneOperationsContext.Executable<T> operation = (CloneOperationsContext.Executable<T>) context.operationForClass(element.getClass());
        if (operation == null) {
            return element;
        } else {
            return operation.execute(element);
        }
    }
    public static boolean equalGroupElements(List<? extends SchemaElement> groupElements1, List<? extends SchemaElement> groupElements2) {
        if(groupElements1 == null && groupElements2 == null) return true;
        if(groupElements1 == null || groupElements2 == null) return false;
        if (groupElements1.size() != groupElements2.size())  return false;

        if (!(groupElements1.get(0) instanceof AbstractResourceGroupElement)) {
            return groupElements1.containsAll(groupElements2);
        }
        List<? extends SchemaElement> groupElements1Copy = new ArrayList<SchemaElement>(groupElements1);
        List<? extends SchemaElement> groupElements2Copy = new ArrayList<SchemaElement>(groupElements2);
        Comparator<SchemaElement> comparator = new Comparator<SchemaElement>() {
            public int compare(SchemaElement elem1, SchemaElement elem2) {
                if (elem1 != null && elem2 != null)
                    return ObjectUtils.compare(elem1.getName(), elem2.getName());
                else if (elem1 == null) return  1;
                else if(elem2 == null) return -1;
                else return 0;
            }
        };
        Collections.sort(groupElements1Copy, comparator);
        Collections.sort(groupElements2Copy, comparator);
        boolean result = true;
        for (int i = 0; i < groupElements1Copy.size(); i++) {
            final SchemaElement resourceElement1 = groupElements1Copy.get(i);
            final SchemaElement resourceElement2 = groupElements2Copy.get(i);
            if (resourceElement1 == null && resourceElement2 == null){
                result &= true;
            } else if(resourceElement1 != null && resourceElement2 != null &&resourceElement1.getName().equals(resourceElement2.getName())
                    && resourceElement1.getClass() == resourceElement2.getClass()) {
                if(resourceElement1 instanceof AbstractResourceGroupElement) {
                    result &= equalGroupElements(((AbstractResourceGroupElement) resourceElement1).getElements(),
                            ((AbstractResourceGroupElement) resourceElement2).getElements());
                } else if (resourceElement1 instanceof ConstantsResourceGroupElement) {
                    result &=  equalGroupElements(((ConstantsResourceGroupElement) resourceElement1).getElements(),
                            ((ConstantsResourceGroupElement) resourceElement2).getElements());
                }
            }  else {
                result &= false;
            }
        }
        return result;
    }

    public static List<? extends SchemaElement> sortResourceGroupElement(List<? extends SchemaElement> list) {
        if(list == null || list.isEmpty()) {
            return list;
        }
        Comparator<SchemaElement> comparator = new Comparator<SchemaElement>() {
            public int compare(SchemaElement elem1, SchemaElement elem2) {
                if (elem1 != null && elem2 != null)
                    return ObjectUtils.compare(elem1.getName(), elem2.getName());
                else if (elem1 == null) return  1;
                else if(elem2 == null) return -1;
                else return 0;
            }
        };
        Collections.sort(list, comparator);

        if (list.get(0) instanceof AbstractResourceGroupElement || list.get(0) instanceof ConstantsResourceGroupElement) {
            for (SchemaElement element : list) {
                if (element instanceof AbstractResourceGroupElement) {
                    sortResourceGroupElement(((AbstractResourceGroupElement) element).getElements());
                } else if (element instanceof ConstantsResourceGroupElement) {
                    sortResourceGroupElement(((ConstantsResourceGroupElement) element).getElements());
                }
            }
        }
        return list;
    }

    public static  <T> boolean isListsOfArraysEquals(List<T[]> first, List<T[]> second) {
        if (first == null) {
            return second == null;
        }

        if (second == null) {
            return false;
        }

        if (first.size() != second.size()) {
            return false;
        }

        boolean arraysEquals = true;

        for (int i = 0; i < first.size(); i++) {
            T[] firstItem = first.get(i);
            T[] secondItem = second.get(i);
            arraysEquals = Arrays.equals(firstItem, secondItem);
        }
        return arraysEquals;
    }

    public static <KEY, VALUE> boolean isMapsWithArraysAsValuesEquals(Map<KEY, VALUE[]> first, Map<KEY, VALUE[]> second) {
        if (first == null && second == null) {
            return true;
        }

        if (first != null && second == null || first == null) {
            return false;
        }

        if (first.size() != second.size())
            return false;

        if (!(first.keySet().containsAll(second.keySet())))
            return false;

        for (KEY key : first.keySet()) {
            VALUE[] firstValues = first.get(key);
            VALUE[] secondValues = second.get(key);
            if (!Arrays.equals(firstValues, secondValues))
                return false;
        }

        return true;
    }

    public static <KEY, VALUE> int hashCodeOfMapWithArraysAsValues(Map<KEY, VALUE[]> map) {
        if (map == null) {
            return 0;
        }

        if (map.size() == 0) {
            return 0;
        }

        int result = 0;

        for (KEY key : map.keySet()) {
            result = 31 * result + Arrays.hashCode(map.get(key));
        }

        return result;
    }

    public static <T> int hashCodeOfListOfArrays(List<T[]> list) {
        // TODO: discuss better approach
        if (list == null) {
            return 0;
        }

        if (list.size() == 0) {
            return 0;
        }

        int result = 0;

        for (T[] item : list) {
            result = 31 * result + Arrays.hashCode(item);
        }

        return result;
    }

    public static void checkNotNull(Object object) {
        checkNotNull(object, NULL_OBJECT_ERROR_MESSAGE);
    }

    public static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
