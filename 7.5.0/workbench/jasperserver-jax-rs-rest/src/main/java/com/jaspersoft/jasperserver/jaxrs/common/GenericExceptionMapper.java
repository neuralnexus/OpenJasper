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
package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.api.ErrorDescriptorHolder;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.exception.ErrorDescriptorBuildingService;
import com.jaspersoft.jasperserver.remote.exception.builders.DefaultMessageApplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.UnmarshalException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Provider
@Component
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    private static final Log log = LogFactory.getLog(GenericExceptionMapper.class);

    @Resource
    private XmlParseExceptionMapper xmlParseExceptionMapper;
    @Resource
    private ErrorDescriptorBuildingService errorDescriptorBuildingService;
    @Resource
    private DefaultMessageApplier defaultMessageApplier;

    public Response toResponse(Exception exception) {
        Response response;
        if (exception instanceof WebApplicationException) {
            response = handleWebApplicationException(exception);
        } else if(exception instanceof ErrorDescriptorHolder && ((ErrorDescriptorHolder) exception).getErrorDescriptor() != null){
            final ErrorDescriptor errorDescriptor = ((ErrorDescriptorHolder) exception).getErrorDescriptor();
            Response.Status status = Response.Status.BAD_REQUEST;
            if(ErrorDescriptor.ERROR_CODE_UNEXPECTED_ERROR.equals(errorDescriptor.getErrorCode())){
                status = Response.Status.INTERNAL_SERVER_ERROR;
                log.error("Unexpected error occurs", exception);
            }
            ErrorDescriptor descriptor = defaultMessageApplier.
                    applyDefaultMessageIfNotSet(((ErrorDescriptorHolder) exception).getErrorDescriptor(), true);
            response = Response.status(status).entity(descriptor).build();
        } else {
            log.error("Unexpected error occurs", exception);
            response = Response.serverError().entity(errorDescriptorBuildingService.buildErrorDescriptor(exception)).build();
        }
        return response;
    }

    private Response handleWebApplicationException(Exception exception) {
        if (exception.getCause() instanceof UnmarshalException) {
            return xmlParseExceptionMapper.toResponse((UnmarshalException) exception.getCause());
        }
        return ((WebApplicationException) exception).getResponse();
    }
}
