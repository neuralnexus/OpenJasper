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
package com.jaspersoft.jasperserver.war.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;

public class FileResourceWrapper extends BaseDTO implements Serializable {
	private FileResource fileResource;

	private Resource resource;

	private byte[] newData;

	private String newUri;

	private Map allTypes;

	private boolean suggested;

	private List allResources;
	
	private List existingResources;

	private String source;

	private String fileName;

	private Object parentFlowObject;

	private boolean located;
	
	private List allFolders;
	
	private String folder;

	public boolean isLocated() {
		return located;
	}

	public void setLocated(boolean located) {
		this.located = located;
	}

	public Object getParentFlowObject() {
		return parentFlowObject;
	}

	public void setParentFlowObject(Object parentFlowObject) {
		this.parentFlowObject = parentFlowObject;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List getAllResources() {
		return allResources;
	}

	public void setAllResources(List allResources) {
		this.allResources = allResources;
	}

	public boolean isSuggested() {
		return suggested;
	}

	public void setSuggested(boolean suggested) {
		this.suggested = suggested;
	}

	public Map getAllTypes() {
		//TODO remove this as we also have ConfigurationBean.getAllFileResourceTypes
		if (allTypes == null) {
			allTypes = new LinkedHashMap();
			allTypes.put(FileResource.TYPE_IMAGE,
					JasperServerConst.TYPE_RSRC_IMAGE);
			allTypes.put(FileResource.TYPE_FONT,
					JasperServerConst.TYPE_RSRC_FONT);
			allTypes.put(FileResource.TYPE_JAR,
					JasperServerConst.TYPE_RSRC_CLASS_JAR);
			allTypes.put(FileResource.TYPE_JRXML,
					JasperServerConst.TYPE_RSRC_SUB_REPORT);
			allTypes.put(FileResource.TYPE_RESOURCE_BUNDLE,
					JasperServerConst.TYPE_RSRC_RESOURCE_BUNDLE);
			allTypes.put(FileResource.TYPE_STYLE_TEMPLATE,
					JasperServerConst.TYPE_RSRC_STYLE_TEMPLATE);
			allTypes.put(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA,
					JasperServerConst.TYPE_RSRC_OLAP_SCHEMA);
			allTypes.put(ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA,
						 JasperServerConst.TYPE_RSRC_ACCESS_GRANT_SCHEMA); // pro-only
			allTypes.put(FileResource.TYPE_XML,
					JasperServerConst.TYPE_RSRC_XML_FILE);
            allTypes.put(FileResource.TYPE_CSS,
                    JasperServerConst.TYPE_RSRC_CSS_FILE);
		}
		return allTypes;
	}

	public void setAllTypes(Map allTypes) {
		this.allTypes = allTypes;
	}

	public FileResource getFileResource() {
		return fileResource;
	}

	public void setFileResource(FileResource fileResource) {
		this.fileResource = fileResource;
	}

	public void afterBind() {
		if (getSource() != null
				&& fileResource != null
				&& getSource().equals(
						JasperServerConst.FIELD_CHOICE_FILE_SYSTEM)) {
			fileResource.setReferenceURI(null);
		}
	}

	public byte[] getNewData() {
		return newData;
	}

	public void setNewData(byte[] newData) {
		if (newData != null && newData.length != 0) {
			fileResource.setData(newData);
		}
		this.newData = newData;
	}

	public String getNewUri() {
		return newUri;
	}

	public void setNewUri(String newUri) {
		if (newUri != null && newUri.trim().length() != 0)
			fileResource.setReferenceURI(newUri);
		this.newUri = newUri;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public List getExistingResources() {
		return existingResources;
	}

	public void setExistingResources(List existingResources) {
		this.existingResources = existingResources;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public List getAllFolders() {
		return allFolders;
	}

	public void setAllFolders(List allFolders) {
		this.allFolders = allFolders;
	}
}
