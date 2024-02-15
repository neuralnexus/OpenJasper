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
package com.jaspersoft.jasperserver.war;

import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.core.MockObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class JSSessionLocaleResolverTest extends UnitilsJUnit4 {

    @TestedObject
    private JSSessionLocaleResolver localeResolver;

    @Test
    public void ensureLocaleGetFromSessionIfPresentInSession() throws Exception {
        Mock<HttpServletRequest> requestMock = new MockObject<HttpServletRequest>(HttpServletRequest.class, this);
        Mock<HttpSession> sessionMock = new MockObject<HttpSession>(HttpSession.class, this);
        sessionMock.returns(Locale.GERMAN).getAttribute(JasperServerConstImpl.getUserLocaleSessionAttr());
        requestMock.returns(sessionMock).getSession();
        requestMock.returns(Locale.US).getLocale();

        Locale locale = localeResolver.resolveLocale(requestMock.getMock());

        assertEquals(Locale.GERMAN, locale);
    }

    @Test
    public void ensureLocaleGetFromCookiesIfNotPresentInSession() throws Exception {
        Mock<HttpServletRequest> requestMock = new MockObject<HttpServletRequest>(HttpServletRequest.class, this);
        Mock<HttpSession> sessionMock = new MockObject<HttpSession>(HttpSession.class, this);
        sessionMock.returns(null).getAttribute(JasperServerConstImpl.getUserLocaleSessionAttr());
        requestMock.returns(sessionMock).getSession();
        requestMock.returns(Locale.US).getLocale();

        Mock<Cookie> cookie = new MockObject<Cookie>(Cookie.class, this);
        cookie.returns(JasperServerConstImpl.getUserLocaleSessionAttr()).getName();
        cookie.returns("zh_CN").getValue();

        Cookie[] cookies = new Cookie[] {cookie.getMock()};
        requestMock.returns(cookies).getCookies();

        Locale locale = localeResolver.resolveLocale(requestMock.getMock());

        assertEquals("zh_CN", locale.toString());
    }

    @Test
    public void ensureLocaleGetFromRequestIfNotPresentInSessionAndInCookies() throws Exception {
        Mock<HttpServletRequest> requestMock = new MockObject<HttpServletRequest>(HttpServletRequest.class, this);
        Mock<HttpSession> sessionMock = new MockObject<HttpSession>(HttpSession.class, this);
        requestMock.returns(sessionMock).getSession();
        requestMock.returns(Locale.UK).getLocale();
        Locale locale = localeResolver.resolveLocale(requestMock.getMock());

        assertEquals(Locale.UK, locale);
    }
}
