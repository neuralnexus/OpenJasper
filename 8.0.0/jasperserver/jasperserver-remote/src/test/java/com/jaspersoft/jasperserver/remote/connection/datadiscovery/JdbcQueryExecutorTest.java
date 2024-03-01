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
package com.jaspersoft.jasperserver.remote.connection.datadiscovery;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.common.domain.JdbcMetaConfiguration;
import com.jaspersoft.jasperserver.dto.connection.datadiscovery.FlatDataSet;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceSingleElement;
import com.jaspersoft.jasperserver.remote.exception.OperationCancelledException;
import org.mockito.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JdbcQueryExecutorTest {
    @Mock
    private JdbcMetaConfiguration jdbcMetaConfiguration;
    @Mock
    private SQLQueryValidator sqlQueryValidator;
    @InjectMocks
    private JdbcQueryExecutor queryExecutor = new JdbcQueryExecutor();
    @Mock
    private ExecutorService executor;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
    @AfterMethod
    public void reset(){
        Mockito.reset(sqlQueryValidator, executor);
    }
    @Test
    public void executeQuery_callableIsExpected() throws Exception {
        final String query = "someQuery";
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();
        final ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        final Future future = mock(Future.class);
        doReturn(future).when(executor).submit(callableArgumentCaptor.capture());
        final ResultSet resultSet = mock(ResultSet.class);
        doReturn(resultSet).when(future).get();
        final ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);
        doReturn(resultSetMetaData).when(resultSet).getMetaData();
        doReturn(2).when(resultSetMetaData).getColumnCount();
        doReturn("String").when(resultSetMetaData).getColumnTypeName(1);
        doReturn("column1Name").when(resultSetMetaData).getColumnLabel(1);
        doReturn("Integer").when(resultSetMetaData).getColumnTypeName(2);
        doReturn("column2Name").when(resultSetMetaData).getColumnLabel(2);
        when(jdbcMetaConfiguration.getJavaType(resultSetMetaData.getColumnTypeName(1),resultSetMetaData.getColumnType(1))).thenReturn("java.lang.String");
        when(jdbcMetaConfiguration.getJavaType(resultSetMetaData.getColumnTypeName(2),resultSetMetaData.getColumnType(2))).thenReturn("java.lang.Integer");

        final FlatDataSet flatDataSet = queryExecutor.executeQuery(query, connection, true);

        assertEquals(flatDataSet, new FlatDataSet().setData(new ArrayList<String[]>())
                .setMetadata(new ResourceGroupElement().setKind("resultSet").setElements((List)Arrays.asList(
                        new ResourceSingleElement().setName("column1Name").setType("string"),
                        new ResourceSingleElement().setName("column2Name").setType("integer")
                ))));
        verify(sqlQueryValidator).validate(query, connection);
        // verify set max row is used
        verify(statement).setMaxRows(1);
        verify(statement).close();
        verify(statement, never()).executeQuery(query);

        final Callable callable = callableArgumentCaptor.getValue();
        assertNotNull(callable);
        doReturn(resultSet).when(statement).executeQuery(query);
        assertSame(callable.call(), resultSet);
    }

    @Test
    public void executeQuery_sqlInvalid_sqlExceptionExpected() throws Exception {

        final String query = "someQuery";
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();
        final ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        final Future future = mock(Future.class);
        doReturn(future).when(executor).submit(callableArgumentCaptor.capture());
        final SQLException expected = new SQLException();
        ExecutionException executionException = new ExecutionException(expected);
        doThrow(executionException).when(future).get();
        Exception exception = null;
        try {
            queryExecutor.executeQuery(query, connection, true);
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof JSExceptionWrapper);
        assertSame(exception.getCause(), expected);
    }

    @Test
    public void executeQuery_CancellationExceptionExpected() throws Exception {

        final String query = "someQuery";
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();
        final ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        final Future future = mock(Future.class);
        doReturn(future).when(executor).submit(callableArgumentCaptor.capture());
        final CancellationException expected = new CancellationException();
        doThrow(expected).when(future).get();
        Exception exception = null;
        try {
            queryExecutor.executeQuery(query, connection, true);
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof OperationCancelledException);
        assertSame(exception.getCause(),  expected);
    }

    @Test
    public void executeQuery_RuntimeExceptionExpected() throws Exception {

        final String query = "someQuery";
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();
        final ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        final Future future = mock(Future.class);
        doReturn(future).when(executor).submit(callableArgumentCaptor.capture());
        final RuntimeException expected = new RuntimeException();
        ExecutionException executionException = new ExecutionException(expected);
        doThrow(executionException).when(future).get();
        Exception exception = null;
        try {
            queryExecutor.executeQuery(query, connection, true);
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertSame(exception, expected);
    }

    @Test
    public void executeQuery_notRuntimeExceptionExpected() throws Exception {

        final String query = "someQuery";
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        doReturn(statement).when(connection).createStatement();
        final ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        final Future future = mock(Future.class);
        doReturn(future).when(executor).submit(callableArgumentCaptor.capture());
        final Exception expected = new Exception();
        ExecutionException executionException = new ExecutionException(expected);
        doThrow(executionException).when(future).get();
        Exception exception = null;
        try {
            queryExecutor.executeQuery(query, connection, true);
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof JSExceptionWrapper);
        assertSame(exception.getCause(), expected);
    }

}
