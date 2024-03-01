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

package com.jaspersoft.jasperserver.inputcontrols.cascade;

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.EhcacheEngineService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.cache.ControlLogicCacheManager;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class InputControlsLogicServiceImplTest {
    public void setCacheParameter(boolean freshData, Map<String, String[]> parameters) {
        if(freshData){
            controlLogicCacheManager.clearCache();
            parameters.put(EhcacheEngineService.IC_REFRESH_KEY, new String[]{"true"});
        }
    }
    ControlLogicCacheManager controlLogicCacheManager;
    InputControlsLogicServiceImpl controlLogic;

    @Before
    public void setup() {

        controlLogicCacheManager = mock(ControlLogicCacheManager.class);
        controlLogic = new InputControlsLogicServiceImpl();
    }

    @Test
    public void setCacheParameter() {
        Map<String, String[]> parameters = new HashMap<>();
        controlLogic.setControlLogicCacheManager(controlLogicCacheManager);
        controlLogic.setCacheParameter(true, parameters);
        assertFalse(parameters.isEmpty());
    }
}
