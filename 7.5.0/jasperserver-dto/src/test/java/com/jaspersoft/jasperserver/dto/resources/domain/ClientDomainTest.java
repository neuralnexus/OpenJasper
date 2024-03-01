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

package com.jaspersoft.jasperserver.dto.resources.domain;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.ClientAdhocDataView;
import com.jaspersoft.jasperserver.dto.resources.ClientBundle;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientDomainTest extends BaseDTOPresentableTest<ClientDomain> {

    @Override
    protected List<ClientDomain> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setSchema(new Schema().setPresentation(Collections.singletonList(new PresentationGroupElement()))),
                createFullyConfiguredInstance().setBundles(Collections.singletonList(new ClientBundle().setLocale("locale2"))),
                createFullyConfiguredInstance().setCreationDate("creationDate2"),
                createFullyConfiguredInstance().setDataSource(new ClientAdhocDataView().setLabel("label2")),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setSecurityFile(new ClientFile().setContent("content2")),
                createFullyConfiguredInstance().setVersion(4),
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setUpdateDate("updateDate2"),
                // with null values
                createFullyConfiguredInstance().setSchema(null),
                createFullyConfiguredInstance().setBundles(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setSecurityFile(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setUpdateDate(null)
        );
    }

    @Override
    protected ClientDomain createFullyConfiguredInstance() {
        ClientDomain clientDomain = new ClientDomain();
        clientDomain.setSchema(new Schema().setResources(Collections.singletonList(((ResourceElement) new ResourceSingleElement().setName("name")))));
        clientDomain.setBundles(Arrays.asList(new ClientBundle(), new ClientBundle().setLocale("locale")));
        clientDomain.setCreationDate("creationDate");
        clientDomain.setDataSource(new ClientAdhocDataView().setLabel("label"));
        clientDomain.setDescription("description");
        clientDomain.setLabel("label");
        clientDomain.setPermissionMask(23);
        clientDomain.setSecurityFile(new ClientFile().setContent("content"));
        clientDomain.setVersion(3);
        clientDomain.setUri("uri");
        clientDomain.setUpdateDate("updateDate");
        return clientDomain;
    }

    @Override
    protected ClientDomain createInstanceWithDefaultParameters() {
        return new ClientDomain();
    }

    @Override
    protected ClientDomain createInstanceFromOther(ClientDomain other) {
        return new ClientDomain(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientDomain expected, ClientDomain actual) {
        assertNotSame(expected.getSchema(), actual.getSchema());
        assertNotSame(expected.getDataSource(), actual.getDataSource());
        assertNotSame(expected.getSecurityFile(), actual.getSecurityFile());
        assertNotSameCollection(expected.getBundles(), actual.getBundles());
    }
}
