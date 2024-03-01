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

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientMultiAxisGroupLevelTest extends BaseDTOJSONPresentableTest<ClientMultiAxisGroupLevel> {

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_ALT = "TEST_TYPE_ALT";

    private static final ClientMultiAxisLevelReference TEST_REFERENCE_OBJECT = new ClientMultiAxisLevelReference();
    private static final ClientMultiAxisLevelReference TEST_REFERENCE_OBJECT_ALT = new ClientMultiAxisLevelReference().setName("TEST_NAME");

    private static final List<String> TEST_MEMBERS = Collections.singletonList("MEMBER");
    private static final List<String> TEST_MEMBERS_ALT = Collections.singletonList("MEMBER_ALT");

    @Override
    protected List<ClientMultiAxisGroupLevel> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setType(TEST_TYPE_ALT),
                createFullyConfiguredInstance().setReferenceObject(TEST_REFERENCE_OBJECT_ALT),
                (ClientMultiAxisGroupLevel)createFullyConfiguredInstance().setMembers(TEST_MEMBERS_ALT),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setReferenceObject(null),
                (ClientMultiAxisGroupLevel)createFullyConfiguredInstance().setMembers(null)
        );
    }

    @Override
    protected ClientMultiAxisGroupLevel createFullyConfiguredInstance() {
        ClientMultiAxisGroupLevel instance = createInstanceWithDefaultParameters()
                .setType(TEST_TYPE)
                .setReferenceObject(TEST_REFERENCE_OBJECT);
        return (ClientMultiAxisGroupLevel)instance
                .setMembers(TEST_MEMBERS);
    }

    @Override
    protected ClientMultiAxisGroupLevel createInstanceWithDefaultParameters() {
        return new ClientMultiAxisGroupLevel();
    }

    @Override
    protected ClientMultiAxisGroupLevel createInstanceFromOther(ClientMultiAxisGroupLevel other) {
        return new ClientMultiAxisGroupLevel(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiAxisGroupLevel expected, ClientMultiAxisGroupLevel actual) {
        assertNotSame(expected.getMembers(), actual.getMembers());
        assertNotSame(expected.getReferenceObject(), actual.getReferenceObject());
    }
}