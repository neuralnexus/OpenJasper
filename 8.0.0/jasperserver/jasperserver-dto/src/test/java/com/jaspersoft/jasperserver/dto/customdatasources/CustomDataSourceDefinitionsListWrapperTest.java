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

package com.jaspersoft.jasperserver.dto.customdatasources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class CustomDataSourceDefinitionsListWrapperTest extends BaseDTOPresentableTest<CustomDataSourceDefinitionsListWrapper> {

    private static final String TEST_DEFINITION_VALUE = "TEST_DEFINITION_VALUE";
    private static final String TEST_DEFINITION_VALUE_ALT = "TEST_DEFINITION_VALUE_ALT";

    private static final List<String> TEST_DEFINITION = Collections.singletonList(TEST_DEFINITION_VALUE);
    private static final List<String> TEST_DEFINITION_ALT = Collections.singletonList(TEST_DEFINITION_VALUE_ALT);

    @Override
    protected void assertFieldsHaveUniqueReferences(CustomDataSourceDefinitionsListWrapper expected, CustomDataSourceDefinitionsListWrapper actual) {
        assertNotSame(expected.getDefinitions(), actual.getDefinitions());
    }

    /*
     * Preparing
     */

    @Override
    protected List<CustomDataSourceDefinitionsListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDefinitions(TEST_DEFINITION_ALT),
                createFullyConfiguredInstance().setDefinitions(null)
        );
    }

    @Override
    protected CustomDataSourceDefinitionsListWrapper createFullyConfiguredInstance() {
        return new CustomDataSourceDefinitionsListWrapper()
                .setDefinitions(TEST_DEFINITION);
    }

    @Override
    protected CustomDataSourceDefinitionsListWrapper createInstanceWithDefaultParameters() {
        return new CustomDataSourceDefinitionsListWrapper();
    }

    @Override
    protected CustomDataSourceDefinitionsListWrapper createInstanceFromOther(CustomDataSourceDefinitionsListWrapper other) {
        return new CustomDataSourceDefinitionsListWrapper(other);
    }

    @Test
    public void instanceCanBeCreatedWithDefinitions() {
        CustomDataSourceDefinitionsListWrapper instance = new CustomDataSourceDefinitionsListWrapper(TEST_DEFINITION);
        assertEquals(TEST_DEFINITION, instance.getDefinitions());
    }
}
