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
package com.jaspersoft.jasperserver.jaxrs.importexport;

import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.async.ExportRunnable;
import com.jaspersoft.jasperserver.remote.services.async.ImportExportTask;
import com.jaspersoft.jasperserver.remote.services.async.TasksManager;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Zakhar.Tomchenco
 */
@Component
@Path("/export")
public class ExportJaxrsService  {

    @Resource
    private ImportExportService synchImportExportService;

    @Resource
    private TasksManager basicTaskManager;

    @Resource(name="messageSource")
    private MessageSource messageSource;

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createNewTask(final ExportTaskDto data) throws RemoteException{

        Map<String, Boolean> params = new HashMap<String, Boolean>();
        if (data.getParameters() != null){
            for (String par : data.getParameters()) {
                params.put(par, true);
            }
        }

        ExportRunnable exportRunnable = new ExportRunnable(params, data.getUris(), data.getScheduledJobs(), data.getRoles(), data.getUsers(), LocaleContextHolder.getLocale());
        exportRunnable.setService(synchImportExportService);
        exportRunnable.setMessageSource(messageSource);
        basicTaskManager.startTask(new ImportExportTask<InputStream>(exportRunnable));

        return Response.ok(exportRunnable.getState()).build();
    }

    @GET
    @Path("/{id}/state")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStateOfTheTask(@PathParam("id") final String taskId) throws RemoteException{
        return Response.ok(basicTaskManager.getTaskState(taskId)).build();
    }

    @GET
    @Path("/{id}/{name}")
    @Produces("application/zip")
    public Response downloadFile(@PathParam("id") final String taskId, @PathParam("name") String name) throws RemoteException{
        Response response = Response.ok(basicTaskManager.getTask(taskId).getResult()).build();
        basicTaskManager.finishTask(taskId);
        return response;
    }

}
