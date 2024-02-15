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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.AbstractHtmlExporter;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.WebDeploymentInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.HtmlExportUtil;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.MemoryDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: HtmlReportOutput.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class HtmlReportOutput extends AbstractReportOutput 
{

	private static final Log log = LogFactory.getLog(HtmlReportOutput.class);

    private boolean forceToUseHTMLExporter = false;

	private WebDeploymentInformation deploymentInformation;

	public HtmlReportOutput()
	{
	}

    /*
     * set to enforce to use grid-base exporter for HTML
     * it is good for embedded report in email
     */
    public void setForceToUseHTMLExporter(boolean state) {
        forceToUseHTMLExporter = true;
    }

     /*
     * return whether it is set to use grid-base exporter for HTML
     */
    public boolean isForceToUseHTMLExporter() {
        return forceToUseHTMLExporter;
    }

	/** 
	 * @see com.jaspersoft.jasperserver.api.engine.scheduling.quartz.Output#getOutput()
	 */
	public ReportOutput getOutput(
			EngineService engineService, 
			ExecutionContext executionContext, 
			String reportUnitURI, 
			DataContainer htmlData,
			JRHyperlinkProducerFactory hyperlinkProducerFactory,
			RepositoryService repositoryService,
			JasperPrint jasperPrint, 
			String baseFilename,
			Locale locale,
			String characterEncoding) throws JobExecutionException
	{
		try {
			String filename = baseFilename + ".html";
			String childrenFolderName = null;

            if (repositoryService != null) childrenFolderName = repositoryService.getChildrenFolderName(filename);
			else childrenFolderName = "";
            AbstractHtmlExporter exporter = null;

            if (isForceToUseHTMLExporter()) {
                // enforce to use grid-base exporter (only use for embedded report in email)
                exporter = new HtmlExporter();
            } else {
                exporter = HtmlExportUtil.getHtmlExporter(getJasperReportsContext());
            }
		    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, characterEncoding);

			HttpServletRequest proxy = (HttpServletRequest) Proxy.newProxyInstance(this.getClass().getClassLoader(),
					new Class<?>[]{HttpServletRequest.class}, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Object result = null;
					if ("getContextPath".equals(method.getName())) {
						result = deploymentInformation.getDeploymentURI();
					}
					return result;
				}
			});
			exporter.setParameter(new JRHtmlExporterParameter("HttpServletRequest"), proxy);

			boolean close = true;
			OutputStream htmlDataOut = htmlData.getOutputStream();
			
			try {
				ReportOutput htmlOutput = new ReportOutput(htmlData,
						ContentResource.TYPE_HTML, filename);

				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, htmlDataOut);
				if (!childrenFolderName.equals("")) {
                    exporter.setImageHandler(new RepoHtmlResourceHandler(htmlOutput, childrenFolderName + "/{0}"));
                	exporter.setResourceHandler(new RepoHtmlResourceHandler(htmlOutput, null));
                	exporter.setFontHandler(new RepoHtmlResourceHandler(htmlOutput, childrenFolderName + "/{0}"));
                } else {
                    exporter.setImageHandler(new RepoHtmlResourceHandler(htmlOutput, childrenFolderName + "{0}"));
                	exporter.setResourceHandler(new RepoHtmlResourceHandler(htmlOutput, null));
                	exporter.setFontHandler(new RepoHtmlResourceHandler(htmlOutput, childrenFolderName + "{0}"));
                }
				if (hyperlinkProducerFactory != null) {
					exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, hyperlinkProducerFactory);
				}
				
				exporter.exportReport();

				close = false;
				htmlDataOut.close();

				return htmlOutput;
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						htmlDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	public WebDeploymentInformation getDeploymentInformation() {
		return deploymentInformation;
	}

	public void setDeploymentInformation(
			WebDeploymentInformation deploymentInformation) {
		this.deploymentInformation = deploymentInformation;
	}

}

class RepoHtmlResourceHandler implements HtmlResourceHandler
{
	private ReportOutput htmlOutput;
	private String pathPattern;
	
	protected RepoHtmlResourceHandler(ReportOutput htmlOutput, String pathPattern)
	{
		this.htmlOutput = htmlOutput;
		this.pathPattern = pathPattern;
	}

	@Override
	public String getResourcePath(String id) 
	{
		if (pathPattern == null)
		{
			return id;
		}
		return MessageFormat.format(pathPattern, new Object[]{id});
	}

	@Override
	public void handleResource(String id, byte[] data) 
	{
		MemoryDataContainer imageDataContainer = new MemoryDataContainer(data);
		ReportOutput resource = new ReportOutput(imageDataContainer,
				ContentResource.TYPE_IMAGE, id);
		htmlOutput.addChild(resource);
	}
	
}
