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

import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.BasicAclEntry;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;

/**
 * @author swood
 *
 */
public interface AclService {

    public BasicAclEntry[] getAcls(InternalURI res);
    
    /**
     * Used with String based URIs
     */
    public BasicAclEntry[] getAcls(String resUri); 

	/**
	 * returns the ACLs for a specific object and a specific recipient
	 * @param aclObjectIdentity the object for which ACLs are returned
	 * @param recipient the recipient for which ACLs are returned
	 * @return an array of ACLs
	 */
    public BasicAclEntry[] getAcls(AclObjectIdentity aclObjectIdentity, Object recipient);

}
