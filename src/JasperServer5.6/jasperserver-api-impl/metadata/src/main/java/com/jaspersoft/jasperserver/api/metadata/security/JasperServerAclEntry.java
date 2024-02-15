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

package com.jaspersoft.jasperserver.api.metadata.security;

import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.BasicAclDao;
import org.springframework.security.acl.basic.SimpleAclEntry;

import java.util.*;

/**
 * @author bob
 *
 */
public class JasperServerAclEntry extends SimpleAclEntry {
    public static final int EXECUTE = (int) Math.pow(2, 5);
    
    // Array required by the abstract superclass via getValidPermissions()
    private static final int[] VALID_PERMISSIONS = {
            NOTHING, ADMINISTRATION, READ, WRITE, CREATE, DELETE, READ_WRITE_CREATE_DELETE, READ_WRITE_CREATE,
            READ_WRITE, READ_WRITE_DELETE, EXECUTE
        };

    private static final String[] VALID_PERMISSIONS_AS_STRING = {
            "NOTHING", "ADMINISTRATION", "READ", "WRITE", "CREATE", "DELETE", "READ_WRITE_CREATE_DELETE",
            "READ_WRITE_CREATE", "READ_WRITE", "READ_WRITE_DELETE", "EXECUTE" };

    //~ Constructors ===================================================================================================

    /**
     * Allows {@link BasicAclDao} implementations to construct this object
     * using <code>newInstance()</code>.
     *
     * <P>
     * Normal classes should <B>not</B> use this default constructor.
     * </p>
     */
    public JasperServerAclEntry() {
        super();
    }

    public JasperServerAclEntry(Object recipient, AclObjectIdentity aclObjectIdentity,
        AclObjectIdentity aclObjectParentIdentity, int mask) {
        super(recipient, aclObjectIdentity, aclObjectParentIdentity, mask);
    }

    //~ Methods ========================================================================================================

    /**
     * @return a copy of the permissions array, changes to the values won't affect this class.
     */
    public int[] getValidPermissions() {
        return (int[]) VALID_PERMISSIONS.clone();
    }

    public String printPermissionsBlock(int i) {
        StringBuffer sb = new StringBuffer();

        if (isPermitted(i, ADMINISTRATION)) {
            sb.append('A');
        } else {
            sb.append('-');
        }

        if (isPermitted(i, READ)) {
            sb.append('R');
        } else {
            sb.append('-');
        }

        if (isPermitted(i, WRITE)) {
            sb.append('W');
        } else {
            sb.append('-');
        }

        if (isPermitted(i, CREATE)) {
            sb.append('C');
        } else {
            sb.append('-');
        }

        if (isPermitted(i, DELETE)) {
            sb.append('D');
        } else {
            sb.append('-');
        }

        if (isPermitted(i, EXECUTE)) {
            sb.append('X');
        } else {
            sb.append('-');
        }

        return sb.toString();
    }

    /**
     * Overlaps permissions by the OR rule. If we have for example a few permission mask:
     * permission1 = 18("-R--D-") and permission2 = 32("-----X") so the result must be - 40("-R--DX")
     *
     * @param permissionMasks set of permission mask witch will be overlapped.
     *
     * @return permission template of overlapped permission mask.
     */
    public int printPermissionsOverlappingBlock(Set<Integer> permissionMasks) {
        int overlapPermission = 0;
        LinkedHashMap<Integer, Character> mainPermissions = new LinkedHashMap<Integer, Character>();
        mainPermissions.put(ADMINISTRATION, 'A');
        mainPermissions.put(READ, 'R');
        mainPermissions.put(WRITE, 'W');
        mainPermissions.put(CREATE, 'C');
        mainPermissions.put(DELETE, 'D');
        mainPermissions.put(EXECUTE, 'X');

        for (Integer mainPermissionKey : mainPermissions.keySet()) {
            for (Integer permissionMask : permissionMasks) {
                if (isPermitted(permissionMask, mainPermissionKey)) {
                    overlapPermission += mainPermissionKey;
                    break;
                }
            }
        }
        return overlapPermission;
    }


    /**
     * Parse a permission {@link String} literal and return associated value.
     *
     * @param permission one of the field names that represent a permission: <code>ADMINISTRATION</code>,
     * <code>READ</code>, <code>WRITE</code>,...
     * @return the value associated to that permission
     * @throws IllegalArgumentException if argument is not a valid permission
     */
    public static int parsePermission(String permission) {
        for (int i = 0; i < VALID_PERMISSIONS_AS_STRING.length; i++) {
            if (VALID_PERMISSIONS_AS_STRING[i].equalsIgnoreCase(permission)) {
                return VALID_PERMISSIONS[i];
            }
        }
        throw new IllegalArgumentException("Permission provided does not exist: " + permission);
    }
}
