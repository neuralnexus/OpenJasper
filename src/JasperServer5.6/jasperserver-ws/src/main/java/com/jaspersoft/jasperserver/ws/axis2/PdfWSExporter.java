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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PdfExportParametersBean;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @@version $Id: PdfWSExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class PdfWSExporter implements WSExporter {
	
	PdfExportParametersBean exportParams;
	
	public Map exportReport(
			JasperPrint jasperPrint, 
			OutputStream output, 
			EngineService engineService, 
			HashMap exportParameters,
			ExecutionContext executionContext,
			String reportUnitURI
			) throws Exception
	{
		//set the input/output parameters in the map
		exportParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exportParameters.put(JRExporterParameter.OUTPUT_STREAM, output);
		
		//use the PDF export params bean
//		PdfExportParametersBean pdfParams = (PdfExportParametersBean) 
//				getServiceConfiguration().getExportParameters(format.toLowerCase());
		if (exportParams != null) {
//			String printLocaleCode = jasperPrint.getLocaleCode();
//			Locale printLocale = LocaleHelper.getInstance().getLocale(printLocaleCode);
		}
		
		//export through EngineService to resolve repository fonts
		engineService.exportToPdf(executionContext, 
				reportUnitURI, exportParameters);
		
		return exportParameters;
		
	}

	/**
	 * @return Returns the exportParams.
	 */
	public PdfExportParametersBean getExportParams() {
		return exportParams;
	}

	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(PdfExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}
}
