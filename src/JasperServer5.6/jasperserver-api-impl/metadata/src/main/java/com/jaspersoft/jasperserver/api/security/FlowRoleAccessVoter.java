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

import java.util.Iterator;

import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.vote.AbstractAclVoter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FlowRoleAccessVoter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class FlowRoleAccessVoter extends AbstractAclVoter {
	
    private FlowDefinitionSource flowDefinitionSource;
    private String flowAccessAttribute;
    
	public FlowRoleAccessVoter() {
		super();
		
		setProcessDomainObjectClass(String.class);
	}

	public boolean supports(ConfigAttribute attribute) {
		String attr = attribute.getAttribute();
		return attr != null && attr.equals(getFlowAccessAttribute());
	}

	public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config) {
		int result = ACCESS_ABSTAIN;
		
		for (Iterator it = config.getConfigAttributes().iterator(); it.hasNext(); ) {
			ConfigAttribute attribute = (ConfigAttribute) it.next();
			if (supports(attribute)) {
				result = voteFlowAccess(authentication, object);
			}
		}
		
		return result;
	}

	protected int voteFlowAccess(Authentication authentication, Object object) {
		int result;
		
		String flowId = (String) getDomainObjectInstance(object);		
		if (flowId == null) {
			result = ACCESS_ABSTAIN;
		} else {
	        result = ACCESS_DENIED;
			
			ConfigAttributeDefinition flowAttributes = getFlowDefinitionSource().getAttributes(flowId);
	        for (Iterator iter = flowAttributes.getConfigAttributes().iterator(); iter.hasNext(); ) {
	            ConfigAttribute attribute = (ConfigAttribute) iter.next();

				if (matchesAuthority(authentication, attribute.getAttribute())) {
					result = ACCESS_GRANTED;
					break;
				}
	        }
		}
        
		return result;
	}

	protected boolean matchesAuthority(Authentication authentication, String attribute) {
		GrantedAuthority[] authorities = authentication.getAuthorities();
		boolean matches = false;
		for (int i = 0; i < authorities.length; i++) {
		    if (attribute.equals(authorities[i].getAuthority())) {
		    	matches = true;
		    	break;
		    }
		}
		return matches;
	}

	public FlowDefinitionSource getFlowDefinitionSource() {
		return flowDefinitionSource;
	}

	public void setFlowDefinitionSource(FlowDefinitionSource flowDefinitionSource) {
		this.flowDefinitionSource = flowDefinitionSource;
	}

	public String getFlowAccessAttribute() {
		return flowAccessAttribute;
	}

	public void setFlowAccessAttribute(String flowAccessAttribute) {
		this.flowAccessAttribute = flowAccessAttribute;
	}

}
