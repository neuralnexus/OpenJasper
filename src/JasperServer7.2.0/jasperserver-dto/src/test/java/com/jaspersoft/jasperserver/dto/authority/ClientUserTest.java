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

package com.jaspersoft.jasperserver.dto.authority;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientUserTest extends BaseDTOPresentableTest<ClientUser> {

    @Override
    protected List<ClientUser> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFullName("user name2"),
                createFullyConfiguredInstance().setPassword("pass2"),
                createFullyConfiguredInstance().setEmailAddress("email2@test.com"),
                createFullyConfiguredInstance().setExternallyDefined(false),
                createFullyConfiguredInstance().setEnabled(false),
                createFullyConfiguredInstance().setPreviousPasswordChangeTime(new Date(888888888888L)),
                createFullyConfiguredInstance().setTenantId("24"),
                createFullyConfiguredInstance().setUsername("user2"),
                createFullyConfiguredInstance().setRoleSet(new HashSet<ClientRole>(Arrays.asList(new ClientRole().setExternallyDefined(true), new ClientRole().setName("name2")))),
                // with null values
                createFullyConfiguredInstance().setFullName(null),
                createFullyConfiguredInstance().setPassword(null),
                createFullyConfiguredInstance().setEmailAddress(null),
                createFullyConfiguredInstance().setExternallyDefined(null),
                createFullyConfiguredInstance().setEnabled(null),
                createFullyConfiguredInstance().setPreviousPasswordChangeTime(null),
                createFullyConfiguredInstance().setTenantId(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setRoleSet(null)
        );
    }

    @Override
    protected ClientUser createFullyConfiguredInstance() {
        ClientUser clientUser = new ClientUser();
        clientUser.setFullName("user name");
        clientUser.setPassword("pass");
        clientUser.setEmailAddress("email@test.com");
        clientUser.setExternallyDefined(true);
        clientUser.setEnabled(true);
        clientUser.setPreviousPasswordChangeTime(new Date(999999999999L));
        clientUser.setTenantId("23");
        clientUser.setUsername("user");
        clientUser.setRoleSet(new HashSet<ClientRole>(Arrays.asList(new ClientRole(), new ClientRole().setName("name"))));
        return clientUser;
    }

    @Override
    protected ClientUser createInstanceWithDefaultParameters() {
        return new ClientUser();
    }

    @Override
    protected ClientUser createInstanceFromOther(ClientUser other) {
        return new ClientUser(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientUser expected, ClientUser actual) {
        assertNotSame(expected.getPreviousPasswordChangeTime(), actual.getPreviousPasswordChangeTime());
        assertNotSameCollection(expected.getRoleSet(), actual.getRoleSet());
    }
}
