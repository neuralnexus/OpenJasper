/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jaxrs.authority;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.dto.authority.ClientAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributesListWrapper;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.helpers.RecipientIdentity;
import com.jaspersoft.jasperserver.remote.resources.converters.HypermediaOptions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
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
import java.util.Set;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Path("/attributes")
@Scope("prototype")
public class ServerAttributesJaxrsService {
    @Resource(name = "attributesJaxrsService")
    private AttributesJaxrsService attributesJaxrsService;

    @Context
    private Providers providers;

    @Context
    private HttpHeaders httpHeaders;

    @GET
    @Produces({"application/attributes.collection+xml", "application/attributes.collection+json",
            "application/attributes.collection.hal+json", "application/attributes.collection.hal+xml"})
    public Response getAttributes(@QueryParam("name") Set<String> attrNames,
                                  @QueryParam("group") Set<String> groups,
                                  @QueryParam(RestConstants.QUERY_PARAM_OFFSET) Integer startIndex,
                                  @QueryParam(RestConstants.QUERY_PARAM_LIMIT) Integer limit,
                                  @QueryParam("recursive") Boolean recursive,
                                  @QueryParam("holder") String holder,
                                  @QueryParam("_embedded") String embedded,
                                  @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                  @QueryParam("includeInherited") Boolean effective) throws RemoteException {
        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
                .setHolder(holder)
                .setStartIndex(startIndex == null ? 0 : startIndex)
                .setMaxRecords(limit == null ? 0 : limit)
                .setNames(attrNames)
                .setGroups(groups)
                .setRecursive(Boolean.TRUE == recursive)
                .setEffective(Boolean.TRUE == effective)
                .build();
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);

        return attributesJaxrsService.getAttributes(searchCriteria, hypermediaOptions);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    public Response getAttributes(@QueryParam("name") Set<String> attrNames,
                                  @QueryParam("_embedded") String embedded,
                                  @HeaderParam(HttpHeaders.ACCEPT) String accept) throws RemoteException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        return attributesJaxrsService.getAttributesOfRecipient(getHolder(), attrNames, hypermediaOptions);
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    public Response putAttributes(HypermediaAttributesListWrapper newCollection,
                                  @QueryParam("name") Set<String> attrNames,
                                  @HeaderParam(HttpHeaders.CONTENT_TYPE) String mediaType,
                                  @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                  @QueryParam("_embedded") String embedded) throws RemoteException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        return attributesJaxrsService.putAttributes(newCollection.getProfileAttributes(), getHolder(), attrNames, hypermediaOptions, mediaType);
    }

    @DELETE
    public Response deleteAttributes(@QueryParam("name") Set<String> attrNames) throws RemoteException {
        return attributesJaxrsService.deleteAttributes(getHolder(), attrNames);
    }

    @GET
    @Path("/{attrName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+json", "application/hal+xml"})
    public Response getSpecificAttribute(@PathParam("attrName") String attrName,
                                         @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                         @QueryParam("_embedded") String embedded) throws RemoteException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        return attributesJaxrsService.getSpecificAttributeOfRecipient(getHolder(), attrName, hypermediaOptions);
    }

    @PUT
    @Path("/{attrName}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+xml", "application/hal+json"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, "application/hal+xml","application/hal+json"})
    public Response putAttribute(InputStream stream,
                                 @PathParam("attrName") String attrName,
                                 @HeaderParam(HttpHeaders.ACCEPT) String accept,
                                 @HeaderParam(HttpHeaders.CONTENT_TYPE)MediaType mediaType,
                                 @QueryParam("_embedded") String embedded) throws RemoteException {
        HypermediaOptions hypermediaOptions = attributesJaxrsService.getHypermediaOptions(accept, embedded);
        ClientAttribute clientAttribute = AttributesJaxrsService.parseEntity(stream, mediaType, providers, httpHeaders);
        return attributesJaxrsService.putAttribute(clientAttribute, getHolder(), attrName, hypermediaOptions, mediaType.toString());
    }

    @DELETE
    @Path("/{attrName}")
    public Response deleteAttribute(@PathParam("attrName") String attrName) throws RemoteException {
        return attributesJaxrsService.deleteAttribute(getHolder(), attrName);
    }

    public RecipientIdentity getHolder() {
        return new RecipientIdentity(Tenant.class, TenantService.ORGANIZATIONS);
    }

}
