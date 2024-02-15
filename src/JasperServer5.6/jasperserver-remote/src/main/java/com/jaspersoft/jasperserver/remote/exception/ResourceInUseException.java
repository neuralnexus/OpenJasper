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

package com.jaspersoft.jasperserver.remote.exception;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ResourceInUseException extends AccessDeniedException {
    public static final String ERROR_CODE  = "resource.in.use";

    public ResourceInUseException(String message, String... parameters) {
        super(message, parameters);
        this.getErrorDescriptor().setErrorCode(ERROR_CODE);
    }

    public ResourceInUseException(List<ResourceLookup> dependentResources) {
        super("Operation cannot be performed: resource in use");
        this.getErrorDescriptor().setErrorCode(ERROR_CODE);

        String[] uris = new String[dependentResources.size()];
        for (int i = 0; i<uris.length; i++){
            uris[i] = dependentResources.get(i).getURIString();
        }

        this.getErrorDescriptor().setParameters(uris);
    }
}
