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
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsDataSourceRecovery;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.AwsReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientAwsDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.resources.converters.AwsDataSourceResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: AwsConnectionStrategyTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class AwsConnectionStrategyTest {
    private static ClientAwsDataSource AWS_DATA_SOURCE_TEMPLATE = new ClientAwsDataSource()
            .setConnectionUrl("someConnectionUrl").setUsername("someUsername").setPassword("somePassword")
            .setDriverClass("someDriverClass").setUri("someUri").setSecretKey("someSecretKey");
    @InjectMocks
    private AwsConnectionStrategy strategy = new AwsConnectionStrategy();
    @Mock
    private JdbcDriverService jdbcDriverService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private RepositoryService repository;
    @Mock
    private AwsDataSourceRecovery awsDataSourceRecovery;
    @Mock
    private AwsDataSourceResourceConverter awsDataSourceResourceConverter;
    @Mock
    private Connection connection;
    private AwsConnectionStrategy strategySpy;
    private ClientAwsDataSource awsDataSource;
    private AwsReportDataSourceImpl awsReportDataSource;


    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        strategySpy = spy(strategy);
    }

    @BeforeMethod
    public void refresh() throws Exception {
        reset(strategySpy, messageSource, jdbcDriverService, repository, awsDataSourceRecovery,
                awsDataSourceResourceConverter, connection);
        awsDataSource = new ClientAwsDataSource(AWS_DATA_SOURCE_TEMPLATE);
        doReturn(connection).when(strategySpy).establishConnection(AWS_DATA_SOURCE_TEMPLATE.getConnectionUrl(),
                AWS_DATA_SOURCE_TEMPLATE.getUsername(), AWS_DATA_SOURCE_TEMPLATE.getPassword());
        awsReportDataSource = new AwsReportDataSourceImpl();
        doReturn(awsReportDataSource).when(awsDataSourceResourceConverter)
                .toServer(same(awsDataSource), any(ToServerConversionOptions.class));
    }

    @Test
    public void createConnection_withPasswordAndSecretKey_success() throws Exception {
        final ClientAwsDataSource result = strategySpy.createConnection(awsDataSource, null);
        assertSame(result, awsDataSource);
        verify(jdbcDriverService).register(awsDataSource.getDriverClass());
        verify(awsDataSourceRecovery).createAwsDSSecurityGroup(awsReportDataSource);
        verify(connection).close();
    }

    @Test
    public void createConnection_withSubstitutionOfPasswordAndSecretKey_success() throws Exception {
        final String passwordSubstitution = "passwordSubstitution";
        doReturn(passwordSubstitution).when(messageSource).getMessage(eq("input.password.substitution"),
                isNull(Object[].class), any(Locale.class));
        awsDataSource.setPassword(passwordSubstitution).setSecretKey(passwordSubstitution);
        awsReportDataSource.setPassword("realPassword");
        awsReportDataSource.setAWSSecretKey("realSecretKey");
        reset(strategySpy);
        doReturn(connection).when(strategySpy).establishConnection(awsDataSource.getConnectionUrl(),
                awsDataSource.getUsername(), awsReportDataSource.getPassword());
        doReturn(awsReportDataSource).when(repository).getResource(null, awsDataSource.getUri());
        final ClientAwsDataSource result = strategySpy.createConnection(awsDataSource, null);
        assertSame(result, awsDataSource);
        assertEquals(result.getPassword(), awsReportDataSource.getPassword());
        assertEquals(result.getSecretKey(), awsReportDataSource.getAWSSecretKey());
        verify(jdbcDriverService).register(awsDataSource.getDriverClass());
        verify(awsDataSourceRecovery).createAwsDSSecurityGroup(awsReportDataSource);
        verify(connection).close();
    }

    @Test
    public void createConnection_withNullPasswordAndSecretKey_success() throws Exception {
        awsDataSource.setPassword(null).setSecretKey(null);
        awsReportDataSource.setPassword("realPassword");
        awsReportDataSource.setAWSSecretKey("realSecretKey");
        reset(strategySpy);
        doReturn(connection).when(strategySpy).establishConnection(awsDataSource.getConnectionUrl(),
                awsDataSource.getUsername(), awsReportDataSource.getPassword());
        doReturn(awsReportDataSource).when(repository).getResource(null, awsDataSource.getUri());
        final ClientAwsDataSource result = strategySpy.createConnection(awsDataSource, null);
        assertSame(result, awsDataSource);
        assertEquals(result.getPassword(), awsReportDataSource.getPassword());
        assertEquals(result.getSecretKey(), awsReportDataSource.getAWSSecretKey());
        verify(jdbcDriverService).register(awsDataSource.getDriverClass());
        verify(awsDataSourceRecovery).createAwsDSSecurityGroup(awsReportDataSource);
        verify(connection).close();
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void createConnection_establishConnectionReturnsNull_exception() throws Exception{
        reset(strategySpy);
        strategySpy.createConnection(awsDataSource, null);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_establishConnectionThrowsException_exception() throws Exception{
        reset(strategySpy);
        doThrow(new RuntimeException()).when(strategySpy).establishConnection(any(String.class), any(String.class),
                any(String.class));
        strategySpy.createConnection(awsDataSource, null);
    }


}
