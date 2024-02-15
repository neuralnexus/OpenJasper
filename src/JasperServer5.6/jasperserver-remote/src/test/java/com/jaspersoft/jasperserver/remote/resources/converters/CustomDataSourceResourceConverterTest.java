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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.CustomReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientCustomDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: CustomDataSourceResourceConverterTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class CustomDataSourceResourceConverterTest {

    @InjectMocks
    private CustomDataSourceResourceConverter converter = new CustomDataSourceResourceConverter();

    private Set<String> propertiesToIgnore = new HashSet<String>();
    @Mock
    private CustomReportDataSourceServiceFactory customDataSourceFactory;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        converter.propertiesToIgnore = propertiesToIgnore;
    }

    @BeforeMethod
    public void refresh(){
        propertiesToIgnore.clear();
        reset(customDataSourceFactory);
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientCustomDataSource.class));
        assertEquals(converter.getServerResourceType(), CustomReportDataSource.class.getName());
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
        final CustomReportDataSource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
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
}
