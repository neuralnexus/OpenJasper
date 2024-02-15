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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.acl.AclEntry;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.acl.basic.EffectiveAclsResolver;
import org.springframework.security.userdetails.UserDetails;

import java.util.List;
import java.util.Vector;

/**
 * @author swood
 *
 */
public class ObjectPermissionEffectiveAclsResolver
	implements EffectiveAclsResolver {
	
	private static final Log logger = LogFactory.getLog(ObjectPermissionEffectiveAclsResolver.class);

	/* (non-Javadoc)
	 * @see org.springframework.security.acl.basic.EffectiveAclsResolver#resolveEffectiveAcls(org.springframework.security.acl.AclEntry[], org.springframework.security.Authentication)
	 */
    public AclEntry[] resolveEffectiveAcls(AclEntry[] allAcls,
            Authentication filteredBy) {
            if ((allAcls == null) || (allAcls.length == 0)) {
                return null;
            }

            List list = new Vector();

            if (logger.isDebugEnabled()) {
                logger.debug("Locating AclEntry[]s (from set of "
                    + ((allAcls == null) ? 0 : allAcls.length)
                    + ") that apply to Authentication: " + filteredBy);
            }

            for (int i = 0; i < allAcls.length; i++) {
                if (!(allAcls[i] instanceof BasicAclEntry)) {
                    continue;
                }

                Object recipient = ((BasicAclEntry) allAcls[i])
                    .getRecipient();

                String recipientName = null;
                String recipientTenant = null;
                if (recipient instanceof Role) {
                	recipientName = ((Role) recipient).getRoleName();
                    recipientTenant = ((Role) recipient).getTenantId();
                } else if (recipient instanceof User) {
                	recipientName = ((User) recipient).getUsername();
                    recipientTenant = ((User) recipient).getTenantId();
                }
                // Allow the Authentication's getPrincipal to decide whether
                // the presented recipient is "equal" (allows BasicAclDaos to
                // return Strings rather than proper objects in simple cases)
                if (filteredBy.getPrincipal().equals(recipient)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Principal matches AclEntry recipient: "
                            + recipient);
                    }

                    list.add(allAcls[i]);
                } else if (filteredBy.getPrincipal() instanceof UserDetails
                    && filteredBy.getPrincipal() instanceof TenantQualified
                    && ((UserDetails) filteredBy.getPrincipal()).getUsername().equals(recipientName)) {

                    String filteredTenantId = ((TenantQualified) filteredBy.getPrincipal()).getTenantId();
                    if ( filteredTenantId == null || recipientTenant==null ?
                            recipientTenant == null : filteredTenantId.endsWith(recipientTenant)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                "Principal (from UserDetails) matches AclEntry recipient: "
                                + recipient);
                        }

                        list.add(allAcls[i]);
                    }
                } else {
                    // No direct match against principal; try each authority.
                    // As with the principal, allow each of the Authentication's
                    // granted authorities to decide whether the presented
                    // recipient is "equal"
                    GrantedAuthority[] authorities = filteredBy.getAuthorities();

                    if ((authorities == null) || (authorities.length == 0)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                "Did not match principal and there are no granted authorities, so cannot compare with recipient: "
                                + recipient);
                        }

                        continue;
                    }

                    for (int k = 0; k < authorities.length; k++) {
                        boolean canAddAcls = false;

                        if (authorities[k] instanceof TenantQualified) {
                            String tenantId = ((TenantQualified)authorities[k]).getTenantId();
                            canAddAcls = authorities[k].getAuthority().equals((recipientTenant==null) ? recipientName : recipientName + "|" + recipientTenant)
                                    && (recipientTenant == null ? tenantId == null : recipientTenant.equals(tenantId)); 
                        } else {
                            canAddAcls = authorities[k].getAuthority().equals(recipientName);
                        }
                        
                        if (canAddAcls) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("GrantedAuthority: " + authorities[k]
                                    + " matches recipient: " + recipient);
                            }

                            list.add(allAcls[i]);
                        }
                    }
                }
            }

            // return null if appropriate (as per interface contract)
            if (list.size() > 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Returning effective AclEntry array with "
                        + list.size() + " elements");
                }

                return (BasicAclEntry[]) list.toArray(new BasicAclEntry[] {});
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                        "Returning null AclEntry array as zero effective AclEntrys found");
                }

                return null;
            }
        }

}
