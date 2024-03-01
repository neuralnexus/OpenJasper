/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ExportExecutionRejectedException extends ErrorDescriptorException {
    public static final String ERROR_CODE_EXPORT_EXECUTION_REJECTED = "export.execution.rejected";

    public ExportExecutionRejectedException(ErrorDescriptor errorDescriptor) {
        super(errorDescriptor);
        if(errorDescriptor.getErrorCode() == null){
            this.getErrorDescriptor().setErrorCode(ERROR_CODE_EXPORT_EXECUTION_REJECTED);
        }
    }

    public ExportExecutionRejectedException(String message){
        super(message);
        getErrorDescriptor().setErrorCode(ERROR_CODE_EXPORT_EXECUTION_REJECTED);
    }
}
