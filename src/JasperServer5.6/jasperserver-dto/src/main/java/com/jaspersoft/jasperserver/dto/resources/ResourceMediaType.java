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
package com.jaspersoft.jasperserver.dto.resources;

import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ResourceMediaType.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface ResourceMediaType {
    public static final String RESOURCE_MEDIA_TYPE_PREFIX = "application/repository.";
    public static final String RESOURCE_TYPE_PLACEHOLDER = "{resourceType}";
    public static final String RESOURCE_JSON_TYPE = "+json";
    public static final String RESOURCE_XML_TYPE = "+xml";

    public static final String RESOURCE_XML_TEMPLATE = RESOURCE_MEDIA_TYPE_PREFIX + RESOURCE_TYPE_PLACEHOLDER + RESOURCE_XML_TYPE;
    public static final String RESOURCE_JSON_TEMPLATE = RESOURCE_MEDIA_TYPE_PREFIX + RESOURCE_TYPE_PLACEHOLDER + RESOURCE_JSON_TYPE;
    // AwsReportDataSource
    public static final String AWS_DATA_SOURCE_CLIENT_TYPE = "awsDataSource";
    public static final String AWS_DATA_SOURCE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + AWS_DATA_SOURCE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String AWS_DATA_SOURCE_XML = RESOURCE_MEDIA_TYPE_PREFIX + AWS_DATA_SOURCE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // BeanReportDataSource
    public static final String BEAN_DATA_SOURCE_CLIENT_TYPE = "beanDataSource";
    public static final String BEAN_DATA_SOURCE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + BEAN_DATA_SOURCE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String BEAN_DATA_SOURCE_XML = RESOURCE_MEDIA_TYPE_PREFIX + BEAN_DATA_SOURCE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // CustomReportDataSource
    public static final String CUSTOM_DATA_SOURCE_CLIENT_TYPE = "customDataSource";
    public static final String CUSTOM_DATA_SOURCE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + CUSTOM_DATA_SOURCE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String CUSTOM_DATA_SOURCE_XML = RESOURCE_MEDIA_TYPE_PREFIX + CUSTOM_DATA_SOURCE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // DataType
    public static final String DATA_TYPE_CLIENT_TYPE = "dataType";
    public static final String DATA_TYPE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + DATA_TYPE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String DATA_TYPE_XML = RESOURCE_MEDIA_TYPE_PREFIX + DATA_TYPE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // Folder
    public static final String FOLDER_CLIENT_TYPE = "folder";
    public static final String FOLDER_JSON = RESOURCE_MEDIA_TYPE_PREFIX + FOLDER_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String FOLDER_XML = RESOURCE_MEDIA_TYPE_PREFIX + FOLDER_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // File
    public static final String FILE_CLIENT_TYPE = "file";
    public static final String FILE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + FILE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String FILE_XML = RESOURCE_MEDIA_TYPE_PREFIX + FILE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // InputControl
    public static final String INPUT_CONTROL_CLIENT_TYPE = "inputControl";
    public static final String INPUT_CONTROL_JSON = RESOURCE_MEDIA_TYPE_PREFIX + INPUT_CONTROL_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String INPUT_CONTROL_XML = RESOURCE_MEDIA_TYPE_PREFIX + INPUT_CONTROL_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // JdbcReportDataSource
    public static final String JDBC_DATA_SOURCE_CLIENT_TYPE = "jdbcDataSource";
    public static final String JDBC_DATA_SOURCE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + JDBC_DATA_SOURCE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String JDBC_DATA_SOURCE_XML = RESOURCE_MEDIA_TYPE_PREFIX + JDBC_DATA_SOURCE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // JndiJdbcReportDataSource
    public static final String JNDI_JDBC_DATA_SOURCE_CLIENT_TYPE = "jndiJdbcDataSource";
    public static final String JNDI_JDBC_DATA_SOURCE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + JNDI_JDBC_DATA_SOURCE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String JNDI_JDBC_DATA_SOURCE_XML = RESOURCE_MEDIA_TYPE_PREFIX + JNDI_JDBC_DATA_SOURCE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // ListOfValues
    public static final String LIST_OF_VALUES_CLIENT_TYPE = "listOfValues";
    public static final String LIST_OF_VALUES_JSON = RESOURCE_MEDIA_TYPE_PREFIX + LIST_OF_VALUES_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String LIST_OF_VALUES_XML = RESOURCE_MEDIA_TYPE_PREFIX + LIST_OF_VALUES_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // MondrianConnection
    public static final String MONDRIAN_CONNECTION_CLIENT_TYPE = "mondrianConnection";
    public static final String MONDRIAN_CONNECTION_JSON = RESOURCE_MEDIA_TYPE_PREFIX + MONDRIAN_CONNECTION_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String MONDRIAN_CONNECTION_XML = RESOURCE_MEDIA_TYPE_PREFIX + MONDRIAN_CONNECTION_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // MondrianXmlaDefinition
    public static final String MONDRIAN_XMLA_DEFINITION_CLIENT_TYPE = "mondrianXmlaDefinition";
    public static final String MONDRIAN_XMLA_DEFINITION_JSON = RESOURCE_MEDIA_TYPE_PREFIX + MONDRIAN_XMLA_DEFINITION_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String MONDRIAN_XMLA_DEFINITION_XML = RESOURCE_MEDIA_TYPE_PREFIX + MONDRIAN_XMLA_DEFINITION_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // OlapUnit
    public static final String OLAP_UNIT_CLIENT_TYPE = "olapUnit";
    public static final String OLAP_UNIT_JSON = RESOURCE_MEDIA_TYPE_PREFIX + OLAP_UNIT_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String OLAP_UNIT_XML = RESOURCE_MEDIA_TYPE_PREFIX + OLAP_UNIT_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // Query
    public static final String QUERY_CLIENT_TYPE = "query";
    public static final String QUERY_JSON = RESOURCE_MEDIA_TYPE_PREFIX + QUERY_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String QUERY_XML = RESOURCE_MEDIA_TYPE_PREFIX + QUERY_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // ReportUnit
    public static final String REPORT_UNIT_CLIENT_TYPE = "reportUnit";
    public static final String REPORT_UNIT_JSON = RESOURCE_MEDIA_TYPE_PREFIX + REPORT_UNIT_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String REPORT_UNIT_XML = RESOURCE_MEDIA_TYPE_PREFIX + REPORT_UNIT_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // SecureMondrianConnection
    public static final String SECURE_MONDRIAN_CONNECTION_CLIENT_TYPE = "secureMondrianConnection";
    public static final String SECURE_MONDRIAN_CONNECTION_JSON = RESOURCE_MEDIA_TYPE_PREFIX + SECURE_MONDRIAN_CONNECTION_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String SECURE_MONDRIAN_CONNECTION_XML = RESOURCE_MEDIA_TYPE_PREFIX + SECURE_MONDRIAN_CONNECTION_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // VirtualReportDataSource
    public static final String VIRTUAL_DATA_SOURCE_CLIENT_TYPE = "virtualDataSource";
    public static final String VIRTUAL_DATA_SOURCE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + VIRTUAL_DATA_SOURCE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String VIRTUAL_DATA_SOURCE_XML = RESOURCE_MEDIA_TYPE_PREFIX + VIRTUAL_DATA_SOURCE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // XmlaConnection
    public static final String XMLA_CONNECTION_CLIENT_TYPE = "xmlaConnection";
    public static final String XMLA_CONNECTION_JSON = RESOURCE_MEDIA_TYPE_PREFIX + XMLA_CONNECTION_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String XMLA_CONNECTION_XML = RESOURCE_MEDIA_TYPE_PREFIX + XMLA_CONNECTION_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // adhocDataView
    public static final String ADHOC_DATA_VIEW_CLIENT_TYPE = "adhocDataView";
    public static final String ADHOC_DATA_VIEW_JSON = RESOURCE_MEDIA_TYPE_PREFIX + ADHOC_DATA_VIEW_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String ADHOC_DATA_VIEW_XML = RESOURCE_MEDIA_TYPE_PREFIX + ADHOC_DATA_VIEW_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // resourceLookup
    public static final String RESOURCE_LOOKUP_CLIENT_TYPE = "resourceLookup";
    public static final String RESOURCE_LOOKUP_JSON = RESOURCE_MEDIA_TYPE_PREFIX + RESOURCE_LOOKUP_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String RESOURCE_LOOKUP_XML = RESOURCE_MEDIA_TYPE_PREFIX + RESOURCE_LOOKUP_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // semanticLayerDataSource
    public static final String SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE = "semanticLayerDataSource";
    public static final String SEMANTIC_LAYER_DATA_SOURCE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String SEMANTIC_LAYER_DATA_SOURCE_XML = RESOURCE_MEDIA_TYPE_PREFIX + SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // Dashboard
    public static final String DASHBOARD_CLIENT_TYPE = "dashboard";
    public static final String DASHBOARD_JSON = RESOURCE_MEDIA_TYPE_PREFIX + DASHBOARD_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String DASHBOARD_XML = RESOURCE_MEDIA_TYPE_PREFIX + DASHBOARD_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // reportOptions
    public static final String REPORT_OPTIONS_CLIENT_TYPE = "reportOptions";
    public static final String REPORT_OPTIONS_JSON = RESOURCE_MEDIA_TYPE_PREFIX + REPORT_OPTIONS_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String REPORT_OPTIONS_XML = RESOURCE_MEDIA_TYPE_PREFIX + REPORT_OPTIONS_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // domainTopic
    public static final String DOMAIN_TOPIC_CLIENT_TYPE = "domainTopic";
    public static final String DOMAIN_TOPIC_CLIENT_TYPE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + DOMAIN_TOPIC_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String DOMAIN_TOPIC_CLIENT_TYPE_XML = RESOURCE_MEDIA_TYPE_PREFIX + DOMAIN_TOPIC_CLIENT_TYPE + RESOURCE_XML_TYPE;
    //DataSources
    public static final List<String> DATASOURCE_TYPES = Arrays.asList(
            AWS_DATA_SOURCE_CLIENT_TYPE,
            BEAN_DATA_SOURCE_CLIENT_TYPE,
            CUSTOM_DATA_SOURCE_CLIENT_TYPE,
            JDBC_DATA_SOURCE_CLIENT_TYPE,
            JNDI_JDBC_DATA_SOURCE_CLIENT_TYPE,
            VIRTUAL_DATA_SOURCE_CLIENT_TYPE,
            SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE
    );

    //AnyDatasource
    //not exist as string representation of resource type
    //used as defenition of any datasource type
    public static final String ANY_DATASOURCE_TYPE = "anyDatasource";
}


