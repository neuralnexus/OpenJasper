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

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValueInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/*
* @author inesterenko
*/

public class ReportInputControlValuesInformationImpl implements
        ReportInputControlValuesInformation, Serializable {
    
    private static final long serialVersionUID = 1L;

    private Map<String, ReportInputControlValueInformation> infos = new LinkedHashMap<String, ReportInputControlValueInformation>();

    public ReportInputControlValuesInformationImpl() {
    }

    public Set<String> getControlValuesNames() {
        return infos.keySet();
    }

    public ReportInputControlValueInformation getInputControlValueInformation(String name) {
        return infos.get(name);
    }
    
    public void setInputControlValueInformation(String name, ReportInputControlValueInformation info){
        infos.put(name, info);
    }
}
