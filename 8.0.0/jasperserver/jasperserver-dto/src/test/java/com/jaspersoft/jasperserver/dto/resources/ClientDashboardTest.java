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
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientDashboardTest extends BaseDTOPresentableTest<ClientDashboard> {

    private static final List<ClientDashboardFoundation> TEST_FOUNDATIONS = Arrays.asList(
            new ClientDashboardFoundation().setId("TEST_ID"),
            new ClientDashboardFoundation().setComponents("TEST_COMPONENTS"),
            new ClientDashboardFoundation().setDescription("TEST_DESCRIPTION"),
            new ClientDashboardFoundation().setLayout("TEST_LAYOUT"),
            new ClientDashboardFoundation().setWiring("TEST_WIRING")
    );
    private static final List<ClientDashboardFoundation> TEST_FOUNDATIONS_1 = Arrays.asList(
            new ClientDashboardFoundation().setId("TEST_ID_1"),
            new ClientDashboardFoundation().setComponents("TEST_COMPONENTS_1"),
            new ClientDashboardFoundation().setDescription("TEST_DESCRIPTION_1"),
            new ClientDashboardFoundation().setLayout("TEST_LAYOUT_1"),
            new ClientDashboardFoundation().setWiring("TEST_WIRING_1")
    );

    private static final List<ClientDashboardResource> TEST_RESOURCES = Arrays.asList(
            new ClientDashboardResource().setName("TEST_NAME"),
            new ClientDashboardResource().setResource(new ClientAdhocDataView().setLabel("TEST_LABEL")),
            new ClientDashboardResource().setType("TEST_TYPE")
    );
    private static final List<ClientDashboardResource> TEST_RESOURCES_1 = Arrays.asList(
            new ClientDashboardResource().setName("TEST_NAME_1"),
            new ClientDashboardResource().setResource(new ClientAdhocDataView().setLabel("TEST_LABEL_1")),
            new ClientDashboardResource().setType("TEST_TYPE_1")
    );

    private static final String TEST_DEFAULT_FOUNDATION = "TEST_DEFAULT_FOUNDATION";
    private static final String TEST_DEFAULT_FOUNDATION_1 = "TEST_DEFAULT_FOUNDATION_1";

    // Base class fields (ClientResource)

    private static final Integer TEST_VERSION = 101;
    private static final Integer TEST_VERSION_1 = 1011;

    private static final Integer TEST_PERMISSION_MASK = 100;
    private static final Integer TEST_PERMISSION_MASK_1 = 1001;

    private static final String TEST_CREATION_DATE = "TEST_CREATION_DATE";
    private static final String TEST_CREATION_DATE_1 = "TEST_CREATION_DATE_1";

    private static final String TEST_UDPATE_DATE = "TEST_UPDATE_DATE";
    private static final String TEST_UDPATE_DATE_1 = "TEST_UPDATE_DATE_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    @Override
    protected List<ClientDashboard> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDefaultFoundation(TEST_DEFAULT_FOUNDATION_1),
                createFullyConfiguredInstance().setFoundations(TEST_FOUNDATIONS_1),
                createFullyConfiguredInstance().setResources(TEST_RESOURCES_1),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                // fields with null values
                createFullyConfiguredInstance().setDefaultFoundation(null),
                createFullyConfiguredInstance().setFoundations(null),
                createFullyConfiguredInstance().setResources(null),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setUpdateDate(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setUri(null)

        );
    }

    @Override
    protected ClientDashboard createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setFoundations(TEST_FOUNDATIONS)
                .setResources(TEST_RESOURCES)
                .setDefaultFoundation(TEST_DEFAULT_FOUNDATION)
                // base class fields (ClientResource)
                .setVersion(TEST_VERSION)
                .setPermissionMask(TEST_PERMISSION_MASK)
                .setCreationDate(TEST_CREATION_DATE)
                .setUpdateDate(TEST_UDPATE_DATE)
                .setLabel(TEST_LABEL)
                .setDescription(TEST_DESCRIPTION)
                .setUri(TEST_URI);
    }

    @Override
    protected ClientDashboard createInstanceWithDefaultParameters() {
        return new ClientDashboard();
    }

    @Override
    protected ClientDashboard createInstanceFromOther(ClientDashboard other) {
        return new ClientDashboard(other);
    }

    @Test
    public void gettersOfFullyConfiguredInstanceReturnCorrectValues() {
        assertEquals(TEST_FOUNDATIONS, fullyConfiguredTestInstance.getFoundations());
        assertEquals(TEST_RESOURCES, fullyConfiguredTestInstance.getResources());
        assertEquals(TEST_DEFAULT_FOUNDATION, fullyConfiguredTestInstance.getDefaultFoundation());
    }
}