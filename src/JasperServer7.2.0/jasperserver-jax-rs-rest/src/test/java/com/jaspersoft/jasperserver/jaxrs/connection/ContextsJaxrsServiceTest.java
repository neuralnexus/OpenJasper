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
package com.jaspersoft.jasperserver.jaxrs.connection;

import com.jaspersoft.jasperserver.jaxrs.resources.ContentNegotiationHandler;
import com.jaspersoft.jasperserver.remote.connection.ContextsManager;
import com.jaspersoft.jasperserver.remote.exception.NotAcceptableException;
import com.jaspersoft.jasperserver.remote.exception.UnsupportedMediaTypeException;
import com.jaspersoft.jasperserver.remote.exception.UnsupportedOperationErrorDescriptorException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ContextsJaxrsServiceTest {
    @InjectMocks
    private ContextsJaxrsService service = new ContextsJaxrsService();
    @Mock
    private Providers providers;
    @Mock
    private HttpHeaders httpHeaders;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ContextsManager contextsManager;
    @Mock
    private InputStream inputStream;
    @Mock
    private ContentNegotiationHandler contentNegotiationHandler;
    @Mock
    UriInfo uriInfo;
    private ContextsJaxrsService spyService;
    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        spyService = spy(service);
    }

    @BeforeMethod
    public void resetMocks(){
        reset(providers, httpHeaders, request, contextsManager, inputStream, spyService);
    }

    @Test
    public void getConnectionClass(){
        final String connectionType = "lfc";
        final String type = "application/connections." + connectionType + "+json";
        Class expectedClass = Number.class;
        when(contextsManager.getContextDescriptionClass(connectionType)).thenReturn(expectedClass);
        final Class<?> result = service.getContextClass(MediaType.valueOf(type));
        assertSame(result, expectedClass);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void getConnectionClass_notExistentType_exception(){
        service.getContextClass(MediaType.valueOf("application/connections.notExistentType+xml"));
    }

    @Test(expectedExceptions = UnsupportedMediaTypeException.class)
    public void getConnectionMetadata_notSupportedType_exception() throws IOException {
        doReturn(null).when(contextsManager).getMetadataParamsClass("application/connections.notExistentType+xml");
        service.getContextMetadata(UUID.randomUUID(), inputStream, MediaType.valueOf("application/connections.notExistentType+xml"));
    }

    @Test(expectedExceptions = UnsupportedMediaTypeException.class)
    public void getConnectionMetadata_ContextNotSupportsMetadata_exception() throws IOException {
        final Class<Number> connectionClass = Number.class;
        final Object expectedConnectionObject = new Object();
        final MediaType mediaType = MediaType.valueOf("application/connections.notExistentType+xml");
        doReturn(connectionClass).when(contextsManager).getMetadataParamsClass("notExistentType");
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, inputStream, mediaType);
        doThrow(UnsupportedOperationErrorDescriptorException.class).when(contextsManager).getContextMetadata(any(UUID.class), eq(expectedConnectionObject));

        spyService.getContextMetadata(UUID.randomUUID(), inputStream, mediaType);
    }


    @Test
    public void getConnectionMetadata_metadataReturned() throws IOException {
        final Class<Number> connectionClass = Number.class;
        final Object expectedConnectionObject = new Object();
        final MediaType mediaType = MediaType.valueOf("application/connections.notExistentType+xml");
        doReturn(connectionClass).when(contextsManager).getMetadataParamsClass("notExistentType");
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, inputStream, mediaType);
        doReturn(new Object()).when(contextsManager).getContextMetadata(any(UUID.class), eq(expectedConnectionObject));

        final Response response = spyService.getContextMetadata(UUID.randomUUID(), inputStream, mediaType);
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void getConnectionClass_nullMediaType_exception(){
        service.getContextClass(null);
    }

    @Test
    public void createConnection_establish()throws Exception{
        final MediaType connectionType = MediaType.valueOf("application/connections.type+json");
        final MediaType acceptType = MediaType.valueOf("application/json");
        final Class<Number> connectionClass = Number.class;
        doReturn(connectionClass).when(spyService).getContextClass(connectionType);
        final Object expectedConnectionObject = new Object();
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, inputStream, connectionType);
        final UUID expectedUuid = UUID.randomUUID();
        when(contextsManager.createContext(expectedConnectionObject)).thenReturn(expectedUuid);
        final Object contextFromContextManager = new Object();
        final HashMap<String, String[]> parametersMap = new HashMap<String, String[]>();
        when(contextsManager.getContext(expectedUuid, parametersMap)).thenReturn(contextFromContextManager);
        when(contentNegotiationHandler.isAcceptable(
                same(expectedConnectionObject), eq(connectionType.toString()), eq(acceptType.toString()))).thenReturn(true);
        doReturn(parametersMap).when(request).getParameterMap();
        final Object expectedEntity = new Object();
        when(contentNegotiationHandler.handle(
                same(contextFromContextManager), eq(connectionType.toString()), eq(acceptType.toString()),same(parametersMap) )).thenReturn(expectedEntity);
        final String someRequestUrl = "/jasperserver-pro/contexts/";
        when(request.getRequestURL()).thenReturn(new StringBuffer(someRequestUrl));
        when(uriInfo.getBaseUri()).thenReturn(new URI(someRequestUrl));
        final Response response = spyService.createContext(inputStream, connectionType, acceptType, request, uriInfo);
        assertEquals(response.getMetadata().getFirst(HttpHeaders.LOCATION).toString(), someRequestUrl + expectedUuid);
        assertSame(response.getEntity(), expectedEntity);
    }

    @Test(expectedExceptions = NotAcceptableException.class)
    public void createConnection_notAcceptable() throws IOException, URISyntaxException {
        final MediaType connectionType = MediaType.valueOf("application/connections.type+json");
        final MediaType acceptType = MediaType.valueOf("application/json");
        final Class<Number> connectionClass = Number.class;
        doReturn(connectionClass).when(spyService).getContextClass(connectionType);
        final Object expectedConnectionObject = new Object();
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, inputStream, connectionType);
        when(contentNegotiationHandler.isAcceptable(
                same(expectedConnectionObject),
                eq(connectionType.toString()),
                eq(acceptType.toString()))).thenReturn(false);
        spyService.createContext(inputStream, connectionType, acceptType, request, uriInfo);
    }

    @Test
    public void createConnection_metadata()throws Exception{
        final MediaType connectionType = MediaType.valueOf("application/connections.type+json");
        final MediaType metadataType = MediaType.valueOf("application/connections.type.metadata+json");
        final Class<Number> connectionClass = Number.class;
        doReturn(connectionClass).when(spyService).getContextClass(connectionType);
        doReturn(connectionClass).when(spyService).getContextClass(metadataType);
        final InputStream streamMock = mock(InputStream.class);
        final Object expectedConnectionObject = new Object();
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, streamMock, connectionType);
        final UUID expectedUuid = UUID.randomUUID();
        when(contextsManager.isMetadataSupported(expectedConnectionObject, "connections.type.metadata")).thenReturn(true);
        when(contextsManager.createContext(expectedConnectionObject)).thenReturn(expectedUuid);
        when(contextsManager.getContextMetadata(expectedUuid, new HashMap<String, String[]>())).thenReturn(expectedConnectionObject);
        final String someRequestUrl = "someRequestUrl";
        when(request.getRequestURL()).thenReturn(new StringBuffer(someRequestUrl));
        when(uriInfo.getBaseUri()).thenReturn(new URI(someRequestUrl));
        final Response response = spyService.createContext(streamMock, connectionType, metadataType, request, uriInfo);
        assertEquals(response.getMetadata().getFirst(HttpHeaders.LOCATION).toString(), someRequestUrl + "/" + expectedUuid + "/metadata");
        assertSame(response.getEntity(), expectedConnectionObject);
    }

    @Test(expectedExceptions = NotAcceptableException.class)
    public void createConnection_metadata_notSupported()throws Exception{
        final MediaType connectionType = MediaType.valueOf("application/connections.type+json");
        final MediaType metadataType = MediaType.valueOf("application/connections.type.metadata+json");
        final Class<Number> connectionClass = Number.class;
        doReturn(connectionClass).when(spyService).getContextClass(connectionType);
        doReturn(connectionClass).when(spyService).getContextClass(metadataType);
        final InputStream streamMock = mock(InputStream.class);
        final Object expectedConnectionObject = new Object();
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, streamMock, connectionType);
        when(contextsManager.isMetadataSupported(expectedConnectionObject, "connections.type.metadata")).thenReturn(false);
        spyService.createContext(streamMock, connectionType, metadataType, request, uriInfo);
    }

}
