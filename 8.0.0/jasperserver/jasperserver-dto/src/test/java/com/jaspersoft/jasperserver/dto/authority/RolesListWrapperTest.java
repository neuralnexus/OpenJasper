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

public class RolesListWrapperTest extends BaseDTOPresentableTest<RolesListWrapper> {

    @Override
    protected List<RolesListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setRoleList(Arrays.asList(new ClientRole().setName("name2"), new ClientRole().setTenantId("30"))),
                // with null values
                createFullyConfiguredInstance().setRoleList(null)
        );
    }

    @Override
    protected RolesListWrapper createFullyConfiguredInstance() {
        RolesListWrapper organizationsListWrapper = new RolesListWrapper();
        organizationsListWrapper.setRoleList(Arrays.asList(new ClientRole(), new ClientRole().setTenantId("29")));
        return organizationsListWrapper;
    }

    @Override
    protected RolesListWrapper createInstanceWithDefaultParameters() {
        return new RolesListWrapper();
    }

    @Override
    protected RolesListWrapper createInstanceFromOther(RolesListWrapper other) {
        return new RolesListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(RolesListWrapper expected, RolesListWrapper actual) {
        assertNotSameCollection(expected.getRoleList(), actual.getRoleList());
    }

    @Test
    public void instanceCreatedByConstructorWithParameterHasNotSameList() {
        RolesListWrapper rolesListWrapper = createFullyConfiguredInstance();
        RolesListWrapper createdRolesListWrapper = new RolesListWrapper(rolesListWrapper.getRoleList());

        assertEquals(rolesListWrapper, createdRolesListWrapper);
        assertNotSame(rolesListWrapper.getRoleList(), createdRolesListWrapper.getRoleList());
    }
}
