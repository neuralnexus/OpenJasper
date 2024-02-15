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

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetails;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationServiceException;

/**
 * SSO Token Validator interface
 *
 * @author Chaim Arbiv
 */
@JasperServerAPI
public interface SsoTicketValidator {

    /**
     * This method is called to validate SSO token
     *
     * @param ticket the ticket to validate.
     * @return Authentication with user details
     * @throws AuthenticationServiceException in case validation fails
     */
    ExternalUserDetails validate(final Object ticket) throws AuthenticationServiceException;
}
