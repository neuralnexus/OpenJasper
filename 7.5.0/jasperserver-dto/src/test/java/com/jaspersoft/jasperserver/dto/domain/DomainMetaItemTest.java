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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DomainMetaItemTest extends BaseDTOPresentableTest<DomainMetaItem> {

    private static final Map<String,String> TEST_PROPERTIES = createTestProperites("TEST_KEY", "TEST_VALUE");
    private static final Map<String,String> TEST_PROPERTIES_1 = createTestProperites("TEST_KEY_1", "TEST_VALUE_1");

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static Map<String, String> createTestProperites(String key, String value) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(key, value);
        return properties;
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(DomainMetaItem expected, DomainMetaItem actual) {
        assertNotSame(expected.getProperties(), actual.getProperties());
    }

    /*
     * Preparing
     */

    @Override
    protected List<DomainMetaItem> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                (DomainMetaItem) createFullyConfiguredInstance().setId(TEST_ID_1),
                (DomainMetaItem) createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                (DomainMetaItem) createFullyConfiguredInstance().setProperties(TEST_PROPERTIES_1),
                // null values
                (DomainMetaItem) createFullyConfiguredInstance().setId(null),
                (DomainMetaItem) createFullyConfiguredInstance().setLabel(null),
                (DomainMetaItem) createFullyConfiguredInstance().setProperties(null)
        );
    }

    @Override
    protected DomainMetaItem createFullyConfiguredInstance() {
        return (DomainMetaItem) new DomainMetaItem()
                .setId(TEST_ID)
                .setLabel(TEST_LABEL)
                .setProperties(TEST_PROPERTIES);
    }

    @Override
    protected DomainMetaItem createInstanceWithDefaultParameters() {
        return new DomainMetaItem();
    }

    @Override
    protected DomainMetaItem createInstanceFromOther(DomainMetaItem other) {
        return new DomainMetaItem(other);
    }

}
