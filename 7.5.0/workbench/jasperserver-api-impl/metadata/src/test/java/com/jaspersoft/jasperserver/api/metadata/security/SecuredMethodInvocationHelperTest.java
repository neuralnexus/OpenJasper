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
import org.junit.Test;
import org.springframework.security.access.AuthorizationServiceException;

import static com.jaspersoft.jasperserver.api.metadata.security.SecuredMethodInvocationHelper.getContextAsFirstArgumentOfSecureObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Volodya Sabadosh
 */
public class SecuredMethodInvocationHelperTest {

    @Test(expected = AuthorizationServiceException.class)
    public void getContextAsFirstArgumentOfSecureObject_securedObjIsNotMethodInvocation_throwException() {
        Object securedObject = mock(Object.class);
        getContextAsFirstArgumentOfSecureObject(securedObject);
    }

    @Test
    public void getContextAsFirstArgumentOfSecureObject_methodInvocationNotContainsArguments_returnNull() {
        MethodInvocation securedObject = mock(MethodInvocation.class);
        when(securedObject.getArguments()).thenReturn(null);
        assertNull(getContextAsFirstArgumentOfSecureObject(securedObject));
    }

    @Test
    public void getContextAsFirstArgumentOfSecureObject_methodInvocationNotContainsNotExecutionContext_returnNull() {
        MethodInvocation securedObject = mock(MethodInvocation.class);
        when(securedObject.getArguments()).thenReturn(new Object[]{"one", "two"});
        assertNull(getContextAsFirstArgumentOfSecureObject(securedObject));
    }

    @Test
    public void getContextAsFirstArgumentOfSecureObject_methodInvocationContainsExecutionContextAsFirstArg_returnExecutionContext() {
        MethodInvocation securedObject = mock(MethodInvocation.class);
        ExecutionContext mockExecutionContext = mock(ExecutionContext.class);
        when(securedObject.getArguments()).thenReturn(new Object[]{mockExecutionContext, "two"});
        assertEquals(mockExecutionContext, getContextAsFirstArgumentOfSecureObject(securedObject));
    }
}
