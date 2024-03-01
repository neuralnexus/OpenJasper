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

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Tests for {@link JdbcReportDataSourceServiceFactory}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class JdbcReportDataSourceServiceFactoryTest {

    @InjectMocks
    private JdbcReportDataSourceServiceFactory jdbcReportDataSourceServiceFactory;

    @Mock
    private PooledJdbcDataSourceFactory pooledJdbcDataSourceFactory;

    @Test
    public void deleteGoogleBigQueryTempFiles()  {
        String path = new File("target/test-classes").getAbsolutePath().toString();
        File location = Paths.get(path, "location").toFile();
        location.mkdir();
        System.setProperty("java.io.tmpdir",path+"/"+location.getName()+"/");
        try {
            File googleBigQueryFile = new File(path+"/"+location.getName()+"/gbq_test.txt");
            googleBigQueryFile.createNewFile();
            File nonGoogleBigQueryFile = new File(path+"/"+location.getName()+"/other_test.txt");
            nonGoogleBigQueryFile.createNewFile();
            assertEquals(2, location.listFiles().length);
            JdbcReportDataSourceServiceFactory factory = new JdbcReportDataSourceServiceFactory();
            assertEquals(1, location.listFiles().length);
            assertEquals("other_test.txt", location.listFiles()[0].getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ensureDefaultTimezoneUsedForEmptyString() {
        TimeZone tz = jdbcReportDataSourceServiceFactory.getTimeZoneByDataSourceTimeZone("");
        assertEquals(tz, TimeZone.getDefault());
    }

    @Test
    public void ensureDefaultTimezoneUsedForNullString() {
        TimeZone tz = jdbcReportDataSourceServiceFactory.getTimeZoneByDataSourceTimeZone(null);
        assertEquals(tz, TimeZone.getDefault());
    }

    @Test
    public void ensureDataSourceTimeZoneIsAppliedToDataSourceService() {
        final String dsTimeZone = "GMT+5";

        setUp();

        //Actual test method call
        JdbcDataSourceService rds = (JdbcDataSourceService)jdbcReportDataSourceServiceFactory.createService(getDataSourceService(dsTimeZone));

        //Check expectations
        Map<String, ?> paramsMap = new HashMap<>();
        rds.setReportParameterValues(paramsMap);

        assertEquals(TimeZone.getTimeZone(dsTimeZone), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));

    }

    @Test
    public void createService_AppliedProfileAttribute() {
        JdbcReportDataSource orgReportDataSource = new JdbcReportDataSourceImpl();
        orgReportDataSource.setConnectionUrl("jdbc:postgresql://localhost:5432/jasperserver");
        orgReportDataSource.setDriverClass("org.postgresql.Driver");
        ProfileAttributesResolver profileAttributesResolver = mock(ProfileAttributesResolver.class);
        jdbcReportDataSourceServiceFactory.setProfileAttributesResolver(profileAttributesResolver);
        jdbcReportDataSourceServiceFactory.updateJdbcReportDataSource(orgReportDataSource);
        Mockito.verify(profileAttributesResolver).mergeResource(any(Resource.class));
    }

    @Test
    public void ensureDefaultTimeZoneIsAppliedToDataSourceServiceIfTZFromDataSourceIsEmptyOrNull() {
        final String emptyTZ = "";
        final String nullTZ = null;

        setUp();

        //Actual test method call
        JdbcDataSourceService emptyTZService = (JdbcDataSourceService)jdbcReportDataSourceServiceFactory.createService(getDataSourceService(emptyTZ));
        JdbcDataSourceService nullTZService = (JdbcDataSourceService)jdbcReportDataSourceServiceFactory.createService(getDataSourceService(nullTZ));

        //Check expectations
        Map<String, ?> paramsMap = new HashMap<>();
        emptyTZService.setReportParameterValues(paramsMap);
        assertEquals(TimeZone.getDefault(), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));

        nullTZService.setReportParameterValues(paramsMap);
        assertEquals(TimeZone.getDefault(), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));
    }

    private void setUp() {
        PooledDataSource pooledDataSourceMock = mock(PooledDataSource.class);
        DataSource dataSourceMock = mock(DataSource.class);
        when(pooledDataSourceMock.getDataSource()).thenReturn(dataSourceMock);
        when(pooledJdbcDataSourceFactory.createPooledDataSource(any(), any(), any(), any(), eq(true), eq(false)))
                .thenReturn(pooledDataSourceMock);
    }

    private JdbcReportDataSource getDataSourceService(String dsTimeZone) {
        JdbcReportDataSource serviceMock = mock(JdbcReportDataSource.class);
        when(serviceMock.getTimezone()).thenReturn(dsTimeZone);
        when(serviceMock.getDriverClass()).thenReturn("org.postgresql.Driver");
        when(serviceMock.getConnectionUrl()).thenReturn("jdbc:postgresql://localhost:5432/jasperserver");
        return serviceMock;
    }
}
