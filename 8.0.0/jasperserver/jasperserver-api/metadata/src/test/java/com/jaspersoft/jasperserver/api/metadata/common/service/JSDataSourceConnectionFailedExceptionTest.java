/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Alexei Skorodumov askorodu@tibco.com
 */
public class JSDataSourceConnectionFailedExceptionTest {
    @Test
    public void constructor_cause_success() {
        Throwable cause = new RuntimeException("error message");
        JSDataSourceConnectionFailedException exception = new JSDataSourceConnectionFailedException(cause);
        assertEquals("error message", exception.getMessage());
        assertThat(exception.getCause(), sameInstance(cause));
    }

    @Test
    public void constructor_causeAndMessage_success() {
        Throwable cause = new RuntimeException("error message");
        JSDataSourceConnectionFailedException exception =
                new JSDataSourceConnectionFailedException("other error message", cause);
        assertEquals("other error message", exception.getMessage());
        assertThat(exception.getCause(), sameInstance(cause));
    }
}
