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

package com.jaspersoft.jasperserver.war.common;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataSourceConfigurationTest {
    private static final Class<String> STRING_CLASS = String.class;
    private static final String TYPE_VALUE = "typeValue";
    private static final String LABEL_MESSAGE = "labelMessage";

    private static final List<DataSourceConfiguration.DataSourceType> DATA_SOURCE_TYPE_LIST = new ArrayList<DataSourceConfiguration.DataSourceType>() {{
        add(new DataSourceConfiguration.DataSourceType(JdbcReportDataSource.class, JasperServerConstImpl.getJDBCDatasourceType(), "dataSource.jdbc"));
        add(new DataSourceConfiguration.DataSourceType(JndiJdbcReportDataSource.class, JasperServerConstImpl.getJNDIDatasourceType(), "dataSource.jndi"));
        add(new DataSourceConfiguration.DataSourceType(VirtualReportDataSource.class, JasperServerConstImpl.getVirtualDatasourceType(), "dataSource.virtual"));
        add(new DataSourceConfiguration.DataSourceType(JdbcReportDataSource.class, JasperServerConstImpl.getAwsDatasourceType(), "dataSource.aws"));
        add(new DataSourceConfiguration.DataSourceType(BeanReportDataSource.class, JasperServerConstImpl.getBeanDatasourceType(), "dataSource.bean"));
    }};


    @Test
    void getAndSet_instanceWithDefaultValues() {
        final DataSourceConfiguration instance = new DataSourceConfiguration();
        assertAll("an instance with default values",
                new Executable() {
                    @Override
                    public void execute() {
                        assertDataSourceTypeEquals(DATA_SOURCE_TYPE_LIST, instance.getDataSourceTypes());
                    }
                }
        );
    }

    @Nested
    class DataSourceTypeTest {
        @Test
        void getAndSet_instanceWithDefaultValues() {
            DataSourceConfiguration.DataSourceType dataSourceType = new DataSourceConfiguration.DataSourceType(null, null, null);
            assertNull(dataSourceType.getLabelMessage());
            assertNull(dataSourceType.getType());
            assertNull(dataSourceType.getTypeValue());
        }

        @Test
        void getAndSet_fullyConfiguredInstance() {
            DataSourceConfiguration.DataSourceType dataSourceType = new DataSourceConfiguration.DataSourceType(STRING_CLASS, TYPE_VALUE, LABEL_MESSAGE);
            assertEquals(STRING_CLASS, dataSourceType.getType());
            assertEquals(TYPE_VALUE, dataSourceType.getTypeValue());
            assertEquals(LABEL_MESSAGE, dataSourceType.getLabelMessage());
        }
    }

    private static void assertDataSourceTypeEquals(List<DataSourceConfiguration.DataSourceType> expected, List<DataSourceConfiguration.DataSourceType> result) {
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getLabelMessage(), result.get(i).getLabelMessage());
            assertEquals(expected.get(i).getType(), result.get(i).getType());
            assertEquals(expected.get(i).getTypeValue(), result.get(i).getTypeValue());
        }
    }

}
