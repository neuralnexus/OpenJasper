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

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NULL_SUBSTITUTION_VALUE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class BsonObjectIdDataConverterTest {
    BsonObjectIdDataConverter bsonObjectIdDataConverter;

    @Before
    public void setup() {
        bsonObjectIdDataConverter = new BsonObjectIdDataConverter();
    }

    @Test
    public void stringToValue() throws Exception {
        assertEquals(bsonObjectIdDataConverter.stringToValue(NULL_SUBSTITUTION_VALUE), null);

        ObjectId expected = new ObjectId("58d1c36efb0cac4e15afd278");
        ObjectId actual = bsonObjectIdDataConverter.stringToValue("58d1c36efb0cac4e15afd278");
        assertThat(expected, is(actual));
    }

    @Test
    public void valueToString() {
        String expected = "58d1c36efb0cac4e15afd278";
        String actual = bsonObjectIdDataConverter.valueToString(new ObjectId("58d1c36efb0cac4e15afd278"));
        assertThat(expected, is(actual));
    }
}