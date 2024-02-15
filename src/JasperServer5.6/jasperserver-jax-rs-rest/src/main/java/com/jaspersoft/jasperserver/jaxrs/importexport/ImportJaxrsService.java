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
import com.jaspersoft.jasperserver.remote.services.async.*;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * @author Zakhar.Tomchenco
 */

@Component
@Path("/import")
public class ImportJaxrsService {
    private static final Log log = LogFactory.getLog(ImportJaxrsService.class);

    @Resource
    private ImportExportService synchImportExportService;

    @Resource
    private TasksManager basicTaskManager;

    @Resource(name="messageSource")
    private MessageSource messageSource;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response handleMultipartUpload(
            @FormDataParam("file") InputStream stream,
            @DefaultValue("true")@FormDataParam("update")  Boolean update,
            @DefaultValue("true")@FormDataParam("skip-user-update") Boolean skipUserUpdate,
            @DefaultValue("true")@FormDataParam("include-audit-events") Boolean includeAuditEvents,
            @DefaultValue("true")@FormDataParam("include-access-events") Boolean includeAccessEvents,
            @DefaultValue("true")@FormDataParam("include-monitoring-events") Boolean includeMonitoringEvents,
            @DefaultValue("true")@FormDataParam("include-server-settings") Boolean includeSettings ) throws Exception {

        // In case of form submission from browser we for NULL if param is absent and FALSE if it set
        // (because browsers put value 'on' or empty string and jersey parses it to FALSE)
        // That is why @DefaultValue("true") is set and also we have to negate values
        return handleUpload(stream, !update, !skipUserUpdate, !includeAuditEvents, !includeAccessEvents, !includeMonitoringEvents, !includeSettings);

    }

    @POST
    @Consumes("application/zip")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response handleDirectUpload(InputStream stream,
                                       @DefaultValue("false")@QueryParam("update") Boolean update,
                                       @DefaultValue("false")@QueryParam("skipUserUpdate") Boolean skipUserUpdate,
                                       @DefaultValue("false")@QueryParam("includeAuditEvents") Boolean includeAuditEvents,
                                       @DefaultValue("false")@QueryParam("includeAccessEvents") Boolean includeAccessEvents,
                                       @DefaultValue("false")@QueryParam("includeMonitoringEvents") Boolean includeMonitoringEvents,
                                       @DefaultValue("false")@QueryParam("includeServerSettings") Boolean includeSettings ) throws Exception {

        return handleUpload(stream, update, skipUserUpdate, includeAuditEvents, includeAccessEvents, includeMonitoringEvents, includeSettings);
    }

    protected Response handleUpload(InputStream stream, Boolean update, Boolean skipUserUpdate,Boolean includeAuditEvents,Boolean includeAccessEvents,Boolean includeMonitoringEvents,Boolean includeSettings ) throws Exception {
        Map<String, Boolean> params = new HashMap<String, Boolean>();
        params.put("update", update);
        params.put("skip-user-update", skipUserUpdate);
        params.put("include-audit-events", includeAuditEvents);
        params.put("include-access-events", includeAccessEvents);
        params.put("include-monitoring-events", includeMonitoringEvents);
        params.put("include-server-settings", includeSettings);

        if(log.isDebugEnabled()){
            log.debug(new StringBuffer()
                    .append("Request to start import is: \n")
                    .append(StringUtils.join(params.entrySet(), "\n"))
                    .toString()
            );
        }

        ImportRunnable importRunner = new ImportRunnable(params, stream, LocaleContextHolder.getLocale());
        importRunner.setService(synchImportExportService);
        importRunner.setMessageSource(messageSource);

        basicTaskManager.startTask(new ImportExportTask<StateDto>(importRunner));

        return Response.ok(importRunner.getState()).build();
    }


    @GET
    @Path("/{id}/state")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStateOfTheTask(@PathParam("id") final String taskId) throws RemoteException {
        StateDto taskState = basicTaskManager.getTaskState(taskId);
        if (!taskState.getPhase().equals(Task.INPROGRESS)){
            basicTaskManager.finishTask(taskId);
        }
        return Response.ok(taskState).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getTasksIds() throws RemoteException {
        return Response.ok(basicTaskManager.getTaskIds()).build();
    }

}
