/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.war;

import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.mondrian.MondrianDrillThroughTableModel;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.OlapModelDecorator;
import com.tonbeller.wcf.table.EditableTableComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
class CSVServletTest {
    private static final String OLAP_MODEL_ARGUMENT = "olapModel";
    private static final String CURRENT_VIEW_ARGUMENT = "currentView";
    private static final String CURRENT_VIEW = "currentView";
    private static final String DRILLTHROUGHTABLE_ARGUMENT = ".drillthroughtable";
    private static final String DRILL_THROUGH_SQL = "drillThroughSql";
    private static final String DS_NAME = "dsName";
    private static final String COLUMN_NAME_2 = "columnName2";
    private static final String COLUMN_NAME_1 = "columnName1";
    private static final String RESULT_OBJECT_1 = "resultObject1";
    private static final String RESULT_OBJECT_2 = "resultObject2";
    private static final String PRAGMA_HEADER = "Pragma";
    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String CACHE_CONTROL_VALUE = "no-store";

    @InjectMocks
    CSVServlet objectUnderTest;
    @Mock
    Context jndiContext;

    private HttpServletRequest servletRequest = mock(HttpServletRequest.class);
    private HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    private ResultSet resultSet = mock(ResultSet.class);
    private PrintWriter printWriter = mock(PrintWriter.class);
    private Connection connection = mock(Connection.class);
    private OlapModel olapModel = mock(OlapModelDecorator.class);
    private HttpSession httpSession = mock(HttpSession.class);
    private MondrianDrillThroughTableModel drillThroughModel = mock(MondrianDrillThroughTableModel.class);
    private Model model = mock(MondrianModel.class);
    private EditableTableComponent editableTableComponent = mock(EditableTableComponent.class);
    private DataSource dataSource = mock(DataSource.class);
    private Statement statement = mock(Statement.class);
    private ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);

    @BeforeEach
    void setup() throws IOException, NamingException, SQLException {
        MockitoAnnotations.initMocks(this);

        doReturn(printWriter).when(servletResponse).getWriter();
        doReturn(httpSession).when(servletRequest).getSession();
        doReturn(olapModel).when(httpSession).getAttribute(OLAP_MODEL_ARGUMENT);
        doReturn(CURRENT_VIEW).when(httpSession).getAttribute(CURRENT_VIEW_ARGUMENT);
        doReturn(editableTableComponent).when(httpSession).getAttribute(CURRENT_VIEW_ARGUMENT + DRILLTHROUGHTABLE_ARGUMENT);
        doReturn(drillThroughModel).when(editableTableComponent).getModel();
        doReturn(model).when(olapModel).getRootModel();
        doReturn(DRILL_THROUGH_SQL).when(drillThroughModel).getSql();
        doReturn(DS_NAME).when(drillThroughModel).getDataSourceName();
        doReturn(dataSource).when(jndiContext).lookup(DS_NAME);
        doReturn(connection).when(dataSource).getConnection();
        doReturn(statement).when(connection).createStatement();
        doReturn(resultSet).when(statement).executeQuery(DRILL_THROUGH_SQL);
        doReturn(resultSetMetaData).when(resultSet).getMetaData();
        doReturn(2).when(resultSetMetaData).getColumnCount();
        doReturn(COLUMN_NAME_1).when(resultSetMetaData).getColumnName(1);
        doReturn(COLUMN_NAME_2).when(resultSetMetaData).getColumnName(2);
        doReturn(RESULT_OBJECT_1).when(resultSet).getObject(1);
        doReturn(RESULT_OBJECT_2).when(resultSet).getObject(2);
    }

    @Test
    void service_columnHeaderPrinted() throws ServletException {
        objectUnderTest.service(servletRequest, servletResponse);

        verify(printWriter).write("\"" + COLUMN_NAME_1 + "\"");
        verify(printWriter).write(CSVServlet.SEP);

        verify(printWriter).write("\"" + COLUMN_NAME_2 + "\"");
        verify(printWriter).write(CSVServlet.NEWLINE);
    }

    @Test
    void service_rowDataPrinted() throws ServletException, SQLException {
        when(resultSet.next()).thenReturn(true, false);

        objectUnderTest.service(servletRequest, servletResponse);

        verify(printWriter).write("\"" + RESULT_OBJECT_1 + "\"");
        verify(printWriter, times(2)).write(CSVServlet.SEP);

        verify(printWriter).write("\"" + RESULT_OBJECT_2 + "\"");
        verify(printWriter, times(2)).write(CSVServlet.NEWLINE);
    }

    @Test
    void service_responseHeadersIsSetup() throws ServletException {
        objectUnderTest.service(servletRequest, servletResponse);

        verify(servletResponse).setContentType(CSVServlet.MIME_TYPE);
        verify(servletResponse).setHeader(PRAGMA_HEADER, "");
        verify(servletResponse).setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE);
    }

    @Test
    void service_resultSetIsClosed() throws ServletException, SQLException {
        objectUnderTest.service(servletRequest, servletResponse);

        verify(resultSet).close();
    }

    @Test
    void service_connectionIsClosed() throws ServletException, SQLException {
        objectUnderTest.service(servletRequest, servletResponse);

        verify(connection).close();
    }

    @Disabled("Log is obtained from LogFactory.getLog and can not be mocked")
    @Test
    void service_exceptionIsThrown_exceptionIsLogged() throws ServletException {
        Exception exception = new IllegalStateException();
        doThrow(exception).when(servletResponse).setContentType(any(String.class));

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that log.error(exception) was called if exception is thrown in service method
    }

    @Disabled("Null pointer is thrown if getDrillThroughModel() returns null")
    @DisplayName("Service() method will not service request with root model that does not support 'Drill Through'")
    @Test
    void service_rootModelDoesNotSupportDrillThrough_isNotServiced() throws ServletException {
        Model modelThatDoestNotSupportDrillThrough = mock(Model.class);
        doReturn(modelThatDoestNotSupportDrillThrough).when(olapModel).getRootModel();

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that service will fail if root model does not supports DrillThrough
    }

    @Disabled("Null pointer is thrown if getDrillThroughModel() returns null")
    @Test
    void service_currentViewIsNull_isNotServiced() throws ServletException {
        doReturn(null).when(httpSession).getAttribute(CURRENT_VIEW_ARGUMENT);

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that service will fail if current view is null
    }

    @Disabled("Null pointer is thrown if getDrillThroughModel() returns null")
    @Test
    void service_drillThroughTableIsNull_isNotServiced() throws ServletException {
        doReturn(null).when(httpSession).getAttribute(CURRENT_VIEW_ARGUMENT + DRILLTHROUGHTABLE_ARGUMENT);

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that service will fail if drill through table is absent
    }

    @Disabled("Log is obtained from LogFactory.getLog and can not be mocked")
    @Test
    void service_exceptionIsThrownWhileGettingDrillThroughTable_exceptionIsLogged() throws ServletException {
        Exception exception = new IllegalStateException();
        doThrow(exception).when(httpSession).getAttribute(CURRENT_VIEW_ARGUMENT + DRILLTHROUGHTABLE_ARGUMENT);

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that log.error(exception.getStackTrace()) was called if getting drill through table failed
    }

    @Disabled("Call of static DriverManager.getConnection() can not be mocked")
    @Test
    void service_drillThroughModelHasNoDataSourceName_connectionIsObtainedFromDriverManager() throws ServletException {
        doReturn(null).when(drillThroughModel).getDataSourceName();

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that connection will be obtained from driver manager if drill through model has no data source name
    }

    @Disabled("Log is obtained from LogFactory.getLog and can not be mocked")
    @Test
    void service_dataSourceCanNotBeLookupFromJndiContext_exceptionIsLogged() throws ServletException, NamingException {
        Exception exception = new IllegalStateException();
        doThrow(exception).when(jndiContext).lookup(DS_NAME);

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that log.error(exception) was called if lookup for data source name fail
    }

    @Disabled("Context is initiating directly by calling the constructor and can not be mocked")
    @Test
    void service_jndiContextIsNotProvided_jndiContextIsInitedWithInitialContext() throws ServletException {
        CSVServlet csvServletAlternative = new CSVServlet();

        csvServletAlternative.service(servletRequest, servletResponse);

        // verify that jndi context will be inited with initial context if context is not provided
    }

    @Disabled("Log is obtained from LogFactory.getLog and can not be mocked")
    @Test
    void service_statementCanNotBeCreated_exceptionIsLogged() throws ServletException, SQLException {
        Exception exception = new IllegalStateException();
        doThrow(exception).when(connection).createStatement();

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that log.error(exception) was called if statement can not be created for current connection
    }

    @Disabled("Log is obtained from LogFactory.getLog and can not be mocked")
    @Test
    void service_connectionCanNotBeClosed_exceptionIsLogged() throws ServletException, SQLException {
        SQLException exception = new SQLException();
        doThrow(exception).when(connection).close();

        objectUnderTest.service(servletRequest, servletResponse);

        // verify that log.error(exception) was called if connection can not be closed after servicing
    }
}
