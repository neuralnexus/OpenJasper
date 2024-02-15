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
package com.jaspersoft.jasperserver.api.security;

import com.jaspersoft.jasperserver.api.security.encryption.EncryptionRequestUtils;
import org.apache.log4j.Logger;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;


/**
 * Overwrites obtainUsername and obtainPassword so that decrypted values can be used.
 * EncryptionFilter must come before this so that this can pull out the decrypted values
 * for username and password.
 *
 * @author norm
 * @see com.jaspersoft.jasperserver.api.security.encryption.EncryptionFilter
 */
public class EncryptionAuthenticationProcessingFilter extends AuthenticationProcessingFilter {
    private Logger log = Logger.getLogger(this.getClass());
    public static final String WEB_FLOW_CONTROLLER_MAPPING = "flow.html";

    /**
     * When the password is encrypted in the EncryptionFilter, the encrypted value is passed into the
     * PARAM_PWD attribute.  With encryption off, the password is in the original request parameter.
     *
     * @param request A HttpServletRequest.
     * @return String or null.
     */
    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return EncryptionRequestUtils.getValue(request, SPRING_SECURITY_FORM_PASSWORD_KEY);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request) {
        String url;
        final String forceDefaultRedirect = request.getParameter("forceDefaultRedirect");
        if (forceDefaultRedirect != null && forceDefaultRedirect.equalsIgnoreCase("true")) {
            // client doesn't want to follow redirect to any stored URL. Redirect to default URL is requested
            url = getDefaultTargetUrl();
        } else {
            url = super.determineTargetUrl(request);
            if (url.endsWith(WEB_FLOW_CONTROLLER_MAPPING)) {
                url = getDefaultTargetUrl();
                log.info("Cannot restore web flow state, redirection to default page");
            }
        }
        return url;
    }
}
