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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import com.jaspersoft.jasperserver.api.common.util.CacheManager;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.CustomDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JdbcDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.VirtualDataSourceException;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.ServerConfig;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.TeiidDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.TranslatorConfiguration;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualDataSourceConfig;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualDataSourceHandler;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualSQLDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.LogConfig;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TeiidEmbeddedServer;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TeiidLogger;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TranslatorConfig;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.AwsDataSourceService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JdbcDataSourceService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.VirtualReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import org.springframework.beans.factory.InitializingBean;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.logging.LogManager;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.translator.TranslatorException;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: TeiidVirtualDataSourceQueryServiceImpl.java 48307 2014-08-15 21:38:37Z ichan $
 */
public class TeiidVirtualDataSourceQueryServiceImpl extends AbstractVirtualDataSourceQueryServiceImpl implements CacheManager, InitializingBean {

    private static TeiidEmbeddedServer INSTANCE;
    private static Map<String, String> connectionFactoryProviderTranslatorMap = new HashMap();
    static TranslatorConfig defaultTranslatorConfig = null;
    static List<TranslatorConfig> translatorConfigList = new ArrayList<TranslatorConfig>();
    static List<LogConfig> logConfigList = new ArrayList<LogConfig>();
    static List<VirtualDataSourceConfig> virtualDataSourceConfigList =  new ArrayList<VirtualDataSourceConfig>();
    String driverPrefix = "jdbc:teiid:";
    VirtualReportDataSourceServiceFactory virtualReportDataSourceServiceFactory;
    EngineService engineService;
    RepositoryService repositoryService;
    TeiidMemoryConfigImpl memoryConfig;
    ServerConfig ServerConfig;
    private EmbeddedConfiguration embeddedConfiguration = new EmbeddedConfiguration();
    private TransactionManagerConfigurationImpl transactionManagerConfiguration;
    private boolean useSubDSTableList = false;
    private Map<String, Map<String, String>> importPropertyMap;
    private boolean printVDB = true;
    private boolean printDDL = true;
    private Map<String, TeiidDataSource> dataSourceServiceToTeiidConnectorMap;
    
    public VirtualReportDataSourceServiceFactory getVirtualReportDataSourceServiceFactory() {
        return virtualReportDataSourceServiceFactory;
    }

    /*
     * set virtual report data source service factory through spring injection
     */
    public void setVirtualReportDataSourceServiceFactory(VirtualReportDataSourceServiceFactory virtualReportDataSourceServiceFactory) {
        this.virtualReportDataSourceServiceFactory = virtualReportDataSourceServiceFactory;
    }

    public EngineService getEngineService() {
        return engineService;
    }

    public void setEngineService(EngineService engineService) {
        this.engineService = engineService;
    }

    /*
    * get teiid import properties through spring injection
    */
    public Map<String, Map<String, String>> getImportPropertyMap() {
    	return importPropertyMap;
    }

    /*
    * set teiid import properties through spring injection
    */
    public void setImportPropertyMap(Map<String, Map<String, String>> importPropertyMap) {
        this.importPropertyMap = importPropertyMap;
    }

    /*
    * get data source service to teiid connector properties map through spring injection
    */
    public Map<String, TeiidDataSource> getDataSourceServiceToTeiidConnectorMap() {
        return dataSourceServiceToTeiidConnectorMap;
    }

    /*
    * set data source service to teiid connector properties map through spring injection
    */
    public void setDataSourceServiceToTeiidConnectorMap(Map<String, TeiidDataSource> dataSourceServiceToTeiidConnectorMap) {
        this.dataSourceServiceToTeiidConnectorMap = dataSourceServiceToTeiidConnectorMap;
    }

    /*
    * retrieve teiid data source from custom data source based on dataSourceServiceToTeiidConnectorMap
    */
    private TeiidDataSource getTeiidDataSource(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource subDataSource) throws TranslatorException, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (getDataSourceServiceToTeiidConnectorMap() == null) return null;
        String serviceClassName = null;
        if (subDataSource instanceof CustomDataSource) serviceClassName = ((CustomDataSource) subDataSource).getServiceClass();
        if (serviceClassName == null) return null;
        return getDataSourceServiceToTeiidConnectorMap().get(serviceClassName);
    }

    /*
    * get a handle of repository service
    */
    public RepositoryService getRepositoryService()	{
		return repositoryService;
	}

     /*
     * set repository service
     */
	public void setRepositoryService(RepositoryService repository) {
		this.repositoryService = repository;
	}

    public TeiidMemoryConfigImpl getMemoryConfig() {
        return memoryConfig;
    }

    /*
     * set teiid memory config through spring injection
     */
    public void setMemoryConfig(TeiidMemoryConfigImpl memoryConfig) {
        this.memoryConfig = memoryConfig;
    }

    public TransactionManagerConfigurationImpl getTransactionManagerConfiguration() {
        return transactionManagerConfiguration;
    }

    /*
    * set transaction manager config through spring injection
    */
    public void setTransactionManagerConfiguration(TransactionManagerConfigurationImpl transactionManagerConfiguration) {
        this.transactionManagerConfiguration = transactionManagerConfiguration;
    }

    public EmbeddedConfiguration getEmbeddedConfiguration() {
        return embeddedConfiguration;
    }

    public void setEmbeddedConfiguration(EmbeddedConfiguration embeddedConfiguration) {
        this.embeddedConfiguration = embeddedConfiguration;
    }

    public ServerConfig getServerConfig() {
        return ServerConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        ServerConfig = serverConfig;
    }

    public boolean isPrintVDB() {
        return printVDB;
    }

    public void setPrintVDB(boolean printVDB) {
        this.printVDB = printVDB;
    }

    public boolean isPrintDDL() {
        return printDDL;
    }

    public void setPrintDDL(boolean printDDL) {
        this.printDDL = printDDL;
    }

    /*
    * return to retrieve sub data source list by going through each sub datasource (instead of getting it from VDS)
    * Teiid creates "temp" tables in VDS.  Better to retrieve sub data source list from each sub DS.
    */
    public boolean isUseSubDSTableList() {
        return useSubDSTableList;
    }

    /*
    * set whether to retrieve sub data source list by going through each sub datasource or get it from VDS
    * Teiid creates "temp" tables in VDS.  Better to retrieve sub data source list from each sub DS.
    */
    public void setUseSubDSTableList(boolean useSubDSTableList) {
        this.useSubDSTableList = useSubDSTableList;
    }

    /*
    * set default translator config through spring injection
    */
    public static void setDefaultTranslatorConfig(TranslatorConfig defaultTranslatorConfig) {
        TeiidVirtualDataSourceQueryServiceImpl.defaultTranslatorConfig = defaultTranslatorConfig;
    }

    public static TranslatorConfig getDefaultTranslatorConfig() {
        return defaultTranslatorConfig;
    }

    /*
    * set teiid translator config list through spring injection
    */
    public static void setTranslatorConfigList(List<TranslatorConfig> translatorConfigList) {
        TeiidVirtualDataSourceQueryServiceImpl.translatorConfigList = translatorConfigList;
    }

    public static List<TranslatorConfig> getTranslatorConfigList() {
        return translatorConfigList;
    }

    /*
     * set teiid excluded schemas through spring injection
     */
    public static void setVirtualDataSourceConfigList(List<VirtualDataSourceConfig> virtualDataSourceConfigList) {
        TeiidVirtualDataSourceQueryServiceImpl.virtualDataSourceConfigList = virtualDataSourceConfigList;
    }

    public static List<VirtualDataSourceConfig> getVirtualDataSourceConfigList() {
        return virtualDataSourceConfigList;
    }

    /*
    * set teiid log config list through spring injection
    */
    public static void setLogConfigList(List<LogConfig> logConfigList) {
        TeiidVirtualDataSourceQueryServiceImpl.logConfigList = logConfigList;
    }

    public static List<LogConfig> getLogConfigList() {
        return logConfigList;
    }

    // deploy virtual data source into teiid embedded server
    public void deployVirtualDataSource(String virtualDSName, List<ModelMetaData> modelMetaDataList) throws SQLException {
        try {
        long time = 0;
        if (isDebugEnabled()) {
            debug("Deploy virtual data source to Teiid Server - " + virtualDSName);
            time = System.currentTimeMillis();
            if (modelMetaDataList != null) for (ModelMetaData modelMetaData : modelMetaDataList) debug("\t\tDeploy Model - " + modelMetaData.getName());
        }
        INSTANCE.deployVDB(virtualDSName, modelMetaDataList.toArray(new ModelMetaData[modelMetaDataList.size()]));
        if (isDebugEnabled()) {
            debug("Deployment time for virtual data source, " + virtualDSName + ": " + (System.currentTimeMillis() - time) + "ms");
            if (isPrintVDB()) {
                debug("VDB for " + virtualDSName + ":\n" +INSTANCE.printVDB(virtualDSName));
            }
            if (isPrintDDL()) {
                if (modelMetaDataList != null) {
                    for (ModelMetaData modelMetaData : modelMetaDataList) debug("DDL FOR " + virtualDSName + " - " + modelMetaData.getName() + " :\n" +
                            INSTANCE.getSchemaDdl(virtualDSName, modelMetaData.getName()));
                }
            }
        }
        } catch (Exception ex) {
            debug("Teiid:  failed to deploy VDB.", ex);
            try { removeSubDataSource(virtualDSName);
            } catch (Exception ex2) {};
            VirtualDataSourceException virtualDataSourceException = new VirtualDataSourceException(ex.getMessage(), ex);
            virtualDataSourceException.setVirtualDataSourceID(virtualDSName);
            if (modelMetaDataList != null) {
                LinkedHashSet<String> schemas = new LinkedHashSet<String>();
                for (ModelMetaData modelMetaData : modelMetaDataList) schemas.add(modelMetaData.getName());
                virtualDataSourceException.setSchemas(schemas);
            }
            throw virtualDataSourceException;
        }
    }

    // create connection factory from sub data sources
    public synchronized ConnectionFactory createConnectionFactory(String virtualDSName, List<String> dataSourceNames, List<String> subDataSourceIDs,
                                                     List<com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource> subDataSources, String virtualDataSourceUri) throws Exception {
        ConnectionFactory teiidConnectionFactory = null;
        Map<String, com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource> dataSourceMap = new HashMap();
        for (int i = 0; i < dataSourceNames.size(); i++) {
            dataSourceMap.put(dataSourceNames.get(i), subDataSources.get(i));
        }
        try {
        if (!isVirtualDataSourceExisted(virtualDSName)) {
            // if virtual data source doesn't exist in teiid server, build a new one
            debug("Build virtual data source METADATA - " + virtualDSName);
            ArrayList<ModelMetaData> modelMetaDataList = new ArrayList<ModelMetaData>();
            // loop through each sub data source
            VirtualDataSourceConfig virtualDataSourceConfig = null;
            if (virtualDataSourceUri != null) virtualDataSourceConfig = getVirtualDataSourceConfig(virtualDataSourceUri);
            else virtualDataSourceConfig = getVirtualDataSourceConfig(getParentDataSourceURI(subDataSources.get(0)));
            for (int i = 0; i < subDataSourceIDs.size(); i++) {
                // retrieve translator name from connector manager

                String translatorName = connectionFactoryProviderTranslatorMap.get(subDataSourceIDs.get(i));
                // set connection name
                String connectionName =  subDataSourceIDs.get(i);
                // try to get the complete schemas list from each sub data source
                Set availableSchemaList = null;
                try {
                    availableSchemaList = VirtualSQLDataSource.discoverNonEmptySchemas(getDataSource(subDataSources.get(i)).getConnection());
                } catch(SQLException ex) {
                    debug("Unable to read the schema list from data source!");
        //            throw new VirtualDataSourceException(ex);
                } catch (Exception ex) {
                    debug("Unable to read the schema list from data source!");
        //            throw new VirtualDataSourceException("Teiid:  cannot get the schema list for sub data source, " + subDataSources.get(i), ex);
                }

                if ((availableSchemaList == null) || (availableSchemaList.size() == 0)) {
                    // if schema list is not available, add the whole data source
                    modelMetaDataList.add(addModel(null, dataSourceNames.get(i), subDataSourceIDs.get(i), translatorName, connectionName, subDataSources.get(i)));
                }
                else {
                    // only add selected schema into virtual data source model metadata list
                    for (Object schemaName : availableSchemaList) {
                        String virtualSchemaName = dataSourceNames.get(i) +  VirtualDataSourceHandler.getDataSourceSchemaSeparator() + schemaName;
                        // do not add excluded schema into modelMetaDataList
                        if ((virtualDataSourceConfig != null) && virtualDataSourceConfig.isSchemaExcluded(virtualSchemaName)) continue;
                        // only add selected schema into modelMetaDataList
                        if (!isSelectedSchema((String)schemaName, subDataSources.get(i))) continue;
                        modelMetaDataList.add(addModel((String)schemaName, dataSourceNames.get(i), subDataSourceIDs.get(i), translatorName, connectionName, subDataSources.get(i)));
                    }
                }
            }
            // add additional data source which comes from spring injection
            if ((virtualDataSourceConfig != null) && (virtualDataSourceConfig.getAdditionalDataSourceList() != null)) {
                for (TeiidDataSource additionalDataSource : virtualDataSourceConfig.getAdditionalDataSourceList()) {
                    if (!isSubDataSourceExisted(additionalDataSource.getConnectorName())) {
                        INSTANCE.addConnectionFactory(additionalDataSource.getConnectorName(),  additionalDataSource.getConnectionFactory());
                        INSTANCE.addTranslator(additionalDataSource.getTranslatorName(), additionalDataSource.getTranslatorFactory());
                        connectionFactoryProviderTranslatorMap.put(additionalDataSource.getConnectorName(), additionalDataSource.getTranslatorName());
                    }
                    modelMetaDataList.addAll(additionalDataSource.getModelMetaDataList());
                }
            }
            // return teiid connection factory
            teiidConnectionFactory = new TeiidConnectionFactoryImpl(this, virtualDSName, modelMetaDataList, dataSourceMap);
        } else {
            // return teiid connection factory
            teiidConnectionFactory = new TeiidConnectionFactoryImpl(this, virtualDSName, null, dataSourceMap);
        }
        return teiidConnectionFactory;
        } catch (Exception ex) {
            throw ex;
        }
    }

    // put additional data source information into vds key in order to guarantee each DSs combination gets its unique key
    public String getAdditionalInformation(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource subDataSource) throws VirtualDataSourceException {
        return getAdditionalInformation(getParentDataSourceURI(subDataSource));
    }

    // put additional data source information into vds key in order to guarantee each DSs combination gets its unique key
    public String getAdditionalInformation(String dataSourceURI) throws VirtualDataSourceException {
        VirtualDataSourceConfig virtualDataSourceConfig = getVirtualDataSourceConfig(dataSourceURI);
        StringBuffer additionalInfo = new StringBuffer();
        if ((virtualDataSourceConfig != null) && (virtualDataSourceConfig.getAdditionalDataSourceList() != null)) {
            for (TeiidDataSource additionalDataSource : virtualDataSourceConfig.getAdditionalDataSourceList()) {
                    additionalInfo.append(additionalDataSource.getConnectorName() + ";");
            }
        }
        return additionalInfo.toString();
    }


    // remove virtual data source
    public void undeployVirtualDataSource(String virtualDSName) throws Exception {
        debug("Remove virtual data source - " + virtualDSName);
        INSTANCE.undeployVDB(virtualDSName);
	}


    // remove all virtual data source cache
    public void clearVDSCache() {
        debug("Clear virtual data source cache.");
        INSTANCE.clearAllVDBs();
	}

    public boolean isVDSCacheEmpty() {
       return (INSTANCE.countVDBs() == 0);
    }

    // returns whether sub data source exists
    public boolean isSubDataSourceExisted(String subDataSourceName) {
        return (connectionFactoryProviderTranslatorMap.get(subDataSourceName) != null);
    }

    // add sub data source to connector manager in teiid embedded server
    public void addSubDataSource(String subDataSourceName, com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource subDataSource) throws Exception {
        debug("Add sub data source - " + subDataSourceName);
        final Object dataSource = getConnectionFactory(subDataSource);
        String connectionName = subDataSourceName;
        TranslatorConfiguration translatorConfig = getTranslator(subDataSource, dataSource);
        INSTANCE.addConnectionFactory(connectionName, dataSource);
        debug("Connection Name = " + connectionName);
		INSTANCE.addTranslator(translatorConfig.getTranslatorName(), translatorConfig.getTranslatorFactory());
        connectionFactoryProviderTranslatorMap.put(connectionName, translatorConfig.getTranslatorName());
    }

    // remove sub data source from connector manager in teiid embedded server
    public void removeSubDataSource(String subDataSourceName) throws Exception {
        debug("Remove sub data source - " + subDataSourceName);
        connectionFactoryProviderTranslatorMap.remove(subDataSourceName);
    }

    // returns whether virtual data source exists
    public boolean isVirtualDataSourceExisted(String virtualDSName) {
        return INSTANCE.isVDBExisted(virtualDSName);
    }

    // create connection for virtual data source
    protected Connection createConnection(String virtualDataSourceName) throws SQLException {
        debug("Create Connection - " + virtualDataSourceName);
        return INSTANCE.createConnection(driverPrefix + virtualDataSourceName);
    }

    private boolean isSelectedSchema(String schemaName, com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource reportDataSource) {
        Set<String> schemaSet = getSchemaSet(reportDataSource);
        if (schemaSet == null) return true;
        return schemaSet.contains(schemaName);
    }


    public void init() {
        if (INSTANCE == null) startServer();
    }

    private void startServer() {
        setLogConfig();
        INSTANCE = new TeiidEmbeddedServer(embeddedConfiguration, memoryConfig, ServerConfig, transactionManagerConfiguration);
    //    cmr = new ConnectorManagerRepository();
    //    INSTANCE.setConnectorManagerRepository(cmr);
    }

    static public TeiidEmbeddedServer getServer() {
        return INSTANCE;
    }

    private void setLogConfig() {
        TeiidLogger logger = new TeiidLogger();
        for (LogConfig config : logConfigList) {
            logger.setLogLevel(config.getLogContext(), config.getMessageLevel());
        }
        LogManager.setLogListener(logger);
    }

    // get and setup translator config for each sub data source
    private TranslatorConfiguration getTranslator(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource subDataSource, Object dataSource) throws Exception {
        TeiidDataSource teiidDataSource = getTeiidDataSource(subDataSource);
        if (teiidDataSource != null) {
            try {
                return teiidDataSource.getTranslator();
            } catch (Exception ex) {
                throw new VirtualDataSourceException("Teiid Virtual Data Source Query Service - unable to obtain translator", ex);
            }
        } else if (dataSource instanceof  DataSource) {
            return getTranslator((DataSource)dataSource);
        } else {
            throw new VirtualDataSourceException("Teiid Virtual Data Source Query Service - unable to obtain translator");
        }
    }

    // get and setup translator config for each sub data source
    private TranslatorConfig getTranslator(DataSource dataSource) throws Exception {
        String productName;
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        productName =  metaData.getDatabaseProductName().toLowerCase();
  
        String productVersion = null;
        debug("Getting translator for database " + productName);
        // loop through translator config list to find matching translator
        for (TranslatorConfig config : translatorConfigList) {
			if (productName.indexOf(config.getProductName().toLowerCase()) >= 0) {
                if (config.getProductVersion() != null) {
                    productVersion = metaData.getDatabaseProductVersion().toLowerCase();
                    debug("Getting translator for database " + productName + (productVersion != null? " " + productVersion : ""));
                    if (productVersion == null) {
                        productVersion = metaData.getDatabaseProductVersion().toLowerCase();
                        debug("Database Version: " + productVersion);
                    }
                    if (productVersion.indexOf(config.getProductVersion().toLowerCase()) < 0) continue;
                }
                // setup translator
				config.setupTranslator();
                return config;
			}
		}
        // use default translator if it is available
        if (defaultTranslatorConfig != null) {
            debug("Teiid - cannot find matching translator for database " + productName + (productVersion != null? " " + productVersion : "") + ".  Use default translator instead.");
            defaultTranslatorConfig.setupTranslator();
            return defaultTranslatorConfig;
        }
        // if translator is not found, throws exception
        debug("ERROR:  Teiid - cannot find matching translator for database " + productName + (productVersion != null? " " + productVersion : ""));
        throw new VirtualDataSourceException("Teiid - cannot find matching translator for database " + productName + (productVersion != null? " " + productVersion : ""));
    }

    // add model to virtual data source
    private ModelMetaData addModel(String schema, String modelName, String connectorName, String translatorName, String connectionName,
            com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource dataSource) {
        ModelMetaData model = new ModelMetaData();
		model.setModelType(Model.Type.PHYSICAL);
        Properties importProperties = new Properties();
        debug("Adding Model - SCHEMA = [" + schema + "], MODELNAME = [" + modelName + "], CONNECTORNAME = [" + connectionName + "], CONNECTIONNAME = [" + connectionName + "]");
        importProperties.setProperty("importer.useFullSchemaName", Boolean.FALSE.toString());
        debug("TEIID IMPORT PROPERTY [import.userFullSchemaName, " + Boolean.FALSE.toString() + "]");
        importProperties.setProperty("importer.trimColumnNames", Boolean.TRUE.toString());
        debug("TEIID IMPORT PROPERTY [import.trimColumnNames, " + Boolean.TRUE.toString() + "]");
        if (schema != null) {
            model.setName(modelName + VirtualDataSourceHandler.getDataSourceSchemaSeparator() + schema);
            importProperties.setProperty("importer.schemaPattern", schema);
            debug("TEIID IMPORT PROPERTY [import.schemaPattern, " + schema + "]");

        } else {
            model.setName(modelName);
        }
        // import property from spring injection
        if (importPropertyMap != null) {
        Map<String, String> propertyMapForDataSourceModel = importPropertyMap.get(modelName);
            if (propertyMapForDataSourceModel != null) {
                for (Map.Entry<String, String> entry : propertyMapForDataSourceModel.entrySet()) {
                    importProperties.setProperty(entry.getKey(), entry.getValue());
                    debug("TEIID IMPORT PROPERTY [" + entry.getKey() + ", " + entry.getValue() + "]");
                }
            }
        }
        model.setProperties(importProperties);
        String schemaText = null;
        String schemaType = "native";
        try {
            if (dataSource instanceof CustomDataSource) {
                TeiidDataSource teiidDataSource = getTeiidDataSource(dataSource);
                schemaText = teiidDataSource.getSchemaText(((CustomDataSource) dataSource).getPropertyMap());
                schemaType = teiidDataSource.getSchemaSourceType();
            }
        } catch (Exception ex) {
            log.debug("Fail to retrieve schema content: set schema source type to NATIVE");
        }

        model.setSchemaSourceType(schemaType);
        if (schemaText != null) {
            log.debug("SCHEMA CONTENT = " + schemaText);
            model.setSchemaText(schemaText);
        }
        model.addSourceMapping(connectorName, translatorName, connectionName);
		return model;
	}

    private VirtualDataSourceConfig getVirtualDataSourceConfig(String virtualDataSourceURI) {
        if (virtualDataSourceConfigList == null) return null;
        for (VirtualDataSourceConfig virtualDataSourceConfig : virtualDataSourceConfigList) {;
            String datSourceURIInternal =  repositoryService.transformPathToExternal(virtualDataSourceConfig.getDataSourceURI());
            debug("Transform LOOKUP RESOURCE from " + virtualDataSourceConfig.getDataSourceURI() + " to " + datSourceURIInternal);
            if (virtualDataSourceURI.toLowerCase().endsWith(datSourceURIInternal.toLowerCase())) {
                return virtualDataSourceConfig;
            }
        }
        return null;
    }

    private String getParentDataSourceURI(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource dataSource) throws VirtualDataSourceException {
        if (!(dataSource instanceof DataSourceImpl)) {
             throw new VirtualDataSourceException("Teiid Virtual Data Source Query Service - unable to generate data source from " + dataSource.getDataSourceName());
        }
        return ((DataSourceImpl)dataSource).getParentDataSource().getURIString();
    }

    // return connection factory for different data source
    protected Object getConnectionFactory(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource dataSource) throws VirtualDataSourceException {
        try {
            if (dataSource instanceof CustomDataSource) {
                TeiidDataSource teiidDataSource = getTeiidDataSource(dataSource);
                if (teiidDataSource != null) return teiidDataSource.getConnectionFactory(((CustomDataSource) dataSource).getPropertyMap());
            }
        } catch (Exception ex) {
            throw new VirtualDataSourceException("Teiid Virtual Data Source Query Service - unable to create connection factory", ex);
        }
        return getDataSource(dataSource);
    }

    // get javax.sql.DataSource from virtualdatasourcequery.DataSource
    protected DataSource getDataSource(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource dataSource) throws VirtualDataSourceException {
        if (!(dataSource instanceof DataSourceImpl)) {
             throw new VirtualDataSourceException("Teiid Virtual Data Source Query Service - unable to generate data source from " + dataSource.getDataSourceName());
        }
        ReportDataSourceService reportDataSourceService = null;
        if (dataSource instanceof CustomDataSource) {
            throw new VirtualDataSourceException("Teiid  Virtual Data Source Query Service - unsupported data source: " + dataSource.getDataSourceName());
        } else if (dataSource instanceof JdbcDataSource) {
            // do not share the same JDBC pool with standalone JDBC connection
            reportDataSourceService = virtualReportDataSourceServiceFactory.createService(((DataSourceImpl)dataSource).getReportDataSource());
        } else {
            // use default data source service based on report data source type
            reportDataSourceService = engineService.createDataSourceService(((DataSourceImpl)dataSource).getReportDataSource());
        }
        if (reportDataSourceService instanceof AwsDataSourceService) {
            return ((AwsDataSourceService) reportDataSourceService).getDataSource();
        } else if (reportDataSourceService instanceof JdbcDataSourceService) {
            return ((JdbcDataSourceService) reportDataSourceService).getDataSource();
        } else {
           throw new VirtualDataSourceException("Teiid  Virtual Data Source Query Service - unsupported data source: " + dataSource.getDataSourceName());
        }
    }

    @Override
    public String getCacheId() {
        return "vds";
    }

    @Override
    public void clear() {
        clearVDSCache();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
