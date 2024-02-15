/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.remote.exception;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * <p>
 * <p>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class ResourceCorruptedException extends ErrorDescriptorException {

    private final String RESOURCE_CORRUPTED_ERROR_CODE = "resource.corrupted.error";

    public ResourceCorruptedException(ErrorDescriptor errorDescriptor) {
        super(errorDescriptor);
        if(getErrorDescriptor().getErrorCode() == null) getErrorDescriptor().setErrorCode(RESOURCE_CORRUPTED_ERROR_CODE);
    }

    public ResourceCorruptedException(String message) {
        super(message);
        getErrorDescriptor().setErrorCode(RESOURCE_CORRUPTED_ERROR_CODE);
    }

}
