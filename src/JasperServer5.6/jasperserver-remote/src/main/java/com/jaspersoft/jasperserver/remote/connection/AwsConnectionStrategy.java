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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.AwsDataSourceService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsDataSourceRecovery;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientAwsDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.resources.converters.AwsDataSourceResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: AwsConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class AwsConnectionStrategy implements ConnectionManagementStrategy<ClientAwsDataSource> {
    private final static Log log = LogFactory.getLog(AwsConnectionStrategy.class);
    @Resource
    private JdbcDriverService jdbcDriverService;
    @Resource
    private MessageSource messageSource;
    @Resource(name = "concreteRepository")
    private RepositoryService repository;
    @Resource
    private AwsDataSourceRecovery awsDataSourceRecovery;
    @Resource
    private AwsDataSourceResourceConverter awsDataSourceResourceConverter;

    @Override
    public ClientAwsDataSource createConnection(ClientAwsDataSource connectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        Connection conn = null;
        boolean passed = false;
        Exception exception = null;
        try {
            jdbcDriverService.register(connectionDescription.getDriverClass());
            String passwordSubstitution = messageSource.getMessage("input.password.substitution", null,
                    LocaleContextHolder.getLocale());

            // On edit datasource we set the passwordSubstitution to the passwords form fields
            // If we get the substitution from UI then set the password from original datasource (if it exists)
            AwsReportDataSource existingDs = null;
            final String password = connectionDescription.getPassword();
            final String secretKey = connectionDescription.getSecretKey();
            if (password == null || password.equals(passwordSubstitution) || secretKey == null || secretKey.equals(passwordSubstitution)) {
                existingDs = (AwsReportDataSource) repository.getResource(null, connectionDescription.getUri());
            }
            if ((password == null || password.equals(passwordSubstitution)) && existingDs != null) {
                connectionDescription.setPassword(existingDs.getPassword());
            }
            if ((secretKey == null || secretKey.equals(passwordSubstitution)) && existingDs != null) {
                connectionDescription.setSecretKey(existingDs.getAWSSecretKey());
            }
            awsDataSourceRecovery.createAwsDSSecurityGroup(awsDataSourceResourceConverter
                    .toServer(connectionDescription, ToServerConversionOptions.getDefault()));
            conn = establishConnection(connectionDescription.getConnectionUrl(),
                    connectionDescription.getUsername(), connectionDescription.getPassword());
            if (conn != null) {
                passed = true;
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Couldn't disconnect AWS connection", e);
                }
            }
        }
        if (!passed) {
            if (exception != null) {
                Throwable throwable = ExceptionUtils.getRootCause(exception);
                if (throwable instanceof ConnectException || throwable instanceof SocketTimeoutException ||
                        (exception instanceof SQLException && ((SQLException) exception).getSQLState().
                                startsWith(AwsDataSourceService.SQL_STATE_CLASS))) {
                    final StringWriter result = new StringWriter();
                    final PrintWriter traceWriter = new PrintWriter(result);
                    exception.printStackTrace(traceWriter);
                    final String traceString = result.toString();
                    throw new IllegalParameterValueException(
                            "Invalid AWS connection information",
                            "awsDataSource",
                            connectionDescription.toString(),
                            messageSource.getMessage("aws.exception.datasource.recovery.timeout", null, LocaleContextHolder.getLocale()),
                            traceString);
                }
            }
            throw new ConnectionFailedException(connectionDescription, exception);
        }
        return connectionDescription;
    }

    protected Connection establishConnection(String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void deleteConnection(ClientAwsDataSource connectionDescription, Map<String, Object> data) {
        // nothing to clean, do nothing
    }

    @Override
    public ClientAwsDataSource modifyConnection(ClientAwsDataSource newConnectionDescription, ClientAwsDataSource oldConnectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        return createConnection(newConnectionDescription, data);
    }

    @Override
    public ClientAwsDataSource secureGetConnection(ClientAwsDataSource connectionDescription, Map<String, Object> data) {
        return new ClientAwsDataSource(connectionDescription).setPassword(null).setSecretKey(null);
    }
}
