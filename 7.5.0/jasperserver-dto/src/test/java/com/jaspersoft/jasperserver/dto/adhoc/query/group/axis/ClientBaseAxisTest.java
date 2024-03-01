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

package com.jaspersoft.jasperserver.dto.adhoc.query.group.axis;

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class ClientBaseAxisTest extends BaseDTOTest<ClientBaseAxis<ClientQueryGroup>> {
    private static final String CLIENT_QUERY_GROUP_ID = "id";
    private static final ClientQueryGroup CLIENT_QUERY_GROUP = new ClientQueryGroup().setId(CLIENT_QUERY_GROUP_ID);
    private static final ClientQueryGroup CLIENT_QUERY_GROUP_2 = new ClientQueryGroup().setId("id2");
    private static final List<ClientQueryGroup> CLIENT_QUERY_GROUP_LIST = Arrays.asList(CLIENT_QUERY_GROUP, CLIENT_QUERY_GROUP_2);

    private static final ClientQueryGroup CLIENT_QUERY_GROUP_ALTERNATIVE = new ClientQueryGroup().setId("idAlternative");

    @Override
    protected List<ClientBaseAxis<ClientQueryGroup>> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setItems(Collections.singletonList(CLIENT_QUERY_GROUP_ALTERNATIVE)),
                // with null values
                createFullyConfiguredInstance().setItems(null)
        );
    }

    @Override
    protected ClientBaseAxis<ClientQueryGroup> createFullyConfiguredInstance() {
        return new ClientBaseAxis<ClientQueryGroup>()
                .setItems(CLIENT_QUERY_GROUP_LIST);
    }

    @Override
    protected ClientBaseAxis<ClientQueryGroup> createInstanceWithDefaultParameters() {
        return new ClientBaseAxis<ClientQueryGroup>();
    }

    @Override
    protected ClientBaseAxis<ClientQueryGroup> createInstanceFromOther(ClientBaseAxis<ClientQueryGroup> other) {
        return new ClientBaseAxis<ClientQueryGroup>(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientBaseAxis expected, ClientBaseAxis actual) {
        assertNotSameCollection(expected.getItems(), actual.getItems());
    }

    @Test
    public void constructor_clientQueryGroups_instance() {
        ClientBaseAxis<ClientQueryGroup> instance = new ClientBaseAxis<ClientQueryGroup>(CLIENT_QUERY_GROUP_LIST);
        assertEquals(CLIENT_QUERY_GROUP_LIST, instance.getItems());
    }

    @Test
    public void get_nullId_null() {
        ClientBaseAxis<ClientQueryGroup> instance = createFullyConfiguredInstance();
        assertNull(instance.get(null));
    }

    @Test
    public void get_notExistingId_null() {
        ClientBaseAxis<ClientQueryGroup> instance = createFullyConfiguredInstance();
        assertNull(instance.get("notExisting"));
    }

    @Test
    public void get_itemId_item() {
        ClientBaseAxis<ClientQueryGroup> instance = createFullyConfiguredInstance();
        assertEquals(CLIENT_QUERY_GROUP, instance.get(CLIENT_QUERY_GROUP_ID));
    }

    @Test
    public void get_index_itemByIndex() {
        ClientBaseAxis<ClientQueryGroup> instance = createFullyConfiguredInstance();
        assertEquals(CLIENT_QUERY_GROUP_2, instance.get(1));
    }

    @Test
    public void size_itemsCount() {
        ClientBaseAxis<ClientQueryGroup> instance = createFullyConfiguredInstance();
        assertEquals(CLIENT_QUERY_GROUP_LIST.size(), instance.size());
    }
}
