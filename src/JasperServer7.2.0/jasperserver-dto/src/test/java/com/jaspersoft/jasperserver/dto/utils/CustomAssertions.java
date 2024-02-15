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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Collection;
import java.util.Iterator;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class CustomAssertions {
    public static void assertNotSameCollection(Collection expected, Collection actual) {
        assertNotSame(expected, actual);
        assertEquals(expected.size(), actual.size());

        Iterator iteratorForExpected = expected.iterator();
        Iterator iteratorForActual = actual.iterator();

        while (iteratorForExpected.hasNext()) {
            Object expectedObject = iteratorForExpected.next();
            Object actualObject = iteratorForActual.next();
            assertNotSame(expectedObject, actualObject);
        }
    }

    public static void assertNodesEquals(JsonNode expected, JsonNode actual) {
        Iterator<String> iterator = expected.fieldNames();
        while(iterator.hasNext()) {
            String fieldName = iterator.next();
            JsonNode actualChildNode = actual.get(fieldName);
            JsonNode expectedChildNode = expected.get(fieldName);
            if (expectedChildNode.isIntegralNumber()) {
                assertEquals(expectedChildNode.numberValue().longValue(
                ), actualChildNode.numberValue().longValue());
            } else if (expectedChildNode.isFloatingPointNumber()) {
                assertEquals(expectedChildNode.numberValue().doubleValue(),
                        actualChildNode.numberValue().doubleValue(), 0);
            } else if (expectedChildNode.isArray()) {
                assertArrayNodesEquals((ArrayNode) expectedChildNode,
                        (ArrayNode) actualChildNode);
            } else {
                assertEquals(expectedChildNode, actualChildNode);
            }
        }
    }

    private static void assertArrayNodesEquals(ArrayNode expected, ArrayNode actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        Iterator<JsonNode> iteratorForExpected = expected.iterator();
        Iterator<JsonNode> iteratorForActual = actual.iterator();
        while (iteratorForExpected.hasNext()) {
            JsonNode expectedObject = iteratorForExpected.next();
            JsonNode actualObject = iteratorForActual.next();
            assertNodesEquals(expectedObject, actualObject);
        }
    }

}
