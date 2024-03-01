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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;


import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualDataSourceHandler;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualSQLDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.TeiidConnectionFactoryImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.VirtualReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


public class VirtualReportDataSourceServiceFactoryTest{
    @InjectMocks
    VirtualReportDataSourceServiceFactory virtualReportDataSourceServiceFactory = new VirtualReportDataSourceServiceFactory();
    @Mock
    private VirtualDataSourceHandler virtualDataSourceHandler;
    @Mock
    private TeiidConnectionFactoryImpl teiidConnectionFactory;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @BeforeMethod
    public void setUp() throws Exception {
        reset(virtualDataSourceHandler);
        when(teiidConnectionFactory.testIndividualConnections()).thenReturn(true);
        when(virtualDataSourceHandler.getSqlDataSource(any(ExecutionContext.class), any(VirtualReportDataSource.class))).thenReturn(new VirtualSQLDataSource(teiidConnectionFactory));
    }

    @Test
    public void getDataSourceService_VerifyVirtualService() throws SQLException {
        ReportDataSourceService service = virtualReportDataSourceServiceFactory.createService(new VirtualReportDataSourceImpl());
        Assert.assertEquals(service instanceof VirtualDataSourceService, true);
        Assert.assertEquals(((VirtualDataSourceService)service).testConnection(), true);
    }

}
