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
package com.jaspersoft.jasperserver.api.engine.scheduling.security;

import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.BasicAclDao;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.acl.basic.NamedEntityObjectIdentity;
import org.springframework.security.acl.basic.SimpleAclEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.engine.scheduling.ReportJobsInternalService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobAclDao.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportJobAclDao implements BasicAclDao {

	private static final Log log = LogFactory.getLog(ReportJobAclDao.class);
	
	private ReportJobsInternalService reportJobsInternalService;
	private String administratorRole;
	
	public BasicAclEntry[] getAcls(AclObjectIdentity objectIdentity) {
		if (log.isDebugEnabled()) {
			log.debug("Retrieving ACLs for " + objectIdentity);
		}

		String jobOwner = getJobOwner(objectIdentity);
		return makeJobAclEntries(objectIdentity, jobOwner);
	}

	protected final String getJobOwner(AclObjectIdentity jobIdentity) {
		NamedEntityObjectIdentity namedId = (NamedEntityObjectIdentity) jobIdentity;
		long jobId = Long.parseLong(namedId.getId());
		return getReportJobsInternalService().getJobOwner(jobId);
	}

	protected BasicAclEntry[] makeJobAclEntries(
			AclObjectIdentity objectIdentity, String jobOwner) {
		SimpleAclEntry userReadEntry = new SimpleAclEntry(jobOwner, 
				objectIdentity, null, SimpleAclEntry.READ_WRITE_DELETE);
		SimpleAclEntry admReadEntry = new SimpleAclEntry(getAdministratorRole(), 
				objectIdentity, null, SimpleAclEntry.ADMINISTRATION);
		return new BasicAclEntry[]{userReadEntry, admReadEntry};
	}

	public String getAdministratorRole() {
		return administratorRole;
	}

	public void setAdministratorRole(String administratorRole) {
		this.administratorRole = administratorRole;
	}

	public ReportJobsInternalService getReportJobsInternalService() {
		return reportJobsInternalService;
	}

	public void setReportJobsInternalService(
			ReportJobsInternalService reportJobsInternalService) {
		this.reportJobsInternalService = reportJobsInternalService;
	}

}
