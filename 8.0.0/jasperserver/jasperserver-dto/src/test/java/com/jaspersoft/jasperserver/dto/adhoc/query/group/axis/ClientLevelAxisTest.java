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

package com.jaspersoft.jasperserver.dto.adhoc.query.group.axis;

import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientExpandable;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientLevelExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
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
public class ClientLevelAxisTest extends BaseDTOPresentableTest<ClientLevelAxis> {

    private static final List<ClientQueryLevel> CLIENT_QUERY_LEVEL_LIST =
            Arrays.asList(new ClientQueryLevel().setId("id"), new ClientQueryLevel().setId("id2"));
    private static final List<ClientExpandable> CLIENT_EXPANDABLE_LIST =
            Collections.<ClientExpandable>singletonList(new ClientLevelExpansion().setLevelReference("levelReference"));

    private static final List<ClientQueryLevel> CLIENT_QUERY_LEVEL_LIST_ALTERNATIVE =
            Collections.singletonList(new ClientQueryLevel().setId("idAlternative"));
    private static final List<ClientExpandable> CLIENT_EXPANDABLE_LIST_ALTERNATIVE =
            Arrays.<ClientExpandable>asList(new ClientLevelExpansion().setLevelReference("levelReference"), new ClientLevelExpansion());

    @Override
    protected List<ClientLevelAxis> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setExpansions(CLIENT_EXPANDABLE_LIST_ALTERNATIVE),
                createFullyConfiguredInstance().setItems(CLIENT_QUERY_LEVEL_LIST_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setExpansions(null),
                createFullyConfiguredInstance().setItems(null)
        );
    }

    @Override
    protected ClientLevelAxis createFullyConfiguredInstance() {
        return new ClientLevelAxis()
                .setExpansions(CLIENT_EXPANDABLE_LIST)
                .setItems(CLIENT_QUERY_LEVEL_LIST);
    }

    @Override
    protected ClientLevelAxis createInstanceWithDefaultParameters() {
        return new ClientLevelAxis();
    }

    @Override
    protected ClientLevelAxis createInstanceFromOther(ClientLevelAxis other) {
        return new ClientLevelAxis(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientLevelAxis expected, ClientLevelAxis actual) {
        assertNotSameCollection(expected.getItems(), actual.getItems());
        assertNotSameCollection(expected.getExpansions(), actual.getExpansions());
    }

    @Test
    public void constructor_clientQueryLevel_instance() {
        ClientLevelAxis instance = new ClientLevelAxis(CLIENT_QUERY_LEVEL_LIST);
        assertEquals(CLIENT_QUERY_LEVEL_LIST, instance.getItems());
    }

    @Test
    public void constructor_clientQueryLevelAndClientExpandable_instance() {
        ClientLevelAxis instance = new ClientLevelAxis(CLIENT_QUERY_LEVEL_LIST, CLIENT_EXPANDABLE_LIST);
        assertEquals(CLIENT_QUERY_LEVEL_LIST, instance.getItems());
        assertEquals(CLIENT_EXPANDABLE_LIST, instance.getExpansions());
    }
}
