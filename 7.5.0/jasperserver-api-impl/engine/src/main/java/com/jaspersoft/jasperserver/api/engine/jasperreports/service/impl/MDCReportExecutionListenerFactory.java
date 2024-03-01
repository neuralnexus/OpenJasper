/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.Hashtable;
import java.util.Map;


import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListener;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListenerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;
import org.apache.logging.log4j.ThreadContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
//copied from DiagnosticLoggingContextCompatibleExecutorService
public class MDCReportExecutionListenerFactory implements ReportExecutionListenerFactory {

	public ReportExecutionListener createListener(ReportUnitRequestBase request) {
		return new MDCReportExecutionListener();
	}

}

class MDCReportExecutionListener implements ReportExecutionListener {

	private Thread originalThread;
	private Map<String, String> mdcContextCopy;

	@SuppressWarnings("unchecked")
	public void init() {
		this.originalThread = Thread.currentThread();
        if (ThreadContext.getContext() != null) {
            mdcContextCopy = new Hashtable<String, String>();
            mdcContextCopy.putAll(ThreadContext.getContext());
        }
	}

	@SuppressWarnings("unchecked")
	public void start() {
		boolean setContext = !Thread.currentThread().equals(originalThread);
		if (setContext && mdcContextCopy != null) {
			//FIXME is this safe? can MDC.getContext() be null?
            (ThreadContext.getContext()).putAll(mdcContextCopy);
		}
	}

	public void end(boolean success, long time) {
		//FIXME do some cleanup?
	}
	
	public void end(boolean success) {
		//FIXME do some cleanup?
	}

}