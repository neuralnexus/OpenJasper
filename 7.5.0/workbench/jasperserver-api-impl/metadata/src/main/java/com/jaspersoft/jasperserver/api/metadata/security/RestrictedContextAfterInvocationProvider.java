/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.metadata.security;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceVisitor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.acls.afterinvocation.AbstractAclProvider;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.isRestrictedRuntimeExecutionContext;
import static com.jaspersoft.jasperserver.api.metadata.security.SecuredMethodInvocationHelper.getContextAsFirstArgumentOfSecureObject;

/**
 * This after invocation provider handles security decision in restricted runtime execution.
 *
 * @author Volodya Sabadosh
 */
public class RestrictedContextAfterInvocationProvider extends AbstractAclProvider {
    private final List<Permission> ignoreRestrictedContextForPermission;
    private final Set<Class<? extends Resource>> inAccessibleResourceTypes;
    private final Set<Class<? extends Resource>> skipResourceTypes;

    RestrictedContextAfterInvocationProvider(AclService aclService, String processConfigAttribute,
            List<Permission> requirePermission, List<Permission> ignoreRestrictedContextForPermission,
                Set<Class<? extends Resource>> inAccessibleResourceTypes, Set<Class<? extends Resource>> skipResourceTypes) {
        super(aclService, processConfigAttribute, requirePermission);
        this.ignoreRestrictedContextForPermission = ignoreRestrictedContextForPermission;
        this.inAccessibleResourceTypes = inAccessibleResourceTypes;
        this.skipResourceTypes = skipResourceTypes;
    }

    /**
     * Decides accessibility of particular secure object in restricted execution context. Only Deny access if
     * authenticated principal has only minimal permission for secured object and this object is included in restricted list.
     * Also, if authenticated principal has only minimal permission, then hide all dependent local resources, that in
     * restricted list.
     *
     * @param authentication authentication
     * @param secureObject secure object
     * @param attributes attributes
     * @param returnedObject return object
     *
     * @return same or modified object if access granted.
     *
     * @throws AccessDeniedException if authenticated principal has only minimal permission for secured object,
     * that is included in restricted list.
     */
    @Override
    public Object decide(Authentication authentication, Object secureObject, Collection<ConfigAttribute> attributes,
                         Object returnedObject) throws AccessDeniedException {
        if (returnedObject == null) {
            return null;
        }

        boolean isSupportAttribute = attributes != null && attributes.stream().anyMatch(this::supports);
        if (isSupportAttribute) {
            ExecutionContext context = getContextAsFirstArgumentOfSecureObject(secureObject);

            boolean isInaccessibleResource = isInaccessibleResource(returnedObject);
            boolean isRestrictedContextApplied = isRestrictedContextApplied(context, authentication, returnedObject);

            if (isInaccessibleResource && isRestrictedContextApplied) {
                throw new AccessDeniedException("Access is denied");
            }

            if (isRestrictedContextApplied) {
                ((Resource)returnedObject).accept(new HideRestrictedLocalResources());
            }
        }
        return returnedObject;
    }

    private boolean isRestrictedContextApplied(ExecutionContext context, Authentication authentication, Object securedObject) {
        return isRestrictedRuntimeExecutionContext(context) && isOnlyMinimalPermissionsGranted(authentication, securedObject);
    }

    private boolean isInaccessibleResource(Object securedObject) {
        return checkIfTypeIsDerivedFrom(inAccessibleResourceTypes,
                securedObject.getClass()) && !checkIfTypeIsDerivedFrom(skipResourceTypes, securedObject.getClass());
    }

    private boolean checkIfTypeIsDerivedFrom(Set<Class<? extends Resource>> types, Class type) {
        return types.stream().anyMatch(a -> a.isAssignableFrom(type));
    }

    private boolean isOnlyMinimalPermissionsGranted(Authentication authentication, Object domainObject) {
        // Obtain the OID applicable to the domain object
        ObjectIdentity objectIdentity = objectIdentityRetrievalStrategy
                .getObjectIdentity(domainObject);

        // Obtain the SIDs applicable to the principal
        List<Sid> sids = sidRetrievalStrategy.getSids(authentication);

        try {
            // Lookup only ACLs for SIDs we're interested in
            Acl acl = aclService.readAclById(objectIdentity, sids);

            return acl.isGranted(requirePermission, sids, false) &&
                    !acl.isGranted(ignoreRestrictedContextForPermission, sids, false);
        }
        catch (NotFoundException ignore) {
            return false;
        }
    }

    class HideRestrictedLocalResources implements ResourceVisitor {
        @Override
        public void visit(Resource resource) {
            //Empty
        }

        @Override
        public void visit(ResourceReference resourceReference) {
            if (resourceReference.isLocal()) {
                Resource localResource = resourceReference.getLocalResource();
                if (isInaccessibleResource(localResource)) {
                    //Hide Local resource. Changed it from local to external. So, when this resource will be
                    //fetched directly via RepositoryService, then Access Denied will be thrown.
                    resourceReference.setReference(localResource.getURIString());
                }
            }
        }
    }
}
