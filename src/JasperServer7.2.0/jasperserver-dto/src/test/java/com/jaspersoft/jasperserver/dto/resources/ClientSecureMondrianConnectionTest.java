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
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientSecureMondrianConnectionTest extends BaseDTOPresentableTest<ClientSecureMondrianConnection> {

    @Override
    protected List<ClientSecureMondrianConnection> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAccessGrants(Arrays.<ClientReferenceableFile>asList(new ClientFile(), new ClientFile().setContent("content2"))),
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
                createFullyConfiguredInstance().setAccessGrants(null),
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
    protected ClientSecureMondrianConnection createFullyConfiguredInstance() {
        ClientSecureMondrianConnection clientSecureMondrianConnection = new ClientSecureMondrianConnection();
        clientSecureMondrianConnection.setAccessGrants(Arrays.<ClientReferenceableFile>asList((new ClientFile()), new ClientFile().setContent("content")));
        clientSecureMondrianConnection.setCreationDate("creationDate");
        clientSecureMondrianConnection.setDataSource(new ClientAdhocDataView().setLabel("label"));
        clientSecureMondrianConnection.setDescription("description");
        clientSecureMondrianConnection.setLabel("label");
        clientSecureMondrianConnection.setPermissionMask(23);
        clientSecureMondrianConnection.setSchema(new ClientFile().setContent("content"));
        clientSecureMondrianConnection.setUri("uri");
        clientSecureMondrianConnection.setVersion(23);
        clientSecureMondrianConnection.setUpdateDate("updateDate");
        return clientSecureMondrianConnection;
    }

    @Override
    protected ClientSecureMondrianConnection createInstanceWithDefaultParameters() {
        return new ClientSecureMondrianConnection();
    }

    @Override
    protected ClientSecureMondrianConnection createInstanceFromOther(ClientSecureMondrianConnection other) {
        return new ClientSecureMondrianConnection(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientSecureMondrianConnection expected, ClientSecureMondrianConnection actual) {
        assertNotSameCollection(expected.getAccessGrants(), actual.getAccessGrants());
        assertNotSame(expected.getDataSource(), actual.getDataSource());
        assertNotSame(expected.getSchema(), actual.getSchema());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientReference() {
        List<ClientReferenceableFile> accessGrants = Collections.<ClientReferenceableFile>singletonList(new ClientReference().setUri("uri"));
        fullyConfiguredTestInstance.setAccessGrants(accessGrants);

        ClientSecureMondrianConnection result = new ClientSecureMondrianConnection(fullyConfiguredTestInstance);

        assertEquals(accessGrants, result.getAccessGrants());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientReferenceSchema() {
        ClientReferenceableFile schema = new ClientReference();
        fullyConfiguredTestInstance.setSchema(schema);

        ClientSecureMondrianConnection result = new ClientSecureMondrianConnection(fullyConfiguredTestInstance);

        assertEquals(schema, result.getSchema());
    }
}
