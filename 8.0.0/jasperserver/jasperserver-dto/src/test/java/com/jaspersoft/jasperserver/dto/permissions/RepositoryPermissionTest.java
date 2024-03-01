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

public class RepositoryPermissionTest extends BaseDTOPresentableTest<RepositoryPermission> {

    @Override
    protected List<RepositoryPermission> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setMask(24),
                createFullyConfiguredInstance().setRecipient("rec1"),
                // with null values
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setMask(null),
                createFullyConfiguredInstance().setRecipient(null)
        );
    }

    @Override
    protected RepositoryPermission createFullyConfiguredInstance() {
        RepositoryPermission repositoryPermission = new RepositoryPermission();
        repositoryPermission.setUri("uri");
        repositoryPermission.setMask(23);
        repositoryPermission.setRecipient("rec");
        return repositoryPermission;
    }

    @Override
    protected RepositoryPermission createInstanceWithDefaultParameters() {
        return new RepositoryPermission();
    }

    @Override
    protected RepositoryPermission createInstanceFromOther(RepositoryPermission other) {
        return new RepositoryPermission(other);
    }

    @Test
    public void instanceCanBeCreatedWithParams() {
        RepositoryPermission instance = new RepositoryPermission(fullyConfiguredTestInstance.getUri(),
                fullyConfiguredTestInstance.getRecipient(), fullyConfiguredTestInstance.getMask());
        assertEquals(fullyConfiguredTestInstance, instance);
    }
}
