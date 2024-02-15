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

package com.jaspersoft.jasperserver.war.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class HeartbeatCustomDSInfoTest {
    private static final String CUSTOM_DS_CLASS_KEY = "customDSClass[]";
    private static final String CUSTOM_DS_COUNT_KEY = "customDSCount[]";
    private static final String SERVICE_CLASS = "serviceClass";

    private HeartbeatCustomDSInfo objectUnderTest = new HeartbeatCustomDSInfo();
    private HeartbeatCall heartbeatCall = mock(HeartbeatCall.class);

    @Test
    void getAndSet_instanceWithDefaultValues() {
        final HeartbeatCustomDSInfo instance = new HeartbeatCustomDSInfo();

        assertNull(instance.getServiceClass());
    }

    @Test
    void getAndSet_fullyConfiguredInstance() {
        final HeartbeatCustomDSInfo instance = new HeartbeatCustomDSInfo();
        instance.setServiceClass(SERVICE_CLASS);

        assertEquals(SERVICE_CLASS, instance.getServiceClass());
    }

    @Test
    void getKey_serviceClassIsNull_null() {
        assertNull(objectUnderTest.getKey());
    }

    @Test
    void getKey_serviceClassIsDefined_serviceClass() {
        objectUnderTest.setServiceClass(SERVICE_CLASS);

        assertEquals(SERVICE_CLASS, objectUnderTest.getKey());
    }

    @Test
    void contributeToHttpCall_serviceClassIsNull_customDSClassAndCountParamIsContributed() {
        objectUnderTest.contributeToHttpCall(heartbeatCall);

        verify(heartbeatCall).addParameter(CUSTOM_DS_CLASS_KEY, "");
        verify(heartbeatCall).addParameter(CUSTOM_DS_COUNT_KEY, "0");
    }

    @Test
    void contributeToHttpCall_serviceClassIsDefined_customDSClassAndCountParamIsContributed() {
        objectUnderTest.setServiceClass(SERVICE_CLASS);
        objectUnderTest.incrementCount();

        objectUnderTest.contributeToHttpCall(heartbeatCall);

        verify(heartbeatCall).addParameter(CUSTOM_DS_CLASS_KEY, SERVICE_CLASS);
        verify(heartbeatCall).addParameter(CUSTOM_DS_COUNT_KEY, "1");
    }
}
