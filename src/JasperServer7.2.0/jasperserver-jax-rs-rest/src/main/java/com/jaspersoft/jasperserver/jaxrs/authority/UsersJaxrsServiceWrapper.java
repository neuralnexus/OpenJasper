/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jaxrs.authority;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.dto.authority.ClientAttribute;
import com.jaspersoft.jasperserver.dto.authority.ClientUser;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributesListWrapper;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.remote.helpers.RecipientIdentity;
import com.jaspersoft.jasperserver.remote.resources.converters.HypermediaOptions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @author: Zakhar.Tomchenco
 */
@Component
@Transactional(rollbackFor = Exception.class)
@Path("/users")
@Scope("prototype")
public class UsersJaxrsServiceWrapper {
    @Resource(name = "usersJaxrsService")
    private UsersJaxrsService service;

    @Resource(name = "attributesJaxrsService")
    private AttributesJaxrsService attributesJaxrsService;

    @Context
    private Providers providers;

    @Context
    private HttpHeaders httpHeaders;

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
                             @QueryParam("requiredRole") List<String> requredRoleNames) throws ErrorDescriptorException {

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
    public Response getPropertiesOfUser(@PathParam("name") String name) throws ErrorDescriptorException {
        return service.getPropertiesOfUser(name, null);
    }

    @PUT
    @Path("/{name}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putUser(ClientUser clientUser,
                            @PathParam("name") String name) throws ErrorDescriptorException {
        if (clientUser.getTenantId() != null && !clientUser.getTenantId().isEmpty()){
            throw new IllegalParameterValueException("tenantId", clientUser.getTenantId());
        }

        return service.putUser(clientUser, name, null);
    }

    @DELETE
    @Path("/{name}")
    public Response deleteUser(@PathParam("name") String name) throws ErrorDescriptorException {
        return service.deleteUser(name, null);
    }

    @GET
    @Path("/{name}/attributes")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    public Response getAttributesOfUser(@PathParam("name") String userName,
                                        @QueryParam("name") Set<String> attrNames,
                                        @QueryParam("_embedded") String embedded,
                                        @HeaderParam(HttpHeaders.ACCEPT) String accept) throws ErrorDescriptorException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        return attributesJaxrsService.getAttributesOfRecipient(getHolder(userName), attrNames, hypermediaOptions);
    }

    @PUT
    @Path("/{name}/attributes")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    public Response putAttributes(HypermediaAttributesListWrapper newCollection,
                                  @QueryParam("name") Set<String> attrNames,
                                  @HeaderParam(HttpHeaders.CONTENT_TYPE) String mediaType,
                                  @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                  @QueryParam("_embedded") String embedded,
                                  @PathParam("name") String userName) throws ErrorDescriptorException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        return attributesJaxrsService.putAttributes(newCollection.getProfileAttributes(), getHolder(userName), attrNames, hypermediaOptions, mediaType);
    }

    @DELETE
    @Path("/{name}/attributes")
    public Response deleteAttributes(@PathParam("name") String userName,
                                      @QueryParam("name") Set<String> attrNames) throws ErrorDescriptorException {
        return attributesJaxrsService.deleteAttributes(getHolder(userName), attrNames);
    }


    @GET
    @Path("/{name}/attributes/{attrName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    public Response getSpecificAttributeOfUser(@PathParam("name") String userName,
                                               @PathParam("attrName") String attrName,
                                               @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                               @QueryParam("_embedded") String embedded) throws ErrorDescriptorException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        return attributesJaxrsService.getSpecificAttributeOfRecipient(getHolder(userName), attrName, hypermediaOptions);
    }

    @PUT
    @Path("/{name}/attributes/{attrName}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    public Response putAttribute(InputStream stream,
                                 @PathParam("name") String userName,
                                 @PathParam("attrName") String attrName,
                                 @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                 @HeaderParam(HttpHeaders.CONTENT_TYPE) MediaType mediaType,
                                 @QueryParam("_embedded") String embedded) throws ErrorDescriptorException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        ClientAttribute clientAttribute = AttributesJaxrsService.parseEntity(stream, mediaType, providers, httpHeaders);
        return attributesJaxrsService
                .putAttribute(clientAttribute, getHolder(userName), attrName, hypermediaOptions, mediaType.toString());
    }


    @DELETE
    @Path("/{name}/attributes/{attrName}")
    public Response deleteAttribute(@PathParam("name") String userName,
                                    @PathParam("attrName") String attrName,
                                    @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                    @QueryParam("_embedded") String embedded) throws ErrorDescriptorException {
        return attributesJaxrsService.deleteAttribute(getHolder(userName), attrName);
    }

    public RecipientIdentity getHolder(String userName) {
        return new RecipientIdentity(User.class, userName);
    }

}
