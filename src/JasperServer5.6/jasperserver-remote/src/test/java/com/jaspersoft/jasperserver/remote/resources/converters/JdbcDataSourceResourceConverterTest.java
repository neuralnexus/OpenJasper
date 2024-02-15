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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JdbcDataSourceResourceConverterTest {

    private JdbcDataSourceResourceConverter converter = new JdbcDataSourceResourceConverter();

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientJdbcDataSource.class));
        assertEquals(converter.getServerResourceType(), JdbcReportDataSource.class.getName());
    }
}
