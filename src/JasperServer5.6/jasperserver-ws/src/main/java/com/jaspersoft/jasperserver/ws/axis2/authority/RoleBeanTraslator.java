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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSUser;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * @author stas.chubar
 */
public class RoleBeanTraslator {

	public static WSRole toWSRole(Role role) {
		WSRole wsRole = new WSRole();

		wsRole.setRoleName(role.getRoleName());
		wsRole.setExternallyDefined(role.isExternallyDefined());
        wsRole.setTenantId(role.getTenantId());

        if (role.getUsers() != null) {
            wsRole.setUsers(UserBeanTraslator.toWSUserArray(role.getUsers()));
        }

        return wsRole;
	}

	public static Role toRole(WSRole wsRole) {
		Role role = new RoleImpl();

		role.setRoleName(wsRole.getRoleName());
        if (wsRole.getExternallyDefined() == null) {
            role.setExternallyDefined(false);
        } else {
            role.setExternallyDefined(wsRole.getExternallyDefined());
        }
        role.setTenantId(wsRole.getTenantId());

        if (wsRole.getUsers() != null) {

            List<User> users = UserBeanTraslator.toUserList(wsRole.getUsers());
            for (User u : users) {
                role.addUser(u);
            }
        }

        return role;
	}

    public static WSRole[] toWSRoleArray(List roles) {
        List<WSRole> wsRoles = new ArrayList<WSRole>();

        for(Object o : roles) {
            Role r = (Role) o;

            wsRoles.add(toWSRole(r));
        }

        return wsRoles.toArray(new WSRole[wsRoles.size()]);
    }

    public static WSRole[] toWSRoleArray(Set roles) {
        List<WSRole> wsRoles = new ArrayList<WSRole>();

        for(Object o : roles) {
            Role r = (Role) o;

            wsRoles.add(toWSRole(r));
        }

        return wsRoles.toArray(new WSRole[wsRoles.size()]);
    }

    public static List<Role> toRoleList(WSRole[] wsRoles) {

        if (wsRoles == null) {
            return null;
        }

        List<Role> roleList = new ArrayList<Role>();

        for(WSRole r : wsRoles) {

            roleList.add(toRole(r));
        }

        return roleList;
    }

    public static Set<Role> toRoleSet(WSRole[] wsRoles) {

        if (wsRoles == null) {
            return null;
        }

        Set<Role> roleSet = new HashSet<Role>();

        for(WSRole r : wsRoles) {

            roleSet.add(toRole(r));
        }

        return roleSet;
    }

}