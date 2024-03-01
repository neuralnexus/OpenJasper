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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientPropertyTest extends BaseDTOPresentableTest<ClientProperty> {

    private static final String TEST_KEY = "TEST_KEY";
    private static final String TEST_KEY_1 = "TEST_KEY_1";

    private static final String TEST_VALUE = "TEST_VALUE";
    private static final String TEST_VALUE_1 = "TEST_VALUE_1";

    @Override
    protected List<ClientProperty> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setKey(TEST_KEY_1),
                createFullyConfiguredInstance().setValue(TEST_VALUE_1),
                // fields with null values
                createFullyConfiguredInstance().setKey(null),
                createFullyConfiguredInstance().setValue(null)
        );
    }

    @Override
    protected ClientProperty createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setKey(TEST_KEY)
                .setValue(TEST_VALUE);
    }

    @Override
    protected ClientProperty createInstanceWithDefaultParameters() {
        return new ClientProperty();
    }

    @Override
    protected ClientProperty createInstanceFromOther(ClientProperty other) {
        return new ClientProperty(other);
    }
}