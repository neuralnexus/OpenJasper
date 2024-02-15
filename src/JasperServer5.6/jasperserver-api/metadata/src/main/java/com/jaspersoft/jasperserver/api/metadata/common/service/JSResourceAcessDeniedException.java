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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import com.jaspersoft.jasperserver.api.JSSecurityException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;

/**
 * Exception thrown when a operation that expects to find a resource in the
 * repository at a specified path fails to locate the resource.
 * 
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: JSResourceAcessDeniedException.java 47331 2014-07-18 09:13:06Z kklein $
 * @since 1.0
 */
@JasperServerAPI
public class JSResourceAcessDeniedException extends JSSecurityException {

    ResourceReference resourceReference;

	/**
	 * Creates a resource access denied exception.
	 *
     * @param resourceReference object
	 * @param message the exception message
	 * @see com.jaspersoft.jasperserver.api.JSException#JSException(String)
	 */
	public JSResourceAcessDeniedException(ResourceReference resourceReference, String message) {
		super(message);
        this.resourceReference = resourceReference;
	}

    public JSResourceAcessDeniedException(ResourceReference resourceReference, String message, Exception cause) {
        super(message, cause);
        this.resourceReference = resourceReference;
    }

    public ResourceReference getResourceReference() {
        return resourceReference;
    }
}
