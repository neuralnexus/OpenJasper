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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;

/**
 * @author gtoffoli
 * @version $Id: UnknownResourceHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class UnknownResourceHandler extends RepositoryResourceHandler {

	private final static Log log = LogFactory.getLog(UnknownResourceHandler.class); 

	public Class getResourceType() {
		return Resource.class;
	}
	
	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext) {
		descriptor.setWsType(ResourceDescriptor.TYPE_UNKNOW);
		
		log.warn("Unknow resource type: "
				+ resource.getResourceType() + " instanceof "
				+ resource.getClass().getName());
	}


	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor, RepositoryServiceContext newParam) {
		// nothing
	}

}
