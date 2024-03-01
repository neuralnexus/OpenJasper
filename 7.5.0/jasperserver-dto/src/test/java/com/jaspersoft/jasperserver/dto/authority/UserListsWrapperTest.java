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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class UserListsWrapperTest extends BaseDTOPresentableTest<UsersListWrapper> {

    @Override
    protected List<UsersListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setUserList(Arrays.asList(new ClientUser().setFullName("name2"), new ClientUser().setPassword("password2"))),
                // with null values
                createFullyConfiguredInstance().setUserList(null)
        );
    }

    @Override
    protected UsersListWrapper createFullyConfiguredInstance() {
        UsersListWrapper organizationsListWrapper = new UsersListWrapper();
        organizationsListWrapper.setUserList(Arrays.asList(new ClientUser(), new ClientUser().setFullName("name")));
        return organizationsListWrapper;
    }

    @Override
    protected UsersListWrapper createInstanceWithDefaultParameters() {
        return new UsersListWrapper();
    }

    @Override
    protected UsersListWrapper createInstanceFromOther(UsersListWrapper other) {
        return new UsersListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(UsersListWrapper expected, UsersListWrapper actual) {
        assertNotSameCollection(expected.getUserList(), actual.getUserList());
    }

    @Test
    public void instanceCreatedByConstructorWithParameterHasNotSameList() {
        UsersListWrapper usersListWrapper = createFullyConfiguredInstance();
        UsersListWrapper createdUsersListWrapper = new UsersListWrapper(usersListWrapper.getUserList());
        assertEquals(usersListWrapper, createdUsersListWrapper);
        assertNotSame(usersListWrapper.getUserList(), createdUsersListWrapper.getUserList());
    }
}
