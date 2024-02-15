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

import com.jaspersoft.jasperserver.sample.controller.vo.PermissionVO;
import com.jaspersoft.jasperserver.sample.controller.vo.RoleVO;
import com.jaspersoft.jasperserver.sample.controller.vo.UserVO;
import com.jaspersoft.jasperserver.ws.authority.*;
import com.jaspersoft.jasperserver.ws.client.authority.PermissionsManagement;
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
 * @author Yuriy Plakosh
 */
public class PermissionsServlet extends WSServlet {
    protected final static String SEPARATOR = "/";
    protected final static int NO_PERMISSION_SET = -1;
    protected final static int INHERITED = 0x100;
    protected final static String PERMISSION_PARM = "permission_";
    protected final static String PREV_PERMISSION_PARM = "prev_permission_";
    protected final static int NO_ACCESS = 0;
    protected final static String PROTOCOL = "repo:";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String resourceUri = request.getParameter("resourceUri");
        request.setAttribute("resourceUri", resourceUri);

        String show = request.getParameter("show");
        request.setAttribute("show", show);

        String ok = request.getParameter("ok");
        String cancel = request.getParameter("cancel");

        if (ok != null) {
            if (show.equals("byUser")) {
                try {
                    setUserPermissions(request, resourceUri);
                } catch (ServiceException e) {
                    throw new ServletException(e);
                }
            } else if (show.equals("byRole")) {
                try {
                    setRolePermissions(request, resourceUri);
                } catch (ServiceException e) {
                    throw new ServletException(e);
                }
            } else {
                throw new RuntimeException("Unknown mode '" + show + "'");
            }

            prepareDataAndForward(request, response, resourceUri, show);
        } else if (cancel != null) {
            response.sendRedirect("../listReports.jsp");
        } else {
            prepareDataAndForward(request, response, resourceUri, show);
        }
    }

    private void prepareDataAndForward(HttpServletRequest request, HttpServletResponse response, String resourceUri,
            String show) throws IOException, ServletException {
        PermissionsManagement permissionsManagement;
        try {
            permissionsManagement = WSClientManager.getPermissionsManagement(getBaseAddress(request));

            WSObjectPermission[] wsObjectPermissions =
                    permissionsManagement.getPermissionsForObject(getAddressWithProtocol(resourceUri));

            if (show.equals("byUser")) {
                Map<String, WSObjectPermission> userPermissionMap = getUserPermissionsMap(wsObjectPermissions);
                WSUser[] users = getUsers(request);
                List<UserVO> userVOs = getUserVOs(request, resourceUri, users, userPermissionMap);

                request.setAttribute("users", userVOs);
            } else if (show.equals("byRole")) {
                Map<String, WSObjectPermission> rolePermissionMap = getRolePermissionsMap(wsObjectPermissions);
                WSRole[] roles = getRoles(request);
                List<RoleVO> roleVOs = getRoleVOs(request, resourceUri, roles, rolePermissionMap);

                request.setAttribute("roles", roleVOs);
            }

            forward("authority/permissions.jsp", request, response);
        } catch (ServiceException e) {
            forwardError(e, request, response);
        }
    }

    private void setRolePermissions(HttpServletRequest request, String resourceUri)
            throws ServiceException, RemoteException {
        Map<String, Integer> permissionMap = getPermissionMap(request);

        if (permissionMap.size() > 0) {
            for (Map.Entry<String, Integer> entry : permissionMap.entrySet()) {
                String roleName = entry.getKey();
                int permissionMask = entry.getValue();
                int prevPermissionMask = getPreviousPermissionMask(roleName, request);
                WSRole role = getRole(request, roleName);

                setPermission(request, resourceUri, permissionMask, prevPermissionMask, role);
            }
        }
    }

    private void setPermission(HttpServletRequest request, String resourceUri, int permissionMask,
            int prevPermissionMask, Object recipient) throws ServiceException, RemoteException {
        if (prevPermissionMask != permissionMask) {
            PermissionsManagement permissionsManagement =
                    WSClientManager.getPermissionsManagement(getBaseAddress(request));

            WSObjectPermission permission = new WSObjectPermission();
            permission.setPermissionRecipient(recipient);
            permission.setUri(getAddressWithProtocol(resourceUri));
            permission.setPermissionMask(permissionMask);

            if (permissionMask <= 0xff) {
                int upChainPermission = goUpFolderChainToGetPermission(request, resourceUri, recipient);

                if (permissionMask == 0) {
                    if (upChainPermission == NO_ACCESS) {
                        permissionsManagement.deletePermission(permission);
                    } else {
                        permissionsManagement.putPermission(permission);
                    }
                } else {
                    if (upChainPermission == (permissionMask << 9)) {
                        permissionsManagement.deletePermission(permission);
                    } else {
                        permissionsManagement.putPermission(permission);
                    }
                }
            } else {
                permissionMask = permissionMask / 512;
                permission.setPermissionMask(permissionMask);

                permissionsManagement.putPermission(permission);
            }
        }
    }

    protected WSRole getRole(HttpServletRequest request, String roleName) throws RemoteException, ServiceException {
        WSRoleSearchCriteria searchCriteria = new WSRoleSearchCriteria(roleName, null, false, 0);
        WSRole[] roles = WSClientManager.getUserAndRoleManagement(getBaseAddress(request)).findRoles(searchCriteria);

        if (roles == null || roles.length == 0 || roles.length > 1) {
            throw new RuntimeException("Problems to find role with role name '" + roleName + "'");
        }

        return roles[0];
    }

    protected WSUser getUser(HttpServletRequest request, String userName) throws RemoteException, ServiceException {
        WSUserSearchCriteria searchCriteria = new WSUserSearchCriteria(userName, null, false, null, 0);
        WSUser[] users = WSClientManager.getUserAndRoleManagement(getBaseAddress(request)).findUsers(searchCriteria);

        if (users == null || users.length == 0 || users.length > 1) {
            throw new RuntimeException("Problems to find role with role name '" + userName + "'");
        }

        return users[0];
    }

    private void setUserPermissions(HttpServletRequest request, String resourceUri)
            throws ServiceException, RemoteException {
        Map<String, Integer> permissionMap = getPermissionMap(request);

        if (permissionMap.size() > 0) {
            for (Map.Entry<String, Integer> entry : permissionMap.entrySet()) {
                String userName = entry.getKey();
                int permissionMask = entry.getValue();
                int prevPermissionMask = getPreviousPermissionMask(userName, request);
                WSUser user = getUser(request, userName);

                setPermission(request, resourceUri, permissionMask, prevPermissionMask, user);
            }
        }
    }

    private Map<String, Integer> getPermissionMap(HttpServletRequest request) {
        Map<String, Integer> permissionMap = new HashMap<String, Integer>(request.getParameterMap().size());
        for (Map.Entry<String, String[]> entry : (Set<Map.Entry<String, String[]>>)request.getParameterMap().entrySet()) {
            if (entry.getKey().startsWith(PERMISSION_PARM)) {
                String name = entry.getKey().substring(PERMISSION_PARM.length());
                int permissionMask = Integer.parseInt(entry.getValue()[0]);

                permissionMap.put(name, permissionMask);
            }
        }

        return permissionMap;
    }
    
    protected int getPreviousPermissionMask(String name, HttpServletRequest request) {
        return Integer.parseInt(request.getParameter(PREV_PERMISSION_PARM + name));
    }

    private List<UserVO> getUserVOs(HttpServletRequest request, String resourceUri, WSUser[] users, Map<String,
            WSObjectPermission> permissionMap) throws ServiceException, RemoteException {
        List<UserVO> userVOs = new ArrayList<UserVO>(users.length);

        for (WSUser user : users) {
            PermissionVO permissionVO = getPermissionVO(request, resourceUri, permissionMap.get(getUserKey(user)), user);
            userVOs.add(getUserVO(user, permissionVO));
        }

        return userVOs;
    }

    protected UserVO getUserVO(WSUser user, PermissionVO permissionVO) {
        UserVO userVO = new UserVO();
        userVO.setUser(user);
        userVO.setPermission(permissionVO);

        return userVO;
    }

    private List<RoleVO> getRoleVOs(HttpServletRequest request, String resourceUri, WSRole[] roles, Map<String,
            WSObjectPermission> permissionMap) throws ServiceException, RemoteException {
        List<RoleVO> roleVOs = new ArrayList<RoleVO>(roles.length);

        for (WSRole role : roles) {
            PermissionVO permissionVO =
                    getPermissionVO(request, resourceUri, permissionMap.get(getRoleKey(role)), role);
            RoleVO roleVO = getRoleVO(role, permissionVO);

            roleVOs.add(roleVO);
        }

        return roleVOs;
    }

    protected RoleVO getRoleVO(WSRole role, PermissionVO permissionVO) {
        RoleVO roleVO = new RoleVO();
        roleVO.setRole(role);
        roleVO.setPermission(permissionVO);
        
        return roleVO;
    }

    private PermissionVO getPermissionVO(HttpServletRequest request, String resourceUri, WSObjectPermission permission,
            Object recipient) throws RemoteException, ServiceException {
        int permissionToDisplay = NO_PERMISSION_SET;

        if (permission != null && permission.getPermissionRecipient() != null) {
            permissionToDisplay = permission.getPermissionMask() +
                    goUpFolderChainToGetPermission(request, resourceUri, recipient);
        }

        if (permissionToDisplay == NO_PERMISSION_SET) {
            permissionToDisplay = goUpFolderChainToGetPermission(request, resourceUri, recipient) + INHERITED;
        }

        PermissionVO permissionVO = new PermissionVO();
        permissionVO.setPermission(permission);
        permissionVO.setPermissionToDisplay(permissionToDisplay);

        return permissionVO;
    }

    private Map<String, WSObjectPermission> getUserPermissionsMap(WSObjectPermission[] permissions) {
        Map<String, WSObjectPermission> permissionMap = new HashMap<String, WSObjectPermission>(permissions.length);
        for (WSObjectPermission wsObjectPermission : permissions) {
            if (wsObjectPermission.getPermissionRecipient() instanceof WSUser) {
                permissionMap.put(getUserKey((WSUser)wsObjectPermission.getPermissionRecipient()),
                        wsObjectPermission);
            }
        }

        return permissionMap;
    }

    private Map<String, WSObjectPermission> getRolePermissionsMap(WSObjectPermission[] permissions) {
        Map<String, WSObjectPermission> permissionMap = new HashMap<String, WSObjectPermission>(permissions.length);
        for (WSObjectPermission wsObjectPermission : permissions) {
            if (wsObjectPermission.getPermissionRecipient() instanceof WSRole) {
                permissionMap.put(getRoleKey((WSRole)wsObjectPermission.getPermissionRecipient()),
                        wsObjectPermission);
            }
        }

        return permissionMap;
    }

    protected String getUserKey(WSUser user) {
        return user.getUsername();
    }

    protected String getRoleKey(WSRole role) {
        return role.getRoleName();
    }

    protected WSUser[] getUsers(HttpServletRequest request) throws ServiceException, RemoteException {
        UserAndRoleManagement userAndRoleManagement =
                WSClientManager.getUserAndRoleManagement(getBaseAddress(request));

        WSUserSearchCriteria searchCriteria = new WSUserSearchCriteria();
        searchCriteria.setName("");
        searchCriteria.setMaxRecords(0);
        searchCriteria.setIncludeSubOrgs(Boolean.TRUE);
        searchCriteria.setRequiredRoles(null);

        return userAndRoleManagement.findUsers(searchCriteria);
    }

    protected WSRole[] getRoles(HttpServletRequest request) throws ServiceException, RemoteException {
        UserAndRoleManagement userAndRoleManagement =
                WSClientManager.getUserAndRoleManagement(getBaseAddress(request));

        WSRoleSearchCriteria searchCriteria = new WSRoleSearchCriteria();
        searchCriteria.setRoleName("");
        searchCriteria.setMaxRecords(0);
        searchCriteria.setIncludeSubOrgs(Boolean.TRUE);

        return userAndRoleManagement.findRoles(searchCriteria);
    }

    private int goUpFolderChainToGetPermission(HttpServletRequest request, String resourceUri, Object recipient)
            throws ServiceException, RemoteException {
		int permission = getInheritedObjectPermissionMask(request, resourceUri, recipient);
        return permission << 9;
	}

    private int getInheritedObjectPermissionMask(HttpServletRequest request, String resourceUri, Object recipient)
            throws RemoteException, ServiceException {
        int permissionMask = 0;

        String folderUri = getParentUri(resourceUri);
        while (folderUri != null) {
            WSObjectPermission[] permissions = getObjectPermissions(request, folderUri);
            if (permissions != null && permissions.length > 0) {
                for (WSObjectPermission permission : permissions) {
                    if (recipient instanceof WSUser && permission.getPermissionRecipient() instanceof WSUser) {
                        WSUser user = (WSUser)recipient;
                        WSUser permissionUser = (WSUser)permission.getPermissionRecipient();

                        if (user.getUsername().equals(permissionUser.getUsername())) {
                            permissionMask = permission.getPermissionMask();
                            break;
                        }
                    } else if (recipient instanceof WSRole && permission.getPermissionRecipient() instanceof WSRole) {
                        WSRole role = (WSRole)recipient;
                        WSRole permissionRole = (WSRole)permission.getPermissionRecipient();

                        if (role.getRoleName().equals(permissionRole.getRoleName())) {
                            permissionMask = permission.getPermissionMask();
                            break;
                        }
                    }
                }
            }

            folderUri = getParentUri(folderUri);
        }

        return permissionMask;
    }

    private WSObjectPermission[] getObjectPermissions(HttpServletRequest request, String resourceUri)
            throws ServiceException, RemoteException {
        PermissionsManagement permissionsManagement =
                WSClientManager.getPermissionsManagement(getBaseAddress(request));

        return permissionsManagement.getPermissionsForObject(getAddressWithProtocol(resourceUri));
    }

    private String getAddressWithProtocol(String resourceUri) {
        return resourceUri = (resourceUri.startsWith(PROTOCOL)) ? resourceUri : PROTOCOL + resourceUri;
    }

    private String getParentUri(String resourceUri) {
        int lastSeparator = resourceUri.lastIndexOf(SEPARATOR);

        // no separator
        if (lastSeparator < 0) {
            return null;
        } else if (lastSeparator == 0) {
            // if we are the root: no parent
            if (resourceUri.length() == 1) {
                return null;
            } else {
                return SEPARATOR;
            }
        } else {
            return resourceUri.substring(0, lastSeparator);
        }
    }
}
