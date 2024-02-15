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
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: NotRecipientSuperuserObjectPermissionVoter.java 31060 2013-04-12 10:38:31Z ztomchenco $
 */
@Service
public class NotRecipientAdminObjectPermissionVoter extends BasicObjectPermissionArgumentVoter {
    private static final String ATTRIBUTE = "NOT_RECIPIENT_ADM";
    @Resource
    protected String roleAdministrator;
    @Override
    protected boolean isPermitted(Authentication authentication, ObjectPermission objectPermission, Object object) {
        return objectPermission.getPermissionRecipient() != null && (!(objectPermission.getPermissionRecipient() instanceof Role)
                || (objectPermission.getPermissionRecipient() instanceof Role
                && !roleAdministrator.equals(((Role)objectPermission.getPermissionRecipient()).getRoleName())));
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return ATTRIBUTE.equals(attribute.getAttribute());
    }
}
