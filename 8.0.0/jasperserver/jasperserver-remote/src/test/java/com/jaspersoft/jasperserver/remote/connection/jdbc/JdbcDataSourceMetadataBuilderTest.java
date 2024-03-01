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

package com.jaspersoft.jasperserver.remote.connection.jdbc;

import com.jaspersoft.jasperserver.dto.resources.ClientBeanDataSource;
import com.jaspersoft.jasperserver.remote.connection.ContextMetadataBuilder;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JdbcDataSourceMetadataBuilderTest {

    JdbcDataSourceMetadataBuilder jdbcDataSourceMetadataBuilder = new JdbcDataSourceMetadataBuilder();

    @Test
    void supportedBeanTest() {
        List<String> supportedList = (List)Arrays.asList(
                "com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource",
                "com.jaspersoft.jasperserver.dto.resources.ClientBeanDataSource"
        );
        jdbcDataSourceMetadataBuilder.buildProcessingClasses(supportedList);
        Assert.assertTrue(jdbcDataSourceMetadataBuilder.getProcessableTypes(
                ContextMetadataBuilder.class).contains(ClientBeanDataSource.class));
    }


}
