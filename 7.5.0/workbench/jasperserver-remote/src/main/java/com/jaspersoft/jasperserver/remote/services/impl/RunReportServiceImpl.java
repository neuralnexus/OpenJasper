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
package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportExecutionStatusInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.SchedulerReportExecutionStatusSearchCriteria;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.JSReportExecutionRequestCancelledException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.executions.ExecutionStatus;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsLogicService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException;
import com.jaspersoft.jasperserver.inputcontrols.util.ReportParametersUtils;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.exception.*;
import com.jaspersoft.jasperserver.remote.reports.HtmlExportStrategy;
import com.jaspersoft.jasperserver.remote.services.*;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.GenericElementReportTransformer;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.web.servlets.AsyncJasperPrintAccessor;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;
import net.sf.jasperreports.web.servlets.ReportExecutionStatus;
import net.sf.jasperreports.web.servlets.ReportPageStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEventType.EXPORT;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NOTHING_SUBSTITUTION_VALUE;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NULL_SUBSTITUTION_VALUE;

/**
 * Run Report service
 * Run a report unit using the passing in parameters and options
 *
 * @author ykovalchyk
 * @version $Id$
 */
@SuppressWarnings("deprecation")
@Service("runReportService")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RunReportServiceImpl implements RunReportService, Serializable, DisposableBean {
    /**
    * Thanks, Eclipse!
    */
    private static final long serialVersionUID = 8292143346647445860L;
    private final static Log log = LogFactory.getLog(RunReportServiceImpl.class);
    private final static Pattern FILE_NAME_PATTERN = Pattern.compile(".*/([^/]+)$");
    @Resource
    private AuditHelper auditHelper;
    @Resource(name = "concreteEngineService")
    private EngineService engine;
    @Resource(name = "${bean.engineServiceTarget}")
    private EngineService unsecuredEngine;
    @Resource(name="loggableExecutorService")
    private Executor asyncExecutor;
    @Resource
    private InputControlsLogicService inputControlsLogicService;
    @Resource
    private ReportExecutor reportExecutor;
    @Resource(name = "concreteRepository")
    private RepositoryService repositoryService;
    @Resource
    private ErrorDescriptorBuildingService errorDescriptorBuildingService;
    @Resource
    private Integer waitForFinalJasperPrintMs;
    @Resource
    private Map<String, HtmlExportStrategy> htmlExportStrategies;
    @Resource(name = "defaultHtmlExportStrategy")
    private HtmlExportStrategy defaultHtmlExportStrategy;
    @Resource(name = "concreteVirtualizerFactory")
    private VirtualizerFactory virtualizerFactory;
    @Resource
    private SecureExceptionHandler secureExceptionHandler;

    @Resource
    private RunReportServiceCacheFactoryBean cacheFactoryBean;

    private final ConcurrentHashMap<String,Integer> CACHE_KEYS = new ConcurrentHashMap<String, Integer>();
     

    private Cache getExecutionsCache() {
        return cacheFactoryBean.getObject();
    }

    private ReportExecution getREfromCache(final String requestId) {
        // can't get executions originated from other beans. fix bug JRS-20322
        if(CACHE_KEYS.get(requestId)==null){
            return null;
        }
        Element element = getExecutionsCache().get(requestId);
        if (element != null) {
            return (ReportExecution) (element.getObjectValue());
        } else {
            return null;
        }
    }

    private void putREtoCache(final String requestId, final ReportExecution re) {
        CACHE_KEYS.put(requestId,1);
        getExecutionsCache().put(new Element(requestId, re));
    }

    private void removeREfromCache(String requestId) {
        getExecutionsCache().remove(requestId);
        CACHE_KEYS.remove(requestId);
    }

    public ReportExecution getReportExecution(String requestId) throws ResourceNotFoundException {
        final ReportExecution execution = getREfromCache(requestId);
        if (execution == null) {
            throw new ResourceNotFoundException(requestId);
        }
        actualizeExecutionStatus(execution);
        return execution;
    }

    protected void actualizeExecutionStatus(ReportExecution execution) {
        final ReportExecutionStatus reportStatus = execution != null && execution.getReportUnitResult() != null
                && execution.getReportUnitResult().getJasperPrintAccessor() != null
                        ? execution.getReportUnitResult().getJasperPrintAccessor().getReportStatus() : null;
        if (reportStatus != null) {
            ExecutionStatus executionStatus = ExecutionStatus.queued;
            final ReportExecutionStatus.Status status = reportStatus.getStatus();
            switch (status) {
                case CANCELED:
                    executionStatus = ExecutionStatus.cancelled;
                    break;
                case ERROR: {
                    executionStatus = ExecutionStatus.failed;
                    execution.setErrorDescriptor(secureExceptionHandler.handleException(reportStatus.getError()));
                }
                break;
                case FINISHED: {
                    executionStatus = ExecutionStatus.ready;
                    execution.setTotalPages(reportStatus.getTotalPageCount());
                }
                break;
                case RUNNING: {
                    executionStatus = ExecutionStatus.execution;
                    execution.setCurrentPage(reportStatus.getCurrentPageCount());
                }
                break;
            }
            execution.setStatus(executionStatus);
        }
    }

    public void startReportExecution(ReportExecution reportExecution) {
        startReportExecution(reportExecution, null);
    }

    public void startReportExecution(ReportExecution reportExecution,
            ReportExecutionOptions reportExecutionOptions) {
        final ReportExecutionOptions options = reportExecutionOptions != null ? reportExecutionOptions
                : reportExecution.getOptions();
        ReportUnitResult reportUnitResult = (ReportUnitResult)reportExecution.getReportUnitResult();
        try {
            if (reportUnitResult!=null) {
                virtualizerFactory.disposeReport(reportUnitResult);
            }
        } catch (RuntimeException ex) {
            log.warn("Report execution cleanup failed: ", ex);
        }
        if (ExecutionStatus.execution == reportExecution.getStatus()) {
            cancelReportExecution(reportExecution.getRequestId());
            reportExecution.setStatus(ExecutionStatus.cancelled);
            reportExecution = createFreshReportExecutionCopy(reportExecution, options);
        }
        // if we are restarting reportExecution, then we need to remove link to relatedExecution. It will be ran again
        // if alternative pagination mode will be needed
        reportExecution.setRelatedExecution(null);
        reportExecution.setReportUnitResult(null);
        reportExecution.setErrorDescriptor(null);
        reportExecution.setStatus(ExecutionStatus.execution);
        final Locale locale = LocaleContextHolder.getLocale();
        final TimeZone timeZone = TimeZoneContextHolder.getTimeZone();
        final SecurityContext context = SecurityContextHolder.getContext();
        final ReportExecution finalReportExecution = reportExecution;
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LocaleContextHolder.setLocale(locale);
                TimeZoneContextHolder.setTimeZone(timeZone);
                SecurityContextHolder.setContext(context);
                ReportUnitResult reportUnitResult = null;
                ErrorDescriptor errorDescriptor = null;

                try {
                    final String reportUnitUri = finalReportExecution.getReportURI();
                    Map<String, Object> convertedParameters = finalReportExecution.getConvertedParameters();
                    if (convertedParameters == null) {
                        synchronized (finalReportExecution) {
                            convertedParameters = finalReportExecution.getConvertedParameters();
                            if (convertedParameters == null) {
                                // no cached converted parameters. Let's make them by running input controls logic
                                // on raw parameters
                                final Map<String, String[]> rawParameters = finalReportExecution.getRawParameters();
                                // convert parameters from raw strings to objects
                                try {
                                    convertedParameters = executeInputControlsCascadeWithRawInput(reportUnitUri, rawParameters);
                                } catch (CascadeResourceNotFoundException e) {
                                    throw new ResourceNotFoundException("URI:" + e.getResourceUri() + " Type:" + e.getResourceType());
                                } catch (InputControlsValidationException e) {
                                    // raw type is used in core class. Cast is safe.
                                    @SuppressWarnings("unchecked")
                                    List<ValidationError> errors = e.getErrors().getErrors();
                                    List<String> errorParameters = new ArrayList<String>();
                                    for (ValidationError error : errors) {
                                        errorParameters.add(error.toString());
                                    }
                                    throw new ErrorDescriptorException(new ErrorDescriptor().setErrorCode("input.controls.validation.error")
                                            .setMessage("Input controls validation failure").addParameters(errorParameters.toArray()));
                                }
                                // forward parameters without input control defined
                                for (String currentKey : rawParameters.keySet()) {
                                    String[] currentValue = rawParameters.get(currentKey);
                                    if (currentValue != null && !convertedParameters.containsKey(currentKey)) {
                                        Object valueToForward;
                                        if (currentValue.length == 1) {
                                            // forward as single value
                                            valueToForward = currentValue[0];
                                        } else {
                                            // forward as collection
                                            Collection<String> collection = new ArrayList<String>();
                                            collection.addAll(Arrays.asList(currentValue));
                                            valueToForward = collection;
                                        }
                                        convertedParameters.put(currentKey, valueToForward);
                                    }
                                }
                            }
                            // cache converted parameter for feather runes.
                            finalReportExecution.setConvertedParameters(convertedParameters);
                        }
                    }
                    finalReportExecution.setStatus(ExecutionStatus.execution);
                    reportUnitResult = reportExecutor.runReport(reportUnitUri, convertedParameters, options);
                    //If reportExecutionOptions.ignorePagination wasn't set, then only here we can know what was a value
                    // for ignorePagination in current report execution. Let's cache it to use for further executions if
                    // not specified explicitly.
                    options.setDefaultIgnorePagination(!reportUnitResult.isPaginated());

                } catch (ErrorDescriptorException e) {
                    errorDescriptor = e.getErrorDescriptor();
                } catch (JSReportExecutionRequestCancelledException e){
                    // do nothing in this thread. Correct status is set by the thread, cancelled the execution.
                } catch (Exception e) {
                    // if report execution is interrupted, then it's cancelled. Do nothing in this case.
                    if(!(e.getCause() instanceof InterruptedException)){
                        // if not the case, then let's build error descriptor
                        errorDescriptor = errorDescriptorBuildingService.buildErrorDescriptor(e);
                    }
                }
                if (errorDescriptor != null) {
                    finalReportExecution.setErrorDescriptor(errorDescriptor);
                }
                if (reportUnitResult != null) {
                    final String requestId = options.getRequestId();
                    if (!requestId.equals(reportUnitResult.getRequestId())) {
                        throw new IllegalStateException("Report unit request and report unit result should have the same ID. RequestId: "
                                + requestId + " ResultId: " + reportUnitResult.getRequestId());
                    }
                    finalReportExecution.setReportUnitResult(reportUnitResult);
                    // wait for report execution to complete for execution status actualization
                    reportUnitResult.getJasperPrint();
                    actualizeExecutionStatus(finalReportExecution);
                }
            }
        });
    }

    @Override
    public ReportExecution getReportExecutionFromRawParameters(final String reportUnitURI, final Map<String, String[]> rawParameters,
            ReportExecutionOptions inputOptions, ExportExecutionOptions exportOptions) throws ErrorDescriptorException, JSValidationException {
        // basic synchronous validation:
        // check report URI for null
        if (reportUnitURI == null) {
            throw new MandatoryParameterNotFoundException("reportUnitUri");
        }
        // check report resource for existence
        final com.jaspersoft.jasperserver.api.metadata.common.domain.Resource resource = repositoryService
                .getResource(ExecutionContextImpl.getRuntimeExecutionContext(), reportUnitURI);
        if (resource == null) {
            throw new ResourceNotFoundException(reportUnitURI);
        } else if(!reportExecutor.isRunnableResource(resource)){
            throw new IllegalParameterValueException("reportURI", reportUnitURI);
        }

        final ReportExecutionOptions options = inputOptions != null ? inputOptions : new ReportExecutionOptions();

        String exportFormat = (exportOptions != null && exportOptions.getOutputFormat() != null
        		&& !exportOptions.getOutputFormat().isEmpty()) ? exportOptions.getOutputFormat().toUpperCase() : null;
        if (exportFormat != null) {
            PaginationParameters exportPagination = getExportPaginationParameters(reportUnitURI, null, exportFormat);
            options.setPaginationDefaults(exportPagination);
        }

        final ReportExecution execution = createReportExecution(reportUnitURI, rawParameters, options);
        final String requestId = execution.getRequestId();

        startReportExecution(execution);

        if(!options.isAsync()){
            // wait till report execution is complete
            try{
                execution.getFinalReportUnitResult().getJasperPrintAccessor().getFinalJasperPrint();
            } catch (ErrorDescriptorException e){
                throw e;
            } catch (Exception e){
                // if report fails in non async mode, then send error immediately
                throw new IllegalParameterValueException(secureExceptionHandler.handleException(e));
            }
        }
        if (exportFormat != null) {
            final ExportExecution exportExecution = executeExport(exportOptions, execution);
            if (!options.isAsync()) {
                // wait till export is complete
                try {
                    exportExecution.getFinalOutputResource();
                } catch (ExportExecutionRejectedException e) {
                    // if report fails in non async mode, then send error immediately
                    throw new IllegalParameterValueException(e.getErrorDescriptor());
                }
            }
        }
        return getReportExecution(requestId);
    }

    private ReportExecution createReportExecution(String reportUnitURI, Map<String, String[]> rawParameters, ReportExecutionOptions options) {
        final ReportExecution execution = new ReportExecution();
        final String requestId;
        if (options.getRequestId() == null) {
            requestId = UUID.randomUUID().toString();
            options.setRequestId(requestId);
        } else {
            requestId = options.getRequestId();
        }
        execution.setOptions(options);
        execution.setRequestId(requestId);
        execution.setReportURI(reportUnitURI);
        execution.setRawParameters(rawParameters);

        putREtoCache(requestId, execution);

        return execution;
    }

    private ReportExecution createFreshReportExecutionCopy(ReportExecution copy, ReportExecutionOptions options) {
        String requestId = copy.getRequestId();
        Assert.notNull(requestId);

        options.setRequestId(requestId);

        return createReportExecution(copy.getReportURI(), copy.getRawParameters(), options);
    }

    public ExportExecution executeExport(final String executionId, final ExportExecutionOptions exportOptions)
            throws ErrorDescriptorException {
        final ReportExecution execution = getREfromCache(executionId);
        if (execution == null)
            throw new ResourceNotFoundException(executionId);
        return executeExport(exportOptions, execution);
    }

    protected ExportExecution executeExport(ExportExecutionOptions exportOptions, final ReportExecution reportExecution) throws ErrorDescriptorException {
        final ExportExecution exportExecution = new ExportExecution();
        exportExecution.setSecureExceptionHandler(secureExceptionHandler);
        exportExecution.setStatus(ExecutionStatus.queued);
        exportExecution.setOptions(exportOptions);
        reportExecution.getExports().put(exportExecution);
        startExport(reportExecution, exportExecution);
        return exportExecution;
    }

    protected void startExport(final ReportExecution reportExecution, final ExportExecution exportExecution) {
        final SecurityContext context = SecurityContextHolder.getContext();
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SecurityContextHolder.setContext(context);
                try {
                    // wait for report unit result before start export.
                    // If report execution is failed or cancelled, then no need to start export.
                    final ReportUnitResult finalReportUnitResult = reportExecution.getFinalReportUnitResult();
                    if (finalReportUnitResult == null) {
                        exportExecution.setErrorDescriptor(new ErrorDescriptor()
                                .setErrorCode("export.failed")
                                .setMessage("Export can't be executed. Report execution status is '"
                                        + reportExecution.getStatus().toString() + "'."
                                        + (reportExecution.getErrorDescriptor() != null
                                        ? " " + reportExecution.getErrorDescriptor().toString() : "")));
                    } else {
                        executeExport(exportExecution, reportExecution);
                    }

                } catch (ErrorDescriptorException e) {
                    exportExecution.setErrorDescriptor(secureExceptionHandler.handleException(e, e.getErrorDescriptor()));
                } catch (Exception e) {
                    exportExecution.setErrorDescriptor(secureExceptionHandler.handleException(e));
                }
            }
        });
    }

    /**
     * If client wants to get export with another pagination mode, then we have to run report again with
     * alternate ignorePagination flag value. This method is intended to do it.
     * @param originalReportExecution - the original report execution to use as a template for related execution.
     * @param pagination - ignore pagination flag for report execution
     * @return related report execution instance
     */
    protected ReportExecution runRelatedExecution(ReportExecution originalReportExecution, PaginationParameters pagination){
        final ReportExecution relatedReportExecution = new ReportExecution();
        final String requestId = UUID.randomUUID().toString();
        relatedReportExecution.setRequestId(requestId);
        relatedReportExecution.setReportURI(originalReportExecution.getReportURI());
        relatedReportExecution.setRawParameters(originalReportExecution.getRawParameters());
        relatedReportExecution.setConvertedParameters(originalReportExecution.getConvertedParameters());
        relatedReportExecution.setOptions(new ReportExecutionOptions(originalReportExecution.getOptions()).setPaginationParameters(pagination));
        putREtoCache(requestId, relatedReportExecution);
        startReportExecution(relatedReportExecution);
        originalReportExecution.setRelatedExecution(relatedReportExecution);
        return relatedReportExecution;
    }

    protected void executeExport(ExportExecution exportExecution, ReportExecution reportExecution) {
        final ReportUnitResult reportUnitResult = reportExecution.getFinalReportUnitResult();
        JasperPrintAccessor jasperPrintAccessor = reportUnitResult.getJasperPrintAccessor();

        String rawOutputFormat = exportExecution.getOptions().getOutputFormat();
        String outputFormat = rawOutputFormat != null ? rawOutputFormat.toUpperCase() : Argument.RUN_OUTPUT_FORMAT_PDF;
        PaginationParameters exportPagination = getExportPaginationParameters(reportUnitResult.getReportUnitURI(), jasperPrintAccessor,
        		outputFormat);

        final Boolean ignorePagination = exportExecution.getOptions().getIgnorePagination();
        if (ignorePagination != null) {
        	exportPagination = new PaginationParameters(exportPagination);
        	exportPagination.setPaginated(!ignorePagination);
        }

        if(!reportUnitResult.matchesPagination(exportPagination)){
            // oops, we have to provide export with another pagination mode.
            // Let's check if we already have jasperPrint with this mode generated:
            ReportExecution relatedExecution = reportExecution.getRelatedExecution();
            if(relatedExecution == null
            		|| !relatedExecution.getFinalReportUnitResult().matchesPagination(exportPagination)){
                synchronized (reportExecution){
                    relatedExecution = reportExecution.getRelatedExecution();
                    if(relatedExecution == null
                    		|| !relatedExecution.getFinalReportUnitResult().matchesPagination(exportPagination)){
                        // No. We don't have generated jasperPrint for this pagination mode.
                        // Let's run another report execution to generate it.
                    	//TODO use a data snapshot to refill the report with a different pagination
                        relatedExecution = runRelatedExecution(reportExecution, exportPagination);
                        // link it to current report execution to track it's status and to be able to cancel it.
                        reportExecution.setRelatedExecution(relatedExecution);
                    	//TODO keep a map of ReportExecution per PaginationParameters instead of replacing the single related execution
                        //not changing now (6.4.0 hotfix) because it would change the ReportExecution JSON/XML serialization
                    }
                }
            }
            // let's take a jasperPrintAccessor from related execution with appropriate ignorePagination flag value
            jasperPrintAccessor = relatedExecution.getFinalReportUnitResult().getJasperPrintAccessor();
        }
        try {
            ExportExecutionOptions exportOptions = exportExecution.getOptions();
            Integer exportEndPage = null;
            if (exportOptions.getPages() != null) {
                exportEndPage = exportOptions.getPages().getPage() != null ? exportOptions.getPages().getPage() : exportOptions.getPages().getEndPage();
            }
            JasperPrint jasperPrint;
            boolean isOutputFinal = true;
            if (exportEndPage != null) {
                // convert page number to pageIndex (0-based)
                exportEndPage = exportEndPage - 1;
                if(waitForFinalJasperPrintMs > 0 && exportEndPage == 0
                        && jasperPrintAccessor instanceof AsyncJasperPrintAccessor){
                    ((AsyncJasperPrintAccessor) jasperPrintAccessor).waitForFinalJasperPrint(waitForFinalJasperPrintMs);
                }
                // in case if jasperPrintAccessor is AsyncJasperPrintAccessor and pageStatus() method is invoked,
                // then current thread waits till requested page is generated.
                final ReportPageStatus reportPageStatus = jasperPrintAccessor.pageStatus(exportEndPage, null);
                isOutputFinal = reportPageStatus.isPageFinal();
                jasperPrint = jasperPrintAccessor.getJasperPrint();
            } else {
                jasperPrint = jasperPrintAccessor.getFinalJasperPrint();
                final String anchor = exportOptions.getAnchor();
                if(anchor != null && !anchor.isEmpty()){
                    final JRPrintAnchorIndex jrPrintAnchorIndex = jasperPrint.getAnchorIndexes().get(anchor);
                    if(jrPrintAnchorIndex != null){
                        exportOptions.setPages(new ReportOutputPages().setPage(jrPrintAnchorIndex.getPageIndex() + 1));
                    } else {
                        throw new IllegalParameterValueException("anchor", anchor);
                    }
                }
            }
            final ReportExecutionStatus.Status status = jasperPrintAccessor.getReportStatus().getStatus();
            switch (status){
                case CANCELED: exportExecution.setStatus(ExecutionStatus.cancelled);
                    break;
                case ERROR: exportExecution.setErrorDescriptor(errorDescriptorBuildingService
                        .buildErrorDescriptor(jasperPrintAccessor.getReportStatus().getError()));
                    break;
                default:{
                    exportExecution.setStatus(ExecutionStatus.execution);
                    if (Argument.RUN_OUTPUT_FORMAT_HTML.equalsIgnoreCase(outputFormat)) {
                        HtmlExportStrategy htmlExportStrategy = htmlExportStrategies.get(exportExecution.getOptions().getMarkupType()) != null
                                ? htmlExportStrategies.get(exportExecution.getOptions().getMarkupType()) : defaultHtmlExportStrategy;
                        htmlExportStrategy.export(reportExecution, exportExecution, jasperPrint);
                    } else {
                        generateReportOutput(reportExecution, jasperPrint, outputFormat,
                                exportExecution, exportExecution.getOptions().getPages());
                    }
                    exportExecution.getOutputResource().setOutputFinal(isOutputFinal);
                    exportExecution.setStatus(ExecutionStatus.ready);
                }
            }
        } catch (ErrorDescriptorException ex) {
            exportExecution.setErrorDescriptor(ex.getErrorDescriptor());
        } catch (Exception ex) {
            log.debug("Unexpected error occurs during export", ex);
            exportExecution.setErrorDescriptor(secureExceptionHandler.handleException(ex));
        }
    }

    protected PaginationParameters getExportPaginationParameters(String reportURI, JasperPrintAccessor jasperPrintAccessor,
    		String outputFormat) {
    	PaginationParameters pagination;
    	if (Argument.RUN_OUTPUT_FORMAT_HTML.equalsIgnoreCase(outputFormat)
    			|| Argument.RUN_OUTPUT_FORMAT_JRPRINT.equalsIgnoreCase(outputFormat)) {
    		//default params
    		pagination = new PaginationParameters();
    	} else {
    		pagination = reportExecutor.getExportPaginationParameters(reportURI,
    				jasperPrintAccessor == null ? null : jasperPrintAccessor.getJasperPrint(), outputFormat);
    	}
		return pagination;
	}

    public ReportOutputResource getOutputResource(String executionId, ExportExecutionOptions exportExecutionOptions) throws ErrorDescriptorException {
        final ReportExecution execution = getREfromCache(executionId);
        if (execution == null) throw new ResourceNotFoundException(executionId);
        return executeExport(exportExecutionOptions, execution).getFinalOutputResource();
    }

    public ReportOutputResource getOutputResource(String executionId, String exportId) throws ErrorDescriptorException {
        final ReportExecution reportExecution = getReportExecution(executionId);
        final ExportExecution exportExecution = getExportExecution(executionId, exportId);
        if (exportExecution.getStatus() == ExecutionStatus.cancelled
                && !(reportExecution.getStatus() == ExecutionStatus.cancelled)) {
            // cancelled status means export reset is done. Need to rerun
            exportExecution.setStatus(ExecutionStatus.queued);
            startExport(reportExecution, exportExecution);
        }
        return exportExecution.getFinalOutputResource();
    }

    public ExportExecution getExportExecution(String executionId, String exportId) throws ResourceNotFoundException {
        final ReportExecution execution = getREfromCache(executionId);
        if (execution == null) throw new ResourceNotFoundException(executionId);
        final ExportExecution exportExecution = execution.getExports().get(exportId);
        if (exportExecution == null) throw new ResourceNotFoundException(exportId);
        return exportExecution;
    }

    public ReportOutputResource getAttachment(String executionId, String exportId, String attachmentName) throws ResourceNotFoundException {
        ReportOutputResource outputResource = getExportExecution(executionId, exportId).getAttachments().get(attachmentName);
        if (outputResource == null) throw new ResourceNotFoundException(attachmentName);
        return outputResource;
    }

    @Override
    public ReportOutputResource getReportOutputFromRawParameters(String reportUnitURI, Map<String, String[]> rawParameters,
            ReportExecutionOptions executionOptions, ExportExecutionOptions exportOptions) throws ErrorDescriptorException {
        final ReportExecution execution = getReportExecutionFromRawParameters(reportUnitURI, rawParameters, executionOptions, exportOptions);
        return getOutputResource(execution.getRequestId(), exportOptions);
    }

    protected Map<String, Object> executeInputControlsCascadeWithRawInput(String reportUnitUri, Map<String, String[]> rawInputParameters) throws CascadeResourceNotFoundException, InputControlsValidationException {
        final List<ReportInputControl> inputControlsForReport = inputControlsLogicService.getInputControlsWithValues(reportUnitUri, null, rawInputParameters);
        final Map<String, String[]> inputControlFormattedValues = ReportParametersUtils.getValueMapFromInputControls(inputControlsForReport);

        // Fix for https://jira.tibco.com/browse/JS-30276
        // The behaviour, described in the bug caused by change of actual values, sent in rawInputParameters to some
        // other, resolved by input control logic in inputControlFormattedValues. So, it a user sends invalid values,
        // they just dropped and replaced with values, that will definitely allow report to run.
        // This behaviour might be useful for cascading IC, in particular, when a user sends values for controls in cascade, and sent child's value
        // does not present in the subset of possible child values filtered by parent's value.
        // But this works in this way for all controls, and according to the requirement in the bug, this case should throw an error.
        // This particular check determines case, when sent value for a non-cascading control was silently alternated and throw an error.
        // For cascading controls the behaviour is not changed.
        verifyCorrectParameterValuesForNonCascadingControls(inputControlsForReport, rawInputParameters, inputControlFormattedValues);

        return inputControlsLogicService.getTypedParameters(reportUnitUri, inputControlFormattedValues);
    }

    protected void verifyCorrectParameterValuesForNonCascadingControls(List<ReportInputControl> inputControlsForReport, Map<String, String[]> rawInputParameters, Map<String, String[]> inputControlFormattedValues) throws InputControlsValidationException {
        for (String key : rawInputParameters.keySet()){
            if (inputControlFormattedValues.get(key) != null){
                String[] oldValues, newValues;

                newValues = inputControlFormattedValues.get(key);
                oldValues = rawInputParameters.get(key);

                Arrays.sort(newValues);
                Arrays.sort(oldValues);

                if (newValues.length == 0 && oldValues.length == 1 && NOTHING_SUBSTITUTION_VALUE.equals(oldValues[0])) {
                    // correct case, do nothing
                } else if (newValues.length == 1 && oldValues.length == 1 && NOTHING_SUBSTITUTION_VALUE.equals(newValues[0]) && NULL_SUBSTITUTION_VALUE.equals(oldValues[0])) {
                    // correct case, do nothing
                } else if (newValues.length == oldValues.length){
                    for (int i = 0; i<oldValues.length; i++){
                        if (!((newValues[i] != null && newValues[i].equals(oldValues[i])) || (newValues[i] == null && oldValues[i] == null))){
                            verifyCascade(inputControlsForReport, rawInputParameters.keySet(), key, oldValues[i]);
                        }
                    }
                } else {
                    verifyCascade(inputControlsForReport, rawInputParameters.keySet(), key, null);
                }
            } else {
                verifyCascade(inputControlsForReport, rawInputParameters.keySet(), key, null);
            }
        }
    }

    private void verifyCascade(List<ReportInputControl> inputControls, Set<String> specifiedValues,  String offendedControl, String offendedValue) throws InputControlsValidationException{
        if (!cascadingControlChangeCheck(inputControls, specifiedValues, offendedControl)) {
            ValidationErrors errors = new ValidationErrorsImpl();
            errors.add(new ValidationErrorImpl(null, null, "Non acceptable value" + (offendedValue == null ? "." : ": " + offendedValue), offendedControl));
            throw new InputControlsValidationException(errors);
        }
    }

    private boolean cascadingControlChangeCheck(List<ReportInputControl> inputControls, Set<String> specifiedValues, String controlId){
        ReportInputControl control = null;
        boolean result = false;

        for (ReportInputControl it : inputControls){
            if (it.getId().equals(controlId)){
                control = it;
            }
        }

        if (control == null) {
            // do not do anything, keep existing behaviour
            result = true;
        } else {
            if (control.getMasterDependencies() != null) {
                for (String masterId : control.getMasterDependencies()) {
                    if (specifiedValues.contains(masterId)) {
                        result = true;
                    } else {
                        result |= cascadingControlChangeCheck(inputControls, specifiedValues, masterId);
                    }
                }
            }
        }

        return result;
    }

    /**
     * @param reportExecution - the report execution instance. Holds all execution context related things.
     * @param jasperPrint     - filled with data jasper print object
     * @param outputFormat - output format in raw format
     * @param exportExecution - export execution model
     * @param pages           - what pages should be exported
     * @throws ErrorDescriptorException
     */
    protected void generateReportOutput(ReportExecution reportExecution, JasperPrint jasperPrint,
            String outputFormat, ExportExecution exportExecution, ReportOutputPages pages) throws ErrorDescriptorException {
        try {
            // Export...
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ReportExecutionOptions reportExecutionOptions = reportExecution.getOptions();
            if (outputFormat.equals(Argument.RUN_OUTPUT_FORMAT_JRPRINT)) {
                if (log.isDebugEnabled())
                    log.debug("Returning JasperPrint");
                if (reportExecutionOptions.getTransformerKey() != null) {
                    if (log.isDebugEnabled())
                        log.debug("Transforming JasperPrint generic element for key " + reportExecutionOptions.getTransformerKey());
                    GenericElementReportTransformer.transformGenericElements(reportExecutor.getJasperReportsContext(reportExecutionOptions.isInteractive()), jasperPrint, reportExecutionOptions.getTransformerKey());
                }
                JRSaver.saveObject(jasperPrint, bos);
                exportExecution.setOutputResource(new ReportOutputResource().setContentType("application/octet-stream").setData(bos.toByteArray()));
            } else {
                HashMap<String, Object> exportParameters = new HashMap<String, Object>(reportExecution.getRawParameters());
                if (pages != null) exportParameters.put(Argument.RUN_OUTPUT_PAGES, pages);
                Map<JRExporterParameter, Object> exporterParams;
                final String reportURI = reportExecution.getReportURI();
                auditHelper.createAuditEvent(EXPORT.toString());
                auditHelper.addPropertyToAuditEvent(EXPORT.toString(), "uris", reportURI);
                try {
                    exporterParams = reportExecutor.exportReport(reportURI, jasperPrint, outputFormat, bos, exportParameters);
                    if (log.isDebugEnabled())
                        log.debug("Exporter params: " + Arrays.asList(exporterParams.keySet().toArray()));
                } catch (ErrorDescriptorException e) {
                    auditHelper.addExceptionToAllAuditEvents(e);
                    throw e;
                } catch (Exception e) {
                    log.debug("Error exporting report", e);
                    auditHelper.addExceptionToAllAuditEvents(e);
                    throw new ErrorDescriptorException(
                            new ErrorDescriptor()
                                    .setErrorCode("webservices.error.errorExportingReportUnit").setParameters(e.getMessage()), e
                    );
                } finally {
                    try {
                        bos.close();
                    } catch (IOException ex) {
                        log.error("caught exception: " + ex.getMessage(), ex);
                    }
                }
                final Matcher matcher = FILE_NAME_PATTERN.matcher(reportURI);
                exportExecution.setOutputResource(new ReportOutputResource()
                        .setContentType(reportExecutor.getContentType(outputFormat))
                        .setData(bos.toByteArray())
                        .setFileName((matcher.find() ? matcher.group(1) : "report") + "." + outputFormat.toLowerCase())
                        .setPages(pages != null ? pages.toString() : null));
            }
        } catch (ErrorDescriptorException e) {
            throw e;
        } catch (ServiceException e) {
            log.error("caught exception: " + e.getMessage(), e);
        } catch (Throwable e) {
            log.error("caught Throwable exception: " + e.getMessage(), e);
        }
    }

    /**
     * @param searchCriteria - search criteria
     * @return set of currently running report's information
     */
    public Set<ReportExecutionStatusInformation> getCurrentlyRunningReports(SchedulerReportExecutionStatusSearchCriteria searchCriteria) {
        Set<ReportExecutionStatusInformation> result = null;
        List<ReportExecutionStatusInformation> reportExecutionStatusList = searchCriteria != null ?
                engine.getSchedulerReportExecutionStatusList(searchCriteria) : engine.getReportExecutionStatusList();
        if (reportExecutionStatusList != null && !reportExecutionStatusList.isEmpty()) {
            result = new HashSet<ReportExecutionStatusInformation>();
            result.addAll(reportExecutionStatusList);
        }
        return result;
    }

    public Boolean cancelReportExecution(String requestId) throws ErrorDescriptorException {
        return cancelReportExecution(requestId, engine);
    }

    protected Boolean cancelReportExecution(String requestId, EngineService effectiveEngine) throws ErrorDescriptorException {
        final boolean cancelled = effectiveEngine.cancelExecution(requestId);
        final ReportExecution reportExecution = getREfromCache(requestId);
        if(cancelled && reportExecution != null){
            // update report execution status
            reportExecution.setStatus(ExecutionStatus.cancelled);
        }
        return cancelled;
    }

    public Boolean deleteReportExecution(String requestId) throws ErrorDescriptorException {
        try {
            ReportExecution execution = getREfromCache(requestId);
            if (execution != null) {
                cancelReportExecution(requestId);
                if (execution.getStatus() == ExecutionStatus.ready) {
                    virtualizerFactory.disposeReport(execution.getFinalReportUnitResult());
                } else if(execution.getStatus() == ExecutionStatus.cancelled) {
                    if(execution.getReportUnitResult() != null) {
                        virtualizerFactory.disposeReport(execution.getReportUnitResult());
                    }
                }
                removeREfromCache(requestId);
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e1) {
            throw new ErrorDescriptorException(
                    new ErrorDescriptor().setErrorCode("Unexpected error while removing ReportExecution")
                            .setParameters(e1.getMessage()), e1);
        }
    }


    @Override
    public void destroy() throws Exception {
        for (Object requestId : getExecutionsCache().getKeys()) {
            if (!CACHE_KEYS.containsKey(requestId)) {
                continue;
            }

            ReportExecution execution = getREfromCache((String) requestId);
            try {
                cancelReportExecution((String) requestId, unsecuredEngine);
                if (execution.getStatus() == ExecutionStatus.ready) {
                    virtualizerFactory.disposeReport(execution.getFinalReportUnitResult());
                }
            } catch (RuntimeException ex) {
                log.warn("Report execution cleanup failed: ", ex);
            }
        }
        getExecutionsCache().removeAll(CACHE_KEYS.keySet());
        CACHE_KEYS.clear();
        getExecutionsCache().evictExpiredElements();
    }
}
