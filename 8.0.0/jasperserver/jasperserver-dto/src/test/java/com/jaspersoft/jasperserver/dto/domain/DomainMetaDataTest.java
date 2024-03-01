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

package com.jaspersoft.jasperserver.dto.domain;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DomainMetaDataTest extends BaseDTOPresentableTest<DomainMetaData> {

    private static final Map<String,String> TEST_PROPERTIES = createTestProperites("TEST_KEY", "TEST_VALUE");
    private static final Map<String,String> TEST_PROPERTIES_1 = createTestProperites("TEST_KEY_1", "TEST_VALUE_1");

    private static final List<DomainMetaLevel> TEST_SUB_LEVELS = Collections.singletonList((DomainMetaLevel) new DomainMetaLevel().setId("TEST_ID"));
    private static final List<DomainMetaLevel> TEST_SUB_LEVELS_1 = Collections.singletonList((DomainMetaLevel) new DomainMetaLevel().setId("TEST_ID_1"));
    private static final List<DomainMetaLevel> TEST_SUB_LEVELS_EMPTY = new ArrayList<DomainMetaLevel>();

    private static final DomainMetaLevel TEST_ROOT_LEVEL = new DomainMetaLevel().setSubLevels(TEST_SUB_LEVELS);
    private static final DomainMetaLevel TEST_ROOT_LEVEL_1 = new DomainMetaLevel().setSubLevels(TEST_SUB_LEVELS_1);
    private static final DomainMetaLevel TEST_ROOT_LEVEL_EMPTY = new DomainMetaLevel().setSubLevels(TEST_SUB_LEVELS_EMPTY);

    private static Map<String, String> createTestProperites(String key, String value) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(key, value);
        return properties;
    }

    @Test
    public void test_addProperty_withoutProperties() {
        DomainMetaData instance = createInstanceWithDefaultParameters();

        assertNull(instance.getProperties());
        instance.addProperty("KEY", "VALUE");

        assertTrue(instance.getProperties().containsKey("KEY"));
        assertEquals("VALUE", instance.getProperties().get("KEY"));
    }

    @Test
    public void test_addProperty_withSomeProperties() {
        DomainMetaData instance = createFullyConfiguredInstance();

        assertNotNull(instance.getProperties());
        instance.addProperty("KEY", "VALUE");

        assertTrue(instance.getProperties().containsKey("KEY"));
        assertEquals("VALUE", instance.getProperties().get("KEY"));
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(DomainMetaData expected, DomainMetaData actual) {
        assertNotSame(expected.getProperties(), actual.getProperties());

        assertNotSame(expected.getRootLevel(), actual.getRootLevel());
    }

    /*
     * Preparing
     */

    @Override
    protected List<DomainMetaData> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setProperties(TEST_PROPERTIES_1),
                createFullyConfiguredInstance().setRootLevel(TEST_ROOT_LEVEL_1),
                createFullyConfiguredInstance().setRootLevel(TEST_ROOT_LEVEL_EMPTY),
                // null values
                createFullyConfiguredInstance().setProperties(null),
                createFullyConfiguredInstance().setRootLevel(null)
        );
    }

    @Override
    protected DomainMetaData createFullyConfiguredInstance() {
        return new DomainMetaData()
                .setProperties(TEST_PROPERTIES)
                .setRootLevel(TEST_ROOT_LEVEL);
    }

    @Override
    protected DomainMetaData createInstanceWithDefaultParameters() {
        return new DomainMetaData();
    }

    @Override
    protected DomainMetaData createInstanceFromOther(DomainMetaData other) {
        return new DomainMetaData(other);
    }

}