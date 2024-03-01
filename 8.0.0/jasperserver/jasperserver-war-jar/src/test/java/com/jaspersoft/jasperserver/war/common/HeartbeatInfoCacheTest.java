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

package com.jaspersoft.jasperserver.war.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class HeartbeatInfoCacheTest {

    @InjectMocks
    HeartbeatInfoCache heartbeatInfoCache;
    @Spy
    Map infoCache = new HashMap();

    private HeartbeatInfo heartbeatInfo = mock(HeartbeatInfo.class);
    private HeartbeatCall heartbeatCall = mock(HeartbeatCall.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void update_heartbeatInfoIsNotCached_heartbeatInfoIsCachedAndCountIsIncremented() {
        infoCache.clear();
        heartbeatInfoCache.update(heartbeatInfo);

        verify(heartbeatInfo).incrementCount();
        verify(infoCache).put(heartbeatInfo, heartbeatInfo);
    }

    @Test
    void update_heartbeatInfoIsCached_heartbeatInfoCacheIsUpdatedAndCountIsIncremented() {
        HeartbeatInfo cachedHeartbeatInfo = mock(HeartbeatInfo.class);
        infoCache.put(heartbeatInfo, cachedHeartbeatInfo);

        heartbeatInfoCache.update(heartbeatInfo);

        verify(cachedHeartbeatInfo).incrementCount();
        verify(infoCache).put(cachedHeartbeatInfo, cachedHeartbeatInfo);
    }

    @Test
    void contributeToHttpCall_heartbeatInfoCacheIsEmpty_contributeToHttpCallIsNotInvoked() {
        infoCache.clear();
        heartbeatInfoCache.contributeToHttpCall(heartbeatCall);

        verify(heartbeatInfo, never()).contributeToHttpCall(heartbeatCall);
    }

    @Test
    void contributeToHttpCall_heartbeatInfoIsCached_contributeToHttpCallIsInvoked() {
        infoCache.put(heartbeatInfo, heartbeatInfo);

        heartbeatInfoCache.contributeToHttpCall(heartbeatCall);

        verify(heartbeatInfo).contributeToHttpCall(heartbeatCall);
    }

    @Test
    void size_heartbeatInfoCacheIsEmpty_zero() {
        infoCache.clear();
        int result = heartbeatInfoCache.size();

        assertEquals(0, result);
    }

    @Test
    void size_heartbeatInfoIsCached_one() {
        infoCache.put(heartbeatInfo, heartbeatInfo);

        int result = heartbeatInfoCache.size();

        assertEquals(1, result);
    }

}
