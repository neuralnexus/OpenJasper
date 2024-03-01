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

package com.jaspersoft.jasperserver.api.engine.scheduling;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.engine.BaseUnitTest;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEventType;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Taras Matyashovsky
 */
public class ReportSchedulingFacadeTest extends BaseUnitTest {

    @InjectMocks
    private ReportSchedulingFacade reportSchedulingFacade;

    @Mock
    private ReportJobsPersistenceService persistenceService;

    @Mock
    private ReportJobsInternalService jobsInternalService;

    @Mock
    private ReportJobsScheduler scheduler;

    @Mock
    private ReportJobValidator validator;

    @Mock
    private AuditContext auditContext;

    @Mock
    private Map outputKeyMapping;

    @After
    public void tearDown() {
        verifyNoMoreInteractions(
           //     persistenceService,
                jobsInternalService,
                scheduler,
                validator,
                auditContext,
                outputKeyMapping
        );
    }

    @Test
    public void getScheduledJobs() {
        ExecutionContext executionContext = getExecutionContext();

        /* Creating stub for output jobs. */
        List<ReportJobSummary> jobs = new ArrayList<ReportJobSummary>();
        jobs.add(new ReportJobSummary());

        /* Persistence service should once return jobs. */
        when(persistenceService.listJobs(executionContext)).thenReturn(jobs);

        /* Creating stub for output report job runtime information. */
        ReportJobRuntimeInformation[] reportJobRuntimeInformation = new ReportJobRuntimeInformation[1];
        reportJobRuntimeInformation[0] = new ReportJobRuntimeInformation();

        /* Scheduler should once return information about runtime jobs. */
        when(scheduler.getJobsRuntimeInformation(eq(executionContext), eq(new long[1]))).thenReturn(reportJobRuntimeInformation);

        /* Target method invocation. */
        List scheduledJobs = reportSchedulingFacade.getScheduledJobs(executionContext);

        /* Assertions. */
        assertNotNull("Scheduled jobs should not be null", scheduledJobs);
        assertEquals(1, scheduledJobs.size());

        /* Persistence service have been invoked from tested object with request to list jobs. */
        verify(persistenceService, atLeastOnce()).listJobs(executionContext);

        /* Scheduler have been invoked from tested object with request to retrieve job runtime information. */
        verify(scheduler, atLeastOnce()).getJobsRuntimeInformation(eq(executionContext), eq(new long[1]));
    }

    @Test
    public void removeScheduledJobs() {
        ExecutionContext executionContext = getExecutionContext();

        /* Identifiers of jobs to delete. */
        long[] jobIds = new long[1];
        jobIds[0] = 1;

        /* Persistence service should once return job for deletion. */
        when(persistenceService.loadJob(eq(executionContext), eq(new ReportJobIdHolder(jobIds[0])))).thenReturn(new ReportJob());

        /* Target method invocation. */
        reportSchedulingFacade.removeScheduledJobs(executionContext, jobIds);

        /* Audit context have been invoked from tested object. */
        verify(auditContext, atLeastOnce()).doInAuditContext(any());
        verify(auditContext, atLeastOnce()).doInAuditContext(eq(AuditEventType.DELETE_REPORT_SCHEDULING.toString()), any());

        /* Persistence service have been invoked from tested object with request to load job. */
        verify(persistenceService, times(1)).loadJob(executionContext, new ReportJobIdHolder(jobIds[0]));

        /* Persistence service have been invoked from tested object with request to delete job. */
        verify(persistenceService, times(1)).deleteJob(executionContext, new ReportJobIdHolder(jobIds[0]));

        /* Scheduler have been invoked from tested object with request to remove scheduled job. */
        verify(scheduler, times(1)).removeScheduledJob(executionContext, jobIds[0]);
    }

    @Test
    public void reportJobFinalized() {
        long jobId = 1L;

        /* Target method invocation. */
        reportSchedulingFacade.reportJobFinalized(jobId);

        /* Job internal service have been invoked from tested object with request to delete job. */
        verify(jobsInternalService, times(1)).deleteJob(jobId);
    }

    @Test
    public void updateScheduledJobSuccessWithRescheduling() {
        ExecutionContext executionContext = getExecutionContext();

        /* Dummy input report job. */
        ReportJob reportJob = new ReportJob();
        reportJob.setTrigger(new ReportJobSimpleTrigger());
        reportJob.getTrigger().setId(1L);

        ValidationErrors validationErrors = new ValidationErrorsImpl();
        /* Validator should once validate a job. */
        when(validator.validateJob(executionContext, reportJob)).thenReturn(validationErrors);

        /* Dummy input report job. */
        ReportJob updatedJob = new ReportJob();
        updatedJob.setTrigger(new ReportJobSimpleTrigger());
        updatedJob.getTrigger().setId(2L);

        /* Persistence service should once update job. */
        when(persistenceService.updateJob(executionContext, reportJob)).thenReturn(updatedJob);
        
        /* Target method invocation. */
        reportSchedulingFacade.updateScheduledJob(executionContext, reportJob);

        /* Audit context have been invoked from tested object. */
        verify(auditContext, atLeastOnce()).doInAuditContext(eq(AuditEventType.UPDATE_REPORT_SCHEDULING.toString()), any());

        /* Validator have been invoked from tested object with request to validate job. */
        verify(validator, atLeastOnce()).validateJob(executionContext, reportJob);

        /* Persistence service have been invoked from tested object with request to update job. */
        verify(persistenceService, times(1)).updateJob(executionContext, reportJob);

        /* Scheduler have been invoked from tested object with request to reschedule job. */
        verify(scheduler, times(1)).rescheduleJob(executionContext, updatedJob);
    }

    @Test
    public void updateScheduledJobSuccessWithoutRescheduling() {
        ExecutionContext executionContext = getExecutionContext();

        /* Dummy input report job. */
        ReportJob reportJob = new ReportJob();
        reportJob.setTrigger(new ReportJobSimpleTrigger());

        ValidationErrors validationErrors = new ValidationErrorsImpl();
        /* Validator should once validate a job. */
        when(validator.validateJob(executionContext, reportJob)).thenReturn(validationErrors);

        /* Dummy output report job. */
        ReportJob updatedJob = new ReportJob();
        updatedJob.setTrigger(new ReportJobSimpleTrigger());
        
        /* Persistence service should once update job. */
        when(persistenceService.updateJob(executionContext, reportJob)).thenReturn(updatedJob);

        /* Target method invocation. */
        reportSchedulingFacade.updateScheduledJob(executionContext, reportJob);

        /* Audit context have been invoked from tested object. */
        verify(auditContext, atLeastOnce()).doInAuditContext(eq(AuditEventType.UPDATE_REPORT_SCHEDULING.toString()), any());

        /* Validator have been invoked from tested object with request to validate job. */
        verify(validator, times(1)).validateJob(executionContext, reportJob);

        /* Persistence service have been invoked from tested object with request to update job. */
        verify(persistenceService, times(1)).updateJob(executionContext, reportJob);

        /* Scheduler have not been invoked from tested object with request to reschedule job. */
        verify(scheduler, never()).rescheduleJob(eq(executionContext), eq(updatedJob));
    }

    @Test
    public void updateScheduledJobFailure() {
        ExecutionContext executionContext = getExecutionContext();

        /* Dummy input report job. */
        ReportJob reportJob = new ReportJob();

        ValidationErrors validationErrors = new ValidationErrorsImpl();
        validationErrors.add(new ValidationErrorImpl("code", null, "message"));

        /* Validator should once validate a job with errors. */
        when(validator.validateJob(executionContext, reportJob)).thenReturn(validationErrors);

        try {
            /* Target method invocation. Should fail. */
            reportSchedulingFacade.updateScheduledJob(executionContext, reportJob);
            fail();
        } catch (JSValidationException e) {
            /* Validator have been invoked from tested object with request to validate job. */
            verify(validator, times(1)).validateJob(executionContext, reportJob);

            /* Persistence service have not been invoked from tested object with request to update job. */
            verify(persistenceService, never()).updateJob(executionContext, reportJob);
        }
    }

    @Test
    public void updateReportUnitURI() {
        /* Job internal service should once update report URI. */
        when(jobsInternalService.updateReportUnitURI("/oldURI", "/newURI")).thenReturn(new long[1]);

        /* Target method invocation. */
        reportSchedulingFacade.updateReportUnitURI("/oldURI", "/newURI");

        /* Job internal service have been invoked from tested object with request to update report URI. */
        verify(jobsInternalService, times(1)).updateReportUnitURI("/oldURI", "/newURI");
    }


    @Test
    public void pauseJob() {
        ExecutionContext executionContext = getExecutionContext();
        List<ReportJobIdHolder> jobs = new ArrayList<ReportJobIdHolder>();
        ReportJobIdHolder reportJobIdHolder = new ReportJobIdHolder(1245);
        when(persistenceService.loadJob(eq(executionContext), any())).thenThrow(new ReportJobNotFoundException(1245));
        jobs.add(reportJobIdHolder);
        List<Long> ids = reportSchedulingFacade.pauseJobs(executionContext, jobs, true);
        assertTrue(ids.isEmpty());

    }
}
