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
package com.jaspersoft.jasperserver.api.logging.audit.domain;

import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;

import java.util.Date;

/**
 * @author Richard Klein
 * @version $Id: ReportMonitoringFact.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface ReportMonitoringFact extends LoggableEvent {

    public short getDateYear();

    public void setDateYear(short dateYear);

    public byte getDateMonth();

    public void setDateMonth(byte dateMonth);

    public byte getDateDay();

    public void setDateDay(byte dateDay);

    public byte getTimeHour();

    public void setTimeHour(byte timeHour);

    public byte getTimeMinute();

    public void setTimeMinute(byte timeMinute);

    public String getEventContext();

    public void setEventContext(String eventContext);

    public String getUserOrganization();

    public void setUserOrganization(String userOrganization);

    public String getUserName();

    public void setUserName(String userName);

    public String getEventType();

    public void setEventType(String eventType);

    public String getReportUri();

    public void setReportUri(String reportUri);

    public String getEditingAction();

    public void setEditingAction(String editingAction);

    public int getQueryExecutionTime();

    public void setQueryExecutionTime(int queryExecutionTime);

    public int getReportRenderingTime();

    public void setReportRenderingTime(int reportRenderingTime);

    public int getTotalReportExecutionTime();

    public void setTotalReportExecutionTime(int totalReportExecutionTime);

    public Date getTimeStamp();

    public void setTimeStamp(Date timeStamp);
}
