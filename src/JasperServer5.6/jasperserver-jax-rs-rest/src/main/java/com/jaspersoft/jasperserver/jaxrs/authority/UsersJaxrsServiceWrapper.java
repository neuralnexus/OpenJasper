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

import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientUser;
import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import com.jaspersoft.jasperserver.dto.authority.UserAttributesListWrapper;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

/**
 * @author: Zakhar.Tomchenco
 */
@Component
@Path("/users")
public class UsersJaxrsServiceWrapper {

    @Resource(name = "usersJaxrsService")
    private UsersJaxrsService service;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getUsers(@QueryParam(RestConstants.QUERY_PARAM_OFFSET) Integer startIndex,
                             @QueryParam(RestConstants.QUERY_PARAM_LIMIT) Integer limit,
                             @QueryParam("maxRecords") int maxRecords,
                             @QueryParam("subOrgId") String tenantId,
                             @QueryParam("includeSubOrgs") Boolean includeSubOrgs,
                             @QueryParam("hasAllRequiredRoles") Boolean hasAllRequiredRoles,
                             @QueryParam("search") String search,
                             @QueryParam(RestConstants.QUERY_PARAM_SEARCH_QUERY) String q,
                             @QueryParam("requiredRole") List<String> requredRoleNames) throws RemoteException {

        if (q != null){
            search = q;
        }
        if (limit != null){
            maxRecords = limit;
        }
        return service.getUsers(startIndex == null ? 0 : startIndex, maxRecords, tenantId, includeSubOrgs,hasAllRequiredRoles, search, requredRoleNames);
    }

    @GET
    @Path("/{name}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPropertiesOfUser(@PathParam("name") String name) throws RemoteException {
        return service.getPropertiesOfUser(name, null);
    }

    @PUT
    @Path("/{name}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putUser(ClientUser clientUser,
                            @PathParam("name") String name) throws RemoteException {
        if (clientUser.getTenantId() != null && clientUser.getTenantId() != ""){
            throw new IllegalParameterValueException("tenantId", clientUser.getTenantId());
        }

        return service.putUser(clientUser, name, null);
    }

    @DELETE
    @Path("/{name}")
    public Response deleteUser(@PathParam("name") String name) throws RemoteException {
        return service.deleteUser(name, null);
    }

    @GET
    @Path("/{name}/attributes")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAttributesOfUser(@PathParam("name") String name,
                                        @QueryParam("name") Set<String> attrNames,
                                        @QueryParam(RestConstants.QUERY_PARAM_OFFSET) Integer startIndex,
                                        @QueryParam(RestConstants.QUERY_PARAM_LIMIT) Integer limit) throws RemoteException {

        return service.getAttributesOfUser(startIndex == null ? 0 : startIndex, limit == null ? 0 : limit, name, null, attrNames);
    }

    @PUT
    @Path("/{name}/attributes")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putAttributes(UserAttributesListWrapper newCollection,
                                  @PathParam("name") String name) throws RemoteException {

        return service.putAttributes(newCollection.getProfileAttributes(), name, null);
    }

    @DELETE
    @Path("/{name}/attributes")
    public Response deleteAttributes (@PathParam("name") String name,
                                      @QueryParam("name") Set<String> attrNames) throws RemoteException {

        return service.deleteAttributes(name, null, attrNames);
    }


    @GET
    @Path("/{name}/attributes/{attrName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getSpecificAttributeOfUser(@PathParam("name") String name,
                                               @PathParam("attrName") String attrName) throws RemoteException {

        return service.getSpecificAttributeOfUser(name, null, attrName);
    }

    @PUT
    @Path("/{name}/attributes/{attrName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putAttribute(ClientUserAttribute attr,
                                 @PathParam("name") String name,
                                 @PathParam("attrName") String attrName) throws RemoteException {

        return service.putAttribute(attr, name, null, attrName);
    }


    @DELETE
    @Path("/{name}/attributes/{attrName}")
    public Response deleteAttribute(@PathParam("name") String name,
                                    @PathParam("attrName") String attrName) throws RemoteException{

        return service.deleteAttribute(name, null, attrName);
    }
}
