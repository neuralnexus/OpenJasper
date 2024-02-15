/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.hadoop.hive.jasperserver;

import com.jaspersoft.connectors.hive.HiveDataSource;
import com.jaspersoft.connectors.hive.connection.HiveConnection;
import com.jaspersoft.connectors.hive.connection.HiveConnectionManager;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.CustomReportDataSourceService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.Map;

/**                 \
 * 
 * @author Eric Diaz
 * 
 */
public class HiveDataSourceService implements CustomReportDataSourceService {
    private String jdbcURL;
    private String username;
    private String password;
    //private String driverClass;

    private HiveConnection connection;

    private HiveConnectionManager connectionManager;

    private final static Log logger = LogFactory.getLog(HiveDataSourceService.class);

    public HiveDataSourceService() {
    	//logger.error("HiveDataSourceService Constructor not defined");
    }

    @Override
    public void closeConnection() {
        if (connectionManager != null && connection != null) {
            connectionManager.returnConnection(connection);
            connection = null;
        }
    }

    /**
     * Creates a new {@link HiveDataSource} base on the parameters set
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setReportParameterValues(Map parameters) {
        try {
            createConnection();
            parameters.put(JRParameter.REPORT_CONNECTION, connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createConnection() throws JRException, SQLException {
        if (connection != null) {
            closeConnection();
        }

        connectionManager.setJdbcURL(jdbcURL);
        connectionManager.setUsername(username);
        connectionManager.setPassword(password);

        try {
            connection = connectionManager.borrowConnection();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            throw new JRException(e.getMessage());
        }
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectionManager(HiveConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public HiveConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public boolean testConnection() throws JRException {
        try {
            createConnection();
            if (connection == null) {
                return false;
            }
            connection.test();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }
}
