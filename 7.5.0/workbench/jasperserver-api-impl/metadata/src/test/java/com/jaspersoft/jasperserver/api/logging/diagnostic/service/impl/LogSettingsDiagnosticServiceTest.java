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

package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.common.properties.Log4jSettingsService;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link LogSettingsDiagnosticService}
 *
 * @author vsabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class LogSettingsDiagnosticServiceTest {

    @InjectMocks
    private  LogSettingsDiagnosticService logSettingsDiagnosticService;

    @Mock
    private PropertiesManagementService propertiesManagementService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Log4jSettingsService log4jSettingsService;


    private Map<String, String> loggers = new HashMap <String, String>();

    @Before
    public void setUp() throws Exception {
        loggers.put("Log1", "Value1");
        loggers.put("Log2", "Value2");
        loggers.put("Log3", "Value3");
        Set<Map.Entry<String,String>> globalPropertiesList = new HashSet<Map.Entry<String,String>>();
        globalPropertiesList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "log4j.Log1";
            }

            @Override
            public String getValue() {
                return "Value1Updated";
            }

            @Override
            public String setValue(String s) {
                return "Value1Updated";
            }
        });
        globalPropertiesList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "log4j.Log4";
            }

            @Override
            public String getValue() {
                return "NewValue";
            }

            @Override
            public String setValue(String s) {
                return "NewValue";
            }
        });
        globalPropertiesList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "NotLog4JProperty";
            }

            @Override
            public String getValue() {
                return "";
            }

            @Override
            public String setValue(String s) {
                return "";
            }
        });
        when(propertiesManagementService.entrySet()).thenReturn(globalPropertiesList);
        when(log4jSettingsService.getLoggers()).thenReturn(loggers);

    }

    @Test
    public void testGetAllEventsNotModifiable() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = logSettingsDiagnosticService.getDiagnosticData();
        //Test total size of diagnostic attributes
        assertEquals(2, resultDiagnosticData.size());
        assertEquals(4, ((Map)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.LOG_SETTINGS, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Value3", ((Map)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.LOG_SETTINGS, null, null)).getDiagnosticAttributeValue()).get("Log3"));
        assertEquals("Value1Updated", ((Map)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.LOG_SETTINGS, null, null)).getDiagnosticAttributeValue()).get("Log1"));
    }

}
