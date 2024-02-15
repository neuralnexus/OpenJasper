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
package com.jaspersoft.jasperserver.war.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.FileBufferedOutputStream;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.util.HttpUtils;
import com.jaspersoft.jasperserver.war.util.ObjectProcessor;
import com.jaspersoft.jasperserver.war.util.ObjectSelector;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AbstractReportExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class AbstractReportExporter extends MultiAction {
	
	private static final Log log = LogFactory.getLog(AbstractReportExporter.class);

	private SessionObjectSerieAccessor jasperPrintAccessor;
	private String flowAttributeJasperPrintName;
	private String flowAttributeReportUnitURI;
	private boolean setResponseContentLength = true;
	private int memoryThreshold = 1 << 18;// default value
	private int initialMemoryBufferSize = FileBufferedOutputStream.DEFAULT_INITIAL_MEMORY_BUFFER_SIZE;
	private HttpUtils httpUtils;
	private ObjectSelector<HttpServletRequest, ObjectProcessor<HttpServletResponse>> responseHeaderSetter;
	private JasperReportsContext jasperReportsContext;
	
	private Boolean paginated;
	private ViewReportAction viewReportAction;
	
	@Override
	public String toString() {
		return this.getClass().getName() + ", paginated: " + paginated;
	}
	
	public Event export(RequestContext context) throws IOException, JRException {
		ServletExternalContext servletContext = (ServletExternalContext) context.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) servletContext.getNativeResponse();

		AttributeMap flowAttrs = context.getFlowScope();
		String reportUnitURI = flowAttrs.getRequiredString(getFlowAttributeReportUnitURI());
		String jasperPrintName = flowAttrs.getRequiredString(getFlowAttributeJasperPrintName());
		
		if (log.isDebugEnabled()) {
			log.debug(this + " exporting report " + jasperPrintName + " at " + reportUnitURI);
		}
		
		JasperPrint jasperPrint = getJasperPrint(context, jasperPrintName);
		if (setResponseContentLength)
		{
			exportBuffered(context, response, jasperPrint, reportUnitURI);
		}
		else
		{
			exportToStream(context, response, jasperPrint, reportUnitURI);
		}
		
		return success();
		//FIXME lucianc remove the report from the session store on direct URL
	}

	protected JasperPrint getJasperPrint(RequestContext context, String jasperPrintName) 
	{
		ServletExternalContext servletContext = (ServletExternalContext) context.getExternalContext();
		ReportUnitResult result = 
			(ReportUnitResult) getJasperPrintAccessor().getObject(
				(HttpServletRequest) servletContext.getNativeRequest(), 
				jasperPrintName
				);
		
		if (result != null) 
		{
			Boolean isPaginationPreferred = isPaginationPreferred(result.getJasperPrint());
			boolean isPaginated = result.isPaginated();
			if (isPaginationPreferred != null && isPaginated != isPaginationPreferred) 
			{
				if (log.isDebugEnabled()) {
					log.debug("requested report pagination flag: " + isPaginationPreferred + ", current report pagination flag: " + isPaginated);
				}
				
				context.getRequestScope().put(getViewReportAction().getAttributeIgnorePagination(), !isPaginationPreferred);
				result = getViewReportAction().executeReport(context);
			}
		}
		
		JasperPrint jasperPrint = (result == null ? null : result.getJasperPrint());
		if (jasperPrint == null) {
			throw new JSException("jsexception.jasperprint.not.found", new Object[] {jasperPrintName});
		}
		return jasperPrint;
	}

	protected void exportToStream(RequestContext context, HttpServletResponse response, JasperPrint jasperPrint, String reportUnitURI) throws IOException, JRException {
		Map parameters = new HashMap();
		parameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);

		OutputStream ouputStream = response.getOutputStream();
		parameters.put(JRExporterParameter.OUTPUT_STREAM, ouputStream);
		
		try
		{
			response.setContentType(getContentType(context));
			setAdditionalResponseHeaders(context, response);
			export(context, getExecutionContext(context), reportUnitURI, parameters);
		}
		finally
		{
			if (ouputStream != null)
			{
				try
				{
					ouputStream.close();
				}
				catch (IOException ex)
				{
					log.warn("Error closing output stream", ex);
				}
			}
		}
	}

	protected ExecutionContext getExecutionContext(RequestContext context) {
		return ExecutionContextImpl.getRuntimeExecutionContext(JasperServerUtil.getExecutionContext(context));
	}

	protected void exportBuffered(RequestContext context, HttpServletResponse response, JasperPrint jasperPrint, String reportUnitURI) throws IOException, JRException {
		Map parameters = new HashMap();
		parameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);

		FileBufferedOutputStream bufferedOutput = new FileBufferedOutputStream(getMemoryThreshold(), getInitialMemoryBufferSize());
		parameters.put(JRExporterParameter.OUTPUT_STREAM, bufferedOutput);

		try {
			export(context, getExecutionContext(context), reportUnitURI, parameters);
			bufferedOutput.close();
			
			int exportSize = bufferedOutput.size();
			if (log.isDebugEnabled()) {
				log.debug("exported to buffer of size " + exportSize);
			}
			
			response.setContentType(getContentType(context));
			setAdditionalResponseHeaders(context, response);
			response.setContentLength(exportSize);
			ServletOutputStream ouputStream = response.getOutputStream();

			try {
				bufferedOutput.writeData(ouputStream);
				bufferedOutput.dispose();
				
				ouputStream.flush();
			} finally {
				if (ouputStream != null) {
					try {
						ouputStream.close();
					}
					catch (IOException ex) {
						log.warn("Error closing output stream", ex);
					}
				}
			}
		} finally {
			bufferedOutput.close();
			bufferedOutput.dispose();
		}
	}

	public String getFlowAttributeJasperPrintName() {
		return flowAttributeJasperPrintName;
	}

	public void setFlowAttributeJasperPrintName(String flowAttributeJasperPrintName) {
		this.flowAttributeJasperPrintName = flowAttributeJasperPrintName;
	}

	public String getFlowAttributeReportUnitURI() {
		return flowAttributeReportUnitURI;
	}

	public void setFlowAttributeReportUnitURI(
			String requestAttributeReportUnitURI) {
		this.flowAttributeReportUnitURI = requestAttributeReportUnitURI;
	}

	public boolean isSetResponseContentLength() {
		return setResponseContentLength;
	}

	public void setSetResponseContentLength(boolean setResponseContentLength) {
		this.setResponseContentLength = setResponseContentLength;
	}

	public int getInitialMemoryBufferSize() {
		return initialMemoryBufferSize;
	}

	public int getMemoryThreshold() {
		return memoryThreshold;
	}

	public void setMemoryThreshold(int memoryThreshold) {
		this.memoryThreshold = memoryThreshold;
	}

	public void setInitialMemoryBufferSize(int initialMemoryBufferSize) {
		this.initialMemoryBufferSize = initialMemoryBufferSize;
	}
	
	
	protected abstract String getContentType(RequestContext context);

	/**
	 * 
	 * @param context
	 * @return
	 */
	protected abstract ExportParameters getExportParameters(RequestContext context);
	
	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		ServletExternalContext externalContext = (ServletExternalContext) context.getExternalContext();
		ObjectProcessor<HttpServletResponse> headerSetter = responseHeaderSetter.select(
				(HttpServletRequest) externalContext.getNativeRequest());
		if (headerSetter != null) {
			headerSetter.process(response);
		}
	}
	
	protected String getReportName(RequestContext context) {
		AttributeMap flowAttrs = context.getFlowScope();
		String reportUnitURI = flowAttrs.getRequiredString(getFlowAttributeReportUnitURI());
		// Get the last part of the URI
		
		return reportUnitURI.substring(reportUnitURI.lastIndexOf("/") + 1);
	}
	
	protected abstract void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException;
	
	protected String getFilename(RequestContext context) {
		ServletExternalContext servletContext = (ServletExternalContext) context.getExternalContext();
		String reportName = getReportName(context);
		HttpServletRequest request = (HttpServletRequest) servletContext.getNativeRequest();
		String downloadFilename = getDownloadFilename(request, reportName);
		return httpUtils.encodeContentFilename(request, downloadFilename);
	}
	
	protected String getFilename(RequestContext context, String fileExtension) {
		ServletExternalContext servletContext = (ServletExternalContext) context.getExternalContext();
		String filename = getReportName(context) + "." + fileExtension;
		return httpUtils.encodeContentFilename((HttpServletRequest) servletContext.getNativeRequest(), filename);
	}

	public SessionObjectSerieAccessor getJasperPrintAccessor() {
		return jasperPrintAccessor;
	}

	public void setJasperPrintAccessor(
			SessionObjectSerieAccessor jasperPrintAccessor) {
		this.jasperPrintAccessor = jasperPrintAccessor;
	}

	public HttpUtils getHttpUtils() {
		return httpUtils;
	}

	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}

	protected String getDownloadFileExtension() {
		return null;
	}
	
	public String getDownloadFilename(HttpServletRequest request, String reportName) {
		String extension = getDownloadFileExtension();
		if (extension == null) {
			return null;
		}
		
		String filename = reportName + "." + extension;
		return filename;
	}

	public ObjectSelector<HttpServletRequest, ObjectProcessor<HttpServletResponse>> getResponseHeaderSetter() {
		return responseHeaderSetter;
	}

	public void setResponseHeaderSetter(
			ObjectSelector<HttpServletRequest, ObjectProcessor<HttpServletResponse>> responseHeaderSetter) {
		this.responseHeaderSetter = responseHeaderSetter;
	}

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

	public void setJasperReportsContext(JasperReportsContext jasperReportsContext) {
		this.jasperReportsContext = jasperReportsContext;
	}

	public Boolean isPaginated() {
		return paginated;
	}

	public void setPaginated(Boolean paginated) {
		this.paginated = paginated;
	}

	protected Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder)
	{
		return isPaginated();
	}

	public ViewReportAction getViewReportAction() {
		return viewReportAction;
	}

	public void setViewReportAction(ViewReportAction viewReportAction) {
		this.viewReportAction = viewReportAction;
	}
	
}
