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
package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.remote.exception.*;
import com.jaspersoft.jasperserver.remote.services.JobsService;
import com.jaspersoft.jasperserver.remote.services.impl.ReportJobCalendar.Type;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import org.quartz.Calendar;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.impl.calendar.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: JobsServiceImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("jobsService")
public class JobsServiceImpl implements JobsService {
    @Resource(name = "concreteReportSchedulingService")
    private ReportSchedulingService scheduler;
    @Resource
    private AuditHelper auditHelper;
    @Resource(name = "reportScheduler")
    private ReportJobsScheduler reportScheduler;

    public void setReportScheduler(ReportJobsScheduler reportScheduler) {
        this.reportScheduler = reportScheduler;
    }

    public void deleteJob(long id) throws RemoteException {
        auditHelper.createAuditEvent("deleteReportScheduling");
        scheduler.removeScheduledJob(makeExecutionContext(), id);
        auditHelper.closeAuditEvent("deleteReportScheduling");
    }

    public void deleteJobs(long[] ids) throws RemoteException {
        scheduler.removeScheduledJobs(makeExecutionContext(), ids);
    }


    public ReportJob getJob(long id) throws RemoteException {
        return scheduler.getScheduledJob(makeExecutionContext(), id);
    }

    public ReportJob scheduleJob(ReportJob reportJob) throws RemoteException {
        reportJob.setVersion(ReportJob.VERSION_NEW);
        reportJob.setCreationDate(new Timestamp(GregorianCalendar.getInstance().getTimeInMillis()));
        if (reportJob.getAlert() != null)
            reportJob.getAlert().setVersion(ReportJob.VERSION_NEW);
        auditHelper.createAuditEvent("scheduleReport");
        ReportJob savedJob = scheduler.scheduleJob(makeExecutionContext(), reportJob);
        auditHelper.closeAuditEvent("scheduleReport");
        return savedJob;
    }

    public ReportJob updateJob(ReportJob reportJob) throws RemoteException {
        ExecutionContext executionContext = makeExecutionContext();
        auditHelper.createAuditEvent("updateReportScheduling");
        scheduler.updateScheduledJob(executionContext, reportJob);
        auditHelper.closeAuditEvent("updateReportScheduling");
        return scheduler.getScheduledJob(executionContext, reportJob.getId());
    }

    public List<ReportJobSummary> getAllJobs() throws RemoteException {
        return scheduler.getScheduledJobSummaries(makeExecutionContext());
    }

    public List<ReportJobSummary> getJobSummariesByExample(ReportJobModel reportJobCriteria, Integer startIndex, Integer numberOfRows,
                                                           ReportJobModel.ReportJobSortType sortType, Boolean isAscending) throws RemoteException {
        return scheduler.getScheduledJobSummaries(makeExecutionContext(), reportJobCriteria, startIndex != null ? startIndex : 0, numberOfRows != null ? numberOfRows : -1,
                sortType != null ? sortType : ReportJobModel.ReportJobSortType.NONE, isAscending != null ? isAscending : true);
    }

    public List<ReportJobSummary> getReportJobs(String reportURI) throws RemoteException {
        return scheduler.getScheduledJobSummaries(makeExecutionContext(), reportURI);
    }

    public ReportJobRuntimeInformation getReportJobState(long jobId) throws RemoteException {
        return getJob(jobId) != null ? scheduler.getJobRuntimeInformation(makeExecutionContext(), jobId) : null;
    }

    public void updateReportJobs(List<Long> jobIds, ReportJobModel jobModel, Boolean replaceTriggerIgnoreType) {
        scheduler.updateScheduledJobsByID(makeExecutionContext(), getHolders(jobIds), jobModel, replaceTriggerIgnoreType);
    }

    public void pause(List<Long> jobIds) {
        scheduler.pauseById(getHolders(jobIds), isAllJobs(jobIds));
    }

    public void resume(List<Long> jobIds) {
        scheduler.resumeById(getHolders(jobIds), isAllJobs(jobIds));
    }

    public void scheduleJobsOnceNow(List<Long> jobIds) throws ResourceNotFoundException {
        try {
            scheduler.scheduleJobsOnceNowById(makeExecutionContext(), getHolders(jobIds));
        } catch (JSException e) {
            if (e.getCause() instanceof ReportJobNotFoundException) {
                final Object[] arguments = ((ReportJobNotFoundException) e.getCause()).getArgs();
                throw new ResourceNotFoundException(arguments != null && arguments.length > 0 ? arguments[0].toString() : "");
            } else {
                throw e;
            }
        }
    }

    public List<String> getCalendarNames() throws RemoteException {
        return reportScheduler.getCalendarNames();
    }

    public List<String> getCalendarNames(ReportJobCalendar.Type type) throws RemoteException {
        // quarz doesn't provide API to get calendar of specific type,
        // therefore we need to get calendar by name and check it's type
        final List<String> calendarNames = reportScheduler.getCalendarNames();
        if(calendarNames != null && type != null){
            Iterator<String> namesIterator = calendarNames.iterator();
            for(; namesIterator.hasNext();){
                String currentName = namesIterator.next();
                final ReportJobCalendar calendar = getCalendar(currentName);
                if(calendar.getCalendarType() != type) namesIterator.remove();
            }
        }
        return calendarNames;
    }

    public void deleteCalendar(String calendarName) {
        reportScheduler.deleteCalendar(calendarName);
    }

    public void addCalendar(String calendarName, ReportJobCalendar jobCalendar, Boolean replace, Boolean updateTriggers) throws MandatoryParameterNotFoundException, ResourceAlreadyExistsException, IllegalParameterValueException {
        try {
            reportScheduler.addCalendar(calendarName, toQuarzCalendar(jobCalendar), replace, updateTriggers);
        } catch (JSException e) {
            if (e.getCause() != null && e.getCause() instanceof ObjectAlreadyExistsException) {
                throw new ResourceAlreadyExistsException(calendarName);
            } else
                throw e;
        }
    }

    public ReportJobCalendar getCalendar(String calendarName) {
        return toServiceObject(reportScheduler.getCalendar(calendarName));
    }

    protected ReportJobCalendar toServiceObject(Calendar calendar) {
        ReportJobCalendar jobCalendar = null;
        if (calendar != null) {
            jobCalendar = new ReportJobCalendar();
            Type calendarType = Type.getTypeForClass(calendar.getClass());
            jobCalendar.setCalendarType(calendarType);
            jobCalendar.setBaseCalendar(toServiceObject(calendar.getBaseCalendar()));
            if (calendar instanceof BaseCalendar)
                jobCalendar.setTimeZone(((BaseCalendar) calendar).getTimeZone());
            jobCalendar.setDescription(calendar.getDescription());
            switch (calendarType) {
                case annual:
                    jobCalendar.setExcludeDays(((AnnualCalendar) calendar).getDaysExcluded());
                    break;
                case base:// do nothing, all base fields are already filled
                    break;
                case cron:
                    jobCalendar.setCronExpression(((CronCalendar) calendar).getCronExpression() != null ? ((CronCalendar) calendar).getCronExpression().toString() : null);
                    break;
                case daily: {
                    DailyCalendar dailyCalendar = (DailyCalendar) calendar;
                    java.util.Calendar simpleCalendar = (dailyCalendar.getTimeZone() == null) ?
                            java.util.Calendar.getInstance() :
                            java.util.Calendar.getInstance(dailyCalendar.getTimeZone());
                    simpleCalendar.setTime(new Date(dailyCalendar.getTimeRangeStartingTimeInMillis(simpleCalendar.getTimeInMillis())));
                    jobCalendar.setRangeStartingCalendar(simpleCalendar);
                    simpleCalendar = (dailyCalendar.getTimeZone() == null) ?
                            java.util.Calendar.getInstance() :
                            java.util.Calendar.getInstance(dailyCalendar.getTimeZone());
                    simpleCalendar.setTime(new Date(dailyCalendar.getTimeRangeEndingTimeInMillis(simpleCalendar.getTimeInMillis())));
                    jobCalendar.setRangeEndingCalendar(simpleCalendar);
                    jobCalendar.setInvertTimeRange(dailyCalendar.getInvertTimeRange());
                }
                break;
                case holiday: {
                    SortedSet<Date> dates = ((HolidayCalendar) calendar).getExcludedDates();
                    if (dates != null && !dates.isEmpty()) {
                        ArrayList<java.util.Calendar> excludeDays = new ArrayList<java.util.Calendar>();
                        for (Date currentDate : dates) {
                            final java.util.Calendar currentCalendar = java.util.Calendar.getInstance();
                            currentCalendar.setTime(currentDate);
                            excludeDays.add(currentCalendar);
                        }
                        jobCalendar.setExcludeDays(excludeDays);
                    }
                }
                break;
                case monthly:
                    jobCalendar.setExcludeDaysFlags(((MonthlyCalendar) calendar).getDaysExcluded());
                    break;
                case weekly: {
                    final boolean[] oneIndexedFlags = ((WeeklyCalendar) calendar).getDaysExcluded();
                    final boolean[] zerroIndexedFlags = new boolean[7];
                    // first flag doesn't matter. Exist to have 1-based indexing
                    // therefore iterate from 1
                    for (int i = 0; i < zerroIndexedFlags.length; i++)
                        zerroIndexedFlags[i] = oneIndexedFlags[i + 1];
                    jobCalendar.setExcludeDaysFlags(zerroIndexedFlags);
                }
                break;
            }
        }
        return jobCalendar;
    }

    protected Calendar toQuarzCalendar(ReportJobCalendar jobCalendar) throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        Calendar result = null;
        if (jobCalendar == null)
            throw new MandatoryParameterNotFoundException("reportJobCalendar");
        else if (jobCalendar.getCalendarType() == null)
            throw new MandatoryParameterNotFoundException("reportJobCalendar.calendarType");
        Calendar baseCalendar = jobCalendar.getBaseCalendar() != null ? toQuarzCalendar(jobCalendar.getBaseCalendar()) : null;
        ReportJobCalendar.Type type = jobCalendar.getCalendarType();
        final TimeZone timeZone = jobCalendar.getTimeZone() != null ? jobCalendar.getTimeZone() : TimeZone.getDefault();
        switch (type) {
            case base: {
                final BaseCalendar newBaseCalendar = new BaseCalendar(baseCalendar, timeZone);
                newBaseCalendar.setDescription(jobCalendar.getDescription());
                result = newBaseCalendar;
            }
            break;
            case annual: {
                final AnnualCalendar annualCalendar = new AnnualCalendar(baseCalendar, timeZone);
                annualCalendar.setDaysExcluded(jobCalendar.getExcludeDays());
                result = annualCalendar;
            }
            break;
            case cron:
                try {
                    result = new CronCalendar(baseCalendar, jobCalendar.getCronExpression(), timeZone);
                } catch (ParseException e) {
                    throw new IllegalParameterValueException("Couldn't parse cron expression", "reportJobCalendar.cronExpression", jobCalendar.getCronExpression());
                }
                break;
            case daily: {
                if (jobCalendar.getRangeStartingCalendar() == null)
                    throw new MandatoryParameterNotFoundException("reportJobCalendar.rangeStartingCalendar");
                if (jobCalendar.getRangeEndingCalendar() == null)
                    throw new MandatoryParameterNotFoundException("reportJobCalendar.rangeEndingCalendar");
                try {
                    final DailyCalendar dailyCalendar = new DailyCalendar(jobCalendar.getRangeStartingCalendar(), jobCalendar.getRangeEndingCalendar());
                    dailyCalendar.setBaseCalendar(baseCalendar);
                    dailyCalendar.setTimeZone(timeZone);
                    dailyCalendar.setInvertTimeRange(jobCalendar.isInvertTimeRange() != null ? jobCalendar.isInvertTimeRange() : false);
                    result = dailyCalendar;
                } catch (IllegalArgumentException e) {
                    throw new IllegalParameterValueException(
                            e.getMessage(),
                            "reportJobCalendar.rangeStartingCalendar",
                            DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, LocaleContextHolder.getLocale()).format(jobCalendar.getRangeStartingCalendar().getTime()),
                            "reportJobCalendar.rangeEndingCalendar",
                            DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, LocaleContextHolder.getLocale()).format(jobCalendar.getRangeEndingCalendar().getTime()));
                }
            }
            break;
            case holiday: {
                final HolidayCalendar holidayCalendar = new HolidayCalendar();
                holidayCalendar.setBaseCalendar(baseCalendar);
                if (jobCalendar.getExcludeDays() != null && !jobCalendar.getExcludeDays().isEmpty())
                    for (java.util.Calendar currentCalendar : jobCalendar.getExcludeDays()) {
                        holidayCalendar.addExcludedDate(currentCalendar.getTime());
                    }
                holidayCalendar.setTimeZone(timeZone);
                result = holidayCalendar;
            }
            break;
            case monthly: {
                if (jobCalendar.getExcludeDaysFlags() == null)
                    throw new MandatoryParameterNotFoundException("reportJobCalendar.excludeDaysFlags");
                if (jobCalendar.getExcludeDaysFlags() != null && jobCalendar.getExcludeDaysFlags().length < 31)
                    throw new IllegalParameterValueException("The days parameter must have a length of at least 31 elements.", "reportJobCalendar.excludeDaysFlags", "" + jobCalendar.getExcludeDaysFlags().length);
                final MonthlyCalendar monthlyCalendar = new MonthlyCalendar(baseCalendar, timeZone);
                monthlyCalendar.setDaysExcluded(jobCalendar.getExcludeDaysFlags());
                result = monthlyCalendar;
            }
            break;
            case weekly: {
                final WeeklyCalendar weeklyCalendar = new WeeklyCalendar(baseCalendar, timeZone);
                if (jobCalendar.getExcludeDaysFlags() == null)
                    throw new MandatoryParameterNotFoundException("reportJobCalendar.excludeDaysFlags");
                if (jobCalendar.getExcludeDaysFlags() != null && jobCalendar.getExcludeDaysFlags().length < 7)
                    throw new IllegalParameterValueException("The days parameter must have a length of at least 7 elements.", "reportJobCalendar.excludeDaysFlags", "" + jobCalendar.getExcludeDaysFlags().length);
                final boolean[] zerroIndexedFlags = jobCalendar.getExcludeDaysFlags();
                final boolean[] oneIndexedFlags = new boolean[8];
                // first flag doesn't matter. Exist to have 1-based indexing
                // therefore iterate from 1
                for (int i = 1; i < oneIndexedFlags.length; i++)
                    oneIndexedFlags[i] = zerroIndexedFlags[i - 1];
                weeklyCalendar.setDaysExcluded(oneIndexedFlags);
                result = weeklyCalendar;
            }
            break;
        }
        if (result != null)
            result.setDescription(jobCalendar.getDescription());
        return result;
    }

    protected Boolean isAllJobs(List<Long> jobIds) {
        return jobIds == null || jobIds.isEmpty();
    }

    protected List<ReportJobIdHolder> getHolders(List<Long> jobIds) {
        List<ReportJobIdHolder> holders = new ArrayList<ReportJobIdHolder>();
        if (jobIds != null && !jobIds.isEmpty())
            for (Long jobId : jobIds)
                holders.add(new ReportJobIdHolder(jobId));
        return holders;
    }

    protected ExecutionContext makeExecutionContext() {
        ExecutionContextImpl executionContext = new ExecutionContextImpl();
        executionContext.setLocale(getLocale());
        return executionContext;
    }

    protected Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public ReportSchedulingService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ReportSchedulingService scheduler) {
        this.scheduler = scheduler;
    }

    public AuditHelper getAuditHelper() {
        return auditHelper;
    }

    public void setAuditHelper(AuditHelper auditHelper) {
        this.auditHelper = auditHelper;
    }
}
