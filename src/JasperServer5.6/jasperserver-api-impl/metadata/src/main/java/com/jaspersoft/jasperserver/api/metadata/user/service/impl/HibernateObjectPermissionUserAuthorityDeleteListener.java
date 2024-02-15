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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectPermissionRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateObjectPermissionUserAuthorityDeleteListener.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class HibernateObjectPermissionUserAuthorityDeleteListener implements
		HibernateDeleteListener, ApplicationContextAware {

	private String objectPermissionsServiceBeanName;

	private ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	protected ObjectPermissionServiceInternal getObjectPermissionService() {
		return (ObjectPermissionServiceInternal) context.getBean(
				getObjectPermissionsServiceBeanName(),
				ObjectPermissionServiceInternal.class);
	}
	
	public void onDelete(Object o) {
		if (o instanceof RepoUser) {
			deleteObjectPermissions((RepoUser) o);
		} else if (o instanceof RepoRole) {
			deleteObjectPermissions((RepoRole) o);
		}
	}
	
	protected void deleteObjectPermissions(IdedObject recipient) {
		ObjectPermissionRecipientIdentity recipientIdentity = new ObjectPermissionRecipientIdentity(recipient);
		getObjectPermissionService().deleteObjectPermissionsForRecipient(null, recipientIdentity);
	}

	public String getObjectPermissionsServiceBeanName() {
		return objectPermissionsServiceBeanName;
	}

	public void setObjectPermissionsServiceBeanName(
			String objectPermissionsServiceBeanName) {
		this.objectPermissionsServiceBeanName = objectPermissionsServiceBeanName;
	}

}
