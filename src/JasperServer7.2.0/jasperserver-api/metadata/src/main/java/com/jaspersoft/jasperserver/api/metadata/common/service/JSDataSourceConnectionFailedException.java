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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import com.jaspersoft.jasperserver.api.ErrorDescriptorHolder;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.DATASOURCE_CONNECTION_FIELD;

/**
 * @author Alexei Skorodumov askorodu@tibco.com
 */
public class JSDataSourceConnectionFailedException extends JSException implements ErrorDescriptorHolder<JSDataSourceConnectionFailedException> {
    private ErrorDescriptor errorDescriptor;

    public JSDataSourceConnectionFailedException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public JSDataSourceConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
        this.errorDescriptor = DATASOURCE_CONNECTION_FIELD.createDescriptor();
    }


    @Override
    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    @Override
    public JSDataSourceConnectionFailedException setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        this.errorDescriptor = errorDescriptor;
        return this;
    }
}
