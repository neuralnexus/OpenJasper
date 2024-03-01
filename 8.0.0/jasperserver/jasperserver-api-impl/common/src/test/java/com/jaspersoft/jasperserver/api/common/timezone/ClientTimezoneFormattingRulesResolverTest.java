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
package com.jaspersoft.jasperserver.api.common.timezone;

import org.junit.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;

/**
 * @author Vasyl Spachynskyi
 * @version $Id: Id $
 * @since 22.03.2018
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientTimezoneFormattingRulesResolverTest {

    @InjectMocks
    private ClientTimezoneFormattingRulesResolver clientTimezoneFormattingRulesResolver;

    @Mock
    private Map<String, Boolean> applyClientTimezoneFormatting;

    @Before
    public void setUp() throws Exception {
        Mockito.when(applyClientTimezoneFormatting.get(eq(Time.class.getCanonicalName()))).thenReturn(true);
        Mockito.when(applyClientTimezoneFormatting.get(eq(Timestamp.class.getCanonicalName()))).thenReturn(true);
        Mockito.when(applyClientTimezoneFormatting.get(eq(Date.class.getCanonicalName()))).thenReturn(false);
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsNull_returnFalse() {
        assertFalse(new ClientTimezoneFormattingRulesResolver().isApplyClientTimezone((String) null));
        assertFalse(new ClientTimezoneFormattingRulesResolver().isApplyClientTimezone((Class) null));
        assertFalse(new ClientTimezoneFormattingRulesResolver().isApplyClientTimezone((Object) null));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsString_returnTrue() {
        assertTrue(clientTimezoneFormattingRulesResolver.isApplyClientTimezone("Time"));
        assertTrue(clientTimezoneFormattingRulesResolver.isApplyClientTimezone("Timestamp"));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsFullClassName_returnTrue() {
        assertTrue(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(java.sql.Time.class.getName()));
        assertTrue(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(java.sql.Timestamp.class.getName()));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsDate_returnFalse() {
        assertFalse(clientTimezoneFormattingRulesResolver.isApplyClientTimezone("Date"));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsFullDateClassName_returnFalse() {
        assertFalse(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(java.util.Date.class.getName()));
        assertFalse(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(java.sql.Date.class.getName()));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsInteger_returnFalse() {
        assertFalse(clientTimezoneFormattingRulesResolver.isApplyClientTimezone("Integer"));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsTimeClass_returnTrue() {
        assertTrue(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Time.class));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsDateClass_returnFalse() {
        assertFalse(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Date.class));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsIntegerClass_returnFalse() {
        assertFalse(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(Integer.class));
    }

    @Test
    public void shouldApplyClientTimezone_whenDataTypeIsTimeObject_returnTrue() {
        assertTrue(clientTimezoneFormattingRulesResolver.isApplyClientTimezone(new Time(1)));
    }
}