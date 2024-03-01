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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientReportUnitResourceTest extends BaseDTOPresentableTest<ClientReportUnitResource> {

    @Override
    protected List<ClientReportUnitResource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setFile(new ClientFile().setContent("content2")),
                // fields with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setFile(null)
        );
    }

    @Override
    protected ClientReportUnitResource createFullyConfiguredInstance() {
        ClientReportUnitResource clientReportUnitResource = new ClientReportUnitResource();
        clientReportUnitResource.setName("name");
        clientReportUnitResource.setFile(new ClientFile().setContent("content"));
        return clientReportUnitResource;
    }

    @Override
    protected ClientReportUnitResource createInstanceWithDefaultParameters() {
        return new ClientReportUnitResource();
    }

    @Override
    protected ClientReportUnitResource createInstanceFromOther(ClientReportUnitResource other) {
        return new ClientReportUnitResource(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientReportUnitResource expected, ClientReportUnitResource actual) {
        assertNotSame(expected.getFile(), actual.getFile());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientReference() {
        ClientReferenceableFile file = new ClientReference().setUri("uri");
        fullyConfiguredTestInstance.setFile(file);

        ClientReportUnitResource result = new ClientReportUnitResource(fullyConfiguredTestInstance);
        assertEquals(file, result.getFile());
    }
}
