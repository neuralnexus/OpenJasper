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

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.services.ResourcesListRemoteService;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.ws.xml.Marshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Resources REST implementation performs a list of resource starting from the uri specified.
 * Based on the parameters, this class add several filters to a list that is then passed
 * to the ResourcesListRemoteService to list the resource.
 *
 * To the client is sent a flat list of ResourceDescriptors in the tag resourceDescriptors.
 *
 * @author gtoffoli
 * @version $Id: RESTResources.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("restResourcesService")
public class RESTResources extends RESTAbstractService {

    private final static Log log = LogFactory.getLog(RESTResources.class);
    @Resource
    private ResourcesListRemoteService resourcesListRemoteService;

    public void setResourcesListRemoteService(ResourcesListRemoteService resourcesListRemoteService) {
        this.resourcesListRemoteService = resourcesListRemoteService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {

        // Extract the uri from which list the resources...
        String uri = restUtils.extractRepositoryUri(req.getPathInfo());

        // "resources" is a REST service path
        if (uri == null || "resources".equals(uri))
            uri = "/";
        
        
        // Extract the criteria
        String queryString = req.getParameter("q");
        if (queryString == null || queryString.length() == 0) {
            queryString = null;
        }
        List<String> resourceTypes = new ArrayList<String>();

        String[] resourceTypesArray = req.getParameterValues("type");
        if (resourceTypesArray != null) {
            resourceTypes.addAll(Arrays.asList(resourceTypesArray));
        }
        
        Iterator<String> itr = resourceTypes.iterator();
        while(itr.hasNext()) {
            String resourceType = itr.next();
            if (!(resourceType != null && resourceType.length() != 0)) {
                itr.remove();
            }
        }

        int limit = 0;
        
        if (req.getParameter("limit") != null && req.getParameter("limit").length() > 0)
        {
            try {
                limit = Integer.parseInt(req.getParameter("limit"));
                if (limit <0) throw new Exception();
            } catch (Throwable ex)
            {
                restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "Invalid value set for parameter limit.");
                return;
            }
        }
        int startIndex = 0;

        if (req.getParameter("startIndex") != null && req.getParameter("startIndex").length() > 0)
        {
            try {
                startIndex = Integer.parseInt(req.getParameter("startIndex"));
                if (startIndex <0) throw new Exception();
            } catch (Throwable ex)
            {
                restUtils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "Invalid value set for parameter startIndex.");
                return;
            }
        }
        
        List list = new ArrayList();
        
        // If not a search, just list the content of the specified folder...
        if(queryString == null && (resourceTypes.size() == 0))
        {
            list = resourcesListRemoteService.listResources( uri, limit );
        }
        else
        {
        
            boolean recursive = false;
            if (req.getParameter("recursive") != null &&
                (req.getParameter("recursive").equals("1") ||
                 req.getParameter("recursive").equalsIgnoreCase("true") ||
                 req.getParameter("recursive").equalsIgnoreCase("yes")))
            {
                recursive = true;
            }
        
        
            //list = service.getResources(criteria, limit, resourceTypes == null ? null : Arrays.asList(resourceTypes));
            list = resourcesListRemoteService.getResources(uri, queryString, resourceTypes.size() == 0 ? null : resourceTypes, recursive,limit, startIndex);
        
        }
        
        StringBuilder xml = new StringBuilder();
        
        Marshaller m = new Marshaller();
        xml.append("<resourceDescriptors>\n");
        if (list != null && !list.isEmpty())
            for (Object rd : list) {
                // we assume the objects are actually resource descriptors...
                xml.append(m.writeResourceDescriptor((ResourceDescriptor) rd));
            }
        
        xml.append("</resourceDescriptors>");
        
        restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, xml.toString());
    }

}
