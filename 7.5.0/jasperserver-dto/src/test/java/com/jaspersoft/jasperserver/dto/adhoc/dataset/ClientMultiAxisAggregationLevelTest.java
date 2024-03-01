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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientMultiAxisAggregationLevelTest extends BaseDTOJSONPresentableTest<ClientMultiAxisAggregationLevel> {

    private static final List<ClientDatasetFieldReference> TEST_FIELDS = Collections.singletonList(
            new ClientDatasetFieldReference()
    );
    private static final List<ClientDatasetFieldReference> TEST_FIELDS_ALT = Collections.singletonList(
            new ClientDatasetFieldReference().setType("TYPE")
    );
    private static final List<String> TEST_MEMBERS = Collections.singletonList("MEMBER");
    private static final List<String> TEST_MEMBERS_ALT = Collections.singletonList("MEMBER_ALT");

    @Override
    protected List<ClientMultiAxisAggregationLevel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFields(TEST_FIELDS_ALT),
                (ClientMultiAxisAggregationLevel)createFullyConfiguredInstance().setMembers(TEST_MEMBERS_ALT),
                createFullyConfiguredInstance().setFields(null),
                (ClientMultiAxisAggregationLevel)createFullyConfiguredInstance().setMembers(null)
        );
    }

    @Override
    protected ClientMultiAxisAggregationLevel createFullyConfiguredInstance() {
        ClientMultiAxisAggregationLevel instance = createInstanceWithDefaultParameters()
                .setFields(TEST_FIELDS);
        return (ClientMultiAxisAggregationLevel)instance
                .setMembers(TEST_MEMBERS);
    }

    @Override
    protected ClientMultiAxisAggregationLevel createInstanceWithDefaultParameters() {
        return new ClientMultiAxisAggregationLevel();
    }

    @Override
    protected ClientMultiAxisAggregationLevel createInstanceFromOther(ClientMultiAxisAggregationLevel other) {
        return new ClientMultiAxisAggregationLevel(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiAxisAggregationLevel expected, ClientMultiAxisAggregationLevel actual) {
        assertNotSame(expected.getMembers(), actual.getMembers());
        assertNotSameCollection(expected.getFields(), actual.getFields());
    }
}