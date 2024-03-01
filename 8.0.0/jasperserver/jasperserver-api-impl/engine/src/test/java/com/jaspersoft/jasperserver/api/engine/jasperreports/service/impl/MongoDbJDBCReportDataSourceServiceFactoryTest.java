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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.MongoDbJDBCReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.CustomReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


public class MongoDbJDBCReportDataSourceServiceFactoryTest {
    @InjectMocks
    MongoDbJDBCReportDataSourceServiceFactory mongoDbJDBCReportDataSourceServiceFactory = new MongoDbJDBCReportDataSourceServiceFactory();
    @Mock
    PooledJdbcDataSourceFactory pooledJdbcDataSourceFactory;
    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @BeforeMethod
    public void setUp() throws Exception {
        PooledDataSource pooledDataSource = new PooledDataSource() {
            @Override
            public DataSource getDataSource() { return null; }
            @Override
            public void release() { }
            @Override
            public boolean isActive() { return false; }
        };
        when(pooledJdbcDataSourceFactory.createPooledDataSource(anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean())).thenReturn(pooledDataSource);
    }

    @Test
    public void getDataSourceService() throws SQLException {
        CustomReportDataSourceImpl customReportDataSourceImpl = new CustomReportDataSourceImpl();
        customReportDataSourceImpl.setServiceClass("com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataAdapterService");
        customReportDataSourceImpl.setDataSourceName("mongoDbJDBCDataSource");
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put("database", "test");
        propertyMap.put("password", "testUser");
        propertyMap.put("serverAddress", "localhost");
        propertyMap.put("portNumber", "27017");
        propertyMap.put("username", "testUser");
        customReportDataSourceImpl.setPropertyMap(propertyMap);
        HashMap<String, ResourceReference> resources = new HashMap<String, ResourceReference>();
        ResourceReference resourceReference = new ResourceReference();
        FileResourceImpl fileResource = new FileResourceImpl();
        fileResource.setFileType(".config");
        fileResource.setName("MongoDB_JDBC_Data_Source_1_SCHEMA");
        fileResource.setLabel("MongoDB_JDBC_Data_Source_1_SCHEMA");
        fileResource.setParentFolder("/organizations/organization_1/datasources/MongoDB_JDBC_Data_Source_1_files");
        fileResource.setData(new String("ABC").getBytes());
        resourceReference.setLocalResource(fileResource);
        resources.put("dataFile", resourceReference);
        customReportDataSourceImpl.setResources(resources);
        customReportDataSourceImpl.setName("MongoDB_JDBC_Data_Source_1");
        customReportDataSourceImpl.setLabel("34MongoDB JDBC Data Source");
        customReportDataSourceImpl.setParentFolder("/organizations/organization_1/datasources");
        customReportDataSourceImpl.setURIString("/organizations/organization_1/datasources/MongoDB_JDBC_Data_Source_1");
        mongoDbJDBCReportDataSourceServiceFactory.setPooledJdbcDataSourceFactory(pooledJdbcDataSourceFactory);
        ReportDataSourceService service = mongoDbJDBCReportDataSourceServiceFactory.createService(customReportDataSourceImpl);
        Assert.assertTrue(service instanceof MongoDbJDBCReportDataSourceService);
    }

}
