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

import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: LookupResourceConverterTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class LookupResourceConverterTest {
    @InjectMocks
    private LookupResourceConverter converter = new LookupResourceConverter();
    @Mock
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    private LookupResourceConverter converterMock;

    @BeforeClass
    public void initClass() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void initMock() {
        converterMock = mock(LookupResourceConverter.class);
    }

    @Test
    public void toServer_notSupported() {
        IllegalStateException exception = null;
        try {
            converter.toServer(new ClientResourceLookup(), null);
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
        exception = null;
        try {
            converter.toServer(new ClientResourceLookup(), new ResourceLookupImpl(), null);
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
    }

    @Test
    public void resourceSpecificFieldsToClient() {
        final ClientResourceLookup expectedResult = new ClientResourceLookup();
        final ResourceLookupImpl serverObject = new ResourceLookupImpl();
        serverObject.setURIString("/uri/string/to/avoid/exception");
        final String serverResourceType = "serverResourceType";
        final String clientResourceType = "clientResourceType";
        serverObject.setResourceType(serverResourceType);
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        when(converterMock.resourceSpecificFieldsToClient(expectedResult, serverObject, options)).thenCallRealMethod();
        when(converterMock.toClientResourceType(serverResourceType)).thenReturn(clientResourceType);
        final ClientResourceLookup result = converterMock.resourceSpecificFieldsToClient(expectedResult, serverObject, options);
        assertSame(result, expectedResult);
        assertEquals(result.getResourceType(), clientResourceType);
    }

    @Test
    public void toClientResourceType() {
        final String serverResourceType = "serverResourceType";
        final String clientResourceType = "clientResourceType";
        when(genericTypeProcessorRegistry.getTypeProcessor(serverResourceType, ToClientConverter.class, false)).thenReturn(new ToClientConverter() {
            @Override
            public Object toClient(Object serverObject, ToClientConversionOptions options) {
                return null;
            }

            @Override
            public String getClientResourceType() {
                return clientResourceType;
            }
        });
        assertEquals(converter.toClientResourceType(serverResourceType), clientResourceType);
    }

    @Test
    public void toClientResourceType_unknownType() {
        final String serverResourceType = "serverResourceType";
        final String clientResourceType = "unknown";
        reset(genericTypeProcessorRegistry);
        assertEquals(converter.toClientResourceType(serverResourceType), clientResourceType);
    }


}
