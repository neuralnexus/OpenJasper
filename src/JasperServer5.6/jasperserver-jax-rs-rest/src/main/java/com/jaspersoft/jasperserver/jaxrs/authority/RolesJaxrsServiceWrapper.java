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

import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientRole;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author: Zakhar.Tomchenco
 */
@Component
@Path("/roles")
public class RolesJaxrsServiceWrapper {

    @Resource(name = "rolesJaxrsService")
    private RolesJaxrsService service;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getRoles(@QueryParam("maxRecords") int maxRecords,
                             @QueryParam(RestConstants.QUERY_PARAM_OFFSET) Integer startIndex,
                             @QueryParam(RestConstants.QUERY_PARAM_LIMIT) Integer limit,
                             @QueryParam("subOrgId") String tenantId,
                             @QueryParam("includeSubOrgs") Boolean includeSubOrgs,
                             @QueryParam("search") String search,
                             @QueryParam(RestConstants.QUERY_PARAM_SEARCH_QUERY) String q,
                             @QueryParam("hasAllUsers")Boolean hasAllUsers,
                             @QueryParam("user")List<String> userNames) throws RemoteException {
        if (limit != null){
            maxRecords = limit;
        }
        if (q != null){
            search = q;
        }
        return service.getRoles(startIndex == null ? 0 : startIndex, maxRecords, tenantId, includeSubOrgs, search, hasAllUsers, userNames);
    }

    @GET
    @Path("/{name}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getRoles(@PathParam("name") String name) throws RemoteException {
        return service.getRoles(name, null);
    }

    @DELETE
    @Path("/{name}")
    public Response deleteRole(@PathParam("name") String name) throws RemoteException {
        return service.deleteRole(name, null);
    }

    @PUT
    @Path("/{name}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateRole(ClientRole newRole,
                               @PathParam("name") String name) throws RemoteException{

        if (newRole.getTenantId() != null){
            throw new IllegalParameterValueException("tenantId", newRole.getTenantId());
        }
        return service.updateRole(newRole, name, null);
    }
}
