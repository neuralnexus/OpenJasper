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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientXmlaConnectionTest extends BaseDTOPresentableTest<ClientXmlaConnection> {

    @Override
    protected List<ClientXmlaConnection> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setCatalog("catalog2"),
                createFullyConfiguredInstance().setDataSource("dataSource2"),
                createFullyConfiguredInstance().setUrl("url2"),
                createFullyConfiguredInstance().setUsername("username2"),
                createFullyConfiguredInstance().setPassword("password2"),
                createFullyConfiguredInstance().setCreationDate("creationDate2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setVersion(24),
                createFullyConfiguredInstance().setUpdateDate("updateDate2"),
                // fields with null values
                createFullyConfiguredInstance().setCatalog(null),
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setUrl(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setPassword(null),
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
    protected ClientXmlaConnection createFullyConfiguredInstance() {
        ClientXmlaConnection ClientXmlaConnection = new ClientXmlaConnection();
        ClientXmlaConnection.setCatalog("catalog");
        ClientXmlaConnection.setDataSource("dataSource");
        ClientXmlaConnection.setUrl("url");
        ClientXmlaConnection.setUsername("username");
        ClientXmlaConnection.setPassword("password");
        ClientXmlaConnection.setCreationDate("creationDate");
        ClientXmlaConnection.setDescription("description");
        ClientXmlaConnection.setLabel("label");
        ClientXmlaConnection.setPermissionMask(23);
        ClientXmlaConnection.setUri("uri");
        ClientXmlaConnection.setVersion(23);
        ClientXmlaConnection.setUpdateDate("updateDate");
        return ClientXmlaConnection;
    }

    @Override
    protected ClientXmlaConnection createInstanceWithDefaultParameters() {
        return new ClientXmlaConnection();
    }

    @Override
    protected ClientXmlaConnection createInstanceFromOther(ClientXmlaConnection other) {
        return new ClientXmlaConnection(other);
    }
}
