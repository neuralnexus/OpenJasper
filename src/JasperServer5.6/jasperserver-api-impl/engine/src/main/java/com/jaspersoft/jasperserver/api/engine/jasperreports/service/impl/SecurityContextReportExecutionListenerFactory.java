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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListener;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListenerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SecurityContextReportExecutionListenerFactory.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class SecurityContextReportExecutionListenerFactory implements
		ReportExecutionListenerFactory {

	public ReportExecutionListener createListener(ReportUnitRequestBase request) {
		return new SecurityContextReportExecutionListener();
	}

}

class SecurityContextReportExecutionListener implements ReportExecutionListener {

	private Thread originalThread;
	private SecurityContext originalSecurityContext;

	public void init() {
		this.originalThread = Thread.currentThread();
		this.originalSecurityContext = SecurityContextHolder.getContext();
	}

	public void start() {
        /* SecurityContext propagation to thread pool. See #26275 please. */
        /* Only if execution is in new thread (asynchronous). */
		boolean setContext = !Thread.currentThread().equals(originalThread);
		if (setContext) {
			SecurityContextHolder.setContext(originalSecurityContext);
		}
	}
	
	public void end(boolean success, long time) {
		end(success);
	}

	public void end(boolean success) {
		boolean setContext = !Thread.currentThread().equals(originalThread);
		if (setContext) {
			// clear the context
			SecurityContextHolder.clearContext();
		}
	}

}