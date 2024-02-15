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

package com.jaspersoft.jasperserver.ws.axis2.authority;

import org.apache.axis.AxisFault;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.jaspersoft.jasperserver.ws.authority.*;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;

import java.util.Properties;

/**
 * @author stas.chubar
 */
public class PermissionsManagement extends ServletEndpointSupport {

	public static final String SERVICE_BEAN_NAME = "permissionsManagementService";
	public static final String SPRING_PROPERTIES_BEAN_NAME = "springConfiguration";
	public static final String SERVICE_BEAN_PROPERTIE = "bean.permissionsManagementService";

    public WSObjectPermission[] getPermissionsForObject(String targetURI) throws AxisFault {
        return getService().getPermissionsForObject(targetURI);
    }

    public WSObjectPermission putPermission(WSObjectPermission objectPermission) throws AxisFault {
        return getService().putPermission(objectPermission);
    }

    public void deletePermission(WSObjectPermission objectPermission) throws AxisFault {
        getService().deletePermission(objectPermission);
    }

	protected PermissionsManagementService getService() {
        Properties springProperties = (Properties) getApplicationContext().getBean(SPRING_PROPERTIES_BEAN_NAME);

        String serviceBeanName = SERVICE_BEAN_NAME;

        if (springProperties != null && springProperties.containsKey(SERVICE_BEAN_PROPERTIE)) {
            serviceBeanName = springProperties.getProperty(SERVICE_BEAN_PROPERTIE);
        }

		return (PermissionsManagementService) getApplicationContext().getBean(serviceBeanName, PermissionsManagementService.class);
	}

}
