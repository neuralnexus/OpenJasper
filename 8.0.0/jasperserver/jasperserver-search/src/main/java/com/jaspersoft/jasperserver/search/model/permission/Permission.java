/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.search.model.permission;

import com.jaspersoft.jasperserver.search.action.ResourcePermissionsAction;

/**
 * Permission enum.
 *
 * @author Yuriy Plakosh
 */
public enum Permission {
    NO_ACCESS(0, "permissionsDialog.permission.noAccess"),
    ADMINISTER(1, "permissionsDialog.permission.administer"),
    READ_ONLY(2, "permissionsDialog.permission.read"),
    READ_WRITE(6, "permissionsDialog.permission.readWrite"),
    READ_DELETE(18, "permissionsDialog.permission.delete"),
    READ_WRITE_DELETE(30, "permissionsDialog.permission.readWriteDelete"),
//  READ+WRITE+DELETE give mask 22, READ+WRITE+DELETE+CREATE give mask 30
//  by default JRS cheking only WRITE permissions for creating resources and folders.
//  we are introducing new mask 22 which will be mapped to same label in UI
//  Bug JS-33752
    REAL_READ_WRITE_DELETE(22, "permissionsDialog.permission.readWriteDelete"),
    EXECUTE(32, "permissionsDialog.permission.execute");

    private int mask;
    private String labelId;

    Permission(int mask, String labelId) {
        this.mask = mask;
        this.labelId = labelId;
    }

    public int getMask() {
        return mask;
    }

    public String getLabelId() {
        return labelId;
    }

    public static Permission getByMask(int mask) {
        for (Permission permission : Permission.values()) {
            if (permission.getMask() == mask) {
                return permission;
            }
        }

        throw new IllegalArgumentException("Not supported permission [mask=" + mask + ']');
    }
}