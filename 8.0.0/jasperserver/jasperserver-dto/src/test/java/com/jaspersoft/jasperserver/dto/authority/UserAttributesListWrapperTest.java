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

public class UserAttributesListWrapperTest extends BaseDTOPresentableTest<UserAttributesListWrapper> {

    @Override
    protected List<UserAttributesListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setProfileAttributes(Arrays.asList(new ClientAttribute().setName("name2"), new ClientAttribute().setHolder("holder2"))),
                // with null values
                createFullyConfiguredInstance().setProfileAttributes(null)
        );
    }

    @Override
    protected UserAttributesListWrapper createFullyConfiguredInstance() {
        UserAttributesListWrapper organizationsListWrapper = new UserAttributesListWrapper();
        organizationsListWrapper.setProfileAttributes(Arrays.asList(new ClientAttribute(), new ClientAttribute().setName("name")));
        return organizationsListWrapper;
    }

    @Override
    protected UserAttributesListWrapper createInstanceWithDefaultParameters() {
        return new UserAttributesListWrapper();
    }

    @Override
    protected UserAttributesListWrapper createInstanceFromOther(UserAttributesListWrapper other) {
        return new UserAttributesListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(UserAttributesListWrapper expected, UserAttributesListWrapper actual) {
        assertNotSameCollection(expected.getProfileAttributes(), actual.getProfileAttributes());
    }

    @Test
    public void instanceCreatedByConstructorWithParameterHasNotSameList() {
        UserAttributesListWrapper userAttributesListWrapper = createFullyConfiguredInstance();
        UserAttributesListWrapper createdUserAttributesListWrapper = new UserAttributesListWrapper(userAttributesListWrapper.getProfileAttributes());
        assertEquals(userAttributesListWrapper, createdUserAttributesListWrapper);
        assertNotSame(userAttributesListWrapper.getProfileAttributes(), createdUserAttributesListWrapper.getProfileAttributes());
    }
}
