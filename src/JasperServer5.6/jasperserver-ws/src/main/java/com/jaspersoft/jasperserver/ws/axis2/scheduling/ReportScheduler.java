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

package com.jaspersoft.jasperserver.ws.axis2.scheduling;

import org.apache.axis.AxisFault;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.jaspersoft.jasperserver.ws.scheduling.Job;
import com.jaspersoft.jasperserver.ws.scheduling.JobSummary;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportScheduler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportScheduler extends ServletEndpointSupport {

	public static final String SERVICE_ACCESSOR_BEAN_NAME = "reportSchedulerServiceAccessor";
	
	public Job getJob(long id) throws AxisFault {
		return getService().getJob(id);
	}
	
	public Job scheduleJob(Job job) throws AxisFault {
		return getService().scheduleJob(job);
	}
	
	public Job updateJob(Job job) throws AxisFault {
		return getService().updateJob(job);
	}
	
	public void deleteJob(long id) throws AxisFault {
		getService().deleteJob(id);
	}
	
	public void deleteJobs(long[] ids) throws AxisFault {
		getService().deleteJobs(ids);
	}

	public JobSummary[] getAllJobs() throws AxisFault {
		return getService().getAllJobs();
	}
	
	public JobSummary[] getReportJobs(String reportURI) throws AxisFault {
		return getService().getReportJobs(reportURI);
	}
	
	protected ReportSchedulerService getService() {
		ReportSchedulerServiceAccessor accessor = (ReportSchedulerServiceAccessor) getApplicationContext().getBean(SERVICE_ACCESSOR_BEAN_NAME, ReportSchedulerServiceAccessor.class);
		return accessor.getSchedulerService();
	}

}
