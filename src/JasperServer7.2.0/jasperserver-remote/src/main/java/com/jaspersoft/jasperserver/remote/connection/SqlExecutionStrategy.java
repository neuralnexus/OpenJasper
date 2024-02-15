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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.security.validators.Validator;
import com.jaspersoft.jasperserver.dto.resources.SqlExecutionRequest;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.ConnectionOperationTemplate;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.Connector;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.JdbcQueryExecutor;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.SQLQueryValidator;
import com.jaspersoft.jasperserver.remote.connection.jdbc.JdbcConnector;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ReferencedResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.util.Map;


/**
 * @author serhii.blazhyievskyi
 * @version $Id$
 */

@Service
public class SqlExecutionStrategy implements ContextManagementStrategy<SqlExecutionRequest, SqlExecutionRequest>,
        ContextMetadataBuilder<SqlExecutionRequest>
{

    @Resource
    private JdbcConnector jdbcConnector;
    @Resource(name = "concreteRepository")
    private RepositoryService repository;
    @Resource
    private JdbcQueryExecutor queryExecutor;

    private ConnectionOperationTemplate<SqlExecutionRequest, Connection> connectionOperationTemplate =
            new ConnectionOperationTemplate<SqlExecutionRequest, Connection>(new SqlExecutionConnector());

    private SQLQueryValidator sqlQueryValidator = new SQLQueryValidator() {
        @Override
        public boolean validate(String query, Connection connection) {
            return Validator.validateSQL(query, connection);
        }
    };

    @PostConstruct
    public void init(){
        queryExecutor.setValidator(sqlQueryValidator);
    }

    @Override
    public SqlExecutionRequest createContext(SqlExecutionRequest contextDescription, Map<String, Object> data) throws IllegalParameterValueException {
        return contextDescription;
    }

    @Override
    public void deleteContext(SqlExecutionRequest contextDescription, Map<String, Object> data) {

    }

    @Override
    public SqlExecutionRequest getContextForClient(SqlExecutionRequest contextDescription, Map<String, Object> data, Map<String, String[]> additionalProperties) {
        return contextDescription;
    }

    @Override
    @ClientResourceType("dataset.metadata")
    public Object build(final SqlExecutionRequest context, Map<String, String[]> options,
                        Map<String, Object> contextData) {
        return connectionOperationTemplate.operateConnection(context, new ConnectionOperationTemplate.ConnectionOperator<Object, Connection>() {
            @Override
            public Object operate(Connection connection) {
                return queryExecutor.executeQueryForMetadata(context.getSql(), connection);
            }
        });
    }

    private class SqlExecutionConnector implements Connector<Connection, SqlExecutionRequest> {

        @Override
        public Connection openConnection(SqlExecutionRequest connectionDescription) {
            final String dataSourceUri = connectionDescription.getDataSourceUri();
            com.jaspersoft.jasperserver.api.metadata.common.domain.Resource resource =
                    repository.getResource(ExecutionContextImpl.getRuntimeExecutionContext(),
                            dataSourceUri);
            if(resource == null){
                throw new ReferencedResourceNotFoundException(dataSourceUri, "dataSourceUri");
            }
            return jdbcConnector.openConnection(resource);
        }

        @Override
        public void closeConnection(Connection connection) {
            jdbcConnector.closeConnection(connection);
        }
    }
}
