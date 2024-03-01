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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class VirtualDataSourceHandlerTest {
    VirtualDataSourceHandler handler;

    @Before
    public void setup() {
        handler = new VirtualDataSourceHandler();
    }

    @Test
    public void findSchemas_withNoSchema() {
        Set<String> virtualSchemaList = new HashSet<>();
        virtualSchemaList.add("JNDI_DS_SugarCrm");
        virtualSchemaList.add("JNDI_DS");

        assertEquals(null, handler.findSchemas(virtualSchemaList, "JNDI_DS"));
    }

    @Test
    public void findSchemas_with_SchemaIncluded() {
        Set<String> virtualSchemaList = new HashSet<>();
        virtualSchemaList.add("JNDI_DS_SugarCrm");
        virtualSchemaList.add("JNDI_DS_Foodmart");

        Set<String> resultSet = new HashSet<>();
        resultSet.add("SugarCrm");
        resultSet.add("Foodmart");

        assertEquals(resultSet.toString(), handler.findSchemas(virtualSchemaList, "JNDI_DS").toString());
    }

    @Test
    public void findSchemas_with_SchemaIncluded_matches_withDatasourceName() {
        Set<String> virtualSchemaList = new HashSet<>();
        virtualSchemaList.add("JNDI_DS_SugarCrm");
        virtualSchemaList.add("JNDI_DS_SugarCrm");

        Set<String> resultSet = new HashSet<>();
        resultSet.add("SugarCrm");

        assertEquals(resultSet.toString(), handler.findSchemas(virtualSchemaList, "JNDI_DS").toString());
    }
}
