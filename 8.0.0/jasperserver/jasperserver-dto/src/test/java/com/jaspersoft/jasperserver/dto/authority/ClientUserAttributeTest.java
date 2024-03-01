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

public class ClientUserAttributeTest extends BaseDTOPresentableTest<ClientUserAttribute> {

    @Override
    protected List<ClientUserAttribute> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setHolder("holder2"),
                createFullyConfiguredInstance().setValue("value2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setInherited(false),
                createFullyConfiguredInstance().setSecure(false),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setHolder(null),
                createFullyConfiguredInstance().setValue(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setInherited(null),
                createFullyConfiguredInstance().setSecure(null)
        );
    }

    @Override
    protected ClientUserAttribute createFullyConfiguredInstance() {
        ClientUserAttribute clientUserAttribute = new ClientUserAttribute();
        clientUserAttribute.setName("name");
        clientUserAttribute.setDescription("description");
        clientUserAttribute.setHolder("holder");
        clientUserAttribute.setValue("value");
        clientUserAttribute.setPermissionMask(23);
        clientUserAttribute.setInherited(true);
        clientUserAttribute.setSecure(true);
        return clientUserAttribute;
    }

    @Override
    protected ClientUserAttribute createInstanceWithDefaultParameters() {
        return new ClientUserAttribute();
    }

    @Override
    protected ClientUserAttribute createInstanceFromOther(ClientUserAttribute other) {
        return new ClientUserAttribute(other);
    }
}
