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

package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
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

import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link com.jaspersoft.jasperserver.remote.services.impl.ServerInfoServiceImpl}
 *
 * @author vsabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerInfoServiceImplTest {
    @InjectMocks
    private ServerInfoServiceImpl serverInfoService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CalendarFormatProvider calendarFormatProvider;

    private final String JS_VERSION="JS_VERSION";
    private final String BUILD_DATE_STAMP="BUILD_DATE_STAMP";
    private final String BUILD_TIME_STAMP="BUILD_TIME_STAMP";
    private final String BUILD_DATE_STAMP_VALUE="2012/06/11";
    private final String BUILD_TIME_STAMP_VALUE="15:00";
    private final String serverVersion = "5.0";
    private final String serverEdition = "CE";

    @Before
    public void init() throws Exception {
        when(messageSource.getMessage(eq(BUILD_DATE_STAMP), any(Object[].class), eq(Locale.getDefault()))).thenReturn(BUILD_DATE_STAMP_VALUE);
        when(messageSource.getMessage(eq(BUILD_TIME_STAMP), any(Object[].class), eq(Locale.getDefault()))).thenReturn(BUILD_TIME_STAMP_VALUE);
        when(messageSource.getMessage(eq(JS_VERSION), any(Object[].class), eq(Locale.getDefault()))).thenReturn(serverVersion);
    }

    @Test
    public void getServerInfoTest() {
        serverInfoService.getServerInfo();

        assertEquals(serverVersion, serverInfoService.getServerVersion());
        assertEquals(BUILD_DATE_STAMP_VALUE + "_" + BUILD_TIME_STAMP_VALUE, serverInfoService.getServerBuild());
        assertEquals(serverEdition, serverInfoService.getServerEdition().toString());
    }

    @Test
    public void getDiagnosticDataTest() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = serverInfoService.getDiagnosticData();

        //Test total size of diagnostic attributes collected from ServerInfoServiceImpl
        assertEquals(3, resultDiagnosticData.size());

        //Test actual values of diagnostic attributes
        assertEquals(serverVersion, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.VERSION, null, null)).getDiagnosticAttributeValue());
        assertEquals(serverEdition, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.SOURCE_EDITION, null, null)).getDiagnosticAttributeValue());
        assertEquals(BUILD_DATE_STAMP_VALUE + "_" + BUILD_TIME_STAMP_VALUE, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.BUILD, null, null)).getDiagnosticAttributeValue());
    }

}
