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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.dqp.internal.datamgr.ConnectorManager;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;
import org.teiid.resource.adapter.file.FileManagedConnectionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: FileTeiidDataSourceImpl.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
public class FileTeiidDataSourceImpl implements TeiidDataSource {

    String fileDirectory;
    String fileName;
    String fileSeparator = "/";
    String dataSourceName;
    String tableName;
    // syntax :  columnName_1 columnType_1, columnName_2 columnType_2, columnName_3 columnType_3
    // syntax example:  account_type string, annual_cost bigdecimal, symbol string
    String metaDataSyntax;
    String tableExtension = "_info";
    TranslatorConfig translatorConfig;

    private static final Log log = LogFactory.getLog(TeiidEmbeddedServer.class);

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMetaDataSyntax() {
        return metaDataSyntax;
    }

    public void setMetaDataSyntax(String metaDataSyntax) {
        this.metaDataSyntax = metaDataSyntax;
    }

    public TranslatorConfig getTranslatorConfig() {
        return translatorConfig;
    }

    public void setTranslatorConfig(TranslatorConfig translatorConfig) {
        this.translatorConfig = translatorConfig;
    }

    /*
    * returns connector name which use for reference in teiid connector manager repository (sub data source id)
    */
    public String getConnectorName() {
        return (fileDirectory + fileSeparator + fileName).hashCode() + "";
    }

    /*
     * returns connector manager which contains the connection and translator information
     */
    public ConnectorManager getConnectorManager() throws Exception {
        /**
        FileManagedConnectionFactory fileManagedConnectionFactory = new FileManagedConnectionFactory();
        fileManagedConnectionFactory.setParentDirectory(fileDirectory);
        fileManagedConnectionFactory.setAllowParentPaths(true);
        final BasicConnectionFactory bcf = fileManagedConnectionFactory.createConnectionFactory();
        ConnectorManager cm = new ConnectorManager(translatorConfig.getTranslatorName(), getConnectorName()) {
            @Override
            public Object getConnectionFactory() throws TranslatorException {
                return bcf;
            }
        };
        translatorConfig.setupTranslator();
        cm.setExecutionFactory(translatorConfig.getTranslatorFactory());
        return cm;
        **/
        return null;
    }

    public Object getConnectionFactory() throws Exception {
        FileManagedConnectionFactory fileManagedConnectionFactory = new FileManagedConnectionFactory();
        fileManagedConnectionFactory.setParentDirectory(fileDirectory);
        fileManagedConnectionFactory.setAllowParentPaths(true);
        translatorConfig.setupTranslator();
        return fileManagedConnectionFactory.createConnectionFactory();
    }

    public String getTranslatorName() {
        return translatorConfig.getTranslatorName();
    }

    public Object getConnectionFactory(Map map) throws Exception {
        throw new Exception("Not Supported");
    }

    public TranslatorConfig getTranslator() throws Exception {
        throw new Exception("Not Supported");
    }

    public String getSchemaText(Map map) throws Exception {
        throw new Exception("Not Supported");
    }

    public String getSchemaSourceType() throws Exception {
        throw new Exception("Not Supported");
    }

    public ExecutionFactory getTranslatorFactory() throws TranslatorException {
        return translatorConfig.getTranslatorFactory();
    }


    /*
     * returns list of modelMetaData (schema) which is going to be available in virtual data source
     */
    public List<ModelMetaData> getModelMetaDataList() {
        ArrayList<ModelMetaData> modelMetaDataList = new ArrayList<ModelMetaData>();
        modelMetaDataList.add(getFileModel());
        modelMetaDataList.add(getFileViewModel());
        return modelMetaDataList;
    }

    private ModelMetaData getFileModel() {
        String subDataSourceID = getConnectorName();
        String modelName = dataSourceName + tableExtension;
        ModelMetaData model = new ModelMetaData();
        model.setModelType(Model.Type.PHYSICAL);
        model.setName(modelName);
        model.addSourceMapping(subDataSourceID, translatorConfig.getTranslatorName(), subDataSourceID);
        return model;
    }

        // add model to virtual data source
    private ModelMetaData getFileViewModel() {
        String modelName = dataSourceName;
        String tableModelName = dataSourceName + tableExtension;
        String subDataSourceID = getConnectorName();
        ModelMetaData model = new ModelMetaData();
		model.setModelType(Model.Type.VIRTUAL);
        model.setSchemaSourceType("ddl");
        model.setName(modelName);
        String schemaText = "create view \"" + tableName + "\" OPTIONS (UPDATABLE 'true') as select " + tableName +
                ".* from (call " + tableModelName + ".getTextFiles('"+ fileName + "')) f, TEXTTABLE(f.file COLUMNS " +
                metaDataSyntax + " HEADER) " + tableName;
        log.debug("FileTeiidDataSourceImpl schema text = " + schemaText);
		model.setSchemaText(schemaText);
		return model;
	}

    public String getFileSeparator() {
        return fileSeparator;
    }

    public void setFileSeparator(String fileSeparator) {
        this.fileSeparator = fileSeparator;
    }

    public String getTableExtension() {
        return tableExtension;
    }

    public void setTableExtension(String tableExtension) {
        this.tableExtension = tableExtension;
    }
}
