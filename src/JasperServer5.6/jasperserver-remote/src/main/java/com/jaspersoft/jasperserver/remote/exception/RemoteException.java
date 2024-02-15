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
 * @author Yaroslav.Kovalchyk
 * @version $Id: RemoteException.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RemoteException extends RuntimeException {

    private ErrorDescriptor errorDescriptor;

    public RemoteException() {
        super();
        this.errorDescriptor = new ErrorDescriptor.Builder().getErrorDescriptor();
    }

    public RemoteException(String message) {
        super(message);
        this.errorDescriptor = new ErrorDescriptor.Builder().setMessage(message).getErrorDescriptor();
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
        this.errorDescriptor = new ErrorDescriptor.Builder().setMessage(message).getErrorDescriptor();
    }

    public RemoteException(Throwable cause) {
        super(cause);
        this.errorDescriptor = new ErrorDescriptor(cause);
    }

    public RemoteException(ErrorDescriptor errorDescriptor){
        super(errorDescriptor.getMessage());
        this.errorDescriptor = errorDescriptor;
    }

    public RemoteException(ErrorDescriptor errorDescriptor, Throwable e){
        super(errorDescriptor.getMessage(), e);
        this.errorDescriptor = errorDescriptor;
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    public void setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        this.errorDescriptor = errorDescriptor;
    }

    public Boolean isUnexpected(){
        return getErrorDescriptor() != null && ErrorDescriptor.ERROR_CODE_UNEXPECTED_ERROR.equals(getErrorDescriptor().getErrorCode());
    }
}
