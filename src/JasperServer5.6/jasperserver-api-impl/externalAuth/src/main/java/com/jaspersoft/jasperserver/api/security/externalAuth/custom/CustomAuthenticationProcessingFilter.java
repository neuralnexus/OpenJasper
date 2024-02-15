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
package com.jaspersoft.jasperserver.api.security.externalAuth.custom;

import com.jaspersoft.jasperserver.api.security.externalAuth.BaseAuthenticationProcessingFilter;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * Sample AuthenticationProcessingFilter to demonstrate use of arbitrary authentication filter. Here we create roles according
 * to the requester IP
 * <p/>
 * This is only a sample class.
 *
 * @author Chaim Arbiv
 * @version $id$
 */
public class CustomAuthenticationProcessingFilter extends BaseAuthenticationProcessingFilter {

    protected final Log logger = LogFactory.getLog(this.getClass());

    public final Authentication attemptAuthentication(final HttpServletRequest request) throws AuthenticationException {

        if (obtainUsername(request) != null || obtainPassword(request) != null) {
            return super.attemptAuthentication(request);
        } else {

            String ip = request.getRemoteAddr();
            // doing this replace since we have a bug winding folders that look like IP addresses. the get folder does not find it. see bug 31104
            ip = ip.replace(".", "_");

            GrantedAuthority grantedAuthority;

            grantedAuthority = new GrantedAuthorityImpl("ROLE_CUSTOM_AUTH_IP");

            List<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
            authorities.add(grantedAuthority);

            UserDetails ud = new ExternalUserDetails(ip, "", authorities.toArray(new GrantedAuthority[0]));

            final CustomAuthenticationToken authToken = new CustomAuthenticationToken(ud, "");
            return this.getAuthenticationManager().authenticate(authToken);
        }
    }

    private boolean isEvenIp(String ip) {
        String[] vals = ip.split("\\.");
        int sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum = sum + Integer.parseInt(vals[i]);
        }
        return sum % 2 == 0;
    }
}

