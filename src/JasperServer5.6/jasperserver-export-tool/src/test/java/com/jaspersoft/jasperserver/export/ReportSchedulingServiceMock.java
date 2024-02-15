package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.DuplicateOutputLocationException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.TriggerTypeMismatchException;

import java.util.Date;
import java.util.List;

/**
 * @author : Zakhar.Tomchenco
 */
public class ReportSchedulingServiceMock implements ReportSchedulingService {
    @Override
    public ReportJob scheduleJob(ExecutionContext context, ReportJob job) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJob> scheduleJobsOnceNow(ExecutionContext context, List<ReportJob> jobs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobIdHolder> scheduleJobsOnceNowById(ExecutionContext context, List<ReportJobIdHolder> jobs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateScheduledJob(ExecutionContext context, ReportJob job) throws ReportJobNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobIdHolder> updateScheduledJobsByID(ExecutionContext context, List<ReportJobIdHolder> reportJobHolders, ReportJobModel jobModel, boolean replaceTriggerIgnoreType) throws TriggerTypeMismatchException, ReportJobNotFoundException, DuplicateOutputLocationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJob> updateScheduledJobs(ExecutionContext context, List<ReportJob> reportJobs, ReportJobModel jobModel, boolean replaceTriggerIgnoreType) throws TriggerTypeMismatchException, DuplicateOutputLocationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List getScheduledJobs(ExecutionContext context, String reportUnitURI) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List getScheduledJobs(ExecutionContext context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context, String reportUnitURI) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context, ReportJobModel reportJobCriteria, int startIndex, int numberOfRows, ReportJobModel.ReportJobSortType sortType, boolean isAscending) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getJobsByNextFireTime(ExecutionContext context, List<ReportJob> searchList, Date startDate, Date endDate, List<Byte> includeTriggerStates) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ReportJobRuntimeInformation getJobRuntimeInformation(ExecutionContext context, long jobId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeScheduledJob(ExecutionContext context, long jobId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeScheduledJobs(ExecutionContext context, long[] jobIds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ReportJob getScheduledJob(ExecutionContext context, long jobId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ValidationErrors validateJob(ExecutionContext context, ReportJob job) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pause(List<ReportJob> jobs, boolean all) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume(List<ReportJob> jobs, boolean all) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pauseById(List<ReportJobIdHolder> jobs, boolean all) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resumeById(List<ReportJobIdHolder> jobs, boolean all) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
