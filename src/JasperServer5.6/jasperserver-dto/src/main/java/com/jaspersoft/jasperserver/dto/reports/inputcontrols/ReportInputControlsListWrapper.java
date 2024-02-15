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
package com.jaspersoft.jasperserver.dto.reports.inputcontrols;

import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportInputControlsListWrapper.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = "inputControls")
public class ReportInputControlsListWrapper {

    private List<ReportInputControl> inputParameters;

    public ReportInputControlsListWrapper(){}
    public ReportInputControlsListWrapper(List<ReportInputControl> inputParameters){
        this.inputParameters = inputParameters;
    }

    public ReportInputControlsListWrapper(ReportInputControlsListWrapper other) {
        final List<ReportInputControl> reportInputControls = other.getInputParameters();
        if(reportInputControls != null){
            inputParameters = new ArrayList<ReportInputControl>(other.getInputParameters().size());
            for(ReportInputControl inputControl : reportInputControls){
                inputParameters.add(new ReportInputControl(inputControl));
            }
        }
    }

    @XmlElement(name = "inputControl")
    public List<ReportInputControl> getInputParameters() {
        return inputParameters;
    }

    public ReportInputControlsListWrapper setInputParameters(List<ReportInputControl> inputParameters) {
        this.inputParameters = inputParameters;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportInputControlsListWrapper)) return false;

        ReportInputControlsListWrapper that = (ReportInputControlsListWrapper) o;

        if (inputParameters != null ? !inputParameters.equals(that.inputParameters) : that.inputParameters != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return inputParameters != null ? inputParameters.hashCode() : 0;
    }
}
