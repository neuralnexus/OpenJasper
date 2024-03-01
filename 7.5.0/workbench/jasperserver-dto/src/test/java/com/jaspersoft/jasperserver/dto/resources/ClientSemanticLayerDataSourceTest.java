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

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientSemanticLayerDataSourceTest extends BaseDTOPresentableTest<ClientSemanticLayerDataSource> {

    @Override
    protected List<ClientSemanticLayerDataSource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setBundles(Arrays.asList(new ClientBundle(), new ClientBundle().setLocale("locale2"))),
                createFullyConfiguredInstance().setSecurityFile(new ClientFile().setContent("content2")),
                createFullyConfiguredInstance().setCreationDate("creationDate2"),
                createFullyConfiguredInstance().setDataSource(new ClientAdhocDataView().setLabel("label2")),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setSchema(new ClientFile().setContent("content2")),
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setVersion(24),
                createFullyConfiguredInstance().setUpdateDate("updateDate2"),
                // fields with null values
                createFullyConfiguredInstance().setBundles(null),
                createFullyConfiguredInstance().setSecurityFile(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setSchema(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setUpdateDate(null)
        );
    }

    @Override
    protected ClientSemanticLayerDataSource createFullyConfiguredInstance() {
        ClientSemanticLayerDataSource clientSemanticLayerDataSource = new ClientSemanticLayerDataSource();
        clientSemanticLayerDataSource.setBundles(Arrays.asList(new ClientBundle(), new ClientBundle().setLocale("locale")));
        clientSemanticLayerDataSource.setSecurityFile(new ClientFile().setContent("content"));
        clientSemanticLayerDataSource.setCreationDate("creationDate");
        clientSemanticLayerDataSource.setDataSource(new ClientAdhocDataView().setLabel("label"));
        clientSemanticLayerDataSource.setDescription("description");
        clientSemanticLayerDataSource.setLabel("label");
        clientSemanticLayerDataSource.setPermissionMask(23);
        clientSemanticLayerDataSource.setSchema(new ClientFile().setContent("content"));
        clientSemanticLayerDataSource.setUri("uri");
        clientSemanticLayerDataSource.setVersion(23);
        clientSemanticLayerDataSource.setUpdateDate("updateDate");
        return clientSemanticLayerDataSource;
    }

    @Override
    protected ClientSemanticLayerDataSource createInstanceWithDefaultParameters() {
        return new ClientSemanticLayerDataSource();
    }

    @Override
    protected ClientSemanticLayerDataSource createInstanceFromOther(ClientSemanticLayerDataSource other) {
        return new ClientSemanticLayerDataSource(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientSemanticLayerDataSource expected, ClientSemanticLayerDataSource actual) {
        assertNotSameCollection(expected.getBundles(), actual.getBundles());
        assertNotSame(expected.getDataSource(), actual.getDataSource());
        assertNotSame(expected.getSecurityFile(), actual.getSecurityFile());
        assertNotSame(expected.getSchema(), actual.getSchema());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientReferenceSchema() {
        ClientReferenceableFile schema = new ClientReference();
        fullyConfiguredTestInstance.setSchema(schema);

        ClientSemanticLayerDataSource result = new ClientSemanticLayerDataSource(fullyConfiguredTestInstance);

        assertEquals(schema, result.getSchema());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientReferenceSecurityFile() {
        ClientReferenceableFile securityFile = new ClientReference().setUri("uri");
        fullyConfiguredTestInstance.setSecurityFile(securityFile);

        ClientSemanticLayerDataSource result = new ClientSemanticLayerDataSource(fullyConfiguredTestInstance);

        assertEquals(securityFile, result.getSecurityFile());
    }
}
