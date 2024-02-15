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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientCustomDataSourceDefinitionTest extends BaseDTOPresentableTest<ClientCustomDataSourceDefinition> {

    private static final String TEST_NAME = "TEST_NAME";
    private static final String TEST_NAME_1 = "TEST_NAME_1";

    private static final String TEST_QUERY_TYPE = "TEST_QUERY_TYPE";
    private static final String TEST_QUERY_TYPE_1 = "TEST_QUERY_TYPE_1";

    private static final CustomDataSourcePropertyDefinition TEST_CUSTOM_DATA_SOURCE_PROPERTY_DEFINITION = new CustomDataSourcePropertyDefinition().setName("TEST_NAME");
    private static final CustomDataSourcePropertyDefinition TEST_CUSTOM_DATA_SOURCE_PROPERTY_DEFINITION_1 = new CustomDataSourcePropertyDefinition().setName("TEST_NAME_1");

    private static final Boolean TEST_TESTABLE = true;
    private static final Boolean TEST_TESTABLE_1 = false;

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientCustomDataSourceDefinition expected, ClientCustomDataSourceDefinition actual) {
        assertNotSame(expected.getQueryTypes(), actual.getQueryTypes());

        assertNotSame(expected.getPropertyDefinitions(), actual.getPropertyDefinitions());
        assertNotSame(expected.getPropertyDefinitions().get(0), actual.getPropertyDefinitions().get(0));
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientCustomDataSourceDefinition> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(TEST_NAME_1),
                createFullyConfiguredInstance().setQueryTypes(Collections.singletonList(TEST_QUERY_TYPE_1)),
                createFullyConfiguredInstance().setPropertyDefinitions(Collections.singletonList(TEST_CUSTOM_DATA_SOURCE_PROPERTY_DEFINITION_1)),
                createFullyConfiguredInstance().setPropertyDefinitions(Collections.<CustomDataSourcePropertyDefinition>emptyList()),
                createFullyConfiguredInstance().setTestable(TEST_TESTABLE_1),
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setQueryTypes(null),
                createFullyConfiguredInstance().setPropertyDefinitions(null),
                createFullyConfiguredInstance().setTestable(null)
        );
    }

    @Override
    protected ClientCustomDataSourceDefinition createFullyConfiguredInstance() {
        return new ClientCustomDataSourceDefinition()
                .setName(TEST_NAME)
                .setQueryTypes(Collections.singletonList(TEST_QUERY_TYPE))
                .setPropertyDefinitions(Collections.singletonList(TEST_CUSTOM_DATA_SOURCE_PROPERTY_DEFINITION))
                .setTestable(TEST_TESTABLE);
    }

    @Override
    protected ClientCustomDataSourceDefinition createInstanceWithDefaultParameters() {
        return new ClientCustomDataSourceDefinition();
    }

    @Override
    protected ClientCustomDataSourceDefinition createInstanceFromOther(ClientCustomDataSourceDefinition other) {
        return new ClientCustomDataSourceDefinition(other);
    }

}
