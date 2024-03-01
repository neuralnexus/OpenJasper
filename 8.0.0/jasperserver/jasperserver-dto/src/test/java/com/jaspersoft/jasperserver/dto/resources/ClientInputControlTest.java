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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientInputControlTest extends BaseDTOPresentableTest<ClientInputControl> {

    private static final String TEST_VALUE_COLUMN = "TEST_VALUE_COLUMN";
    private static final String TEST_VALUE_COLUMN_1 = "TEST_VALUE_COLUMN_1";

    private static final boolean TEST_MANDATORY = true;
    private static final boolean TEST_MANDATORY_1 = false;

    private static final boolean TEST_READ_ONLY = true;
    private static final boolean TEST_READ_ONLY_1 = false;

    private static final boolean TEST_VISIBLE = true;
    private static final boolean TEST_VISIBLE_1 = false;

    private static final byte TEST_TYPE = (byte) 0x1b;
    private static final byte TEST_TYPE_1 = (byte) 0x2b;

    private static final List<String> TEST_VISIBLE_COLUMNS = Arrays.asList("COLUMN_A", "COLUMN_B");
    private static final List<String> TEST_VISIBLE_COLUMNS_1 = Arrays.asList("COLUMN_A_1", "COLUMN_B_1");

    private static final ClientReferenceableDataType TEST_DATA_TYPE = new ClientReference().setUri("TEST_URI");
    private static final ClientReferenceableDataType TEST_DATA_TYPE_1 = new ClientReference().setUri("TEST_URI_1");

    private static final ClientReferenceableQuery TEST_QUERY = new ClientQuery().setLanguage("TEST_LANGUAGE");
    private static final ClientReferenceableQuery TEST_QUERY_1 = new ClientQuery().setLanguage("TEST_LANGUAGE_1");

    private static final ClientReferenceableListOfValues TEST_LIST_OF_VALUES = new ClientListOfValues().setItems(Collections.singletonList(new ClientListOfValuesItem().setLabel("TEST_LABEL")));
    private static final ClientReferenceableListOfValues TEST_LIST_OF_VALUES_1 = new ClientListOfValues().setItems(Collections.singletonList(new ClientListOfValuesItem().setLabel("TEST_LABEL_1")));

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
    protected List<ClientInputControl> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setMandatory(TEST_MANDATORY_1),
                createFullyConfiguredInstance().setReadOnly(TEST_READ_ONLY_1),
                createFullyConfiguredInstance().setVisible(TEST_VISIBLE_1),
                createFullyConfiguredInstance().setType(TEST_TYPE_1),
                createFullyConfiguredInstance().setVisibleColumns(TEST_VISIBLE_COLUMNS_1),
                createFullyConfiguredInstance().setDataType(TEST_DATA_TYPE_1),
                createFullyConfiguredInstance().setQuery(TEST_QUERY_1),
                createFullyConfiguredInstance().setListOfValues(TEST_LIST_OF_VALUES_1),
                createFullyConfiguredInstance().setValueColumn(TEST_VALUE_COLUMN_1),
                // base class fields (ClientResource)
                createFullyConfiguredInstance().setVersion(TEST_VERSION_1),
                createFullyConfiguredInstance().setPermissionMask(TEST_PERMISSION_MASK_1),
                createFullyConfiguredInstance().setCreationDate(TEST_CREATION_DATE_1),
                createFullyConfiguredInstance().setUpdateDate(TEST_UDPATE_DATE_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                // fields with null values
                createFullyConfiguredInstance().setVisibleColumns(null),
                createFullyConfiguredInstance().setDataType(null),
                createFullyConfiguredInstance().setQuery(null),
                createFullyConfiguredInstance().setListOfValues(null),
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
    protected ClientInputControl createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setMandatory(TEST_MANDATORY)
                .setReadOnly(TEST_READ_ONLY)
                .setVisible(TEST_VISIBLE)
                .setType(TEST_TYPE)
                .setValueColumn(TEST_VALUE_COLUMN)
                .setVisibleColumns(TEST_VISIBLE_COLUMNS)
                .setDataType(TEST_DATA_TYPE)
                .setQuery(TEST_QUERY)
                .setListOfValues(TEST_LIST_OF_VALUES)
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
    protected ClientInputControl createInstanceWithDefaultParameters() {
        return new ClientInputControl();
    }

    @Override
    protected ClientInputControl createInstanceFromOther(ClientInputControl other) {
        return new ClientInputControl(other);
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientDataType() {
        ClientReferenceableDataType dataType = new ClientDataType().setLabel("dataType");
        fullyConfiguredTestInstance.setDataType(dataType);

        ClientInputControl result = new ClientInputControl(fullyConfiguredTestInstance);

        assertEquals(dataType, result.getDataType());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientReferenceQuery() {
        ClientReferenceableQuery query = new ClientReference().setUri("uri");
        fullyConfiguredTestInstance.setQuery(query);

        ClientInputControl result = new ClientInputControl(fullyConfiguredTestInstance);

        assertEquals(query, result.getQuery());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientReferenceListOfValues() {
        ClientReferenceableListOfValues listOfValues = new ClientReference().setUri("uri");
        fullyConfiguredTestInstance.setListOfValues(listOfValues);

        ClientInputControl result = new ClientInputControl(fullyConfiguredTestInstance);
        assertEquals(listOfValues, result.getListOfValues());
    }

}