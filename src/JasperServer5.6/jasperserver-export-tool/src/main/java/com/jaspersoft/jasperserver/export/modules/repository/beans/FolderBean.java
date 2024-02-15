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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import java.util.Date;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

/**
 * @author tkavanagh
 * @version $Id: FolderBean.java 47331 2014-07-18 09:13:06Z kklein $
 */

public class FolderBean {

	/*
	 * The following come from the Resource interface 
	 */
	private String name;
	private String label;
	private String description;
	private String parent;
	private Date creationDate;
	private Date updateDate;
	private String[] subFolders;
	private String[] resources;
	private RepositoryObjectPermissionBean[] permissions;

    /*
    *  Export specific data
    */
    private boolean exportedWithPermissions;
	
	public void copyFrom(Folder folder) {
		setName(folder.getName());
		setLabel(folder.getLabel());
		setDescription(folder.getDescription());
		setParent(folder.getParentFolder());
		setCreationDate(folder.getCreationDate());
		setUpdateDate(folder.getUpdateDate());
	}
	
	public void copyTo(Folder folder) {
		folder.setName(getName());
		folder.setLabel(getLabel());
		folder.setDescription(getDescription());
		folder.setParentFolder(getParent());
	}
	
	/*
	 * getters and setters
	 */
	
	public String getName()	{
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String folderUri) {
		this.parent = folderUri;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String[] getResources() {
		return resources;
	}

	public void setResources(String[] resources) {
		this.resources = resources;
	}

	public String[] getSubFolders() {
		return subFolders;
	}

	public void setSubFolders(String[] subFolders) {
		this.subFolders = subFolders;
	}

	public RepositoryObjectPermissionBean[] getPermissions() {
		return permissions;
	}

	public void setPermissions(RepositoryObjectPermissionBean[] permissions) {
		this.permissions = permissions;
	}

    public boolean isExportedWithPermissions() {
        return exportedWithPermissions;
    }

    public void setExportedWithPermissions(boolean exportedWithPermissions) {
        this.exportedWithPermissions = exportedWithPermissions;
    }
}
