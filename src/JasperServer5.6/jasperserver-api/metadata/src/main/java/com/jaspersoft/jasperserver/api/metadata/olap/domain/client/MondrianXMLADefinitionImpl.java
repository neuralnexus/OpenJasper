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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

/**
 * @author swood
 *
 */
public class MondrianXMLADefinitionImpl extends ResourceImpl implements
		MondrianXMLADefinition {

	private String catalog;
	private ResourceReference connection = null;

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#getCatalog()
	 */
	public String getCatalog() {
		return catalog;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#getMondrianConnection()
	 */
	public ResourceReference getMondrianConnection() {
		return connection;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setMondrianConnection(com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection)
	 */
	public void setMondrianConnection(MondrianConnection connection) {
		setMondrianConnection(new ResourceReference(connection));
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setMondrianConnection(com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference)
	 */
	public void setMondrianConnection(ResourceReference connectionReference) {
		this.connection = connectionReference;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setMondrianConnectionReference(java.lang.String)
	 */
	public void setMondrianConnectionReference(String referenceURI) {
		setMondrianConnection(new ResourceReference(referenceURI));
	}

	protected Class getImplementingItf() {
		return MondrianXMLADefinition.class;
	}

}
