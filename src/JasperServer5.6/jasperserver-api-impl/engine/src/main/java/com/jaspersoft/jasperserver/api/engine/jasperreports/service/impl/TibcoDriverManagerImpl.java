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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.common.util.TibcoDriverManager;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.DelegatingConnection;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Enumeration;

public class TibcoDriverManagerImpl implements TibcoDriverManager {

    private static final Log log = LogFactory.getLog(TibcoDriverManagerImpl.class);
    private static TibcoDriverManagerImpl instance;
    private Class jndiConnectionUtil;
    private Boolean tibcoDriverExists = null;
    private Method unlockConnectionMethod;
    private Method getInnerConnectionFromAppServerClassMethod;
    private Method isTibcoConnectionMethod;
    private Boolean isWebSphere;
    private Boolean isWebLogic;
    private Boolean isLicenseManagerAvailable;
    private Object LicenseManager;


    private TibcoDriverManagerImpl() {
        preLoadDrivers();
        printDrivers();
    }

    public static TibcoDriverManager getInstance() {
        if (instance == null) instance = new TibcoDriverManagerImpl();
        return instance;
    }

    public Connection unlockConnection(Connection connection) throws java.sql.SQLException {
        return connection;
    }

    public Connection unlockConnection(DataSource dataSource) throws SQLException {
        Connection originalConnection = getOriginalConnection(dataSource);
        if (unlockDSConnection(originalConnection)) return originalConnection;
        GenericObjectPool connectionPool = new GenericObjectPool(null);
        DataSourceConnectionFactory connectionFactory = new DataSourceConnectionFactory(dataSource);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        PoolingDataSource ds = new PoolingDataSource(connectionPool);
        if (ds != null) {
            Connection connection = getOriginalConnection(ds);
            ds.setAccessToUnderlyingConnectionAllowed(true);
            if (unlockDSConnection(connection)) return connection;
        }
        return originalConnection;
    }

    public boolean unlockDSConnection(Connection originalConnection) throws SQLException{
        if (originalConnection == null) return true;
        if (!isTibcoDriverExisted()) return true;
		if (!isWebLogic() && !isWebSphere()) return true;
        try {
            testingConnection(originalConnection);
            return true;
        } catch (SQLException ex) {};
        // try to look for the innermost connection and unlock for weblogic and websphere
        try {
            printLog("TibcoDriverManagerImpl Connection INFO = " + originalConnection.toString());
            if (unlockInnerConnection(originalConnection)) {
                return true;
            }
        } catch (Exception ex) {
            printLog("Fail to unlock Data Source Connection", ex);
        }
        return false;
    }

    private boolean unlockInnerConnection(Connection originalConnection) throws SQLException{
        if (originalConnection == null) return false;
        printLog("ORIGINAL CONNECTION CLASS - " + originalConnection.getClass().toString());
    //    printAvailableMethods(originalConnection);
        try {
                if (originalConnection instanceof DelegatingConnection) {
                    printLog("Get innermost connection...");
                    Connection innerConn = ((DelegatingConnection)originalConnection).getInnermostDelegate();
                    if (innerConn != null) return unlockInnerConnection(innerConn);
                } else if ((Boolean)isTibcoConnection().invoke(null, originalConnection)) {
                    printLog("TibcoDriverManagerImpl Tibco connection..." + originalConnection.getClass().toString());
                    boolean isSuccessful = unlockTibcoConnection(originalConnection);
                    printLog("UNLOCK successful? " + isSuccessful);
                    return isSuccessful;
                } else {
                    Connection innerConn =  (Connection) getInnerConnectionFromAppServerClass().invoke(null, originalConnection, JndiJdbcReportDataSourceServiceFactory.getJndiAppServerConnectionFunctionMap());
                    if (innerConn != null) return unlockInnerConnection(innerConn);
                    printLog("Fail to find Tibco Connection");
                }
        } catch (SQLFeatureNotSupportedException sqlFeatureNotSupportedEx) {
            // non-tibco drivers
        } catch (SQLException ex) {
            printLog("Fail to unlock data source.  Connection class = " + originalConnection.getClass().toString(), ex);
        } catch (Throwable throwable) {
            printLog("Fail to unlock data source.", throwable);
        }
        return false;
    }

    private boolean unlockTibcoConnection(Connection connection) throws SQLException {
        try {
            /***
            LocalJasperReportsContext jasperReportsContext = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
            LicenseManager licenseManager = LicenseManager.cachedInstance(jasperReportsContext);
            ***/
            return (Boolean) unlockConnection().invoke(null, connection, this.getClass(), getLicenseManager());
        } catch (Exception ex) {
            printLog("Fail to unlock Tibco Connection ", ex);
    //        ex.printStackTrace();
        }
        return false;
    }


    private Object getJasperReportsContext() throws Exception {
         LocalJasperReportsContext jasperReportsContext = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        return jasperReportsContext;
    }

    private static void printLog(String str) {
        log.debug(str);
    //    System.out.println(str);
    }

    private static void printLog(String str, Throwable error) {
        log.debug(str, error);
    }

    /***
    private static void printAvailableMethods(Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
        //    System.out.println("METHOD - " + method.getName());
            if (method.getName().equals("getNativeConnection")) {
                System.out.println("METHOD - " + method.getName());
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (Class parameterType : parameterTypes) {
                    System.out.println("TYPE =  " + parameterType);
                }
            }
        }
    }
    ***/

    private Connection getOriginalConnection(DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }

    private Method isTibcoConnection() throws Exception {
        if (isTibcoConnectionMethod == null) {
            isTibcoConnectionMethod = getJNDIConnectionUtilMethod("isTibcoConnection");
        }
        return isTibcoConnectionMethod;
    }

    private Method unlockConnection() throws Exception {
        if (unlockConnectionMethod == null) {
            unlockConnectionMethod = getJNDIConnectionUtilMethod("unlockConnection");
        }
        return unlockConnectionMethod;
    }

    private Method getInnerConnectionFromAppServerClass() throws Exception {
        if (getInnerConnectionFromAppServerClassMethod == null) {
            getInnerConnectionFromAppServerClassMethod = getJNDIConnectionUtilMethod("getInnerConnectionFromAppServerClass");
        }
        return getInnerConnectionFromAppServerClassMethod;
    }

    private Method getJNDIConnectionUtilMethod(String methodName) throws Exception {
        Class jndiConnectionUtilClass = getJNDIConnectionUtil();
        if (jndiConnectionUtilClass == null) return null;
        Method[] methods = jndiConnectionUtilClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new Exception("Invalid Method: " + methodName);
    }

    private boolean isTibcoDriverExisted() {
        if (tibcoDriverExists == null) getJNDIConnectionUtil();
        return tibcoDriverExists;
    }

    private Class getJNDIConnectionUtil() {
        if (tibcoDriverExists == null) {
            try {
                jndiConnectionUtil = Class.forName("tibcosoftware.jdbc.common.JNDIConnectionUtil");
                tibcoDriverExists = true;
            } catch (Throwable ex) {
                tibcoDriverExists = false;
            }
        }
        return jndiConnectionUtil;
    }

    public boolean isWebSphere() {
        if (isWebSphere != null) return isWebSphere;
        try {
            Class wsCallHelper = Class.forName("com.ibm.websphere.rsadapter.WSCallHelper");
            isWebSphere = (wsCallHelper != null);
        } catch (ClassNotFoundException ex) {
            isWebSphere = false;

        }
        return isWebSphere;
    }

    public boolean isWebLogic() {
        if (isWebLogic != null) return isWebLogic;
        try {
            Class wsCallHelper = Class.forName("weblogic.jdbc.wrapper.Connection");
            isWebLogic = (wsCallHelper != null);
        } catch (ClassNotFoundException ex) {
            isWebLogic = false;

        }
        return isWebLogic;
    }

    private void testingConnection(Connection originalConnection) throws SQLException {
        Statement statement = originalConnection.createStatement();
        try { statement.close();
        } catch (Throwable ex2) {};
    }

    private Object getLicenseManager() {
        if (isLicenseManagerAvailable == null) {
            try {
                Method LicenseManagerMethod = Class.forName("com.jaspersoft.jasperreports.license.LicenseManager").getMethod("cachedInstance", JasperReportsContext.class);
                LicenseManager = LicenseManagerMethod.invoke(null, getJasperReportsContext());
                isLicenseManagerAvailable = true;
            } catch (Throwable ex) {
                isLicenseManagerAvailable = false;
            }
        }
        return LicenseManager;
    }

    static public void preLoadDrivers() {
        printLog("Pre-load Drivers");
        registerDriver("tibcosoftware.jdbc.oracle.OracleDriver");
        registerDriver("tibcosoftware.jdbc.sqlserver.SQLServerDriver");
        registerDriver("tibcosoftware.jdbc.db2.DB2Driver");
        registerDriver("tibcosoftware.jdbc.hive.HiveDriver");
        deregisterTibcosoftwareincDrivers();
    }

    static public void printDrivers() {
        printLog("PRINT DRIVERS...");
        Enumeration<Driver> e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            String driverClass = (String) ((Driver)e.nextElement()).getClass().getName();
            printLog(driverClass);
        }
    }

    static void registerDriver(String driverClassName) {
        if (isDriverExisted(driverClassName)) return;
        Class<?> tibcoDriver = null;
        Constructor<?> tdConstructor = null;
        Driver driver = null;
        if(driverClassName!=null){
            try {
                tibcoDriver = Class.forName(driverClassName);
                tdConstructor = tibcoDriver.getDeclaredConstructor();
                driver = (Driver) tdConstructor.newInstance((Object[])null);
                DriverManager.registerDriver(driver);
            } catch (Exception ex) {}
            if(tibcoDriver==null)  printLog("Couldn't Class.forName for " + driverClassName);
            else if(tdConstructor == null) printLog("Couldn't get constructor for " + driverClassName);
            else if(driver==null) printLog("Couldn't instantiate " + driverClassName);
        }
    }

    static boolean isDriverExisted(String driverClass) {
        Enumeration<Driver> e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            Driver driver = (Driver)e.nextElement();
            String driverClassName = (String) driver.getClass().getName();
            if (driverClassName.equals(driverClass)) return true;
        }
        return false;
    }

    static void deregisterTibcosoftwareincDrivers() {
        Enumeration<Driver> e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            Driver driver = (Driver)e.nextElement();
            String driverClassName = (String) driver.getClass().getName();
            try {
            if (driverClassName.startsWith("tibcosoftwareinc.")) DriverManager.deregisterDriver(driver);
            } catch (Exception ex) {
                printLog("Unable to deregister driver: " + driverClassName, ex);
            }
        }
    }
}

