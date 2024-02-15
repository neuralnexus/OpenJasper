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

import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ResourceInUseException;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Provider
@Component
public class DataIntegrityViolationExceptionMapper implements ExceptionMapper<DataIntegrityViolationException> {

    @Override
    public Response toResponse(DataIntegrityViolationException exception) {
        Response.ResponseBuilder response;
        Throwable cause = exception.getCause();

        if (cause instanceof JDBCException || cause instanceof SQLException) {
            response = Response.status(isConstraintViolation(cause) ? Response.Status.FORBIDDEN : Response.Status.BAD_REQUEST)
                    .entity(new ErrorDescriptor.Builder()
                            .setErrorCode(isConstraintViolation(cause) ? ResourceInUseException.ERROR_CODE : IllegalParameterValueException.ERROR_CODE)
                            .setMessage(cause.getLocalizedMessage() + ": " + getLocalizedMessage(cause))
                            .getErrorDescriptor());
        } else {
            response = Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDescriptor.Builder()
                            .setErrorCode(IllegalParameterValueException.ERROR_CODE)
                            .setMessage(exception.getMostSpecificCause().getLocalizedMessage())
                            .getErrorDescriptor());
        }

        return response.build();
    }

    private String getLocalizedMessage(Throwable cause) {
        Iterator<Throwable> it = (cause instanceof JDBCException)
                ? ((JDBCException) cause).getSQLException().iterator()
                : ((SQLException) cause).iterator();
        Throwable throwable = null;
        while (it.hasNext() && (throwable = it.next()) instanceof SQLException);
        return throwable != null ? throwable.getLocalizedMessage() : cause.getLocalizedMessage();
    }

    private boolean isConstraintViolation(Throwable cause) {
        return (cause instanceof ConstraintViolationException) ||
                (cause.getMessage().contains("integrity")) && (cause.getMessage().contains("violate"));
    }	
}
