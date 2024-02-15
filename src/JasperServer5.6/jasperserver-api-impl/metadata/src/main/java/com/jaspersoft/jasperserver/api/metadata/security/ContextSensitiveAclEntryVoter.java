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

import java.util.Iterator;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.AuthorizationServiceException;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.acl.AclEntry;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.acl.basic.SimpleAclEntry;
import org.springframework.security.vote.AccessDecisionVoter;
import org.springframework.security.vote.BasicAclEntryVoter;
import org.springframework.util.Assert;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.PermissionOverride;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * This differs from the BasicAclEntryVoter by being able to vary the required permissions 
 * if there is a PermissionsOverride object in the execution context.
 * The PermissionsOverride has a string id value, which is used to look up a different value for the requiredPermissions array
 * 
 * @author bob
 *
 */
public class ContextSensitiveAclEntryVoter extends BasicAclEntryVoter {
	public static final String DEFAULT_PERMS = "default";
    private static final Log logger = LogFactory.getLog(ContextSensitiveAclEntryVoter.class);
    
    private Map<String, int[]> requiredPermissionsMap;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getProcessConfigAttribute(), "A processConfigAttribute is mandatory");
        Assert.notNull(getAclManager(), "An aclManager is mandatory");

        if ((requiredPermissionsMap == null) || (requiredPermissionsMap.size() == 0)) {
            throw new IllegalArgumentException("One or more requiredPermissionsMap entries are mandatory");
        }
    }

    protected int[] getEffectivePermissions(Object secureObject) {
        Object[] args;

        if (secureObject instanceof MethodInvocation) {
            MethodInvocation invocation = (MethodInvocation) secureObject;
            args = invocation.getArguments();
        } else {
            throw new AuthorizationServiceException(
            		"Secure object: " + secureObject + " is not a MethodInvocation");
        }
        
        // ex context is always the first argument
        ExecutionContext ctx = null;
        PermissionOverride permOverride = null;
        if (args[0] instanceof ExecutionContext) {
        	ctx = (ExecutionContext) args[0];
        }
        if (ctx != null && ctx.getAttributes() != null) {
        	for (Object a : ctx.getAttributes()) {
        		if (a instanceof PermissionOverride) {
        			permOverride = (PermissionOverride) a;
        		}
        	}
        }
        String permsId = DEFAULT_PERMS;
        // if there is a perm override, use its id to look up required perms in the override map
        if (permOverride != null && requiredPermissionsMap != null && requiredPermissionsMap.containsKey(permOverride.getOverrideId())) {
        	permsId = permOverride.getOverrideId();
        }
        int[] perms = requiredPermissionsMap.get(permsId);
    	if (logger.isDebugEnabled()) {
    		logger.debug("looked up perms for " + permsId + ": " + perms);
    	}
        if (perms == null) {
        	throw new IllegalArgumentException("perms not found for key " + permsId);
        }
        return perms;
    }
    
	public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config) {
		int[] effectivePerms = getEffectivePermissions(object);
        Iterator iter = config.getConfigAttributes().iterator();

        while (iter.hasNext()) {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr)) {
                // Need to make an access decision on this invocation
                // Attempt to locate the domain object instance to process
                Object domainObject = getDomainObjectInstance(object);
                
            	if (logger.isDebugEnabled()) {
                    MethodInvocation invocation = (MethodInvocation) object;
                    StringBuffer msg = new StringBuffer("calling " + invocation.getMethod().getName());
                    if (domainObject instanceof String) {
                    	msg.append(" on " + domainObject);
                    }
                    logger.debug(msg);
            	}


                // If domain object is null, vote to abstain
                if (domainObject == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Voting to abstain - domainObject is null");
                    }

                    return AccessDecisionVoter.ACCESS_ABSTAIN;
                }

                // special case is that perms are zero and we always grant (just for debug)
                if (effectivePerms[0] == SimpleAclEntry.NOTHING) {
                	if (logger.isDebugEnabled()) {
                		logger.debug("no required perms, granting access");
                	}
                	return AccessDecisionVoter.ACCESS_GRANTED;
                }
                // Obtain the ACLs applicable to the domain object
                AclEntry[] acls = getAclManager().getAcls(domainObject, authentication);

                // If principal has no permissions for domain object, deny
                if ((acls == null) || (acls.length == 0)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Voting to deny access - no ACLs returned for this principal");
                    }

                    return AccessDecisionVoter.ACCESS_DENIED;
                }

                // Principal has some permissions for domain object, check them
                for (int i = 0; i < acls.length; i++) {
                    // Locate processable AclEntrys
                    if (acls[i] instanceof BasicAclEntry) {
                        BasicAclEntry processableAcl = (BasicAclEntry) acls[i];

                        // See if principal has any of the required permissions
                        for (int y = 0; y < effectivePerms.length; y++) {
                            if (processableAcl.isPermitted(effectivePerms[y])) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Voting to grant access");
                                }

                                return AccessDecisionVoter.ACCESS_GRANTED;
                            }
                        }
                    }
                }

                // No permissions match
                if (logger.isDebugEnabled()) {
                    logger.debug(
                        "Voting to deny access - ACLs returned, but insufficient permissions for this principal");
                }

                return AccessDecisionVoter.ACCESS_DENIED;
            }
        }

        // No configuration attribute matched, so abstain
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

	public void setRequiredPermissionsMap(Map<String, int[]> overridePermissionsMap) {
		this.requiredPermissionsMap = overridePermissionsMap;
	}

	public Map<String, int[]> getRequiredPermissionsMap() {
		return requiredPermissionsMap;
	}



}
