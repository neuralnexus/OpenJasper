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

package com.jaspersoft.jasperserver.export.service.impl;


import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.export.ExportTaskImpl;
import com.jaspersoft.jasperserver.export.Exporter;
import com.jaspersoft.jasperserver.export.ImportTaskImpl;
import com.jaspersoft.jasperserver.export.Importer;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.ParametersImpl;
import com.jaspersoft.jasperserver.export.io.PathProcessor;
import com.jaspersoft.jasperserver.export.io.PathProcessorFactory;
import com.jaspersoft.jasperserver.export.io.ZipFileInput;
import com.jaspersoft.jasperserver.export.io.ZipFileInputManager;
import com.jaspersoft.jasperserver.export.io.ZipStreamOutput;
import com.jaspersoft.jasperserver.export.service.ExportFailedException;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.zip.Deflater;
import java.util.zip.ZipException;

/**
 * Implementation of facade on top of import/export tool
 *
 * @author ztomchenco
 */
public abstract class ImportExportServiceImpl implements ImportExportService, ZipFileInputManager, ApplicationContextAware {

    public final static String ROLES_PARAMETER = "roles";
    public final static String USERS_PARAMETER = "users";
    public final static String URIS_PARAMETER = "uris";
    public final static String URIS_OF_SCHEDULED_PARAMETER = "report-jobs";

    protected final static CommandOut log = CommandOut.getInstance();

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    private PathProcessorFactory pathProcessorFactory;
    private String propertyPathProcessorId = "pathProcessorId"; // default value
    private ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void doImport(File input, Map<String, Boolean> importParams, Locale locale) throws ImportFailedException{
        Importer importer = this.getImporter();

        String processorId = pathProcessorFactory.getDefaultInputProcessor();
        PathProcessor processor = pathProcessorFactory.getProcessor(processorId);

        ImportTaskImpl task = new ImportTaskImpl();

        task.setExecutionContext(makeExecutionContext(locale));
        task.setParameters(createImportParameters(importParams));
        task.setInput(new ZipFileInput(input, processor, this));
        task.setApplicationContext(getApplicationContext());

        startImport(importer, task);
    }

    @Override
    public void doImport(InputStream input, Map<String, Boolean> importParams, Locale locale) throws ImportFailedException {
        File tempFile = null;
        try {
            tempFile = createTmpFile(input);
            log.debug("Upload finished. Temp file created at " + tempFile.getAbsolutePath());
            doImport(tempFile, importParams, locale);
        }catch (IOException  e){
            throw new ImportFailedException(e.getMessage());
        } finally {
            if (tempFile != null){
                if (tempFile.delete()){
                    log.debug("Uploaded file was deleted from: "+ tempFile.getAbsolutePath());
                }else{
                    log.debug("Can't delete temp dir from:"+ tempFile.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void doExport(OutputStream output, Map<String, Boolean> exportParams, List<String> urisOfResources, List<String> urisOfScheduledJobs, List<String> rolesToExport, List<String> usersToExport, Locale locale) throws ExportFailedException {
        Exporter exporter = this.getExporter();

        ExportTaskImpl task = new ExportTaskImpl();

        task.setExecutionContext(makeExecutionContext(locale));
        task.setParameters(createExportParameters(exportParams,urisOfResources,urisOfScheduledJobs,rolesToExport,usersToExport));
        String processorId = pathProcessorFactory.getDefaultOutputProcessor();
        PathProcessor pathProcessor = pathProcessorFactory.getProcessor(processorId);
        Properties properties = new Properties();
        properties.setProperty(propertyPathProcessorId, processorId);
        task.setOutput(new ZipStreamOutput(output, Deflater.BEST_COMPRESSION, pathProcessor, properties));

        startExport(exporter, task);
    }

    @Override
    public void updateInputProperties(ZipFileInput input, Properties properties) {
        String pathProcessorId = properties.getProperty(propertyPathProcessorId);
        if (pathProcessorId != null && !pathProcessorId.equals(pathProcessorFactory.getDefaultInputProcessor())) {
            PathProcessor processor = pathProcessorFactory.getProcessor(pathProcessorId);
            input.setPathProcessor(processor);
        }
    }

    protected void startImport(Importer importer, ImportTaskImpl task) throws ImportFailedException {
        try{
            log.info("About to start import process");
            importer.setTask(task);
            importer.performImport();
            log.info("Import process completed successfully");
        }catch (Exception e){
            //all exceptions in import/export tool wrapperd by JSException, decompose it
            if (e.getCause() instanceof ZipException) {
                throw new ImportFailedException(localize("exception.remote.import.failed.zip.error", task.getExecutionContext().getLocale()));
            } else if (e instanceof NullPointerException) {
                //Import export tool throws null pointer exceptions when content hasn't proper structure
                throw new ImportFailedException(localize("exception.remote.import.failed.content.error", task.getExecutionContext().getLocale()));
            } else {
                throw new ImportFailedException(MessageFormat.format(localize("exception.remote.import.failed", task.getExecutionContext().getLocale()),e.getMessage()));
            }
        }
    }

    protected void startExport(Exporter exporter, ExportTaskImpl task) throws ExportFailedException {
        try{
            log.info("About to start export process");
            exporter.setTask(task);
            exporter.performExport();
            log.info("Export process completed successfully");
        }catch (JSException e){
            //convert from unchecked to checked exception
            throw new ExportFailedException(e.getLocalizedMessage());
        }
    }

    private Parameters createExportParameters(Map<String, Boolean> exportParams, List<String> urisOfResources, List<String> urisOfScheduledJobs, List<String> rolesToExport, List<String> usersToExport){
        Parameters parameters = new ParametersImpl();

        processInputList(parameters, URIS_PARAMETER, urisOfResources);
        processInputList(parameters, URIS_OF_SCHEDULED_PARAMETER, urisOfScheduledJobs);
        processInputList(parameters, ROLES_PARAMETER, rolesToExport);
        processInputList(parameters, USERS_PARAMETER, usersToExport);

        processInputMap(parameters, exportParams);

        return parameters;
    }

    private Parameters createImportParameters(Map<String, Boolean> importParams){
        Parameters parameters = new ParametersImpl();

        processInputMap(parameters, importParams);

        return parameters;
    }

    private void processInputMap(Parameters parameters, Map<String, Boolean>  map){
        if (map != null) {
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String param = it.next();
                if (map.get(param)) {
                    parameters.addParameter(param);
                }
            }
        }
    }

    private void processInputList(Parameters parameters, String parameter, List<String> list) {
        if (list != null) {
            if (list.size() > 0) {
                parameters.addParameterValues(parameter, list.toArray(new String[list.size()]));
            } else {
                parameters.addParameter(parameter);
            }
        }
    }

    private File createTmpFile(InputStream input) throws IOException{
        File tmp = File.createTempFile("tmp"+input.hashCode(), null);

        FileOutputStream fileStream = new FileOutputStream(tmp);

        byte[] buff = new byte[512];
        int read = input.read(buff);
        while (read > 0){
            fileStream.write(buff, 0, read);
            read = input.read(buff);
        }
        fileStream.flush();
        fileStream.close();

        return tmp;
    }

    private ExecutionContextImpl makeExecutionContext(Locale locale){
        ExecutionContextImpl context = new ExecutionContextImpl();
        context.setLocale(locale);
        context.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);
        return context;
    }

    private String localize(String key, Locale locale){
        return messageSource.getMessage(key, null, key, locale);
    }

    public abstract Exporter getExporter();

    public abstract Importer getImporter();

    public PathProcessorFactory getPathProcessorFactory() {
        return pathProcessorFactory;
    }

    public void setPathProcessorFactory(PathProcessorFactory pathProcessorFactory) {
        this.pathProcessorFactory = pathProcessorFactory;
    }

    public String getPropertyPathProcessorId() {
        return propertyPathProcessorId;
    }

    public void setPropertyPathProcessorId(String propertyPathProcessorId) {
        this.propertyPathProcessorId = propertyPathProcessorId;
    }
}
