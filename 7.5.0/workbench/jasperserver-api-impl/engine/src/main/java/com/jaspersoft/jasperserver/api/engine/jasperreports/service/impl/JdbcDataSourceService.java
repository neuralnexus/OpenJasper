/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSDataSourceConnectionFailedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TimeZone;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JdbcDataSourceService extends BaseJdbcDataSource {

	protected static final Log log = LogFactory
			.getLog(JdbcDataSourceService.class);

	private final DataSource dataSource;
	private TimeZone timezone;

	public JdbcDataSourceService(DataSource dataSource, TimeZone timezone) {
		this.dataSource = dataSource;
		this.timezone = timezone;
	}


	protected Connection createConnection() {
		try {
			Connection c = TibcoDriverManagerImpl.getInstance().unlockConnection(dataSource);
      if (log.isDebugEnabled()) {
        log.debug("CreateConnection successful at com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JdbcDataSourceService.createConnection");
      }
      return c;
		} catch (SQLException e) {
			if (log.isDebugEnabled())
				log.debug("Error creating connection.", e);
            throw new JSDataSourceConnectionFailedException("jsexception.error.creating.connection", e);
		}
	}

	public void setReportParameterValues(Map parameterValues) {
		super.setReportParameterValues(parameterValues);
		//TODO implement as java.sql.Connection decoration?
		parameterValues.put(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE, timezone);
	}

	/**
	 * @return Returns the dataSource.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

    @Override
    public boolean testConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return isConnectionValid(conn);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Couldn't disconnect JDBC connection", e);
                }
        }
    }

    public static boolean isConnectionValid(Connection conn) throws SQLException {
        if (conn == null) {
            return false;
        }
        try {
            return conn.isValid(10 /*seconds*/);
        } catch (IncompatibleClassChangeError e) {
            log.error("Connection validation is not supported by driver", e);
            // return true so old drivers can still be used.
            return true;
        }
    }

}
