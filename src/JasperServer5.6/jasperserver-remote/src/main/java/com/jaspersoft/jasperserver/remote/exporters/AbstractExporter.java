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


package com.jaspersoft.jasperserver.remote.exporters;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.remote.ReportExporter;
import com.jaspersoft.jasperserver.remote.services.ReportOutputPages;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id: AbstractExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class AbstractExporter implements ReportExporter {
	
    @Resource(name = "jasperReportsRemoteContext")
    private JasperReportsContext jasperReportsContext;

    public Map<JRExporterParameter, Object> exportReport(JasperPrint jasperPrint, OutputStream output, EngineService engineService, HashMap exportParameters, ExecutionContext executionContext, String reportUnitURI) throws Exception {

        final JRExporter exporter = createExporter();

        // Handle generic parameters....
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);

        // Be sure the page number is correctly set, so PAGE 1 is PAGE 1...
        // JasperReports uses a 0 based page system, while we prefer a 1 based page system
        if (exportParameters.get(Argument.RUN_OUTPUT_PAGE) != null) {
            int pageIndex = Integer.parseInt((String) exportParameters.get(Argument.RUN_OUTPUT_PAGE));
            pageIndex--; // transform a 1 index page to 0 indexed page...
            exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex);
        }else if(exportParameters.get(Argument.RUN_OUTPUT_PAGES) != null){
            // cast is safe because of known key
            @SuppressWarnings("unchecked")
            ReportOutputPages pages = (ReportOutputPages) exportParameters.get(Argument.RUN_OUTPUT_PAGES);
            if(pages.getPage() != null){
                exporter.setParameter(JRExporterParameter.PAGE_INDEX, pages.getPage() - 1);
            } else if(pages.getStartPage() != null && pages.getEndPage() != null) {
                exporter.setParameter(JRExporterParameter.START_PAGE_INDEX, pages.getStartPage() - 1);
                exporter.setParameter(JRExporterParameter.END_PAGE_INDEX, pages.getEndPage() - 1);
            }
        }

        // Give the opportunity to each exporter to better configure itself...
        configureExporter(exporter, exportParameters);

        exporter.exportReport();
        return exporter.getParameters();
    }

    public abstract JRExporter createExporter() throws Exception;

    public void configureExporter(JRExporter exporter, HashMap exportParameters) throws Exception {
        // do nothing by default
    }

    protected Object getSingleParameterValue(String parameterName, Map<String, Object> exportParameters) {
        Object result = null;
        if (exportParameters.get(parameterName) != null) {
            if (exportParameters.get(parameterName) instanceof String[]) {
                if (((String[]) exportParameters.get(parameterName)).length > 0)
                    result = ((String[]) exportParameters.get(parameterName))[0];
            } else
                result = exportParameters.get(parameterName);
        }
        return result;
    }

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

}
