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
package com.jaspersoft.jasperserver.remote.connection.jdbc;

import com.jaspersoft.jasperserver.api.metadata.jdbc.SqlEscapingFactory;
import com.jaspersoft.jasperserver.core.util.type.MultipleTypeProcessor;
import com.jaspersoft.jasperserver.dto.connection.metadata.PartialMetadataOptions;
import com.jaspersoft.jasperserver.dto.resources.ClientAwsDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientAzureSqlDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientVirtualDataSource;
import com.jaspersoft.jasperserver.remote.connection.ClientResourceType;
import com.jaspersoft.jasperserver.remote.connection.ContextMetadataBuilder;
import com.jaspersoft.jasperserver.remote.connection.ContextParametrizedMetadataBuilder;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.ConnectionOperationTemplate;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.JdbcMetadataBuilder;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.MetadataBuilder;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.SpecialCharacterEscape;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class JdbcDataSourceMetadataBuilder<ConnectionDescriptionType extends ClientResource<ConnectionDescriptionType>>
        implements ContextMetadataBuilder<ConnectionDescriptionType>,
        ContextParametrizedMetadataBuilder<ConnectionDescriptionType, PartialMetadataOptions>, MultipleTypeProcessor {
    private static final List<Class<?>> IMPLEMENTED_PROCESSOR_CLASSES = Arrays.asList(
            ContextMetadataBuilder.class, ContextParametrizedMetadataBuilder.class);
    private static final List<Class<?>> SUPPORTED_PROCESSING_CLASSES = (List)Arrays.asList(ClientJdbcDataSource.class,
            ClientJndiJdbcDataSource.class, ClientVirtualDataSource.class, ClientAwsDataSource.class,
            ClientAzureSqlDataSource.class);
    @Resource
    private ClientJdbcConnector clientJdbcConnector;
    @Resource
    private SqlEscapingFactory sqlEscapingFactory;
    private ConnectionOperationTemplate<ConnectionDescriptionType, Connection> connectionOperationTemplate;
    @Resource
    private JdbcMetadataBuilder jdbcMetadataBuilder;


    private SpecialCharacterEscape specialCharacterEscape = new SpecialCharacterEscape() {
        @Override
        public String escape(String toEscape, DatabaseMetaData databaseMetaData, String type) {
            return toEscape != null ? sqlEscapingFactory.getSlqEscapingStrategy(type).sqlEscape(databaseMetaData, toEscape) : null;
        }
    };
    @PostConstruct
    public void init(){
        connectionOperationTemplate
                = new ConnectionOperationTemplate<ConnectionDescriptionType, Connection>(clientJdbcConnector);

        jdbcMetadataBuilder.setCharacterEscape(specialCharacterEscape);
    }

    @Override
    @ClientResourceType("jdbc.metadata")
    public Object build(ConnectionDescriptionType context, final Map<String, String[]> options,
            Map<String, Object> contextData) {
        return connectionOperationTemplate.operateConnection(context, new ConnectionOperationTemplate.ConnectionOperator<Object, Connection>() {
            @Override
            public Object operate(Connection connection) {
                return jdbcMetadataBuilder.build(connection, options);
            }
        });
    }


    @Override
    public Object build(ConnectionDescriptionType connection, PartialMetadataOptions options, Map<String, Object> contextData) {
        return connectionOperationTemplate.operateConnection(connection, new ConnectionOperationTemplate.ConnectionOperator<Object, Connection>() {
            @Override
            public Object operate(Connection connection) {
                return jdbcMetadataBuilder.build(connection, (options == null)? new PartialMetadataOptions():options);
            }
        });
    }

    @Override
    public List<Class<?>> getProcessableTypes(Class<?> processorClass) {
        return IMPLEMENTED_PROCESSOR_CLASSES.contains(processorClass) ? SUPPORTED_PROCESSING_CLASSES : null;
    }
}
