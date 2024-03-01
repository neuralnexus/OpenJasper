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

import com.jaspersoft.jasperserver.api.CacheDatasetException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.ExceptionMapperFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class JSExceptionWrapperMapper implements ExceptionMapper<JSExceptionWrapper> {

    @Context
    private ServiceLocator serviceLocator;

    @Override
    public Response toResponse(JSExceptionWrapper exception) {
        Exception rootException = getRootException(exception);

        ExceptionMapperFactory factory = new ExceptionMapperFactory(serviceLocator);
        ExceptionMapper em = factory.find(rootException.getClass());
        @SuppressWarnings("unchecked")
        final Response response = em.toResponse(rootException);
        return response;
    }

    private Exception getRootException(JSExceptionWrapper exceptionWrapper) {
        Exception originalException = exceptionWrapper.getOriginalException();

        if (originalException == null) return exceptionWrapper;

        if (originalException instanceof JSExceptionWrapper) {
            return getRootException((JSExceptionWrapper) originalException);
        }
        if (originalException instanceof CacheDatasetException) {
             return (Exception) ExceptionUtils.getRootCause(originalException);
        }
        return originalException;
    }

}
