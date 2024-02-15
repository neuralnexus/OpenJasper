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
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.core.util.XMLUtil;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.handlers.FileResourceHandler;
import com.jaspersoft.jasperserver.remote.services.LegacyRunReportService;
import com.jaspersoft.jasperserver.remote.services.ResourcesManagementRemoteService;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.ws.xml.Marshaller;
import com.jaspersoft.jasperserver.ws.xml.Unmarshaller;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the logic to manage the repository resources.
 * It provides support for the HTTP verbs GET, POST, PUT and DELETE.
 *
 * @author gtoffoli
 * @version $Id: RESTResource.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("restResourceService")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RESTResource extends RESTAbstractService {

    private final static Log log = LogFactory.getLog(RESTResource.class);
    @Resource
    private ResourcesManagementRemoteService resourcesManagementRemoteService;
    @Resource(name = "legacyRunReportService")
    private LegacyRunReportService runReportService;

    public void setResourcesManagementRemoteService(ResourcesManagementRemoteService resourcesManagementRemoteService) {
        this.resourcesManagementRemoteService = resourcesManagementRemoteService;
    }

    public void setRunReportService(LegacyRunReportService runReportService) {
        this.runReportService = runReportService;
    }

    /**
     * Get a resource based of the requested URI.
     * If the parameter file is set, it will be used to retrieve the content of the file.
     * 
     * @param req
     * @param resp
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {

            // Get the uri of the resource
            String uri = restUtils.extractRepositoryUri(req.getPathInfo());
        if (!validParameters(req.getParameterMap().keySet())){
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "request contains unsupported parameters");
        }


            // by default get the root...
            if (uri == null || uri.length() == 0) uri="/";

            // get the resources....
            // Add all the options...
            Map<String,Object> options = new HashMap<String, Object>();

            // This options allow to get the informations about an input control
            // including the data to fill the input control....
            if (req.getParameter(Argument.IC_GET_QUERY_DATA) != null)
            {
                options.put(Argument.IC_GET_QUERY_DATA, req.getParameter(Argument.IC_GET_QUERY_DATA));

                // Extract parameters
                Map<String, Object> parameters = restUtils.extractParameters(req);

                // Add the parsed parameters to the options map (only if it makes sense)
                if (parameters.size() > 0)
                {
                    options.put(Argument.PARAMS_ARG, parameters);
                }
            }

            options.put(restUtils.SWITCH_PARAM_GET_LOCAL_RESOURCE, restUtils.isLocalResource(uri) && req.getParameterMap().containsKey(restUtils.FILE_DATA));
            if (log.isDebugEnabled()) {
                log.debug("adding the local resource with data flag");
            }

            ResourceDescriptor rd = null;
            try {
               rd = resourcesManagementRemoteService.getResource(uri, options);
            } catch (ServiceException ex)
            {
                throw ex;
            } catch (Exception ex)
            {
               throw new ServiceException(ex.getMessage());
            }

            // This check should not be useful, since an execption should have been thrown by service.getReporse
            if (rd == null)
            {
                if (log.isDebugEnabled()) {
                    log.debug("Could not find resource: "+ uri);
                }
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "Could not find resource: "+ uri);
            }

            // If the client specified a specific file, return the file
            if (Boolean.parseBoolean(req.getParameter(restUtils.FILE_DATA)))
            {
                if (runReportService.getReportAttachments(rd.getUriString()).get(rd.getUriString())!=null)
                    restUtils.sendFile(runReportService.getReportAttachments(rd.getUriString()).get(rd.getUriString()), resp);
                else
                    if (runReportService.getReportAttachments(rd.getUriString()).get(FileResourceHandler.MAIN_ATTACHMENT_ID)!=null)
                        restUtils.sendFile(runReportService.getReportAttachments(rd.getUriString()).get(FileResourceHandler.MAIN_ATTACHMENT_ID), resp);
                return;

            }
            else // else return the resource descriptor
            {

                Marshaller m = new Marshaller();
                String xml = m.writeResourceDescriptor(rd);

                resp.setContentType("text/xml");
                restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, xml);
            }
            return;
        }

    private boolean validParameters(Set<String> set) throws ServiceException {
        for (String key:set){
            if (!key.equals(restUtils.FILE_DATA) && !key.equals(Argument.IC_GET_QUERY_DATA) && !key.startsWith("P_") && !key.startsWith("PL_")){
                if (log.isDebugEnabled()) {
                    log.debug("request contains unsupported parameters: " + key);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * The PUT service is used to create a new resource in the repository...
     *
     * @param req
     * @param resp
     * @throws ServiceException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {

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
            if (log.isDebugEnabled()) {
                log.debug("resource descriptor was created successfully for: "+ rd.getUriString());
            }

            // we force the rd to be new...
            rd.setIsNew(true);

            ResourceDescriptor createdRd = resourcesManagementRemoteService.putResource(rd);

            Marshaller m = new Marshaller();
            String xml = m.writeResourceDescriptor(createdRd);
            // send the xml...
            restUtils.setStatusAndBody(HttpServletResponse.SC_CREATED, resp, "");

        } catch (SAXException ex) {
            log.error("Unexpected error during resource descriptor marshaling: " + ex.getMessage(), ex);
            restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "Invalid resource descriptor");
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during resource save: " + ex.getMessage(), ex);
            throw new ServiceException(ServiceException.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        }

    }

    /**
     * POST can be used to modify a resource or to copy/move it.
     *
     * @param req
     * @param resp
     * @throws ServiceException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        String sourceURI = restUtils.extractRepositoryUri(req.getPathInfo());
        String destURI = restUtils.getDetinationUri(req);
        if (destURI!=null)
        {
            if (req.getParameterMap().containsKey(restUtils.REQUEST_PARAMENTER_COPY_TO))
                resourcesManagementRemoteService.copyResource(sourceURI, destURI);
            else
                resourcesManagementRemoteService.moveResource(sourceURI, destURI);
        }
        else // Modify the resource...
        {
            HttpServletRequest mreq = restUtils.extractAttachments(runReportService, req);
            String resourceDescriptorXml = null;

            // get the resource descriptor...
            if (mreq instanceof MultipartHttpServletRequest)
                resourceDescriptorXml = mreq.getParameter(restUtils.REQUEST_PARAMENTER_RD);
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
                String msg = "Missing parameter " + restUtils.REQUEST_PARAMENTER_RD + " " + runReportService.getInputAttachments();
                restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, msg);
                throw new ServiceException(ServiceException.INTERNAL_SERVER_ERROR, msg);
            }

            // Parse the resource descriptor...
            InputSource is = new InputSource(new StringReader(resourceDescriptorXml));
            Document doc = null;
            ResourceDescriptor rd = null;
            try {
                doc = XMLUtil.getNewDocumentBuilder().parse(is);
                rd = Unmarshaller.readResourceDescriptor(doc.getDocumentElement());

                // we force the rd to be new...
                rd.setIsNew(false);

                if (rd.getUriString() == null || !rd.getUriString().equals(sourceURI))
                {
                    throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "Request URI and descriptor URI are not equals");
                }
                resourcesManagementRemoteService.updateResource(sourceURI, rd, true);

                restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");

            } catch (SAXException ex) {
                log.error("error parsing...", ex);
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (ServiceException ex) {
                log.error("error executing the service...", ex);
                throw ex;
            } catch (ParserConfigurationException e) {
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (IOException e) {
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        String uri = restUtils.extractRepositoryUri(req.getPathInfo());
        resourcesManagementRemoteService.deleteResource(uri);
        resp.setStatus(HttpServletResponse.SC_OK);
    }





}
