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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AzureSqlReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.AzureSqlReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientAzureSqlDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AzureSqlDataSourceResourceConverterTest {
    @InjectMocks
    private AzureSqlDataSourceResourceConverter converter;
    @Mock
    protected ResourceReferenceConverterProvider resourceReferenceConverterProvider;

    private final String expectedSubscriptionId = "expectedSubscriptionId";
    private final String expectedServerName = "expectedServerName";
    private final String expectedDbName = "expectedDbName";
    private final String expectedKeyStorePassword = "expectedKeyStorePassword";
    private final String expectedKeyStoreUri = "expectedKeyStoreUri";
    private final String expectedPassword = "expectedPassword";
    private AzureSqlReportDataSource expectedServerObject;
    private ClientAzureSqlDataSource expectedClientObject;
    private final ClientAzureSqlDataSource clientEmptyObject = new ClientAzureSqlDataSource();
    private final ClientFile clientFile = new ClientFile().setUri(expectedKeyStoreUri);
    private final ResourceReference keyStoreResource = new ResourceReference(expectedKeyStoreUri);

    @Before
    public void refresh(){
        ResourceReferenceConverter resourceReferenceConverter = mock(ResourceReferenceConverter.class);
        when(resourceReferenceConverter.toClient(eq(keyStoreResource), any(ToClientConversionOptions.class))).
                thenReturn(clientFile);
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class)).
                thenReturn(resourceReferenceConverter);
        expectedServerObject = prepareServerObject();
        expectedClientObject = prepareClientObject();
    }

    @Test
    public void resourceSecureFieldsToClient_resultContainsSetKeyStorePasswordAndPassword() {
        ClientAzureSqlDataSource client = new ClientAzureSqlDataSource(expectedClientObject)
                .setKeyStorePassword(null).setPassword(null);

        converter.resourceSecureFieldsToClient(client, expectedServerObject,
                null);

        assertEquals(client, expectedClientObject);
    }

    private AzureSqlReportDataSource prepareServerObject() {
        AzureSqlReportDataSource serverObject = new AzureSqlReportDataSourceImpl();
        serverObject.setSubscriptionId(expectedSubscriptionId);
        serverObject.setServerName(expectedServerName);
        serverObject.setDbName(expectedDbName);
        serverObject.setKeyStorePassword(expectedKeyStorePassword);
        serverObject.setPassword(expectedPassword);
        serverObject.setKeyStoreResource(keyStoreResource);

        return serverObject;
    }

    private ClientAzureSqlDataSource prepareClientObject() {
        expectedClientObject = new ClientAzureSqlDataSource()
                .setSubscriptionId(expectedSubscriptionId)
                .setServerName(expectedServerName)
                .setDbName(expectedDbName)
                .setKeyStorePassword(expectedKeyStorePassword)
                .setKeyStoreUri(expectedKeyStoreUri)
                .setPassword(expectedPassword);
        return expectedClientObject;
    }

}
