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
import java.util.Locale;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.CsvExportParametersBean;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: CsvReportOutput.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class CsvReportOutput extends AbstractReportOutput 
{

	private static final Log log = LogFactory.getLog(CsvReportOutput.class);

	private CsvExportParametersBean exportParams;
	
	public CsvReportOutput()
	{
		
	}
	/** 
	 * @see com.jaspersoft.jasperserver.api.engine.scheduling.quartz.Output#getOutput()
	 */
	public ReportOutput getOutput(
			EngineService engineService, 
			ExecutionContext executionContext, 
			String reportUnitURI, 
			DataContainer csvData,
			JRHyperlinkProducerFactory hyperlinkProducerFactory,
			RepositoryService repositoryService,
			JasperPrint jasperPrint, 
			String baseFilename,
			Locale locale,
			String characterEncoding) throws JobExecutionException
	{
		try {
			JRCsvExporter exporter = new JRCsvExporter(getJasperReportsContext());
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			
			boolean close = false;
			OutputStream csvDataOut = csvData.getOutputStream();
			try {
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, characterEncoding);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, csvDataOut);
				
				if(exportParams != null)
					exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, exportParams.getFieldDelimiter());
				
				exporter.exportReport();
				
				close = false;
				csvDataOut.close();

				String fileName = baseFilename + ".csv";
				return new ReportOutput(csvData, ContentResource.TYPE_CSV, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						csvDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
			
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}
	/**
	 * @return Returns the exportParams.
	 */
	public CsvExportParametersBean getExportParams() {
		return exportParams;
	}
	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(CsvExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null)
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), CsvExportParametersBean.PROPERTY_CSV_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
