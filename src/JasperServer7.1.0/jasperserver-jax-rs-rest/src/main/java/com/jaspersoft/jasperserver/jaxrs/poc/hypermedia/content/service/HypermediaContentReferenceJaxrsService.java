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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.service;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.MediaTypes;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.cache.CacheControlHelper;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.activity.ReadContentReferenceActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.activity.ReadContentReferenceCollectionActivity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author: Igor.Nesterenko
 * @version: $Id$
 */

@Service
@Path("/hypermedia/contentReferences")
@Scope("prototype")
public class HypermediaContentReferenceJaxrsService {

    @Resource
    private ReadContentReferenceCollectionActivity contentCollectionActivity;

    @Resource
    private ReadContentReferenceActivity contentReferenceActivity;

    @GET
    @Path("/{id}")
    @Produces({MediaTypes.APPLICATION_HAL_JSON})
    public Response getContentReference(@PathParam("id") String name){

        contentReferenceActivity.setGenericRequest(new GenericRequest()
                .addParam("id", name)
                .setExpanded(true)
        );

        EmbeddedElement representation =  contentReferenceActivity.proceed();

        return CacheControlHelper
                .enableLocaleAwareStaticCache(Response.ok(representation))
                .build();
    }

    @GET
    @Produces({MediaTypes.APPLICATION_HAL_JSON})
    public Response getContentReferenceCollection(@DefaultValue("null") @QueryParam("group") String group){

        contentCollectionActivity.setGenericRequest(new GenericRequest()
                .addParam("group", group)
                .setExpanded(true)
        );

        EmbeddedElement representation = contentCollectionActivity.proceed();

        return CacheControlHelper
                .enableLocaleAwareStaticCache(Response.ok(representation))
                .build();
    }

}
