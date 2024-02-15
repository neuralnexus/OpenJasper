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

/**
 * UserAndRoleManagementServiceImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.axis2.authority;

import com.jaspersoft.jasperserver.remote.common.*;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.UserAndRoleService;
import com.jaspersoft.jasperserver.ws.authority.WSUser;
import com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.ws.axis2.util.RemoteServiceFromWsCallTemplate;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

@CallTemplate(RemoteServiceFromWsCallTemplate.class)
public class UserAndRoleManagementServiceImpl extends RemoteServiceWrapperWithCheckedException<UserAndRoleService,
        AxisFault> implements UserAndRoleManagementService {

    public WSUser[] findUsers(final WSUserSearchCriteria criteria) throws AxisFault {

        List<User> users = callRemoteService(new ConcreteCaller<List<User>>() {
            public List<User> call(UserAndRoleService userAndRoleService) throws RemoteException {
                return userAndRoleService.findUsers(toUserSearchCriteria(criteria));            }
        });
        return UserBeanTraslator.toWSUserArray(users);

    }

    public WSUser putUser(final WSUser wsUser) throws AxisFault {
        User aUser = callRemoteService(new ConcreteCaller<User>() {
            public User call(UserAndRoleService userAndRoleService) throws RemoteException {
                return userAndRoleService.putUser(UserBeanTraslator.toUser(wsUser));
            }
        });
        return UserBeanTraslator.toWSUser(aUser);
    }

    public void deleteUser(final WSUser wsUser) throws AxisFault {
        callRemoteService(new ConcreteCaller<Object>() {
            public Object call(UserAndRoleService userAndRoleService) throws RemoteException {
                userAndRoleService.deleteUser(UserBeanTraslator.toUser(wsUser));
                return null;
            }
        });
    }

    public WSRole[] findRoles(final WSRoleSearchCriteria criteria) throws AxisFault {
        List<Role> roles = callRemoteService(new ConcreteCaller<List<Role>>() {
            public List<Role> call(UserAndRoleService userAndRoleService) throws RemoteException {
                return userAndRoleService.findRoles(toRoleSearchCriteria(criteria));            }
        });
        return RoleBeanTraslator.toWSRoleArray(roles);
    }

    public WSRole putRole(final WSRole wsRole) throws AxisFault {
        Role role = callRemoteService(new ConcreteCaller<Role>() {
            public Role call(UserAndRoleService userAndRoleService) throws RemoteException {
                return userAndRoleService.putRole(RoleBeanTraslator.toRole(wsRole));
            }
        });
        return RoleBeanTraslator.toWSRole(role);
    }

    public WSRole updateRoleName(final WSRole oldWSRole, final String newName) throws AxisFault {
        Role role = callRemoteService(new ConcreteCaller<Role>() {
            public Role call(UserAndRoleService userAndRoleService) throws RemoteException {
                return userAndRoleService.updateRoleName(RoleBeanTraslator.toRole(oldWSRole), newName);
            }
        });
        return RoleBeanTraslator.toWSRole(role);
    }

    public void deleteRole(final WSRole wsRole) throws AxisFault {
        callRemoteService(new ConcreteCaller<Object>() {
            public Object call(UserAndRoleService userAndRoleService) throws RemoteException {
                userAndRoleService.deleteRole(RoleBeanTraslator.toRole(wsRole));
                return null;
            }
        });
    }

    private RoleSearchCriteria toRoleSearchCriteria(WSRoleSearchCriteria wsRoleSearchCriteria) {
        if (wsRoleSearchCriteria == null) {
            return null;
        }
        RoleSearchCriteria roleSearchCriteria = new RoleSearchCriteria();
        roleSearchCriteria.setIncludeSubOrgs(wsRoleSearchCriteria.getIncludeSubOrgs());
        roleSearchCriteria.setMaxRecords(wsRoleSearchCriteria.getMaxRecords());
        roleSearchCriteria.setRoleName(wsRoleSearchCriteria.getRoleName());
        roleSearchCriteria.setTenantId(wsRoleSearchCriteria.getTenantId());
        return roleSearchCriteria;
    }

    private UserSearchCriteria toUserSearchCriteria(WSUserSearchCriteria wsUserSearchCriteria) {
        if (wsUserSearchCriteria == null) {
            return null;
        }
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        userSearchCriteria.setIncludeSubOrgs(wsUserSearchCriteria.getIncludeSubOrgs());
        userSearchCriteria.setMaxRecords(wsUserSearchCriteria.getMaxRecords());
        userSearchCriteria.setName(wsUserSearchCriteria.getName());
        userSearchCriteria.setRequiredRoles(RoleBeanTraslator.toRoleList(wsUserSearchCriteria.getRequiredRoles()));
        userSearchCriteria.setTenantId(wsUserSearchCriteria.getTenantId());

        return userSearchCriteria;
    }

    public void setUserAndRoleService(UserAndRoleService userAndRoleService) {
        this.remoteService = userAndRoleService;
    }

}
