/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.Locale;

public class AuditAccessTransferCommandImpl implements CommandBean, ApplicationContextAware {

    private static final CommandOut commandOut = CommandOut.getInstance();

    private ApplicationContext ctx;

    private String exporterPrototypeBeanName;

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    public void process(Parameters parameters) {
        Exporter exporter = createPrototypeExporter(parameters);
        exporter.performExport();
    }

    protected ExecutionContext getExecutionContext(Parameters parameters) {
        ExecutionContextImpl context = new ExecutionContextImpl();
        context.setLocale(getLocale(parameters));
        return context;
    }

    protected Locale getLocale(Parameters parameters) {
        return Locale.getDefault();
    }

    protected Exporter createPrototypeExporter(Parameters parameters) {
        String exporterBeanName = getExporterPrototypeBeanName(parameters);

        commandOut.debug("Using " + exporterBeanName + " exporter prototype bean.");

        return (Exporter) ctx.getBean(exporterBeanName, Exporter.class);
    }

    protected String getExporterPrototypeBeanName(Parameters parameters) {
        return getExporterPrototypeBeanName();
    }

    public String getExporterPrototypeBeanName() {
        return exporterPrototypeBeanName;
    }

    public void setExporterPrototypeBeanName(String exporterPrototypeBeanName) {
        this.exporterPrototypeBeanName = exporterPrototypeBeanName;
    }
}
