/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Locale;

public class ProfileAttributeImportCommandImpl implements CommandBean, ApplicationContextAware {

    private static final CommandOut commandOut = CommandOut.getInstance();

    private ApplicationContext ctx;

    private String importerPrototypeBeanName;

    public String getImporterPrototypeBeanName() {
        return importerPrototypeBeanName;
    }

    public void setImporterPrototypeBeanName(String importerPrototypeBeanName) {
        this.importerPrototypeBeanName = importerPrototypeBeanName;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    public void process(Parameters parameters) {
        try {
            ImportTask task = createTask(parameters);
            Importer importer = createPrototypeImporter(parameters);
            importer.setTask(task);
            importer.performImport();
        } catch (ImportFailedException e) {
            commandOut.error(e.getMessage());
        }
    }
    protected ImportTask createTask(Parameters parameters) {
        ImportTaskImpl task = new ImportTaskImpl();
        task.setParameters(parameters);
        task.setExecutionContext(getExecutionContext(parameters));
        task.setApplicationContext(this.ctx);
        return task;
    }

    protected ExecutionContext getExecutionContext(Parameters parameters) {
        ExecutionContextImpl context = new ExecutionContextImpl();
        context.setLocale(getLocale(parameters));
        return context;
    }

    protected Locale getLocale(Parameters parameters) {
        return Locale.getDefault();
    }

    protected Importer createPrototypeImporter(Parameters parameters) {
        String importerBeanName;
        if (parameters.hasParameter("importerBeanName")) {
            importerBeanName = parameters.getParameterValue("importerBeanName");
        } else {
            importerBeanName = getImporterPrototypeBeanName(parameters);
        }

        commandOut.debug("Using " + importerBeanName + " importer prototype bean.");

        return (Importer) ctx.getBean(importerBeanName, Importer.class);
    }

    protected String getImporterPrototypeBeanName(Parameters parameters) {
        return getImporterPrototypeBeanName();
    }

}