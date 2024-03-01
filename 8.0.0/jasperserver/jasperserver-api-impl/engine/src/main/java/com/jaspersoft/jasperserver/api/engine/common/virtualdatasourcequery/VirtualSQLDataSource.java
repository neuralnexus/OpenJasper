/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class VirtualSQLDataSource implements javax.sql.DataSource {

    ConnectionFactory connectionFactory;
    Connection connection = null;
    private static final String TABLE_SCHEM = "TABLE_SCHEM";
    private static final String TABLE_CAT = "TABLE_CAT";
    private static final String TABLE_NAME = "TABLE_NAME";

    private static final Logger log = LogManager.getLogger(VirtualSQLDataSource.class);


    public VirtualSQLDataSource(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /*
     * return SQL connection
     */
    public Connection getConnection() throws SQLException {
            if ((connection != null) && !connection.isClosed()) return connection;
            connection = connectionFactory.createConnection();
        //    testConnection();
            return connection;
    }

     /*
     * return SQL connection
     */
    public Connection getConnection(String username, String password) throws SQLException {
            if ((connection != null) && !connection.isClosed()) return connection;
            connection = connectionFactory.createConnection();
         //   testConnection();
            return connection;
    }

    /*
     * return schema list from connection
     */
    public Set<String> getSchemas() throws SQLException {
        Set<String> schemas = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) schemas = ((VirtualSQLDataSourceMetaData)connectionFactory).getSchemas();
        if (schemas == null) return discoverSchemas(getConnection());
        else return schemas;
    }

    /*
     * return catalog list from connection
     */
    public Set<String> getCatalogs() throws SQLException  {
        Set<String> catalogs = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) catalogs = ((VirtualSQLDataSourceMetaData)connectionFactory).getCatalogs();
        if (catalogs == null) return discoverCatalogs(getConnection());
        return catalogs;
    }

    /*
    *  Teiid creates temp tables in the VDS and we should not display those temp tables in domain designer
    *  This method will go to each sub-data source and retrieve the original table list
    *  return NULL if not able to retrieve sub data source table list
    */
    public Set<String> getSubDSTableList(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData)
            return ((VirtualSQLDataSourceMetaData)connectionFactory).getSubDSTableList(catalog, schemaPattern, tableNamePattern, types);
        throw new SQLException("This method only works JDBC/ JNDI sub data source for virtual data source");
    }

    public boolean testIndividualConnections()  throws SQLException {
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData)
            return ((VirtualSQLDataSourceMetaData)connectionFactory).testIndividualConnections();
        throw new SQLException("This method only works JDBC/ JNDI sub data source for virtual data source");
    }

    /*
     * return database product name from connection
     */
    public String getDatabaseProductName() throws SQLException {
        String databaseProductName = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) databaseProductName = ((VirtualSQLDataSourceMetaData)connectionFactory).getDatabaseProductName();
        if (databaseProductName == null) return getConnection().getMetaData().getDatabaseProductName();
        return databaseProductName;
    }

    /*
     * return identifier quote string from connection
     */
    public String getIdentifierQuoteString() throws SQLException {
        String identifierQuoteString = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) identifierQuoteString = ((VirtualSQLDataSourceMetaData)connectionFactory).getIdentifierQuoteString();
        if (identifierQuoteString == null) return getConnection().getMetaData().getDatabaseProductName();
        return identifierQuoteString;
    }

    public PrintWriter getLogWriter() throws SQLException {
        PrintWriter logWriter = null;
        logWriter = new PrintWriter(new LoggerOutputStream());
        return logWriter;
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(new PrintWriter(new LoggerOutputStream()));
    }

    public void setLoginTimeout(int seconds) throws SQLException {};

    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return (new org.apache.logging.log4j.jul.LogManager()).getLogger("com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequeryVirtualSQLDataSource");
    }

    public <T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException {
        return null;
    }


    public boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException {
        return false;
    }

    // write debug messages into the log
    class LoggerOutputStream extends OutputStream {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        public void write(int b) {
            if (b != '\n') bOut.write(b);
            log.debug(bOut.toString());
        }
    }

    // find schemas from database metadata
    public static Set<String> discoverSchemas(Connection conn) throws SQLException {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = null;
        try {
            Set<String> set = new LinkedHashSet<String>();
            rs = dbMetaData.getSchemas();
            while (rs.next()) {
                String schema = rs.getString(TABLE_SCHEM);
                if (schema != null) set.add(schema);
            }
            return set;
        } catch (SQLException ex) {
            log.error("Cannot get schemas", ex);
            throw ex;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception ex) { }
            }
        }
    }

    /**
     * Retrieves all nonempty schemas from the specified connection.
     * @param conn a connection
     * @param databaseObjectTypesFilter a list of table types, which must be from the list of table types
     *         returned from {@link DatabaseMetaData#getTableTypes}, to include; <code>null</code> returns all types
     * @return
     */
    public static Set<String> discoverNonEmptySchemas(Connection conn, Set<String> databaseObjectTypesFilter, Map<String, Map<String, Set>> customSelectedSchemas, String dataSourceName) throws SQLException {

        DatabaseMetaData dbMetaData = conn.getMetaData();
        try {
            Set<String> schemaRefSet;
            Set<String> schemaSet = new LinkedHashSet<>();
            boolean checkEmptyTables;
            String dsProductName = dbMetaData.getDatabaseProductName();
            String dsUserName = conn.getMetaData().getUserName();

            //get the schemas added explicitly to be included instead of all schemas.
            checkEmptyTables = getCustomSelectedSchemas(dsProductName , dsUserName, dataSourceName, customSelectedSchemas, schemaSet);

            //return if the username have to be the schema name
            if(!schemaSet.isEmpty()) {
                if(!checkEmptyTables) {
                    return schemaSet;
                }
            }

            schemaRefSet = getResult(dbMetaData.getSchemas(), TABLE_SCHEM, schemaSet);
            String[] types = databaseObjectTypesFilter.toArray(new String[databaseObjectTypesFilter.size()]);

            for (String schema : schemaRefSet) {
                ResultSet rs2 = null;
                try {

                    rs2 = conn.getMetaData().getTables(null, schema, null, types);
                    if (rs2.next()) {
                        if (includeCurrentSchema(conn, schema)) {
                            schemaSet.add(schema);
                        }
                    } else {
                        log.debug(schema + " schema contains no table.  Ignore in VDS");
                    }

                    /***  take too long to check empty tables
                    Set<String> tableList = getResult(conn.getMetaData().getTables(null, schema, null, types), TABLE_NAME);
                    Set<String> tableWithColsList = getResult(conn.getMetaData().getColumns(null, schema, null, null), TABLE_NAME);

                    if (tableList.size() == 0) {
                        log.debug(schema + " schema contains no table.  Ignore in VDS");
                        continue;
                    } else {
                        set.add(schema);

                        // does all tables contain columns
                        boolean doesAllTablesContainCols = true;
                        for (String tableName : tableList) {
                            if (!tableWithColsList.contains(tableName)) {
                                log.debug(schema + "." +  tableName + " table contains table with no column.  Ignore in VDS");
                                doesAllTablesContainCols = false;
                                break;
                            }
                        }
                        if (doesAllTablesContainCols) set.add(schema);

                    }
                    ****/
                } catch  (SQLException ex2) {
                    log.debug("Fail to read schema, " + schema + ".  Ignore in VDS");
                    ex2.printStackTrace();

                } finally {
                    if (rs2 != null) rs2.close();
                }
            }
            return schemaSet;
        } catch (SQLException ex) {
            log.error("Cannot get schemas", ex);
            throw ex;
        }
    }

    /**
     * get the schemas to be included from the user as part of the datasource
     * @param productName
     * @param userName
     * @param customSelectedSchemas
     * @param schemaSet
     * @return
     */
    protected static boolean getCustomSelectedSchemas(String productName, String userName, String dataSourceName,  Map<String, Map<String, Set>> customSelectedSchemas, Set<String> schemaSet) {
        boolean hasAdditionalSchemas = false;

        Map<String, Map<String, Set>> customSchemasCaseIgnored = new TreeMap<>(
                String.CASE_INSENSITIVE_ORDER);

        if(customSelectedSchemas !=  null && !customSelectedSchemas.isEmpty()) {
            customSchemasCaseIgnored.putAll(customSelectedSchemas);
        }

        Map<String, Set> selectedSchemas = getSelectedSchemas(productName, dataSourceName, customSchemasCaseIgnored);


        if(selectedSchemas != null && !selectedSchemas.isEmpty()) {
            /**
             * JS-35409 Oracle DB tend to return all the available schemas in DB,
             * inorder to avoid returning all the schemas, we can use username as the schema.
             */
            if(selectedSchemas.get("usernameAsSchema") != null) {
                schemaSet.add(userName);
            }
            if(selectedSchemas.get("schemasToBeIncluded") != null) {
                hasAdditionalSchemas = true;
                Set<String> additionalSchemas = selectedSchemas.get("schemasToBeIncluded");
                if(!additionalSchemas.isEmpty() && !schemaSet.isEmpty()) {
                    schemaSet.remove(userName);
                    schemaSet.add(userName.toLowerCase());
                }

                for(String schema : additionalSchemas) {
                    schemaSet.add(schema.toLowerCase());
                }
            }
        }

        return hasAdditionalSchemas;
    }


    private static Map<String, Set> getSelectedSchemas(String productName, String dataSourceName, Map<String, Map<String, Set>> customSchemasCaseIgnored) {
        final Map<String, Set> selectedSchemas = customSchemasCaseIgnored.get(productName);

        //fetch the schemas based on dataSourceName
        if(dataSourceName != null) {
            if(selectedSchemas == null || selectedSchemas.isEmpty()) {
                return customSchemasCaseIgnored.get(dataSourceName);
            } else if (customSchemasCaseIgnored.get(dataSourceName) != null && !customSchemasCaseIgnored.get(dataSourceName).isEmpty()){
                Map<String, Set> dsNameBasedSchemas = customSchemasCaseIgnored.get(dataSourceName);

                //merge all the schemas which is fetched using both datasourceName and productName
                dsNameBasedSchemas.forEach(
                        (key, value) -> selectedSchemas.merge( key, value, (v1, v2)  -> v1.addAll(v2) ? v1 : v2)
                );
            }
        }
        return selectedSchemas;
    }

    /**
     *
     * @param conn
     * @param schema
     * @return
     */
    protected static boolean includeCurrentSchema(Connection conn, String schema) {
        try {
            String connectionURL = conn.getMetaData().getURL().toLowerCase();
            /**
             * bug JRS-19585 Cannot get context of VDS based on cassandra (simba) datasource in Domain Designer
             * for simba cassandra driver: VDS should only contains selected key space, ignore all system schemas
             */
            if (connectionURL.startsWith("jdbc:cassandra")) {
                int index = connectionURL.indexOf(";");
                if (index > 0 && index < connectionURL.length()) {
                    String paramString = connectionURL.substring(index + 1);
                    Properties p = new Properties();
                    p.load(new StringReader(paramString.replaceAll(";", "\n")));
                    String selectedSchema = p.getProperty("defaultkeyspace");
                    return (schema.equals(selectedSchema));
                }
            }
        } catch (Exception ex) {};
        return true;
    }

    private static Set<String> getResult(ResultSet rs, String columnName, Set<String> schemaSet) {
        Set<String> result = new HashSet<>();
        Set<String> configSchemas = new HashSet<>();
        try {
        while (rs.next()) {
            // filter all schemas to have the explicitly schemas to be included
            if(!schemaSet.isEmpty()) {
                if(schemaSet.contains(rs.getString(columnName).toLowerCase())) {
                    configSchemas.add(rs.getString(columnName));
                }
            }
            result.add(rs.getString(columnName));


        }
        } catch (Exception ex) {};
        try { rs.close();
        } catch (Exception ex) {};

        schemaSet.clear();

        //if explicitly included schemas are not available in the list of schemas of the DS,
        // then lets select all the schemas
        if(configSchemas.isEmpty()) {
            return result;
        }
        return configSchemas;
    }

    // find catalogs from database metadata
    public static Set discoverCatalogs(Connection conn) throws SQLException {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = null;
        try {
            Set set = new LinkedHashSet();
            rs = dbMetaData.getCatalogs();
            while (rs.next()) {
                String catalog = rs.getString(TABLE_CAT);
                set.add(catalog);
            }
            return set;
        } catch (SQLException ex) {
            log.error("Cannot get catalogs", ex);
            throw ex;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception ex) { }
            }
        }
    }

    /*
     * test connection
     */
    private void testConnection() throws SQLException {
        DatabaseMetaData dbMetaData = connection.getMetaData();
        System.out.println("MetaDataConstants.DATASOURCE_VENDOR = [" + dbMetaData.getDatabaseProductName() + "]");
        System.out.println("MetaDataConstants.NAME_QUOTE_CHAR = [" + dbMetaData.getIdentifierQuoteString() + "]");
        Set set = discoverSchemas(connection);
        for (Object val : set) System.out.println("SCHEMA = " + val);
    }
}
