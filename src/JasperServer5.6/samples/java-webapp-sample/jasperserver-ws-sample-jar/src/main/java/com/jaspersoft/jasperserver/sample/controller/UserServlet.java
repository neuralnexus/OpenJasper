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

import com.jaspersoft.jasperserver.sample.controller.vo.RoleWithAssignedMarkVO;
import com.jaspersoft.jasperserver.ws.authority.*;
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
public class UserServlet extends WSServlet {

    public static final String ATTR_USERS = "users";
    public static final String ATTR_USER = "user";
    public static final String ATTR_ROLES = "roles";
    public static final String ATTR_SEARCH_NAME = "searchName";
    public static final String ATTR_VIEW_MODE = "viewMode";

    public static final String PARAM_ACTION = "action";
    public static final String PARAM_SEARCH_NAME = "searchName";

    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_PUT = "put";
    public static final String ACTION_DELETE = "delete";

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_TENANT_ID = "tenantId";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_FULLNAME = "fullName";
    public static final String PARAM_EMAILADDRESS = "emailAddress";
    public static final String PARAM_ENABLED = "enabled";

    public static final String PARAM_ROLE_PREFIX = "role_";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter(PARAM_ACTION);

        UserAndRoleManagement userAndRoleManagement;
        try {
            userAndRoleManagement = WSClientManager.getUserAndRoleManagement(getBaseAddress(request));

            if (ACTION_EDIT.equalsIgnoreCase(action)) {
                WSUser user = getEditUser(request, userAndRoleManagement);
                List<RoleWithAssignedMarkVO> rolesWithAssignedMark = getEditRolesAssignedMarks(request,
                        userAndRoleManagement, user);

                if (user == null) {
                    response.sendRedirect("user");
                } else {
                    request.setAttribute(ATTR_USER, user);
                    request.setAttribute(ATTR_ROLES, rolesWithAssignedMark);
                    forward("authority/user.jsp", request, response);
                }
            } else if (ACTION_PUT.equalsIgnoreCase(action)) {
                putUser(request, userAndRoleManagement);

                response.sendRedirect("user");
            } if (ACTION_DELETE.equalsIgnoreCase(action)) {
                deleteUser(request, userAndRoleManagement);

                response.sendRedirect("user");
            }
        } catch (Exception e) {
            forwardError(e, request, response);
        }
    }

    protected WSUser getEditUser(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        String username = request.getParameter(PARAM_USERNAME);

        WSUser user;
        if (username != null && username.length() > 0) {
            user = getUser(request, username, userAndRoleManagement);
        } else {
            user = new WSUser();
        }

        return user;
    }

    private List<RoleWithAssignedMarkVO> getEditRolesAssignedMarks(HttpServletRequest request,
            UserAndRoleManagement userAndRoleManagement, WSUser user) throws RemoteException {
        String username = request.getParameter(PARAM_USERNAME);

        List<RoleWithAssignedMarkVO> rolesWithAssignedMark;
        if (username != null && username.length() > 0) {
            rolesWithAssignedMark = getRolesWithAssignedMark(
                    Arrays.asList(getAllRoles(request, userAndRoleManagement)),
                    Arrays.asList(user.getRoles()));
        } else {
            rolesWithAssignedMark = getRolesWithAssignedMark(
                    Arrays.asList(getAllRoles(request, userAndRoleManagement)), Collections.EMPTY_LIST);
        }

        return rolesWithAssignedMark;
    }

    protected void deleteUser(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement) throws RemoteException {
        String username = request.getParameter(PARAM_USERNAME);

        WSUser user = new WSUser();
        user.setUsername(username);

        userAndRoleManagement.deleteUser(user);
    }

    protected void putUser(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement) throws RemoteException {
        String username = request.getParameter(PARAM_USERNAME);
        String password = request.getParameter(PARAM_PASSWORD);
        String fullName = request.getParameter(PARAM_FULLNAME);
        String emailAddress = request.getParameter(PARAM_EMAILADDRESS);
        String enabled = request.getParameter(PARAM_ENABLED);

        WSRole[] assignedRoles = getAssignedRoles(request, userAndRoleManagement);

        WSUser user = new WSUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmailAddress(emailAddress);
        user.setEnabled(enabled.equals("on"));
        user.setPreviousPasswordChangeTime(new Date());
        user.setRoles(assignedRoles);

        userAndRoleManagement.putUser(user);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchName = request.getParameter(PARAM_SEARCH_NAME);
        searchName = (searchName != null) ? searchName : "";

        UserAndRoleManagement userAndRoleManagement;
        try {
            userAndRoleManagement = WSClientManager.getUserAndRoleManagement(getBaseAddress(request));

            WSUserSearchCriteria searchCriteria = createSearchCriteria(request, searchName);

            WSUser[] users = userAndRoleManagement.findUsers(searchCriteria);

            request.setAttribute(ATTR_SEARCH_NAME, searchName);
            request.setAttribute(ATTR_USERS, users);
            request.setAttribute(ATTR_VIEW_MODE, "user");

            forward("authority/list.jsp", request, response);
        } catch (Exception e) {
            forwardError(e, request, response);
        }
    }

    protected WSUserSearchCriteria createSearchCriteria(HttpServletRequest request, String searchName)
            throws RemoteException, ServiceException {
        WSUserSearchCriteria searchCriteria = new WSUserSearchCriteria();
        searchCriteria.setName(searchName);
        searchCriteria.setTenantId(null);
        searchCriteria.setMaxRecords(0);
        searchCriteria.setIncludeSubOrgs(Boolean.TRUE);
        searchCriteria.setRequiredRoles(null);
        return searchCriteria;
    }

    protected WSUser getUser(HttpServletRequest request, String username, UserAndRoleManagement userAndRoleManagement) throws RemoteException {
        WSUserSearchCriteria searchCriteria = new WSUserSearchCriteria();
        searchCriteria.setName(username);
        searchCriteria.setTenantId(null);
        searchCriteria.setMaxRecords(0);
        searchCriteria.setIncludeSubOrgs(Boolean.TRUE);
        searchCriteria.setRequiredRoles(null);

        WSUser[] users = userAndRoleManagement.findUsers(searchCriteria);

        for (WSUser user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    protected WSRole[] getAssignedRoles(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        List<WSRole> roles = new ArrayList<WSRole>();
        for (Map.Entry<String, String[]> entry : (Set<Map.Entry<String, String[]>>)request.getParameterMap().entrySet()) {
            String key = entry.getKey();

            if (key.startsWith(PARAM_ROLE_PREFIX)) {
                String name = key.substring(PARAM_ROLE_PREFIX.length());

                WSRole[] wsRole = userAndRoleManagement.findRoles(new WSRoleSearchCriteria(name, null, true, 0));

                if (wsRole == null || wsRole.length == 0 || wsRole.length > 1) {
                    throw new RuntimeException("Problems to find role with role name '" + name + "'");
                }

                roles.add(wsRole[0]);
            }
        }

        return roles.toArray(new WSRole[roles.size()]);
    }

    protected WSRole[] getAllRoles(HttpServletRequest request, UserAndRoleManagement userAndRoleManagement)
            throws RemoteException {
        WSRoleSearchCriteria searchCriteria = new WSRoleSearchCriteria("", null, true, 0);

        return userAndRoleManagement.findRoles(searchCriteria);
    }

    protected List<RoleWithAssignedMarkVO> getRolesWithAssignedMark(Collection<WSRole> allRoles,
            Collection<WSRole> assignedRoles) {
        List<RoleWithAssignedMarkVO> rolesWithAssignedMark = new ArrayList<RoleWithAssignedMarkVO>(allRoles.size());

        for (WSRole role : allRoles) {
            RoleWithAssignedMarkVO roleWithAssignedMarkVO = new RoleWithAssignedMarkVO();
            roleWithAssignedMarkVO.setRole(role);
            roleWithAssignedMarkVO.setAssigned(assignedRoles.contains(role));

            rolesWithAssignedMark.add(roleWithAssignedMarkVO);
        }

        return rolesWithAssignedMark;
    }
}
