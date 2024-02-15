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
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EngineServiceImplTest {

    @InjectMocks
    private EngineServiceImpl service;

    @Mock
    private AuditContext auditContext;
    @Mock
    private Appendable testAppendable;
    @Mock(name = "engineExecutions")
    private Map<String, EngineServiceImpl.ReportExecutionStatus> engineExecutions;
    @Mock(name = "errorReportDSFactory")
    private Map<String, ErrorTemplateReportService> errorReportDSFactory;
    @Mock
    private DataSourceServiceFactory dataSourceServiceFactories;

    @Mock
    private ReportDataSourceServiceFactory factory;
    @Mock
    private ReportDataSourceService dataSourceService;
    @Mock
    private EngineServiceImpl.ReportFiller filler;
    @Mock
    private ExecutionContext context;
    @Mock
    private ReportUnit reportUnit;
    @Mock
    private JasperReport report;
    @Mock
    private ReportDataSource datasource;

    @Before
    public void setUp() throws Exception {
        doNothing().when(auditContext).doInAuditContext(anyString(), any());
        doReturn(factory).when(dataSourceServiceFactories).getBean(any());
        doReturn(dataSourceService).when(factory).createService(any(ReportDataSource.class));
        doReturn(dataSourceService).when(factory).createService(any(ReportDataSource.class), anyBoolean());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void fillReport_hasCachedDataParameterIsTrue_usedCorrectReportDataSourceService() {
        verify_fillReport_hasCachedDataParameter_usedCorrectReportDataSourceService(true);
    }

    @Test
    public void fillReport_hasCachedDataParameterIsFalse_usedCorrectReportDataSourceService() {
        verify_fillReport_hasCachedDataParameter_usedCorrectReportDataSourceService(false);
    }

    public void verify_fillReport_hasCachedDataParameter_usedCorrectReportDataSourceService(boolean hasCachedData) {
        Map reportParameters = mock(Map.class);

        service.fillReport(context, reportUnit, report, reportParameters, datasource, null, filler, hasCachedData);

        verify(factory).createService(same(datasource), eq(hasCachedData));
        verify(dataSourceService).setReportParameterValues(same(reportParameters), eq(hasCachedData));
    }
}
