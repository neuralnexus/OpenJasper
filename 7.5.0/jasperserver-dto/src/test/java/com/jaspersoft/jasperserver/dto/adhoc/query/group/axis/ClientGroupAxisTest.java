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

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class ClientGroupAxisTest extends BaseDTOTest<ClientGroupAxis> {
    private static final String CLIENT_QUERY_GROUP_ID = "id";
    private static final ClientQueryGroup CLIENT_QUERY_GROUP = new ClientQueryGroup().setId(CLIENT_QUERY_GROUP_ID);
    private static final ClientQueryGroup CLIENT_QUERY_GROUP_2 = new ClientQueryGroup().setId("id2");
    private static final List<ClientQueryGroup> CLIENT_QUERY_GROUP_LIST = Arrays.asList(CLIENT_QUERY_GROUP, CLIENT_QUERY_GROUP_2);

    private static final ClientQueryGroup CLIENT_QUERY_GROUP_ALTERNATIVE = new ClientQueryGroup().setId("idAlternative");

    @Override
    protected List<ClientGroupAxis> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setItems(Collections.singletonList(CLIENT_QUERY_GROUP_ALTERNATIVE)),
                // with null values
                createFullyConfiguredInstance().setItems(null)
        );
    }

    @Override
    protected ClientGroupAxis createFullyConfiguredInstance() {
        return new ClientGroupAxis()
                .setItems(CLIENT_QUERY_GROUP_LIST);
    }

    @Override
    protected ClientGroupAxis createInstanceWithDefaultParameters() {
        return new ClientGroupAxis();
    }

    @Override
    protected ClientGroupAxis createInstanceFromOther(ClientGroupAxis other) {
        return new ClientGroupAxis(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientGroupAxis expected, ClientGroupAxis actual) {
        assertNotSameCollection(expected.getItems(), actual.getItems());
    }

    @Test
    public void constructor_clientQueryGroups_instance() {
        ClientGroupAxis instance = new ClientGroupAxis(CLIENT_QUERY_GROUP_LIST);
        assertEquals(CLIENT_QUERY_GROUP_LIST, instance.getItems());
    }
}
