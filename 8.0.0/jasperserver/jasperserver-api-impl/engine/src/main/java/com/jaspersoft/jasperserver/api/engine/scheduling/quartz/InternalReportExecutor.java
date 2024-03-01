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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequest;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.EngineServiceImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.SimpleReportContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class InternalReportExecutor implements ReportExecutor, MessageSourceAware {
	
	private static final Log log = LogFactory.getLog(InternalReportExecutor.class);
	
	private VirtualizerFactory virtualizerFactory;
	private DataSnapshotService dataSnapshotService;
	private DataCacheProvider dataCacheProvider;
	private Map<String, String> outputKeyMapping;
	private Map<String, Output> outputFormatMap;
	private MessageSource messageSource;
	
	protected ReportJobContext jobContext;
    protected ReportJob jobDetails;
    private ReportUnit reportUnit;
    private ExecutionContext executionContext;

    private boolean hasDataSnapshotOutput;
    private boolean recordDataSnapshot;
    private boolean recordedDataSnapshot;
    private String dataSnapshotOutputName;
    private ReportContext reportContext;

    private Map<PaginationParameters, ReportUnitResult> reportResults = new LinkedHashMap<>();
	private JasperReport jasperReport;

	public InternalReportExecutor() {
	}
	
	public void init(ReportJobContext jobContext) {
		this.jobContext = jobContext;
		
		this.jobDetails = jobContext.getReportJob();
		this.reportUnit = jobContext.getReportUnit();
		this.executionContext = jobContext.getExecutionContext();
	}

	@Override
	public List<? extends ReportExecutionOutput> createOutputs(Map<String, Object> reportParameters) {
        Set<Byte> outputFormats = jobDetails.getOutputFormatsSet();
        List<ReportExecutionOutput> outputs = new ArrayList<ReportExecutionOutput>(outputFormats.size());
        for (Iterator<Byte> it = outputFormats.iterator(); it.hasNext(); ) {
            Byte format = it.next();

            ReportExecutionOutput output = null;
            try {
                output = getOutput(format, jobContext.getBaseFilename());
                if (output != null) {
                    outputs.add(output);
                }
            } catch (Exception e) {
                String formatKey = getMessage("report.scheduling.output.format." + format, null);
                String formatLabel = getMessage("report.output." + formatKey + ".label", null);
                jobContext.handleException(getMessage("report.scheduling.error.exporting.report", new Object[]{formatLabel}), e);
            }
        }

        executeReport(reportParameters, outputs);
        return outputs;
	}

    protected ReportExecutionOutput getOutput(Byte format, String baseFilename) throws JobExecutionException {
        if (format == null) {
            throw new JSException("jsexception.report.unknown.output.format", new Object[]{"null"});
        }

        if (format == ReportJob.OUTPUT_FORMAT_DATA_SNAPSHOT) {
            hasDataSnapshotOutput = true;
            dataSnapshotOutputName = baseFilename;
            return null;
        }

        String outputFormat = getOutputKeyMapping().get(format.toString());
        if (outputFormat == null) {
            throw new JSException("jsexception.report.unknown.output.format", new Object[]{new Byte(format)});
        }

        Output output = getOutputFormatMap().get(outputFormat);
		return new InternalReportExecutionOutput(outputFormat, output);
    }
    
    protected class InternalReportExecutionOutput implements ReportExecutionOutput {
    	private final String outputFormat;
    	private final Output output;
    	
    	public InternalReportExecutionOutput(String outputFormat, Output output) {
    		this.outputFormat = outputFormat;
    		this.output = output;
    	}

    	@Override
    	public String getOutputFormat() {
    		return outputFormat;
    	}

    	public Output getOutput() {
    		return output;
    	}

    	@Override
    	public ReportOutput getReportOutput(ReportJobContext reportJobContext) throws JobExecutionException {
    		ReportUnitResult result = getReportResultForOutput(output, jasperReport);
    		return getOutput().getOutput(reportJobContext, result.getJasperPrint());
    	}
    }

    protected void executeReport(Map<String, Object> reportParameters, List<ReportExecutionOutput> outputs) {
        jasperReport = jobContext.getEngineService().getMainJasperReport(executionContext, 
        		jobContext.getReportUnitURI());
        
    	List<PaginationParameters> paginations = new ArrayList<PaginationParameters>();
        for (ReportExecutionOutput output : outputs) {
        	if (output instanceof InternalReportExecutionOutput) {
            	InternalReportExecutionOutput reportExecutionOutput = (InternalReportExecutionOutput) output;
				PaginationParameters paginationParameters = reportExecutionOutput.getOutput().getPaginationParameters(jasperReport);
            	if (!paginations.contains(paginationParameters)) {
            		paginations.add(paginationParameters);
            	}
        	}
        }

        // if we have data snapshot output but no regular output, run the default paginated report
        if (hasDataSnapshotOutput && paginations.isEmpty()) {
        	PaginationParameters defaultPagination = new PaginationParameters();
        	paginations.add(defaultPagination);
        }
        
        //sort in an order that allows results to be reused for several pagination params
        Collections.sort(paginations, PaginationParamsExecutionComparator.instance());

        runReport(paginations, reportParameters);
    }

	protected void runReport(List<PaginationParameters> paginations, Map<String, Object> reportParameters) {
		// recording a data snapshot if saving is enabled or if we need to fill the report multiple times
        recordDataSnapshot = getDataSnapshotService().isSnapshotPersistenceEnabled() 
                //FIXME detect common cases when multiple pagination params can use a single execution
        		|| paginations.size() > 1;
        		
        for (PaginationParameters paginationParams : paginations) {
        	ReportUnitResult result = findMatchingResult(paginationParams);
			if (result == null) {
				result = runReport(paginationParams, reportParameters);
				reportResults.put(paginationParams, result);
			}
		}
	}

	protected ReportUnitResult findMatchingResult(PaginationParameters paginationParams) {
		ReportUnitResult result = null;
		//try to find an existing result that matches the params
		for (Entry<PaginationParameters, ReportUnitResult> entry : reportResults.entrySet()) {
			ReportUnitResult existingResult = entry.getValue();
			if (existingResult != null && existingResult.matchesPagination(paginationParams)) {
				result = existingResult;
				if (log.isDebugEnabled()) {
					log.debug("report for " + entry.getKey() + " matches pagination " + paginationParams);
				}
				break;
			}
		}
		return result;
	}

	protected ReportUnitResult runReport(PaginationParameters paginationParams, 
			Map<String, Object> reportParameters) {
        jobContext.checkCancelRequested();
        if (log.isDebugEnabled()) {
            log.debug("running report with pagination " + paginationParams);
        }
        
		ReportUnitResult result = null;
        try {
        	Map<String, Object> parametersMap = new HashMap<>(reportParameters);
        	putAdditionalParameters(parametersMap);
            Map<String, Object> reportJobProperties = collectReportJobProperties();

            paginationParams.setReportParameters(parametersMap);
            ReportUnitRequest request = new ReportUnitRequest(jobContext.getReportUnitURI(), 
            		parametersMap, reportJobProperties);
            request.setJasperReportsContext(jobContext.getJasperReportsContext());

            result = runReport(request);
        } catch (Exception e) {
        	jobContext.handleException(getMessage("report.scheduling.error.filling.report", null), e);
        }
        return result;
	}

    // put ReportJob information into ReportUnitRequest
    protected Map<String, Object> collectReportJobProperties() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> jobParams = jobDetails.getSource().getParameters();
        if (jobParams != null) {
            params.putAll(jobParams);
        }
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_JOBID, jobDetails.getId());
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_JOBLABEL, jobDetails.getLabel());
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_REPORTURI, jobDetails.getSource().getReportUnitURI());
        Date scheduledFireTime = jobContext.getScheduledFireTime();
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_FIRETIME, scheduledFireTime);
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_USERNAME, jobDetails.getUsername());
        params.put(EngineServiceImpl.ReportExecutionStatus.PROPERTY_QUARTZJOB, jobContext.getReportExecutionJob());
        return params;
    }

    protected void putAdditionalParameters(Map<String, Object> parametersMap) {
        if (!parametersMap.containsKey(ReportExecutionJob.REPORT_PARAMETER_SCHEDULED_TIME)) {
            Date scheduledFireTime = jobContext.getScheduledFireTime();
            parametersMap.put(ReportExecutionJob.REPORT_PARAMETER_SCHEDULED_TIME, scheduledFireTime);
        }
        VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
        if (virtualizerFactory != null) {
            parametersMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizerFactory.getVirtualizer());
        }
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

        EngineService engineService = jobContext.getEngineService();
        ReportUnitResult result = (ReportUnitResult) engineService.execute(
        		executionContext, request);
        setReadOnly(result);

        if (firstRun && getDataSnapshotService().isSnapshotPersistenceEnabled()) {
            dataSnapshotRecorded();
        }

        return result;
	}

    protected void setReadOnly(ReportUnitResult result) {
        if (result != null) {
            VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
            if (virtualizerFactory != null) {
                virtualizerFactory.setReadOnly(result);
            }
        }
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
            reportUnit = (ReportUnit) jobContext.getRepositoryService().getResource(executionContext,
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
        	jobContext.handleException(getMessage("report.scheduling.error.saving.data.snapshot.failed", null), e);
        }
        // TODO lucianc link to the report in the mail notification
    }

	@Override
	public boolean hasResult() {
        return !reportResults.isEmpty();
    }

	@Override
	public boolean isResultEmpty() {
    	for (ReportUnitResult result : reportResults.values()) {
			if (isEmpty(result)) {
	    		//old code returned true if any of the results was empty, preserving the logic
				return true;
			}
		}
    	return false;
    }

    protected boolean isEmpty(ReportUnitResult result) {
        if (result == null) {
            return false;
        }
        
        JasperPrint jasperPrint = result.getJasperPrint();
        List<JRPrintPage> pages = jasperPrint.getPages();
        Boolean empty = null;
        if (pages == null || pages.isEmpty()) {
            empty = true;
        } else if (pages.size() == 1) {
            JRPrintPage page = pages.get(0);
            List<JRPrintElement> elements = page.getElements();
            if (elements == null || elements.isEmpty()) {
                empty = true;
            }
	    }
        if (empty == null) {
        	empty = 
        		JRPropertiesUtil.asBoolean(
    				JRPropertiesUtil.getOwnProperty(jasperPrint, ReportExecutionJob.PROPERTY_REPORT_EMPTY),
    				false
    				);
	    }
        return empty;
    }

	@Override
	public void dispose() {
    	for (ReportUnitResult result : reportResults.values()) {
    		disposeVirtualizer(result);
		}
    	reportResults.clear();
    	
        jobDetails = null;
        reportUnit = null;
        executionContext = null;
        hasDataSnapshotOutput = false;
        
        reportContext = null;
        recordDataSnapshot = false;
        recordedDataSnapshot = false;
        dataSnapshotOutputName = null;
	}

    protected void disposeVirtualizer(ReportUnitResult result) {
        if (result != null) {
            VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
            if (virtualizerFactory != null) {
                virtualizerFactory.disposeReport(result);
            }
        }
    }

    protected ReportUnitResult getReportResultForOutput(Output output, JasperReport jasperReport) throws JobExecutionException {
    	PaginationParameters paginationParameters = output.getPaginationParameters(jasperReport);
    	ReportUnitResult result = reportResults.get(paginationParameters);
    	if (result == null) {
    		result = findMatchingResult(paginationParameters);
    		if (result == null) {
    			//should not happen
    			throw new JobExecutionException("Did not find report result for " + paginationParameters);
    		}
    	}
    	return result;
    }

	protected String getMessage(String key, Object[] arguments) {
        return messageSource.getMessage(key, arguments, jobContext.getLocale());
    }

    public VirtualizerFactory getVirtualizerFactory() {
        return virtualizerFactory;
    }

	public void setVirtualizerFactory(VirtualizerFactory virtualizerFactory) {
		this.virtualizerFactory = virtualizerFactory;
	}

    public DataSnapshotService getDataSnapshotService() {
        return dataSnapshotService;
    }

	public void setDataSnapshotService(DataSnapshotService dataSnapshotService) {
		this.dataSnapshotService = dataSnapshotService;
	}

	public DataCacheProvider getDataCacheProvider() {
        return dataCacheProvider;
    }

	public void setDataCacheProvider(DataCacheProvider dataCacheProvider) {
		this.dataCacheProvider = dataCacheProvider;
	}
    
	public Map<String, Output> getOutputFormatMap() {
        return outputFormatMap;
    }

	public void setOutputFormatMap(Map<String, Output> outputFormatMap) {
		this.outputFormatMap = outputFormatMap;
	}

	public Map<String, String> getOutputKeyMapping() {
        return outputKeyMapping;
    }

	public void setOutputKeyMapping(Map<String, String> outputKeyMapping) {
		this.outputKeyMapping = outputKeyMapping;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
