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
package com.jaspersoft.jasperserver.export.modules.scheduling.beans;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobSimpleTriggerBean.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportJobSimpleTriggerBean extends ReportJobTriggerBean {
	
	private int occurrenceCount;
	private Integer recurrenceInterval;
	private Byte recurrenceIntervalUnit;
	
	public void copyFrom(ReportJobTrigger trigger) {
		super.copyFrom(trigger);
		
		ReportJobSimpleTrigger simpleTrigger = (ReportJobSimpleTrigger) trigger;
		setOccurrenceCount(simpleTrigger.getOccurrenceCount());
		setRecurrenceInterval(simpleTrigger.getRecurrenceInterval());
		setRecurrenceIntervalUnit(simpleTrigger.getRecurrenceIntervalUnit());
	}

	public ReportJobTrigger toJobTrigger() {
		return new ReportJobSimpleTrigger();
	}
	
	public void copyTo(ReportJobTrigger trigger) {
		super.copyTo(trigger);
		
		ReportJobSimpleTrigger simpleTrigger = (ReportJobSimpleTrigger) trigger;
		simpleTrigger.setOccurrenceCount(getOccurrenceCount());
		simpleTrigger.setRecurrenceInterval(getRecurrenceInterval());
		simpleTrigger.setRecurrenceIntervalUnit(getRecurrenceIntervalUnit());
	}
	
	public int getOccurrenceCount() {
		return occurrenceCount;
	}
	public void setOccurrenceCount(int occurrenceCount) {
		this.occurrenceCount = occurrenceCount;
	}
	
	public Integer getRecurrenceInterval() {
		return recurrenceInterval;
	}
	public void setRecurrenceInterval(Integer recurrenceInterval) {
		this.recurrenceInterval = recurrenceInterval;
	}
	
	public Byte getRecurrenceIntervalUnit() {
		return recurrenceIntervalUnit;
	}
	public void setRecurrenceIntervalUnit(Byte recurrenceIntervalUnit) {
		this.recurrenceIntervalUnit = recurrenceIntervalUnit;
	}

}
