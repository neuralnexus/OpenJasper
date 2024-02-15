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

package com.jaspersoft.jasperserver.api.metadata.security;

import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.acl.AclManager;
import org.springframework.security.vote.AccessDecisionVoter;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Lucian Chirita
 *
 */
public class MultiAclEntryVoter implements AccessDecisionVoter {

	private ConfigAttribute configAttribute;
	private AclManager aclManager;
	private MethodArgumentAclVoter[] argumentVoters;
	
	public boolean supports(ConfigAttribute attribute) {
		return configAttribute.equals(attribute);
	}

	public boolean supports(Class clazz) {
		return MethodInvocation.class.isAssignableFrom(clazz);
	}

	public int vote(Authentication authentication, Object object,
			ConfigAttributeDefinition config) {
		int access;
		if (config.contains(configAttribute)) {
			MethodInvocation call = (MethodInvocation) object;
			access = ACCESS_GRANTED;
			for (int i = 0; i < argumentVoters.length; i++) {
				MethodArgumentAclVoter voter = argumentVoters[i];
				if (!voter.allow(call, authentication, aclManager)) {
					access = ACCESS_DENIED;
					break;
				}
			}
		} else {
			access = ACCESS_ABSTAIN;
		}
		return access;
	}

	public ConfigAttribute getConfigAttribute() {
		return configAttribute;
	}

	public void setConfigAttribute(ConfigAttribute configAttribute) {
		this.configAttribute = configAttribute;
	}

	/**
	 * @return Returns the argumentVoters.
	 */
	public MethodArgumentAclVoter[] getArgumentVoters() {
		return argumentVoters;
	}

	/**
	 * @param argumentVoters The argumentVoters to set.
	 */
	public void setArgumentVoters(MethodArgumentAclVoter[] argumentVoters) {
		this.argumentVoters = argumentVoters;
	}

	/**
	 * @return Returns the aclManager.
	 */
	public AclManager getAclManager() {
		return aclManager;
	}

	/**
	 * @param aclManager The aclManager to set.
	 */
	public void setAclManager(AclManager aclManager) {
		this.aclManager = aclManager;
	}

}
