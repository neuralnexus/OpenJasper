/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war.dto;

import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

import java.util.List;

public class MondrianXmlaSourceWrapper extends BaseDTO {
	private MondrianXMLADefinition mondrianXmlaDefinition;
	private String connectionUri;
	private List allMondrianConnections;
	private boolean connectionInvalid;

	/**
	 * MondrianXmlaSourceWrapper provides wrapper for the
	 * EditMondrianXmlaSourceAction
	 * 
	 * @param mondrianXmlaDefinition
	 */
	public MondrianXmlaSourceWrapper(
			MondrianXMLADefinition mondrianXmlaDefinition) {
		this.mondrianXmlaDefinition = mondrianXmlaDefinition;
		if (mondrianXmlaDefinition.getMondrianConnection() != null)
			this.connectionUri = mondrianXmlaDefinition.getMondrianConnection()
					.getReferenceURI();
	}

	public MondrianXMLADefinition getMondrianXmlaDefinition() {
		return mondrianXmlaDefinition;
	}

	public void setMondrianXmlaDefinition(
			MondrianXMLADefinition mondrianXmlaDefinition) {
		this.mondrianXmlaDefinition = mondrianXmlaDefinition;
	}

	public String getConnectionUri() {
		return connectionUri;
	}

	public void setConnectionUri(String connectionUri) {
		this.connectionUri = connectionUri;
	}

	public boolean isConnectionInvalid() {
		return connectionInvalid;
	}

	public void setConnectionInvalid(boolean connectionInvalid) {
		this.connectionInvalid = connectionInvalid;
	}

	public List getAllMondrianConnections() {
		return allMondrianConnections;
	}

	public void setAllMondrianConnections(List allMondrianConnections) {
		this.allMondrianConnections = allMondrianConnections;
	}
}
