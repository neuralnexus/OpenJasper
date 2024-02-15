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

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportExecutionStatusInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.SchedulerReportExecutionStatusSearchCriteria;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.ServicesUtils;
import com.jaspersoft.jasperserver.remote.exception.ErrorDescriptorBuildingService;
import com.jaspersoft.jasperserver.remote.exception.ExportExecutionRejectedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.exporters.HtmlExporter;
import com.jaspersoft.jasperserver.remote.services.ExecutionStatus;
import com.jaspersoft.jasperserver.remote.services.ExportExecution;
import com.jaspersoft.jasperserver.remote.services.ExportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportExecution;
import com.jaspersoft.jasperserver.remote.services.ReportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportExecutor;
import com.jaspersoft.jasperserver.remote.services.ReportOutputPages;
import com.jaspersoft.jasperserver.remote.services.ReportOutputResource;
import com.jaspersoft.jasperserver.remote.services.RunReportService;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import com.jaspersoft.jasperserver.war.action.ReportParametersUtils;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlsLogicService;
import com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.export.GenericElementReportTransformer;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.web.servlets.AsyncJasperPrintAccessor;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;
import net.sf.jasperreports.web.servlets.ReportExecutionStatus;
import net.sf.jasperreports.web.servlets.ReportPageStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run Report service
 * Run a report unit using the passing in parameters and options
 *
 * @author ykovalchyk
 * @version $Id: RunReportServiceImpl.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
@Service("runReportService")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RunReportServiceImpl implements RunReportService {
    private final static Log log = LogFactory.getLog(RunReportServiceImpl.class);
    private final static Pattern FILE_NAME_PATTERN = Pattern.compile(".*/([^/]+)$");
    @Resource
    private AuditHelper auditHelper;
    @Resource(name = "concreteEngineService")
    private EngineService engine;
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

    private final Map<String, ReportExecution> executions = new ConcurrentHashMap<String, ReportExecution>();

    public ReportExecution getReportExecution(String requestId) throws ResourceNotFoundException {
        final ReportExecution execution = executions.get(requestId);
        if (execution == null) {
            throw new ResourceNotFoundException(requestId);
        }
        actualizeExecutionStatus(execution);
        return execution;
    }

    protected void actualizeExecutionStatus(ReportExecution execution) {
        final ReportExecutionStatus reportStatus = execution != null && execution.getReportUnitResult() != null && execution.getReportUnitResult().getJasperPrintAccessor() != null
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
                    execution.setErrorDescriptor(new ErrorDescriptor(reportStatus.getError()));
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

    public void startReportExecution(final ReportExecution reportExecution,
            ReportExecutionOptions reportExecutionOptions) {
        final ReportExecutionOptions options = reportExecutionOptions != null
                ? reportExecutionOptions : reportExecution.getOptions();
        reportExecution.setStatus(ExecutionStatus.queued);
        reportExecution.setReportUnitResult(null);
        final Locale locale = LocaleContextHolder.getLocale();
        final TimeZone timeZone = TimeZoneContextHolder.getTimeZone();
        final SecurityContext context = SecurityContextHolder.getContext();
        // reset exports if any
        for (ExportExecution exportExecution : reportExecution.getExports().values()) exportExecution.reset();
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LocaleContextHolder.setLocale(locale);
                TimeZoneContextHolder.setTimeZone(timeZone);
                SecurityContextHolder.setContext(context);
                ReportUnitResult reportUnitResult = null;
                ErrorDescriptor errorDescriptor = null;
                try {
                    final String reportUnitUri = reportExecution.getReportURI();
                    final Map<String, String[]> rawParameters = reportExecution.getRawParameters();
                    // convert parameters from raw strings to objects
                    Map<String, Object> convertedParameters = null;
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
                        throw new RemoteException(new ErrorDescriptor.Builder().setErrorCode("input.controls.validation.error")
                                .setMessage("Input controls validation failure").setParameters(errorParameters.toArray())
                                .getErrorDescriptor());
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
                    reportExecution.setStatus(ExecutionStatus.execution);

                    reportUnitResult = reportExecutor.runReport(reportUnitUri, convertedParameters, options);
                } catch (RemoteException e) {
                    errorDescriptor = e.getErrorDescriptor();
                } catch (Exception e) {
                    errorDescriptor = errorDescriptorBuildingService.buildErrorDescriptor(e);
                }
                if (errorDescriptor != null) {
                    reportExecution.setErrorDescriptor(errorDescriptor);
                }
                if (reportUnitResult != null) {
                    final String requestId = options.getRequestId();
                    if (!requestId.equals(reportUnitResult.getRequestId())) {
                        throw new IllegalStateException("Report unit request and report unit result should have the same ID. RequestId: "
                                + requestId + " ResultId: " + reportUnitResult.getRequestId());
                    }
                    reportExecution.setReportUnitResult(reportUnitResult);
                    // wait for report execution to complete for execution status actualization
                    reportUnitResult.getJasperPrint();
                    actualizeExecutionStatus(reportExecution);
                }
            }
        });
    }

    @Override
    public ReportExecution getReportExecutionFromRawParameters(final String reportUnitURI, final Map<String, String[]> rawParameters,
            ReportExecutionOptions inputOptions, ExportExecutionOptions exportOptions) throws RemoteException, JSValidationException {
        final ReportExecutionOptions options = inputOptions != null ? inputOptions : new ReportExecutionOptions();
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
        // basic synchronous validation:
        // check report URI for null
        if (reportUnitURI == null) {
            throw new MandatoryParameterNotFoundException("reportUnitUri");
        }
        // check report resource for existence
        final com.jaspersoft.jasperserver.api.metadata.common.domain.Resource resource = repositoryService.getResource(null, reportUnitURI);
        if (resource == null) {
            throw new ResourceNotFoundException(reportUnitURI);
        } else if(!reportExecutor.isRunnableResource(resource)){
            throw new IllegalParameterValueException("reportURI", reportUnitURI);
        }
        executions.put(requestId, execution);
        startReportExecution(execution);
        if (exportOptions != null && exportOptions.getOutputFormat() != null && !exportOptions.getOutputFormat().isEmpty()) {
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

    public ExportExecution executeExport(final String executionId, final ExportExecutionOptions exportOptions) throws RemoteException {
        final ReportExecution execution = executions.get(executionId);
        if (execution == null) throw new ResourceNotFoundException(executionId);
        return executeExport(exportOptions, execution);
    }

    protected ExportExecution executeExport(ExportExecutionOptions exportOptions, final ReportExecution reportExecution) throws RemoteException {
        final ExportExecution exportExecution = new ExportExecution();
        exportExecution.setStatus(ExecutionStatus.queued);
        exportExecution.setOptions(exportOptions);
        reportExecution.getExports().put(exportExecution);
        startExport(reportExecution, exportExecution);
        return exportExecution;
    }

    protected void startExport(final ReportExecution reportExecution, final ExportExecution exportExecution) {
        ExportExecutionOptions exportOptions = exportExecution.getOptions();
        final ReportExecutionOptions options = reportExecution.getOptions();
        String attachmentsPrefix = exportOptions.getAttachmentsPrefix() != null ?
                exportOptions.getAttachmentsPrefix() : options.getDefaultAttachmentsPrefixTemplate();
        if (attachmentsPrefix != null) {
            attachmentsPrefix = attachmentsPrefix
                    .replace(CONTEXT_PATH_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER, options.getContextPath() != null ? options.getContextPath() : "")
                    .replace(REPORT_EXECUTION_ID_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER, reportExecution.getRequestId())
                    .replace(EXPORT_EXECUTION_ID_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER, exportExecution.getId());
        }
        final String attachmentsPrefixClosure = attachmentsPrefix;
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
                        exportExecution.setErrorDescriptor(new ErrorDescriptor.Builder()
                                .setErrorCode("export.failed")
                                .setMessage("Export can't be executed. Report execution status is '"
                                        + reportExecution.getStatus().toString() + "'."
                                        + (reportExecution.getErrorDescriptor() != null
                                        ? " " + reportExecution.getErrorDescriptor().toString() : "")).getErrorDescriptor());
                    } else {
                        executeExport(exportExecution, reportExecution, attachmentsPrefixClosure);
                    }
                } catch (Exception e) {
                    exportExecution.setErrorDescriptor(new RemoteException(e).getErrorDescriptor());
                }
            }
        });
    }

    protected void executeExport(ExportExecution exportExecution, ReportExecution reportExecution, String imagesUri) {
        JasperPrintAccessor jasperPrintAccessor = reportExecution.getFinalReportUnitResult().getJasperPrintAccessor();
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
            }
            exportExecution.setStatus(ExecutionStatus.execution);
            generateReportOutput(reportExecution, jasperPrint, exportExecution.getOptions().getOutputFormat(),
                    imagesUri, exportExecution, exportExecution.getOptions().getPages());
            exportExecution.getOutputResource().setOutputFinal(isOutputFinal);
            exportExecution.setStatus(ExecutionStatus.ready);
        } catch (RemoteException ex) {
            exportExecution.setErrorDescriptor(ex.getErrorDescriptor());
        } catch (Exception ex) {
            log.debug("Unexpected error occurs during export", ex);
            exportExecution.setErrorDescriptor(new ErrorDescriptor(ex));
        }
    }

    public ReportOutputResource getOutputResource(String executionId, ExportExecutionOptions exportExecutionOptions) throws RemoteException {
        final ReportExecution execution = executions.get(executionId);
        if (execution == null) throw new ResourceNotFoundException(executionId);
        return executeExport(exportExecutionOptions, execution).getFinalOutputResource();
    }

    public ReportOutputResource getOutputResource(String executionId, String exportId) throws RemoteException {
        final ExportExecution exportExecution = getExportExecution(executionId, exportId);
        if (exportExecution.getStatus() == ExecutionStatus.cancelled) {
            // cancelled status means export reset is done. Need to rerun
            exportExecution.setStatus(ExecutionStatus.queued);
            startExport(getReportExecution(executionId), exportExecution);
        }
        return exportExecution.getFinalOutputResource();
    }

    public ExportExecution getExportExecution(String executionId, String exportId) throws ResourceNotFoundException {
        final ReportExecution execution = executions.get(executionId);
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
            ReportExecutionOptions executionOptions, ExportExecutionOptions exportOptions) throws RemoteException {
        final ReportExecution execution = getReportExecutionFromRawParameters(reportUnitURI, rawParameters, executionOptions, exportOptions);
        return getOutputResource(execution.getRequestId(), exportOptions);
    }

    protected Map<String, Object> executeInputControlsCascadeWithRawInput(String reportUnitUri, Map<String, String[]> rawInputParameters) throws CascadeResourceNotFoundException, InputControlsValidationException {
        final List<InputControlState> valuesForInputControls = inputControlsLogicService.getValuesForInputControls(reportUnitUri, null, rawInputParameters);
        final Map<String, String[]> inputControlFormattedValues = ReportParametersUtils.getValueMapFromInputControlStates(valuesForInputControls);
        return inputControlsLogicService.getTypedParameters(reportUnitUri, inputControlFormattedValues);
    }

    /**
     * @param reportExecution - the report execution instance. Holds all execution context related things.
     * @param jasperPrint     - filled with data jasper print object
     * @param rawOutputFormat - output format in raw format
     * @param imagesURI       - images URI prefix
     * @param exportExecution - export execution model
     * @param pages           - what pages should be exported
     * @throws RemoteException
     */
    protected void generateReportOutput(ReportExecution reportExecution, JasperPrint jasperPrint,
            String rawOutputFormat, String imagesURI, ExportExecution exportExecution, ReportOutputPages pages) throws RemoteException {
        try {
            String outputFormat = rawOutputFormat != null ? rawOutputFormat.toUpperCase() : Argument.RUN_OUTPUT_FORMAT_PDF;
            // Export...
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ReportExecutionOptions reportExecutionOptions = reportExecution.getOptions();
            final ExportExecutionOptions exportExecutionOptions = exportExecution.getOptions();
            if (outputFormat.equals(Argument.RUN_OUTPUT_FORMAT_JRPRINT)) {
                if (log.isDebugEnabled())
                    log.debug("Returning JasperPrint");
                if (reportExecutionOptions.getTransformerKey() != null) {
                    if (log.isDebugEnabled())
                        log.debug("Transforming JasperPrint generic element for key " + reportExecutionOptions.getTransformerKey());
                    GenericElementReportTransformer.transformGenericElements(reportExecutor.getJasperReportsContext(reportExecutionOptions.isInteractive()), jasperPrint, reportExecutionOptions.getTransformerKey());
                }
                JRSaver.saveObject(jasperPrint, bos);
                exportExecution.setOutputResource(new ReportOutputResource("application/octet-stream", bos.toByteArray()));
            } else {
                HashMap<String, Object> exportParameters = new HashMap<String, Object>(reportExecution.getRawParameters());
                if (pages != null) exportParameters.put(Argument.RUN_OUTPUT_PAGES, pages);
                if (imagesURI != null) exportParameters.put(Argument.RUN_OUTPUT_IMAGES_URI, imagesURI);
                exportParameters.put(HtmlExporter.CONTEXT_PATH_PARAM_NAME, reportExecutionOptions.getContextPath());
                exportParameters.put(HtmlExporter.BASE_URL_PARAM_NAME, exportExecutionOptions.getBaseUrl());
                exportParameters.put(HtmlExporter.ALLOW_INLINE_SCRIPTS_PARAM_NAME, exportExecutionOptions.isAllowInlineScripts());
                exportParameters.put(HtmlExporter.INTERACTIVE_PARAM_NAME, reportExecutionOptions.isInteractive());
                if (!exportExecutionOptions.isAllowInlineScripts()) {
                    ReportContext reportContext = reportExecution.getFinalReportUnitResult().getReportContext();
                    if (reportContext != null) {
                        reportContext.setParameterValue(ReportContext.REQUEST_PARAMETER_APPLICATION_DOMAIN, exportExecution.getOptions().getBaseUrl());
                        exportParameters.put(HtmlExporter.REPORT_CONTEXT_PARAM_NAME, reportContext);
                    }
                }

                Map<JRExporterParameter, Object> exporterParams;
                final String reportURI = reportExecution.getReportURI();
                auditHelper.createAuditEvent("export");
                auditHelper.addPropertyToAuditEvent("export", "uris", reportURI);
                try {
                    exporterParams = reportExecutor.exportReport(reportURI, jasperPrint, outputFormat, bos, exportParameters);
                    if (log.isDebugEnabled())
                        log.debug("Exporter params: " + Arrays.asList(exporterParams.keySet().toArray()));
                } catch (RemoteException e) {
                    auditHelper.addExceptionToAllAuditEvents(e);
                    throw e;
                } catch (Exception e) {
                    log.debug("Error exporting report", e);
                    auditHelper.addExceptionToAllAuditEvents(e);
                    throw new RemoteException(
                            new ErrorDescriptor.Builder()
                                    .setErrorCode("webservices.error.errorExportingReportUnit").setParameters(e.getMessage())
                                    .getErrorDescriptor(), e
                    );
                } finally {
                    try {
                        bos.close();
                    } catch (IOException ex) {
                        log.error("caught exception: " + ex.getMessage(), ex);
                    }
                }
                final Matcher matcher = FILE_NAME_PATTERN.matcher(reportURI);
                exportExecution.setOutputResource(new ReportOutputResource(reportExecutor.getContentType(outputFormat),
                        bos.toByteArray(), (matcher.find() ? matcher.group(1) : "report") + "." + outputFormat.toLowerCase()));
                if (Argument.RUN_OUTPUT_FORMAT_HTML.equals(outputFormat)) {
                    putImages(exporterParams, exportExecution.getAttachments());
                }
            }
        } catch (RemoteException e) {
            throw e;
        } catch (ServiceException e) {
            log.error("caught exception: " + e.getMessage(), e);
        } catch (Throwable e) {
            log.error("caught Throwable exception: " + e.getMessage(), e);
        }
    }

    /**
     * Place images to output container.
     *
     * @param exportParameters - export result, contains images
     * @param outputContainer  - output container to fill with images
     * @throws RemoteException if any error occurs
     */
    protected void putImages(Map<JRExporterParameter, Object> exportParameters, Map<String, ReportOutputResource> outputContainer) throws RemoteException {
        try {
            // cast is safe because of known parameter key
            @SuppressWarnings("unchecked")
            Map<String, byte[]> imagesMap = (Map<String, byte[]>) exportParameters.get(JRHtmlExporterParameter.IMAGES_MAP);
            if (imagesMap != null && !imagesMap.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("imagesMap : " + Arrays.asList(imagesMap.keySet().toArray()));
                }
                for (String name : imagesMap.keySet()) {
                    byte[] data = imagesMap.get(name);
                    if (log.isDebugEnabled()) {
                        log.debug("Adding image for HTML: " + name);
                    }
                    outputContainer.put(name, new ReportOutputResource(JRTypeSniffer.getImageTypeValue(data).getMimeType(), data, name));
                }
            }
        } catch (Throwable e) {
            log.error(e);
            throw new RemoteException(new ErrorDescriptor.Builder()
                    .setErrorCode("webservices.error.errorAddingImage").setParameters(e.getMessage()).getErrorDescriptor(), e);
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

    public Boolean cancelReportExecution(String requestId) throws RemoteException {
        return engine.cancelExecution(requestId);
    }
}
