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

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: JSValidationExceptionMapper.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Provider
@Component
public class JSValidationExceptionMapper implements ExceptionMapper<JSValidationException> {
    @Override
    public Response toResponse(JSValidationException exception) {
        // raw type is used by core class. Cast is safe
        @SuppressWarnings("unchecked")
        List<ValidationError> errors = exception.getErrors().getErrors();
        List<ErrorDescriptor> errorDescriptors = new ArrayList<ErrorDescriptor>();
        for (ValidationError error : errors) {
            final List<Object> errorArgumentsList = new ArrayList<Object>();
            if (error.getErrorArguments() != null && error.getErrorArguments().length > 0) {
                errorArgumentsList.addAll(Arrays.asList(error.getErrorArguments()));
            }
            if (error.getField() != null) {
                errorArgumentsList.add(0, error.getField());
            }
            errorDescriptors.add(new ErrorDescriptor.Builder()
                    .setErrorCode(error.getErrorCode())
                    .setMessage(error.getDefaultMessage())
                    .setParameters(errorArgumentsList.toArray())
                    .getErrorDescriptor());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(new GenericEntity< List<ErrorDescriptor>>(errorDescriptors){}).build();
    }
}
