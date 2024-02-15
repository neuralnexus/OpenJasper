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

package com.jaspersoft.jasperserver.dto.adhoc.component;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientGenericComponentTest extends BaseDTOTest<ClientGenericComponent> {
    private static final String COMPONENT_TYPE = "componentType";
    private static final List<ClientGenericComponent> COMPONENT_LIST = Collections.singletonList(new ClientGenericComponent().setComponentType("type"));
    public static final String KEY = "key";
    public static final Object VALUE = "value";
    private static final Map<String, Object> PROPERTIES = Collections.singletonMap(KEY, VALUE);

    private static final String COMPONENT_TYPE_2 = "componentType2";
    private static final List<ClientGenericComponent> COMPONENT_LIST_2 = Collections.singletonList(new ClientGenericComponent().setComponentType("type2"));
    private static final Map<String, Object> PROPERTIES_2 = Collections.singletonMap("key2", ((Object) "value2"));

    @Override
    protected List<ClientGenericComponent> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setComponentType(COMPONENT_TYPE_2),
                createFullyConfiguredInstance().setComponents(COMPONENT_LIST_2),
                createFullyConfiguredInstance().setProperties(PROPERTIES_2),
                // with null values
                createFullyConfiguredInstance().setComponentType(null),
                createFullyConfiguredInstance().setComponents(null),
                createFullyConfiguredInstance().setProperties(null)
        );
    }

    @Override
    protected ClientGenericComponent createFullyConfiguredInstance() {
        return new ClientGenericComponent()
                .setComponentType(COMPONENT_TYPE)
                .setComponents(COMPONENT_LIST)
                .setProperties(PROPERTIES);
    }

    @Override
    protected ClientGenericComponent createInstanceWithDefaultParameters() {
        return new ClientGenericComponent();
    }

    @Override
    protected ClientGenericComponent createInstanceFromOther(ClientGenericComponent other) {
        return new ClientGenericComponent(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientGenericComponent expected, ClientGenericComponent actual) {
        assertNotSameCollection(expected.getComponents(), actual.getComponents());
        assertNotSame(expected.getProperties(), actual.getProperties());
    }

    @Test
    public void propertyCanBeSetCorrectly() {
        ClientGenericComponent instance = new ClientGenericComponent();
        instance.setProperty(KEY, VALUE);
        assertEquals(VALUE, instance.getProperty(KEY));
    }
}
