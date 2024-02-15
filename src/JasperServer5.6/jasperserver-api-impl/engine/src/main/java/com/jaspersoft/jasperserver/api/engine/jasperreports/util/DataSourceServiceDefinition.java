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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Set;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataSourceServiceDefinition.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class DataSourceServiceDefinition {

	private String serviceBeanName;
	private String dataSourceInterface;
	private Set supportedQueryLanguages;
	private boolean anyLanguage;

	public String getServiceBeanName() {
		return serviceBeanName;
	}

	public void setServiceBeanName(String serviceBeanName) {
		this.serviceBeanName = serviceBeanName;
	}

	public Set getSupportedQueryLanguages() {
		return supportedQueryLanguages;
	}

	public void setSupportedQueryLanguages(Set supportedQueryLanguages) {
		this.supportedQueryLanguages = supportedQueryLanguages;
	}

	public boolean isAnyLanguage() {
		return anyLanguage;
	}

	public void setAnyLanguage(boolean anyLanguage) {
		this.anyLanguage = anyLanguage;
	}

	public String getDataSourceInterface() {
		return dataSourceInterface;
	}

	public void setDataSourceInterface(String dataSourceInterface) {
		this.dataSourceInterface = dataSourceInterface;
	}

}
