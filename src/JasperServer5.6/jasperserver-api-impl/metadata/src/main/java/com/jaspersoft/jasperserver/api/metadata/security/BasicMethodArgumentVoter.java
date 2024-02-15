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
import org.springframework.security.AuthorizationServiceException;
import org.springframework.security.acl.AclEntry;
import org.springframework.security.acl.AclManager;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.common.util.Functor;

/**
 * @author Lucian Chirita
 *
 */
public class BasicMethodArgumentVoter implements MethodArgumentAclVoter {

	private static final Log log = LogFactory.getLog(BasicMethodArgumentVoter.class);

	private Class argumentType;
	private int argumentIndex = 1;
	private int[] accessPermissions;
	private Functor argumentFunctor;
	
	public boolean allow(MethodInvocation methodCall, Authentication authentication, AclManager aclManager) {
		Object secureObject = getSecureObject(methodCall);
		return accessPermitted(authentication, aclManager, secureObject);
	}

	protected Object getSecureObject(MethodInvocation methodCall) {
		Class[] types = methodCall.getMethod().getParameterTypes();
		int idx = -1;
		int cnt = 0;
		for (int i = 0; i < types.length; i++) {
			if (argumentType.isAssignableFrom(types[i])) {
				++cnt;
				if (cnt == argumentIndex) {
					idx = i;
					break;
				}
			}
		}
		
		if (idx == 0) {
			throw new AuthorizationServiceException("Argument #" + argumentIndex + " of type " + argumentType
					+ " not found in method " + methodCall.getMethod());
		}
		
		Object arg = methodCall.getArguments()[idx];
		
		if (log.isDebugEnabled()) {
			log.debug("Found argument #" + argumentIndex + " of type " + argumentType
					+ " in methdo " + methodCall.getMethod() + ": " + arg);
		}

		if (argumentFunctor != null) {
			arg = argumentFunctor.transform(arg);
			
			if (log.isDebugEnabled()) {
				log.debug("Argument transformed to " + arg);
			}
		}
		return arg;
	}

	protected boolean accessPermitted(Authentication authentication,
			AclManager aclManager, Object secureObject) {
		AclEntry[] acls = aclManager.getAcls(secureObject, authentication);
		boolean permitted = accessPermitted(acls);
		if (log.isDebugEnabled()) {
			if (permitted) {
				log.debug("Access permitted on " + secureObject + " for " + authentication);
			} else {
				log.debug("Access denied on " + secureObject + " for " + authentication);
			}
		}
		return permitted;
	}

	protected boolean accessPermitted(AclEntry[] acls) {
		boolean matches = false;
		if (acls != null && acls.length > 0) {
			for (int i = 0; i < acls.length; i++) {
				AclEntry aclEntry = acls[i];
				if (aclEntry instanceof BasicAclEntry) {
					BasicAclEntry basicAclEntry = (BasicAclEntry) aclEntry;
					if (accessPermitted(basicAclEntry)) {
						matches = true;
						break;
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Ignoring non BasicAclEntry ACL entry instance " 
								+ aclEntry);
					}
				}
			}
		}
		return matches;
	}
	
	protected boolean accessPermitted(BasicAclEntry basicAclEntry) {
		boolean access = false;
		for (int i = 0; i < accessPermissions.length; i++) {
			if (basicAclEntry.isPermitted(accessPermissions[i])) {
				if (log.isDebugEnabled()) {
					log.debug("ACL entry " + basicAclEntry + " matched permission " 
							+ accessPermissions[i] + ", access permitted");
				}
				
				access = true;
				break;
			}
		}
		return access;
	}

	public Class getArgumentType() {
		return argumentType;
	}

	public void setArgumentType(Class argumentType) {
		this.argumentType = argumentType;
	}

	public int getArgumentIndex() {
		return argumentIndex;
	}

	public void setArgumentIndex(int argumentIndex) {
		this.argumentIndex = argumentIndex;
	}

	public int[] getAccessPermissions() {
		return accessPermissions;
	}

	public void setAccessPermissions(int[] accessPermissions) {
		this.accessPermissions = accessPermissions;
	}

	public Functor getArgumentFunctor() {
		return argumentFunctor;
	}

	public void setArgumentFunctor(Functor argumentFunctor) {
		this.argumentFunctor = argumentFunctor;
	}

}
