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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualSQLDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.TeiidConnectionFactoryImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

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
        when(connection.getMetaData()).thenReturn(databaseMetaData);
    }

    @Test
    public void includeCurrentSchema() {
        Assert.assertEquals(VirtualSQLDataSource.includeCurrentSchema(connection, "ABC"), false);
        Assert.assertEquals(VirtualSQLDataSource.includeCurrentSchema(connection, "store"), true);
    }
}
