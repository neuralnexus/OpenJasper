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

import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: JdbcDataSource.java 47331 2014-07-18 09:13:06Z kklein $
 */
@JasperServerAPI
public interface JdbcDataSource extends DataSource {

    /**
	 * Get the name of the JDBC driver required to create this JDBC connection
	 * @return name of a JDBC class loadable by JasperServer
	 */
	String getDriverClass();

	/**
	 * Get the URL specifying the JDBC connection
	 * @return URL string compatible with the JDBC driver
	 */
	String getConnectionUrl();

	/**
	 * Get the username used to authenticate the JDBC connection
	 * @return username for JDBC connection
	 */
	String getUsername();

	/**
	 * Get the password used to authenticate the JDBC connection
	 * @return password for JDBC connection
	 */
	String getPassword();

    /*
     * Returns a set of selected schema that use in this data source
     * @return a set of selected schema
     */
    Set<String> getSchemas();

}
