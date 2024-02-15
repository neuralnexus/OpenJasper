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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: MondrianXmlaDefinitionBean.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class MondrianXmlaDefinitionBean extends ResourceBean {

	private String catalog;
	private ResourceReferenceBean mondrianConnection;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		MondrianXMLADefinition def = (MondrianXMLADefinition) res;
		setCatalog(def.getCatalog());
		setMondrianConnection(exportHandler.handleReference(def.getMondrianConnection()));
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		MondrianXMLADefinition def = (MondrianXMLADefinition) res;
		def.setCatalog(getCatalog());
		def.setMondrianConnection(importHandler.handleReference(getMondrianConnection()));
	}

	public String getCatalog() {
		return catalog;
	}
	
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	public ResourceReferenceBean getMondrianConnection() {
		return mondrianConnection;
	}
	
	public void setMondrianConnection(ResourceReferenceBean mondrianConnection) {
		this.mondrianConnection = mondrianConnection;
	}
}
