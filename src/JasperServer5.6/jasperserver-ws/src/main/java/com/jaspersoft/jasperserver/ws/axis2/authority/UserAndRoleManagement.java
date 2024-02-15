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

import com.jaspersoft.jasperserver.ws.authority.WSUser;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria;
import com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria;

import java.util.Properties;

/**
 * @author stas.chubar
 */
public class UserAndRoleManagement extends ServletEndpointSupport {

	public static final String SERVICE_BEAN_NAME = "userAndRoleManagementService";
	public static final String SPRING_PROPERTIES_BEAN_NAME = "springConfiguration";
	public static final String SERVICE_BEAN_PROPERTIE = "bean.userAndRoleManagementService";

    public WSUser[] findUsers(WSUserSearchCriteria criteria) throws AxisFault {
        return getService().findUsers(criteria);
    }

    public WSUser putUser(WSUser user) throws AxisFault {
        return getService().putUser(user);
    }

    public void deleteUser(WSUser user) throws AxisFault {
        getService().deleteUser(user);
    }

    public WSRole[] findRoles(WSRoleSearchCriteria criteria) throws AxisFault {
        return getService().findRoles(criteria);
    }

    public WSRole putRole(WSRole role) throws AxisFault {
        return getService().putRole(role);
    }

    public WSRole updateRoleName(WSRole oldRole, String newName) throws AxisFault {
        return getService().updateRoleName(oldRole, newName);
    }

    public void deleteRole(WSRole role) throws AxisFault {
        getService().deleteRole(role);
    }

	protected UserAndRoleManagementService getService() {
        Properties springProperties = (Properties) getApplicationContext().getBean(SPRING_PROPERTIES_BEAN_NAME);

        String serviceBeanName = SERVICE_BEAN_NAME;

        if (springProperties != null && springProperties.containsKey(SERVICE_BEAN_PROPERTIE)) {
            serviceBeanName = springProperties.getProperty(SERVICE_BEAN_PROPERTIE);
        }

		return (UserAndRoleManagementService) getApplicationContext().getBean(serviceBeanName, UserAndRoleManagementService.class);
	}

}