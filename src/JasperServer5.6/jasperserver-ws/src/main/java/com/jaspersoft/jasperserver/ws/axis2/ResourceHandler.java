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

package com.jaspersoft.jasperserver.ws.axis2;

import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Request;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface ResourceHandler {

	Class getResourceType();

	ResourceDescriptor describe(Resource resource, Map arguments,
			RepositoryServiceContext serviceContext) throws WSException;

	void getAttachments(Resource resource, Map arguments,
			ResourceDescriptor descriptor,
			ResultAttachments attachments, RepositoryServiceContext serviceContext) throws WSException;

	void put(ServiceRequest request) throws WSException;

	void delete(ResourceDescriptor descriptor,
			RepositoryServiceContext serviceContext) throws WSException;

	void move(Request request,
			RepositoryServiceContext serviceContext) throws WSException;

	ResourceDescriptor copy(Request request,
			RepositoryServiceContext serviceContext) throws WSException;

	List listResources(Request request, 
			RepositoryServiceContext serviceContext) throws WSException;

}
