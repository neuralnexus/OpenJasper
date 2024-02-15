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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author swood
 *
 */
public class CustomReportDataSourceServiceFactory implements ReportDataSourceServiceFactory, ApplicationContextAware {
	private ApplicationContext ctx;
	private List<CustomDataSourceDefinition> customDataSourceDefs = new ArrayList<CustomDataSourceDefinition>();
	private ResourceFactory mappingResourceFactory;
	
	/**
	 * 
	 */
	public CustomReportDataSourceServiceFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ctx = arg0;
	}
	
	protected ApplicationContext getApplicationContext() {
		return ctx;
	}
	
	/**
	 * create a new CustomReportDataSource instance with default values
	 * 
	 * @param dsTypeName
	 * @return
	 */
	public CustomReportDataSource createDataSource(String dsTypeName) {
		CustomDataSourceDefinition cdsd = getDefinitionByName(dsTypeName);
		if (cdsd == null) {
			throw new IllegalArgumentException("unknown custom data source type name '" + dsTypeName + "'");
		}
		CustomReportDataSource cds = (CustomReportDataSource) mappingResourceFactory.newResource(null, CustomReportDataSource.class);
		cds.setServiceClass(cdsd.getServiceClassName());
		cds.setDataSourceName(dsTypeName);
		// fill prop map with default values
		cdsd.setDefaultValues(cds);
		return cds;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof CustomReportDataSource)) {
			throw new JSException("jsexception.invalid.custom.datasource", new Object[] {reportDataSource.getClass()});
		}
		CustomReportDataSource customDataSource = (CustomReportDataSource) reportDataSource;
		
		// look up definition
		CustomDataSourceDefinition dsDef = getDefinition(customDataSource);
		// does it have its own factory? if so, delegate to it
		if (dsDef.getCustomFactory() != null) {
			return dsDef.getCustomFactory().createService(customDataSource);
		}
		// get the service class name, look up the class, and create an instance
		String serviceClassName = customDataSource.getServiceClass();
		ReportDataSourceService service;
		try {
			Class<?> serviceClass = Class.forName(serviceClassName);
			service = (ReportDataSourceService) serviceClass.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JSException ex = new JSException("jsexception.creating.custom.datasource", e);
			ex.setArgs(new Object[] { serviceClassName });
			throw ex;
		}
		try {
			dsDef.setDataSourceServiceProperties(customDataSource, service);
		} catch (Exception e) {
			JSException ex = new JSException("jsexception.setting.custom.datasource.props", e);
			ex.setArgs(new Object[] { serviceClassName });
			throw ex;
		}
		
		return service;
	}

	/**
	 * add a definition to the list of definitions
	 */
	public void addDefinition(CustomDataSourceDefinition def) {
		customDataSourceDefs.add(def);
	}
	
	public List<CustomDataSourceDefinition> getDefinitions() {
		return customDataSourceDefs ;
	}

	/**
	 * @param serviceClass
	 * @return
	 */
	public CustomDataSourceDefinition getDefinitionByServiceClass(String serviceClass) {
		for (CustomDataSourceDefinition cds : getDefinitions()) {
			if (cds.getServiceClassName().equals(serviceClass)) {
				return cds;
			}
		}
		return null;
	}

	/**
	 * @param serviceClass
	 * @return
	 */
	public CustomDataSourceDefinition getDefinitionByName(String name) {
		for (CustomDataSourceDefinition cds : getDefinitions()) {
			if (cds.getName().equals(name)) {
				return cds;
			}
		}
		return null;
	}

	public CustomDataSourceDefinition getDefinition(CustomReportDataSource cds) {
		// look up by name if present
		if (cds.getDataSourceName() != null) {
			return getDefinitionByName(cds.getDataSourceName());
		}
		return getDefinitionByServiceClass(cds.getServiceClass());
	}

	public ResourceFactory getMappingResourceFactory() {
		return mappingResourceFactory;
	}

	public void setMappingResourceFactory(ResourceFactory mappingResourceFactory) {
		this.mappingResourceFactory = mappingResourceFactory;
	}
}
