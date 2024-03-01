/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.security;

import com.jaspersoft.jasperserver.api.security.encryption.EncryptionRequestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;


/**
 * Overwrites obtainUsername and obtainPassword so that decrypted values can be used.
 * EncryptionFilter must come before this so that this can pull out the decrypted values
 * for username and password.
 *
 * @author norm
 * @see com.jaspersoft.jasperserver.api.security.encryption.EncryptionFilter
 */
public class EncryptionAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilterWarningWrapper {
    private Logger log = LogManager.getLogger(this.getClass());

    /**
     * When the password is encrypted in the EncryptionFilter, the encrypted value is passed into the
     * PARAM_PWD attribute.  With encryption off, the password is in the original request parameter.
     *
     * @param request A HttpServletRequest.
     * @return String or null.
     */
    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return EncryptionRequestUtils.getValueWithLegacySupport(request, getPasswordParameter());
    }
}
