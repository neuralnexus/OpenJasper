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

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;

import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.TxtExportParametersBean;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: ReportTextExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportTextExporter extends AbstractReportExporter{

	private static final String DIALOG_NAME = "txtExportParams";
	
	private TxtExportParametersBean exportParameters;
	
	/**
	 * @return Returns the exportParameters.
	 */
	public TxtExportParametersBean getExportParameters() {
		return exportParameters;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		return context.getFlowScope().get(ReportTextExporter.DIALOG_NAME)== null? exportParameters : (ExportParameters)context.getFlowScope().get(ReportTextExporter.DIALOG_NAME);
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(TxtExportParametersBean exportParameters) {
		this.exportParameters = exportParameters;
	}

	public void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException,JSException {
		JRTextExporter exporter = new JRTextExporter(getJasperReportsContext());
		exporter.setParameters(baseParameters);
		TxtExportParametersBean exportParams = (TxtExportParametersBean)getExportParameters(context);
		
		if (exportParams.isOverrideReportHints()) {
			exporter.setParameter(JRExporterParameter.PARAMETERS_OVERRIDE_REPORT_HINTS, Boolean.TRUE);
		}
		
		if (exportParams.getCharacterHeight() != null)
			exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, exportParams.getCharacterHeight());
		if (exportParams.getCharacterWidth() != null)
			exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, exportParams.getCharacterWidth());
		if (exportParams.getPageHeight() != null)
			exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, exportParams.getPageHeight());
		if (exportParams.getPageWidth() != null)
			exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, exportParams.getPageWidth());
		
		exporter.exportReport();
	}

	protected String getContentType(RequestContext context) {
		return "application/txt";
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
		response.setHeader("Content-Disposition", "inline; filename=\"" + getFilename(context) + "\"");
	}

	protected String getDownloadFileExtension() {
		return "txt";
	}

	@Override
	protected Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder) {
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null) 
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), TxtExportParametersBean.PROPERTY_TEXT_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
