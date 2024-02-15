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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRParameter;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseJdbcDataSource.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class BaseJdbcDataSource implements ReportDataSourceService {


	private static final Log log = LogFactory.getLog(BaseJdbcDataSource.class);
	
	private Connection conn;
	

	public void setReportParameterValues(Map parameterValues) {
		conn = createConnection();
		parameterValues.put(JRParameter.REPORT_CONNECTION, conn);
	}

	public void closeConnection() {
		if (conn != null)
		{
			try {
				conn.close();
                if (log.isDebugEnabled()) {
                    log.debug("Connection successfully closed");
                }
            } catch (SQLException e) {
				log.error("Error closing connection.", e);
				throw new JSExceptionWrapper(e);
			}

			conn = null;
		}
	}
	
	
	protected abstract Connection createConnection();
}
