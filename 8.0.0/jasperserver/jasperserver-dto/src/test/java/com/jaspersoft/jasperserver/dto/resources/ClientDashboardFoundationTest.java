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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientDashboardFoundationTest extends BaseDTOTest<ClientDashboardFoundation> {

    private static final String ID = "id";
    private static final String DESCRIPTION = "description";
    private static final String LAYOUT = "layout";
    private static final String WIRING = "wiring";
    private static final String COMPONENTS = "components";

    @Override
    protected List<ClientDashboardFoundation> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId("id2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLayout("layout2"),
                createFullyConfiguredInstance().setWiring("wiring2"),
                createFullyConfiguredInstance().setComponents("components2"),
                // with null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLayout(null),
                createFullyConfiguredInstance().setWiring(null),
                createFullyConfiguredInstance().setComponents(null)
        );
    }

    @Override
    protected ClientDashboardFoundation createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setId(ID)
                .setDescription(DESCRIPTION)
                .setLayout(LAYOUT)
                .setWiring(WIRING)
                .setComponents(COMPONENTS);
    }

    @Override
    protected ClientDashboardFoundation createInstanceWithDefaultParameters() {
        return new ClientDashboardFoundation();
    }

    @Override
    protected ClientDashboardFoundation createInstanceFromOther(ClientDashboardFoundation other) {
        return new ClientDashboardFoundation(other);
    }

    @Test
    public void gettersReturnCorrectValue() {
        assertSame(ID, fullyConfiguredTestInstance.getId());
        assertSame(DESCRIPTION, fullyConfiguredTestInstance.getDescription());
        assertSame(LAYOUT, fullyConfiguredTestInstance.getLayout());
        assertSame(WIRING, fullyConfiguredTestInstance.getWiring());
        assertSame(COMPONENTS, fullyConfiguredTestInstance.getComponents());
    }
}