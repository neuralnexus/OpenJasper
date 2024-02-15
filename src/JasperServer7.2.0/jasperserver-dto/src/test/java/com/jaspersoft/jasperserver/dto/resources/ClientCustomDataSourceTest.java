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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ClientCustomDataSourceTest extends BaseDTOPresentableTest<ClientCustomDataSource> {
    final ClientCustomDataSource dataSource1 = new ClientCustomDataSource(), dataSource2 = new ClientCustomDataSource();
    final ClientProperty property1 = new ClientProperty("a", "b"), property2 = new ClientProperty("c", "d");

    private static final String TEST_DATA_SOURCE_NAME = "TEST_DATA_SOURCE_NAME";
    private static final String TEST_DATA_SOURCE_NAME_1 = "TEST_DATA_SOURCE_NAME_1";
    private static final List<ClientProperty> TEST_PROPERTIES = Arrays.asList(
            new ClientProperty().setKey("TEST_KEY_A").setValue("TEST_VALUE_A"),
            new ClientProperty().setKey("TEST_KEY_B").setValue("TEST_VALUE_B")
    );
    private static final List<ClientProperty> TEST_PROPERTIES_1 = Arrays.asList(
            new ClientProperty().setKey("TEST_KEY_A_1").setValue("TEST_VALUE_A_1"),
            new ClientProperty().setKey("TEST_KEY_B_1").setValue("TEST_VALUE_B_1")
    );
    private static final String TEST_SERVICE_CLASS = "TEST_SERVICE_CLASS";
    private static final String TEST_SERVICE_CLASS_1 = "TEST_SERVICE_CLASS_1";

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

    @BeforeEach
    public void init() {
        dataSource1.setProperties(Arrays.asList(property1, property2));
        dataSource2.setProperties(Arrays.asList(property2, property1));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(dataSource1.equals(dataSource2));
        assertTrue(dataSource2.equals(dataSource1));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(dataSource1.hashCode(), dataSource2.hashCode());
    }

    @Test
    public void cloeableConstuctorClonesResources() {
        final ClientCustomDataSource source = new ClientCustomDataSource();
        final HashMap<String, ClientReferenceableFile> resources = new HashMap<String, ClientReferenceableFile>();
        resources.put("resource1", new ClientReference("/reference1"));
        ClientFile file = new ClientFile();
        file.setType(ClientFile.FileType.css);
        resources.put("resource2", file);
        resources.put("resource3", new ClientReference("/reference2"));
        file = new ClientFile();
        file.setType(ClientFile.FileType.csv);
        resources.put("resource4", file);
        source.setResources(resources);
        final Map<String, ClientReferenceableFile> resourcesClone = new ClientCustomDataSource(source).getResources();
        assertNotNull(resourcesClone);
        assertNotSame(resourcesClone, resources);
        assertEquals(resourcesClone.size(), resources.size());
        for (String key : resourcesClone.keySet()) {
            final ClientReferenceableFile clone = resourcesClone.get(key);
            final ClientReferenceableFile original = resources.get(key);
            assertNotNull(clone);
            assertNotNull(original);
            assertTrue(clone.equals(original));
            assertNotSame(clone, original);
        }
    }

    @Test
    public void setPropertiesWithFileNameKeySetResources() {
        Map<String, ClientReferenceableFile> expectedResource = new HashMap<String, ClientReferenceableFile>();
        expectedResource.put("dataFile", new ClientReference().setUri(TEST_URI));
        ClientProperty clientProperty = new ClientProperty().setKey("fileName").setValue("repo:" + TEST_URI);

        testInstanceWithDefaultParameters.setProperties(Collections.singletonList(clientProperty));

        assertEquals(expectedResource, testInstanceWithDefaultParameters.getResources());
    }

    /*
     * BaseDTOPresentableTest
     */
    @Override
    protected List<ClientCustomDataSource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDataSourceName(TEST_DATA_SOURCE_NAME_1),
                createFullyConfiguredInstance().setProperties(TEST_PROPERTIES_1),
                createFullyConfiguredInstance().setServiceClass(TEST_SERVICE_CLASS_1),
                createFullyConfiguredInstance().setResources(new HashMap<String, ClientReferenceableFile>() {{
                    put(TEST_URI_1, new ClientReference().setUri(TEST_URI_1));
                }}),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                // fields with null values
                createFullyConfiguredInstance().setDataSourceName(null),
                createFullyConfiguredInstance().setProperties(null),
                createFullyConfiguredInstance().setServiceClass(null),
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
    protected ClientCustomDataSource createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setDataSourceName(TEST_DATA_SOURCE_NAME)
                .setProperties(TEST_PROPERTIES)
                .setServiceClass(TEST_SERVICE_CLASS)
                .setResources(new HashMap<String, ClientReferenceableFile>() {{
                    put(TEST_URI, new ClientReference().setUri(TEST_URI));
                }})
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
    protected ClientCustomDataSource createInstanceWithDefaultParameters() {
        return new ClientCustomDataSource();
    }

    @Override
    protected ClientCustomDataSource createInstanceFromOther(ClientCustomDataSource other) {
        return new ClientCustomDataSource(other);
    }
}
