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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.BeanReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientBeanDataSource;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class BeanDataSourceResourceConverterTest {
    private BeanDataSourceResourceConverter converter = new BeanDataSourceResourceConverter();

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeUtility.extractClientType(ClientBeanDataSource.class));
        assertEquals(converter.getServerResourceType(), BeanReportDataSource.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final String expectedBeanName = "testBeanName";
        final String expectedBeanMethod = "testBeanMethod";
        ClientBeanDataSource clientObject = new ClientBeanDataSource();
        BeanReportDataSource serverObject = new BeanReportDataSourceImpl();
        clientObject.setBeanMethod(expectedBeanMethod);
        clientObject.setBeanName(expectedBeanName);
        final BeanReportDataSource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, new ArrayList<Exception>(), null);
        assertSame(result, serverObject);
        assertEquals(result.getBeanMethod(), expectedBeanMethod);
        assertEquals(result.getBeanName(), expectedBeanName);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedBeanName = "testBeanName";
        final String expectedBeanMethod = "testBeanMethod";
        ClientBeanDataSource clientObject = new ClientBeanDataSource();
        BeanReportDataSource serverObject = new BeanReportDataSourceImpl();
        serverObject.setBeanMethod(expectedBeanMethod);
        serverObject.setBeanName(expectedBeanName);
        final ClientBeanDataSource result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertEquals(result.getBeanMethod(), expectedBeanMethod);
        assertEquals(result.getBeanName(), expectedBeanName);
    }
}
