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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: ConnectionFailedException.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ConnectionFailedException extends RemoteException {
    private static final String ERROR_KEY_CONNECTION_FAILED = "connection.failed";

    public ConnectionFailedException(Object errorValue, String errorField, String errorMessage, Throwable cause) {
        super(new ErrorDescriptor.Builder().setErrorCode(ERROR_KEY_CONNECTION_FAILED)
                .setMessage("Invalid connection information")
                .getErrorDescriptor(), cause);
        String errorItem = errorField != null ? errorField : ClientTypeHelper.extractClientType(errorValue.getClass());
        if (cause != null) {
            final StringWriter trace = new StringWriter();
            final PrintWriter traceWriter = new PrintWriter(trace);
            cause.printStackTrace(traceWriter);
            String message = errorMessage;
            if(message == null){
                message = cause.getMessage() != null ? cause.getMessage() : "Connection failed";
            }
            getErrorDescriptor().setParameters(errorValue.toString(), errorItem, message, trace);
        } else {
            getErrorDescriptor().setParameters(errorValue.toString(), errorItem, errorMessage);
        }
    }

    public ConnectionFailedException(Object connectionDescription, Throwable cause){
        this(connectionDescription, null, null, cause);
    }

    public ConnectionFailedException(Object connectionDescription){
        this(connectionDescription, null, null, null);
    }
}
