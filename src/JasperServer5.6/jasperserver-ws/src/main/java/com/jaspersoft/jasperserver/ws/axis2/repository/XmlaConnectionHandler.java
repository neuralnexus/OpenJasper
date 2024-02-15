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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;

/**
 * @author gtoffoli
 * @version $Id: XmlaConnectionHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class XmlaConnectionHandler extends RepositoryResourceHandler {

	public Class getResourceType() {
		return XMLAConnection.class;
	}

	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext) {
		XMLAConnection connection = (XMLAConnection) resource;
		descriptor.setWsType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
		descriptor.setHasData(false);
		descriptor.setIsReference(false);
		descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_URI,
				connection.getURI());
		descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_CATALOG,
				connection.getCatalog());
		descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_DATASOURCE,
				connection.getDataSource());
		descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_USERNAME,
				connection.getUsername());
		descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_PASSWORD,
				connection.getPassword());
	}

	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor, RepositoryServiceContext serviceContext) {
		XMLAConnection xmlaConnection = (XMLAConnection) resource;
		xmlaConnection.setURI(descriptor
				.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_URI));
		xmlaConnection
				.setCatalog(descriptor
						.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_CATALOG));
		xmlaConnection
				.setDataSource(descriptor
						.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_DATASOURCE));
		xmlaConnection
				.setUsername(descriptor
						.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_USERNAME));
		xmlaConnection
				.setPassword(descriptor
						.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_PASSWORD));
	}

}
