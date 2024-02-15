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

package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientDataType;
import com.jaspersoft.jasperserver.dto.resources.ClientInputControl;
import com.jaspersoft.jasperserver.dto.resources.ClientListOfValues;
import com.jaspersoft.jasperserver.dto.resources.ClientQuery;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.war.util.CalendarFormatProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.Authentication;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: InputControlResourceConverterTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class InputControlResourceConverterTest {
    @InjectMocks
    private InputControlResourceConverter converter = new InputControlResourceConverter();
    @Mock private ResourceFactory resourceFactory;
    @Mock private CalendarFormatProvider calendarFormatProvider;
    @Mock private ResourceReferenceConverter resourceReferenceConverter;
    @Mock private ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    @Mock private PermissionsService permissionsService;
    @Mock
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;


    private final ClientInputControl client = new ClientInputControl();
    private final InputControlImpl server = new InputControlImpl();

    private final List<String> visibleColumnsList = new ArrayList<String>();
    private final ObjectPermission repositoryPermission = new ObjectPermissionImpl();

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        visibleColumnsList.add("test");
        repositoryPermission.setPermissionMask(1);

        when(resourceFactory.newResource(any(ExecutionContext.class), anyString())).thenReturn(new InputControlImpl());
        when(calendarFormatProvider.getDatetimeFormat()).thenReturn(new SimpleDateFormat());
        when(permissionsService.getEffectivePermission(any(Resource.class), any(Authentication.class))).thenReturn(repositoryPermission);
        when(resourceReferenceConverterProvider.getConverterForType(any(Class.class))).thenReturn(resourceReferenceConverter);
    }

    @BeforeMethod
    public void fillSampleObjects(){
        client.setValueColumn("clientValueColumn");
        client.setMandatory(true);
        client.setReadOnly(true);
        client.setVisible(true);
        client.setType(InputControl.TYPE_BOOLEAN);
        client.setVisibleColumns(visibleColumnsList);
        client.setVersion(1);
        client.setLabel("label");

        client.setDataType(new ClientDataType());
        client.setListOfValues(new ClientListOfValues());
        client.setQuery(new ClientQuery());

        server.setMandatory(true);
        server.setReadOnly(true);
        server.setVisible(true);
        server.setType(InputControl.TYPE_SINGLE_VALUE);
        server.setQueryValueColumn("serverValueColumn");
        server.addQueryVisibleColumn("serverTest");
        server.setName("server");

        server.setQuery(new QueryImpl());
        server.setDataType(new DataTypeImpl());
        server.setListOfValues(new ListOfValuesImpl());
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientInputControl.class));
        assertEquals(converter.getServerResourceType(), InputControl.class.getName());
    }

    @Test
    public void testToServer() throws Exception {
        when(resourceReferenceConverter.toServer(client.getDataType(), null, null)).thenReturn(server.getDataType());
        when(resourceReferenceConverter.toServer(client.getListOfValues(), null, null)).thenReturn(server.getListOfValues());
        when(resourceReferenceConverter.toServer(client.getQuery(), null, null)).thenReturn(server.getQuery());

        InputControl converted = converter.toServer(client, null);

        assertEquals(converted.getType(), client.getType());
        assertEquals(converted.isMandatory(), client.isMandatory());
        assertEquals(converted.isReadOnly(), client.isReadOnly());
        assertEquals(converted.isVisible(), client.isVisible());
        assertEquals(converted.getQueryValueColumn(), client.getValueColumn());

        for (int i = 0; i<converted.getQueryVisibleColumns().length; i++){
            assertEquals(converted.getQueryVisibleColumns()[i], client.getVisibleColumns().get(i));
        }

        assertEquals(converted.getDataType().getLocalResource().getClass(), server.getDataType().getLocalResource().getClass());
        assertEquals(converted.getListOfValues().getLocalResource().getClass(), server.getListOfValues().getLocalResource().getClass());
        assertEquals(converted.getQuery().getLocalResource().getClass(), server.getQuery().getLocalResource().getClass());
    }

    @Test
    public void testToClient() throws Exception {
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        when(resourceReferenceConverter.toClient(server.getDataType(), options)).thenReturn(client.getDataType());
        when(resourceReferenceConverter.toClient(server.getListOfValues(), options)).thenReturn(client.getListOfValues());
        when(resourceReferenceConverter.toClient(server.getQuery(), options)).thenReturn(client.getQuery());

        ClientInputControl converted = converter.toClient(server, options);

        assertEquals(converted.getType(), server.getType());
        assertEquals(converted.isMandatory(), server.isMandatory());
        assertEquals(converted.isReadOnly(), server.isReadOnly());
        assertEquals(converted.isVisible(), server.isVisible());
        assertEquals(converted.getValueColumn(), server.getQueryValueColumn());

        for (int i = 0; i<server.getQueryVisibleColumns().length; i++){
            assertEquals(server.getQueryVisibleColumns()[i], converted.getVisibleColumns().get(i));
        }

        assertEquals(converted.getDataType(), client.getDataType());
        assertEquals(converted.getListOfValues(), client.getListOfValues());
        assertEquals(converted.getQuery(), client.getQuery());
    }
}
