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
package com.jaspersoft.jasperserver.api.metadata.security;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AuthorizationServiceException;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 * @author Volodya Sabadosh
 */
class SecuredMethodInvocationHelper {
    static ExecutionContext getContextAsFirstArgumentOfSecureObject(Object secureObject) {
        Object[] args;

        if (secureObject instanceof MethodInvocation) {
            MethodInvocation invocation = (MethodInvocation) secureObject;
            args = invocation.getArguments();
        } else {
            throw new AuthorizationServiceException("Secure object: " + secureObject + " is not a MethodInvocation");
        }

        // ex context is always the first argument
        ExecutionContext context = null;
        if (isNotEmpty(args) && args[0] instanceof ExecutionContext) {
            context = (ExecutionContext) args[0];
        }
        return context;
    }
}
