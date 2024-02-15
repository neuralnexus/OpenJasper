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

import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class FolderNotFoundException extends ResourceNotFoundException {
    public static final String ERROR_CODE_FOLDER_NOT_FOUND = "folder.not.found";

    public FolderNotFoundException() {
        super();
        getErrorDescriptor().setErrorCode(ERROR_CODE_FOLDER_NOT_FOUND);
    }

    public FolderNotFoundException(String message) {
        super();
        ErrorDescriptor errorDescriptor = getErrorDescriptor();
        errorDescriptor.setErrorCode(ERROR_CODE_FOLDER_NOT_FOUND);
        errorDescriptor.setParameters(message);
        errorDescriptor.setMessage("Folder " + message + " not found or it is not a folder");
    }

    public FolderNotFoundException(String message, Throwable cause) {
        super(cause);
        getErrorDescriptor().setErrorCode(ERROR_CODE_FOLDER_NOT_FOUND);
        ErrorDescriptor errorDescriptor = getErrorDescriptor();
        errorDescriptor.setParameters(message);
        errorDescriptor.setMessage("Folder " + message + " not found or it is not a folder");
    }

    public FolderNotFoundException(Throwable cause) {
        super(cause);
        getErrorDescriptor().setErrorCode(ERROR_CODE_FOLDER_NOT_FOUND);
    }

    public FolderNotFoundException(ErrorDescriptor errorDescriptor){
        super(errorDescriptor);
    }
}
