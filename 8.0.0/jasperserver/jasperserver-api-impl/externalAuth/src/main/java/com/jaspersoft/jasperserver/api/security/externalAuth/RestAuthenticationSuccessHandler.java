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

package com.jaspersoft.jasperserver.api.security.externalAuth;

import com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dlitvak
 */
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    protected final Log logger = LogFactory.getLog(getClass());
    private ExternalDataSynchronizer externalDataSynchronizer;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            if (!(authentication instanceof InternalAuthenticationToken))
                externalDataSynchronizer.synchronize();
            logger.debug("Successful authentication");

            //empty-body response
        } catch (RuntimeException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw e;
        }
    }

    /**
     *
     * @param externalDataSynchronizer
     */
    public void setExternalDataSynchronizer(ExternalDataSynchronizer externalDataSynchronizer) {
        this.externalDataSynchronizer = externalDataSynchronizer;
    }

    protected ExternalDataSynchronizer getExternalDataSynchronizer() {
        return externalDataSynchronizer;
    }
}
