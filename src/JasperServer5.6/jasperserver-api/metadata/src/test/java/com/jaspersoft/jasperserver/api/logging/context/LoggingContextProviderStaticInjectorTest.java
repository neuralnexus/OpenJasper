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
package com.jaspersoft.jasperserver.api.logging.context;

import org.junit.Before;
import org.junit.Test;

import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link LoggingContextProviderStaticInjector}
 *
 * @author Sergey Prilukin
 * @version $Id: LoggingContextProviderStaticInjectorTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class LoggingContextProviderStaticInjectorTest extends UnitilsJUnit4 {

    public static class TestClassForStaticInjection {
        public static void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
            assertNotNull(loggingContextProvider);

            //called just for further assertion.
            loggingContextProvider.flushContext();
        }
    }

    @TestedObject
    private LoggingContextProviderStaticInjector loggingContextProviderStaticInjector;

    @InjectInto(property = "loggingContextProvider")
    private Mock<LoggingContextProvider> loggingContextProviderMock;

    private List<String> loggingContextProviderAwareClasses;

    @Before
    public void setUp() throws Exception {
        loggingContextProviderAwareClasses = new ArrayList<String>(1);
        loggingContextProviderAwareClasses.add("com.jaspersoft.jasperserver.api.logging.context.LoggingContextProviderStaticInjectorTest$TestClassForStaticInjection");
    }

    @Test
    public void testLoggingContextProviderStaticInjector() throws Exception {
        loggingContextProviderStaticInjector.setLoggingContextProviderAwareClasses(loggingContextProviderAwareClasses);

        loggingContextProviderStaticInjector.afterPropertiesSet();
        loggingContextProviderMock.assertInvoked().flushContext();
    }
}
