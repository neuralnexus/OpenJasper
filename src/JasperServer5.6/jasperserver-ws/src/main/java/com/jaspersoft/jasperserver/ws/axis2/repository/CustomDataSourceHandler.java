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

package com.jaspersoft.jasperserver.ws.axis2.repository;

import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;


public class CustomDataSourceHandler extends RepositoryResourceHandler {

	public Class getResourceType() {
		return CustomReportDataSource.class;
	}

	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext) {
		CustomReportDataSource dsResource = (CustomReportDataSource) resource;
		descriptor.setServiceClass(dsResource.getServiceClass());
		descriptor.setPropertyMap(dsResource.getPropertyMap());
		descriptor.setWsType(ResourceDescriptor.TYPE_DATASOURCE_CUSTOM);
	}

	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor, RepositoryServiceContext serviceContext) {
		CustomReportDataSource ds = (CustomReportDataSource) resource;
		ds.setServiceClass(descriptor.getServiceClass());
		ds.setPropertyMap(descriptor.getPropertyMap());
	}

}
