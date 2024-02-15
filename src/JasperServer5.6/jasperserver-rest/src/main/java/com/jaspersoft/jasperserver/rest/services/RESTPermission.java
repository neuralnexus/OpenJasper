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
package com.jaspersoft.jasperserver.rest.services;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.remote.services.ResourcesManagementRemoteService;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.rest.utils.JAXBList;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author carbiv
 * @version $Id: RESTPermission.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service("restPermissionService")
public class RESTPermission extends RESTAbstractService
{
    private final static Log log = LogFactory.getLog(RESTPermission.class);
    @Resource(name = "rolesToDisablePermissionEditForEveryone")
    private List<String> rolesToDisablePermissionEditForEveryone;
    @Resource(name = "rolesToDisablePermissionEditForNonSuperuser")
    private List<String> rolesToDisablePermissionEditForNonSuperuser;
    @Resource
    private PermissionsService permissionsService;
    @Resource
    private ResourcesManagementRemoteService resourcesManagementRemoteService;

    public void setPermissionsService(PermissionsService permissionsService) {
        this.permissionsService = permissionsService;
    }

    public void setResourcesManagementRemoteService(ResourcesManagementRemoteService resourcesManagementRemoteService) {
        this.resourcesManagementRemoteService = resourcesManagementRemoteService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        String res = restUtils.extractRepositoryUri(req.getPathInfo());
        try {
            List<ObjectPermission> permissions = permissionsService.getPermissions(res);
            restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, generatePermissionUsingJaxb(permissions));
        } catch (RemoteException e) {
            throw new ServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getClass().getName() + (e.getMessage() != null ? ": " + e.getMessage() : ""));
        }
    }

    private String generatePermissionUsingJaxb(List<ObjectPermission> permissions) {
        try{
            StringWriter sw = new StringWriter();

            JAXBList<ObjectPermission> lst;
            lst = new JAXBList<ObjectPermission>(permissions);

            Marshaller m = restUtils.getMarshaller(JAXBList.class, ObjectPermissionImpl.class, UserImpl.class, RoleImpl.class);
            m.marshal(lst, sw);
            if (log.isDebugEnabled()) {
                log.debug("finished marshaling permissions: " + lst.size());
            }

            return sw.toString();
        }
        catch (JAXBException e) {
            throw new ServiceException(e.getMessage());
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        doPut(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        try {
            JAXBList<ObjectPermission> perm = (JAXBList<ObjectPermission>) restUtils.unmarshal(req.getInputStream(),
                    JAXBList.class, ObjectPermissionImpl.class, UserImpl.class, RoleImpl.class);
            for (int i = 0; i < perm.size(); i++) {
                if (isValidObjectPermission(perm.get(i)) && canUpdateObjectPermissions(perm.get(i)))
                    try {
                        permissionsService.putPermission(perm.get(i));
                    } catch (IllegalArgumentException e){
                        throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    } catch (RemoteException e) {
                        throw new ServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getClass().getName() +
                                (e.getMessage() != null ? ": " + e.getMessage() : ""));
                    }
                else {
                    if (perm.get(i).getPermissionRecipient() instanceof User) {
                        throw new AccessDeniedException("could not set permissions for: " +
                                ((User) perm.get(i).getPermissionRecipient()).getUsername());
                    } else if (perm.get(i).getPermissionRecipient() instanceof Role){
                        throw new AccessDeniedException("could not set permissions for: " +
                                ((Role) perm.get(i).getPermissionRecipient()).getRoleName());
                    } else {
                        throw new AccessDeniedException("could not set permissions for: " +
                                (perm.get(i).getPermissionRecipient() != null ?
                                        perm.get(i).getPermissionRecipient().toString() : "null"));
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("finished PUT permissions " + perm.size() + " permissions were added");
            }
            restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");
        } catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "please check the request job descriptor");
        } catch (IOException e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "please check the request job descriptor");
        } catch (JAXBException e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "please check the request job descriptor");
        }
    }

    private boolean isValidObjectPermission(ObjectPermission objectPermission) {
        return resourcesManagementRemoteService.locateResource(objectPermission.getURI().replace("repo:", "")) != null;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        String[] params;
        String res = restUtils.extractRepositoryUri(req.getPathInfo());
        List<ObjectPermission> permissions;
        try {
            permissions = permissionsService.getPermissions(res);
        } catch (RemoteException e) {
            throw new ServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getClass().getName() + (e.getMessage() != null ? ": " + e.getMessage() : ""));
        }

        List<String> rolesThatCantBeDeleted = new LinkedList();
        List<String> usersThatCantBeDeleted = new LinkedList();


        final String roles = req.getParameter(restUtils.REQUEST_PARAMENTER_ROLES);
        final String users = req.getParameter(restUtils.REQUEST_PARAMENTER_USERS);
        if ((roles != null && !"".equals(roles)) || (users != null && !"".equals(users)))
        {
            List<String> rolesToDelete = restUtils.stringToList(roles != null ? roles : "", restUtils.REQUEST_PARAMENTER_SEPARATOR);

            List<String> usersToDelete = restUtils.stringToList(users != null ? users : "", restUtils.REQUEST_PARAMENTER_SEPARATOR);

            if (log.isDebugEnabled()) {
                log.debug("roles to delete: " + rolesToDelete.size()+" users to delete: "+ usersToDelete.size());
            }

            String roleName, userName;
            Iterator<String> rolesIter = rolesToDelete.iterator();
            while (rolesIter.hasNext()){
                roleName = rolesIter.next();
                if (!canUpdateRolePermissions(roleName)){
                    throw new AccessDeniedException("could not set permissions for: "+roleName);
                }
            }

            Iterator<String> usersIter = usersToDelete.iterator();
            while (usersIter.hasNext()){
                userName = usersIter.next();
                if (!canUpdateUserPermissions(userName)){
                    if (log.isDebugEnabled()) {
                        log.debug("removed user: " + userName+" from the delete permission request");
                    }
                    throw new AccessDeniedException("could not set permissions for: "+userName);
                }
            }

            Object permissionRecipient;
            for (ObjectPermission op : permissions) {
                permissionRecipient = op.getPermissionRecipient();


                if ((permissionRecipient instanceof Role && rolesToDelete.contains(((Role) permissionRecipient).getRoleName())) ||
                        (permissionRecipient instanceof User && usersToDelete.contains(((User) permissionRecipient).getUsername())))
                    try {
                        permissionsService.deletePermission(op);
                    } catch (RemoteException e) {
                        throw new ServiceException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getClass().getName() + (e.getMessage() != null ? ": " + e.getMessage() : ""));
                    }
            }
        }
        else {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "users or roles must be specified");
        }

        restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");
    }

    private boolean canUpdateObjectPermissions(ObjectPermission objectPermission)
    {
        if (objectPermission.getPermissionRecipient() instanceof User)
        {
            User user = (User)objectPermission.getPermissionRecipient();
            if (!restUtils.getCurrentlyLoggedUserDetails().getUsername().equals(user.getUsername()))
                return true;
        }
        if (objectPermission.getPermissionRecipient() instanceof Role)
        {
            Role role = (Role)objectPermission.getPermissionRecipient();

            // if is super user and the role in the not in the disable for super user || if role is not in disable for every one
            if (    (restUtils.isCurrentlyLoggedUserHasSuperUserRole() && !rolesToDisablePermissionEditForEveryone.contains(role.getRoleName())) ||
                    !(rolesToDisablePermissionEditForEveryone.contains(role.getRoleName()) || rolesToDisablePermissionEditForNonSuperuser.contains(role.getRoleName())))
            {
                return true;
            }
        }
        return false;
    }

    private boolean canUpdateRolePermissions(String roleName) {
        return (restUtils.isCurrentlyLoggedUserHasSuperUserRole() && !rolesToDisablePermissionEditForEveryone.contains(roleName)) ||
                !(rolesToDisablePermissionEditForEveryone.contains(roleName) || rolesToDisablePermissionEditForNonSuperuser.contains(roleName));
    }

    private boolean canUpdateUserPermissions(String userName){
        return !restUtils.getCurrentlyLoggedUserDetails().getUsername().equals(userName);
    }

    public List<String> getRolesToDisablePermissionEditForEveryone() {
        return rolesToDisablePermissionEditForEveryone;
    }

    public void setRolesToDisablePermissionEditForEveryone(List<String> rolesToDisablePermissionEditForEveryone) {
        this.rolesToDisablePermissionEditForEveryone = rolesToDisablePermissionEditForEveryone;
    }

    public List<String> getRolesToDisablePermissionEditForNonSuperuser() {
        return rolesToDisablePermissionEditForNonSuperuser;
    }

    public void setRolesToDisablePermissionEditForNonSuperuser(List<String> rolesToDisablePermissionEditForNonSuperuser) {
        this.rolesToDisablePermissionEditForNonSuperuser = rolesToDisablePermissionEditForNonSuperuser;
    }

}
