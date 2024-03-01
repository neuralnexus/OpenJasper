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

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientTenantTest extends BaseDTOPresentableTest<ClientTenant> {

    @Override
    protected List<ClientTenant> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId("24"),
                createFullyConfiguredInstance().setAlias("alias2"),
                createFullyConfiguredInstance().setParentId("52"),
                createFullyConfiguredInstance().setTenantName("name2"),
                createFullyConfiguredInstance().setTenantDesc("description2"),
                createFullyConfiguredInstance().setTenantNote("note2"),
                createFullyConfiguredInstance().setTenantUri("httt://url2.com"),
                createFullyConfiguredInstance().setTenantFolderUri("/test2"),
                createFullyConfiguredInstance().setTheme("theme2"),
                // with null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setAlias(null),
                createFullyConfiguredInstance().setParentId(null),
                createFullyConfiguredInstance().setTenantName(null),
                createFullyConfiguredInstance().setTenantDesc(null),
                createFullyConfiguredInstance().setTenantNote(null),
                createFullyConfiguredInstance().setTenantUri(null),
                createFullyConfiguredInstance().setTenantFolderUri(null),
                createFullyConfiguredInstance().setTheme(null)
        );
    }

    @Override
    protected ClientTenant createFullyConfiguredInstance() {
        ClientTenant clientTenant = new ClientTenant();
        clientTenant.setId("23");
        clientTenant.setAlias("alias");
        clientTenant.setParentId("51");
        clientTenant.setTenantName("name");
        clientTenant.setTenantDesc("description");
        clientTenant.setTenantNote("note");
        clientTenant.setTenantUri("httt://url.com");
        clientTenant.setTenantFolderUri("/test");
        clientTenant.setTheme("theme");
        return clientTenant;
    }

    @Override
    protected ClientTenant createInstanceWithDefaultParameters() {
        return new ClientTenant();
    }

    @Override
    protected ClientTenant createInstanceFromOther(ClientTenant other) {
        return new ClientTenant(other);
    }
}
