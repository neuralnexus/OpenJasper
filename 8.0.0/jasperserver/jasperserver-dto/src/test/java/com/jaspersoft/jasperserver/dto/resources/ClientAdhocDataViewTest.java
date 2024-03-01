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

import com.jaspersoft.jasperserver.dto.adhoc.component.ClientGenericComponent;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiAxisQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiLevelQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationSingleElement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientAdhocDataViewTest extends BaseDTOPresentableTest<ClientAdhocDataView> {

    private static final List<ClientBundle> TEST_BUNDLES = Arrays.asList(
            new ClientBundle().setLocale("TEST_LOCALE_A"),
            new ClientBundle().setLocale("TEST_LOCALE_B")
    );
    private static final List<ClientBundle> TEST_BUNDLES_1 = Arrays.asList(
            new ClientBundle().setLocale("TEST_LOCALE_A_1"),
            new ClientBundle().setLocale("TEST_LOCALE_B_1")
    );

    private static final ClientGenericComponent TEST_COMPONENT = new ClientGenericComponent().setComponentType("TEST_TYPE");
    private static final ClientGenericComponent TEST_COMPONENT_1 = new ClientGenericComponent().setComponentType("TEST_TYPE_1");

    private static final ClientQuery TEST_QUERY = new ClientMultiAxisQuery().setSelect(new ClientSelect().setFields(Collections.singletonList(new ClientQueryField().setFieldName("TEST_FIELD_NAME"))));
    private static final ClientQuery TEST_QUERY_1 = new ClientMultiLevelQuery().setSelect(new ClientSelect().setFields(Collections.singletonList(new ClientQueryField().setFieldName("TEST_FIELD_NAME_1"))));

    private static final ClientAdhocDataViewSchema TEST_SCHEMA = new ClientAdhocDataViewSchema().setPresentation(
            Collections.singletonList(
                    (PresentationElement) new PresentationSingleElement().setResourcePath("TEST_RESOURCE_PATH")
            )
    );
    private static final ClientAdhocDataViewSchema TEST_SCHEMA_1 = new ClientAdhocDataViewSchema().setPresentation(
            Collections.singletonList(
                    (PresentationElement) new PresentationSingleElement().setResourcePath("TEST_RESOURCE_PATH_1")
            )
    );

    private static final String TEST_CREATION_DATE = "TEST_CREATION_DATE";
    private static final String TEST_CREATION_DATE_1 = "TEST_CREATION_DATE_1";

    private static final ClientReferenceableDataSource TEST_DATA_SOURCE = new ClientCustomDataSource().setDataSourceName("TEST_DATA_SOURCE_NAME");
    private static final ClientReferenceableDataSource TEST_DATA_SOURCE_1 = new ClientCustomDataSource().setDataSourceName("TEST_DATA_SOURCE_NAME_1");

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final Integer TEST_PERMISSION_MASK = 100;
    private static final Integer TEST_PERMISSION_MASK_1 = 1001;

    private static final String TEST_UDPATE_DATE = "TEST_UPDATE_DATE";
    private static final String TEST_UDPATE_DATE_1 = "TEST_UPDATE_DATE_1";

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final Integer TEST_VERSION = 101;
    private static final Integer TEST_VERSION_1 = 1011;

    @Override
    protected List<ClientAdhocDataView> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setBundles(TEST_BUNDLES_1),
                createFullyConfiguredInstance().setComponent(TEST_COMPONENT_1),
                createFullyConfiguredInstance().setQuery(TEST_QUERY_1),
                createFullyConfiguredInstance().setSchema(TEST_SCHEMA_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setDataSource(TEST_DATA_SOURCE_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                // fields with null values
                createFullyConfiguredInstance().setBundles(null),
                createFullyConfiguredInstance().setComponent(null),
                createFullyConfiguredInstance().setQuery(null),
                createFullyConfiguredInstance().setSchema(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setUpdateDate(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setVersion(null)
        );
    }

    @Override
    protected ClientAdhocDataView createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setBundles(TEST_BUNDLES)
                .setComponent(TEST_COMPONENT)
                .setQuery(TEST_QUERY)
                .setSchema(TEST_SCHEMA)
                .setCreationDate(TEST_CREATION_DATE)
                .setDataSource(TEST_DATA_SOURCE)
                .setDescription(TEST_DESCRIPTION)
                .setLabel(TEST_LABEL)
                .setPermissionMask(TEST_PERMISSION_MASK)
                .setUpdateDate(TEST_UDPATE_DATE)
                .setUri(TEST_URI)
                .setVersion(TEST_VERSION);
    }

    @Override
    protected ClientAdhocDataView createInstanceWithDefaultParameters() {
        return new ClientAdhocDataView();
    }

    @Override
    protected ClientAdhocDataView createInstanceFromOther(ClientAdhocDataView other) {
        return new ClientAdhocDataView(other);
    }

    @Test
    public void instanceCanBeCreatedWithUriParameter() {
        String uri = "uri";

        ClientAdhocDataView result = new ClientAdhocDataView(uri);

        assertEquals(uri, result.getUri());
    }
}