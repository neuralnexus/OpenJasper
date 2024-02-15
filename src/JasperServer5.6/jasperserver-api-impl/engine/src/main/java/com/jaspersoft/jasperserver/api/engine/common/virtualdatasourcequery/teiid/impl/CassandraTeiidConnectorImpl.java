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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.TeiidDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TeiidEmbeddedServer;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TranslatorConfig;
import com.tonbeller.jpivot.olap.model.Property;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.dqp.internal.datamgr.ConnectorManager;
import org.teiid.resource.adapter.cassandra.CassandraManagedConnectionFactory;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: CassandraTeiidConnectorImpl.java 48307 2014-08-15 21:38:37Z ichan $
 */
public class CassandraTeiidConnectorImpl implements TeiidDataSource {

    String address;
    String keyspace;
    String username;
    String password;
    int port;
    String dataSourceName;
    TranslatorConfig translatorConfig;
    private Map<String, String> importPropertyMap;
    private static final Log log = LogFactory.getLog(TeiidEmbeddedServer.class);

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public TranslatorConfig getTranslatorConfig() {
        return translatorConfig;
    }

    public void setTranslatorConfig(TranslatorConfig translatorConfig) {
        this.translatorConfig = translatorConfig;
    }

    /*
    * get teiid import properties through spring injection
    */
    public Map<String, String> getImportPropertyMap() {
        return importPropertyMap;
    }

    /*
    * set teiid import properties through spring injection
    */
    public void setImportPropertyMap(Map<String, String> importPropertyMap) {
        this.importPropertyMap = importPropertyMap;
    }

    /*
    * returns connector name which use for reference in teiid connector manager repository (sub data source id)
    */
    public String getConnectorName() {
        return (address + keyspace + username + port).hashCode() + "";
    }

    /*
     * returns connector manager which contains the connection and translator information
     */
    public ConnectorManager getConnectorManager() throws Exception {
        return null;
    }

    public Object getConnectionFactory() throws Exception {
        if (translatorConfig == null) {
            translatorConfig = new TranslatorConfig();
            translatorConfig.setProductName("cassandra");
            translatorConfig.setTranslatorName("cassandra");
            translatorConfig.setTranslatorFactoryClass("org.teiid.translator.cassandra.CassandraExecutionFactory");
        }
        CassandraManagedConnectionFactory cassandraManagedConnectionFactory = new CassandraManagedConnectionFactory();
        cassandraManagedConnectionFactory.setAddress(address);
        cassandraManagedConnectionFactory.setKeyspace(keyspace);
        cassandraManagedConnectionFactory.setUsername(username);
        cassandraManagedConnectionFactory.setPassword(password);
        cassandraManagedConnectionFactory.setPort(port);
        translatorConfig.setupTranslator();
        return cassandraManagedConnectionFactory.createConnectionFactory();
    }

    public Object getConnectionFactory(Map map) throws Exception {
        CassandraManagedConnectionFactory cassandraManagedConnectionFactory = new CassandraManagedConnectionFactory();
        cassandraManagedConnectionFactory.setAddress((String)map.get("hostname"));
        cassandraManagedConnectionFactory.setKeyspace((String)map.get("keyspace"));
        cassandraManagedConnectionFactory.setUsername((String) map.get("username"));
        cassandraManagedConnectionFactory.setPassword((String) map.get("password"));
        cassandraManagedConnectionFactory.setPort(new Integer((String)map.get("port")).intValue());
        return cassandraManagedConnectionFactory.createConnectionFactory();
    }

    public TranslatorConfig getTranslator() throws Exception {
        if (translatorConfig == null) {
            translatorConfig = new TranslatorConfig();
            translatorConfig.setProductName("cassandra");
            translatorConfig.setTranslatorName("cassandra");
            translatorConfig.setTranslatorFactoryClass("org.teiid.translator.cassandra.CassandraExecutionFactory");
        }
        translatorConfig.setupTranslator();
        return translatorConfig;
    }

    public String getSchemaText(Map map) throws Exception {
        return null;
    }

    public String getSchemaSourceType() throws Exception {
        return "native";
    }

    public String getTranslatorName() {
        return translatorConfig.getTranslatorName();
    }

    public ExecutionFactory getTranslatorFactory() throws TranslatorException {
        return translatorConfig.getTranslatorFactory();
    }

    /*
     * returns list of modelMetaData (schema) which is going to be available in virtual data source
     */
    public List<ModelMetaData> getModelMetaDataList() {
        ArrayList<ModelMetaData> modelMetaDataList = new ArrayList<ModelMetaData>();
        modelMetaDataList.add(getModel());
        return modelMetaDataList;
    }

    private ModelMetaData getModel() {
        String subDataSourceID = getConnectorName();
        String modelName = dataSourceName;
        ModelMetaData model = new ModelMetaData();
        model.setModelType(Model.Type.PHYSICAL);
        model.setName(modelName);
        if (importPropertyMap != null) {
            Properties importProperties = new Properties();
            for (Map.Entry<String, String> entry : importPropertyMap.entrySet()) {
                importProperties.setProperty(entry.getKey(), entry.getValue());
            }
            model.setProperties(importProperties);
        }
        model.addSourceMapping(subDataSourceID, translatorConfig.getTranslatorName(), subDataSourceID);
        return model;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }


}
