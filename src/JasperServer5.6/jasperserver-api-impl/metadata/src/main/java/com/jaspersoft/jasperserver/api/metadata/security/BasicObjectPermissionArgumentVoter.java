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
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.afterinvocation.AfterInvocationProvider;
import org.springframework.security.vote.AccessDecisionVoter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: BasicObjectPermissionArgumentVoter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class BasicObjectPermissionArgumentVoter implements AccessDecisionVoter, AfterInvocationProvider {

    @Override
    public boolean supports(Class clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz)
                || ObjectPermission.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config) {
        int result = ACCESS_ABSTAIN;
        if (supports(object, config)) {
            if (isPrivileged(object)) {
                result = ACCESS_GRANTED;
            } else {
                Collection<ObjectPermission> permissions = getObjectPermissions(object);
                if (permissions != null && !permissions.isEmpty()) {
                    result = ACCESS_GRANTED;
                    for (ObjectPermission permission : permissions) {
                        if (!isPermitted(authentication, permission, object)) {
                            result = ACCESS_DENIED;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject) throws AccessDeniedException {
        if (supports(object, config)) {
            if (returnedObject instanceof Collection) {
                Collection<ObjectPermission> returned = getObjectPermissions(returnedObject), res = new LinkedList<ObjectPermission>();
                for (ObjectPermission permission : returned) {
                    if (isPermitted(authentication, permission, object)) {
                        res.add(permission);
                    }
                }
                returnedObject = res;
            } else if (returnedObject instanceof ObjectPermission && !isPermitted(authentication, (ObjectPermission) returnedObject, object)) {
                returnedObject = null;
            }
        }
        return returnedObject;
    }

    protected boolean supports(Object object, ConfigAttributeDefinition config) {
        boolean supports = false;
        if (object != null && supports(object.getClass()) && config != null && config.getConfigAttributes() != null) {
            // raw type is used by ConfigAttributeDefinition class. Cast is safe.
            @SuppressWarnings("unchecked")
            final Collection<ConfigAttribute> configAttributes = config.getConfigAttributes();
            for (ConfigAttribute configAttribute : configAttributes) {
                if (supports(configAttribute)) {
                    supports = true;
                    break;
                }
            }
        }
        return supports;
    }

    protected Collection<ObjectPermission> getObjectPermissions(Object object) {
        List<ObjectPermission> permissions = new ArrayList<ObjectPermission>();
        if (object instanceof ObjectPermission) {
            permissions.add((ObjectPermission) object);
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            // check only first element for instanceof ObjectPermission because of performance reason
            if (!collection.isEmpty() && collection.iterator().next() instanceof ObjectPermission) {
                // if the first element is ObjectPermission, then let's assume, that the rest are also ;)
                permissions.addAll(collection);
            }
        } else if (object instanceof MethodInvocation) {
            Object[] arguments = ((MethodInvocation) object).getArguments();
            if (arguments != null) {
                for (Object argument : arguments) {
                    // collect all arguments from invocation and check them all
                    permissions.addAll(getObjectPermissions(argument));
                }
            }
        }
        return permissions;
    }

    protected abstract boolean isPermitted(Authentication authentication, ObjectPermission objectPermission, Object object);

    private boolean isPrivileged(Object object){
        if (object instanceof MethodInvocation) {
            Object[] arguments = ((MethodInvocation) object).getArguments();
            if (arguments != null) {
                for (Object argument : arguments) {
                    if (argument instanceof ExecutionContext && ((ExecutionContext) argument).getAttributes().contains(ObjectPermissionService.PRIVILEGED_OPERATION)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
