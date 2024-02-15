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

package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.CsvExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.XlsExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.MemoryDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: XlsReportOutput.java 19940 2010-12-13 09:29:40Z tmatyashovsky $
 */
public class XlsxNoPaginationReportOutput extends XlsxReportOutput
{

	private static final Log log = LogFactory.getLog(XlsxNoPaginationReportOutput.class);

	private XlsExportParametersBean exportParams;
	public XlsxNoPaginationReportOutput()
	{
	}

	/** 
	 *
	 */
	public ReportOutput getOutput(
		ReportExecutionJob job, 
		ExecutionContext executionContext, 
		JasperPrint jasperPrint, 
		String baseFilename
		) throws JobExecutionException
	{
		ReportUnitResult result = job.executeReport(Boolean.TRUE);
		
		if (result != null)
		{
			jasperPrint = result.getJasperPrint();
		}
		
		return 
			super.getOutput(
				job.getEngineService(), 
				executionContext, 
				job.getReportUnitURI(),
				job.createDataContainer(),
				job.getHyperlinkProducerFactory(), 
				job.getRepository(), 
				jasperPrint, 
				baseFilename, 
				job.getLocale(), 
				job.getCharacterEncoding()
				);
	}
	
	@Override
	protected String getFileExtension()
	{
		return "nopag.xlsx";
	}
}
