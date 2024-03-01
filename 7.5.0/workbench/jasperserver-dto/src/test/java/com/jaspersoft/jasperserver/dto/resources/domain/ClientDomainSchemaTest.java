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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientDomainSchemaTest extends BaseDTOPresentableTest<ClientDomainSchema> {

    @Override
    protected List<ClientDomainSchema> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setSchema(new Schema().setResources(
                        Collections.<ResourceElement>singletonList( new ResourceSingleElement().setName("name2")))),
                createFullyConfiguredInstance().setCreationDate("creationDate2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setVersion(5),
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setUpdateDate("updateDate2"),
                createFullyConfiguredInstance().setResources(Collections.singletonList(((ResourceElement) new ResourceSingleElement().setName("name2")))),
                createFullyConfiguredInstance().setPresentation(Collections.singletonList(new PresentationGroupElement().setKind("kind2"))),
                // with null values
                createFullyConfiguredInstance().setSchema(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setUpdateDate(null),
                createFullyConfiguredInstance().setResources(null),
                createFullyConfiguredInstance().setPresentation(null)
        );
    }

    @Override
    protected ClientDomainSchema createFullyConfiguredInstance() {
        ClientDomainSchema clientDomainSchema = new ClientDomainSchema();
        clientDomainSchema.setSchema(new Schema().setResources(Collections.singletonList(((ResourceElement) new ResourceSingleElement().setName("name")))));
        clientDomainSchema.setCreationDate("creationDate");
        clientDomainSchema.setDescription("description");
        clientDomainSchema.setLabel("label");
        clientDomainSchema.setPermissionMask(23);
        clientDomainSchema.setVersion(3);
        clientDomainSchema.setUri("uri");
        clientDomainSchema.setUpdateDate("updateDate");
        clientDomainSchema.setResources(Collections.singletonList(((ResourceElement) new ResourceSingleElement().setName("name"))));
        clientDomainSchema.setPresentation(Collections.singletonList(new PresentationGroupElement().setKind("kind")));
        return clientDomainSchema;
    }

    @Override
    protected ClientDomainSchema createInstanceWithDefaultParameters() {
        return new ClientDomainSchema();
    }

    @Override
    protected ClientDomainSchema createInstanceFromOther(ClientDomainSchema other) {
        return new ClientDomainSchema(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientDomainSchema expected, ClientDomainSchema actual) {
        assertNotSame(expected.getSchema(), actual.getSchema());
    }

    @Test
    public void instanceCreatedFromSchemaParameterHaveSameSchema() {
        ClientDomainSchema result = new ClientDomainSchema(fullyConfiguredTestInstance.getSchema());
        assertEquals(fullyConfiguredTestInstance.getSchema(), result.getSchema());
    }

    @Test
    public void instanceCreatedFromNullSchemaParameterHasSchema() {
        ClientDomainSchema result = new ClientDomainSchema(((Schema) null));
        assertEquals(new Schema(), result.getSchema());
    }
}
