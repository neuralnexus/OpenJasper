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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JndiJdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
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
public class JndiJdbcDataSourceResourceConverterTest {
    private JndiJdbcDataSourceResourceConverter converter = new JndiJdbcDataSourceResourceConverter();

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeUtility.extractClientType(ClientJndiJdbcDataSource.class));
        assertEquals(converter.getServerResourceType(), JndiJdbcReportDataSource.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final String expectedJndiName = "testJndiName";
        final String expectedTimezone = "testTime";
        ClientJndiJdbcDataSource clientObject = new ClientJndiJdbcDataSource();
        JndiJdbcReportDataSource serverObject = new JndiJdbcReportDataSourceImpl();
        clientObject.setJndiName(expectedJndiName);
        clientObject.setTimezone(expectedTimezone);
        final JndiJdbcReportDataSource result = converter.resourceSpecificFieldsToServer(    ExecutionContextImpl.getRuntimeExecutionContext()
                , clientObject, serverObject, new ArrayList<Exception>(), null);
        assertSame(result, serverObject);
        assertEquals(result.getJndiName(), expectedJndiName);
        assertEquals(result.getTimezone(), expectedTimezone);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedJndiName = "testJndiName";
        final String expectedTimezone = "testTime";
        ClientJndiJdbcDataSource clientObject = new ClientJndiJdbcDataSource();
        JndiJdbcReportDataSource serverObject = new JndiJdbcReportDataSourceImpl();
        serverObject.setJndiName(expectedJndiName);
        serverObject.setTimezone(expectedTimezone);
        final ClientJndiJdbcDataSource result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertEquals(result.getJndiName(), expectedJndiName);
        assertEquals(result.getTimezone(), expectedTimezone);
    }
}
