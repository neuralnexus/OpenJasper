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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yehor Bobyk
 */
@Deprecated
public class DataSourceConfiguration {

    private final DataSourceType[] DATA_SOURCE_TYPES = new DataSourceType[] {
            new DataSourceType(JdbcReportDataSource.class, JasperServerConstImpl.getJDBCDatasourceType(), "dataSource.jdbc"),
            new DataSourceType(JndiJdbcReportDataSource.class, JasperServerConstImpl.getJNDIDatasourceType(), "dataSource.jndi"),
            new DataSourceType(VirtualReportDataSource.class, JasperServerConstImpl.getVirtualDatasourceType(), "dataSource.virtual"),
            new DataSourceType(JdbcReportDataSource.class, JasperServerConstImpl.getAwsDatasourceType(), "dataSource.aws"),
            new DataSourceType(BeanReportDataSource.class, JasperServerConstImpl.getBeanDatasourceType(), "dataSource.bean"),
    };

    public List getDataSourceTypes() {
        ArrayList types = new ArrayList(DATA_SOURCE_TYPES.length);
        for (int i = 0; i < DATA_SOURCE_TYPES.length; i++) {
            types.add(DATA_SOURCE_TYPES[i]);
        }
        return types;
    }

    public static class DataSourceType {
        private final Class type;
        private final String typeValue;
        private final String labelMessage;

        public DataSourceType(final Class type, final String typeValue, final String labelMessage) {
            this.type = type;
            this.typeValue = typeValue;
            this.labelMessage = labelMessage;
        }

        public String getLabelMessage() {
            return labelMessage;
        }

        public Class getType() {
            return type;
        }

        public String getTypeValue() {
            return typeValue;
        }
    }

}
