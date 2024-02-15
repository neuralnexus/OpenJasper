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

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.GrantedAuthority;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: IsRecipientOwnPermissionArgumentVoter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class IsRecipientOwnPermissionArgumentVoter extends BasicObjectPermissionArgumentVoter {
    private static final String ATTRIBUTE = "PERMISSION_RECIPIENT";

    @Override
    protected boolean isPermitted(Authentication authentication, ObjectPermission objectPermission, Object object) {
        boolean res = false;
        if (objectPermission.getPermissionRecipient() instanceof User){
            User recipient = (User)objectPermission.getPermissionRecipient();
            User user = (User)authentication.getPrincipal();
            res = (recipient.getUsername() == user.getUsername() || (recipient.getUsername() != null && recipient.getUsername().equals(user.getUsername()))) &&
                  (recipient.getTenantId() == user.getTenantId() || (recipient.getTenantId() != null && recipient.getTenantId().equals(user.getTenantId())));
        } else
        if (objectPermission.getPermissionRecipient() instanceof Role){
            Role recipient = (Role)objectPermission.getPermissionRecipient();
            GrantedAuthority[] authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities){
                String name = authority.getAuthority();
                String tenantId = authority instanceof TenantQualified ? ((TenantQualified)authority).getTenantId() : null;

                if (name.equals(recipient.getRoleName()) && (tenantId == recipient.getTenantId() || (tenantId != null && tenantId.equals(recipient.getTenantId())))){
                    res = true;
                    break;
                }
            }
        }

        return res;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return ATTRIBUTE.equals(attribute.getAttribute());
    }
}
