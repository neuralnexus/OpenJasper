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

package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSDataSourceConnectionFailedException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.DATASOURCE_CONNECTION_FIELD;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
@Provider
@Component
public class JSDataSourceConnectionFailedExceptionMapper implements ExceptionMapper<JSDataSourceConnectionFailedException> {
    private static final Log log = LogFactory.getLog(JSDataSourceConnectionFailedExceptionMapper.class);

    @Resource
    private SecureExceptionHandler secureExceptionHandler;

    @Override
    public Response toResponse(JSDataSourceConnectionFailedException e) {
        log.error("The data source connection failed", e);
        ErrorDescriptor errorDescriptor = secureExceptionHandler.handleException(e, DATASOURCE_CONNECTION_FIELD.createDescriptor());
        return Response.status(Response.Status.BAD_REQUEST).entity(errorDescriptor).build();
    }
}
