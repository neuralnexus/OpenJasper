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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DomainMetaLevelTest extends BaseDTOPresentableTest<DomainMetaLevel> {

    private static final DomainMetaItem TEST_ITEM = (DomainMetaItem) new DomainMetaItem().setId("TEST_ID");
    private static final List<DomainMetaItem> TEST_ITEMS = new ArrayList<DomainMetaItem>(Collections.singletonList(TEST_ITEM));

    private static final DomainMetaItem TEST_ITEM_1 = (DomainMetaItem) new DomainMetaItem().setId("TEST_ID_1");
    private static final List<DomainMetaItem> TEST_ITEMS_1 = Collections.singletonList(TEST_ITEM_1);
    private static final List<DomainMetaItem> TEST_ITEMS_EMPTY = new ArrayList<DomainMetaItem>();

    private static final DomainMetaLevel TEST_SUB_LEVEL = (DomainMetaLevel)new DomainMetaLevel().setId("TEST_ID");
    private static final List<DomainMetaLevel> TEST_SUB_LEVELS = new ArrayList<DomainMetaLevel>(Collections.singletonList(TEST_SUB_LEVEL));

    private static final DomainMetaLevel TEST_SUB_LEVEL_1 = (DomainMetaLevel)new DomainMetaLevel().setId("TEST_ID_1");
    private static final List<DomainMetaLevel> TEST_SUB_LEVELS_1 = Collections.singletonList(TEST_SUB_LEVEL_1);
    private static final List<DomainMetaLevel> TEST_SUB_LEVELS_EMPTY = new ArrayList<DomainMetaLevel>();

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final Map<String, String> TEST_PROPERTIES = createTestMap("TEST_KEY", "TEST_VALUE");
    private static final Map<String, String> TEST_PROPERTIES_1 = createTestMap("TEST_KEY_1", "TEST_VALUE_1");

    private static Map<String, String> createTestMap(String key, String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    }

    @Test
    public void test_addItem_withSomeItems() {
        DomainMetaItem item = new DomainMetaItem();

        DomainMetaLevel instance = createFullyConfiguredInstance();
        instance.addItem(item);
        assertTrue(instance.getItems().contains(TEST_ITEM));
        assertTrue(instance.getItems().contains(item));
    }

    @Test
    public void test_addItem_withoutItems() {
        DomainMetaItem item = new DomainMetaItem();

        DomainMetaLevel instance = createInstanceWithDefaultParameters();
        assertNull(instance.getItems());
        instance.addItem(item);
        assertTrue(instance.getItems().contains(item));
    }

    @Test
    public void test_addSubLevel_withSomeSubLevels() {
        DomainMetaLevel sublevel = new DomainMetaLevel();

        DomainMetaLevel instance = createFullyConfiguredInstance();
        instance.addSubLevel(sublevel);
        assertTrue(instance.getSubLevels().contains(TEST_SUB_LEVEL));
        assertTrue(instance.getSubLevels().contains(sublevel));
    }

    @Test
    public void test_addSubLevel_withoutSomeSubLevels() {
        DomainMetaLevel sublevel = new DomainMetaLevel();

        DomainMetaLevel instance = createInstanceWithDefaultParameters();
        assertNull(instance.getSubLevels());
        instance.addSubLevel(sublevel);

        assertTrue(instance.getSubLevels().contains(sublevel));
    }

    @Test
    public void test_addProperty_withSomeProperties() {
        DomainMetaLevel instance = createFullyConfiguredInstance();

        instance.addProperty("KEY", "VALUE");

        assertTrue(instance.getProperties().containsKey("KEY"));
        assertEquals("VALUE", instance.getProperties().get("KEY"));
    }

    @Test
    public void test_addProperty_withoutProperties() {
        DomainMetaLevel instance = createInstanceWithDefaultParameters();

        assertNull(instance.getProperties());
        instance.addProperty("KEY", "VALUE");

        assertTrue(instance.getProperties().containsKey("KEY"));
        assertEquals("VALUE", instance.getProperties().get("KEY"));
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(DomainMetaLevel expected, DomainMetaLevel actual) {
        assertNotSame(expected.getItems(), actual.getItems());
        assertNotSame(expected.getItems().get(0), actual.getItems().get(0));

        assertNotSame(expected.getSubLevels().get(0), actual.getSubLevels().get(0));
    }

    /*
     * Preparing
     */

    @Override
    protected List<DomainMetaLevel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // parent properties
                (DomainMetaLevel)createFullyConfiguredInstance().setId(TEST_ID_1),
                (DomainMetaLevel)createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                (DomainMetaLevel)createFullyConfiguredInstance().setProperties(TEST_PROPERTIES_1),
                // own properties
                createFullyConfiguredInstance().setItems(TEST_ITEMS_1),
                createFullyConfiguredInstance().setItems(TEST_ITEMS_EMPTY),
                createFullyConfiguredInstance().setSubLevels(TEST_SUB_LEVELS_1),
                createFullyConfiguredInstance().setSubLevels(TEST_SUB_LEVELS_EMPTY),
                // null values
                // parent properties
                (DomainMetaLevel)createFullyConfiguredInstance().setId(null),
                (DomainMetaLevel)createFullyConfiguredInstance().setLabel(null),
                (DomainMetaLevel)createFullyConfiguredInstance().setProperties(null),
                // own properties
                createFullyConfiguredInstance().setItems(null),
                createFullyConfiguredInstance().setSubLevels(null)
        );
    }

    @Override
    protected DomainMetaLevel createFullyConfiguredInstance() {
        // parent properties
        DomainMetaLevel instance = (DomainMetaLevel)new DomainMetaLevel()
                .setId(TEST_ID)
                .setLabel(TEST_LABEL)
                .setProperties(TEST_PROPERTIES);
        return instance
                .setItems(TEST_ITEMS)
                .setSubLevels(TEST_SUB_LEVELS);
    }

    @Override
    protected DomainMetaLevel createInstanceWithDefaultParameters() {
        return new DomainMetaLevel();
    }

    @Override
    protected DomainMetaLevel createInstanceFromOther(DomainMetaLevel other) {
        return new DomainMetaLevel(other);
    }
}
