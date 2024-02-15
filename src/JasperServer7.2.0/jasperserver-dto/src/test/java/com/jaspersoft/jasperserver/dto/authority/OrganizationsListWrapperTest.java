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

public class OrganizationsListWrapperTest extends BaseDTOPresentableTest<OrganizationsListWrapper> {

    @Override
    protected List<OrganizationsListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setList(Arrays.asList(new ClientTenant().setAlias("alias2"), new ClientTenant().setId("30"))),
                // with null values
                createFullyConfiguredInstance().setList(null)
        );
    }

    @Override
    protected OrganizationsListWrapper createFullyConfiguredInstance() {
        OrganizationsListWrapper organizationsListWrapper = new OrganizationsListWrapper();
        organizationsListWrapper.setList(Arrays.asList(new ClientTenant(), new ClientTenant().setId("29")));
        return organizationsListWrapper;
    }

    @Override
    protected OrganizationsListWrapper createInstanceWithDefaultParameters() {
        return new OrganizationsListWrapper();
    }

    @Override
    protected OrganizationsListWrapper createInstanceFromOther(OrganizationsListWrapper other) {
        return new OrganizationsListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(OrganizationsListWrapper expected, OrganizationsListWrapper actual) {
        assertNotSameCollection(expected.getList(), actual.getList());
    }

    @Test
    public void createdWithParamsOrganizationsListWrapperHaveSameLists() {
        OrganizationsListWrapper organizationsListWrapper1 = createFullyConfiguredInstance();
        OrganizationsListWrapper organizationsListWrapper2 = new OrganizationsListWrapper(organizationsListWrapper1.getList());

        assertEquals(organizationsListWrapper1, organizationsListWrapper2);
        assertNotSame(organizationsListWrapper1.getList(), organizationsListWrapper2.getList());
    }
}
