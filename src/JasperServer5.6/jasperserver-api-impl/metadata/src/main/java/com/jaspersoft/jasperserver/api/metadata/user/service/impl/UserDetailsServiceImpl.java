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

import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.TenantAwareGrantedAuthority;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.security.GrantedAuthority;

import java.util.Iterator;
import java.util.List;

/**
 * @author bklawans from code written by swood
 * @version $Id: UserDetailsServiceImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	protected static final Log log = LogFactory.getLog(UserDetailsServiceImpl.class);

	private List defaultInternalRoles;
	private List defaultAdminRoles;
	private List adminUsers;

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		if (adminUsers.contains(username)) {
		    log.debug("User " + username + " is an admin");
		    return detailsFromList(defaultAdminRoles, username);
		}
		
		log.debug("User " + username + " is not an admin, getting default authorities");
		return detailsFromList(defaultInternalRoles, username);
	}

	public void setDefaultInternalRoles(List defaultInternalRoles) {
		this.defaultInternalRoles = defaultInternalRoles;
	}
	
    public void setDefaultAdminRoles(List defaultAdminRoles) {
        this.defaultAdminRoles = defaultAdminRoles;
    }
    
    public void setAdminUsers(List adminUsers) {
        this.adminUsers = adminUsers;
    }
    
    public List getDefaultInternalRoles() {
        return defaultInternalRoles;
    }
    
    public List getDefaultAdminRoles() {
        return defaultAdminRoles;
    }
    
    public List getAdminUsers() {
        return adminUsers;
    }
    
    private UserDetails detailsFromList(List roles, String username) {
		
		GrantedAuthority[] authorities = roles == null ? new GrantedAuthority[0] : new GrantedAuthority[roles.size()];
		
		if (roles == null) {
			return new UserDetailsImpl(authorities, username);
		}
		
		Iterator it = roles.iterator();
		int i = 0;
		while (it.hasNext()) {
			authorities[i++] = new TenantAwareGrantedAuthority((String) it.next());
		}
		
		return new UserDetailsImpl(authorities, username);
       
    }
    
    public static class UserDetailsImpl implements UserDetails {
        private GrantedAuthority[] authorities;
        private String username;
        
        public UserDetailsImpl(GrantedAuthority[] authorities, String username) {
            this.authorities = authorities;
            this.username = username;
        }
        
        public GrantedAuthority[] getAuthorities() {
            return authorities;
        }
        
        public String getPassword() {
            return null;
        }
        
        public String getUsername() {
            return username;
        }
        
        public boolean isAccountNonExpired() {
            return true;
        }
        
        public boolean isAccountNonLocked() {
            return true;
        }
        
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        public boolean isEnabled() {
            return true;
        }
    }
}
