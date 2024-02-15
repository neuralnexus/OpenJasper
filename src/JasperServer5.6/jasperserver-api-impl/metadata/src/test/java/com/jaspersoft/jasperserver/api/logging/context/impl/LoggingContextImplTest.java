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

package com.jaspersoft.jasperserver.api.logging.context.impl;

import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link LoggingContextImpl}
 *
 * @author Sergey Prilukin
 * @version $Id: LoggingContextImplTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class LoggingContextImplTest extends UnitilsJUnit4 {

    public class TestLoggableEvent implements LoggableEvent {
    }

    @TestedObject
    private LoggingContextImpl loggingContext;

    Map<Class<? extends LoggableEvent>, Boolean> enabledLoggingTypesMap;

    @Before
    public void setUp() throws Exception {
        enabledLoggingTypesMap = new HashMap<Class<? extends LoggableEvent>, Boolean>(1);
        enabledLoggingTypesMap.put(TestLoggableEvent.class, true);
    }

    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testGetAllEventsNotModifiable() throws Exception {
        loggingContext.setEnabledLoggingTypesMap(enabledLoggingTypesMap);

        loggingContext.logEvent(new TestLoggableEvent());
        assertEquals(1, loggingContext.getAllEvents().size());

        loggingContext.getAllEvents().remove(0);
    }
}
