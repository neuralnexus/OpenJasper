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
 * @author Yaroslav.Kovalchyk
 * @version $Id: NoSuchImportTaskException.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class NoSuchImportTaskException extends RemoteException {
    public static final String ERROR_CODE_NO_SUCH_IMPORT_PROCESS = "no.such.import.process";

    public NoSuchImportTaskException(String taskId) {
        super(new ErrorDescriptor.Builder()
                .setErrorCode(ERROR_CODE_NO_SUCH_IMPORT_PROCESS)
                .setMessage("No export task with id " + taskId)
                .setParameters(taskId)
                .getErrorDescriptor());
    }
}
