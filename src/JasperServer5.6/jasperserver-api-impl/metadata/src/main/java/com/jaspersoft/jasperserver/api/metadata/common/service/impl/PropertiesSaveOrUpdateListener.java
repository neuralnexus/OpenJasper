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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.SaveOrUpdateEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoListOfValues;

/**
 * @author udavidovich
 */
public class PropertiesSaveOrUpdateListener implements PostUpdateEventListener, PostInsertEventListener, ApplicationContextAware {
	
	protected ApplicationContext context;
	protected String propertiesManagementServiceName;
	protected PropertiesManagementService propertiesManagementService;

	public void afterSaveOrUpdate(SaveOrUpdateEvent event) {

	}

	public String getPropertiesManagementServiceName() {
		return propertiesManagementServiceName;
	}

	public void setPropertiesManagementServiceName(
			String propertiesManagementServiceName) {
		this.propertiesManagementServiceName = propertiesManagementServiceName;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		if (propertiesManagementServiceName==null) return;
		if (event.getEntity() instanceof RepoListOfValues) {
			RepoListOfValues i = (RepoListOfValues)event.getEntity();
			if (PropertiesManagementServiceImpl.RESOURCE_NAME.equals(i.getName()) &&
					PropertiesManagementServiceImpl.CONTENT_FOLDER.equals(i.getParent().getName()))
			{
				if (propertiesManagementService==null)
				{
					propertiesManagementService = context.getBean(propertiesManagementServiceName, PropertiesManagementService.class);
				}
				propertiesManagementService.reloadProperties();				
			}
		}
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		if (propertiesManagementServiceName==null) return;
		if (event.getEntity() instanceof RepoListOfValues) {
			RepoListOfValues i = (RepoListOfValues)event.getEntity();
			if (PropertiesManagementServiceImpl.RESOURCE_NAME.equals(i.getName()) &&
					PropertiesManagementServiceImpl.CONTENT_FOLDER.equals(i.getParent().getName()))
			{
				if (propertiesManagementService==null)
				{
					propertiesManagementService = context.getBean(propertiesManagementServiceName, PropertiesManagementService.class);
				}
				propertiesManagementService.reloadProperties();				
			}
		}		
	}
	
}
