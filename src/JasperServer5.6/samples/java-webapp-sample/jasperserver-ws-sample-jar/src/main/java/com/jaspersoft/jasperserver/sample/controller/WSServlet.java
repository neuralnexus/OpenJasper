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

package com.jaspersoft.jasperserver.sample.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 */
public class WSServlet extends HttpServlet {

    public static final String ATTR_WS_USERNAME = "wsUsername";
    public static final String ATTR_PASSWORD = "password";
    public static final String PARAM_PROTOCOL = "protocol";
    public static final String PARAM_HOST = "host";
    public static final String PARAM_PORT = "port";
    public static final String PARAM_APP_NAME = "appName";
    public static final String BASE_WSDL_SOAP_ADDRESS = "/services";

    protected String getBaseAddress(HttpServletRequest request) {
        HttpSession session = request.getSession();

        final String wsUsername = (String) session.getAttribute(ATTR_WS_USERNAME);
        final String password = (String) session.getAttribute(ATTR_PASSWORD);

        final String protocol = getServletContext().getInitParameter(PARAM_PROTOCOL);
        final String host = getServletContext().getInitParameter(PARAM_HOST);
        final String port = getServletContext().getInitParameter(PARAM_PORT);
        final String appName = getServletContext().getInitParameter(PARAM_APP_NAME);

        return new StringBuilder().
                append(protocol).
                append(wsUsername).append(":").append(password).append("@").
                append(host).append(":").append(port).append("/").
                append(appName).
                append(BASE_WSDL_SOAP_ADDRESS).toString();
    }

    protected void forward(String view, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String viewPath = new StringBuilder().append("/").append(view).toString();

        RequestDispatcher jsp = request.getRequestDispatcher(viewPath);
        jsp.forward(request, response);
    }

    protected void forwardError(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        request.setAttribute("message", e.getMessage());
        request.setAttribute("stackTrace", sw.toString());

        forward("error.jsp", request, response);
    }

}

