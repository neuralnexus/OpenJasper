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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: JdbcConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class JdbcConnectionStrategy implements ConnectionManagementStrategy<ClientJdbcDataSource> {
    private final static Log log = LogFactory.getLog(JdbcConnectionStrategy.class);
    @Resource
    private JdbcDriverService jdbcDriverService;
    @Resource
    private MessageSource messageSource;
    @Resource(name = "concreteRepository")
    private RepositoryService repository;

    @Override
    public ClientJdbcDataSource createConnection(ClientJdbcDataSource connectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        Connection conn = null;
        Exception exception = null;
        boolean passed = false;
        try {
            jdbcDriverService.register(connectionDescription.getDriverClass());

            // On edit datasource we set the passwordSubstitution to the passwords form fields
            // If we get the substitution from UI then use the password from original data source (if it exists)
            String password = connectionDescription.getPassword();
            final String passwordSubstitution = messageSource.getMessage("input.password.substitution", null, LocaleContextHolder.getLocale());
            if ((password == null || password.equals(passwordSubstitution)) && connectionDescription.getUri() != null) {
                JdbcReportDataSource existingDs = (JdbcReportDataSource) repository.getResource(null, connectionDescription.getUri());
                if (existingDs != null) {
                    password = existingDs.getPassword();
                }
            }

            conn = establishConnection(connectionDescription.getConnectionUrl(), connectionDescription.getUsername(), password);
            passed = conn != null;
        } catch (Exception e) {
            exception = e;
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Couldn't disconnect JDBC connection", e);
                }
        }
        if (!passed) {
            throw new ConnectionFailedException(connectionDescription, exception);
        }
        return connectionDescription;
    }

    protected Connection establishConnection(String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void deleteConnection(ClientJdbcDataSource connectionDescription, Map<String, Object> data) {
        // nothing to clean. Do nothing.
    }

    @Override
    public ClientJdbcDataSource modifyConnection(ClientJdbcDataSource newConnectionDescription, ClientJdbcDataSource oldConnectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        return createConnection(newConnectionDescription, data);
    }

    @Override
    public ClientJdbcDataSource secureGetConnection(ClientJdbcDataSource connectionDescription, Map<String, Object> data) {
        return new ClientJdbcDataSource(connectionDescription).setPassword(null);
    }
}
