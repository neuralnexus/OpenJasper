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

import jxl.write.biff.RowsExceededException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;

import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.OdsExportParametersBean;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: ReportOdsExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportOdsExporter extends AbstractReportExporter{

	private static final String DIALOG_NAME = "odsExportParams";
	
	private OdsExportParametersBean exportParameters;
	
	/**
	 * @return Returns the exportParameters.
	 */
	public OdsExportParametersBean getExportParameters() {
		return exportParameters;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		return context.getFlowScope().get(ReportOdsExporter.DIALOG_NAME)== null? exportParameters : (ExportParameters)context.getFlowScope().get(ReportOdsExporter.DIALOG_NAME);
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(OdsExportParametersBean exportParameters) {
		this.exportParameters = exportParameters;
	}

	public void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException,JSException {
		JROdsExporter exporter = new JROdsExporter(getJasperReportsContext());
		exporter.setParameters(baseParameters);
		OdsExportParametersBean exportParams = (OdsExportParametersBean)getExportParameters(context);
		
		if (exportParams.isOverrideReportHints()) {
			exporter.setParameter(JRExporterParameter.PARAMETERS_OVERRIDE_REPORT_HINTS, Boolean.TRUE);
		}
		
		if (exportParams.getOnePagePerSheet() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, exportParams.getOnePagePerSheet());
		if (exportParams.getDetectCellType() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, exportParams.getDetectCellType());
		if (exportParams.getRemoveEmptySpaceBetweenRows() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, exportParams.getRemoveEmptySpaceBetweenRows());
		if (exportParams.getRemoveEmptySpaceBetweenColumns() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, exportParams.getRemoveEmptySpaceBetweenColumns());
		if (exportParams.getWhitePageBackground() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, exportParams.getWhitePageBackground());
		if (exportParams.getIgnoreGraphics() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, exportParams.getIgnoreGraphics());
		if (exportParams.getCollapseRowSpan() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, exportParams.getCollapseRowSpan());
		if (exportParams.getIgnoreCellBorder() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, exportParams.getIgnoreCellBorder());
		if (exportParams.getFontSizeFixEnabled() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, exportParams.getFontSizeFixEnabled());
		if (exportParams.getMaximumRowsPerSheet() != null)
			exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, exportParams.getMaximumRowsPerSheet());
		if(exportParams.getXlsFormatPatternsMap() != null && !exportParams.getXlsFormatPatternsMap().isEmpty())
			exporter.setParameter(JRXlsExporterParameter.FORMAT_PATTERNS_MAP, exportParams.getXlsFormatPatternsMap());
		exporter.setParameter(JExcelApiExporterParameter.CREATE_CUSTOM_PALETTE, Boolean.TRUE);
		try{
			exporter.exportReport();
		}catch(JRException e){
			if(e.getCause() instanceof RowsExceededException)
				throw new JSException("jsexception.too.many.data.rows");
			else
				throw e;
		}
	}

	protected String getContentType(RequestContext context) {
		return "application/vnd.oasis.opendocument.spreadsheet";
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
		response.setHeader("Content-Disposition", "inline; filename=\"" + getFilename(context) + "\"");
	}

	protected String getDownloadFileExtension() {
		return "ods";
	}

	@Override
	protected Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder) {
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null) 
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), OdsExportParametersBean.PROPERTY_XLS_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
