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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JdbcDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;

import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: JdbcDataSourceImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JdbcDataSourceImpl extends DataSourceImpl implements JdbcDataSource {

    private Set<String> schemas;

    public JdbcDataSourceImpl(JdbcReportDataSource jdbcReportDataSource, Set<String> schemas, String dataSourceName, VirtualReportDataSource parentDataSource) {
        super(jdbcReportDataSource, dataSourceName, parentDataSource);
        this.schemas = schemas;
    }

    /**
	 * Get all the selected schemas
	 * @return a set of schema that is going to use to build virtual data source
	 */
    public Set<String> getSchemas() {
        return schemas;
    }

    /**
	 * Get the name of the JDBC driver required to create this JDBC connection
	 * @return name of a JDBC class loadable by JasperServer
	 */
	public String getDriverClass() {
        return ((JdbcReportDataSource)reportDataSource).getDriverClass();
    }

	/**
	 * Get the URL specifying the JDBC connection
	 * @return URL string compatible with the JDBC driver
	 */
	public String getConnectionUrl() {
       return ((JdbcReportDataSource)reportDataSource).getConnectionUrl();
    }

	/**
	 * Get the username used to authenticate the JDBC connection
	 * @return username for JDBC connection
	 */
	public String getUsername() {
        return ((JdbcReportDataSource)reportDataSource).getUsername();
    }

	/**
	 * Get the password used to authenticate the JDBC connection
	 * @return password for JDBC connection
	 */
	public String getPassword() {
        return ((JdbcReportDataSource)reportDataSource).getPassword();
    }
}
