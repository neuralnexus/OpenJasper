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


package com.jaspersoft.jasperserver.api.security.externalAuth.sso;

import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.rememberme.NullRememberMeServices;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Chaim Arbiv
 * @version $id$
 *  handles REST SSO authentication requests.
 *
 */
public class SsoAuthenticationRestProcessingFilter extends SsoAuthenticationProcessingFilter {

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getAuthenticationManager(), "authenticationManager must be specified");
        Assert.notNull(getExternalDataSynchronizer(), "externalDataSynchronizer cannot be null");
//        Assert.notNull(getExternalAuthProperties(), "externalAuthProperties cannot be null");

        if (getRememberMeServices() == null) {
            setRememberMeServices(new NullRememberMeServices());
        }
    }

    // On successful login it should let the filter chain end and handle the response (which should be 200)
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        response.setContentType("text/xml; charset=UTF-8");
        return;
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        response.setContentType("text/xml; charset=UTF-8");
        return;
    }
}
