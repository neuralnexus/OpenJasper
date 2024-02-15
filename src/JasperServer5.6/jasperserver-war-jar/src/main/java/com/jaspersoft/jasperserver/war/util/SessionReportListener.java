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
package com.jaspersoft.jasperserver.war.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;

import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.war.util.LRUSessionObjectAccessor.ObjectSerie;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SessionReportListener.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class SessionReportListener implements SessionObjectSeriesListener, Serializable{

	private VirtualizerFactory virtualizerFactory;

	public void objectSeriesBound(HttpSessionBindingEvent event,
			SessionObjectSeries series) {
		// NOOP
	}

	public void objectSeriesUnbound(HttpSessionBindingEvent event,
			SessionObjectSeries series) {
		disposeVirtualizers(series);
	}

	public void objectSeriesDidActivate(HttpSessionEvent se,
			ObjectSerie objectSerie) {
		// NOOP
		// ideally we should restore virtualization here.
	}

	public void objectSeriesWillPassivate(HttpSessionEvent event,
			SessionObjectSeries series) {
		// disposing virtualizers before session passivation because virtualizers
		// are not serializable.
		// ideally virtualization should be restored at activation, but activation
		// does not happen in JS due to unserializable object on the session.
		disposeVirtualizers(series);
	}

	protected void disposeVirtualizers(SessionObjectSeries reportSeries) {
		if (virtualizerFactory != null) {
			List reports = reportSeries.getValues();
			for (Iterator it = reports.iterator(); it.hasNext();) {
				ReportUnitResult report = (ReportUnitResult) it.next();
				virtualizerFactory.disposeReport(report);
			}
		}
	}

	public VirtualizerFactory getVirtualizerFactory() {
		return virtualizerFactory;
	}

	public void setVirtualizerFactory(VirtualizerFactory virtualizerFactory) {
		this.virtualizerFactory = virtualizerFactory;
	}

}
