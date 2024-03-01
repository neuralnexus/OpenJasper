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

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;


import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class VirtualSQLDataSourceTest {

    @Mock
    Connection connection;

    @Mock
    DatabaseMetaData databaseMetaData;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        when(databaseMetaData.getURL()).thenReturn("jdbc:cassandra://172.17.19.96:9042;DefaultKeyspace=store");
        when(databaseMetaData.getDatabaseProductName()).thenReturn("Oracle");
        when(databaseMetaData.getUserName()).thenReturn("FOODMART");
        when(connection.getMetaData()).thenReturn(databaseMetaData);
    }

    @Test
    public void includeCurrentSchema() {
        Assert.assertEquals(VirtualSQLDataSource.includeCurrentSchema(connection, "ABC"), false);
        Assert.assertEquals(VirtualSQLDataSource.includeCurrentSchema(connection, "store"), true);
    }

    @Test
    public void getCustomSelectedSchemas_with_customAdded_schemas() {
        String productName = "Oracle";
        String userName = "FOODMART";

        Set<String> schemaSet = new LinkedHashSet<>();
        Map<String, Map<String, Set>> customSelectedSchemas = new HashMap<>();
        Map<String, Set> oracleSchemas = new HashMap<>();


        oracleSchemas.put("usernameAsSchema", new HashSet());

        HashSet<String> includedSchemaSet = new HashSet<>();
        includedSchemaSet.add("XDB");
        oracleSchemas.put("schemasToBeIncluded", includedSchemaSet);

        customSelectedSchemas.put("oracle", oracleSchemas);
        boolean checkForEmptyTables = VirtualSQLDataSource.getCustomSelectedSchemas(productName, userName, "", customSelectedSchemas, schemaSet);

        assertEquals(schemaSet.size(), 2);
        assertTrue(schemaSet.contains("xdb"));
        assertTrue(schemaSet.contains("foodmart"));
        assertTrue(checkForEmptyTables);
    }

    @Test
    public void getCustomSelectedSchemas_with_DatasourceName_based_schemas() {
        String dataSourceName = "Oracle_DS";
        String userName = "FOODMART";

        Set<String> schemaSet = new LinkedHashSet<>();
        Map<String, Map<String, Set>> customSelectedSchemas = new HashMap<>();
        Map<String, Set> oracleSchemas = new HashMap<>();


        oracleSchemas.put("usernameAsSchema", new HashSet());

        HashSet<String> includedSchemaSet = new HashSet<>();
        includedSchemaSet.add("XDB");
        oracleSchemas.put("schemasToBeIncluded", includedSchemaSet);

        customSelectedSchemas.put("oracle_ds", oracleSchemas);
        boolean checkForEmptyTables = VirtualSQLDataSource.getCustomSelectedSchemas("", userName, dataSourceName, customSelectedSchemas, schemaSet);

        assertEquals(schemaSet.size(), 2);
        assertTrue(schemaSet.contains("xdb"));
        assertTrue(schemaSet.contains("foodmart"));
        assertTrue(checkForEmptyTables);
    }

    @Test
    public void getCustomSelectedSchemas_with_DatasourceName_And_ProductName_Based_schemas() {
        String dataSourceName = "Oracle_DS";
        String productName = "Oracle";
        String userName = "FOODMART";
        HashSet<String> includedSchemaSet;

        Set<String> schemaSet = new LinkedHashSet<>();
        Map<String, Map<String, Set>> customSelectedSchemas = new HashMap<>();

        Map<String, Set> oracleSchemas = new HashMap<>();
        oracleSchemas.put("usernameAsSchema", new HashSet());
        includedSchemaSet = new HashSet<>();
        includedSchemaSet.add("XDB");
        oracleSchemas.put("schemasToBeIncluded", includedSchemaSet);
        customSelectedSchemas.put("oracle", oracleSchemas);

        Map<String, Set> dsSchemas = new HashMap<>();
        dsSchemas.put("usernameAsSchema", new HashSet());
        includedSchemaSet = new HashSet<>();
        includedSchemaSet.add("SYS");
        dsSchemas.put("schemasToBeIncluded", includedSchemaSet);

        customSelectedSchemas.put("oracle_ds", dsSchemas);
        boolean checkForEmptyTables = VirtualSQLDataSource.getCustomSelectedSchemas(productName, userName, dataSourceName, customSelectedSchemas, schemaSet);

        assertEquals(schemaSet.size(), 3);
        assertTrue(schemaSet.contains("sys"));
        assertTrue(schemaSet.contains("xdb"));
        assertTrue(schemaSet.contains("foodmart"));
        assertTrue(checkForEmptyTables);
    }

    @Test
    public void getCustomSelectedSchemas_with_only_userSchema() {
        String productName = "Oracle";
        String userName = "FOODMART";

        Set<String> schemaSet = new LinkedHashSet<>();
        Map<String, Map<String, Set>> customSelectedSchemas = new HashMap<>();
        Map<String, Set> oracleSchemas = new HashMap<>();


        oracleSchemas.put("usernameAsSchema", new HashSet());

        customSelectedSchemas.put("oracle", oracleSchemas);
        boolean checkForEmptyTables = VirtualSQLDataSource.getCustomSelectedSchemas(productName, userName, null, customSelectedSchemas, schemaSet);

        assertEquals(schemaSet.size(), 1);
        assertTrue(schemaSet.contains("FOODMART"));
        assertFalse(checkForEmptyTables);
    }



    @Test
    public void getCustomSelectedSchemas_with_No_includedSchema() {
        String productName = "Oracle";
        String userName = "FOODMART";

        Set<String> schemaSet = new LinkedHashSet<>();
        Map<String, Map<String, Set>> customSelectedSchemas = new HashMap<>();

        boolean checkForEmptyTables = VirtualSQLDataSource.getCustomSelectedSchemas(productName, userName, null, customSelectedSchemas, schemaSet);
        assertEquals(schemaSet.size(), 0);
        assertFalse(checkForEmptyTables);

        Map<String, Set> oracleSchemas = new HashMap<>();
        customSelectedSchemas.put("oracle", oracleSchemas);

        checkForEmptyTables = VirtualSQLDataSource.getCustomSelectedSchemas(productName, userName, "", customSelectedSchemas, schemaSet);
        assertEquals(schemaSet.size(), 0);
        assertFalse(checkForEmptyTables);
    }
}
