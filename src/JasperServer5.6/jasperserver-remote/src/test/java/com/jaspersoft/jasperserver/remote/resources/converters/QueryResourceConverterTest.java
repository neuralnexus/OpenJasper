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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientQuery;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: QueryResourceConverterTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class QueryResourceConverterTest {
    @InjectMocks
    private QueryResourceConverter converter = new QueryResourceConverter();
    @Mock
    private ResourceReferenceConverter resourceReferenceConverter;

    @BeforeClass
    public void initConverter() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientQuery.class));
        assertEquals(converter.getServerResourceType(), Query.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final String expectedQueryValue = "testQueryValue";
        final String expectedQueryLanguage = "testQueryLanguage";
        final ClientQuery clientObject = new ClientQuery();
        clientObject.setLanguage(expectedQueryLanguage);
        clientObject.setValue(expectedQueryValue);
        final Query expectedServerObject = new QueryImpl();
        final Query result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertSame(result, expectedServerObject);
        assertEquals(result.getLanguage(), expectedQueryLanguage);
        assertEquals(result.getSql(), expectedQueryValue);

    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedQueryValue = "testQueryValue";
        final String expectedQueryLanguage = "testQueryLanguage";
        final ClientQuery clientObject = new ClientQuery();
        final Query serverObject = new QueryImpl();
        serverObject.setLanguage(expectedQueryLanguage);
        serverObject.setSql(expectedQueryValue);
        final ClientQuery result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertEquals(result.getLanguage(), expectedQueryLanguage);
        assertEquals(result.getValue(), expectedQueryValue);
    }
}
