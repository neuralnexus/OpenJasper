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

package com.jaspersoft.jasperserver.dto.adhoc.query.expansion;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientMemberExpansionTest extends BaseDTOTest<ClientMemberExpansion> {
    private static final List<String> PATH_LIST = Arrays.asList("path1", "path2");
    private static final List<String> PATH_LIST_ALTERNATIVE = Collections.singletonList("path3");

    @Override
    protected List<ClientMemberExpansion> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setExpanded(false),
                createFullyConfiguredInstance().setPath(PATH_LIST_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setPath(null)
        );
    }

    @Override
    protected ClientMemberExpansion createFullyConfiguredInstance() {
        return new ClientMemberExpansion()
                .setExpanded(true)
                .setPath(PATH_LIST);
    }

    @Override
    protected ClientMemberExpansion createInstanceWithDefaultParameters() {
        return new ClientMemberExpansion();
    }

    @Override
    protected ClientMemberExpansion createInstanceFromOther(ClientMemberExpansion other) {
        return new ClientMemberExpansion(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMemberExpansion expected, ClientMemberExpansion actual) {
        assertNotSame(expected.getPath(), actual.getPath());
    }

    @Test
    public void getReturnsPath() {
        ClientMemberExpansion instance = createFullyConfiguredInstance();
        assertEquals(PATH_LIST, instance.get());
    }
}
