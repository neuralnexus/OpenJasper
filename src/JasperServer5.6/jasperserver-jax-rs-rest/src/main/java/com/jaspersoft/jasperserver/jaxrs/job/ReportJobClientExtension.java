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
package com.jaspersoft.jasperserver.jaxrs.job;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import net.sf.jasperreports.engine.JRParameter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.TimeZone;

/**
 * <p>REST specific extension of ReportJob object</p>
 * This extension is intended to correct representation of outputTimeZone property of ReportJob.
 * Here it is real property and not saved to the source's parameters map.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportJobClientExtension.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = "job")
public class ReportJobClientExtension extends ReportJob {
    public ReportJobClientExtension(){
    }
    public ReportJobClientExtension(ReportJob sourceObject){
        super(sourceObject);
        if(sourceObject.getTrigger() instanceof ReportJobSimpleTrigger){
            setTrigger(new ReportJobSimpleTriggerClientExtension((ReportJobSimpleTrigger) sourceObject.getTrigger()));
        } else if(sourceObject.getTrigger() instanceof ReportJobCalendarTrigger){
            setTrigger(new ReportJobCalendarTriggerClientExtension((ReportJobCalendarTrigger) sourceObject.getTrigger()));
        }
        // move outputTimeZone from parameters map to corresponding property of this class.
        this.outputTimeZone = this.getSource() != null
                && this.getSource().getParameters() != null
                && this.getSource().getParameters().get(JRParameter.REPORT_TIME_ZONE) != null
                && this.getSource().getParameters().get(JRParameter.REPORT_TIME_ZONE) instanceof TimeZone
                ? ((TimeZone)this.getSource().getParameters().remove(JRParameter.REPORT_TIME_ZONE)).getID() : null;

        this.setId(sourceObject.getId());
        this.setVersion(sourceObject.getVersion());
        this.setAlert(sourceObject.getAlert());
    }

    private String outputTimeZone;
    @Override
    public String getOutputTimeZone() {
        return outputTimeZone;
    }

    @Override
    public void setOutputTimeZone(String outputTimeZone) {
        this.outputTimeZone = outputTimeZone;
    }

    @Override
    @XmlElements({
            @XmlElement(name = "simpleTrigger", type = ReportJobSimpleTriggerClientExtension.class),
            @XmlElement(name = "calendarTrigger", type = ReportJobCalendarTriggerClientExtension.class)})
    public ReportJobTrigger getTrigger() {
        return super.getTrigger();
    }
}
