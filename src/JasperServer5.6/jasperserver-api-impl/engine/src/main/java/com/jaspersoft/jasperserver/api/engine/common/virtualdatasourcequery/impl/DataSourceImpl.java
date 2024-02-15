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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: DataSourceImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class DataSourceImpl implements DataSource {

    protected String dataSourceName;
    protected ReportDataSource reportDataSource;
    protected VirtualReportDataSource parentDataSource;

    public DataSourceImpl(ReportDataSource reportDataSource, String dataSourceName, VirtualReportDataSource parentDataSource) {
        this.reportDataSource = reportDataSource;
        this.dataSourceName = dataSourceName;
        this.parentDataSource = parentDataSource;
    }

    /**
     * Returns the name of the resource.
	 *
     * @return name
	 */
    public String getDataSourceName() {
        if (dataSourceName != null) return dataSourceName;
        else return reportDataSource.getName();
    }

    /**
     * Returns the original domain report data source object
	 */
    protected ReportDataSource getReportDataSource() {
        return reportDataSource;
    }

    public VirtualReportDataSource getParentDataSource() {
        return parentDataSource;
    }

    public void setParentDataSource(VirtualReportDataSource parentDataSource) {
        this.parentDataSource = parentDataSource;
    }
}
