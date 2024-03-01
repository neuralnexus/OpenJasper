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
package com.jaspersoft.jasperserver.api;

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;


/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ErrorDescriptorException extends RuntimeException implements ErrorDescriptorHolder<ErrorDescriptorException> {

    private ErrorDescriptor errorDescriptor;

    public ErrorDescriptorException() {
        super();
        errorDescriptor = new ErrorDescriptor();
    }

    public ErrorDescriptorException(String message) {
        super(message);
        this.errorDescriptor = new ErrorDescriptor().setMessage(message);
    }

    public ErrorDescriptorException(String message, Throwable cause) {
        super(message, cause);
        this.errorDescriptor = new ErrorDescriptor().setMessage(message);
    }

    public ErrorDescriptorException(Throwable cause, SecureExceptionHandler exceptionHandler) {
        super(cause);
        this.errorDescriptor = exceptionHandler.handleException(cause);
    }

    public ErrorDescriptorException(ErrorDescriptor errorDescriptor){
        super(errorDescriptor.getMessage());
        this.errorDescriptor = errorDescriptor;
    }

    public ErrorDescriptorException(ErrorDescriptor errorDescriptor, Throwable e){
        super(errorDescriptor.getMessage(), e);
        this.errorDescriptor = errorDescriptor;
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    public ErrorDescriptorException setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        this.errorDescriptor = errorDescriptor;
        return this;
    }

    public Boolean isUnexpected(){
        return getErrorDescriptor() != null && ErrorDescriptor.ERROR_CODE_UNEXPECTED_ERROR.equals(getErrorDescriptor().getErrorCode());
    }
}
