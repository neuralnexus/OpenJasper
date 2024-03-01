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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TeiidVirtualDataSourceQueryServiceImplTest {

    TeiidVirtualDataSourceQueryServiceImpl teiidVirtualDataSourceQueryServiceImpl = new TeiidVirtualDataSourceQueryServiceImpl();


    @Test
    public void isSelectedSchema_EmptyListReturnTrue() {
        JdbcDataSourceImpl jdbcDataSource = new JdbcDataSourceImpl(null,  new HashSet<String>(), "SampleDS", null);
        Assert.assertTrue(teiidVirtualDataSourceQueryServiceImpl.isSelectedSchema("EmptySchema", jdbcDataSource));
        JdbcDataSourceImpl jdbcDataSource2 = new JdbcDataSourceImpl(null,  null, "SampleDS", null);
        Assert.assertTrue(teiidVirtualDataSourceQueryServiceImpl.isSelectedSchema("EmptySchema", jdbcDataSource2));
        HashSet<String> schemaList = new HashSet<String>();
        schemaList.add("schema1");
        JdbcDataSourceImpl jdbcDataSource3 = new JdbcDataSourceImpl(null,  schemaList, "SampleDS", null);
        Assert.assertTrue(teiidVirtualDataSourceQueryServiceImpl.isSelectedSchema("schema1", jdbcDataSource3));
        Assert.assertFalse(teiidVirtualDataSourceQueryServiceImpl.isSelectedSchema("schema2", jdbcDataSource3));
    }

    @Test
    public void getSubDataSourceSchemas() {
        Set<String> schemas = new HashSet<String>(Arrays.asList("ABC", "EFG"));
        JdbcReportDataSource jdbcReportDataSource = mock(JdbcReportDataSource.class);
        when(jdbcReportDataSource.getDriverClass()).thenReturn("SampleDriverClass");
        JdbcDataSourceImpl jdbcDataSource = new JdbcDataSourceImpl(jdbcReportDataSource, schemas, "SubDataSource", null);
        Set<String> newSchemas = teiidVirtualDataSourceQueryServiceImpl.getSubDataSourceSchemas(jdbcDataSource, jdbcDataSource.getDataSourceName());
        Assert.assertEquals(schemas.size(), newSchemas.size());
        Assert.assertTrue(newSchemas.contains("ABC"));
        Assert.assertTrue(newSchemas.contains("EFG"));
        Assert.assertTrue(teiidVirtualDataSourceQueryServiceImpl.getDataSourceCache().get("ABC_SCHEMA", System.currentTimeMillis()) == null);
        Assert.assertTrue(teiidVirtualDataSourceQueryServiceImpl.getDataSourceCache().get("EFG_SCHEMA", System.currentTimeMillis()) == null);
    }

    @Test
    public void collectImportProperties_withDefault_AndDataSourceSpecificProperty() {
        Map<String, Map<String, String>> importPropertyMap = getImportPropertyMap();

        Properties importProperties = new Properties();
        teiidVirtualDataSourceQueryServiceImpl.setImportPropertyMap(importPropertyMap);
        teiidVirtualDataSourceQueryServiceImpl.setDefaultImportProperties("","", "", null, importProperties);
        teiidVirtualDataSourceQueryServiceImpl.collectImportProperties("postgresql", importProperties);

        assertEquals(importProperties.getProperty("importer.tableTypes"), "TABLE,VIEW,SYNONYM");
        assertEquals(importProperties.getProperty("importer.importKeys"), "false");
    }

    @Test
    public void collectImportProperties_withDuplicateDefault_AndDataSourceSpecificProperty() {
        Map<String, Map<String, String>> importPropertyMap = getImportPropertyMap();

        Map<String, String> propertyMap = importPropertyMap.get("postgresql");
        propertyMap.put("importer.importKeys", Boolean.TRUE.toString());
        Properties importProperties = new Properties();
        teiidVirtualDataSourceQueryServiceImpl.setImportPropertyMap(importPropertyMap);

        teiidVirtualDataSourceQueryServiceImpl.collectImportProperties("postgresql", importProperties);

        assertEquals(importProperties.getProperty("importer.tableTypes"), "TABLE,VIEW,SYNONYM");
        assertEquals(importProperties.getProperty("importer.importKeys"), "true");
    }

    @Test
    public void releaseExpiredPools_teiidExpiredPool_release() throws Exception {
        final String key = "key";
        final int poolTimeout = 10;
        final long now = System.currentTimeMillis();

        teiidVirtualDataSourceQueryServiceImpl.setPoolTimeoutInMinute(poolTimeout);
        TeiidVirtualDataSourceQueryServiceImpl.PooledVirtualSchemaEntry entry =
                spy(teiidVirtualDataSourceQueryServiceImpl.new PooledVirtualSchemaEntry(key, Collections.emptySet()));
        teiidVirtualDataSourceQueryServiceImpl.getDataSourceCache().put("key", entry, now);
        teiidVirtualDataSourceQueryServiceImpl.releaseExpiredPools(now + TimeUnit.MINUTES.toMillis(poolTimeout + 1));

        verify(entry).release();
    }

    @Test
    public void releaseExpiredPools_teiidActivePool_nothing() throws Exception {
        final String key = "key";
        final int poolTimeout = 10;
        final long now = System.currentTimeMillis();

        teiidVirtualDataSourceQueryServiceImpl.setPoolTimeoutInMinute(poolTimeout);
        TeiidVirtualDataSourceQueryServiceImpl.PooledVirtualSchemaEntry entry =
                spy(teiidVirtualDataSourceQueryServiceImpl.new PooledVirtualSchemaEntry(key, Collections.emptySet()));
        teiidVirtualDataSourceQueryServiceImpl.getDataSourceCache().put("key", entry, now);
        teiidVirtualDataSourceQueryServiceImpl.releaseExpiredPools(now);

        verify(entry, times(0)).release();
    }

    public Map<String, Map<String, String>> getImportPropertyMap() {
        Map<String, Map<String, String>> importPropertyMap = new HashMap<>();
        Map<String, String> propertyMapForDataSourceModel = new HashMap<>();
        propertyMapForDataSourceModel.put("importer.tableTypes", "TABLE,VIEW,SYNONYM");

        Map<String, String> propertyMapForAllDatasources = new HashMap<>();
        propertyMapForAllDatasources.put("importer.importForeignKeys", Boolean.FALSE.toString());
        propertyMapForAllDatasources.put("importer.importKeys", Boolean.FALSE.toString());
        importPropertyMap.put("postgresql", propertyMapForDataSourceModel);
        importPropertyMap.put("default", propertyMapForAllDatasources);
        return importPropertyMap;
    }

}
