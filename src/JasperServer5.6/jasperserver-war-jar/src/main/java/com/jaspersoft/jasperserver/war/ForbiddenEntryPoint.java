/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war;

import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: Zakhar.Tomchenco
 */
public class ForbiddenEntryPoint implements AuthenticationEntryPoint {
    public final String SUPPRESS_BASIC_HEADER = "X-Suppress-Basic";
    public final String HTTP_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";

    public void commence(ServletRequest request, ServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!"true".equalsIgnoreCase(httpRequest.getHeader(SUPPRESS_BASIC_HEADER))){
            httpResponse.setHeader(HTTP_WWW_AUTHENTICATE_HEADER, "Basic realm=\"Protected area\"");
        }

        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
