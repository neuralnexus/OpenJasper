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

package com.jaspersoft.jasperserver.dto.adhoc.query.order;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientGenericOrderTest extends BaseDTOJSONPresentableTest<ClientGenericOrder> {

    private static final Boolean TEST_ASCENDING = Boolean.TRUE;
    private static final Boolean TEST_ASCENDING_ALT = Boolean.FALSE;

    private static final String TEST_FIELD_REFERENCE = "TEST_FIELD_REFERENCE";
    private static final String TEST_FIELD_REFERENCE_ALT = "TEST_FIELD_REFERENCE_ALT";

    private static final Boolean TEST_AGGREGATION = Boolean.TRUE;
    private static final Boolean TEST_AGGREGATION_ALT = Boolean.FALSE;

    @Override
    protected List<ClientGenericOrder> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAscending(TEST_ASCENDING_ALT),
                createFullyConfiguredInstance().setFieldReference(TEST_FIELD_REFERENCE_ALT),
                createFullyConfiguredInstance().setAggregation(TEST_AGGREGATION_ALT),
                createFullyConfiguredInstance().setAscending(null),
                createFullyConfiguredInstance().setFieldReference(null),
                createFullyConfiguredInstance().setAggregation(null)
        );
    }

    @Override
    protected ClientGenericOrder createFullyConfiguredInstance() {
        return new ClientGenericOrder()
                .setAscending(TEST_ASCENDING)
                .setFieldReference(TEST_FIELD_REFERENCE)
                .setAggregation(TEST_AGGREGATION);
    }

    @Override
    protected ClientGenericOrder createInstanceWithDefaultParameters() {
        return new ClientGenericOrder();
    }

    @Override
    protected ClientGenericOrder createInstanceFromOther(ClientGenericOrder other) {
        return new ClientGenericOrder(other);
    }

    @Test
    public void isAggregation_default() {
        ClientGenericOrder instance = new ClientGenericOrder();
        assertNull(instance.isAggregation());
    }

    @Test
    public void isAggregation_non_default() {
        ClientGenericOrder instance = new ClientGenericOrder().setAggregation(Boolean.TRUE);
        assertTrue(instance.isAggregation());
    }
}