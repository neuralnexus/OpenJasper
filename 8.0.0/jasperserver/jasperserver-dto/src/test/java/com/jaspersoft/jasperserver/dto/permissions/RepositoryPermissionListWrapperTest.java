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

package com.jaspersoft.jasperserver.dto.permissions;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class RepositoryPermissionListWrapperTest extends BaseDTOPresentableTest<RepositoryPermissionListWrapper> {

    @Override
    protected List<RepositoryPermissionListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setPermissions(Arrays.asList(new RepositoryPermission().setMask(12), new RepositoryPermission().setUri("uri2"))),
                // with null values
                createFullyConfiguredInstance().setPermissions(null)
        );
    }

    @Override
    protected RepositoryPermissionListWrapper createFullyConfiguredInstance() {
        RepositoryPermissionListWrapper repositoryPermissionListWrapper = new RepositoryPermissionListWrapper();
        repositoryPermissionListWrapper.setPermissions(Arrays.asList(new RepositoryPermission(), new RepositoryPermission().setUri("uri")));
        return repositoryPermissionListWrapper;
    }

    @Override
    protected RepositoryPermissionListWrapper createInstanceWithDefaultParameters() {
        return new RepositoryPermissionListWrapper();
    }

    @Override
    protected RepositoryPermissionListWrapper createInstanceFromOther(RepositoryPermissionListWrapper other) {
        return new RepositoryPermissionListWrapper(other);
    }

    @Test
    public void instanceCanBeCreatedWithParams() {
        RepositoryPermissionListWrapper instance = new RepositoryPermissionListWrapper(fullyConfiguredTestInstance.getPermissions());
        assertEquals(fullyConfiguredTestInstance.getPermissions(), instance.getPermissions());
    }
}
