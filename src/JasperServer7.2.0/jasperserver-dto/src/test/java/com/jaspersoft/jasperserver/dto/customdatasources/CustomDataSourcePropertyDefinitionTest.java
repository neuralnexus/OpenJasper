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

package com.jaspersoft.jasperserver.dto.customdatasources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class CustomDataSourcePropertyDefinitionTest extends BaseDTOPresentableTest<CustomDataSourcePropertyDefinition> {

    private static final String TEST_NAME = "TEST_NAME";
    private static final String TEST_NAME_1 = "TEST_NAME_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_DEFAULT_VALUE = "TEST_DEFAULT_VALUE";
    private static final String TEST_DEFAULT_VALUE_1 = "TEST_DEFAULT_VALUE_1";

    private static final ClientProperty TEST_PROPERTY = new ClientProperty().setKey("TEST_KEY");
    private static final ClientProperty TEST_PROPERTY_1 = new ClientProperty().setKey("TEST_KEY_1");

    @Override
    protected void assertFieldsHaveUniqueReferences(CustomDataSourcePropertyDefinition expected, CustomDataSourcePropertyDefinition actual) {
        assertNotSame(expected.getProperties(), actual.getProperties());
        assertNotSame(expected.getProperties().get(0), actual.getProperties().get(0));
    }

    /*
     * Preparing
     */

    @Override
    protected List<CustomDataSourcePropertyDefinition> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(TEST_NAME_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDefaultValue(TEST_DEFAULT_VALUE_1),
                createFullyConfiguredInstance().setProperties(Collections.singletonList(TEST_PROPERTY_1)),
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setDefaultValue(null),
                createFullyConfiguredInstance().setProperties(null)
        );
    }

    @Override
    protected CustomDataSourcePropertyDefinition createFullyConfiguredInstance() {
        return new CustomDataSourcePropertyDefinition()
                .setName(TEST_NAME)
                .setLabel(TEST_LABEL)
                .setDefaultValue(TEST_DEFAULT_VALUE)
                .setProperties(Collections.singletonList(TEST_PROPERTY));
    }

    @Override
    protected CustomDataSourcePropertyDefinition createInstanceWithDefaultParameters() {
        return new CustomDataSourcePropertyDefinition();
    }

    @Override
    protected CustomDataSourcePropertyDefinition createInstanceFromOther(CustomDataSourcePropertyDefinition other) {
        return new CustomDataSourcePropertyDefinition(other);
    }
}
