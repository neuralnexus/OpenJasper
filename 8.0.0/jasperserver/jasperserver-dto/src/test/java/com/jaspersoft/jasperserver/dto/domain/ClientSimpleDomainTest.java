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

package com.jaspersoft.jasperserver.dto.domain;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.connection.metadata.TableMetadata;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableDataSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientSimpleDomainTest extends BaseDTOPresentableTest<ClientSimpleDomain> {

    private static final TableMetadata TEST_METADATA = new TableMetadata().setQueryLanguage("TEST_LANGUAGE");
    private static final TableMetadata TEST_METADATA_1 = new TableMetadata().setQueryLanguage("TEST_LANGUAGE_1");

    private static final ClientReferenceableDataSource TEST_DATA_SOURCE = new ClientReference().setUri("TEST_URI");
    private static final ClientReferenceableDataSource TEST_DATA_SOURCE_1 = new ClientReference().setUri("TEST_URI_1");

    private static final String TEST_CREATION_DATE = "TEST_DATE";
    private static final String TEST_CREATION_DATE_1 = "TEST_DATE_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final Integer TEST_PERMISSION_MASK = 1;
    private static final Integer TEST_PERMISSION_MASK_1 = 2;

    private static final String TEST_UPDATE_DATE = "TEST_UPDATE_DATE";
    private static final String TEST_UPDATE_DATE_1 = "TEST_UPDATE_DATE_1";

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final Integer TEST_VERSION = 1;
    private static final Integer TEST_VERSION_1 = 2;

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientSimpleDomain expected, ClientSimpleDomain actual) {
        assertNotSame(expected.getMetadata(), actual.getMetadata());
    }

    /*
     * Preparing
     */

    @Override
    protected List<ClientSimpleDomain> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // parent properties
                createFullyConfiguredInstance().setDataSource(TEST_DATA_SOURCE_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UPDATE_DATE_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                // own properties
                createFullyConfiguredInstance().setMetadata(TEST_METADATA_1),
                // null values
                // parent properties
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setUpdateDate(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setVersion(null),
                // own properties
                createFullyConfiguredInstance().setMetadata(null)
        );
    }

    @Override
    protected ClientSimpleDomain createFullyConfiguredInstance() {
        // parent properties
        ClientSimpleDomain instance = new ClientSimpleDomain()
                .setDataSource(TEST_DATA_SOURCE)
                .setCreationDate(TEST_CREATION_DATE)
                .setDescription(TEST_DESCRIPTION)
                .setLabel(TEST_LABEL)
                .setPermissionMask(TEST_PERMISSION_MASK)
                .setUpdateDate(TEST_UPDATE_DATE)
                .setUri(TEST_URI)
                .setVersion(TEST_VERSION);
        return instance
                .setMetadata(TEST_METADATA);
    }

    @Override
    protected ClientSimpleDomain createInstanceWithDefaultParameters() {
        return new ClientSimpleDomain();
    }

    @Override
    protected ClientSimpleDomain createInstanceFromOther(ClientSimpleDomain other) {
        return new ClientSimpleDomain(other);
    }

}
