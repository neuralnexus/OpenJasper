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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import org.junit.Test;

import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;

import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.RESTRICTED_CONTEXT_ATTR;
import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.EXECUTE_OVERRIDE_ATTR;
import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.create;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Volodya Sabadosh
 */
public class ExecutionContextImplTest {
    private Locale locale = Locale.ENGLISH;
    private TimeZone timezone = TimeZone.getDefault();

    @Test
    public void isRestrictedRuntimeExecutionContext_contextIsNull_returnFalse() {
        assertFalse(ExecutionContextImpl.isRestrictedRuntimeExecutionContext(null));
    }

    @Test
    public void isRestrictedRuntimeExecutionContext_contextIsRestrictedRuntime_returnTrue() {
        ExecutionContext context = ExecutionContextImpl.create(null, null,
                EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);
        assertTrue(ExecutionContextImpl.isRestrictedRuntimeExecutionContext(context));
    }

    @Test
    public void isRestrictedRuntimeExecutionContext_contextIsNotRestricted_returnFalse() {
        ExecutionContext context = ExecutionContextImpl.create(null, null,
                EXECUTE_OVERRIDE_ATTR);
        assertFalse(ExecutionContextImpl.isRestrictedRuntimeExecutionContext(context));
    }

    @Test
    public void checkIfAllAttributesExist_contextIsNull_returnFalse() {
        assertFalse(ExecutionContextImpl.checkIfAllAttributesExist(null, EXECUTE_OVERRIDE_ATTR));
    }

    @Test
    public void checkIfAllAttributesExist_contextIsNullAndInputIsNull_returnTrue() {
        assertTrue(ExecutionContextImpl.checkIfAllAttributesExist(null, (Object[])null));
    }

    @Test
    public void checkIfAllAttributesExist_contextWithoutAttributesButInputRequiredOne_returnFalse() {
        ExecutionContext context = ExecutionContextImpl.create(null, null);
        assertFalse(ExecutionContextImpl.checkIfAllAttributesExist(context, EXECUTE_OVERRIDE_ATTR));
    }

    @Test
    public void checkIfAllAttributesExist_contextContainsDifferentAttr_returnFalse() {
        ExecutionContext context = ExecutionContextImpl.create(null, null, EXECUTE_OVERRIDE_ATTR);

        assertFalse(ExecutionContextImpl.checkIfAllAttributesExist(context, RESTRICTED_CONTEXT_ATTR));
    }

    @Test
    public void checkIfAllAttributesExist_contextContainsAllRequiredAttrs_returnTrue() {
        ExecutionContext context = ExecutionContextImpl.create(null, null, EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);

        assertTrue(ExecutionContextImpl.checkIfAllAttributesExist(context, RESTRICTED_CONTEXT_ATTR, EXECUTE_OVERRIDE_ATTR));
    }

    @Test
    public void getRuntimeExecutionContext_noArguments_returnCorrectContext() {
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext();
        assertEquals(create(null, null,  EXECUTE_OVERRIDE_ATTR), context);
    }

    @Test
    public void getRuntimeExecutionContext_originalContextIsNull_returnCorrectContext() {
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext(null);
        assertEquals(create(null, null,  EXECUTE_OVERRIDE_ATTR), context);
    }

    @Test
    public void getRuntimeExecutionContext_originalContextIsExecution_returnSameContext() {
        ExecutionContext originalContext = create(null, null,  EXECUTE_OVERRIDE_ATTR);
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext(originalContext);
        assertSame(originalContext, context);
    }

    @Test
    public void getRuntimeExecutionContext_originalContextIsNotExecution_returnCorrectContext() {
        ExecutionContext originalContext = create(locale, timezone);
        ExecutionContext expectedContext = create(locale, timezone, EXECUTE_OVERRIDE_ATTR);
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext(originalContext);
        assertEquals(expectedContext, context);
    }

    @Test
    public void getRestrictedRuntimeExecutionContext_noArguments_returnCorrectContext() {
        ExecutionContext context = ExecutionContextImpl.getRestrictedRuntimeExecutionContext();
        assertEquals(create(null, null,  EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR), context);
    }

    @Test
    public void getRestrictedRuntimeExecutionContext_originalContextIsNull_returnCorrectContext() {
        ExecutionContext context = ExecutionContextImpl.getRestrictedRuntimeExecutionContext(null);
        assertEquals(create(null, null,  EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR), context);
    }

    @Test
    public void getRestrictedRuntimeExecutionContext_originalContextIsRestricted_returnSameContext() {
        ExecutionContext originalContext = create(null, null,  EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);
        ExecutionContext context = ExecutionContextImpl.getRestrictedRuntimeExecutionContext(originalContext);
        assertSame(originalContext, context);
    }

    @Test
    public void getRestrictedRuntimeExecutionContext_originalContextIsNotRestrictedAndNotExecution_returnCorrectContext() {
        ExecutionContext originalContext = create(locale, timezone);
        ExecutionContext expectedContext = create(locale, timezone, EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);
        ExecutionContext context = ExecutionContextImpl.getRestrictedRuntimeExecutionContext(originalContext);
        assertEquals(expectedContext, context);
    }

    @Test
    public void getRestrictedRuntimeExecutionContext_originalContextIsNotRestricted_returnCorrectContext() {
        ExecutionContext originalContext = create(locale, timezone, EXECUTE_OVERRIDE_ATTR);
        ExecutionContext expectedContext = create(locale, timezone, EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);
        ExecutionContext context = ExecutionContextImpl.getRestrictedRuntimeExecutionContext(originalContext);
        assertEquals(expectedContext, context);
    }

    @Test
    public void create_contextWithAllNotNullProperties_success() {
        ExecutionContextImpl expectedContext = new ExecutionContextImpl();
        expectedContext.setLocale(locale);
        expectedContext.setTimeZone(timezone);
        expectedContext.setAttributes(Stream.of(EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR).collect(toList()));
        ExecutionContext actualContext = create(locale, timezone, EXECUTE_OVERRIDE_ATTR, RESTRICTED_CONTEXT_ATTR);
        assertEquals(expectedContext, actualContext);
    }

    @Test
    public void create_contextWithAllNullProperties_success() {
        ExecutionContextImpl expectedContext = new ExecutionContextImpl();
        expectedContext.setLocale(null);
        expectedContext.setTimeZone(null);
        ExecutionContext actualContext = create(null, null);
        assertEquals(expectedContext, actualContext);
    }
}
