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
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.springframework.context.MessageSource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: JdbcConnectionStrategyTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JdbcConnectionStrategyTest {
    private static ClientJdbcDataSource INITIAL_CONNECTION_DESCRIPTION = new ClientJdbcDataSource()
            .setConnectionUrl("someUrl").setUsername("someUsername").setPassword("somePassword")
            .setDriverClass("someDriverClass").setUri("/test/resource/uri");
    @InjectMocks
    private JdbcConnectionStrategy strategy = new JdbcConnectionStrategy();
    private JdbcConnectionStrategy strategySpy;
    @Mock
    private JdbcDriverService jdbcDriverService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private RepositoryService repository;
    @Mock
    private Connection connection;
    private ClientJdbcDataSource testConnectionDescription;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        strategySpy = spy(strategy);
    }
    @BeforeMethod
    public void refresh() throws SQLException {
        reset(jdbcDriverService, messageSource, repository, strategySpy, connection);
        doReturn(connection).when(strategySpy).establishConnection(any(String.class), any(String.class), any(String.class));
        testConnectionDescription = new ClientJdbcDataSource(INITIAL_CONNECTION_DESCRIPTION);
    }

    @Test
    public void createConnection_withPassword_passed() throws Exception {
        final ClientJdbcDataSource result = strategySpy.createConnection(testConnectionDescription, null);
        verify(jdbcDriverService).register(INITIAL_CONNECTION_DESCRIPTION.getDriverClass());
        verify(strategySpy).establishConnection(INITIAL_CONNECTION_DESCRIPTION.getConnectionUrl(),
                INITIAL_CONNECTION_DESCRIPTION.getUsername(), INITIAL_CONNECTION_DESCRIPTION.getPassword());
        assertSame(result, testConnectionDescription);
        verify(connection, new Times(1)).close();
    }

    @Test
    public void createConnection_passwordSubstitution_passed() throws Exception{
        final String passwordSubstitution = "passwordSubstitution";
        doReturn(passwordSubstitution).when(messageSource).getMessage(eq("input.password.substitution"), isNull(Object[].class), any(Locale.class));
        JdbcReportDataSource dataSource = new JdbcReportDataSourceImpl();
        final String expectedPassword = "expectedPassword";
        dataSource.setPassword(expectedPassword);
        doReturn(dataSource).when(repository).getResource(null, INITIAL_CONNECTION_DESCRIPTION.getUri());
        final ClientJdbcDataSource result = strategySpy.createConnection(testConnectionDescription.setPassword(passwordSubstitution), null);
        assertSame(result, testConnectionDescription);
        assertEquals(result.getPassword(), passwordSubstitution);
        verify(strategySpy).establishConnection(INITIAL_CONNECTION_DESCRIPTION.getConnectionUrl(),
                INITIAL_CONNECTION_DESCRIPTION.getUsername(), expectedPassword);
    }

    @Test
    public void createConnection_passwordNull_passed() throws Exception{
        JdbcReportDataSource dataSource = new JdbcReportDataSourceImpl();
        final String expectedPassword = "expectedPassword";
        dataSource.setPassword(expectedPassword);
        doReturn(dataSource).when(repository).getResource(null, INITIAL_CONNECTION_DESCRIPTION.getUri());
        final ClientJdbcDataSource result = strategySpy.createConnection(testConnectionDescription.setPassword(null), null);
        assertSame(result, testConnectionDescription);
        assertNull(result.getPassword());
        verify(strategySpy).establishConnection(INITIAL_CONNECTION_DESCRIPTION.getConnectionUrl(),
                INITIAL_CONNECTION_DESCRIPTION.getUsername(), expectedPassword);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_connectionIsNull_exception()throws Exception{
        reset(strategySpy);
        doReturn(null).when(strategySpy).establishConnection(any(String.class), any(String.class), any(String.class));
        strategySpy.createConnection(testConnectionDescription, null);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_connectionEstablishingThrowsException_exception()throws Exception{
        reset(strategySpy);
        doThrow(new RuntimeException()).when(strategySpy).establishConnection(any(String.class), any(String.class), any(String.class));
        strategySpy.createConnection(testConnectionDescription, null);
    }

}
