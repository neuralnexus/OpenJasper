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

package com.jaspersoft.jasperserver.api.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.jaspersoft.jasperserver.api.security.EncryptionAuthenticationProcessingFilter;
import com.jaspersoft.jasperserver.api.security.SecurityConfiguration;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.springframework.mock.web.MockHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.DefaultSavedRequest.Builder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.jaspersoft.jasperserver.api.common.util.AuthFilterConstants;
import com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationTokenImpl;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.jaspersoft.jasperserver.api.security.SecurityConfiguration")
@PrepareForTest({ SecurityConfiguration.class, })
@PowerMockIgnore( {"javax.management.*", "org.w3c.dom.*", "org.apache.log4j.*", "org.xml.sax.*",   "javax.xml.*",  "javax.script.*",  "javax.security.*"})
public class EncryptionAuthenticationProcessingFilterTest {

    @InjectMocks
    EncryptionAuthenticationProcessingFilter currentFilter;

    @Mock
    EncryptionAuthenticationProcessingFilter encFilter;

    @Mock
    Properties securityConfigProps;

    @Mock
    FilterChain chain;

    @Mock
    RequestMatcher requiresAuthenticationRequestMatcher;

    @Mock
    ProviderManager providerManager;

    @Mock
    InternalAuthenticationTokenImpl tokenImpl;

    private MockHttpServletRequest request;
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = (filterRequest, filterResponse) -> {
    };


    public static final String DEFAULT_SAVED_REQUEST_ATTR = "SPRING_SECURITY_SAVED_REQUEST";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(SecurityConfiguration.class);
        setUpSecurityConfigProperties();
        setupRequest();
        setupProviderManager();
    }

    private void setUpSecurityConfigProperties() {
        SecurityConfiguration secConfig = PowerMockito.mock(SecurityConfiguration.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(securityConfigProps.getProperty(Mockito.any(String.class), Mockito.any(String.class))).thenReturn("false");
        PowerMockito.when(secConfig.isEncryptionOn()).thenReturn(false);
    }

    private void setupProviderManager() {
        Mockito.when(providerManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(tokenImpl);
        Mockito.when(encFilter.getAuthenticationManager()).thenReturn(providerManager);
    }

    private void setupRequest() {
        request = new MockHttpServletRequest(HttpMethod.POST.name(), "https://jaspersoft.com/test.html");
        request.setParameter("username", "joeuser");
        request.setParameter("password", "joeuser");
        Builder builder = new Builder();
        builder.setContextPath("/jasperserver-pro");
        builder.setScheme("https");
        builder.setServerName("jasperserver-pro");
        builder.setServerPort(443);
        builder.setRequestURI("/login");
        MockHttpSession session = new MockHttpSession();
        DefaultSavedRequest defSavedRequest = builder.build();
        session.setAttribute(DEFAULT_SAVED_REQUEST_ATTR, defSavedRequest);
        request.setSession(session);
        Mockito.when(requiresAuthenticationRequestMatcher.matches(request)).thenReturn(true);
    }

    @Test
    public void shouldContinueFilterforXRemoteRequests() throws ServletException, IOException {
        request.addHeader(AuthFilterConstants.X_REMOTE_DOMAIN, "test");
        currentFilter.doFilter(request, response, filterChain);
        assertEquals(request.getAttribute(AuthFilterConstants.AUTH_FLOW_CONST), "true");
    }

    @Test
    public void shouldContinueRedirectforNonXRemoteRequests() throws Exception {
        currentFilter.doFilter(request, response, filterChain);
        assertEquals(response.getStatus(), HttpServletResponse.SC_FOUND);
    }

}
