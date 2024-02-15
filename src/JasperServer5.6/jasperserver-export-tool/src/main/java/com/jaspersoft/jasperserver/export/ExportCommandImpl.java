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

package com.jaspersoft.jasperserver.export;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.export.io.ExportImportIOFactory;
import com.jaspersoft.jasperserver.export.io.ExportOutput;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ExportCommandImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ExportCommandImpl implements CommandBean, ApplicationContextAware {
	
	private static final CommandOut commandOut = CommandOut.getInstance();

	private ApplicationContext ctx;
	
	private ExportImportIOFactory exportImportIOFactory;
	private String exporterPrototypeBeanName;

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	public void process(Parameters parameters) {
		ExportTask task = createTask(parameters);
		Exporter exporter = createPrototypeExporter(parameters);
		exporter.setTask(task);
		exporter.performExport();
	}

	protected ExportTask createTask(Parameters parameters) {
		ExportTaskImpl task = new ExportTaskImpl();
		task.setParameters(parameters);
		task.setExecutionContext(getExecutionContext(parameters));
		task.setOutput(getExportOutput(parameters));
		return task;
	}

	protected ExportOutput getExportOutput(Parameters parameters) {
		return getExportImportIOFactory().createOutput(parameters);
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

	public ExportImportIOFactory getExportImportIOFactory() {
		return exportImportIOFactory;
	}

	public void setExportImportIOFactory(ExportImportIOFactory ioFactory) {
		this.exportImportIOFactory = ioFactory;
	}

}
