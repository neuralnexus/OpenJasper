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

package com.jaspersoft.jasperserver.dto.resources;


import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ClientVirtualDataSourceTest extends BaseDTOPresentableTest<ClientVirtualDataSource> {

    @Override
    protected List<ClientVirtualDataSource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setSubDataSources(Arrays.asList(new ClientSubDataSourceReference(), new ClientSubDataSourceReference().setId("id2"))),
                createFullyConfiguredInstance().setCreationDate("creationDate2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setVersion(24),
                createFullyConfiguredInstance().setUpdateDate("updateDate2"),
                // fields with null values
                createFullyConfiguredInstance().setSubDataSources(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setUpdateDate(null)
        );
    }

    @Override
    protected ClientVirtualDataSource createFullyConfiguredInstance() {
        ClientVirtualDataSource ClientVirtualDataSource = new ClientVirtualDataSource();
        ClientVirtualDataSource.setSubDataSources(Arrays.asList(new ClientSubDataSourceReference(), new ClientSubDataSourceReference().setId("id")));
        ClientVirtualDataSource.setCreationDate("creationDate");
        ClientVirtualDataSource.setDescription("description");
        ClientVirtualDataSource.setLabel("label");
        ClientVirtualDataSource.setPermissionMask(23);
        ClientVirtualDataSource.setUri("uri");
        ClientVirtualDataSource.setVersion(23);
        ClientVirtualDataSource.setUpdateDate("updateDate");
        return ClientVirtualDataSource;
    }

    @Override
    protected ClientVirtualDataSource createInstanceWithDefaultParameters() {
        return new ClientVirtualDataSource();
    }

    @Override
    protected ClientVirtualDataSource createInstanceFromOther(ClientVirtualDataSource other) {
        return new ClientVirtualDataSource(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientVirtualDataSource expected, ClientVirtualDataSource actual) {
        assertNotSameCollection(expected.getSubDataSources(), actual.getSubDataSources());
    }
}
