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
package com.jaspersoft.jasperserver.war.tags;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import com.jaspersoft.jasperserver.api.SessionAttribMissingException;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.export.AbstractHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.engine.export.JsonExporter;
import net.sf.jasperreports.engine.export.type.ZoomTypeEnum;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;
import net.sf.jasperreports.web.servlets.ReportExecutionStatus;
import net.sf.jasperreports.web.servlets.ReportPageStatus;
import net.sf.jasperreports.web.util.JacksonUtil;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import net.sf.jasperreports.web.util.WebUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider.SnapshotSaveStatus;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.HtmlExportUtil;
import com.jaspersoft.jasperserver.war.action.ReportExecutionController;
import com.jaspersoft.jasperserver.war.action.ReportParametersAction;
import com.jaspersoft.jasperserver.war.action.WebflowReportContext;
import com.jaspersoft.jasperserver.war.action.WebflowReportContextAccessor;
import com.jaspersoft.jasperserver.war.action.hyperlinks.HyperlinkProducerFactoryFlowFactory;
import com.jaspersoft.jasperserver.war.util.JRHtmlExportUtils;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;


/**
 * renderJsp parameter allows override of default output format for HTML controls
 * 
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: JasperViewerTag.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JasperViewerTag extends RequestContextAwareTag
{
	protected static final Log log = LogFactory.getLog(JasperViewerTag.class);
	
	public static final String DEFAULT_RENDER_JSP = "/WEB-INF/jsp/modules/viewReport/DefaultJasperViewer.jsp";
	public static final String DEFAULT_JASPER_PRINT_ATTRIBUTE = "jasperPrint";
	public static final String DEFAULT_PAGE_INDEX_ATTRIBUTE = "pageIndex";
	public static final String DEFAULT_LINK_PRODUCER_FACTORY_ATTRIBUTE = "hyperlinkHandlerFactory";
	public static final String EMPTY_REPORT_ATTRIBUTE = "emptyReport";
	public static final String INNER_PAGINATION_ATTRIBUTE = "innerPagination";
    public static final String IGNORE_PAGE_MARGINS ="ignorePageMargins";
    public static final String MESSAGE_SOURCE ="messageSource";

	protected static final String JASPER_PRINT_ACCESSOR_BEAN_NAME = "jasperPrintAccessor";
	protected static final String REPORT_CONTEXT_ACCESSOR_BEAN_NAME = "reportContextWebAccessor";
	protected static final String DATA_CACHE_PROVIDER_BEAN_NAME = "engineServiceDataCacheProvider";

	private String imageServlet;
	private String resourceServlet;
	private String page;
	private String renderJsp;
	private String providedExporterClassName;
	private boolean innerPagination;
    private boolean ignorePageMargins;
	private Map exporterParameters;
	private String jasperPrintAttribute = DEFAULT_JASPER_PRINT_ATTRIBUTE;
	private String pageIndexAttribute = DEFAULT_PAGE_INDEX_ATTRIBUTE;
	private String linkProducerFactoryAttribute = DEFAULT_LINK_PRODUCER_FACTORY_ATTRIBUTE;
	private Map reportContextMap;
	

	protected int doStartTagInternal() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

		try {
			ReportUnitResult reportResult = getReportResult(request);
			JasperPrintAccessor printAccessor = reportResult.getJasperPrintAccessor();
			if (printAccessor == null) {
				log.error("There is no JasperPrint object cannot be accessed.");
				return EVAL_PAGE;
			}
			
			setReportContextAttributes(request, reportResult);
            
			Integer pageIndex = (Integer) request.getAttribute(getPageIndexAttribute());
			if (pageIndex == null) {
				pageIndex = new Integer(0);
			}
			
			ReportExecutionStatus reportStatus = printAccessor.getReportStatus();
			
			// get the status of the page, waiting for the page to be available in necessary
			ReportPageStatus pageStatus = printAccessor.pageStatus(pageIndex.intValue(), null);
			
			request.setAttribute("page", page);
			request.setAttribute("dataTimestamp", reportResult.getDataTimestamp());

			request.setAttribute(INNER_PAGINATION_ATTRIBUTE, innerPagination);
			request.setAttribute(IGNORE_PAGE_MARGINS, ignorePageMargins);
			
			Integer totalPageCount = reportStatus.getTotalPageCount();
			// set the total page count if known
			if (totalPageCount != null) {
				request.setAttribute("lastPageIndex", new Integer(totalPageCount - 1));
				
				//FIXME this duplicates logic in ReportExecutionController.viewReportPageUpdateCheck
				SnapshotSaveStatus snapshotSaveStatus = getDataCacheProvider().getSnapshotSaveStatus(
						reportResult.getReportContext());
				if (snapshotSaveStatus != null) {
					request.setAttribute("snapshotSaveStatus", snapshotSaveStatus.toString());
				}
			}
			
			// set the partial page count
			request.setAttribute("lastPartialPageIndex", new Integer(reportStatus.getCurrentPageCount() - 1));
			
			// if the page count is null, it means that the fill is not yet done but there is at least a page
			boolean emptyReport = totalPageCount != null && totalPageCount.intValue() == 0;
			request.setAttribute(EMPTY_REPORT_ATTRIBUTE, Boolean.valueOf(emptyReport));

            ApplicationContext applicationContext = getRequestContext().getWebApplicationContext();
            request.setAttribute(MESSAGE_SOURCE, applicationContext.getBean("messageSource"));

            if (!emptyReport) {
            	if (!pageStatus.pageExists()) {
            		// setting a header as there's no simple way currently to detect a specific exception in the client
            		response.setHeader("reportPageNonExisting", "true");
            		throw new JSException("jsexception.view.page.does.not.exist", new Object[]{pageIndex});
            	}
            	
				request.setAttribute("pageIndex", pageIndex);
				
				if (!pageStatus.isPageFinal()) {
					request.setAttribute("pageTimestamp", pageStatus.getTimestamp());
				}
				
	            JasperReportsContext jasperReportsContext = getJasperReportsContext(applicationContext);
				
	            AbstractHtmlExporter exporter = HtmlExportUtil.getHtmlExporter(jasperReportsContext);
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, printAccessor.getJasperPrint());
				exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex);
				exporter.setImageHandler(new WebHtmlResourceHandler(response.encodeURL(imageServlet + "image={0}")));
				exporter.setResourceHandler(new WebHtmlResourceHandler(response.encodeURL(resourceServlet + "/{0}")));
				exporter.setFontHandler(new WebHtmlResourceHandler(response.encodeURL(resourceServlet + "&font={0}")));

				StringBuffer htmlHeader = new StringBuffer();
                String contextPath = request.getContextPath();

                // fix for Bug 26294 - [case #24473 +1] Problem with IE8 and IE9 when embedding app into iframe
                String referer = request.getHeader("Referer");
                referer = referer.substring(0, referer.indexOf(contextPath) + contextPath.length());
                // end fix
				exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, htmlHeader.toString());
				
				exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
				exporter.setParameter(JRHtmlExportUtils.PARAMETER_HTTP_REQUEST, request);
				exporter.setParameter(JRExporterParameter.IGNORE_PAGE_MARGINS, ignorePageMargins);
                WebflowReportContext webflowReportContext = getReportContextAccessor().getContext(request, reportContextMap);
                webflowReportContext.setParameterValue("net.sf.jasperreports.web.app.context.path", contextPath);//FIXMEJIVE use constant
//              webflowReportContext.setParameterValue(JRParameter.REPORT_CONTEXT, webflowReportContext);
				exporter.setReportContext(webflowReportContext);
                // hide the preview toolbar if view as dashboard frame
                if ((webflowReportContext.getParameterValue(ReportParametersAction.VIEW_AS_DASHBOARD_FRAME) != null) &&
                    webflowReportContext.getParameterValue(ReportParametersAction.VIEW_AS_DASHBOARD_FRAME).toString().equalsIgnoreCase("true")) {
                    request.setAttribute(ReportParametersAction.VIEW_AS_DASHBOARD_FRAME, "true");
                }
				HyperlinkProducerFactoryFlowFactory linkProducerFactory = (HyperlinkProducerFactoryFlowFactory) request.getAttribute(getLinkProducerFactoryAttribute());
				if (linkProducerFactory != null) {
					JRHyperlinkProducerFactory hyperlinkProducerFactory = linkProducerFactory.getHyperlinkProducerFactory(request, response);
					exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, hyperlinkProducerFactory);
				}

/*
 * Future enhancement
 * 
 * 				JRExporter exporter = (providedExporterClassName != null)
									? defaultExporter(jasperPrint, pageIndex) 
									: providedExporter(jasperPrint, pageIndex);
				setParameters(exporter);
*/			
				request.setAttribute("exporter", exporter);

                boolean isComponentMetadataEmbedded = WebUtil.getInstance(jasperReportsContext).isComponentMetadataEmbedded();
                request.setAttribute("isComponentMetadataEmbedded", isComponentMetadataEmbedded);
                if (isComponentMetadataEmbedded) {
                    JsonExporter jsonExporter = new JsonExporter(jasperReportsContext);
                    jsonExporter.setReportContext(webflowReportContext);
                    jsonExporter.setParameter(JRExporterParameter.JASPER_PRINT, printAccessor.getJasperPrint());
                    request.setAttribute("jsonExporter", jsonExporter);
                }

                // set report status on response header
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("reportStatus", reportStatus.getStatus().toString().toLowerCase());
                result.put("totalPages", reportStatus.getTotalPageCount());
                result.put("partialPageCount", reportStatus.getCurrentPageCount());
                result.put("pageFinal", pageStatus.isPageFinal());
                result.put("contextid", webflowReportContext.getId());
                result.put("isComponentMetadataEmbedded", isComponentMetadataEmbedded);
                result.put("jasperPrintName", request.getAttribute("jasperPrintName"));
                if (!pageStatus.isPageFinal()) {
                    result.put("pageTimestamp", String.valueOf(pageStatus.getTimestamp()));
                }

				String defaultZoomProperty = printAccessor.getJasperPrint().getProperty("net.sf.jasperreports.viewer.zoom"); // FIXME: use constant
				if (defaultZoomProperty != null) {
					ZoomTypeEnum zoomType = ZoomTypeEnum.getByName(defaultZoomProperty);
					result.put("defaultZoom", zoomType != null ? zoomType.getHtmlValue() : defaultZoomProperty);
				}

				response.setHeader("jasperreports-report-status", JacksonUtil.getInstance(jasperReportsContext).getJsonString(result));
			}
			
			BodyContent nestedContent = pageContext.pushBody();
			boolean popped = false;
			try {
				pageContext.include(getRenderJsp());
                popped = true;
				pageContext.popBody();
				nestedContent.writeOut(pageContext.getOut());
			} finally {
				if (!popped) {
					pageContext.popBody();
				}
			}

		} catch (Exception e) {
            if (e instanceof SessionAttribMissingException) {
                throw new SessionAttribMissingException(e.getMessage(), ((SessionAttribMissingException) e).getArgs());
            } else {
                throw new JspException(e);
            }
		}

		return EVAL_PAGE;
	}

	protected JasperReportsContext getJasperReportsContext(ApplicationContext applicationContext) {
		Properties springConfiguration = applicationContext.getBean("springConfiguration", Properties.class);
		String jasperReportsContextBeanName = springConfiguration.getProperty("bean.jasperReportsContext");
		JasperReportsContext jasperReportsContext = applicationContext.getBean(jasperReportsContextBeanName, JasperReportsContext.class);
		return jasperReportsContext;
	}
	
	protected ReportUnitResult getReportResult(HttpServletRequest request) {
		SessionObjectSerieAccessor jasperPrintAccessor = getJasperPrintAccessor();
		ReportUnitResult result = (ReportUnitResult) jasperPrintAccessor.getObject(request, getJasperPrintAttribute());
		if (result == null) {
			throw new JSException("jsexception.jasperprint.not.found", new Object[] {getJasperPrintAttribute()});
		}
		return result;
	}

	protected void setReportContextAttributes(HttpServletRequest request, ReportUnitResult reportResult) {
		ReportContext reportContext = reportResult.getReportContext();
		if (reportContext == null) {
			return;
		}
		
		// we need this for ReportExecutionController.getReportComponents
		String flowExecutionKey = (String) request.getAttribute("flowExecutionKey");
		reportContext.setParameterValue(ReportExecutionController.REPORT_CONTEXT_HTML_PRINT_ID, jasperPrintAttribute);
		reportContext.setParameterValue(ReportExecutionController.REPORT_CONTEXT_HTML_FLOW_KEY, flowExecutionKey);
	}

	protected SessionObjectSerieAccessor getJasperPrintAccessor() {
		WebApplicationContext applicationContext = getRequestContext().getWebApplicationContext();
		SessionObjectSerieAccessor jasperPrintAccessor = (SessionObjectSerieAccessor) applicationContext.getBean(
				JASPER_PRINT_ACCESSOR_BEAN_NAME,
				SessionObjectSerieAccessor.class);
		return jasperPrintAccessor;
	}

	protected WebflowReportContextAccessor getReportContextAccessor() {
		WebApplicationContext applicationContext = getRequestContext().getWebApplicationContext();
		WebflowReportContextAccessor contextAccessor = (WebflowReportContextAccessor) applicationContext.getBean(
				REPORT_CONTEXT_ACCESSOR_BEAN_NAME,
				WebflowReportContextAccessor.class);
		return contextAccessor;
	}

	protected DataCacheProvider getDataCacheProvider() {
		WebApplicationContext applicationContext = getRequestContext().getWebApplicationContext();
		DataCacheProvider dataCacheProvider = applicationContext.getBean(
				DATA_CACHE_PROVIDER_BEAN_NAME, DataCacheProvider.class);
		return dataCacheProvider;
	}

	public String getImageServlet()
	{
		return imageServlet;
	}

	public void setImageServlet(String imageServlet)
	{
		this.imageServlet = imageServlet;
	}

	public String getResourceServlet()
	{
		return resourceServlet;
	}

	public void setResourceServlet(String resourceServlet)
	{
		this.resourceServlet = resourceServlet;
	}

	public void setInnerPagination(String innerPagination)
	{
		this.innerPagination = innerPagination.equals("true");
	}

	public void setReportContext(Map reportContextMap)
	{
		this.reportContextMap = reportContextMap;
	}

    public void setIgnorePageMargins(String ignorePageMargins){
        boolean ignore = false;
        if(ignorePageMargins != null && ignorePageMargins.length() > 0){
            ignore = ignorePageMargins.equalsIgnoreCase("true");
        }
        this.ignorePageMargins = ignore;
    }


	public String getPage()
	{
		return page;
	}

	public void setPage(String page)//FIXME used?
	{
		this.page = page;
	}

	public String getRenderJsp() {
		if (renderJsp == null || renderJsp.trim().length() == 0) {
			return DEFAULT_RENDER_JSP;
		}
		return renderJsp;
	}

	public void setRenderJsp(String renderJsp) {
		this.renderJsp = renderJsp;
	}

	public String getExporterClassName()
	{
		return providedExporterClassName;
	}

	public void setExporterClassName(String exporterClassName)
	{
		this.providedExporterClassName = exporterClassName;
	}
	
	/**
	 * @return Returns the exporterParameters.
	 */
	public Map getExporterParameters()
	{
		return exporterParameters;
	}

	/**
	 * @param exporterParameters The exporterParameters to set.
	 */
	public void setExporterParameters(Map exporterParameters)
	{
		this.exporterParameters = exporterParameters;
	}

//	private JRExporter defaultExporter(JasperPrint jasperPrint, Integer pageIndex) {
//		JRHtmlExporter exporter = new JRHtmlExporter();
//		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//		exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex);
//		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imageServlet + "?image=");
//		exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
//		exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
//		return exporter;
//	}
	
//	private JRExporter providedExporter(JasperPrint jasperPrint, Integer pageIndex) throws Exception {
//		Class exporterClass = Class.forName(providedExporterClassName);
//		
//		JRExporter exporter = (JRExporter) exporterClass.newInstance();
//		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//		exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex);
//		return exporter;
//	}
	
	private void setParameters(JRExporter exporter) {
		if (exporterParameters == null || exporterParameters.size() == 0) {
			return;
		}
		Iterator it =  exporterParameters.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			exporter.setParameter((JRExporterParameter) entry.getKey(), entry.getValue());
		}
	}

	public String getJasperPrintAttribute() {
		return jasperPrintAttribute;
	}

	public void setJasperPrintAttribute(String jasperPrintAccessorAttribute) {
		this.jasperPrintAttribute = jasperPrintAccessorAttribute;
	}

	public String getPageIndexAttribute() {
		return pageIndexAttribute;
	}

	public void setPageIndexAttribute(String pageIndexAttribute) {
		this.pageIndexAttribute = pageIndexAttribute;
	}

	public String getLinkProducerFactoryAttribute() {
		return linkProducerFactoryAttribute;
	}

	public void setLinkProducerFactoryAttribute(String linkHandlerFactoryAttribute) {
		this.linkProducerFactoryAttribute = linkHandlerFactoryAttribute;
	}
}
