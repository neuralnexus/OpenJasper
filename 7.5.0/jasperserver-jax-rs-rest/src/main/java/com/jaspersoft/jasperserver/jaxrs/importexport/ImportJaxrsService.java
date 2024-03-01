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
package com.jaspersoft.jasperserver.jaxrs.importexport;

import com.jaspersoft.jasperserver.api.common.crypto.Hexer;
import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.importexport.ImportTask;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.remote.services.async.ImportExportTask;
import com.jaspersoft.jasperserver.remote.services.async.ImportRunnable;
import com.jaspersoft.jasperserver.remote.services.async.Task;
import com.jaspersoft.jasperserver.remote.services.async.TasksManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.getRuntimeExecutionContext;
import static com.jaspersoft.jasperserver.export.service.ImportExportService.SECRET_KEY;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.join;

/**
 * @author Zakhar.Tomchenco
 */

@Component
@Path("/import")
@Scope("prototype")
public class ImportJaxrsService {
    private static final Log log = LogFactory.getLog(ImportJaxrsService.class);

    @Resource
    private ImportExportService synchImportExportService;

    @Resource
    private TasksManager basicTaskManager;

    @Resource(name="messageSource")
    private MessageSource messageSource;

    @Resource(name = "concreteRepository")
    protected RepositoryService repository;

    @Resource(name = "passwordEncoder")
    protected PlainCipher passwordEncoder;

    @Resource(name = "importExportCipher")
    protected PlainCipher importExportCipher;

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
            @DefaultValue("true")@FormDataParam("include-server-settings") Boolean includeSettings,
            @DefaultValue("true")@FormDataParam(ImportExportService.SKIP_THEMES) Boolean skipThemes,
            @FormDataParam(ImportExportServiceImpl.ORGANIZATION) String organizationId,
            @DefaultValue("fail")@FormDataParam(ImportExportService.BROKEN_DEPENDENCIES) String strategy,
            @DefaultValue("true")@FormDataParam(ImportExportService.MERGE_ORGANIZATION) Boolean mergeOrganization,
            @DefaultValue("")@FormDataParam("secret-key") String secretKey,
            @DefaultValue("")@FormDataParam("secretUri") String secretUri
    ) throws Exception {

        // In case of form submission from browser we for NULL if param is absent and FALSE if it set
        // (because browsers put value 'on' or empty string and jersey parses it to FALSE)
        // That is why @DefaultValue("true") is set and also we have to negate values
        return handleUpload(stream, !update, !skipUserUpdate, !includeAuditEvents, !includeAccessEvents,
                !includeMonitoringEvents, !includeSettings, !skipThemes, organizationId, !mergeOrganization, strategy, secretKey, secretUri);

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
            @DefaultValue("false")@QueryParam("includeServerSettings") Boolean includeSettings,
            @DefaultValue("false")@QueryParam("skipThemes") Boolean skipThemes,
            @QueryParam(ImportExportServiceImpl.ORGANIZATION) String organizationId,
            @DefaultValue("fail")@QueryParam("brokenDependencies") String strategy,
            @DefaultValue("false")@QueryParam("mergeOrganization") Boolean mergeOrganization,
            @DefaultValue("")@QueryParam("secretKey") String secretKey,
            @DefaultValue("")@QueryParam("secretUri") String secretUri) throws Exception {

        return handleUpload(stream, update, skipUserUpdate, includeAuditEvents, includeAccessEvents,
                includeMonitoringEvents, includeSettings, skipThemes, organizationId, mergeOrganization, strategy, secretKey, secretUri);
    }

    protected Response handleUpload(InputStream stream, Boolean update, Boolean skipUserUpdate,
                                    Boolean includeAuditEvents,Boolean includeAccessEvents,
                                    Boolean includeMonitoringEvents,Boolean includeSettings,
                                    Boolean skipThemes,
                                    String organizationId,
                                    Boolean mergeOrganization,
                                    String brokenDependenciesStrategy,
                                    String secretKey, String secretUri) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("update", String.valueOf(update));
        params.put("skip-user-update", String.valueOf(skipUserUpdate));
        params.put("include-audit-events", String.valueOf(includeAuditEvents));
        params.put("include-access-events", String.valueOf(includeAccessEvents));
        params.put("include-monitoring-events", String.valueOf(includeMonitoringEvents));
        params.put("include-server-settings", String.valueOf(includeSettings));
        params.put(ImportExportService.SKIP_THEMES, String.valueOf(skipThemes));
        params.put(ImportExportService.MERGE_ORGANIZATION, String.valueOf(mergeOrganization));

        String sKey = secretKey.trim();
        String sUri = secretUri.trim();

        final Response invalidKeyLength = Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDescriptor()
                        .setErrorCode("import.invalid.secretKey.length")
                        .addProperties(new ClientProperty("secretKey", sKey))).build();

        if (!sKey.isEmpty()) {
            try {
                if (!isValid(sKey)) {
                    return invalidKeyLength;
                }

                params.put(SECRET_KEY, sKey);
            } catch (Exception e) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDescriptor()
                                .setErrorCode("import.invalid.secretKey")
                                .addProperties(new ClientProperty("secretKey", sKey))).build();
            }


        } else if (!sUri.isEmpty()) {
            final String content;
            try {
                FileResourceData resourceData = repository.getResourceData(getRuntimeExecutionContext(), sUri);
                content = IOUtils.toString(resourceData.getDataStream(), UTF_8.name());
            } catch (JSResourceNotFoundException e) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDescriptor()
                                .setErrorCode("import.invalid.secretUri").setException(e)
                                .addProperties(new ClientProperty("secretUri", sUri)))
                        .build();
            }
            try {
                final String key = passwordEncoder.decode(content.trim());

                if (!isValid(key)) {
                    return invalidKeyLength;
                }
                params.put(SECRET_KEY, key);
            } catch (Exception e) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDescriptor()
                                .setErrorCode("import.invalid.secretUri.secretFile").setException(e)
                                .addProperties(new ClientProperty("secretUri", sUri)))
                        .build();
            }
        }

        if(log.isDebugEnabled()) { log.debug("Request to start import is: \n" + join(params.entrySet(), "\n")); }

        ImportRunnable importRunner = new ImportRunnable(params, stream, LocaleContextHolder.getLocale());
        importRunner.setService(synchImportExportService);
        importRunner.setMessageSource(messageSource);
        importRunner.setOrganizationId(organizationId);
        importRunner.setBrokenDependenciesStrategy(brokenDependenciesStrategy);

        basicTaskManager.startTask(new ImportExportTask<>(importRunner));

        return Response.ok(importRunner.getState()).build();
    }

    private boolean isValid(String sKey) throws Exception {
        final byte[] key = Hexer.parse(sKey);// parsing just to validate

        final String transformation = importExportCipher.getCipherTransformation();
        return
                (transformation.startsWith("AES") && (key.length == 16 || key.length == 24 || key.length == 32))
                        || (transformation.startsWith("DESede") && (key.length == 14 || key.length == 21));
    }

    @GET
    @Path("/{id}/state")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStateOfTheTask(@PathParam("id") final String taskId) throws ErrorDescriptorException {
        State taskState = basicTaskManager.getTaskState(taskId);
        if (!taskState.getPhase().equals(Task.INPROGRESS) && !taskState.getPhase().equals(Task.PENDING)){
            basicTaskManager.finishTask(taskId);
        }
        return Response.ok(taskState).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getTasksIds() throws ErrorDescriptorException {
        return Response.ok(basicTaskManager.getTaskIds()).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getParamsOfTheTask(@PathParam("id") final String taskId) throws ErrorDescriptorException {
        Task task = basicTaskManager.getTask(taskId);

        ImportTask taskDto = new ImportTask();
        taskDto.setOrganization(task.getOrganizationId());
        taskDto.setBrokenDependencies(task.getBrokenDependenciesStrategy());

        Map parameters = task.getParameters();
        if (parameters == null) {
            taskDto.setParameters(null);
        } else {
            List<String> parametersList = new ArrayList<String>();
            for (Object key : parameters.keySet()) {
                Object value = parameters.get(key);
                if (key instanceof String) {
                    if (value instanceof Boolean) {
                        if ((Boolean) value) {
                            parametersList.add((String) key);
                        }
                    } else if (value instanceof String) {
                        if (Boolean.parseBoolean((String) value)) {
                            parametersList.add((String) key);
                        }
                    }
                }
            }
            taskDto.setParameters(parametersList.isEmpty() ? null : parametersList);
        }

        return Response.ok(taskDto).build();
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putTask(ImportTask taskDto, @PathParam("id") String taskId)  throws ErrorDescriptorException {
        Task task = basicTaskManager.getTask(taskId);
        task.updateTask(taskDto.getParameters(), taskDto.getOrganization(), taskDto.getBrokenDependencies());
        basicTaskManager.restartTask(task);

        return Response.ok(task.getState()).build();
    }

    @DELETE
    @Path("/{id}")
    public Response cancel(@PathParam("id")String id){
        basicTaskManager.finishTask(id);

        return Response.noContent().build();
    }
}
