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

import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePptxReportConfiguration;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;

import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PptxExportParametersBean;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: ReportPptxExporter.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
public class ReportPptxExporter extends AbstractReportExporter{

	private static final String DIALOG_NAME = "pptxExportParams";
	
	private PptxExportParametersBean exportParameters;
	
	/**
	 * @return Returns the exportParameters.
	 */
	public PptxExportParametersBean getExportParameters() {
		return exportParameters;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		return context.getFlowScope().get(ReportPptxExporter.DIALOG_NAME)== null? exportParameters : (ExportParameters)context.getFlowScope().get(ReportPptxExporter.DIALOG_NAME);
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(PptxExportParametersBean exportParameters) {
		this.exportParameters = exportParameters;
	}

	public void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException {
		JRPptxExporter exporter = new JRPptxExporter(getJasperReportsContext());
		JasperPrint jasperPrint = (JasperPrint)baseParameters.get(JRExporterParameter.JASPER_PRINT);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		OutputStream os = (OutputStream)baseParameters.get(JRExporterParameter.OUTPUT_STREAM);
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
		PptxExportParametersBean exportParams = (PptxExportParametersBean)getExportParameters(context);
		if(exportParams != null)
		{
			if(exportParams.getIgnoreHyperlink() != null) {
				SimplePptxReportConfiguration configuration = new SimplePptxReportConfiguration();
				configuration.setIgnoreHyperlink(exportParams.getIgnoreHyperlink());
				exporter.setConfiguration(configuration);
			}
		}	
		exporter.exportReport();
	}

	protected String getContentType(RequestContext context) {
		return "application/pptx";
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
		response.setHeader("Content-Disposition", "inline; filename=\"" + getFilename(context) + "\"");
	}

	protected String getDownloadFileExtension() {
		return "pptx";
	}

	@Override
	protected Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder) {
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null) 
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), PptxExportParametersBean.PROPERTY_PPTX_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
