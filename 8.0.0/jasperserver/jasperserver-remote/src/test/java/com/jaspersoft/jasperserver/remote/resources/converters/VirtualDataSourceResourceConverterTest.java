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

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.VirtualReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientSubDataSourceReference;
import com.jaspersoft.jasperserver.dto.resources.ClientVirtualDataSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class VirtualDataSourceResourceConverterTest {
    @InjectMocks
    private VirtualDataSourceResourceConverter converter = new VirtualDataSourceResourceConverter();

    @Mock
    protected RepositoryService repositoryService;

    @BeforeMethod
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeUtility.extractClientType(ClientVirtualDataSource.class));
        assertEquals(converter.getServerResourceType(), VirtualReportDataSource.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final String expectedId1 = "testId1";
        final String expectedId2 = "testId2";
        final String expectedUri1 = "testUri1";
        final String expectedUri2 = "testUri2";
        ClientVirtualDataSource clientObject = new ClientVirtualDataSource();
        VirtualReportDataSource serverObject = new VirtualReportDataSourceImpl();
        List<ClientSubDataSourceReference> subDataSourceReferences = new ArrayList<ClientSubDataSourceReference>();
        ClientSubDataSourceReference reference = new ClientSubDataSourceReference();
        reference.setId(expectedId1);
        reference.setUri(expectedUri1);
        subDataSourceReferences.add(reference);
        reference = new ClientSubDataSourceReference();
        reference.setId(expectedId2);
        reference.setUri(expectedUri2);
        subDataSourceReferences.add(reference);
        clientObject.setSubDataSources(subDataSourceReferences);
        final VirtualReportDataSource result = converter.resourceSpecificFieldsToServer(    ExecutionContextImpl.getRuntimeExecutionContext()
                , clientObject, serverObject, new ArrayList<Exception>(), null);
        assertSame(result, serverObject);
        final Map<String,ResourceReference> dataSourceUriMap = result.getDataSourceUriMap();
        assertNotNull(dataSourceUriMap);
        assertEquals(dataSourceUriMap.size(), 2);
        assertTrue(dataSourceUriMap.containsKey(expectedId1));
        assertTrue(dataSourceUriMap.containsKey(expectedId2));
        assertEquals(dataSourceUriMap.get(expectedId1).getReferenceURI(), expectedUri1);
        assertEquals(dataSourceUriMap.get(expectedId2).getReferenceURI(), expectedUri2);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedId1 = "testId1";
        final String expectedId2 = "testId2";
        final String expectedUri1 = "testUri1";
        final String expectedUri2 = "testUri2";
        ClientVirtualDataSource clientObject = new ClientVirtualDataSource();
        VirtualReportDataSource serverObject = new VirtualReportDataSourceImpl();
        final Map<String,ResourceReference> dataSourceUriMap = new HashMap<String, ResourceReference>();
        dataSourceUriMap.put(expectedId1, new ResourceReference(expectedUri1));
        dataSourceUriMap.put(expectedId2, new ResourceReference(expectedUri2));
        serverObject.setDataSourceUriMap(dataSourceUriMap);
        final ClientVirtualDataSource result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        final List<ClientSubDataSourceReference> subDataSources = result.getSubDataSources();
        assertNotNull(subDataSources);
        assertEquals(subDataSources.size(), 2);
        for(ClientSubDataSourceReference currentReference : subDataSources){
            if(expectedId1.equals(currentReference.getId())){
                assertEquals(expectedUri1, currentReference.getUri());
            } else if(expectedId2.equals(currentReference.getId())){
                assertEquals(expectedUri2, currentReference.getUri());
            } else {
                // no other subResource id is allowed
                assertTrue(false);
            }
        }
    }
}
