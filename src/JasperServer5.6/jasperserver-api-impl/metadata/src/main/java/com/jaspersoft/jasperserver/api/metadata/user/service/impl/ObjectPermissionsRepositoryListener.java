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

import com.jaspersoft.jasperserver.api.common.util.UpgradeRunMonitor;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerSupport;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceCopiedEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ObjectPermissionsRepositoryListener.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ObjectPermissionsRepositoryListener extends RepositoryEventListenerSupport implements RepositoryListener {

	private ObjectPermissionServiceInternal permissionsService;
	
	public void onResourceDelete(Class resourceItf, String resourceURI) {
        // We do not remove permissions when upgrading because the new resources will be created with the same URI and
        // they will have the same permissions.
        if (!UpgradeRunMonitor.isUpgradeRun()) {
            getPermissionsService().deleteObjectPermissionForRepositoryPath(null, resourceURI);
        }
	}

	public void onFolderDelete(String folderURI) {
		getPermissionsService().deleteObjectPermissionForRepositoryPath(null, folderURI);
	}

	public ObjectPermissionServiceInternal getPermissionsService() {
		return permissionsService;
	}

	public void setPermissionsService(ObjectPermissionServiceInternal permissionsService) {
		this.permissionsService = permissionsService;
	}

	public void folderMoved(FolderMoveEvent folderMove) {
		getPermissionsService().updateObjectPermissionRepositoryPath(
				folderMove.getOldFolderURI(), folderMove.getNewFolderURI());
	}

	public void resourceMoved(ResourceMoveEvent resourceMove) {
		getPermissionsService().updateObjectPermissionRepositoryPath(
				resourceMove.getOldResourceURI(), resourceMove.getNewResourceURI());
	}

	public void resourceCopied(ResourceCopiedEvent event) {
		// NOP
	}

}
