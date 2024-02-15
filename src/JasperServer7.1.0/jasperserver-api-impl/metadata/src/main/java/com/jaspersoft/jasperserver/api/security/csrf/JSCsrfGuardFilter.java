/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.security.csrf;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.owasp.csrfguard.CsrfGuardFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Wrapper filter around org.owasp.csrfguard.CsrfGuardFilter, delegating csrf protection to Owasp CSRFGuard.
 * This filter checks that quest user-agent's are of browser type and are covered by CSRF protection.
 *
 * @author Dmitriy Litvak
 * @version $Id: JSCsrfGuardFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JSCsrfGuardFilter implements Filter {

    private static final Logger log = Logger.getLogger(JSCsrfGuardFilter.class);
    private CsrfGuardFilter csrfGuardFilter = new CsrfGuardFilter();

    private List<Pattern> protectedUserAgentRegexs = new ArrayList<Pattern>();

    public void setProtectedUserAgentRegexs(List<String> protectedUserAgents) {
        for (String ua : protectedUserAgents) {
            this.protectedUserAgentRegexs.add(Pattern.compile(ua, Pattern.CASE_INSENSITIVE));
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String uaHeader = httpRequest.getHeader(HTTP.USER_AGENT);
        boolean isVisualizeRequest = httpRequest.getHeader(CrossDomainCommunicationFilter.X_REMOTE_DOMAIN) != null;

        // !isVisualizeRequest - we do not need to protect Visualize per domain white listing that we implement.
        // isUserAgentProtected() - determines if the reques is coming from a supported browser.
        if (!isVisualizeRequest && isUserAgentProtected(uaHeader)) {
            if (log.isDebugEnabled()) {
                log.debug("Checking for CSRF token in request " + ((HttpServletRequest) request).getRequestURI() + " user-agent: " + uaHeader);
            }

            csrfGuardFilter.doFilter(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
	}

    /** Determines if the request comes from one of the supported browsers.
     * Assumption is that most browsers insert u-a header and there are only
     * a few common u-a types.
     *
     * @param uaHeader user-agent request header
     * @return false if don't need to check CSRF token in the request
     */
    private boolean isUserAgentProtected(String uaHeader) {
        // if there's no user-agent header, the request is not from a browser.
        if (uaHeader == null || uaHeader.trim().isEmpty())
            return false;

        for (Pattern uaRegex : protectedUserAgentRegexs) {
            if (uaRegex.matcher(uaHeader).matches())
                return true;
        }

        return false;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        csrfGuardFilter.init(filterConfig);
	}

    public void destroy() {
		csrfGuardFilter.destroy();
	}
}
