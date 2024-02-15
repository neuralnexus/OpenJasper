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
package com.jaspersoft.jasperserver.datasource.test;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author swood
 *
 */
public class CustomDataSourceService implements ReportDataSourceService {

	JRDataSource ds;
	
	public CustomDataSourceService() {
		this.ds = new CustomDataSource();
	}
	
	public CustomDataSourceService(JRDataSource ds) {
		this.ds = ds;
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService#closeConnection()
	 */
	public void closeConnection() {
		// Do nothing
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService#setReportParameterValues(java.util.Map)
	 */
	public void setReportParameterValues(Map parameterValues) {
		parameterValues.put(JRParameter.REPORT_DATA_SOURCE, ds);
	}

}
