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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.services.ResourcesManagementRemoteService;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.rest.RESTServlet;
import com.jaspersoft.jasperserver.ws.axis2.scheduling.ReportSchedulerService;
import com.jaspersoft.jasperserver.ws.scheduling.JobSummary;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;

/**
 * @author carbiv
 * @version $Id: RESTJobSummary.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("restJobSummarySchedService")
public class RESTJobSummary extends RESTAbstractService {
    private final static Log log = LogFactory.getLog(RESTServlet.class);

    @javax.annotation.Resource(name = "concreteReportSchedulerService")
    private ReportSchedulerService reportSchedulerService;
    @javax.annotation.Resource
    private ResourcesManagementRemoteService resourcesManagementRemoteService;

    public void setResourcesManagementRemoteService(ResourcesManagementRemoteService resourcesManagementRemoteService) {
        this.resourcesManagementRemoteService = resourcesManagementRemoteService;
    }

    public ResourcesManagementRemoteService getResourcesManagementRemoteService() {
        return resourcesManagementRemoteService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        // Get the uri of the resource
        String url = restUtils.extractRepositoryUri(req.getPathInfo());
        if (log.isDebugEnabled()) {
            log.debug("getting job summary for " + url);
        }
        if (url == null) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "malformed url: " + url);
        }


        if (isValidUri(url)) {
            JobSummary[] summaries = new JobSummary[0];
            try {
                summaries = reportSchedulerService.getReportJobs(url);
            } catch (AxisFault axisFault) {
                throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, "could not locate jobs for report in uri: " + url + axisFault.getLocalizedMessage());
            }

            if (summaries == null) {
                restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("found " + summaries.length + "job summaries for report: " + req.getPathInfo());
                }
                restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, generateSummeryReport(summaries));
            }
        } else
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "bad report uri");
    }

    private String generateSummeryReport(JobSummary[] summaries) throws ServiceException{
        try{
            StringWriter sw = new StringWriter();

            sw.append("<jobs>");


            for (int i=0 ; i<summaries.length ; i++){
                restUtils.getMarshaller(JobSummary.class).marshal(summaries[i], sw);
                if (log.isDebugEnabled()) {
                    log.debug("finished marshaling job: "+summaries[i].getId());
                }
            }
            sw.append("</jobs>");
            return sw.toString();
        }
        catch (JAXBException e) {
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    protected boolean isValidUri(String reportUri){
        Resource resource= resourcesManagementRemoteService.locateResource(reportUri);
        return resource!=null && resource.getResourceType().equals(ReportUnit.class.getName());
    }

    public void setReportSchedulerService(ReportSchedulerService reportSchedulerService) {
        this.reportSchedulerService = reportSchedulerService;
    }
}
