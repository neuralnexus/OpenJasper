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
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.DocxExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.TxtExportParametersBean;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: TxtReportOutput.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class TxtReportOutput extends AbstractReportOutput
{

	private static final Log log = LogFactory.getLog(TxtReportOutput.class);

	private TxtExportParametersBean exportParams;
	public TxtReportOutput()
	{
	}

	/** 
	 * @see com.jaspersoft.jasperserver.api.engine.scheduling.quartz.Output#getOutput()
	 */
	public ReportOutput getOutput(
			EngineService engineService, 
			ExecutionContext executionContext, 
			String reportUnitURI, 
			DataContainer txtData,
			JRHyperlinkProducerFactory hyperlinkProducerFactory,
			RepositoryService repositoryService,
			JasperPrint jasperPrint, 
			String baseFilename,
			Locale locale,
			String characterEncoding) throws JobExecutionException
	{
		try {
			JRTextExporter exporter = new JRTextExporter(getJasperReportsContext());
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			
			boolean close = false;
			OutputStream txtDataOut = txtData.getOutputStream();
			try {
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, characterEncoding);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, txtDataOut);
				
				if(exportParams != null)
				{
					exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, exportParams.getCharacterHeight());
					exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, exportParams.getCharacterWidth());
					exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, exportParams.getPageHeight());
					exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, exportParams.getPageWidth());
				}
				exporter.exportReport();
				
				close = false;
				txtDataOut.close();

				String fileName = baseFilename + ".txt";
				return new ReportOutput(txtData, ContentResource.TYPE_TXT, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						txtDataOut.close();
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
	public TxtExportParametersBean getExportParams() {
		return exportParams;
	}

	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(TxtExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
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
