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

import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.*;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import org.hibernate.event.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author udavidovich
 */
public class DriverDeleteListener implements PostDeleteEventListener, ApplicationContextAware {
	
	protected ApplicationContext context;
	protected String propertiesManagementServiceName;
	protected PropertiesManagementService propertiesManagementService;

    private String jdbcDriversFolder;

	public String getPropertiesManagementServiceName() {
		return propertiesManagementServiceName;
	}

	public void setPropertiesManagementServiceName(
			String propertiesManagementServiceName) {
		this.propertiesManagementServiceName = propertiesManagementServiceName;
	}

    public String getJdbcDriversFolder() {
        return jdbcDriversFolder;
    }

    public void setJdbcDriversFolder(String jdbcDriversFolder) {
        this.jdbcDriversFolder = jdbcDriversFolder;
    }

    @Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		if (propertiesManagementServiceName == null) return;

        Object entity = event.getEntity();
		if (entity instanceof RepoFileResource || entity instanceof RepoFolder) {
            RepoResourceBase file = (RepoResourceBase) entity;
            RepoFolder parent = file.getParent();


            if (parent != null) {
                if (parent.getResourceURI().equals(this.jdbcDriversFolder)) {
                    // If it's driver folder than just remove the property from global properties
                    getService().removeByValue(file.getName());
                } else {
                    // In case wen we have remove only one file or last one from the folder
                    // than we can also remove the mapping from global properties
                    RepoFolder parentOfParent = parent.getParent();

                    if (parentOfParent != null &&
                            parentOfParent.getResourceURI().equals(this.jdbcDriversFolder) &&
                            parent.getChildren().size() == 0)

                        getService().removeByValue(parent.getName());
                }
            }
		}		
	}

    private PropertiesManagementService getService() {
        if (propertiesManagementService == null) {
            propertiesManagementService = context.getBean(propertiesManagementServiceName, PropertiesManagementService.class);
        }

        return propertiesManagementService;
    }
	
}
