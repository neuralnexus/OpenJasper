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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;
import com.jaspersoft.jasperserver.ws.axis2.WSException;

/**
 * @author gtoffoli
 * @version $Id: QueryHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class QueryHandler extends RepositoryResourceHandler {

	public Class getResourceType() {
		return Query.class;
	}

	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext) throws WSException {
		Query fileResource = (Query) resource;
		descriptor.setWsType(ResourceDescriptor.TYPE_QUERY);
		descriptor.setHasData(false);
		descriptor.setIsReference(false);
		descriptor.setSql(fileResource.getSql());
		descriptor.setResourceProperty(ResourceDescriptor.PROP_QUERY_LANGUAGE,
				fileResource.getLanguage());

		// Get datasource...
		ResourceReference rref = fileResource.getDataSource();
		if (rref != null) {
			ResourceDescriptor childRd = null;

			if (rref.isLocal()) {
				childRd = serviceContext.createResourceDescriptor(rref
						.getLocalResource());
			} else {
				childRd = new ResourceDescriptor();
				childRd.setWsType(ResourceDescriptor.TYPE_DATASOURCE);
				childRd.setReferenceUri(rref.getReferenceURI());
				childRd.setIsReference(true);
			}

			if (childRd != null) {
				descriptor.getChildren().add(childRd);
			}
		}
	}

	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor, RepositoryServiceContext serviceContext) throws WSException {
		Query query = (Query) resource;

		query.setSql(descriptor.getSql());

		// Update the datasource....
		for (int i = 0; i < descriptor.getChildren().size(); ++i) {
			ResourceDescriptor childResource = (ResourceDescriptor) descriptor
					.getChildren().get(i);
			if (serviceContext.getHandlerRegistry().typeExtends(childResource.getWsType(),
					ResourceDescriptor.TYPE_DATASOURCE)) {
				if (childResource.getIsReference()) {
					query.setDataSourceReference(childResource
							.getReferenceUri());
				} else {
					ReportDataSource datasource = (ReportDataSource) toChildResource(childResource, serviceContext);
					query.setDataSource(datasource);
				}
			}
		}

		String lang = descriptor
				.getResourcePropertyValue(ResourceDescriptor.PROP_QUERY_LANGUAGE);
		if (lang == null || lang.length() == 0)
			lang = "sql";
		query.setLanguage(lang);
	}

}
