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

import com.jaspersoft.jasperserver.api.JSException;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRParameterDecorator.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JRParameterDecorator implements JRParameter {

	private final JRParameter decorated;
	
	public JRParameterDecorator(JRParameter decorated) {
		this.decorated = decorated;
	}
	
	public JRParameter getDecorated() {
		return decorated;
	}

	public JRExpression getDefaultValueExpression() {
		return decorated.getDefaultValueExpression();
	}

	public String getDescription() {
		return decorated.getDescription();
	}

	public String getName() {
		return decorated.getName();
	}

	public Class getValueClass() {
		return decorated.getValueClass();
	}

	public String getValueClassName() {
		return decorated.getValueClassName();
	}

	public Class getNestedType() {
		return decorated.getNestedType();
	}

	public String getNestedTypeName() {
		return decorated.getNestedTypeName();
	}

	public boolean isForPrompting() {
		return decorated.isForPrompting();
	}

	public boolean isSystemDefined() {
		return decorated.isSystemDefined();
	}

	public void setDescription(String description) {
		decorated.setDescription(description);
	}

	public JRPropertiesMap getPropertiesMap() {
		return decorated.getPropertiesMap();
	}

	public JRPropertiesHolder getParentProperties() {
		return decorated.getParentProperties();
	}

	public boolean hasProperties() {
		return decorated.hasProperties();
	}
	
	public Object clone() {
		throw new JSException("Clone not supported");
	}

}
