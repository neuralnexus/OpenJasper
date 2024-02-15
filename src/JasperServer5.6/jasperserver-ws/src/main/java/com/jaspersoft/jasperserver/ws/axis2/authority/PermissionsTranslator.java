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

import com.jaspersoft.jasperserver.ws.authority.WSObjectPermission;
import com.jaspersoft.jasperserver.ws.authority.WSUser;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;

import java.util.List;

public class PermissionsTranslator {
    
    public static WSObjectPermission toWSObjectPermission(ObjectPermission objectPermission) {
        WSObjectPermission wsObjectPermission = new WSObjectPermission();

        wsObjectPermission.setPermissionMask(objectPermission.getPermissionMask());

        if (objectPermission.getPermissionRecipient() instanceof User) {
            wsObjectPermission.setPermissionRecipient(
                    UserBeanTraslator.toWSUser((User)objectPermission.getPermissionRecipient()));
        } else if (objectPermission.getPermissionRecipient() instanceof Role) {
            wsObjectPermission.setPermissionRecipient(
                    RoleBeanTraslator.toWSRole((Role)objectPermission.getPermissionRecipient()));
        }

        wsObjectPermission.setUri(objectPermission.getURI());

        return wsObjectPermission;
    }

    public static WSObjectPermission[] toWSTenantArray(List objectPermissionList) {
        WSObjectPermission[] wsObjectPermissions = new WSObjectPermission[objectPermissionList.size()];

        for (int i = 0; i < objectPermissionList.size(); i++) {
            Object objectPermission = objectPermissionList.get(i);
            ObjectPermission wsObjectPermission = (ObjectPermission) objectPermission;

            wsObjectPermissions[i] = toWSObjectPermission(wsObjectPermission);
        }

        return wsObjectPermissions;
    }

    public static void populateObjectPermission(WSObjectPermission wsObjectPermission,
        ObjectPermission objectPermission) {
        objectPermission.setPermissionMask(wsObjectPermission.getPermissionMask());
        objectPermission.setURI(wsObjectPermission.getUri());

        if (wsObjectPermission.getPermissionRecipient() instanceof WSUser) {
            objectPermission.setPermissionRecipient(
                    UserBeanTraslator.toUser((WSUser)wsObjectPermission.getPermissionRecipient()));
        } else if (wsObjectPermission.getPermissionRecipient() instanceof WSRole) {
            objectPermission.setPermissionRecipient(
                    RoleBeanTraslator.toRole((WSRole)wsObjectPermission.getPermissionRecipient()));
        }
    }

}
