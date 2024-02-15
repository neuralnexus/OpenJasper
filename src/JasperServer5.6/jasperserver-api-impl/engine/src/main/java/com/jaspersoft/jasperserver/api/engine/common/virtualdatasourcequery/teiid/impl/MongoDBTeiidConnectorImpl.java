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
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectCache;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.dqp.internal.datamgr.ConnectorManager;
import org.teiid.resource.adapter.mongodb.MongoDBConnectionImpl;
import org.teiid.resource.adapter.mongodb.MongoDBManagedConnectionFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: MongoDBTeiidConnectorImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class MongoDBTeiidConnectorImpl implements TeiidDataSource {

    String remoteServerList;
    String username;
    String password;
    String database;
    String dataSourceName;
    String schemaText;
//    private PooledObjectCache mongoDBConnectionCache = new PooledObjectCache();
//    private int poolTimeoutInMinute;
    TranslatorConfig translatorConfig;
    private Map<String, String> importPropertyMap;
    private static final Log log = LogFactory.getLog(TeiidEmbeddedServer.class);

    public void setRemoteServerList( String remoteServerList ) {
        this.remoteServerList = remoteServerList;
    }

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String googlePassword) {
		this.password = googlePassword;
	}

	public String getDatabase() {
		return this.database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

    public String getSchemaText() {
        return schemaText;
    }

    public void setSchemaText(String schemaText) {
        this.schemaText = schemaText;
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
        return (database).hashCode() + "";
    }

    /*
     * returns connector manager which contains the connection and translator information
     */
    public ConnectorManager getConnectorManager() throws Exception {
        return null;
    }

    public Object getConnectionFactory() throws Exception {
        MongoDBManagedConnectionFactory mongoDBManagedConnectionFactory = new MongoDBManagedConnectionFactory();
        if (translatorConfig == null) {
            translatorConfig = new TranslatorConfig();
            translatorConfig.setProductName("mongodb");
            translatorConfig.setTranslatorName("mongodb");
            translatorConfig.setTranslatorFactoryClass("org.teiid.translator.mongodb.MongoDBExecutionFactory");
        }
        mongoDBManagedConnectionFactory.setRemoteServerList(remoteServerList);
        mongoDBManagedConnectionFactory.setUsername(username);
        mongoDBManagedConnectionFactory.setPassword(password);
        mongoDBManagedConnectionFactory.setDatabase(database);
        translatorConfig.setupTranslator();
        return mongoDBManagedConnectionFactory.createConnectionFactory();
    }

    public String getTranslatorName() {
        return translatorConfig.getTranslatorName();
    }

    public Object getConnectionFactory(Map map) throws Exception {
        MongoDBManagedConnectionFactory mongoDBManagedConnectionFactory = new MongoDBManagedConnectionFactory();
        String mongoURI = (String) map.get("mongoURI");
        int databaseIndex = mongoURI.lastIndexOf("/");
        String databaseName = mongoURI.substring(databaseIndex + 1);
        String remoteServerList = mongoURI.substring(10, databaseIndex);
        String userName = (String) map.get("username");
        String password = (String) map.get("password");
        mongoDBManagedConnectionFactory.setRemoteServerList(remoteServerList);
        if ((userName != null) && !userName.equals("")) mongoDBManagedConnectionFactory.setUsername(userName);
        if ((password != null) && !password.equals(""))mongoDBManagedConnectionFactory.setPassword(password);
        mongoDBManagedConnectionFactory.setDatabase(databaseName);
        return mongoDBManagedConnectionFactory.createConnectionFactory();
    }

    public TranslatorConfig getTranslator() throws Exception {
        if (translatorConfig == null) {
            translatorConfig = new TranslatorConfig();
            translatorConfig.setProductName("mongodb");
            translatorConfig.setTranslatorName("mongodb");
            translatorConfig.setTranslatorFactoryClass("org.teiid.translator.mongodb.MongoDBExecutionFactory");
        }
        translatorConfig.setupTranslator();
        return translatorConfig;
    }

    public String getSchemaText(Map map) throws Exception {
        return (String)map.get("schema");
    }

    public String getSchemaSourceType() throws Exception {
        return "DDL";
    }

    public ExecutionFactory getTranslatorFactory() throws TranslatorException {
        return translatorConfig.getTranslatorFactory();
    }

    /*
     * returns list of modelMetaData (schema) which is going to be available in virtual data source
     */
    public List<ModelMetaData> getModelMetaDataList() {
        ArrayList<ModelMetaData> modelMetaDataList = new ArrayList<ModelMetaData>();
        modelMetaDataList.add(getMongoDBModel());
        return modelMetaDataList;
    }

    private ModelMetaData getMongoDBModel() {
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
        model.setSchemaSourceType("DDL");
        log.debug("SCHEMA = " + schemaText);
        model.setSchemaText(schemaText);
        model.addSourceMapping(subDataSourceID, translatorConfig.getTranslatorName(), subDataSourceID);
        return model;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }


/****

    public Object getConnectionFactory(Map map) throws Exception {
        long now = System.currentTimeMillis();
        Object connectionFactory = getConnectionFactory(map, now);
        releaseExpiredPools(connectionFactory);
        return connectionFactory;
    }


    class PooledMongoDBConnectionEntry extends PooledObjectEntry {

        public PooledMongoDBConnectionEntry(Object key) {
            super(key);
        }

        public void release() throws Exception {
        }

        public Object getOriginalKey() {
            return getKey();
        }
    }

    // mark data source is in used and update last used time
    protected Object getConnectionFactory(Map map, long now) throws Exception {
        String poolKey = buildKey(map);
        synchronized (mongoDBConnectionCache) {
            PooledMongoDBConnectionEntry pooledObjectEntry = (PooledMongoDBConnectionEntry) mongoDBConnectionCache.get(poolKey, now);

            if (pooledObjectEntry == null) {
                MongoDBManagedConnectionFactory mongoDBManagedConnectionFactory = new MongoDBManagedConnectionFactory();
                String mongoURI = (String) map.get("mongoURI");
                int databaseIndex = mongoURI.lastIndexOf("/");
                String databaseName = mongoURI.substring(databaseIndex + 1);
                String remoteServerList = mongoURI.substring(10, databaseIndex);
                String userName = (String) map.get("username");
                String password = (String) map.get("password");
                mongoDBManagedConnectionFactory.setRemoteServerList(remoteServerList);
                if ((userName != null) && !userName.equals("")) mongoDBManagedConnectionFactory.setUsername(userName);
                if ((password != null) && !password.equals(""))mongoDBManagedConnectionFactory.setPassword(password);
                mongoDBManagedConnectionFactory.setDatabase(databaseName);
                Object mongoDBConnectionFactory = mongoDBManagedConnectionFactory.createConnectionFactory();
                mongoDBConnectionCache.put(poolKey, new PooledMongoDBConnectionEntry(mongoDBConnectionFactory), now);
                log.debug("Acquire MongoDB Connection Pool Key: " + poolKey + ".");
                return mongoDBConnectionFactory;
            } else {
                log.debug("Update MongoDB Connection Pool Key: " + poolKey + ".");
                return pooledObjectEntry.getOriginalKey();
            }
        }
    }

    private String buildKey(Map map) {
        return map.toString();
    }

    // release expired data source and remove from server
    protected void releaseExpiredPools(long now) {
        if (getPoolTimeoutInMinute() <= 0) return;
		List expired = null;
		synchronized (mongoDBConnectionCache) {
		    expired = mongoDBConnectionCache.removeExpired(now, getPoolTimeoutInMinute() * 60);
		}

		if (expired != null && !expired.isEmpty()) {
			for (Iterator it = expired.iterator(); it.hasNext();) {
				PooledMongoDBConnectionEntry ds = (PooledMongoDBConnectionEntry) it.next();
				try {
                    // remove data source
					ds.release();
				} catch (Exception e) {
					log.error("Error while releasing Virtual Pool Key.", e);
					// ignore
				}
			}
		}
	}

    public int getPoolTimeoutInMinute() {
        return poolTimeoutInMinute;
    }

    public void setPoolTimeoutInMinute(int poolTimeoutInMinute) {
        this.poolTimeoutInMinute = poolTimeoutInMinute;
    }
****/
}
