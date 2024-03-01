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

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientReportUnitResourceListWrapperTest extends BaseDTOTest<ClientReportUnitResourceListWrapper> {

    @Override
    protected List<ClientReportUnitResourceListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFiles(Arrays.asList(new ClientReportUnitResource(), new ClientReportUnitResource().setName("name2"))),
                // fields with null values
                createFullyConfiguredInstance().setFiles(null)
        );
    }

    @Override
    protected ClientReportUnitResourceListWrapper createFullyConfiguredInstance() {
        ClientReportUnitResourceListWrapper clientReportUnitResourceListWrapper = new ClientReportUnitResourceListWrapper();
        clientReportUnitResourceListWrapper.setFiles(Arrays.asList(new ClientReportUnitResource(), new ClientReportUnitResource().setName("name")));
        return clientReportUnitResourceListWrapper;
    }

    @Override
    protected ClientReportUnitResourceListWrapper createInstanceWithDefaultParameters() {
        return new ClientReportUnitResourceListWrapper();
    }

    @Override
    protected ClientReportUnitResourceListWrapper createInstanceFromOther(ClientReportUnitResourceListWrapper other) {
        return new ClientReportUnitResourceListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientReportUnitResourceListWrapper expected, ClientReportUnitResourceListWrapper actual) {
        assertNotSameCollection(expected.getFiles(), actual.getFiles());
    }
}
