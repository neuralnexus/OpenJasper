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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.CustomReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientCustomDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceable;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doCallRealMethod;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class CustomDataSourceResourceConverterTest {

    @InjectMocks
    private CustomDataSourceResourceConverter converter = mock(CustomDataSourceResourceConverter.class);

    private Set<String> propertiesToIgnore = new HashSet<String>();
    @Mock
    private CustomReportDataSourceServiceFactory customDataSourceFactory;
    @Mock
    private ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    ExecutionContext ctx = ExecutionContextImpl.getRuntimeExecutionContext();

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        converter.propertiesToIgnore = propertiesToIgnore;
        final ResourceReferenceConverter resourceReferenceConverterMock = mock(ResourceReferenceConverter.class);
        when(resourceReferenceConverterMock.toServer(any(ExecutionContext.class), nullable(ClientReferenceable.class), nullable(ToServerConversionOptions.class)))
                .thenAnswer(new Answer<ResourceReference>() {
                    @Override
                    public ResourceReference answer(InvocationOnMock invocationOnMock) throws Throwable {
                        ResourceReference result = null;
                        final ClientReferenceable clientReferenceable = (ClientReferenceable) invocationOnMock.getArguments()[1];
                        if(clientReferenceable != null){
                            result = new ResourceReference(clientReferenceable.getUri());
                        }
                        return result;
                    }
                });
        when(resourceReferenceConverterMock.toClient(nullable(ResourceReference.class), nullable(ToClientConversionOptions.class)))
                .thenAnswer(new Answer<ClientReferenceable>() {
                    @Override
                    public ClientReferenceable answer(InvocationOnMock invocationOnMock) throws Throwable {
                        ClientReferenceable result = null;
                        final ResourceReference resourceReference = (ResourceReference) invocationOnMock.getArguments()[0];
                        if(resourceReference != null){
                            result = new ClientReference(resourceReference.getReferenceURI());
                        }
                        return result;
                    }
                });
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class)).thenReturn(resourceReferenceConverterMock);
    }

    @BeforeMethod
    public void refresh(){
        propertiesToIgnore.clear();
        reset(customDataSourceFactory, converter);
        when(converter.resourceSpecificFieldsToClient(nullable(ClientCustomDataSource.class), nullable(CustomReportDataSource.class), nullable(ToClientConversionOptions.class))).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(any(ExecutionContext.class), nullable(ClientCustomDataSource.class), nullable(CustomReportDataSource.class), nullable(List.class), nullable(ToServerConversionOptions.class))).thenCallRealMethod();
        doCallRealMethod().when(converter).resourceSecureFieldsToClient(nullable(ClientCustomDataSource.class), nullable(CustomReportDataSource.class), nullable(ToClientConversionOptions.class));
        when(converter.convertResourcesToServer(any(ExecutionContext.class), nullable(Map.class), nullable(ToServerConversionOptions.class))).thenCallRealMethod();
        when(converter.convertResourcesToClient(nullable(Map.class), nullable(ToClientConversionOptions.class))).thenCallRealMethod();
    }

    @Test
    public void correctClientServerResourceType(){
        final CustomDataSourceResourceConverter resourceConverter = new CustomDataSourceResourceConverter();
        assertEquals(resourceConverter.getClientResourceType(), ClientTypeUtility.extractClientType(ClientCustomDataSource.class));
        assertEquals(resourceConverter.getServerResourceType(), CustomReportDataSource.class.getName());
    }
    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final String ignoredServiceClass = "testServiceClass";
        final String expectedDataSourceName = "testDataSourceName";
        final String expectedPropertyKey1 = "testPropertyKey1";
        final String expectedPropertyKey2 = "testPropertyKey2";
        final String expectedPropertyKey3 = "testPropertyKey3";
        final String expectedPropertyValue1 = "testPropertyValue1";
        final String expectedPropertyValue2 = "testPropertyValue2";
        propertiesToIgnore.add(expectedPropertyKey3);
        final CustomDataSourceDefinition customDataSourceDefinition = mock(CustomDataSourceDefinition.class);
        final String definedServiceClassName = "definedServiceClassName";
        when(customDataSourceDefinition.getServiceClassName()).thenReturn(definedServiceClassName);
        when(customDataSourceFactory.getDefinitionByName(expectedDataSourceName)).thenReturn(customDataSourceDefinition);
        final ClientCustomDataSource clientObject = new ClientCustomDataSource();
        clientObject.setServiceClass(ignoredServiceClass);
        clientObject.setDataSourceName(expectedDataSourceName);
        List<ClientProperty> properties = new ArrayList<ClientProperty>();
        properties.add(new ClientProperty(expectedPropertyKey1, expectedPropertyValue1));
        properties.add(new ClientProperty(expectedPropertyKey2, expectedPropertyValue2));
        clientObject.setProperties(properties);
        final CustomReportDataSource serverObject = new CustomReportDataSourceImpl();
        serverObject.setPropertyMap(new HashMap(){{
            put(expectedPropertyKey1, "valueToReplace");
            put(expectedPropertyKey2, "valueToReplace");
            put(expectedPropertyKey3, "valueToStay");
        }});
        final CustomReportDataSource result = converter.resourceSpecificFieldsToServer(ExecutionContextImpl.getRuntimeExecutionContext(), clientObject, serverObject, new ArrayList<Exception>(), null);
        assertSame(result, serverObject);
        // incoming serviceClass should be ignored. Value should be taken from corresponding custom data source definition.
        assertEquals(result.getServiceClass(), definedServiceClassName);
        assertEquals(result.getDataSourceName(), expectedDataSourceName);
        final Map<String, String> propertyMap = result.getPropertyMap();
        assertNotNull(propertyMap);
        assertEquals(propertyMap.size(), 3);
        assertEquals(propertyMap.get(expectedPropertyKey1), expectedPropertyValue1);
        assertEquals(propertyMap.get(expectedPropertyKey2), expectedPropertyValue2);
        assertEquals(propertyMap.get(expectedPropertyKey3), "valueToStay");
            }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedServiceClass = "testServiceClass";
        final String expectedDataSourceName = "testDataSourceName";
        final String expectedPropertyKey1 = "testPropertyKey1";
        final String expectedPropertyKey2 = "testPropertyKey2";
        final String expectedPropertyValue1 = "testPropertyValue1";
        final String expectedPropertyValue2 = "testPropertyValue2";
        final ClientCustomDataSource clientObject = new ClientCustomDataSource();
        final CustomReportDataSource serverObject = new CustomReportDataSourceImpl();
        serverObject.setServiceClass(expectedServiceClass);
        serverObject.setDataSourceName(expectedDataSourceName);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(expectedPropertyKey1, expectedPropertyValue1);
        properties.put(expectedPropertyKey2, expectedPropertyValue2);
        serverObject.setPropertyMap(properties);
        final ClientCustomDataSource result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertEquals(result.getServiceClass(), expectedServiceClass);
        assertEquals(result.getDataSourceName(), expectedDataSourceName);
        final List<ClientProperty> resultProperties = result.getProperties();
        assertNotNull(resultProperties);
        assertEquals(resultProperties.size(), 2);
        for(ClientProperty property : resultProperties){
            if(expectedPropertyKey1.equals(property.getKey())){
                assertEquals(property.getValue(), expectedPropertyValue1);
            } else if(expectedPropertyKey2.equals(property.getKey())){
                assertEquals(property.getValue(), expectedPropertyValue2);
            } else {
                // not allowed to have any other property name
                assertTrue(false);
            }
        }
    }

    @Test
    public void resourceSpecificFieldsToClient_ignore(){
        final String expectedServiceClass = "testServiceClass";
        final String expectedDataSourceName = "testDataSourceName";
        final String expectedPropertyKey1 = "testPropertyKey1";
        final String expectedPropertyKey2 = "testPropertyKey2";
        final String expectedPropertyValue1 = "testPropertyValue1";
        final String expectedPropertyValue2 = "testPropertyValue2";
        final ClientCustomDataSource clientObject = new ClientCustomDataSource();
        final CustomReportDataSource serverObject = new CustomReportDataSourceImpl();
        serverObject.setServiceClass(expectedServiceClass);
        serverObject.setDataSourceName(expectedDataSourceName);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(expectedPropertyKey1, expectedPropertyValue1);
        properties.put(expectedPropertyKey2, expectedPropertyValue2);
        serverObject.setPropertyMap(properties);

        propertiesToIgnore.add(expectedPropertyKey2);

        final ClientCustomDataSource result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertEquals(result.getServiceClass(), expectedServiceClass);
        assertEquals(result.getDataSourceName(), expectedDataSourceName);
        final List<ClientProperty> resultProperties = result.getProperties();
        assertNotNull(resultProperties);
        assertEquals(resultProperties.size(), 1);
        for(ClientProperty property : resultProperties){
            if(expectedPropertyKey1.equals(property.getKey())){
                assertEquals(property.getValue(), expectedPropertyValue1);
            } else {
                fail("This property should not be here");
            }
        }
    }

    @Test
    public void resourceSpecificFieldsToServer_convertResourcesToServer_isCalled() throws Exception{
        final ClientCustomDataSource clientObject = new ClientCustomDataSource();
        final HashMap<String, ClientReferenceableFile> clientResourcesMap = new HashMap<String, ClientReferenceableFile>();
        clientObject.setResources(clientResourcesMap);
        final CustomReportDataSourceImpl serverObject = new CustomReportDataSourceImpl();
        final ToServerConversionOptions options = ToServerConversionOptions.getDefault();
        final HashMap<String, ResourceReference> serverResources = new HashMap<String, ResourceReference>();
        when(converter.convertResourcesToServer(ctx, clientResourcesMap, options)).thenReturn(serverResources);
        final CustomReportDataSource result = converter.resourceSpecificFieldsToServer(ctx, clientObject, serverObject, new ArrayList<Exception>(), options);
        assertSame(result, serverObject);
        assertSame(result.getResources(), serverResources);
    }

    @Test
    public void resourceSpecificFieldsToClient_convertResourcesToClient_isCalled() throws Exception{
        final ClientCustomDataSource clientObject = new ClientCustomDataSource();
        final HashMap<String, ClientReferenceableFile> clientResourcesMap = new HashMap<String, ClientReferenceableFile>();
        final CustomReportDataSourceImpl serverObject = new CustomReportDataSourceImpl();
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        final HashMap<String, ResourceReference> serverResources = new HashMap<String, ResourceReference>();
        serverObject.setResources(serverResources);
        when(converter.convertResourcesToClient(serverResources, options)).thenReturn(clientResourcesMap);
        final ClientCustomDataSource result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, options);
        assertSame(result, clientObject);
        assertSame(result.getResources(), clientResourcesMap);
    }

    @Test
    public void convertResourcesToServer_nullSafety(){
        assertNull(converter.convertResourcesToServer(ExecutionContextImpl.getRuntimeExecutionContext(), null, null));
    }

    @Test
    public void convertResourcesToServer(){
        final HashMap<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        clientResources.put("/uri1", new ClientReference("/uri1"));
        clientResources.put("/uri2", new ClientReference("/uri2"));
        clientResources.put("/uri3", new ClientReference("/uri3"));
        clientResources.put("/uri4", new ClientReference("/uri4"));
        clientResources.put("/uri5", new ClientReference("/uri5"));
        final Map<String, ResourceReference> result = converter.convertResourcesToServer(ctx, clientResources, ToServerConversionOptions.getDefault());
        assertNotNull(result);
        assertEquals(result.size(), clientResources.size());
        for(String key : result.keySet()){
            assertEquals(key, result.get(key).getReferenceURI());
        }
    }

    @Test
    public void convertResourcesToClient_nullSafety(){
        assertNull(converter.convertResourcesToClient(null, null));
    }

    @Test
    public void convertResourcesToClient(){
        final HashMap<String, ResourceReference> serverResources = new HashMap<String, ResourceReference>();
        serverResources.put("/uri1", new ResourceReference("/uri1"));
        serverResources.put("/uri2", new ResourceReference("/uri2"));
        serverResources.put("/uri3", new ResourceReference("/uri3"));
        serverResources.put("/uri4", new ResourceReference("/uri4"));
        serverResources.put("/uri5", new ResourceReference("/uri5"));
        final Map<String, ClientReferenceableFile> result = converter.convertResourcesToClient(serverResources, ToClientConversionOptions.getDefault());
        assertNotNull(result);
        assertEquals(result.size(), serverResources.size());
        for(String key : result.keySet()){
            assertEquals(key, result.get(key).getUri());
        }
    }

    @Test
    public void resourceSecureFieldsToClient_clientPropsIsNull_resultContainsOneIgnoredProps() {
        final String expectedPropertyKey = "testPropertyKey1";
        final String expectedPropertyValue = "testPropertyValue1";
        final ClientCustomDataSource clientObject = new ClientCustomDataSource();
        final CustomReportDataSource serverObject = new CustomReportDataSourceImpl();
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(expectedPropertyKey, expectedPropertyValue);
        serverObject.setPropertyMap(properties);

        propertiesToIgnore.add(expectedPropertyKey);

        converter.resourceSecureFieldsToClient(clientObject, serverObject, null);

        final List<ClientProperty> resultProperties = clientObject.getProperties();
        assertNotNull(resultProperties);
        assertEquals(resultProperties.size(), 1);
        assertEquals(resultProperties.get(0).getKey(), expectedPropertyKey);
        assertEquals(resultProperties.get(0).getValue(), expectedPropertyValue);
    }

    @Test
    public void resourceSecureFieldsToClient_clientPropsContainsOneElement_resultContainsTwoProps() {
        final String expectedPropertyKey = "testPropertyKey1";
        final String expectedPropertyValue = "testPropertyValue1";
        final String existingPropertyKey = "testPropertyKey2";
        final String existingPropertyValue = "testPropertyValue2";

        List<ClientProperty> existingProps = Arrays.asList(new ClientProperty(existingPropertyKey, existingPropertyValue));
        final ClientCustomDataSource clientObject = new ClientCustomDataSource().setProperties(existingProps);
        final CustomReportDataSource serverObject = new CustomReportDataSourceImpl();
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(expectedPropertyKey, expectedPropertyValue);
        serverObject.setPropertyMap(properties);

        propertiesToIgnore.add(expectedPropertyKey);

        converter.resourceSecureFieldsToClient(clientObject, serverObject, null);

        final List<ClientProperty> resultProperties = clientObject.getProperties();
        assertNotNull(resultProperties);
        assertEquals(resultProperties.size(), 2);
        assertEquals(resultProperties.get(0).getKey(), existingPropertyKey);
        assertEquals(resultProperties.get(0).getValue(), existingPropertyValue);
        assertEquals(resultProperties.get(1).getKey(), expectedPropertyKey);
        assertEquals(resultProperties.get(1).getValue(), expectedPropertyValue);
    }

}
