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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.LogEvent;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.LoggingService;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.ContentResourceURIResolver;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequest;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.EngineServiceImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobAlert;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.context.impl.DummyAuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainerFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockManager;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.SimpleReportContext;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.WeakHashMap;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportExecutionJob.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
public class ReportExecutionJob implements Job {
	
	private static final Log log = LogFactory.getLog(ReportExecutionJob.class);
	
	public static final String REPORT_PARAMETER_SCHEDULED_TIME = "_ScheduledTime";
	
	public static final String REPOSITORY_FILENAME_SEQUENCE_SEPARATOR = "-";
	public static final String REPOSITORY_FILENAME_TIMESTAMP_SEQUENCE_PATTERN = "yyyyMMddHHmm";
	
	public static final String SCHEDULER_CONTEXT_KEY_APPLICATION_CONTEXT = "applicationContext";
	
	public static final String SCHEDULER_CONTEXT_KEY_JOB_PERSISTENCE_SERVICE = "jobPersistenceService";
    public static final String SCHEDULER_CONTEXT_KEY_JOB_REPORT_SCHEDULING_SERVICE = "reportSchedulingService";
	public static final String SCHEDULER_CONTEXT_KEY_ENGINE_SERVICE = "engineService";
	public static final String SCHEDULER_CONTEXT_KEY_VIRTUALIZER_FACTORY = "virtualizerFactory";
	public static final String SCHEDULER_CONTEXT_KEY_REPOSITORY = "repositoryService";
	public static final String SCHEDULER_CONTEXT_KEY_MAIL_SENDER = "mailSender";
	public static final String SCHEDULER_CONTEXT_KEY_MAIL_FROM_ADDRESS = "mailFromAddress";
	public static final String SCHEDULER_CONTEXT_KEY_LOGGING_SERVICE = "loggingService";
	public static final String SCHEDULER_CONTEXT_KEY_SECURITY_CONTEXT_PROVIDER = "securityContextProvider";
	public static final String SCHEDULER_CONTEXT_KEY_HYPERLINK_PRODUCER_FACTORY = "hyperlinkProducerFactory";
	public static final String SCHEDULER_CONTEXT_KEY_ENCODING_PROVIDER = "encodingProvider";
	public static final String SCHEDULER_CONTEXT_KEY_EXPORT_PARAMETRES_MAP = "exportParametersMap";
	public static final String SCHEDULER_CONTEXT_KEY_DATA_CONTAINER_FACTORY = "dataContainerFactory";
	public static final String SCHEDULER_CONTEXT_KEY_CONTENT_RESOURCE_URI_RESOLVER = "contentResourceURIResolver";
	public static final String SCHEDULER_CONTEXT_KEY_LOCK_MANAGER = "lockManager";
	public static final String SCHEDULER_CONTEXT_KEY_OUTPUT_FORMAT_MAP = "outputFormatMap";
	public static final String SCHEDULER_CONTEXT_KEY_OUTPUT_KEY_MAPPING = "outputKeyMapping";
    public static final String SCHEDULER_CONTEXT_KEY_ADMINISTRATOR_ROLE = "administratorRole";
    public static final String SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_INIT = "reportExecutionJobInit";
    public static final String SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_ALERT = "reportExecutionJobAlert";
    public static final String SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_MAIL_NOTIFICATION = "reportExecutionJobMailNotification";
    public static final String SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_FILE_SAVING = "reportExecutionJobFileSaving";
	public static final String SCHEDULER_CONTEXT_KEY_DATA_SNAPSHOT_SERVICE_BEAN = "dataSnapshotServiceName";
	public static final String SCHEDULER_CONTEXT_KEY_DATA_CACHE_PROVIDER_BEAN = "dataCacheProviderName";
    public static final String SCHEDULER_CONTEXT_KEY_AUTO_DELETE_BROKEN_URI_REPORT_JOB = "autoDeleteBrokenUriReportJob";
	public static final String SCHEDULER_CONTEXT_KEY_JASPERREPORTS_CONTEXT_BEAN = "jasperReportsContextName";
	public static final String SCHEDULER_CONTEXT_KEY_DISABLE_SENDING_ALERT_TO_ADMIN = "disableSendingAlertToAdmin";
    public static final String SCHEDULER_CONTEXT_KEY_DISABLE_SENDING_ALERT_TO_OWNER = "disableSendingAlertToOwner";

	public static final String JOB_DATA_KEY_DETAILS_ID = "jobDetailsID";	
	public static final String JOB_DATA_KEY_USERNAME = "jobUser";
	
	public static final String LOGGING_COMPONENT = "reportScheduler";
	
	protected List<ExceptionInfo> exceptions = new ArrayList<ExceptionInfo>();
	protected ApplicationContext applicationContext;
	protected String username;
	protected ReportJob jobDetails;
	protected ReportUnit reportUnit;
	protected JobExecutionContext jobContext;
	protected SchedulerContext schedulerContext;
	protected ExecutionContext executionContext;

    protected static AuditContext auditContext = new DummyAuditContext();
    protected static LoggingContextProvider loggingContextProvider;
    protected boolean cancelRequested = false;
    
	private ReportContext reportContext;
	private boolean hasDataSnapshotOutput;
	private boolean recordDataSnapshot;
	private boolean recordedDataSnapshot;
	private String dataSnapshotOutputName;
	
	private final WeakHashMap<DataContainer, Boolean> dataContainers = new WeakHashMap<DataContainer, Boolean>();
    
    public static void setAuditContext(AuditContext auditContext) {
        ReportExecutionJob.auditContext = auditContext;
    }

    public static void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
        ReportExecutionJob.loggingContextProvider = loggingContextProvider;
    }

    protected void createAuditEvent() {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent("runReport");
            }
        });
    }

    protected void addReportJobLabelToAuditEvent(final long jobID, final String jobLabel) {
        auditContext.doInAuditContext("runReport", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent("jobID", jobID, auditEvent);
                auditContext.addPropertyToAuditEvent("jobLabel", jobLabel, auditEvent);
            }
        });
    }

    protected void addExceptionToAuditEvent(final Throwable ex) {
        auditContext.doInAuditContext("runReport", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent("exception", ex, auditEvent);
            }
        });
    }

    protected void closeAuditEvent() {
        auditContext.doInAuditContext("runReport", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });

        //Not only audit events are produced during report execution, but
        //also other event types like access events,
        //so we need to flush them all to avoid memory leak (see bug #25994)
        if (loggingContextProvider != null) {
            loggingContextProvider.flushContext();
        }
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
            this.jobContext = context;
			this.schedulerContext = jobContext.getScheduler().getContext();
			this.applicationContext = (ApplicationContext) schedulerContext.get(SCHEDULER_CONTEXT_KEY_APPLICATION_CONTEXT);
			this.username = getUsername();
			SecurityContextProvider securityContextProvider = getSecurityContextProvider();
			securityContextProvider.setAuthenticatedUser(this.username);
            createAuditEvent();
			try {
				executeAndSendReport();
			} finally {
				securityContextProvider.revertAuthenticatedUser();
			}
		} catch (JobExecutionException e) {
            addExceptionToAuditEvent(e);
			throw e;
		} catch (SchedulerException e) {
            addExceptionToAuditEvent(e);
			throw new JobExecutionException(e);
		} finally {
            closeAuditEvent();
			clear();
		}
	}

	protected void initJobExecution() {
		updateExecutionContextDetails();
	}

	protected void clear() {
		exceptions.clear();
		jobContext = null;
		schedulerContext = null;
		jobDetails = null;
		reportUnit = null;
		executionContext = null;
		username = null;
		
		reportContext = null;
		hasDataSnapshotOutput = false;
		recordDataSnapshot = false;
		recordedDataSnapshot = false;
		dataSnapshotOutputName = null;
		
		for (DataContainer dataContainer : dataContainers.keySet()) {
			dataContainer.dispose();
		}
		dataContainers.clear();
	}

	protected String getUsername() {
		JobDataMap jobDataMap = jobContext.getTrigger().getJobDataMap();
		return jobDataMap.getString(JOB_DATA_KEY_USERNAME);
	}

	protected ExecutionContext getExecutionContext() {
		return new ExecutionContextImpl();
	}

	protected void updateExecutionContextDetails() {
		ExecutionContextImpl context = (ExecutionContextImpl) executionContext;
		context.setLocale(getLocale());
		// using default system timezone, and not job trigger timezone
		context.setTimeZone(TimeZone.getDefault());
	}
	
	protected Locale getJobLocale() {
		String localeCode = jobDetails.getOutputLocale();
		Locale locale;
		if (localeCode != null && localeCode.length() > 0) {
			locale = LocaleHelper.getInstance().getLocale(localeCode);
		} else {
			locale = null;
		}
		return locale;
	}
	
	protected Locale getMessageLocale() {
		return getLocale();
	}

	protected Locale getLocale() {
		Locale locale = null;
		// the jobDetails might not be loaded
		if (jobDetails != null) {
			locale = getJobLocale();
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
	
	protected String getMessage(String key, Object[] arguments) {
		return applicationContext.getMessage(key, arguments, getMessageLocale());
	}
	
	protected void handleException(String message, Throwable exc) {
		log.error(message, exc);
		exceptions.add(new ExceptionInfo(message, exc));
	}

	protected boolean hasExceptions() {
		return !exceptions.isEmpty();
	}
	
	protected void checkExceptions() throws JobExecutionException {
		if (hasExceptions()) {
			ExceptionInfo firstException = ((ExceptionInfo) exceptions.get(0));
			
			try {
				logExceptions();
			} catch (Exception e) {
				log.error(e, e);
				throwJobExecutionException(firstException);
			}

			throwJobExecutionException(firstException);
		}
	}
	
	protected void throwJobExecutionException(ExceptionInfo exceptionInfo) throws JobExecutionException {
		JobExecutionException jobException;
		Throwable exception = exceptionInfo.getException();
		if (exception instanceof Exception) {
			jobException = new JobExecutionException(exceptionInfo.getMessage(), (Exception) exception, false);
		} else {
			jobException = new JobExecutionException(exceptionInfo.getMessage());
		}
		throw jobException;
	}

	protected void logExceptions() {
		LoggingService loggingService = getLoggingService();
		LogEvent event = loggingService.instantiateLogEvent();
		event.setComponent(LOGGING_COMPONENT);
		event.setType(LogEvent.TYPE_ERROR);
		event.setMessageCode("log.error.report.job.failed");
		if (jobDetails != null) {
			event.setResourceURI(jobDetails.getSource().getReportUnitURI());
		}
		
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		if (jobDetails != null) {
			printWriter.println("Job: " + jobDetails.getLabel() + " (ID: " + jobDetails.getId() + ")");
			printWriter.println("Report unit: " + jobDetails.getSource().getReportUnitURI());			
		}
		printWriter.println("Quartz Job: " + jobContext.getJobDetail().getKey());
		printWriter.println("Quartz Trigger: " + jobContext.getTrigger().getKey());
        //printWriter.println("Quartz Job: " + jobContext.getJobDetail().getFullName());
		//printWriter.println("Quartz Trigger: " + jobContext.getTrigger().getFullName());
		
		printWriter.println("Exceptions:");
		for (ListIterator it = exceptions.listIterator(); it.hasNext();) {
			ExceptionInfo exceptionInfo = (ExceptionInfo) it.next();
			printWriter.println();
			printWriter.println(exceptionInfo.getMessage());
			
			Throwable exception = exceptionInfo.getException();
			if (exception != null) {
				exception.printStackTrace(printWriter);
			}
		}
		
		printWriter.flush();
		event.setText(writer.toString());
		event.setState(LogEvent.STATE_UNREAD);
		
		loggingService.log(event);
	}

	protected SecurityContextProvider getSecurityContextProvider() {
		return (SecurityContextProvider) schedulerContext.get(SCHEDULER_CONTEXT_KEY_SECURITY_CONTEXT_PROVIDER);
	}

	protected LoggingService getLoggingService() {
		return (LoggingService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_LOGGING_SERVICE);
	}

	protected ReportJob getJobDetails() {
		ReportJobsPersistenceService persistenceService = getPersistenceService();
		JobDataMap jobDataMap = jobContext.getTrigger().getJobDataMap();
		long jobId = jobDataMap.getLong(JOB_DATA_KEY_DETAILS_ID);
		ReportJob job = persistenceService.loadJob(executionContext, new ReportJobIdHolder(jobId));
		return job;
	}

	protected ReportJobsPersistenceService getPersistenceService() {
		return (ReportJobsPersistenceService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_JOB_PERSISTENCE_SERVICE);
	}

    protected ReportSchedulingService getReportSchedulingService() {
        return (ReportSchedulingService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_JOB_REPORT_SCHEDULING_SERVICE);
    }

	protected EngineService getEngineService() {
		EngineService engineService = (EngineService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_ENGINE_SERVICE);
		return engineService;
	}

	protected VirtualizerFactory getVirtualizerFactory() {
		return (VirtualizerFactory) schedulerContext.get(SCHEDULER_CONTEXT_KEY_VIRTUALIZER_FACTORY);
	}

    protected void executeAndSendReport() throws JobExecutionException {
        try {
            executionContext = getExecutionContext();
            jobDetails = getJobDetails();
            initJobExecution();
            addReportJobLabelToAuditEvent(jobDetails.getId(), jobDetails.getLabel());
            ReportUnitResult paginatedResult = null;
            ReportUnitResult nonPaginatedResult = null;
            boolean isMailNotificationSent = false;
            try {
                reportUnit = (ReportUnit) getRepository().getResource(executionContext,
                        getReportUnitURI(), ReportUnit.class);
                if (reportUnit == null) {
                    if (isAutoDeleteBrokenUriReportJob()) {
                        getReportSchedulingService().removeScheduledJob(executionContext, jobDetails.getId());
                        log.info("The following report doesn't exist: "+ getReportUnitURI() + ".  Deleting ReportJob ID = " + jobDetails.getId());
                        return;
                    } else {
                        throw new JSException("report.scheduling.error.broken.report.uri");
                    }
                }

                if (getReportExecutionJobInit() != null)
                    jobDetails = getReportExecutionJobInit().initJob(this, jobDetails);
                boolean needPaginatedReport = false;
                boolean needNonPaginatedReport = false;

                JasperReport jasperReport = getEngineService().getMainJasperReport(executionContext, getReportUnitURI());

                List<Output> outputs = createOutputs();
                for (Output output : outputs) {
                    Boolean isPaginationPreferred = output.isPaginationPreferred(jasperReport);
                    if (isPaginationPreferred != null)
                    {
                        needPaginatedReport = needPaginatedReport || isPaginationPreferred;
                        needNonPaginatedReport = needNonPaginatedReport || !isPaginationPreferred;
                    }
                }
                isCancelRequested();
                // if we have data snapshot output but no regular output, run the paginated report
                if (hasDataSnapshotOutput && !needPaginatedReport && !needNonPaginatedReport) {
                    needPaginatedReport = true;
                }

                // recording a data snapshot if saving is enabled or if we need to fill the report twice
                recordDataSnapshot = getDataSnapshotService().isSnapshotPersistenceEnabled()
                        || (needPaginatedReport && needNonPaginatedReport);

                if (!needPaginatedReport && !needNonPaginatedReport)
                {
                	paginatedResult = executeReport(null);
                }
                else
                {
                    if (needPaginatedReport) {
                        paginatedResult = executeReport(Boolean.FALSE);
                    }

                    if (needNonPaginatedReport) {
                        nonPaginatedResult = executeReport(Boolean.TRUE);
                    }
                }

                if (paginatedResult != null || nonPaginatedResult != null) {
                    isCancelRequested();
                    ReportJobMailNotification mailNotification = jobDetails.getMailNotification();
                    boolean skipEmpty = false;
                    if (mailNotification != null) {
                        skipEmpty = mailNotification.isSkipEmptyReports() && (isEmpty(paginatedResult) || isEmpty(nonPaginatedResult));
                    }
                    List<ReportOutput> reportOutputs = new ArrayList<ReportOutput>();

                    if (!skipEmpty) {
                        String baseFileName = getBaseFileName();
                        boolean useFolderHierarchy = true;

                        if ((mailNotification != null) &&
                                ((mailNotification.getResultSendTypeCode() == ReportJobMailNotification.RESULT_SEND_ATTACHMENT_NOZIP) ||
                                        (mailNotification.getResultSendTypeCode() == ReportJobMailNotification.RESULT_SEND_EMBED))) {
                            useFolderHierarchy = false;
                        }

                        for (Output output : outputs) {
                            ReportOutput reportOutput = null;
                            Boolean isPaginationPreferred = output.isPaginationPreferred(jasperReport);
                            ReportUnitResult resultToExport = 
                                isPaginationPreferred == null
                                ? (paginatedResult == null ? nonPaginatedResult : paginatedResult)
                                : (isPaginationPreferred ? paginatedResult : nonPaginatedResult);

                            if (resultToExport != null) 
                            {
                                // enforce to use grid-base HTML exporter for embedded report in email
                                // DIV doesn't work well in email
                                if ((mailNotification != null) && (mailNotification.getResultSendType() == ReportJobMailNotification.RESULT_SEND_EMBED) &&
                                        (output instanceof HtmlReportOutput)) {
                                    ((HtmlReportOutput) output).setForceToUseHTMLExporter(true);
                                }
                                isCancelRequested();
                                try {
                                    reportOutput = output.getOutput(
                                            getEngineService(),
                                            executionContext,
                                            getReportUnitURI(),
                                            createDataContainer(output),
                                            getHyperlinkProducerFactory(),
                                            (useFolderHierarchy ? getRepository() : null),
                                            resultToExport.getJasperPrint(),
                                            baseFileName,
                                            getLocale(),
                                            getCharacterEncoding()
                                    );
                                } catch (Exception e) {
                                    String fileExtension = null;
                                    final Map outputFormatMap = getOutputFormatMap();
                                    for(Object currentExtension : outputFormatMap.keySet()){
                                        if(outputFormatMap.get(currentExtension) == output){
                                            fileExtension = currentExtension.toString();
                                            break;
                                        }
                                    }
                                    // log the error and continue with outputs generation
                                    handleException(getMessage("report.scheduling.error.exporting.report", new Object[]{fileExtension}), e);
                                    continue;
                                }
                                isCancelRequested();
                                if (reportOutput != null) reportOutputs.add(reportOutput);

                            }

                            isCancelRequested();


                            if ((!useFolderHierarchy) && (jobDetails.getContentRepositoryDestination() != null) &&
                                    (jobDetails.getContentRepositoryDestination().isSaveToRepository()) && (!reportOutput.getChildren().isEmpty())) {
                                // if not using hierarchy, but contains children and requires to save to repository.  regenerate the output with folder hierarchy
                                ReportOutput reportOutputForRepository = output.getOutput(getEngineService(), executionContext, getReportUnitURI(),
                                        createDataContainer(output), getHyperlinkProducerFactory(), getRepository(),
                                        output.isIgnorePagination() != null && output.isIgnorePagination() ? nonPaginatedResult.getJasperPrint() : paginatedResult.getJasperPrint(),
                                        baseFileName, getLocale(), getCharacterEncoding());
                                isCancelRequested();
                                if (reportOutputForRepository != null)
                                    getReportExecutionJobFileSaving().save(this, reportOutputForRepository, true, jobDetails);
                            } else
                                getReportExecutionJobFileSaving().save(this, reportOutput, useFolderHierarchy, jobDetails);
                        }
                    }

                    if (mailNotification != null) {
                        if (!skipEmpty || hasExceptions()) {
                            List attachments = skipEmpty ? null : reportOutputs;
                            isCancelRequested();
                            try {
                                isMailNotificationSent = true;
                                sendMailNotification(attachments);
                            } catch (Exception e) {
                                handleException(getMessage("report.scheduling.error.sending.email.notification", null), e);
                            }
                        }
                    }
                }
            } catch (CancelRequestException cancelRequestException) {
                handleException(getMessage("report.scheduling.cancelling.by.request", null), cancelRequestException);
            } catch (Exception otherException) {
                handleException(getMessage("error.generating.report", null), otherException);
            } finally {
                disposeVirtualizer(paginatedResult);
                disposeVirtualizer(nonPaginatedResult);
                if (!isMailNotificationSent) {
                    try {
                        // only send mail notification when exception is found
                        if ((jobDetails.getMailNotification() != null) && hasExceptions()) {
                            sendMailNotification(new ArrayList());
                        }
                    } catch (Exception e) {
                        handleException(getMessage("report.scheduling.error.sending.email.notification", null), e);
                    }
                }
                try {
                    sendAlertMail();
                } catch (Exception e) {
                    handleException(getMessage("fail to send out alert mail notification", null), e);
                }
            }
        } catch (Throwable e) {
            handleException(getMessage("report.scheduling.error.system", null), e);
        } finally {
            checkExceptions();
        }
    }

    protected void sendAlertMail()  throws JobExecutionException {
		ReportJobAlert alert = jobDetails.getAlert();
        if (alert == null) return;
        JavaMailSender mailSender = getMailSender();
	    String fromAddress = getFromAddress();
        String[] toAddresses = getAlertMailRecipients(alert);
        if ((toAddresses == null) || (toAddresses.length == 0)) return;
        String characterEncoding = getCharacterEncoding();
        getReportExecutionJobAlert().sendAlertMail(this, jobDetails, exceptions, mailSender, fromAddress, toAddresses, characterEncoding);
	}

	protected void disposeVirtualizer(ReportUnitResult result)
	{
		if (result != null)
		{
			VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
			if (virtualizerFactory != null)
			{
				virtualizerFactory.disposeReport(result);
			}
		}
	}

	protected void setReadOnly(ReportUnitResult result) {
		if (result != null)
		{
			VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
			if (virtualizerFactory != null)
			{
				virtualizerFactory.setReadOnly(result);
			}
		}
	}

	protected ReportUnitResult executeReport(Boolean ignorePagination) 
	{
		ReportUnitResult result = null;
		try
		{
			Map parametersMap = collectReportParameters();
            Map reportJobProperties = collectReportJobProperties();
			
			if (ignorePagination != null)
			{
				parametersMap.put(JRParameter.IS_IGNORE_PAGINATION, ignorePagination);
            }
			ReportUnitRequest request = new ReportUnitRequest(getReportUnitURI(), parametersMap, reportJobProperties);
			request.setJasperReportsContext(getJasperReportsContext());

			result = runReport(request);
		}
		catch (Exception e)
		{
			handleException(getMessage("report.scheduling.error.filling.report", null), e);
		}
		return result;
	}

	protected JasperReportsContext getJasperReportsContext() {
		String contextBeanName = schedulerContext.getString(SCHEDULER_CONTEXT_KEY_JASPERREPORTS_CONTEXT_BEAN);
		return applicationContext.getBean(contextBeanName, JasperReportsContext.class);
	}

	protected ReportUnitResult runReport(ReportUnitRequest request) {
		boolean firstRun = false;
		boolean recordingSnapshot = false;
		boolean useSnapshot = false;
		if (reportContext == null) {
			firstRun = true;
			// we need a report context for data caching
			reportContext = new SimpleReportContext();
			// record the snapshot if enabled
			recordingSnapshot = recordDataSnapshot;
		} else {
			// use the snapshot from the first execution
			useSnapshot = recordedDataSnapshot;
		}
		
		request.getReportParameters().put(JRParameter.REPORT_CONTEXT, reportContext);
		request.setReportContext(reportContext);
		request.setRecordDataSnapshot(recordingSnapshot);
		request.setUseDataSnapshot(useSnapshot);
		
		EngineService engineService = getEngineService();
		ReportUnitResult result = (ReportUnitResult) engineService.execute(executionContext, request);
		setReadOnly(result);
		
		if (firstRun && getDataSnapshotService().isSnapshotPersistenceEnabled()) {
			dataSnapshotRecorded();
		}
		
		return result;
	}
	
	protected void dataSnapshotRecorded() {
		DataCacheProvider dataCacheProvider = getDataCacheProvider();
		DataCacheSnapshot dataSnapshot = dataCacheProvider.getDataSnapshot(executionContext, reportContext);
		if (dataSnapshot != null) {
			// set the flag so that the snapshot will be used at the next report fill
			recordedDataSnapshot = true;
		}
		
		// always updating in-place the data snapshot
		updateDataSnapshot(dataSnapshot);
		
		if (hasDataSnapshotOutput) {
			// save report unit copy with data snapshot
			saveDataSnapshotOutput(dataSnapshot);
		}
	}

	protected void updateDataSnapshot(DataCacheSnapshot dataSnapshot) {
		DataSnapshotService snapshotService = getDataSnapshotService();
		if (!snapshotService.isSnapshotPersistenceEnabled()) {
			return;
		}
		
		if (dataSnapshot == null) {
			if (log.isDebugEnabled()) {
				log.debug("failed to record data snapshot for job " + jobDetails.getId());
			}
			// do not fail because data snapshot output was not explicitly requested
			return;
		}
		
		if (!dataSnapshot.getSnapshot().isPersistable()) {
			if (log.isDebugEnabled()) {
				log.debug("data snapshot for job " + jobDetails.getId() + " is not persistable");
			}
			// do not fail because data snapshot output was not explicitly requested
			return;
		}
		
		try {
			if (log.isDebugEnabled()) {
				log.debug("updating report with data snapshot at " + reportUnit.getURIString());
			}

			// TODO lucianc save without report unit write permissions?
			snapshotService.saveReportDataSnapshot(executionContext, reportContext, reportUnit);
			
			// reload the updated report unit
			reportUnit = (ReportUnit) getRepository().getResource(executionContext, 
					reportUnit.getURIString(), ReportUnit.class);
		} catch (Exception e) {
			// treat as a non critical error
			if (log.isWarnEnabled()) {
				log.warn("Failed to update data snapshot for report " + reportUnit.getURIString(), e);
			}
		}
	}

	protected void saveDataSnapshotOutput(DataCacheSnapshot dataSnapshot) {
		try {
			DataSnapshotService snapshotService = getDataSnapshotService();
			if (!snapshotService.isSnapshotPersistenceEnabled()) {
				if (log.isDebugEnabled()) {
					log.debug("data snapshot persistence disabled for " + jobDetails.getId());
				}
			
				throw new JSException("report.scheduling.error.saving.data.snapshot.disabled");
			}
			
			if (dataSnapshot == null) {
				if (log.isDebugEnabled()) {
					log.debug("failed to record data snapshot for job " + jobDetails.getId());
				}
			
				throw new JSException("report.scheduling.error.data.snapshot.not.populated");
			}
			
			if (!dataSnapshot.getSnapshot().isPersistable()) {
				if (log.isDebugEnabled()) {
					log.debug("data snapshot for job " + jobDetails.getId() + " is not persistable");
				}
			
				throw new JSException("report.scheduling.error.data.snapshot.not.persistable");
			}
			
			String destinationFolderURI = jobDetails.getContentRepositoryDestination().getFolderURI();
			if (destinationFolderURI.equals(reportUnit.getParentPath())
					&& dataSnapshotOutputName.equals(reportUnit.getName())) {
				// data snapshot was already updated in-place, nothing to do
	 		} else {
				if (log.isDebugEnabled()) {
					log.debug("saving report with data snapshot at " + destinationFolderURI + "/" + dataSnapshotOutputName);
				}
				
				String savedURI = snapshotService.saveReportDataSnapshotCopy(executionContext, reportContext, reportUnit, destinationFolderURI, 
						dataSnapshotOutputName, jobDetails.getContentRepositoryDestination().isOverwriteFiles());
				
				if (log.isDebugEnabled()) {
					log.debug("saved report copy at " + savedURI);
				}
	 		}
		} catch (Exception e) {
			handleException(getMessage("report.scheduling.error.saving.data.snapshot.failed", null), e);
		}
		// TODO lucianc link to the report in the mail notification
	}

	protected DataSnapshotService getDataSnapshotService() {
		String serviceBeanName = schedulerContext.getString(SCHEDULER_CONTEXT_KEY_DATA_SNAPSHOT_SERVICE_BEAN);
		return applicationContext.getBean(serviceBeanName, DataSnapshotService.class);
	}

	protected DataCacheProvider getDataCacheProvider() {
		String serviceBeanName = schedulerContext.getString(SCHEDULER_CONTEXT_KEY_DATA_CACHE_PROVIDER_BEAN);
		return applicationContext.getBean(serviceBeanName, DataCacheProvider.class);
	}

    public boolean cancelExecution() {
        cancelRequested = true;
        return true;
    }

    private boolean isCancelRequested() throws CancelRequestException {
        if (cancelRequested)  {
            throw new CancelRequestException();
        }
        return false;
    }

	protected Map collectReportParameters() {
		Map params = new HashMap();
		Map jobParams = jobDetails.getSource().getParametersMap();
		if (jobParams != null) {
			params.putAll(jobParams);
		}
		putAdditionalParameters(params);
		return params;
	}

    // put ReportJob information into ReportUnitRequest
    protected Map collectReportJobProperties() {
        Map params = new HashMap();
		Map jobParams = jobDetails.getSource().getParametersMap();
		if (jobParams != null) {
			params.putAll(jobParams);
		}
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_JOBID, jobDetails.getId());
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_JOBLABEL, jobDetails.getLabel());
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_REPORTURI, jobDetails.getSource().getReportUnitURI());
        Date scheduledFireTime = jobContext.getScheduledFireTime();
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_FIRETIME, scheduledFireTime);
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_USERNAME, jobDetails.getUsername());
		params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_QUARTZJOB, this);
        return params;
    }

	protected void putAdditionalParameters(Map parametersMap) {
		if (!parametersMap.containsKey(REPORT_PARAMETER_SCHEDULED_TIME)) {
			Date scheduledFireTime = jobContext.getScheduledFireTime();
			parametersMap.put(REPORT_PARAMETER_SCHEDULED_TIME, scheduledFireTime);
		}
		VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
		if (virtualizerFactory != null)
		{
			parametersMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizerFactory.getVirtualizer());
		}
	}

	protected List<Output> createOutputs() throws JobExecutionException {
		Set outputFormats = jobDetails.getOutputFormats();
		List<Output> outputs = new ArrayList<Output>(outputFormats.size());
		for (Iterator it = outputFormats.iterator(); it.hasNext();) {
			Byte format = (Byte) it.next();

			Output output = null;
			try {
				output = getOutput(format, getBaseFileName());
				if (output != null) {
					outputs.add(output);
				}
			} catch (Exception e) {
				String formatKey = getMessage("report.scheduling.output.format." + format, null);
				String formatLabel = getMessage("report.output." + formatKey + ".label", null);
				handleException(getMessage("report.scheduling.error.exporting.report", new Object[]{formatLabel}), e);
			}
		}
		
		return outputs;
	}

	protected String getBaseFileName()
	{
		String baseFilename = jobDetails.getBaseOutputFilename();
		if (jobDetails.getContentRepositoryDestination().isSequentialFilenames()) {
			Date scheduledTime = jobContext.getScheduledFireTime();
			SimpleDateFormat format = getTimestampFormat();
			baseFilename = jobDetails.getBaseOutputFilename() + REPOSITORY_FILENAME_SEQUENCE_SEPARATOR + format.format(scheduledTime);
		} else {
			baseFilename = jobDetails.getBaseOutputFilename();
		}
		return baseFilename;
	}

	protected SimpleDateFormat getTimestampFormat() {
		String pattern = jobDetails.getContentRepositoryDestination().getTimestampPattern();
		if (pattern == null || pattern.length() == 0) {
			pattern = REPOSITORY_FILENAME_TIMESTAMP_SEQUENCE_PATTERN;
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern, getLocale());
		return format;
	}

	protected boolean isEmpty(ReportUnitResult result) {
		if (result == null) {
			return false;
		}
		JasperPrint jasperPrint = result.getJasperPrint();
		List pages = jasperPrint.getPages();
		boolean empty;
		if (pages == null || pages.isEmpty()) {
			empty = true;
		} else if (pages.size() == 1) {
			JRPrintPage page = (JRPrintPage) pages.get(0);
			List elements = page.getElements();
			empty = elements == null || elements.isEmpty();
		} else {
			empty = false;
		}
		return empty;
	}

	protected Output getOutput(Byte format, String baseFilename) throws JobExecutionException
	{
		if(format == null)
		{
			throw new JSException("jsexception.report.unknown.output.format", new Object[] {"null"});
		}
		
		if (format == ReportJob.OUTPUT_FORMAT_DATA_SNAPSHOT)
		{
			hasDataSnapshotOutput = true;
			dataSnapshotOutputName = baseFilename;
			return null;
		}
	
		String fileExtension = (String)getOutputKeyMapping().get(format.toString());
		if(fileExtension == null)
		{
			throw new JSException("jsexception.report.unknown.output.format", new Object[] {new Byte(format)});
		}
		
		return (Output)getOutputFormatMap().get(fileExtension);
	}


	
	protected String getReportUnitURI() {
		return jobDetails.getSource().getReportUnitURI();
	}

	protected JRHyperlinkProducerFactory getHyperlinkProducerFactory() {
		JRHyperlinkProducerFactory engineService = (JRHyperlinkProducerFactory) schedulerContext.get(SCHEDULER_CONTEXT_KEY_HYPERLINK_PRODUCER_FACTORY);
		return engineService;
	}

	protected RepositoryService getRepository() {
		RepositoryService repositoryService = (RepositoryService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_REPOSITORY);
		return repositoryService;
	}

	protected void sendMailNotification(List reportOutputs) throws JobExecutionException {
        ReportJobMailNotification mailNotification = jobDetails.getMailNotification();
		if (mailNotification == null) return;
        getReportExecutionJobMailNotification().sendMailNotification(this, jobDetails, reportOutputs);
    }

	protected String getRepositoryLinkDescription() {
		return reportUnit.getLabel();
	}
	
	protected ContentResourceURIResolver getContentResourceURIResolver() {
		return (ContentResourceURIResolver) schedulerContext.get(
                SCHEDULER_CONTEXT_KEY_CONTENT_RESOURCE_URI_RESOLVER);
	}
	
	protected LockManager getLockManager() {
		return (LockManager) schedulerContext.get(
				SCHEDULER_CONTEXT_KEY_LOCK_MANAGER);
	}
	

	
	protected String getCharacterEncoding() {
		CharacterEncodingProvider encodingProvider = (CharacterEncodingProvider) schedulerContext.get(SCHEDULER_CONTEXT_KEY_ENCODING_PROVIDER);
		return encodingProvider.getCharacterEncoding();
	}

    protected String[] getAlertMailRecipients(ReportJobAlert alert) {
		String adminRoleName = getAdministratorRole();
        Set<String> toAddresses = new HashSet<String>();
        if (alert.getToAddresses() != null && !alert.getToAddresses().isEmpty()) {
            for (String address : alert.getToAddresses()) toAddresses.add(address);
        }
        User user = getSecurityContextProvider().getUserAuthorityService().getUser(executionContext, username);
        switch (alert.getRecipient()) {
            case ADMIN:
                if (isDisableSendingAlertToAdmin()) break;
                List userList = getSecurityContextProvider().getUserAuthorityService().getUsersInRole(executionContext, adminRoleName);
                if (userList != null) for (Object user1 : userList) if (((User) user1).getEmailAddress() != null && !(((User) user1).getEmailAddress()).trim().isEmpty() && fromSameOrganization(user, (User)user1))  toAddresses.add(((User)user1).getEmailAddress());
                break;
            case OWNER:
                if (isDisableSendingAlertToOwner()) break;
                if ((user != null) && (user.getEmailAddress() != null) && !user.getEmailAddress().trim().isEmpty()) toAddresses.add(user.getEmailAddress());
                break;
            case OWNER_AND_ADMIN:
                if (!isDisableSendingAlertToAdmin()) {
                    userList = getSecurityContextProvider().getUserAuthorityService().getUsersInRole(executionContext, adminRoleName);
                    if (userList != null) for (Object user1 : userList) if (((User) user1).getEmailAddress() != null && !(((User) user1).getEmailAddress()).trim().isEmpty() && fromSameOrganization(user, (User)user1))  toAddresses.add(((User)user1).getEmailAddress());
                }
                if (!isDisableSendingAlertToOwner()) {
                    if ((user != null) && (user.getEmailAddress() != null) && !user.getEmailAddress().trim().isEmpty()) toAddresses.add(user.getEmailAddress());
                }
                break;
            case NONE:
        }
		if (toAddresses != null && !toAddresses.isEmpty()) {
			String[] addressArray = new String[toAddresses.size()];
			toAddresses.toArray(addressArray);
            return addressArray;
		}
        return null;
	}

    protected boolean fromSameOrganization(User user1, User user2) {
        return true;
    }

	protected String getFromAddress() {
		String fromAddress = (String) schedulerContext.get(SCHEDULER_CONTEXT_KEY_MAIL_FROM_ADDRESS);
		return fromAddress;
	}

	protected JavaMailSender getMailSender() {
		JavaMailSender mailSender = (JavaMailSender) schedulerContext.get(SCHEDULER_CONTEXT_KEY_MAIL_SENDER);
		return mailSender;
	}
	
	protected Map getExportParametersMap() {
		return (Map) schedulerContext.get(SCHEDULER_CONTEXT_KEY_EXPORT_PARAMETRES_MAP);
	}

    protected String getAdministratorRole() {
		String administratorRole = (String) schedulerContext.get(SCHEDULER_CONTEXT_KEY_ADMINISTRATOR_ROLE);
		return administratorRole;
	}

    protected ReportExecutionJobInit getReportExecutionJobInit() {
		ReportExecutionJobInit initJob = (ReportExecutionJobInit) schedulerContext.get(SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_INIT);
		return initJob;
	}

    protected ReportExecutionJobAlert getReportExecutionJobAlert() {
		ReportExecutionJobAlert alert = (ReportExecutionJobAlert) schedulerContext.get(SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_ALERT);
		return alert;
	}

    protected ReportExecutionJobMailNotification getReportExecutionJobMailNotification() {
		ReportExecutionJobMailNotification mailNotification = (ReportExecutionJobMailNotification) schedulerContext.get(SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_MAIL_NOTIFICATION);
		return mailNotification;
	}

    protected ReportExecutionJobFileSaving getReportExecutionJobFileSaving() {
		ReportExecutionJobFileSaving savingFile = (ReportExecutionJobFileSaving) schedulerContext.get(SCHEDULER_CONTEXT_KEY_REPORT_EXECUTION_JOB_FILE_SAVING);
		return savingFile;
	}

    protected boolean isAutoDeleteBrokenUriReportJob() {
        String value = (String) schedulerContext.get(SCHEDULER_CONTEXT_KEY_AUTO_DELETE_BROKEN_URI_REPORT_JOB);
        if (value == null) return false;
        return value.equalsIgnoreCase("true");
    }

    protected boolean isDisableSendingAlertToAdmin() {
        String value = (String) schedulerContext.get(SCHEDULER_CONTEXT_KEY_DISABLE_SENDING_ALERT_TO_ADMIN);
        if (value == null) return false;
        return value.equalsIgnoreCase("true");
    }

    protected boolean isDisableSendingAlertToOwner() {
        String value = (String) schedulerContext.get(SCHEDULER_CONTEXT_KEY_DISABLE_SENDING_ALERT_TO_OWNER);
        if (value == null) return false;
        return value.equalsIgnoreCase("true");
    }

	protected Map getOutputFormatMap() {
		return (Map) schedulerContext.get(SCHEDULER_CONTEXT_KEY_OUTPUT_FORMAT_MAP);
	}

	protected Map getOutputKeyMapping() {
		return (Map) schedulerContext.get(SCHEDULER_CONTEXT_KEY_OUTPUT_KEY_MAPPING);
	}

	protected DataContainer createDataContainer(Output output) {
		DataContainer dataContainer = createDataContainer();
		if (output.isCompress()) {
			dataContainer = DataContainerStreamUtil.createCompressedContainer(dataContainer);
		}
		return dataContainer;
	}

	protected DataContainer createDataContainer() {
		DataContainerFactory factory = 
			(DataContainerFactory) schedulerContext.get(SCHEDULER_CONTEXT_KEY_DATA_CONTAINER_FACTORY);
		DataContainer dataContainer = factory.createDataContainer();
		// keep for clear()
		dataContainers.put(dataContainer, Boolean.TRUE);
		return dataContainer;
	}

    protected class CancelRequestException extends JSException {

        public CancelRequestException() {
            super("report.scheduling.cancel.requested.by.user");
        }
    }
}
