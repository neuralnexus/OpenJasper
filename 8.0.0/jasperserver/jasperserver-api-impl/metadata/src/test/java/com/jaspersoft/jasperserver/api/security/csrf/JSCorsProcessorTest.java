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

package com.jaspersoft.jasperserver.api.security.csrf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JSCorsProcessorTest {

    @InjectMocks
    JSCorsProcessor corsProcessor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private Map<String, List<String>> testHeaderUrlPatterns = new HashMap();
    private Map<String, List<String>> testHeaderValues = new HashMap();
    private List<String> patternList = new ArrayList();
    private List<String> headerValuesList = new ArrayList();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        request.setServletPath(null);
        request.setContextPath("http://localhost.jaspersoft.com:8080/jasperserver-pro");
        patternList.add("/runtime/[0-9A-Za-z]*/rest_v2/settings/.*");
        headerValuesList.add("Accept-Language");
        testHeaderUrlPatterns.put("Vary", patternList);
        testHeaderValues.put("Vary", headerValuesList);
    }


    @Test
    public void shouldAddHeaderForValidRequests() throws Exception {
        request.setRequestURI("http://localhost.jaspersoft.com:8080/jasperserver-pro/runtime/27F3EE97/rest_v2/settings/auth");
        corsProcessor.setHeaderUrlPatterns(testHeaderUrlPatterns);
        corsProcessor.setHeaderValues(testHeaderValues);
        corsProcessor.addJrsHeaders(response, request);
        assertEquals(response.getHeader("Vary").contains("Accept-Language"), true);
    }


    @Test
    public void shouldNotAddHeaderForInValidRequests() throws Exception {
        request.setRequestURI("http://localhost.jaspersoft.com:8080/jasperserver-pro/login.html");
        corsProcessor.setHeaderUrlPatterns(testHeaderUrlPatterns);
        corsProcessor.setHeaderValues(testHeaderValues);
        corsProcessor.addJrsHeaders(response, request);
        assertEquals(response.getHeader("Vary"), null);
    }

}
