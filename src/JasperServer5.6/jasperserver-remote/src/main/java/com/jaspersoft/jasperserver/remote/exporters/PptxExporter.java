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
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PptxExportParametersBean;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import jxl.write.biff.RowsExceededException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePptxReportConfiguration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: PptxExporter.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
@Service("remotePptxExporter")
@Scope("prototype")
public class PptxExporter extends AbstractExporter {

    @Resource(name = "pptxExportParameters")
    private PptxExportParametersBean exportParams;

    @Override
    public JRExporter createExporter() throws Exception {
        return new JRPptxExporter(getJasperReportsContext());
    }

    @Override
    public void configureExporter(JRExporter exporter, HashMap exportParameters) throws Exception {
		if(exportParams != null)
		{
			if(exportParams.getIgnoreHyperlink() != null) {
				SimplePptxReportConfiguration configuration = new SimplePptxReportConfiguration();
				configuration.setIgnoreHyperlink(exportParams.getIgnoreHyperlink());
				exporter.setConfiguration(configuration);
			}
		}	
    }

    /**
     * @param exportParams The exportParams to set.
     */
    public void setExportParams(PptxExportParametersBean exportParams) {
        this.exportParams = exportParams;
    }

    @Override
    public String getContentType() {
        return "application/pptx";
    }
}
