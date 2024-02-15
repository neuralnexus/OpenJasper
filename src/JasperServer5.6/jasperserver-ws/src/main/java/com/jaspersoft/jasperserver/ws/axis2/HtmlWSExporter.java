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
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.HtmlExportUtil;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @@version $Id: HtmlWSExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class HtmlWSExporter implements WSExporter {
	
	private JasperReportsContext jasperReportsContext;
	
	public Map exportReport(
			JasperPrint jasperPrint, 
			OutputStream output, 
			EngineService engineService, 
			HashMap exportParameters,
			ExecutionContext executionContext,
			String reportUnitURI
			) throws Exception
	{
		JRExporter exporter = HtmlExportUtil.getHtmlExporter(getJasperReportsContext());
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
		
        if (exportParameters.get(Argument.RUN_OUTPUT_IMAGES_URI) != null)
        {
             exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,"" + exportParameters.get(Argument.RUN_OUTPUT_IMAGES_URI));
        }
        else
        {
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "images/");
        }
	    exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.TRUE);
	    //exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, reportContent);
	    
	    // collecting the images into a map
	    exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, new LinkedHashMap());
    
		if (exportParameters.get(Argument.RUN_OUTPUT_PAGE) != null) 
		{
            exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer("" + exportParameters.get(Argument.RUN_OUTPUT_PAGE)));
		}
		
		exporter.exportReport();
		return exporter.getParameters();
		
	}

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

	public void setJasperReportsContext(JasperReportsContext jasperReportsContext) {
		this.jasperReportsContext = jasperReportsContext;
	}
}
