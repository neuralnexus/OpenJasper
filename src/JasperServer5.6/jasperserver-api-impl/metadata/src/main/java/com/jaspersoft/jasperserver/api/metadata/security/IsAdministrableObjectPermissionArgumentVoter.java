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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.acl.AclProvider;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: IsAdministrableObjectPermissionArgumentVoter.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component
public class IsAdministrableObjectPermissionArgumentVoter extends BasicObjectPermissionArgumentVoter {
    private static final String ATTRIBUTE = "CAN_ADMINISTER";
    // spring initializes interceptors twice in case if proxied bean is injected to one of it's interceptors.
    // let's use unsecure object permission service
    @Resource(name = "objectPermissionServiceUnsecure")
    private AclProvider resourcesAclProvider;

    @Override
    protected boolean isPermitted(Authentication authentication, ObjectPermission objectPermission, Object object) {
        BasicAclEntry[] entries = (BasicAclEntry[])resourcesAclProvider.getAcls(objectPermission.getURI(), authentication);
        if (entries != null) {
            for (BasicAclEntry entry : entries) {
                if (entry.isPermitted(JasperServerAclEntry.ADMINISTRATION)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return ATTRIBUTE.equals(attribute.getAttribute());
    }
}
