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

import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.ws.axis2.scheduling.ReportSchedulerService;
import com.jaspersoft.jasperserver.ws.scheduling.Job;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author carbiv
 * @version $Id: RESTJob.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("restJobSchedService")
public class RESTJob extends RESTAbstractService {
    private final static Log log = LogFactory.getLog(RESTJob.class);
    private final static int DISREGARD_VERSION = -1;
    @Resource(name = "concreteReportSchedulerService")
    private ReportSchedulerService reportSchedulerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        try {
            // Get the uri of the resource
            long jobId = getJobId(restUtils.extractRepositoryUri(req.getPathInfo()));

            // get the resources....
            Job job = reportSchedulerService.getJob(jobId);


            StringWriter sw = new StringWriter();
            // create JAXB context and instantiate marshaller

            restUtils.getMarshaller(Job.class).marshal(job, sw);

            restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, sw.toString());

        }
        catch (JAXBException e) {
            throw new ServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, axisFault.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        // Get the uri of the resource
        long jobId = getJobId(restUtils.extractRepositoryUri(req.getPathInfo()));

        // get the resources....
        try {
            reportSchedulerService.deleteJob(jobId);
        } catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, axisFault.getMessage());
        }


        String xml = null; //m.writeResourceDescriptor(job);
        // send the xml...
        restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        try {
            StringWriter sw = new StringWriter();
            Job job = restUtils.unmarshal(Job.class, req.getInputStream());


            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                job.setUsername(userDetails.getUsername());
            }
            try {

                job = reportSchedulerService.scheduleJob(job);
            } catch (AxisFault axisFault) {
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "could not schedule job to report: " + job.getReportUnitURI() + ". check job parameters\n" + axisFault.getMessage());
            }

            restUtils.getMarshaller(Job.class).marshal(job, sw);
            restUtils.setStatusAndBody(HttpServletResponse.SC_CREATED, resp, sw.toString()); // job is a unique case where we return the descriptor

        }catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, axisFault.getMessage());
        } catch (IOException e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (JAXBException e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "please check the request job descriptor");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException{
        try {
            long jobId = getJobId(restUtils.extractRepositoryUri(req.getPathInfo()));

            Job job = restUtils.unmarshal(Job.class, req.getInputStream());

            if ( job.getId()!= jobId ){
                if (log.isDebugEnabled()) {
                    log.debug("job descriptor id "+ job.getId() +" and request id "+ jobId +"do not match. assigning the request id to as the job id");
                }
                job.setId(jobId);
            }

            if (job.getVersion()==DISREGARD_VERSION && reportSchedulerService.getJob(jobId)!=null){
                Job oldJob = reportSchedulerService.getJob(jobId);
                job.setVersion(oldJob.getVersion());

            }
            reportSchedulerService.updateJob(job);
        }catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, axisFault.getMessage());
        }catch (IOException e) {
            throw new ServiceException(ServiceException.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JAXBException e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "please check the request job descriptor");
        }

    }

    private long getJobId(String uri) throws ServiceException{
        String jobId;
        if ( uri == null ){
            throw new ServiceException(ServiceException.RESOURCE_BAD_REQUEST, "malformed input parameter");
        }

        else {
            jobId = uri.replace("/", "");
            return Long.parseLong(jobId);
        }
    }

    public ReportSchedulerService getReportSchedulerService() {
        return reportSchedulerService;
    }

    public void setReportSchedulerService(ReportSchedulerService reportSchedulerService) {
        this.reportSchedulerService = reportSchedulerService;
    }
}
