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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.core.util.validators.InputValidator;
import com.jaspersoft.jasperserver.remote.common.RoleSearchCriteria;
import com.jaspersoft.jasperserver.remote.common.UserSearchCriteria;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.WeakPasswordException;
import com.jaspersoft.jasperserver.remote.services.UserAndRoleService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Volodya Sabadosh (vsabadosh@jaspersoft.com)
 * @version $Id $
 */
@Component("userAndRoleService")
public class UserAndRoleServiceImpl implements UserAndRoleService {

    @javax.annotation.Resource(name = "concreteSecurityContextProvider")
    private SecurityContextProvider securityContextProvider;

    @javax.annotation.Resource(name = "concreteTenantService")
    private TenantService tenantService;

    @javax.annotation.Resource(name = "concreteUserAuthorityService")
    protected UserAuthorityService userAuthorityService;

    @javax.annotation.Resource(name = "concreteAuditContext")
    private AuditContext auditContext;

    @javax.annotation.Resource(name = "configurationBean")
    private ConfigurationBean conf;

    @javax.annotation.Resource
    private List<Role> defaultRoles;

    @javax.annotation.Resource(name = "emailInputValidator")
    private InputValidator emailValidator;

    protected void createAuditEvent(final String auditEventType) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent(auditEventType);
            }
        });
    }

    protected void addExceptionToAuditEvent(final String auditEventType, final Exception exception) {
        auditContext.doInAuditContext(auditEventType, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent("exception", exception, auditEvent);
            }
        });
    }


    public List<User> findUsers(UserSearchCriteria criteria) throws RemoteException {
        User user;
        if (criteria == null || criteria.getTenantId()==null && criteria.getName()==null){
            throw new IllegalStateException("findUser: malformed search criteria");
        }

        if (criteria.getTenantId() != null && !doesContextUserHasAccessToTenant(criteria.getTenantId())) {
            throw new AccessDeniedException("Access is denied: the current logged user can not access the requested information");
        }

        List<User> userList = new ArrayList<User>();

        int maxRecords = criteria.getMaxRecords();
        Boolean includeSubOrgs = criteria.getIncludeSubOrgs();
        Set tenantsCriteriaSet = getTenantsCriteriaSet(criteria.getTenantId(), (includeSubOrgs == null || includeSubOrgs));

        List result = userAuthorityService.getTenantUsers(null, tenantsCriteriaSet, criteria.getName());
        result = getUsersWithRoles(result, criteria.getRequiredRoles(), criteria.getHasAllRequiredRoles());

        if (maxRecords > 0 && result.size() > maxRecords){
            result = result.subList(0, maxRecords);
        }

        return result;
    }

    public User putUser(User user) throws RemoteException {
        String auditEventType = "createUser";

        try {
            if(user == null) {
                createAuditEvent(auditEventType);
                throw new RemoteException("User is null.");
            }

            if (user.getUsername() == null){
                throw new IllegalParameterValueException("username", "null");
            }

            String nameWithoutNotSupportedSymbols =
                    user.getUsername().replaceAll(conf.getUserNameNotSupportedSymbols(), "");

            if (nameWithoutNotSupportedSymbols.length() != user.getUsername().length()) {
                createAuditEvent(auditEventType);
                throw new IllegalParameterValueException("User name contains not supported symbols");
            }

            User existedUser = getUser(user);
            if (existedUser == null) {
                auditEventType = "createUser";
            } else {
                auditEventType = "updateUser";
            }
            createAuditEvent(auditEventType);

            if (!isEmailValid(user)) {
                throw new IllegalParameterValueException("emailAddress", user.getEmailAddress());
            }

            if (!user.isExternallyDefined() && (user.getPassword() == null || user.getPassword().trim().length() == 0)) {
                throw new IllegalParameterValueException("password", "");
            }

            if (!doesContextUserHasAccessToTenant(user.getTenantId())) {
                throw new AccessDeniedException("Access is denied.");
            }

            if (existedUser == null) {
                if (defaultRoles != null) {
                    for (Role role : defaultRoles) {
                        addDefaultRoleToUser(role, user);
                    }
                }
            }

            try {
                userAuthorityService.putUser(null, user);
            } catch (InvalidDataAccessApiUsageException ida){
                throw new IllegalParameterValueException("roleSet","");
            } catch (JSException jse){
                throw new WeakPasswordException(user.getPassword());
            } catch (Exception e) {
                handleUnexpectedException(e, user.getUsername());
            }

            return user;
        }
        catch (RemoteException remoteException) {
            addExceptionToAuditEvent(auditEventType, remoteException);
            throw remoteException;
        }
    }

    public void deleteUser(User user) throws RemoteException {

        createAuditEvent("deleteUser");

        try {
            if(user == null) {
                throw new RemoteException("User is null.");
            }

            if (!doesContextUserHasAccessToTenant(user.getTenantId())) {
                throw new AccessDeniedException("Access is denied.");
            }

            deleteUser(user.getUsername(), user.getTenantId());
        } catch (RemoteException remoteException) {
            addExceptionToAuditEvent("deleteUser", remoteException);
            throw remoteException;
        }
    }

    public List<Role> findRoles(RoleSearchCriteria criteria) throws RemoteException {
        if(criteria == null) {
            throw new RemoteException("Role search criteria is null.");
        }

        if (!doesContextUserHasAccessToTenant(criteria.getTenantId()) && !isRootTenant(criteria.getTenantId())) {
            throw new AccessDeniedException("Access is denied.");
        }

        List result;

        if (criteria.getUsersNames() != null && !criteria.getUsersNames().isEmpty()){
            result = getRolesOfUsers(criteria.getUsersNames(), criteria.getHasAllUsers(), criteria.getRoleName());
        } else {
            Boolean includeSubOrgs = criteria.getIncludeSubOrgs();
            Set tenantsCriteriaSet = getTenantsCriteriaSet(criteria.getTenantId(), (includeSubOrgs == null || includeSubOrgs));

            if (criteria.getMaxRecords() > 0) {
                result = userAuthorityService.getTenantRoles(null, tenantsCriteriaSet, criteria.getRoleName(), 0, criteria.getMaxRecords());
            } else {
                result = userAuthorityService.getTenantRoles(null, tenantsCriteriaSet, criteria.getRoleName());
            }
        }

        return result;
    }

    public Role putRole(Role role) throws RemoteException {
        if (isValidRole(role)) {
            String auditEventType = "createRole";

            try {
                if(role == null) {
                    createAuditEvent(auditEventType);
                    throw new RemoteException("Role is null.");
                }

                String nameWithoutNotSupportedSymbols =
                        role.getRoleName().replaceAll(conf.getRoleNameNotSupportedSymbols(), "");

                if (nameWithoutNotSupportedSymbols.length() != role.getRoleName().length()) {
                    createAuditEvent(auditEventType);
                    throw new RemoteException("Role name contains not supported symbols");
                }

                Role existedRole = getRole(role);
                if (existedRole == null) {
                    auditEventType = "createRole";
                } else {
                    auditEventType = "updateRole";
                }
                createAuditEvent(auditEventType);

                if (!doesContextUserHasAccessToTenant(role.getTenantId())) {
                    throw new AccessDeniedException("Access is denied.");
                }

                userAuthorityService.putRole(null, role);
                Role r = getRole(role);

                if (r != null) {
                    return r;
                } else {
                    throw new RemoteException("Error while putting role : " + role.getRoleName());
                }
            } catch (RemoteException remoteException) {
                addExceptionToAuditEvent(auditEventType, remoteException);
                throw remoteException;
            }
        }
        else
            throw new IllegalParameterValueException("name", role.getRoleName());
    }

    public Role updateRoleName(Role oldRole, String newName) throws RemoteException {

        createAuditEvent("updateRole");

        try {
            if(oldRole == null) {
                throw new RemoteException("Role is null.");
            }

            if(newName == null) {
                throw new RemoteException("New name is null.");
            }

            Role aRole = oldRole;

            String nameWithoutNotSupportedSymbols = newName.replaceAll(conf.getRoleNameNotSupportedSymbols(), "");

            if (nameWithoutNotSupportedSymbols.length() != newName.length()) {
                throw new RemoteException("Role name contains not supported symbols");
            }

            if (!doesContextUserHasAccessToTenant(aRole.getTenantId())) {
                throw new AccessDeniedException("Access is denied.");
            }

            Role existedRole = getRole(aRole);
            if (existedRole == null) {
                throw new ResourceNotFoundException("Can't find role " + aRole.getRoleName());
            }

            String auditEventType = "updateRole";
            createAuditEvent(auditEventType);

            Role newRole = new RoleImpl();
            newRole.setRoleName(newName);
            newRole.setTenantId(existedRole.getTenantId());
            newRole.setExternallyDefined(existedRole.isExternallyDefined());

            Role r = updateRole(existedRole, newRole);

            if (r != null) {
                return r;
            } else {
                throw new RemoteException("Error while putting role : " + aRole.getRoleName());
            }
        } catch (RemoteException remoteException) {
            addExceptionToAuditEvent("updateRole", remoteException);
            throw remoteException;
        }
    }

    public void deleteRole(Role role) throws RemoteException {

        createAuditEvent("deleteRole");

        try {
            if(role == null) {
                throw new RemoteException("Role is null.");
            }

            if (!doesContextUserHasAccessToTenant(role.getTenantId())) {
                throw new AccessDeniedException("Access is denied.");
            }

            deleteRole(role.getRoleName(), role.getTenantId());
        } catch (RemoteException remoteException) {
            addExceptionToAuditEvent("deleteRole", remoteException);
            throw remoteException;
        }
    }

    private boolean isValidRole(Role role) {
        return  role.getRoleName()!=null && !role.getRoleName().equals("") &&
                role.getRoleName().replaceAll(conf.getRoleNameNotSupportedSymbols(), "").equals(role.getRoleName());
    }

    private boolean isRootTenant(String tenantId) throws RemoteException {
        return tenantId == null || TenantService.ORGANIZATIONS.equals(tenantId);
    }

    private boolean isEmailValid(User user) throws RemoteException {
        String email = user.getEmailAddress();
        if(email == null) {
            return true;
        }

        if(email.trim().length() > 0) {
            if(!emailValidator.isValid(user.getEmailAddress())) {
                return false;
            }
        } else  {
            if(email.trim().length() != email.length()) {
                return false;
            }
        }

        return true;
    }

    protected Role updateRole(Role oldRole, Role newRole) {
        userAuthorityService.updateRole(null, oldRole.getRoleName(), newRole);

        return getRole(newRole);
    }

    private void addDefaultRoleToUser(Role role, User user) {
        if (role == null) {
            return;
        }

        boolean isUserHasRole = false;

        for (Object o : user.getRoles()) {
            Role r = (Role) o;

            boolean isNameEquals = role.getRoleName().equals(r.getRoleName());
            boolean isTenantEquals = isTenantEquals(role.getTenantId(), r.getTenantId());

            isUserHasRole = (isNameEquals && isTenantEquals);

            if (isUserHasRole) {
                return;
            }
        }

        user.addRole(role);
    }

    protected void deleteRole(String roleName, String tenantId) {
        userAuthorityService.deleteRole(null, roleName);
    }

    private List<User> getUsersWithRoles(List users, List<Role> roles, boolean hasAllRequiredRoles) {
        if (roles == null) {
            return new ArrayList<User>(users);
        }

        List<User> userList = new ArrayList<User>();

        for(Object o : users) {
            User u = (User) o;

            if (isUserHasRoles(u, roles, hasAllRequiredRoles)) {
                userList.add(u);
            }
        }

        return userList;
    }

    private boolean isUserHasRoles(User user, List<Role> roles, boolean hasAllRequiredRoles) {
        boolean isUserHasRoles = hasAllRequiredRoles;

        for(Role r : roles) {
            boolean hasRole = false;

            for(Object o : user.getRoles()) {
                Role ur = (Role) o;

                boolean isNameEquals = ur.getRoleName().equals(r.getRoleName());
                boolean isTenantEquals = isTenantEquals(ur.getTenantId(), r.getTenantId());

                if (isNameEquals && isTenantEquals) {
                    hasRole = true;
                    break;
                }
            }

            if (hasAllRequiredRoles){
                if (!hasRole) return false;
            } else {
                if (hasRole) return true;
            }
        }

        return isUserHasRoles;
    }

    private boolean isTenantEquals(String tenantId1, String tenantId2) {
        if (tenantId1 == null) {
            return (tenantId2 == null);
        } else {
            return tenantId1.equals(tenantId2);
        }
    }

    protected Set getTenantsCriteriaSet(String tenantId, boolean includeSubOrgs) throws RemoteException {
        Set tenantIdSet = new HashSet();
        tenantIdSet.add(tenantId);

        if (includeSubOrgs){
            String id = (tenantId == null) ? TenantService.ORGANIZATIONS : tenantId;
            List allTenants = null;
            try {
                allTenants = tenantService.getAllSubTenantList(null, id);
            } catch (Exception e) {
                throw new RemoteException("Organization '" + tenantId + "' not found.");
            }

            if (allTenants != null) {
                for (Iterator it = allTenants.iterator(); it.hasNext(); ) {
                    Tenant tenant = (Tenant) it.next();

                    tenantIdSet.add(tenant.getId());
                }
            }
        }

        return tenantIdSet;
    }

    @SuppressWarnings("unchecked")
    private List<Role> getRolesOfUsers(List<String> usersNames, Boolean hasAllUsers, String roleName) {
        boolean search = roleName == null || "".equals(roleName);
        Set<Role> result = new HashSet<Role>(), tmpResult = new HashSet<Role>();

        if (!usersNames.isEmpty()){
            if (search){
                result.addAll(userAuthorityService.getAssignedRoles(null,usersNames.get(0),roleName, 0, Integer.MAX_VALUE));
            } else {
                result.addAll(userAuthorityService.getAssignedRoles(null,usersNames.get(0)));
            }
        }

        for (int i = 1; i< usersNames.size(); i++){
            if (search){
                tmpResult.addAll(userAuthorityService.getAssignedRoles(null, usersNames.get(i), roleName, 0, Integer.MAX_VALUE));
            } else {
                tmpResult.addAll(userAuthorityService.getAssignedRoles(null, usersNames.get(i)));
            }

            if (hasAllUsers){
                result.retainAll(tmpResult);
            } else {
                result.addAll(tmpResult);
            }

            tmpResult.clear();
        }

        List<Role> listResult = new ArrayList<Role>(result.size());
        listResult.addAll(result);
        return listResult;
    }

    protected Role getRole(Role role) {
        return userAuthorityService.getRole(null, role.getRoleName());
    }

    protected void deleteUser(String username, String tenantId) {
        userAuthorityService.deleteUser(null, username);
    }

    protected User getUser(User user) {
        return userAuthorityService.getUser(null, user.getUsername());
    }

    protected void handleUnexpectedException(Exception unexpectedException, String username) throws RemoteException {
        throw new RemoteException("An unexpected exception has occurred while putting user:" + username, unexpectedException);
    }

    private boolean doesContextUserHasAccessToTenant(String tenantId) throws RemoteException {
        String currentTenantId = securityContextProvider.getContextUser().getTenantId();
        currentTenantId = (currentTenantId == null) ? TenantService.ORGANIZATIONS : currentTenantId;
        tenantId = (tenantId == null) ? TenantService.ORGANIZATIONS : tenantId;

        if (tenantId.equals(currentTenantId)){
            return true;
        } else {
            Set tenants = getTenantsCriteriaSet(currentTenantId, true);
            return tenants.contains(tenantId);
        }
    }

    public void setDefaultRoles(List<Role> defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    public void setSecurityContextProvider(SecurityContextProvider securityContextProvider) {
        this.securityContextProvider = securityContextProvider;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    public UserAuthorityService getUserAuthorityService() {
        return userAuthorityService;
    }

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    public void setConf(ConfigurationBean conf) {
        this.conf = conf;
    }

    public InputValidator getEmailValidator() {
        return emailValidator;
    }

    public void setEmailValidator(InputValidator emailValidator) {
        this.emailValidator = emailValidator;
    }
}
