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

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.spi.OperableTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ReportJobsQuartzScheduler}
 *
 * @author vsabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportJobsQuartzSchedulerTest {

    @InjectMocks
    private ReportJobsQuartzScheduler reportJobsQuartzScheduler;

    @Mock
    private Scheduler scheduler;

    private OperableTrigger triggerMock = mock(OperableTrigger.class);
    private JobDataMap jobDataMapMock = mock(JobDataMap.class);

    private ReportUnit reportUnit1 = mock(ReportUnit.class);
    private ReportUnit reportUnit2 = mock(ReportUnit.class);
    private ReportUnit reportUnit3 = mock(ReportUnit.class);
    private ReportUnit reportUnit4 = mock(ReportUnit.class);

    private ReportExecutionJob reportExecutionJobMock1 = mock(ReportExecutionJob.class);
    private ReportExecutionJob reportExecutionJobMock2 = mock(ReportExecutionJob.class);
    private ReportExecutionJob reportExecutionJobMock3 = mock(ReportExecutionJob.class);
    private ReportExecutionJob reportExecutionJobMock4 = mock(ReportExecutionJob.class);

    
    private JobExecutionContext jobExecutionContext1 = mock(JobExecutionContext.class);
    private JobExecutionContext jobExecutionContext2 = mock(JobExecutionContext.class);
    private JobExecutionContext jobExecutionContext3 = mock(JobExecutionContext.class);
    private JobExecutionContext jobExecutionContext4 = mock(JobExecutionContext.class);
    
    private static final String GROUP = "ReportJobs";
    private static final String reportURI1 = "/public/diagnostic/DiagnosticReport_1"; 
    private static final String reportURI2 = "/public/diagnostic/DiagnosticReport_2"; 
    private static final String reportURI3 = "/public/diagnostic/DiagnosticReport_3"; 
    private static final String reportURI4 = "/public/diagnostic/DiagnosticReport_4";
    
    private static final String jobKeyName1 = "jobKey1"; 
    private static final String jobKeyName2 = "jobKey2"; 
    private static final String jobKeyName3 = "jobKey3"; 
    private static final String jobKeyName4 = "jobKey4"; 
    
    private JobKey jobKey1 = new JobKey(jobKeyName1, GROUP);
    private JobKey jobKey2 = new JobKey(jobKeyName2, GROUP);
    private JobKey jobKey3 = new JobKey(jobKeyName3);
    private JobKey jobKey4 = new JobKey(jobKeyName4);

    @Before
    public void setUp() throws SchedulerException {
        Set<JobKey> jobKeys = new HashSet<>();
        jobKeys.add(jobKey1);
        jobKeys.add(jobKey2);
        jobKeys.add(jobKey3);
        jobKeys.add(jobKey4);
        when(scheduler.getJobKeys(any())).thenReturn(jobKeys);

        JobDetailImpl jobDetail1 = new JobDetailImpl();
        jobDetail1.setKey(jobKey1);
        JobDetailImpl jobDetail2 = new JobDetailImpl();
        jobDetail2.setKey(jobKey2);
        JobDetailImpl jobDetail3 = new JobDetailImpl();
        jobDetail3.setKey(jobKey3);
        JobDetailImpl jobDetail4 = new JobDetailImpl();
        jobDetail4.setKey(jobKey4);

        when(scheduler.getJobDetail(eq(jobKey1))).thenReturn(jobDetail1);
        when(scheduler.getJobDetail(eq(jobKey2))).thenReturn(jobDetail2);
        when(scheduler.getJobDetail(eq(jobKey3))).thenReturn(jobDetail3);
        when(scheduler.getJobDetail(eq(jobKey4))).thenReturn(jobDetail4);

        when(triggerMock.getJobDataMap()).thenReturn(jobDataMapMock);

        List<JobExecutionContext> jobExecutionContexts = new ArrayList<>();

        when(reportUnit1.getURI()).thenReturn(reportURI1);
        when(reportUnit2.getURI()).thenReturn(reportURI2);
        when(reportUnit3.getURI()).thenReturn(reportURI3);
        when(reportUnit4.getURI()).thenReturn(reportURI4);

        reportExecutionJobMock1.reportUnit = reportUnit1;
        reportExecutionJobMock2.reportUnit = reportUnit2;
        reportExecutionJobMock3.reportUnit = reportUnit3;
        reportExecutionJobMock4.reportUnit = reportUnit4;

        when(jobExecutionContext1.getJobDetail()).thenReturn(jobDetail1);
        when(jobExecutionContext2.getJobDetail()).thenReturn(jobDetail2);
        when(jobExecutionContext3.getJobDetail()).thenReturn(jobDetail3);
        when(jobExecutionContext4.getJobDetail()).thenReturn(jobDetail4);

        when(jobExecutionContext1.getFireTime()).thenReturn(new Date());
        when(jobExecutionContext2.getFireTime()).thenReturn(new Date());
        when(jobExecutionContext3.getFireTime()).thenReturn(new Date());
        when(jobExecutionContext4.getFireTime()).thenReturn(new Date());

        when(jobExecutionContext1.getJobInstance()).thenReturn(reportExecutionJobMock1);
        when(jobExecutionContext2.getJobInstance()).thenReturn(reportExecutionJobMock2);
        when(jobExecutionContext3.getJobInstance()).thenReturn(reportExecutionJobMock3);
        when(jobExecutionContext4.getJobInstance()).thenReturn(reportExecutionJobMock4);

        jobExecutionContexts.add(jobExecutionContext1);
        jobExecutionContexts.add(jobExecutionContext2);
        jobExecutionContexts.add(jobExecutionContext3);
        jobExecutionContexts.add(jobExecutionContext4);

        when(scheduler.getCurrentlyExecutingJobs()).thenReturn(jobExecutionContexts);
    }

    @Test
    public void getDiagnosticDataTest() throws SchedulerException {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = reportJobsQuartzScheduler.getDiagnosticData();

        //Testing total size of diagnostic attributes collected from SessionRegistryDiagnosticService
        assertEquals(3, resultDiagnosticData.size());

        //Test collecting of TOTAL_SCHEDULED_JOBS
        int totalScheduledJobs = (Integer)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.
                TOTAL_SCHEDULED_JOBS, null, null)).getDiagnosticAttributeValue();
        assertEquals(4, totalScheduledJobs);

        verify(scheduler, atLeastOnce()).getJobKeys(any());
        verify(scheduler, atLeastOnce()).getJobDetail(eq(jobKey1));
        verify(scheduler, atLeastOnce()).getJobDetail(eq(jobKey2));
        verify(scheduler, atLeastOnce()).getJobDetail(eq(jobKey3));
        verify(scheduler, atLeastOnce()).getJobDetail(eq(jobKey4));

        //Test collecting of TOTAL_RUNNING_JOBS
        int totalRunningJobs = (Integer)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.
                TOTAL_RUNNING_JOBS, null, null)).getDiagnosticAttributeValue();
        assertEquals(2, totalRunningJobs);

        //Test collecting of RUNNING_JOBS_LIST
        Map<String, Map<String, Long>> runnningJobList = (Map<String, Map<String, Long>>)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.RUNNING_JOBS_LIST, null, null)).
                getDiagnosticAttributeValue();

        assertEquals(2, runnningJobList.size());
        
        Map<String, Long> reportUrisWithTimeExecutions1 = runnningJobList.get(jobKeyName1);
        Map<String, Long> reportUrisWithTimeExecutions2 = runnningJobList.get(jobKeyName2);

        assertNotNull(reportUrisWithTimeExecutions1.get(reportURI1));
        assertNotNull(reportUrisWithTimeExecutions2.get(reportURI2));
    }

}
