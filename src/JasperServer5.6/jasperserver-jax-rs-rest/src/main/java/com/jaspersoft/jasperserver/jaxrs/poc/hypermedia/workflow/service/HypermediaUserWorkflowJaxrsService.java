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
package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.service;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.activity.ReadUserWorkflowActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.activity.ReadUserWorkflowCollectionActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.MediaTypes;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.wadl.PrivateApi;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * @author Igor.Nesterenko
 * @version $Id: HypermediaUserWorkflowJaxrsService.java 47331 2014-07-18 09:13:06Z kklein $
 */
@PrivateApi
@Component
@Path("/hypermedia/workflows")
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HypermediaUserWorkflowJaxrsService {

    @Resource
    private ReadUserWorkflowCollectionActivity workflowCollectionActivity;

    @Resource
    private ReadUserWorkflowActivity workflowActivity;

    @GET
    @Path("/{name}")
    @Produces({MediaTypes.APPLICATION_HAL_JSON})
    public Response getUserWorkflow(@PathParam("name") String name){

        workflowActivity.setGenericRequest(new GenericRequest()
                .addParam("name", name)
                .setExpanded(true)
        );

        return  Response.ok(workflowActivity.proceed()).build();
    }

    @GET
    @Produces({MediaTypes.APPLICATION_HAL_JSON})
    public Response getUserWorkflowCollectionRepresentation(
            @DefaultValue("null")@QueryParam("parentName") String parentName){

        GenericRequest genericRequest = new GenericRequest()
                .addParam("parentName", parentName)
                .setExpanded(true);

        workflowCollectionActivity.setGenericRequest(genericRequest);

        return Response.ok(workflowCollectionActivity.proceed()).build();
    }

}
