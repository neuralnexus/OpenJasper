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
package com.jaspersoft.jasperserver.api.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: CustomDataSource.java 47331 2014-07-18 09:13:06Z kklein $
 */
@JasperServerAPI
public interface CustomDataSource extends DataSource {

	/**
	 * Get the persisted property values for this data source.
	 * The keys of this map correspond to the names of the properties in CustomDataSourceDefinition.getPropertyDefinitions()
	 * @return persisted property names and values
	 */
	public Map getPropertyMap();

    /*
     * Returns a set of selected schema that use in this data source
     * @return a set of selected schema
     */
    Set<String> getSchemas();

	/**
	 * get the name of the class implementing ReportDataSourceService for this data source instance,
	 * which matches the value of getServiceClassName() on some instance of CustomDataSourceDefinition
	 * @return
	 */
	public String getServiceClass();

}
