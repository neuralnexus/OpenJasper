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
package com.jaspersoft.jasperserver.war;

import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JSSessionLocaleResolverTest {

    private JSSessionLocaleResolver localeResolver = new JSSessionLocaleResolver();

    @Test
    public void ensureLocaleGetFromSessionIfPresentInSession() throws Exception {

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpSession sessionMock = mock(HttpSession.class);
        when(sessionMock.getAttribute(eq(JasperServerConstImpl.getUserLocaleSessionAttr()))).thenReturn(Locale.GERMAN);
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getLocale()).thenReturn(Locale.US);

        Locale locale = localeResolver.resolveLocale(requestMock);

        assertEquals(Locale.GERMAN, locale);
    }

    @Test
    public void ensureLocaleGetFromCookiesIfNotPresentInSession() throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpSession sessionMock = mock(HttpSession.class);
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getLocale()).thenReturn(Locale.US);

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(JasperServerConstImpl.getUserLocaleSessionAttr());
        when(cookie.getValue()).thenReturn("zh_CN");

        Cookie[] cookies = new Cookie[] {cookie};
        when(requestMock.getCookies()).thenReturn(cookies);

        Locale locale = localeResolver.resolveLocale(requestMock);

        assertEquals("zh_CN", locale.toString());
    }

    @Test
    public void ensureLocaleGetFromRequestIfNotPresentInSessionAndInCookies() throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpSession sessionMock = mock(HttpSession.class);
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getLocale()).thenReturn(Locale.UK);
        Locale locale = localeResolver.resolveLocale(requestMock);

        assertEquals(Locale.UK, locale);
    }
}
