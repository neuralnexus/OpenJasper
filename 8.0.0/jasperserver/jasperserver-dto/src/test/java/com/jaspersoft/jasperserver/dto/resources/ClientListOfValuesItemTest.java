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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientListOfValuesItemTest extends BaseDTOPresentableTest<ClientListOfValuesItem> {

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_VALUE = "TEST_VALUE";
    private static final String TEST_VALUE_1 = "TEST_VALUE_1";

    @Override
    protected List<ClientListOfValuesItem> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setValue(TEST_VALUE_1),
                // fields with null values
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setValue(null)
        );
    }

    @Override
    protected ClientListOfValuesItem createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setLabel(TEST_LABEL)
                .setValue(TEST_VALUE);
    }

    @Override
    protected ClientListOfValuesItem createInstanceWithDefaultParameters() {
        return new ClientListOfValuesItem();
    }

    @Override
    protected ClientListOfValuesItem createInstanceFromOther(ClientListOfValuesItem other) {
        return new ClientListOfValuesItem(other);
    }

    @Test
    public void instanceCanBeCreatedFromLabelAndValueParameters() {
        String label = "label";
        String value = "value";

        ClientListOfValuesItem result = new ClientListOfValuesItem(label, value);

        assertEquals(label, result.getLabel());
        assertEquals(value, result.getValue());
    }
}