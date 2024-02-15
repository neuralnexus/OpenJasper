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

class ClientDatasetGroupLevelTest extends BaseDTOJSONPresentableTest<ClientDatasetGroupLevel> {

    private static final String TEST_FIELD_REF = "TEST_FIELD_REF";
    private static final String TEST_FIELD_REF_ALT = "TEST_FIELD_REF_ATL";

    private static final List<String> TEST_MEMBERS = Collections.singletonList("TEST_MEMBER");
    private static final List<String> TEST_MEMBERS_ALT = Collections.singletonList("TEST_MEMBER_ALT");

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_ALT = "TEST_TYPE_ALT";

    private static final List<ClientDatasetFieldReference> TEST_FIELD_REFS = Collections.singletonList(new ClientDatasetFieldReference());
    private static final List<ClientDatasetFieldReference> TEST_FIELD_REFS_ALT = Collections.singletonList(new ClientDatasetFieldReference().setType("TEST_TYPE"));

    @Override
    protected List<ClientDatasetGroupLevel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFieldRef(TEST_FIELD_REF_ALT),
                createFullyConfiguredInstance().setMembers(TEST_MEMBERS_ALT),
                createFullyConfiguredInstance().setType(TEST_TYPE_ALT),
                (ClientDatasetGroupLevel) createFullyConfiguredInstance().setFieldRefs(TEST_FIELD_REFS_ALT),
                createFullyConfiguredInstance().setFieldRef(null),
                createFullyConfiguredInstance().setMembers(null),
                createFullyConfiguredInstance().setType(null),
                (ClientDatasetGroupLevel) createFullyConfiguredInstance().setFieldRefs(null)
        );
    }

    @Override
    protected ClientDatasetGroupLevel createFullyConfiguredInstance() {
        ClientDatasetGroupLevel instance = createInstanceWithDefaultParameters()
                .setFieldRef(TEST_FIELD_REF)
                .setMembers(TEST_MEMBERS)
                .setType(TEST_TYPE);
        return (ClientDatasetGroupLevel) instance.setFieldRefs(TEST_FIELD_REFS);
    }

    @Override
    protected ClientDatasetGroupLevel createInstanceWithDefaultParameters() {
        return new ClientDatasetGroupLevel();
    }

    @Override
    protected ClientDatasetGroupLevel createInstanceFromOther(ClientDatasetGroupLevel other) {
        return new ClientDatasetGroupLevel(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientDatasetGroupLevel expected, ClientDatasetGroupLevel actual) {
        assertNotSame(expected.getMembers(), actual.getMembers());
        assertNotSameCollection(expected.getFieldRefs(), actual.getFieldRefs());
    }
}