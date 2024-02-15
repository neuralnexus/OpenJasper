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
package com.jaspersoft.jasperserver.jaxrs.authority;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientUser;
import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import com.jaspersoft.jasperserver.dto.authority.UserAttributesListWrapper;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.common.UserSearchCriteria;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.converters.UserAttributesConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.UserConverter;
import com.jaspersoft.jasperserver.remote.services.GenericAttributesService;
import com.jaspersoft.jasperserver.remote.services.UserAndRoleService;
import com.jaspersoft.jasperserver.remote.services.impl.UserAndRoleServiceImpl;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author: Zakhar.Tomchenco
 */

public class UsersJaxrsService {
    @Resource
    private UserConverter userConverter;
    @Resource
    private UserAttributesConverter userAttributesConverter;

    private UserAndRoleService service;
    private GenericAttributesService<User> attributesService;
    private int maxLengthAttrName = 255,  // defaults
                maxLengthAttrValue = 255;
    private Pattern empty = Pattern.compile("^\\s*$");

    public Response getUsers(int startIndex,
                             int maxRecords,
                             String tenantId,
                             Boolean includeSubOrgs,
                             Boolean hasAllRequiredRoles,
                             String search,
                             List<String> requredRoleNames) throws RemoteException {

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setIncludeSubOrgs(includeSubOrgs);
        criteria.setName(search == null ? "" : search);
        criteria.setTenantId("".equals(tenantId) ? null : tenantId);
        criteria.setHasAllRequiredRoles(hasAllRequiredRoles == null ? true : hasAllRequiredRoles);

        if (requredRoleNames != null && requredRoleNames.size() > 0) {
            List<Role> roles = new LinkedList<Role>();
            Role found;
            for (String name : requredRoleNames) {
                found = findRole(name);
                if (found == null) {
                    throw new ResourceNotFoundException(name);
                }
                roles.add(found);
            }
            criteria.setRequiredRoles(roles);
        }

        List<User> users = service.findUsers(criteria);
        int totalCount = users.size();

        if (totalCount < startIndex) {
            users.clear();
        } else {
            if (maxRecords != 0) {
                if (startIndex + maxRecords > totalCount) {
                    users = users.subList(startIndex, totalCount);
                } else {
                    users = users.subList(startIndex, startIndex + maxRecords);
                }
            } else {
                if (startIndex > 0){
                    users = users.subList(startIndex, totalCount);
                }
            }
        }
        Response response;
        if (users.size() == 0) {
            response = Response.status(Response.Status.NO_CONTENT)
                    .header(RestConstants.HEADER_START_INDEX, startIndex)
                    .header(RestConstants.HEADER_RESULT_COUNT, users.size())
                    .header(RestConstants.HEADER_TOTAL_COUNT, totalCount)
                    .build();
        } else {
            response = Response.ok()
                    .entity(new UsersListWrapper(users))
                    .header(RestConstants.HEADER_START_INDEX, startIndex)
                    .header(RestConstants.HEADER_RESULT_COUNT, users.size())
                    .header(RestConstants.HEADER_TOTAL_COUNT, totalCount)
                    .build();
        }
        return response;
    }

    public Response putUsers(){
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response createUser(ClientUser clientUser) throws RemoteException {
        if (findUser(clientUser.getUsername(), clientUser.getTenantId()) != null){
            throw new ResourceAlreadyExistsException(clientUser.getUsername());
        }

        User created = service.putUser(userConverter.toServer(clientUser, null));
        return Response.status(Response.Status.CREATED).entity(userConverter.toClient(created, null)).build();

    }

    public Response deleteUsers() {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response getPropertiesOfUser(String name,
                                        String tenantId) throws RemoteException {

        User user = findUser(name, tenantId);

        if (user != null){
            return Response.ok(userConverter.toClient(user, null)).build();
        }

        throw new ResourceNotFoundException(name);
    }

    public Response putUser(ClientUser clientUser, String name, String tenantId) throws RemoteException {
        User user = findUser(name, tenantId);
        Response.Status status = Response.Status.OK;

        if (user == null){
            status = Response.Status.CREATED;
            user = new UserImpl();
        }

        user = userConverter.toServer(clientUser, user, null);
        user.setUsername(name);
        user.setTenantId(tenantId);

        return Response.status(status).entity(userConverter.toClient(service.putUser(user), null)).build();
    }

    public Response postToUser(String name) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response deleteUser(String name,
                               String tenantId) throws RemoteException {

        User user = findUser(name, tenantId);

        if (user == null){
            throw new ResourceNotFoundException(name);
        }

        service.deleteUser(user);

        return Response.noContent().build();
    }

    public Response getAttributesOfUser(int startIndex, int maxRecords,
                                        String name,
                                        String tenantId,
                                        Set<String> attrNames) throws RemoteException {

        UserImpl user = new UserImpl();
        user.setUsername(name);
        user.setTenantId("".equals(tenantId) ? null : tenantId);

        List<ProfileAttribute> attributes;
        try {
            attributes = getAttributes(user, attrNames);
        } catch (ServiceException se) {
            throw new ResourceNotFoundException(name);
        }

        int totalCount = attributes.size();

        if (totalCount < startIndex) {
            attributes.clear();
        } else {
            if (maxRecords != 0) {
                if (startIndex + maxRecords > totalCount) {
                    attributes = attributes.subList(startIndex, totalCount);
                } else {
                    attributes = attributes.subList(startIndex, startIndex + maxRecords);
                }
            } else {
                if (startIndex > 0){
                    attributes = attributes.subList(startIndex, totalCount);
                }
            }
        }
        List<ClientUserAttribute> clientUserAttributes = new ArrayList<ClientUserAttribute>(attributes.size());
        for (ProfileAttribute pa : attributes){
            clientUserAttributes.add(userAttributesConverter.toClient(pa, null));
        }

        Response response;
        if (attributes.size() == 0) {
            response = Response.status(Response.Status.NO_CONTENT)
                    .header(RestConstants.HEADER_START_INDEX, startIndex)
                    .header(RestConstants.HEADER_RESULT_COUNT, attributes.size())
                    .header(RestConstants.HEADER_TOTAL_COUNT, totalCount)
                    .build();
        } else {
            response = Response.ok()
                    .entity(new UserAttributesListWrapper(clientUserAttributes))
                    .header(RestConstants.HEADER_START_INDEX, startIndex)
                    .header(RestConstants.HEADER_RESULT_COUNT, attributes.size())
                    .header(RestConstants.HEADER_TOTAL_COUNT, totalCount)
                    .build();
        }


       return response;
    }

    public Response putAttributes(List<ClientUserAttribute> newCollection,
                                  String name,
                                  String tenantId) throws RemoteException {

        for (ClientUserAttribute pa: newCollection){
            if (isEmpty(pa.getName()) || pa.getName().length() > maxLengthAttrName){
                 throw new IllegalParameterValueException("name",pa.getName());
            }
            if (isEmpty(pa.getValue()) || pa.getValue().length() > maxLengthAttrValue){
                throw new IllegalParameterValueException("value",pa.getValue());
            }
        }

        User user = findUser(name, tenantId);

        if (user == null){
            throw new ResourceNotFoundException(name);
        }

        List<ProfileAttribute> oldCollection = getAttributes(user, null);

        for (ProfileAttribute pa : oldCollection){
            attributesService.deleteAttribute(user, pa);
        }

        for (ClientUserAttribute pa : newCollection){
            attributesService.putAttribute(user,userAttributesConverter.toServer(pa, null));
        }

        return Response.ok().build();
    }

    public Response addAttribute(ClientUserAttribute clientUserAttribute,
                                 String name,
                                 String tenantId) throws RemoteException {
        ProfileAttribute pa = userAttributesConverter.toServer(clientUserAttribute, null);
        User user = findUser(name, tenantId);

        if (user == null){
            throw new ResourceNotFoundException(name);
        }

        if (attributesService.getAttribute(user, pa.getAttrName()) != null){
            throw new ResourceAlreadyExistsException(pa.getAttrName());
        }

        attributesService.putAttribute(user, pa);

        return Response.status(Response.Status.CREATED).entity(pa).build();
    }

    public Response deleteAttributes (String name,
                                      String tenantId,
                                      Set<String> attrNames) throws RemoteException {

        User user = findUser(name, tenantId);

        if (user == null){
            throw new ResourceNotFoundException(name);
        }

        List<ProfileAttribute> list = getAttributes(user, attrNames);

        for (ProfileAttribute pa : list){
            attributesService.deleteAttribute(user, pa);
        }
        return Response.noContent().build();
    }

    public Response getSpecificAttributeOfUser(String name,
                                               String tenantId,
                                               String attrName) throws RemoteException {
        List<ProfileAttribute> attributes = new ArrayList<ProfileAttribute>(1);

        UserImpl user = new UserImpl();
        user.setUsername(name);
        user.setTenantId("".equals(tenantId) ? null : tenantId);

        ProfileAttribute attribute = null;

        try {
            attribute = attributesService.getAttribute(user, attrName);
        } catch (JSException np) {
            throw new ResourceNotFoundException(name, np);
        }

        if (attribute == null) {
            throw new ResourceNotFoundException(attrName);
        }

        return Response.ok(userAttributesConverter.toClient(attribute, null)).build();
    }

    public Response putAttribute(ClientUserAttribute clientUserAttribute,
                                 String name,
                                 String tenantId,
                                 String attrName) throws RemoteException {
        Response.Status status = Response.Status.OK;
        ProfileAttribute attr = userAttributesConverter.toServer(clientUserAttribute, null);

        if (attr.getAttrName() == null) {
            attr.setAttrName(attrName);
        } else if (isEmpty(attr.getAttrName())) {
            throw new IllegalParameterValueException("name", "<empty>");
        }

        User user = findUser(name, tenantId);

        if (user == null) {
            throw new ResourceNotFoundException(name);
        }

        if (attrName.equals(attr.getAttrName())) {
            if (isEmpty(attr.getAttrValue())) {
                throw new IllegalParameterValueException("value", "<empty>");
            }
            if (attributesService.getAttribute(user, attr.getAttrName()) == null){
                status = Response.Status.CREATED;
            }
            attributesService.putAttribute(user, attr);
        } else {
            ProfileAttribute existing = attributesService.getAttribute(user, attrName);
            if (existing == null) {
                throw new ResourceNotFoundException(attrName);
            }
            if (attr.getAttrValue() == null) {
                attr.setAttrValue(existing.getAttrValue());
            }
            attributesService.putAttribute(user, attr);
            attributesService.deleteAttribute(user, existing);
        }

        return Response.status(status).build();
    }

    public Response postToAttribute(String name, String attrName) throws RemoteException {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response deleteAttribute(String name, String tenantId,String attrName) throws RemoteException {

        User user = findUser(name, tenantId);

        if (user == null){
            throw new ResourceNotFoundException(name);
        }

        ProfileAttributeImpl dto = new ProfileAttributeImpl();
        dto.setAttrName(attrName);

        try {
            attributesService.deleteAttribute(user, dto);
        } catch (ServiceException se) {
            if (se.getErrorCode() == ServiceException.RESOURCE_NOT_FOUND) {
                throw new ResourceNotFoundException(attrName);
            }
            throw se;
        }

        return Response.noContent().build();
    }



    private User findUser(String name, String tenantId) throws RemoteException {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setName(name);
        criteria.setTenantId("".equals(tenantId) ? null : tenantId);

        try{
            List<User> found = service.findUsers(criteria);

            for (User user : found) {
                if (requestedUser(user, name, tenantId)) {
                    return user;
                }
            }

        }  catch (IllegalStateException ise){
            throw new IllegalParameterValueException("username", "null");
        }

        return null;
    }

    private boolean requestedUser(User user, String name, String tenantId) {
        return (user.getUsername().equals(name)) &&
                ((user.getTenantId() == null && tenantId == null) ||
                (user.getTenantId() != null && user.getTenantId().equals(tenantId)));
    }

    private List<ProfileAttribute> getAttributes (User user, Set<String> attrNames) throws RemoteException {
        List<ProfileAttribute> found = attributesService.getAttributes(user), needed;

        if (attrNames != null && !attrNames.isEmpty()){
            needed = new LinkedList<ProfileAttribute>();
            for(ProfileAttribute pa : found){
                if (attrNames.contains(pa.getAttrName())){
                    needed.add(pa);
                }
            }
        } else {
            needed = found;
        }

        return needed;
    }

    private boolean isEmpty(String val){
         return val == null || empty.matcher(val).matches();
    }

    private Role findRole(String name) throws RemoteException{
       return ((UserAndRoleServiceImpl)service).getUserAuthorityService().getRole(null,name);
    }

    public UserAndRoleService getService() {
        return service;
    }

    public void setService(UserAndRoleService service) {
        this.service = service;
    }

    public GenericAttributesService<User> getAttributesService() {
        return attributesService;
    }

    public void setAttributesService(GenericAttributesService<User> attributesService) {
        this.attributesService = attributesService;
    }

    public int getMaxLengthAttrName() {
        return maxLengthAttrName;
    }

    public void setMaxLengthAttrName(int maxLengthAttrName) {
        this.maxLengthAttrName = maxLengthAttrName;
    }

    public int getMaxLengthAttrValue() {
        return maxLengthAttrValue;
    }

    public void setMaxLengthAttrValue(int maxLengthAttrValue) {
        this.maxLengthAttrValue = maxLengthAttrValue;
    }
}
