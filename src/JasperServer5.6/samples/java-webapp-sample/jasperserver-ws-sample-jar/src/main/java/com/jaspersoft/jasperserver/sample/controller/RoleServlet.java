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

package com.jaspersoft.jasperserver.sample.controller;

import com.jaspersoft.jasperserver.sample.controller.vo.UserWithAssignedMarkVO;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria;
import com.jaspersoft.jasperserver.ws.authority.WSUser;
import com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria;
import com.jaspersoft.jasperserver.ws.client.authority.UserAndRoleManagement;
import com.jaspersoft.jasperserver.ws.client.controller.WSClientManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

/**
 */
public class RoleServlet extends WSServlet {

    public static final String ATTR_ROLES = "roles";
    public static final String ATTR_ROLE = "role";
    public static final String ATTR_USERS = "users";
    public static final String ATTR_SEARCH_NAME = "searchName";
    public static final String ATTR_VIEW_MODE = "viewMode";

    public static final String PARAM_ACTION = "action";
    public static final String PARAM_SEARCH_NAME = "searchName";

    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_PUT = "put";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";

    public static final String PARAM_ROLE_NAME = "roleName";
    public static final String PARAM_OLD_ROLE_NAME = "oldRoleName";
    public static final String PARAM_TENANT_ID = "tenantId";
    public static final String PARAM_ENABLED = "enabled";

    public static final String PARAM_USER_PREFIX = "user_";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter(PARAM_ACTION);

        UserAndRoleManagement userAndRoleManagement;
        try {
            userAndRoleManagement = WSClientManager.getUserAndRoleManagement(getBaseAddress(request));

            if (ACTION_EDIT.equalsIgnoreCase(action)) {
                WSRole role = getEditRole(request, userAndRoleManagement);
                List<UserWithAssignedMarkVO> usersWithAssignedMark = getEditRolesAssignedMarks(request,
                        userAndRoleManagement, role);

                if (role == null) {
                    response.sendRedirect("role");
                } else {
                    request.setAttribute(ATTR_ROLE, role);
                    request.setAttribute(ATTR_USERS, usersWithAssignedMark);
                    forward("authority/role.jsp", request, response);
                }

            } if (ACTION_PUT.equalsIgnoreCase(action)) {
                putRole(request, userAndRoleManagement);
                response.sendRedirect("role");
            } if (ACTION_DELETE.equalsIgnoreCase(action)) {
                deleteRole(request, userAndRoleManagement);
                response.sendRedirect("role");
            }
        } catch (Exception e) {
            forwardError(e, request, response);
        }
    }

    private List<UserWithAssignedMarkVO> getEditRolesAssignedMarks(HttpServletRequest request,
            UserAndRoleManagement userAndRoleManagement, WSRole role) throws RemoteException {
        String roleName = request.getParameter(PARAM_ROLE_NAME);

        List<UserWithAssignedMarkVO> usersWithAssignedMark;
        if (roleName != null && roleName.length() > 0) {
            usersWithAssignedMark = getUsersWithAssignedMark(
                    Arrays.asList(getAllUsersForRole(request, null, userAndRoleManagement)),
                    Arrays.asList(getAllUsersForRole(request, role, userAndRoleManagement)));
        } else {
            usersWithAssignedMark = getUsersWithAssignedMark(
                    Arrays.asList(getAllUsersForRole(request, null, userAndRoleManagement)), Collections.EMPTY_LIST);
        }

        return usersWithAssignedMark;
    }

    protected WSRole getEditRole(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        String roleName = request.getParameter(PARAM_ROLE_NAME);

        WSRole role;
        if (roleName != null && roleName.length() > 0) {
            role = getRole(request, roleName, userAndRoleManagement);
        } else {
            role = new WSRole();
        }

        return role;
    }

    protected void putRole(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        String roleName = request.getParameter(PARAM_ROLE_NAME);
        String oldRoleName = request.getParameter(PARAM_OLD_ROLE_NAME);

        WSRole role = new WSRole();
        role.setRoleName(roleName);

        if (oldRoleName != null && oldRoleName.length() > 0 && !oldRoleName.equals(roleName)) {
            role = getRole(request, oldRoleName, userAndRoleManagement);
            role = userAndRoleManagement.updateRoleName(role, roleName);
        }

        WSUser[] assignedUsers = getAssignedUsers(request, userAndRoleManagement);
        role.setUsers(assignedUsers);
        userAndRoleManagement.putRole(role);
    }

    protected void deleteRole(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        String roleName = request.getParameter(PARAM_ROLE_NAME);

        WSRole role = new WSRole();
        role.setRoleName(roleName);

        userAndRoleManagement.deleteRole(role);
    }

    protected WSUser[] getAssignedUsers(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        List<WSUser> users = new ArrayList<WSUser>();
        for (Map.Entry<String, String[]> entry : (Set<Map.Entry<String, String[]>>)request.getParameterMap().entrySet()) {
            String key = entry.getKey();

            if (key.startsWith(PARAM_USER_PREFIX)) {
                String name = key.substring(PARAM_USER_PREFIX.length());

                WSUser[] wsUsers = userAndRoleManagement.findUsers(new WSUserSearchCriteria(name, null, true, null, 0));
                
                if (wsUsers == null || wsUsers.length == 0) {
                    throw new RuntimeException("Problems to find user with username '" + name + "'");
                } else {
                    boolean foundUser = false;
                    for (WSUser wsUser : wsUsers) {
                        if (wsUser.getUsername().equals(name)) {
                            users.add(wsUser);
                            foundUser = true;
                        }
                    }
                    if (!foundUser) {
                        throw new RuntimeException("Problems to find user with username '" + name + "'");
                    }
                }
            }
        }

        return users.toArray(new WSUser[users.size()]);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchName = request.getParameter(PARAM_SEARCH_NAME);
        searchName = (searchName != null) ? searchName : "";

        UserAndRoleManagement userAndRoleManagement;
        try {
            userAndRoleManagement = WSClientManager.getUserAndRoleManagement(getBaseAddress(request));

            WSRoleSearchCriteria searchCriteria = createSearchCriteria(request, searchName);

            WSRole[] roles = userAndRoleManagement.findRoles(searchCriteria);

            request.setAttribute(ATTR_SEARCH_NAME, searchName);
            request.setAttribute(ATTR_ROLES, roles);
            request.setAttribute(ATTR_VIEW_MODE, "role");

            forward("authority/list.jsp", request, response);
        } catch (ServiceException e) {
            forwardError(e, request, response);
        }
    }

    protected WSRoleSearchCriteria createSearchCriteria(HttpServletRequest request, String searchName)
            throws RemoteException, ServiceException {
        WSRoleSearchCriteria searchCriteria = new WSRoleSearchCriteria();
        searchCriteria.setRoleName(searchName);
        searchCriteria.setTenantId(null);
        searchCriteria.setMaxRecords(0);
        searchCriteria.setIncludeSubOrgs(Boolean.TRUE);
        return searchCriteria;
    }

    protected WSRole getRole(HttpServletRequest request, String roleName, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        WSRoleSearchCriteria searchCriteria = new WSRoleSearchCriteria();
        searchCriteria.setRoleName(roleName);
        searchCriteria.setTenantId(null);
        searchCriteria.setMaxRecords(0);
        searchCriteria.setIncludeSubOrgs(Boolean.TRUE);

        WSRole[] roles = userAndRoleManagement.findRoles(searchCriteria);

        for (WSRole role : roles) {
            if (roleName.equals(role.getRoleName())) {
                return role;
            }
        }

        return null;
    }

    protected WSUser[] getAllUsersForRole(HttpServletRequest request, WSRole role,
            UserAndRoleManagement userAndRoleManagement) throws RemoteException {
        WSUserSearchCriteria searchCriteria = new WSUserSearchCriteria("", null, true,
                (role == null) ? null : new WSRole[] {role}, 0);

        return userAndRoleManagement.findUsers(searchCriteria);
    }

    protected List<UserWithAssignedMarkVO> getUsersWithAssignedMark(Collection<WSUser> allUsers,
            Collection<WSUser> assignedUsers) {
        List<UserWithAssignedMarkVO> usersWithAssignedMark = new ArrayList<UserWithAssignedMarkVO>(allUsers.size());

        for (WSUser user : allUsers) {
            UserWithAssignedMarkVO userWithAssignedMarkVO = new UserWithAssignedMarkVO();
            userWithAssignedMarkVO.setUser(user);
            userWithAssignedMarkVO.setAssigned(assignedUsers.contains(user));

            usersWithAssignedMark.add(userWithAssignedMarkVO);
        }

        return usersWithAssignedMark;
    }
}