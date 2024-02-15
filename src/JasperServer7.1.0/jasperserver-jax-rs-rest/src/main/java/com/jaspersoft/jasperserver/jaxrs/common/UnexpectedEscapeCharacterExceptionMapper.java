/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.connection.datadiscovery.UnexpectedEscapeCharacterException;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Provider
@Component
public class UnexpectedEscapeCharacterExceptionMapper implements ExceptionMapper<UnexpectedEscapeCharacterException> {
    @Override
    public Response toResponse(UnexpectedEscapeCharacterException exception) {
        final ErrorDescriptor errorDescriptor = new ErrorDescriptor()
                .setErrorCode("unexpected.escape.character")
                .addProperties(new ClientProperty().setKey("string").setValue(exception.getString()));
        final List<Integer> indexes = exception.getIndexes();
        for (Integer index : indexes) {
            errorDescriptor.addProperties(new ClientProperty().setKey("index").setValue(index.toString()));
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(errorDescriptor).build();
    }
}
