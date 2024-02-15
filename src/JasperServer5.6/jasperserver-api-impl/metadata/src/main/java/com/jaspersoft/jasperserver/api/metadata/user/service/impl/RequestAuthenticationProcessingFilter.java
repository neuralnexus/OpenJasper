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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.savedrequest.SavedRequest;
import org.springframework.security.util.PortResolver;
import org.springframework.security.util.PortResolverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class RequestAuthenticationProcessingFilter extends AbstractProcessingFilter {
	
	private static final Log log = LogFactory.getLog(RequestAuthenticationProcessingFilter.class);
	
	public static final String REQUEST_AUTHENTICATION_ID = "REQUEST_AUTHENTICATION_ID"; 

	private PortResolver portResolver = new PortResolverImpl();
	
	public RequestAuthenticationProcessingFilter() {
		super();
	}

    public void afterPropertiesSet() throws Exception {
       Assert.hasLength(getDefaultTargetUrl(), "defaultTargetUrl must be specified");
        Assert.hasLength(getAuthenticationFailureUrl(),
            "authenticationFailureUrl must be specified");
        Assert.notNull(getAuthenticationManager(),
            "authenticationManager must be specified");
        Assert.notNull(getRememberMeServices());
    }

	/* (non-Javadoc)
	 * @see org.springframework.security.ui.AbstractProcessingFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest)
	 */
	public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        Map requestParameters = obtainRequestParameters(request);
	    String targetUrl = (String) request.getSession().getAttribute(SPRING_SECURITY_SAVED_REQUEST_KEY);

        if (log.isDebugEnabled()) {
    		
    	    targetUrl = (String) request.getSession().getAttribute(SPRING_SECURITY_SAVED_REQUEST_KEY);
		    log.debug("Authenticating with values: '" + requestParameters + "'");
		    log.debug("from URL: " + targetUrl);
        }
        
        Authentication authRequest = new RequestAuthenticationToken(requestParameters);

        // This call to getSession().setAttribute needs to happen, otherwise you get into an
        // infinite loop. Maybe just a getAttribute will work?
        request.getSession().setAttribute(REQUEST_AUTHENTICATION_ID, targetUrl);

        return this.getAuthenticationManager().authenticate(authRequest);
	}

    protected boolean requiresAuthentication(HttpServletRequest request,
            HttpServletResponse response) {
    	return SecurityContextHolder.getContext().getAuthentication() == null;
    	//return obtainRequestParameters(request) != null && obtainRequestParameters(request).size() > 0;
    }


    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, Authentication authResult)
        throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success: " + authResult.toString());
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        if (logger.isDebugEnabled()) {
            logger.debug(
                "Updated SecurityContextHolder to contain the following Authentication: '"
                + authResult + "'");
        }

        String targetUrl = (new SavedRequest(request, portResolver)).getFullRequestUrl();

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to target URL from HTTP Session (or default): " + targetUrl);
        }

        onSuccessfulAuthentication(request, response, authResult);

        getRememberMeServices().loginSuccess(request, response, authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }

        response.sendRedirect(response.encodeRedirectURL(targetUrl));
    }

    
    protected Map obtainRequestParameters(HttpServletRequest request) {
		
		Map result = new HashMap();
		result.putAll(request.getParameterMap());
		
		Enumeration attrs = request.getAttributeNames();
		while (attrs.hasMoreElements()) {
			String attrName = (String) attrs.nextElement();
			result.put(attrName, request.getAttribute(attrName));
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.security.ui.AbstractProcessingFilter#getDefaultFilterProcessesUrl()
	 */
	public String getDefaultFilterProcessesUrl() {
        return "/requestAuthentication";
	}

    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
