/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link JdbcReportDataSourceServiceFactory}
 *
 * @author Sergey Prilukin
 * @version $Id: JdbcReportDataSourceServiceFactoryTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JdbcReportDataSourceServiceFactoryTest extends UnitilsJUnit4 {

    @TestedObject
    private JdbcReportDataSourceServiceFactory jdbcReportDataSourceServiceFactory;

    @InjectIntoByType
    private Mock<PooledJdbcDataSourceFactory> pooledJdbcDataSourceFactory;

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
        Map<String, ?> paramsMap = new HashMap<String, Object>();
        rds.setReportParameterValues(paramsMap);

        assertEquals(TimeZone.getTimeZone(dsTimeZone), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));
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
        Map<String, ?> paramsMap = new HashMap<String, Object>();
        emptyTZService.setReportParameterValues(paramsMap);
        assertEquals(TimeZone.getDefault(), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));

        nullTZService.setReportParameterValues(paramsMap);
        assertEquals(TimeZone.getDefault(), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));
    }

    private void setUp() {
        Mock<PooledDataSource> pooledDataSourceMock = MockUnitils.createMock(PooledDataSource.class);
        Mock<DataSource> dataSourceMock = MockUnitils.createMock(DataSource.class);
        pooledDataSourceMock.returns(dataSourceMock).getDataSource();
        pooledJdbcDataSourceFactory.returns(pooledDataSourceMock).createPooledDataSource(null, null, null, null, true, false);
    }

    private JdbcReportDataSource getDataSourceService(String dsTimeZone) {
        Mock<JdbcReportDataSource> serviceMock = MockUnitils.createMock(JdbcReportDataSource.class);
        serviceMock.returns(dsTimeZone).getTimezone();
        return serviceMock.getMock();
    }
}
