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

package com.jaspersoft.jasperserver.api.engine.common.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.Map;
import java.util.Set;

/**
 * 
 * Represents a list of ReportInputControlInformation objects associated with a ReportUnit
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportInputControlsInformation.java 47331 2014-07-18 09:13:06Z kklein $
 */
@JasperServerAPI
public interface ReportInputControlsInformation {

	/**
	 * Get the set of names for all the input controls
	 * @return input control names
	 */
	Set<String> getControlNames();
	
	/**
	 * Get the ReportInputControlInformation matching the name
	 * @return matching input control info
	 */
	ReportInputControlInformation getInputControlInformation(String name);

    /**
     * Extracts the map of parameters containing in the ReportInputControlInformation objects
     * @return default parameters map
     */
    Map<String, Object> getDefaultValuesMap();
	
}
