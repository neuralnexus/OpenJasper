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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.remote.common.CallTemplate;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceWrapperWithCheckedException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.JobsService;
import com.jaspersoft.jasperserver.ws.axis2.util.RemoteServiceFromWsCallTemplate;
import com.jaspersoft.jasperserver.ws.scheduling.Job;
import com.jaspersoft.jasperserver.ws.scheduling.JobSummary;
import org.apache.axis.AxisFault;

import java.util.Iterator;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportSchedulerServiceImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
@CallTemplate(RemoteServiceFromWsCallTemplate.class)
public class ReportSchedulerServiceImpl extends RemoteServiceWrapperWithCheckedException<JobsService, AxisFault> implements ReportSchedulerService {

	private ReportJobBeanTraslator beanTraslator;

	public void deleteJob(final long id) throws AxisFault {
        callRemoteService(new ConcreteCaller<Object>() {
            public Object call(JobsService service) throws RemoteException {
                service.deleteJob(id);
                return null;
            }
        });
    }

	public void deleteJobs(final long[] ids) throws AxisFault {
        callRemoteService(new ConcreteCaller<Object>() {
            public Object call(JobsService service) throws RemoteException {
                service.deleteJobs(ids);
                return null;
            }
        });
	}

	public Job getJob(final long id) throws AxisFault {
		ReportJob reportJob = callRemoteService(new ConcreteCaller<ReportJob>() {
            public ReportJob call(JobsService service) throws RemoteException {
                return service.getJob(id);
            }
        });
		if (reportJob == null) {
			throw new JSException("report.scheduling.ws.job.not.found", new Object[]{id});
		}
		return beanTraslator.toServiceBean(reportJob);
	}

	public Job scheduleJob(Job job) throws AxisFault {
		final ReportJob reportJob = new ReportJob();
		beanTraslator.copy(reportJob, job);
		ReportJob savedJob = callRemoteService(new ConcreteCaller<ReportJob>() {
            public ReportJob call(JobsService service) throws RemoteException {
                return service.scheduleJob(reportJob);
            }
        });
		return beanTraslator.toServiceBean(savedJob);
	}

	public Job updateJob(Job job) throws AxisFault {
		final ReportJob reportJob = new ReportJob();
		beanTraslator.copy(reportJob, job);
		ReportJob savedJob = callRemoteService(new ConcreteCaller<ReportJob>() {
            public ReportJob call(JobsService service) throws RemoteException {
                return service.updateJob(reportJob);
            }
        });
		return beanTraslator.toServiceBean(savedJob);
	}

	public JobSummary[] getAllJobs() throws AxisFault {
		List<ReportJobSummary> scheduledJobs = callRemoteService(new ConcreteCaller<List<ReportJobSummary>>() {
            public List<ReportJobSummary> call(JobsService service) throws RemoteException {
                return service.getAllJobs();
            }
        });
		return toSummaryArray(scheduledJobs);
	}

	public JobSummary[] getReportJobs(final String reportURI) throws AxisFault {
		List<ReportJobSummary> scheduledJobs = callRemoteService(new ConcreteCaller<List<ReportJobSummary>>() {
            public List<ReportJobSummary> call(JobsService service) throws RemoteException {
                return service.getReportJobs(reportURI);
            }
        });
		return toSummaryArray(scheduledJobs);
	}

	protected JobSummary[] toSummaryArray(List reportJobSummaries) {
		JobSummary[] jobs;
		if (reportJobSummaries == null || reportJobSummaries.isEmpty()) {
			jobs = null;
		} else {
			jobs = new JobSummary[reportJobSummaries.size()];
			int idx = 0;
			for (Iterator it = reportJobSummaries.iterator(); it.hasNext(); ++idx) {
				ReportJobSummary reportJob = (ReportJobSummary) it.next();
				jobs[idx] = beanTraslator.toServiceSummary(reportJob);
			}
		}
		return jobs;
	}

	public ReportJobBeanTraslator getBeanTraslator() {
		return beanTraslator;
	}

	public void setBeanTraslator(ReportJobBeanTraslator beanTraslator) {
		this.beanTraslator = beanTraslator;
	}

    public void setJobsService(JobsService jobsService) {
        remoteService = jobsService;
    }
}
