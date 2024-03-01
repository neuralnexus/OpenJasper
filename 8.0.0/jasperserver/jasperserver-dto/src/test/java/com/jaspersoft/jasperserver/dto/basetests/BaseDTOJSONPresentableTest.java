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
package com.jaspersoft.jasperserver.dto.basetests;

import com.fasterxml.jackson.databind.JsonNode;
import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.utils.JSONSerializer;
import org.junit.jupiter.api.Test;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNodesEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */
public abstract class BaseDTOJSONPresentableTest<T extends DeepCloneable> extends BaseDTOTest<T> {

    private final JSONSerializer jsonSerializer = new JSONSerializer();


    @Test
    public void serializationInstanceWithDefaultParametersInJSON() {
        JsonNode expected = jsonSerializer.contentFromInstance(getInstanceWithDefaultParameters());
        JsonNode actual = jsonSerializer.contentFromResourceAtPath(
                pathForEmptyJSON(dtoClass()),
                dtoClass()
        );
        assertNodesEquals(expected, actual);
    }

    @Test
    public void serializationFullyConfiguredInstanceInJSON() {
        JsonNode expected = jsonSerializer.contentFromInstance(getFullyConfiguredInstance());
        JsonNode actual = jsonSerializer.contentFromResourceAtPath(
                pathForJSON(dtoClass()),
                dtoClass()
        );
        assertNodesEquals(expected, actual);
    }

    @Test
    public void deserializationInstanceWithDefaultParametersInJSON() {
        T actual = jsonSerializer.deserializeFromResourceAtPath(
                pathForEmptyJSON(dtoClass()),
                dtoClass()
        );
        assertEquals(getInstanceWithDefaultParameters(), actual);
    }

    @Test
    public void deserializationFullyConfiguredInstanceInJSON() {
        T actual = jsonSerializer.deserializeFromResourceAtPath(
                pathForJSON(dtoClass()),
                dtoClass()
        );
        assertEquals(getFullyConfiguredInstance(), actual);
    }

    protected T getFullyConfiguredInstance() {
        return fullyConfiguredTestInstance;
    }

    protected T getInstanceWithDefaultParameters() {
        return testInstanceWithDefaultParameters;
    }

    /*
     * Helpers
     */

    private Class<T> dtoClass() {
        return (Class<T>) fullyConfiguredTestInstance.getClass();
    }

    private String pathForJSON(Class c) {
        return pathForResourceFromClass(c) + ".json";
    }

    private String pathForEmptyJSON(Class c) {
        return pathForResourceFromClass(c) + "_empty.json";
    }

    private String pathForResourceFromClass(Class c) {
        return "fixtures/" + c.getName().replace(".", "/");
    }
}
