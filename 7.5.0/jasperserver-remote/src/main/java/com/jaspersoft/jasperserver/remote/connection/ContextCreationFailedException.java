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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ContextCreationFailedException extends ErrorDescriptorException {
    private static final String ERROR_KEY_CONNECTION_FAILED = "connection.failed";

    public ContextCreationFailedException(Object errorValue, String errorField, String errorMessage,
                                     Throwable cause, SecureExceptionHandler secureExceptionHandler) {
        super(new ErrorDescriptor().setErrorCode(ERROR_KEY_CONNECTION_FAILED)
                .setMessage("Invalid context information"), cause);
        String errorItem = errorField != null ? errorField : ClientTypeUtility.extractClientType(errorValue.getClass());
        if (cause != null) {
            if(errorMessage == null)
                errorMessage = cause.getMessage() != null ? cause.getMessage() : "Context creation failed";

            ErrorDescriptor ed = secureExceptionHandler.handleException(cause, new ErrorDescriptor().setMessage(errorMessage));

            final String[] params = ed.getParameters();
            String trace = params != null && params.length > 0 ? params[0] : null;
            getErrorDescriptor().setParameters(errorValue.toString(), errorItem, ed.getMessage(), trace );
        } else {
            getErrorDescriptor().setParameters(errorValue.toString(), errorItem, errorMessage);
        }
    }

    public ContextCreationFailedException(Object connectionDescription, Throwable cause, SecureExceptionHandler secureExceptionHandler){
        this(connectionDescription, null, null, cause, secureExceptionHandler);
    }
}
