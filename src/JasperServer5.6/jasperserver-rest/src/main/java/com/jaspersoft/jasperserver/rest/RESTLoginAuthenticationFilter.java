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


package com.jaspersoft.jasperserver.rest;

import com.jaspersoft.jasperserver.api.security.encryption.EncryptionRequestUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.WebAuthenticationDetails;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

/**
 * This filter allows a client to authenticate to the REST services without using the Basic HTTP
 * Authentication, which does not work well with UTF8 usernames.
 * <p/>
 * The filter looks inside the request for an username and a password parameters:
 * AuthenticationProcessingFilter.SPRING_SECURITY_FORM_USERNAME_KEY
 * AuthenticationProcessingFilter.SPRING_SECURITY_FORM_PASSWORD_KEY
 * <p/>
 * Username could be in the form of user|organization
 *
 * @author gtoffoli
 * @version $Id: RESTLoginAuthenticationFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RESTLoginAuthenticationFilter implements Filter {

    private static final Log log = LogFactory.getLog(RESTLoginAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String username = EncryptionRequestUtils.getValue(request, AuthenticationProcessingFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
        String password = EncryptionRequestUtils.getValue(request, AuthenticationProcessingFilter.SPRING_SECURITY_FORM_PASSWORD_KEY);

        if (!StringUtils.isEmpty(username)) {
            // decoding since | is not http safe
            username = URLDecoder.decode(username, CharEncoding.UTF_8);

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            authRequest.setDetails(new WebAuthenticationDetails(httpRequest));

            Authentication authResult;
            try {
                authResult = authenticationManager.authenticate(authRequest);
            } catch (AuthenticationException e) {
                if (log.isDebugEnabled()) {
                    log.debug("User " + username + " failed to authenticate: " + e.toString());
                }

                if (log.isWarnEnabled()) {
                    log.warn("User " + username + " failed to authenticate: " + e.toString() + " " + e, e.getRootCause());
                }

                SecurityContextHolder.getContext().setAuthentication(null);

                // Send an error message in the form of OperationResult...
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                //OperationResult or = servicesUtils.createOperationResult(1, "Invalid username " + username);
                PrintWriter pw = httpResponse.getWriter();
                pw.print("Unauthorized");
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("User " + username + " authenticated: " + authResult);
            }

            SecurityContextHolder.getContext().setAuthentication(authResult);

            chain.doFilter(request, response);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Failed to authenticate: Bad request. Username and password must be specified.");
            }
            if (log.isWarnEnabled()) {
                log.warn("Failed to authenticate: Bad request. Username and password must be specified.");
            }

            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter pw = httpResponse.getWriter();
            pw.print("Bad request."
                    + (StringUtils.isEmpty(username) ? " Parameter " + AuthenticationProcessingFilter.SPRING_SECURITY_FORM_USERNAME_KEY + " not found." : "")
                    + (StringUtils.isEmpty(password) ? " Parameter " + AuthenticationProcessingFilter.SPRING_SECURITY_FORM_PASSWORD_KEY + " not found." : ""));
        }
    }


    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void init(FilterConfig fc) throws ServletException {
    }
}

