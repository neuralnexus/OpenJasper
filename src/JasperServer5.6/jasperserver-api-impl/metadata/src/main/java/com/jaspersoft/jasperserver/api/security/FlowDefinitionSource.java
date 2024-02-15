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

package com.jaspersoft.jasperserver.api.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.intercept.ObjectDefinitionSource;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FlowDefinitionSource.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class FlowDefinitionSource implements ObjectDefinitionSource {

	protected static final String DEFAULT_FLOW = "*";
	
	private Map flowAttributes;
	
	public FlowDefinitionSource() {
		flowAttributes = new HashMap();
	}
	
	public void addFlow(String flowId, ConfigAttributeDefinition attributes) {
		flowAttributes.put(flowId, attributes);
	}
	
	public ConfigAttributeDefinition getAttributes(Object object) throws IllegalArgumentException {
		ConfigAttributeDefinition attributes = (ConfigAttributeDefinition) flowAttributes.get(object);
		if (attributes == null) {
			attributes = (ConfigAttributeDefinition) flowAttributes.get(DEFAULT_FLOW);
		}
		return attributes;
	}

	public Collection getConfigAttributeDefinitions() {
		return flowAttributes.values();
	}

	public boolean supports(Class clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
	}

}
