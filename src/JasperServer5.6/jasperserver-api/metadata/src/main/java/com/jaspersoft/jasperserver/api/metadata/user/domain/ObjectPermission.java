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
package com.jaspersoft.jasperserver.api.metadata.user.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * ObjectPermission is the interface which is used to give {@link User} or {@link Role} permission to the
 * {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Resource Resource}. The URI of the resource is used
 * to identify the resource for which permission should be set.
 *
 * @author swood
 * @version $Id: ObjectPermission.java 47331 2014-07-18 09:13:06Z kklein $
 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl
 * @since 1.0.1 
 */
@JasperServerAPI
public interface ObjectPermission {

    /**
     * Returns the URI of the resource for which permission should be set.
     *
     * @return  the URI of the resource.
     */
	public String getURI();

    /**
     * Sets the URI of the resource for which permission should be set. The URI should include resource
     *      {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Resource#URI_PROTOCOL Resource.URI_PROTOCOL}.
     *
     * @param URI the URI of the resource.   
     */
	public void setURI(String URI);

    /**
     * Returns the permission recipient object ({@link User} or {@link Role}) to which permission is given.
     *
     * @return the permission recipient object.
     */
	public Object getPermissionRecipient();

    /**
     * Sets the permission recipient object ({@link User} or {@link Role}) to which permission is given.
     *
     * @param permissionRecipient   the permission recipient object.
     */
	public void setPermissionRecipient(Object permissionRecipient);

    /**
     * Returns the permission mask. It contains permissions which are set to the resource for the specified
     * permission recipient object.
     *
     * @return the permission mask.
     */
	public int getPermissionMask();

    /**
     * Sets the permission mask which is an integer value:
     * <ul>
     * <li>0 - No Access</li>
     * <li>1 - Administer</li>
     * <li>2 - Read Only</li>
     * <li>18 - Read + Delete</li>
     * <li>30 - Read + Write + Delete</li>
     * </ul>
     *
     * @param permissionMask the permission mask.
     */
	public void setPermissionMask(int permissionMask);
}
