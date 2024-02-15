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

import com.jaspersoft.jasperserver.api.JSProfileAttributeException;
import com.jaspersoft.jasperserver.remote.exception.builders.DefaultMessageApplier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id: AsyncQueryExecutionManager.java 58175 2016-03-25 19:57:33Z nthapa $
 */
@Provider
@Component
public class JSProfileAttributeExceptionMapper implements ExceptionMapper<JSProfileAttributeException> {
    @Resource
    private DefaultMessageApplier defaultMessageApplier;

    @Override
    public Response toResponse(JSProfileAttributeException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(defaultMessageApplier.applyDefaultMessageIfNotSet(exception.getErrorDescriptor()))
                .build();
    }
}
