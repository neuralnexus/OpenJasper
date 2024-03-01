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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ReportExecutionJob}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportExecutionJobTest {

    private static final String REPORT_UNIT_URI = "/report";

    @InjectMocks
    private ReportExecutionJob reportExecutionJob;

    @Mock
    private LoggingContextProvider loggingContextProviderMock;
    @Mock
    private JobExecutionContext jobExecutionContextMock;
    @Mock
    private Scheduler schedulerMock;
    @Mock
    private SchedulerContext schedulerContextMock;
    @Mock
    private RepositoryService repositoryMock;
    @Mock
    private EngineService engineServiceMock;
    @Mock
    private ReportUnit reportUnitMock;
    @Mock
    private ApplicationContext applicationContextMock;
    @Mock
    private Trigger triggerMock;
    @Mock
    private JobDataMap jobDataMapMock;
    @Mock
    private SecurityContextProvider securityContextProviderMock;
    @Mock
    private ReportJobsPersistenceService reportJobsPersistenceServiceMock;
    @Mock
    private ReportJob reportJobMock;
    @Mock
    private ReportJobSource reportJobSourceMock;
    @Mock
    private ReportJobRepositoryDestination reportJobRepositoryDestinationMock;
    @Mock
    private DataSnapshotService dataSnapshotServiceMock;

    @Before
    public void setUp() throws Exception {
        when(jobExecutionContextMock.getScheduler()).thenReturn(schedulerMock);
        when(jobExecutionContextMock.getTrigger()).thenReturn(triggerMock);
        when(jobExecutionContextMock.getScheduledFireTime()).thenReturn(new Date());
        when(jobExecutionContextMock.getFireTime()).thenReturn(new Date());
        when(schedulerMock.getContext()).thenReturn(schedulerContextMock);
        when(schedulerContextMock.get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_APPLICATION_CONTEXT)).thenReturn(applicationContextMock);
        when(schedulerContextMock.get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_SECURITY_CONTEXT_PROVIDER)).thenReturn(securityContextProviderMock);
        when(schedulerContextMock.get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_JOB_PERSISTENCE_SERVICE)).thenReturn(reportJobsPersistenceServiceMock);
        when(schedulerContextMock.get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_REPOSITORY)).thenReturn(repositoryMock);
        when(schedulerContextMock.get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_ENGINE_SERVICE)).thenReturn(engineServiceMock);
        when(schedulerContextMock.getString(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_DATA_SNAPSHOT_SERVICE_BEAN)).thenReturn("dataSnapshotService");
        when(applicationContextMock.getBean("dataSnapshotService", DataSnapshotService.class)).thenReturn(dataSnapshotServiceMock);
        when(triggerMock.getJobDataMap()).thenReturn(jobDataMapMock);
        when(jobDataMapMock.getString(ReportExecutionJob.JOB_DATA_KEY_USERNAME)).thenReturn("joeuser");
        when(reportJobsPersistenceServiceMock.loadJob(any(), any())).thenReturn(reportJobMock);
        when(reportJobMock.getContentRepositoryDestination()).thenReturn(reportJobRepositoryDestinationMock);
        when(reportJobMock.getSource()).thenReturn(reportJobSourceMock);
        when(reportJobSourceMock.getReportUnitURI()).thenReturn(REPORT_UNIT_URI);
        when(repositoryMock.getResource(any(), eq(REPORT_UNIT_URI), eq(ReportUnit.class))).thenReturn(reportUnitMock);
        when(dataSnapshotServiceMock.isSnapshotPersistenceEnabled()).thenReturn(false);
    }

    @Test
    public void executeMethodShouldFlushLoggingContext() throws Exception {

        ReportExecutionJob.setLoggingContextProvider(loggingContextProviderMock);

        reportExecutionJob.execute(jobExecutionContextMock);
        verify(loggingContextProviderMock, times(1)).flushContext();
    }
}
