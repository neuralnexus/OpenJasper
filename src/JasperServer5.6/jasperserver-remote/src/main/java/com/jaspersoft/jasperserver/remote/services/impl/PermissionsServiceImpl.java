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
package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclEntry;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.AclService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.ObjectPermissionServiceImpl;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ModificationNotAllowedException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.helpers.PermissionsRecipientIdentity;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.remote.services.ResourcesManagementRemoteService;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import com.jaspersoft.jasperserver.search.model.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.acl.AclProvider;
import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Volodya Sabadosh (vsabadosh@jaspersoft.com)
 * @author Zakhar Tomchenco (ztomchenco@jaspersoft.com)
 * @version $Id: PermissionsServiceImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component("permissionsService")
@Transactional(rollbackFor = Exception.class)
public class PermissionsServiceImpl implements PermissionsService {
    protected static final Set<Integer> ALLOWED_MASKS;

    static {
        Set<Integer> set = new HashSet<Integer>();
        for (Permission perm : Permission.values()){
            set.add(perm.getMask());
        }
        ALLOWED_MASKS = Collections.unmodifiableSet(set);
    }

    @javax.annotation.Resource(name = "objectPermissionService")
    protected ObjectPermissionService objectPermissionService;
    @javax.annotation.Resource(name = "objectPermissionService")
    protected AclProvider aclService;
    @javax.annotation.Resource(name = "objectPermissionService")
    protected AclService ourAclService;
    @javax.annotation.Resource(name = "objectPermissionService")
    protected PersistentObjectResolver persistentObjectResolver;
    @javax.annotation.Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;
    @javax.annotation.Resource
    private AuditHelper auditHelper;
    @javax.annotation.Resource
    private ResourcesManagementRemoteService resourcesManagementRemoteService;
    @javax.annotation.Resource(name = "mappingResourceFactory")
    private ResourceFactory resourceFactory;
    @javax.annotation.Resource(name = "concreteUserAuthorityService")
    protected UserAuthorityService userAuthorityService;

    protected Comparator<BasicAclEntry> aclCompartor = new Comparator<BasicAclEntry>() {
        @Override
        public int compare(BasicAclEntry o1, BasicAclEntry o2) {
            // 1 is the highest mask value
            int mask1 = o1.getMask() == JasperServerAclEntry.ADMINISTRATION ? Integer.MAX_VALUE : o1.getMask();
            int mask2 = o2.getMask() == JasperServerAclEntry.ADMINISTRATION ? Integer.MAX_VALUE : o2.getMask();
            // 32 is a lowest except 0
            mask1 = mask1 == JasperServerAclEntry.EXECUTE ? 1 : mask1;
            mask2 = mask2 == JasperServerAclEntry.EXECUTE ? 1 : mask2;

            int result = mask1 - mask2;
            if (result == 0){
                //if masks are equal, closest(longer identity's uri) is bigger
                result = ((ObjectPermissionServiceImpl.URIObjectIdentity) o1.getAclObjectIdentity()).getURI().length() -
                        ((ObjectPermissionServiceImpl.URIObjectIdentity) o2.getAclObjectIdentity()).getURI().length();
            }
            return result;
        }
    };

    public void setResourcesManagementRemoteService(ResourcesManagementRemoteService resourcesManagementRemoteService) {
        this.resourcesManagementRemoteService = resourcesManagementRemoteService;
    }

    public void setAuditHelper(AuditHelper auditHelper) {
        this.auditHelper = auditHelper;
    }

    public List<ObjectPermission> getPermissions(String resourceURI) throws RemoteException {
        Resource resource = resourcesManagementRemoteService.locateResource(resourceURI);
        return objectPermissionService.getObjectPermissionsForObject(makeExecutionContext(), resource);
    }

    public List<ObjectPermission> getPermissions(String resourceURI, Class<?> recipientType, String recipientId, boolean effectivePermissions, boolean resolveAll) throws RemoteException {
        List<ObjectPermission> result;
        if (resolveAll){
            result = resolveAll(resourceURI, recipientType, recipientId, effectivePermissions);
        } else {
            Resource resource = resolveResource(resourceURI);
            if (effectivePermissions){
                result = objectPermissionService.getEffectivePermissionsForObject(makeExecutionContext(), resource);
            } else {
                result = objectPermissionService.getObjectPermissionsForObject(makeExecutionContext(), resource);
            }
            if (recipientType != null){
                result = filterByType(result, recipientType);
                if (recipientId != null){
                    //just to make sure, that recipient exists
                    resolveRecipientObject(recipientType, recipientId);
                    result = filterById(result, recipientId);
                }
            }
        }
        return result;
    }

    public ObjectPermission getPermission(String resourceURI, Class<?> recipientType, String recipientId) throws RemoteException {
        Object recipient = resolveRecipientObject(recipientType, recipientId);
        Resource object = resolveResource(resourceURI);
        List<ObjectPermission> permissions = objectPermissionService.getObjectPermissionsForObjectAndRecipient(makeExecutionContext(), object, recipient);
        return permissions.size() > 0 ? (ObjectPermission) permissions.get(0) : null;
    }

    public ObjectPermission getEffectivePermission(Resource resource, Role role) {
        Authentication authentication = createAuthentication(role);
        ObjectPermission permission = getEffectivePermission(resource, authentication);
        permission.setPermissionRecipient(role);
        return permission;
    }

    public ObjectPermission getEffectivePermission(Resource resource, User user) {
        Authentication authentication = createAuthentication(user);
        return getEffectivePermission(resource, authentication);
    }

    public ObjectPermission getEffectivePermission(Resource resource, Authentication authentication) {
        ObjectPermission permission = new ObjectPermissionImpl();
        permission.setPermissionRecipient(authentication.getPrincipal());
        BasicAclEntry[] effectiveAcls = (BasicAclEntry[]) aclService.getAcls(resource, authentication);

        if (effectiveAcls != null) {
            BasicAclEntry entry = Collections.max(Arrays.asList(effectiveAcls), aclCompartor);

            permission.setPermissionMask(entry.getMask());
            permission.setURI(extractUriFromEntry(entry));
        } else {
            permission.setPermissionMask(JasperServerAclEntry.NOTHING);
        }
        return permission;
    }

    public List<ObjectPermission> getPermissionsForObject(String targetURI) throws RemoteException {
        if (!objectPermissionService.isObjectAdministrable(makeExecutionContext(), targetURI)) {
            throw new AccessDeniedException("Access is denied");
        }
        Resource res = repositoryService.getResource(makeExecutionContext(), targetURI);
        if (res == null) {
            res = repositoryService.getFolder(makeExecutionContext(), targetURI);

            if (res == null) {
                throw new RemoteException("There is no resource or folder for target URI \"" + targetURI + "\"");
            }
        }

        return objectPermissionService.getObjectPermissionsForObject(makeExecutionContext(), res);
    }

    public ObjectPermission createPermission(ObjectPermission objectPermission) throws RemoteException {
        return doPutPermission(objectPermission, false);
    }

    public ObjectPermission putPermission(ObjectPermission objectPermission) throws RemoteException {
        return doPutPermission(objectPermission, true);
    }

    public List<ObjectPermission> putPermissions(String uri, List<ObjectPermission> objectPermissions) throws RemoteException {
        // make sure that permissions will be definitely assigned to this resource
        for (ObjectPermission permission : objectPermissions){
            permission.setURI(REPO_URI_PREFIX + uri);
        }
        return doPutPermissions(REPO_URI_PREFIX + uri, objectPermissions, false);
    }

    public List<ObjectPermission> createPermissions(List<ObjectPermission> objectPermissions) throws RemoteException {
        return doPutPermissions(null, objectPermissions, true);
    }

    public void deletePermission(ObjectPermission objectPermission) throws RemoteException {
        changePermissionConsistencyCheck(objectPermission);

        auditHelper.createAuditEvent("deletePermission");
        objectPermissionService.deleteObjectPermission(makeExecutionContext(), objectPermission);
        auditHelper.closeAuditEvent("deletePermission");
    }

    public ObjectPermission newObjectPermission() {
        return objectPermissionService.newObjectPermission(makeExecutionContext());
    }


    /**
     * Returns permission mask for resource with <strong>targetURI</strong></> for current user.
     *
     * @param targetURI resource URI.
     * @return
     * @throws RemoteException
     */
    public int getAppliedPermissionMaskForObjectAndCurrentUser(String targetURI) throws RemoteException {
        Resource resource = repositoryService.getResource(makeExecutionContext(), targetURI);
        if (resource == null) {
            resource = repositoryService.getFolder(makeExecutionContext(), targetURI);

            if (resource == null) {
                throw new RemoteException("There is no resource or folder for target URI \"" + targetURI + "\"");
            }
        }

        Set<Integer> allUserPermissions = null;

        List<Object> currentUserRecipients = getCurrentUserRecipients();
        if (currentUserRecipients != null && currentUserRecipients.size() > 0) {
            allUserPermissions = new HashSet<Integer>();
            for (Object recipient : getCurrentUserRecipients()) {
            	BasicAclEntry[] acls = ourAclService.getAcls(resource, recipient);
            	for (BasicAclEntry acl : acls) {
            		allUserPermissions.add(acl.getMask());
            	}
            }
        }
        if (allUserPermissions != null && allUserPermissions.size() > 0) {
            JasperServerAclEntry jasperServerAclEntry = new JasperServerAclEntry();
            // returns permissions mask
            return jasperServerAclEntry.printPermissionsOverlappingBlock(allUserPermissions);
        } else {
            // returns no permissions.
            return 0;
        }
    }

    protected List<Object> getCurrentUserRecipients() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        List<Object> recipients = new ArrayList<Object>();
        if (authenticationToken.getPrincipal() instanceof User) {
            User user = (User) authenticationToken.getPrincipal();
            recipients.add(user);
            recipients.addAll(user.getRoles());

            return recipients;
        } else {
            return null;
        }
    }

    protected void changePermissionConsistencyCheck(ObjectPermission objectPermission) throws RemoteException {
        if (StringUtils.isBlank(objectPermission.getURI())) {
            throw new IllegalParameterValueException("URI is blank", "uri", objectPermission.getURI());
        }
        // Permission recipient check
        if (objectPermission.getPermissionRecipient() == null) {
            throw new IllegalParameterValueException("recepient", "null");
        }
        if (!ALLOWED_MASKS.contains(objectPermission.getPermissionMask())){
            throw new IllegalParameterValueException("mask",new Integer(objectPermission.getPermissionMask()).toString());
        }
        if (objectPermission.getPermissionRecipient() instanceof PermissionsRecipientIdentity){
            PermissionsRecipientIdentity identity = (PermissionsRecipientIdentity)objectPermission.getPermissionRecipient();
            objectPermission.setPermissionRecipient(resolveRecipientObject(identity.getRecipientClass(), identity.getId()));
        } else if (objectPermission.getPermissionRecipient() instanceof TenantQualified) {
            if (persistentObjectResolver.getPersistentObject(objectPermission.getPermissionRecipient()) == null) {
                if (objectPermission.getPermissionRecipient() instanceof User) {
                    User user = (User) objectPermission.getPermissionRecipient();
                    throw new ResourceNotFoundException(user.getUsername());
                }
                if (objectPermission.getPermissionRecipient() instanceof Role) {
                    Role role = (Role) objectPermission.getPermissionRecipient();
                    throw new ResourceNotFoundException(role.getRoleName());
                }
            }
        } else {
            throw new IllegalStateException("Unknown recipient class:"+objectPermission.getPermissionRecipient().getClass().getName());
        }

        // make sure, that resource exists
        resolveResource(objectPermission.getURI());
    }

    public void setObjectPermissionService(ObjectPermissionService objectPermissionService) {
        this.objectPermissionService = objectPermissionService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    protected ExecutionContext makeExecutionContext() {
        return ExecutionContextImpl.getRuntimeExecutionContext();
    }

    protected ObjectPermission doPutPermission(ObjectPermission objectPermission, boolean allowUpdate) throws RemoteException {
        changePermissionConsistencyCheck(objectPermission);

        ObjectPermission existingObjectPermission =
                objectPermissionService.getObjectPermission(makeExecutionContext(), objectPermission);
        String auditEventType;

        if (existingObjectPermission == null) {
            auditEventType = "createPermission";
        } else {
            if (allowUpdate){
                auditEventType = "updatePermission";
            } else {
                throw new ResourceAlreadyExistsException(objectPermission.getURI()+";"+((InternalURI)objectPermission.getPermissionRecipient()).getURI());
            }
        }

        auditHelper.createAuditEvent(auditEventType);
        objectPermissionService.putObjectPermission(makeExecutionContext(), objectPermission);
        auditHelper.closeAuditEvent(auditEventType);

        return objectPermissionService.getObjectPermission(makeExecutionContext(), objectPermission);
    }

    protected List<ObjectPermission> doPutPermissions(String uri, List<ObjectPermission> objectPermissions, boolean addTo) throws RemoteException {
        if (!addTo) {
            List<ObjectPermission> existing = this.getPermissions(uri, null, null, false, false);
            for (ObjectPermission permission : existing) {
                this.deletePermission(permission);
            }
        }

        List<ObjectPermission> result = new LinkedList<ObjectPermission>();
        for (ObjectPermission permission : objectPermissions) {
            result.add(this.createPermission(permission));
        }
        return result;
    }

    protected String extractUriFromEntry(BasicAclEntry entry) {
        return ((ObjectPermissionServiceImpl.URIObjectIdentity) entry.getAclObjectIdentity()).getURI();
    }

    protected Object resolveRecipientObject(Class<?> clazz, String id) throws ResourceNotFoundException{
        Object res = null;
        if (Role.class.equals(clazz)){
            Role role = new RoleImpl();
            role.setRoleName(id);
            res = role;
        }
        if (User.class.equals(clazz)){
            User user = new UserImpl();
            user.setUsername(id);
            res = user;
        }

        if (persistentObjectResolver.getPersistentObject(res) == null){
            throw new ResourceNotFoundException(clazz.getSimpleName()+" "+ id);
        }

        return res;
    }

    protected Resource resolveResource(String uri) throws RemoteException {
        if (uri.startsWith(REPO_URI_PREFIX)){
            uri = uri.substring(REPO_URI_PREFIX.length());
        }

        Resource resource = repositoryService.getResource(makeExecutionContext(), uri);
        if (resource == null) {
            resource = repositoryService.getFolder(makeExecutionContext(), uri);
        }
        if (resource == null){
            throw new ResourceNotFoundException(uri);
        }

        if (!objectPermissionService.isObjectAdministrable(null, resource)){
            throw new AccessDeniedException("Access denied", uri);
        }

        return resource;
    }

    protected boolean isSameId(Object permissionRecipient, String recipientId) {
        return permissionRecipient instanceof User && ((User) permissionRecipient).getUsername().equals(recipientId) ||
                permissionRecipient instanceof Role && ((Role) permissionRecipient).getRoleName().equals(recipientId);
    }

    protected List<ObjectPermission> filterByType(List<ObjectPermission> data, Class<?> recipientType) {
        List<ObjectPermission> result = new LinkedList<ObjectPermission>();
        for (ObjectPermission permission : data){
            if (recipientType.isInstance(permission.getPermissionRecipient())){
                result.add(permission);
            }
        }
        return result;
    }

    protected List<ObjectPermission> filterById(List<ObjectPermission> data, String recipientId) {
        List<ObjectPermission> result = new LinkedList<ObjectPermission>();
        for (ObjectPermission permission : data){
            if (isSameId(permission.getPermissionRecipient(), recipientId)){
                result.add(permission);
            }
        }
        return result;
    }

    protected Authentication createAuthentication(Role role) {
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[]{new GrantedAuthorityImpl(role.getRoleName())};
        MetadataUserDetails dummy = new MetadataUserDetails(new UserImpl());
        dummy.setUsername("dummyUserdummyUserdummyUserdummyUserdummyUser");

        return new UsernamePasswordAuthenticationToken(dummy, null, grantedAuthorities);
    }

    protected Authentication createAuthentication(User user) {
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[user.getRoles().size()];
        Role[] roles = (Role[]) user.getRoles().toArray(new Role[user.getRoles().size()]);

        for (int i = 0; i < roles.length; i++) {
            grantedAuthorities[i] = new GrantedAuthorityImpl(roles[i].getRoleName());
        }

        return new UsernamePasswordAuthenticationToken(new MetadataUserDetails(user), null, grantedAuthorities);
    }

    private List<ObjectPermission> resolveAll(String resourceURI, Class<?> recipientType, String recipientId, boolean effectivePermissions) throws RemoteException {
        List<ObjectPermission> res;
        if (recipientType == null){
            res = resolveAllUsers(resourceURI,recipientId, effectivePermissions);
            res.addAll(resolveAllRoles(resourceURI, recipientId));
        } else {
            if (recipientType.equals(User.class)){
                res = resolveAllUsers(resourceURI,recipientId, effectivePermissions);
            } else {
                res = resolveAllRoles(resourceURI, recipientId);
            }
        }
        return res;
    }

    private List<ObjectPermission> resolveAllRoles(String resourceURI, String recipientId) throws RemoteException {
        List<Role> roles = getRolesForResource(resourceURI, recipientId);
        Resource resource = resolveResource(resourceURI);

        List<ObjectPermission> res = new LinkedList<ObjectPermission>();
        for (Role role : roles) {
            res.add(getEffectivePermission(resource, role));
        }

        return res;
    }

    private List<ObjectPermission> resolveAllUsers(String resourceURI, String recipientId, boolean effectivePermissions) throws RemoteException {
        List<User> users = getUsersForResource(resourceURI, recipientId);
        Resource resource = resolveResource(resourceURI);

        List<ObjectPermission> res = new LinkedList<ObjectPermission>();
        ObjectPermission permission;
        for (User user : users) {
            if (effectivePermissions) {
                permission = getEffectivePermission(resource, user);
            } else {
                //create authentication without roles, based on user, so result will be for user only
                Authentication authentication = new UsernamePasswordAuthenticationToken(user,null,new GrantedAuthority[0]);
                permission = getEffectivePermission(resource, authentication);
            }
            res.add(permission);
        }
        return res;
    }

    protected List<User> getUsersForResource(String resourceURI, String recipientId) {
        return userAuthorityService.getTenantUsers(null, null, null);
    }

    protected List getRolesForResource(String resourceURI, String recipientId) {
        return userAuthorityService.getTenantRoles(null, null, null);
    }
}
