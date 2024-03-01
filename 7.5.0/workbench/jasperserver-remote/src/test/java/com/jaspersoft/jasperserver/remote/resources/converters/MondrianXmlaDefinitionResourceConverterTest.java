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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.MondrianXMLADefinitionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.core.util.type.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientMondrianConnection;
import com.jaspersoft.jasperserver.dto.resources.ClientMondrianXmlaDefinition;
import com.jaspersoft.jasperserver.remote.resources.validation.BasicResourceValidator;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.Validator;
import java.text.SimpleDateFormat;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MondrianXmlaDefinitionResourceConverterTest {
    @InjectMocks
    private MondrianXmlaDefinitionResourceConverter converter = new MondrianXmlaDefinitionResourceConverter();

    @Mock private ResourceFactory resourceFactory;
    @Mock private CalendarFormatProvider calendarFormatProvider;
    @Mock private ResourceReferenceConverter resourceReferenceConverter;
    @Mock private ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    @Mock private PermissionsService permissionsService;
    @Mock private GenericTypeProcessorRegistry genericTypeProcessorRegistry;
    @Mock private BasicResourceValidator resourceValidator;
    @Mock private Validator validator;

    private final ClientMondrianXmlaDefinition client = new ClientMondrianXmlaDefinition();
    private final MondrianXMLADefinition server = new MondrianXMLADefinitionImpl();

    private final ObjectPermission repositoryPermission = new ObjectPermissionImpl();
    private final ResourceReference serverConnection = new ResourceReference("/some/uri");
    private final ClientMondrianConnection clientConnection = new ClientMondrianConnection();

    @BeforeClass
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        repositoryPermission.setPermissionMask(1);

        when(resourceFactory.newResource(nullable(ExecutionContext.class), anyString())).thenReturn(new MondrianXMLADefinitionImpl());
        when(calendarFormatProvider.getDatetimeFormat()).thenReturn(new SimpleDateFormat());
        when(permissionsService.getEffectivePermission(nullable(Resource.class), nullable(Authentication.class))).thenReturn(repositoryPermission);
        when(resourceReferenceConverterProvider.getConverterForType(nullable(Class.class))).thenReturn(resourceReferenceConverter);
        when(resourceReferenceConverter.toServer(nullable(ClientMondrianConnection.class), nullable(ResourceReference.class), nullable(ToServerConversionOptions.class))).thenReturn(serverConnection);
        when(resourceReferenceConverter.toClient(nullable(ResourceReference.class), nullable(ToClientConversionOptions.class))).thenReturn(clientConnection);
    }

    @BeforeMethod
    public void cleanUp(){
        client.setCatalog("clientCatalog").setMondrianConnection(clientConnection).setLabel("client");
        server.setCatalog("serverCatalog");
        server.setMondrianConnection(serverConnection);
        server.setURIString("/some/test/uri");
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeUtility.extractClientType(ClientMondrianXmlaDefinition.class));
        assertEquals(converter.getServerResourceType(), MondrianXMLADefinition.class.getName());
    }

    @Test
    public void testToServer() throws Exception{
        MondrianXMLADefinition converted = converter.toServer(client, null);

        assertEquals(converted.getMondrianConnection(), serverConnection);
        assertEquals(converted.getCatalog(), client.getCatalog());
    }

    @Test
    public void testToClient() throws Exception{
        ClientMondrianXmlaDefinition converted = converter.toClient(server, ToClientConversionOptions.getDefault());

        assertEquals(converted.getMondrianConnection(), clientConnection);
        assertEquals(converted.getCatalog(), server.getCatalog());
    }
}
