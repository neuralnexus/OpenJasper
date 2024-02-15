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
package com.jaspersoft.jasperserver.rest.services;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ListItem;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.OperationResult;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.core.util.XMLUtil;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.services.LegacyRunReportService;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.rest.model.Report;
import com.jaspersoft.jasperserver.ws.xml.Unmarshaller;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Report REST service
 * 
 * @author gtoffoli
 * @version $Id: RESTReport.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("restReportService")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RESTReport extends RESTAbstractService {

    private final static Log log = LogFactory.getLog(RESTReport.class);
    @Resource(name = "legacyRunReportService")
    private LegacyRunReportService runReportService;

    public void setRunReportService(LegacyRunReportService runReportService) {
        this.runReportService = runReportService;
    }

    /**
     * The get method get resources of a produced report
     * Urls in this case look like /report/uniqueidentifier?file=filename
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        // Get the uri of the resource
        String uuid = restUtils.extractRepositoryUri(req.getPathInfo());
        if (uuid.startsWith("/")) uuid = uuid.substring(1);

        // Find the report in session...
            HttpSession session = req.getSession();
            Report report = (Report) session.getAttribute(uuid);

            if (report == null)
            {
                restUtils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, "Report not found (uuid not found in session)");
                return;
            }


            String file = req.getParameter("file");
            if (file != null)
            {
                if (!report.getAttachments().containsKey(file))
                {
                    restUtils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, "Report not found (requested file not available for this report)");
                    return;
                }
                else
                {
                    DataSource ds = (DataSource)report.getAttachments().get(file);
                    restUtils.sendFile(ds, resp);
                    return;
                }
            }
            else
            {
                // Send the report description...
                // Please not that here we may decide to send back a final format based on the accepted
                // content type....
                resp.setContentType("text/xml; charset=UTF-8");
                restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, report.toXml());
            }
    }





    /**
     * The get method get resources of a produced report
     * Urls in this case look like /reports/samples/myreport;uniqueidentifier?file=filename
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        // Get the uri of the resource
        String uuid = restUtils.extractRepositoryUri(req.getPathInfo());

        if (uuid.startsWith("/")) uuid = uuid.substring(1);

        // Add all the options...
        Map<String,String> options = new HashMap<String, String>();
        // Add as option all the GET parameters...
        Enumeration en = req.getParameterNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            options.put(key, req.getParameter(key));
        }

        // Find the report in session...
        HttpSession session = req.getSession();
        Report report = (Report) session.getAttribute(uuid);

        if (report == null)
        {
            restUtils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, "Report not found (uuid not found in session)");
            return;
        }


        JasperPrint jp = report.getJasperPrint();

        // highcharts report for REST v1 is by default noninteractive.
        if(!options.containsKey(Argument.PARAM_INTERACTIVE)){
            options.put(Argument.PARAM_INTERACTIVE, Boolean.FALSE.toString());
        }

        OperationResult or = runReportService.exportReport(report.getOriginalUri(), jp, options, report.getAttachments());
            

        if (or.getReturnCode() != 0)
        {
           restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, or.getMessage());
           return;
        }
        else
        {
            // If the jasperprint is present as attribute, it means it has been modified
            // in some way (i.e. using a RUN_TRANSFORMER_KEY).
            if (runReportService.getAttributes().get("jasperPrint") != null)
            {
                jp = (JasperPrint) runReportService.getAttributes().get("jasperPrint");
                report.setJasperPrint(jp);
            }
            
            // Send out the xml...
            resp.setContentType("text/xml; charset=UTF-8");
            restUtils.setStatusAndBody(HttpServletResponse.SC_CREATED, resp, report.toXml());
        }
    }

    /**
     * This method allows the user to create (run/fill) a new report.
     * 
     *
     * @param req
     * @param resp
     * @throws ServiceException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {

        // We are creating a new report here...
        // Add all the options...
        Map<String,String> options = new HashMap<String, String>();
        // Add as option all the GET parameters...
        Enumeration en = req.getParameterNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            options.put(key, req.getParameter(key));
        }

        Map<String,Object> parameters = new HashMap<String, Object>();

        // We expect the user to send us a ResourceDescriptor with some parameters in it...
        HttpServletRequest mreq = restUtils.extractAttachments(runReportService, req);

        String resourceDescriptorXml = null;

        // get the resource descriptor...
        if (mreq instanceof MultipartHttpServletRequest)
        {
            resourceDescriptorXml = mreq.getParameter(restUtils.REQUEST_PARAMENTER_RD);
        }
        else
        {
            try {
                resourceDescriptorXml = IOUtils.toString(req.getInputStream());
            } catch (IOException ex) {
                throw new ServiceException(ServiceException.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
            }
        }


        if (resourceDescriptorXml == null)
        {
            restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "Missing parameter " + restUtils.REQUEST_PARAMENTER_RD + " " + runReportService.getInputAttachments());
            return;
        }

        // Parse the resource descriptor...
        InputSource is = new InputSource(new StringReader(resourceDescriptorXml));
        Document doc = null;
        ResourceDescriptor rd = null;
        try {
            doc = XMLUtil.getNewDocumentBuilder().parse(is);
            rd = Unmarshaller.readResourceDescriptor(doc.getDocumentElement());
        } catch (SAXException ex) {
            restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "Invalid resource descriptor");
            return;
        } catch (ServiceException se) {
            
            if (se.getErrorCode() == ServiceException.RESOURCE_NOT_FOUND)
            {
                restUtils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, se.getLocalizedMessage());
                throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, se.getMessage());
            }
            else
            {
                throw se;
            }
        } catch (Exception ex) {
            throw new ServiceException(ServiceException.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        }

        // At this point we have a resource descriptor, find the parameters in it and the uri
        String uri = rd.getUriString();
        
        List params = rd.getParameters();
        for (int i=0; i<params.size(); ++i)
        {
            ListItem parameter = (ListItem) params.get(i);
            if (parameter.isIsListItem())
            {
                // Check if a collection exists for this parameter..
                List collection = (List)parameters.get(parameter.getLabel());
                if (collection == null)
                {
                    collection = new ArrayList<String>();
                    parameters.put(parameter.getLabel(), collection);
                }
                collection.add(parameter.getValue());
            }
            else
            {
                parameters.put(parameter.getLabel(), parameter.getValue());
            }
        }

        if (log.isDebugEnabled())
                log.debug("Running report " + uri +" with parameters: " + parameters + " and options: " + options);

        // highcharts report for REST v1 is by default noninteractive.
        if(!options.containsKey(Argument.PARAM_INTERACTIVE)){
            options.put(Argument.PARAM_INTERACTIVE, Boolean.FALSE.toString());
        }
        Map<String, DataSource> attachments = new ConcurrentHashMap<String, DataSource>();

        OperationResult or = runReportService.runReport(uri, parameters, options, attachments);

        if (or.getReturnCode() != 0)
        {
           restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, or.getMessage());
           return;
        }
        else
        {
            JasperPrint jp = (JasperPrint) runReportService.getAttributes().get("jasperPrint");

            // Store the attachments in the session, with proper keys...


            HttpSession session = req.getSession();

            String executionId = UUID.randomUUID().toString();

            Report report = new Report();

            report.setUuid(executionId);
            report.setOriginalUri(uri);
            report.setAttachments(attachments);
            report.setJasperPrint(jp);

            session.setAttribute(report.getUuid(), report);


            // Send out the xml...
            resp.setContentType("text/xml; charset=UTF-8");
            restUtils.setStatusAndBody(HttpServletResponse.SC_CREATED, resp, report.toXml());
        }
    }


}
