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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DomElVariableTest extends BaseDTOPresentableTest<DomElVariable> {

    private static final String TEST_NAME = "TEST_NAME";
    private static final String TEST_NAME_1 = "TEST_NAME_1";

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_1 = "TEST_TYPE_1";

    @Test
    public void testGetters() {
        DomElVariable instance = createFullyConfiguredInstance();
        assertEquals(TEST_NAME, instance.getName());
        assertEquals(TEST_TYPE, instance.getType());
    }

    /*
     * Preparing
     */

    @Override
    protected List<DomElVariable> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(TEST_NAME_1),
                createFullyConfiguredInstance().setType(TEST_TYPE_1),
                // null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setType(null)
        );
    }

    @Override
    protected DomElVariable createFullyConfiguredInstance() {
        return new DomElVariable()
                .setName(TEST_NAME)
                .setType(TEST_TYPE);
    }

    @Override
    protected DomElVariable createInstanceWithDefaultParameters() {
        return new DomElVariable();
    }

    @Override
    protected DomElVariable createInstanceFromOther(DomElVariable other) {
        return new DomElVariable(other);
    }
}
