package com.jaspersoft.jasperserver.api.common.service.impl;

import org.junit.Ignore;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * JdbcDriver which is created for test purposes
 * To test jdbc drivers which consists of multipple jars.
 * @see JdbcDriverServiceImplTest
 */
@Ignore
public class TestMultipleJarsJdbcDriver implements Driver {

    private TestJdbcDriver testJdbcDriver = null;

    public TestMultipleJarsJdbcDriver() {
        testJdbcDriver = new TestJdbcDriver();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return null;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

}
