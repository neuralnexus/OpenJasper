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
package com.jaspersoft.jasperserver.api.logging.diagnostic.jmx;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;

import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;
import java.util.List;

/**
 * @author ogavavka
 */
public class JmxSecurityAuthenticator implements JMXAuthenticator {

    private AuthenticationManager authenticationManager;
    private List<String> allowedRoles;
    private static final String UNAUTHORIZED = "Unauthorized";

    public Subject authenticate(Object credentials) {
        try{
            String[] info = (String[]) credentials;
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(info[0], info[1]));
            SecurityContextHolder.getContext().setAuthentication(auth);
            Subject s = new Subject();
            if (!auth.isAuthenticated()) {
                throw new SecurityException(UNAUTHORIZED);
            }
            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails contextUserDetails = (UserDetails) auth.getPrincipal();
                for (GrantedAuthority authority: contextUserDetails.getAuthorities()) {
                    if (allowedRoles.contains(authority.getAuthority())) {
                        s.getPrincipals().add(new JMXPrincipal(auth.getName()));
                        return s;
                    }
                }
                throw new SecurityException(UNAUTHORIZED);
            }
        } catch(Exception e) {
            throw new SecurityException(e);
        }

        return null;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAllowedRoles(List<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

}
