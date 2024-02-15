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

package com.jaspersoft.jasperserver.war.action;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.testng.junit.JUnitTestRunner;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains common ad hoc mocking code which sets up the HttpSession so it will work correctly with the SessionAttributeManager.
 * If you want to put mocks into the session for Ad Hoc to use, use SessionAttributeManager.getInstance() and call its methods.
 * There was a bunch of code that reached into the session directly and broke after some SAM refactoring--this has been removed.
 * @author bob
 * @version $Id$
 *
 */
public abstract class BaseHttpSessionAwareTest extends UnitilsJUnit4 {

    private Mock<HttpSession> httpSessionMock;
    private Mock<ServletExternalContext> servletExternalContextMock;
    private Mock<SharedAttributeMap> sharedAttributeMapMock;
    // preserve session map across invocations
    private final Map<String, Object> sessionMap = new HashMap<String, Object>();

    // set up a mock HttpSession on an HttpServletRequest which wraps a map
    // NOTE: to support the SessionAttributeManager correctly, call this method, then go through the SessionAttributeManager to set session attributes
    // DON'T go directly into the session itself
    protected void prepareHttpSession(Mock<HttpServletRequest> requestMock) {
        // use map to hold session stuff
    	// we can call this multiple times if we want to change other request behavior, but we will keep the same sessionMap
        httpSessionMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                String name = (String) proxyInvocation.getArguments().get(0);
                return sessionMap.get(name);
            }
        }).getAttribute(null);

        httpSessionMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                sessionMap.put(((String) proxyInvocation.getArguments().get(0)), proxyInvocation.getArguments().get(1));
                return null;
            }
        }).setAttribute(null, null);

        httpSessionMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                String name = (String) proxyInvocation.getArguments().get(0);
                sessionMap.remove(name);
                return null;
            }
        }).removeAttribute(null);
        httpSessionMock.returns(new MockObject<ServletContext>(ServletContext.class, this)).getServletContext();
    	requestMock.returns(httpSessionMock.getMock()).getSession();
    }
    
    protected void prepareHttpSession(Mock<HttpServletRequest> requestMock, Mock<RequestContext> reqContextMock) {
        prepareHttpSession(requestMock);
        servletExternalContextMock.returns(requestMock.getMock()).getNativeRequest();
        servletExternalContextMock.returns(new LocalAttributeMap()).getRequestMap();
        servletExternalContextMock.returns(sharedAttributeMapMock.getMock()).getSessionMap();
        reqContextMock.returns(servletExternalContextMock.getMock()).getExternalContext();
    }

    protected Map<String, Object> getSessionMap() {
    	return sessionMap;
    }
}