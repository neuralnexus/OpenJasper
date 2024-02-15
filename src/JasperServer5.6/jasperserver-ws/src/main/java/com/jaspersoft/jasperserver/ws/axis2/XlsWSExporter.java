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

package com.jaspersoft.jasperserver.ws.axis2;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.XlsExportParametersBean;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @@version $Id: XlsWSExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class XlsWSExporter implements WSExporter {
	
	private JasperReportsContext jasperReportsContext;
	private XlsExportParametersBean exportParams;
	
	public Map exportReport(
			JasperPrint jasperPrint, 
			OutputStream output, 
			EngineService engineService, 
			HashMap exportParameters,
			ExecutionContext executionContext,
			String reportUnitURI
			) throws Exception
	{
		JExcelApiExporter exporter = new JExcelApiExporter(getJasperReportsContext());
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
		
		if(exportParams != null)
		{
			if(exportParams.getOnePagePerSheet() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, exportParams.getOnePagePerSheet());
			if(exportParams.getDetectCellType() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, exportParams.getDetectCellType());
			if(exportParams.getRemoveEmptySpaceBetweenRows() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, exportParams.getRemoveEmptySpaceBetweenRows());
			if(exportParams.getRemoveEmptySpaceBetweenColumns() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, exportParams.getRemoveEmptySpaceBetweenColumns());
			if(exportParams.getWhitePageBackground() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, exportParams.getWhitePageBackground());
			if(exportParams.getIgnoreGraphics() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, exportParams.getIgnoreGraphics());
			if(exportParams.getCollapseRowSpan() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, exportParams.getCollapseRowSpan());
			if(exportParams.getIgnoreCellBorder() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, exportParams.getIgnoreCellBorder());
			if(exportParams.getFontSizeFixEnabled() != null);
				exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, exportParams.getFontSizeFixEnabled());
			if(exportParams.getMaximumRowsPerSheet() != null);
				exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, exportParams.getMaximumRowsPerSheet());
			if(exportParams.getXlsFormatPatternsMap() != null && !exportParams.getXlsFormatPatternsMap().isEmpty());
				exporter.setParameter(JRXlsExporterParameter.FORMAT_PATTERNS_MAP, exportParams.getXlsFormatPatternsMap());
		}	
		if (exportParameters.get(Argument.RUN_OUTPUT_PAGE) != null) 
		{
            exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer("" + exportParameters.get(Argument.RUN_OUTPUT_PAGE)));
		}
		
		exporter.exportReport();
		return exporter.getParameters();
		
	}
	
	/**
	 * @return Returns the exportParams.
	 */
	public XlsExportParametersBean getExportParams() {
		return exportParams;
	}

	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(XlsExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

	public void setJasperReportsContext(JasperReportsContext jasperReportsContext) {
		this.jasperReportsContext = jasperReportsContext;
	}

}
