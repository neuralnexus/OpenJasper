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
package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.remote.exception.ErrorDescriptorBuildingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: GenericExceptionMapper.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Provider
@Component
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    private static final Log log = LogFactory.getLog(GenericExceptionMapper.class);

    @Resource
    private ErrorDescriptorBuildingService errorDescriptorBuildingService;

    public Response toResponse(Exception exception) {
        log.error("Unexpected error occurs", exception);
        Response response;
        if (exception instanceof WebApplicationException)
            response = ((WebApplicationException) exception).getResponse();
        else
            response = Response.serverError().entity(errorDescriptorBuildingService.buildErrorDescriptor(exception)).build();
        return response;
    }
}
