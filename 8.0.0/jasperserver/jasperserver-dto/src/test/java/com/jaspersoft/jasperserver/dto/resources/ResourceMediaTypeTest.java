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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ResourceMediaTypeTest {

    @Test
    public void DataSourceTypesContainsAllNecessaryTypes() {
        List<String> types = ResourceMediaType.DATASOURCE_TYPES;

        assertTrue(types.contains(ResourceMediaType.AWS_DATA_SOURCE_CLIENT_TYPE));
        assertTrue(types.contains(ResourceMediaType.BEAN_DATA_SOURCE_CLIENT_TYPE));
        assertTrue(types.contains(ResourceMediaType.CUSTOM_DATA_SOURCE_CLIENT_TYPE));
        assertTrue(types.contains(ResourceMediaType.JDBC_DATA_SOURCE_CLIENT_TYPE));
        assertTrue(types.contains(ResourceMediaType.JNDI_JDBC_DATA_SOURCE_CLIENT_TYPE));
        assertTrue(types.contains(ResourceMediaType.VIRTUAL_DATA_SOURCE_CLIENT_TYPE));
        assertTrue(types.contains(ResourceMediaType.SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE));
    }
}