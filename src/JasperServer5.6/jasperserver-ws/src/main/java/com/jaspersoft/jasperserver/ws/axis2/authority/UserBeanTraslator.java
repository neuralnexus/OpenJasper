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

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSUser;

import java.util.*;

/**
 * @author stas.chubar
 */
public class UserBeanTraslator {

    RoleBeanTraslator roleBeanTraslator = new RoleBeanTraslator();

	public static WSUser toWSUser(User user) {
		WSUser wsUser = new WSUser();

        wsUser.setUsername(user.getUsername());
        wsUser.setFullName(user.getFullName());
        wsUser.setEmailAddress(user.getEmailAddress());
        wsUser.setEnabled(user.isEnabled());
        wsUser.setExternallyDefined(user.isExternallyDefined());
        wsUser.setPassword(null);  //blanking out password for security reasons
        wsUser.setPreviousPasswordChangeTime(user.getPreviousPasswordChangeTime());
        wsUser.setTenantId(user.getTenantId());

        if (user.getRoles() != null) {

            wsUser.setRoles(RoleBeanTraslator.toWSRoleArray(user.getRoles()));
        }

        return wsUser;
	}

	public static User toUser(WSUser wsUser) {
		User user = new UserImpl();

        user.setUsername(wsUser.getUsername());
        user.setFullName(wsUser.getFullName());
        user.setEmailAddress(wsUser.getEmailAddress());
        if (wsUser.getEnabled() == null) {
            user.setEnabled(false);
        } else {
            user.setEnabled(wsUser.getEnabled());
        }
        if (wsUser.getExternallyDefined() == null ) {
            user.setExternallyDefined(false);
        } else {
            user.setExternallyDefined(wsUser.getExternallyDefined());
        }
        user.setPassword(wsUser.getPassword());
        user.setPreviousPasswordChangeTime(wsUser.getPreviousPasswordChangeTime());
        user.setTenantId(wsUser.getTenantId());

        if (wsUser.getRoles() != null) {

            user.setRoles(RoleBeanTraslator.toRoleSet(wsUser.getRoles()));
        }

		return user;
	}

    public static WSUser[] toWSUserArray(List users) {
        if (users == null) {
            return null;
        }

        List<WSUser> wsUsers = new ArrayList<WSUser>();

        for(Object o : users) {
            User u = (User) o;

            wsUsers.add(toWSUser(u));
        }

        return wsUsers.toArray(new WSUser[wsUsers.size()]);
    }

    public static WSUser[] toWSUserArray(Set users) {
        if (users == null) {
            return null;
        }

        List<WSUser> wsUsers = new ArrayList<WSUser>();

        for(Object o : users) {
            User u = (User) o;

            wsUsers.add(toWSUser(u));
        }

        return wsUsers.toArray(new WSUser[wsUsers.size()]);
    }

    public static List<User> toUserList(WSUser[] wsUsers) {

        if (wsUsers == null) {
            return null;
        }

        List<User> userList = new ArrayList<User>();

        for(WSUser u : wsUsers) {

            userList.add(toUser(u));
        }

        return userList;
    }

    public static Set<User> toUserSet(WSUser[] wsUsers) {

        if (wsUsers == null) {
            return null;
        }

        Set<User> userSet = new HashSet<User>();

        for(WSUser u : wsUsers) {

            userSet.add(toUser(u));
        }

        return userSet;
    }

}