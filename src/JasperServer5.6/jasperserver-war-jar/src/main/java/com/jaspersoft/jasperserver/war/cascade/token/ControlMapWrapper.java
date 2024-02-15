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
package com.jaspersoft.jasperserver.war.cascade.token;

import com.jaspersoft.jasperserver.war.cascade.*;

/**
 * ControlMapWrapper
 * @author jwhang
 * @version $Id: ControlMapWrapper.java 47331 2014-07-18 09:13:06Z kklein $
 * structure to hold references between controls accumulated in the initialization phase.
 */

public class ControlMapWrapper {

    private ControlMapWrapper previousControlMap = null;
    private ControlMapWrapper nextControlMap = null;
    private EventEnvelope currentEnvelope = null;

    public ControlMapWrapper(){
    }

    public ControlMapWrapper (EventEnvelope currentEnvelope){
        this.currentEnvelope = currentEnvelope;
    }

    public ControlMapWrapper (EventEnvelope currentEnvelope, ControlMapWrapper previousControlMap){
        this.currentEnvelope = currentEnvelope;
        this.previousControlMap = previousControlMap;
    }

    public ControlMapWrapper getPreviousControl() {
        return previousControlMap;
    }

    public void setPreviousControl(ControlMapWrapper previousControlMap) {
        this.previousControlMap = previousControlMap;
    }

    public ControlMapWrapper getNextControl() {
        return nextControlMap;
    }

    public void setNextControl(ControlMapWrapper nextControlMap) {
        this.nextControlMap = nextControlMap;
    }

    public EventEnvelope getCurrentEnvelope() {
        return currentEnvelope;
    }

    public void setCurrentEnvelope(EventEnvelope currentEnvelope) {
        this.currentEnvelope = currentEnvelope;
    }

}
