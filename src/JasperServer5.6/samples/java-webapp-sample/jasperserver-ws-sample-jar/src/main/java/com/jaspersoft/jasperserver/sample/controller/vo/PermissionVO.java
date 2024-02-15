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

package com.jaspersoft.jasperserver.sample.controller.vo;

import com.jaspersoft.jasperserver.ws.authority.WSObjectPermission;

/**
 * @author Yuriy Plakosh
 */
public class PermissionVO {
    private WSObjectPermission permission;

    private int permissionToDisplay;

    public WSObjectPermission getPermission() {
        return permission;
    }

    public void setPermission(WSObjectPermission permission) {
        this.permission = permission;
    }

    public int getPermissionToDisplay() {
        return permissionToDisplay;
    }

    public void setPermissionToDisplay(int permissionToDisplay) {
        this.permissionToDisplay = permissionToDisplay;
    }

    public int getPermissionMask() {
        return permissionToDisplay & 0xff;
    }

    public int getInheritedPermissionMask() {
        return ((permissionToDisplay & 0x100) >> 8) % 2;
    }

    public int getRemoveInheritedPermissionMask() {
        return ((permissionToDisplay & 0xfe00) >> 9);
    }
}
