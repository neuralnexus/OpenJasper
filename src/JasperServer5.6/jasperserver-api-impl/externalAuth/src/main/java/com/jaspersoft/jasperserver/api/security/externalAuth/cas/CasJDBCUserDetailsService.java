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
package com.jaspersoft.jasperserver.api.security.externalAuth.cas;

import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserDetailsServiceImpl;
import com.jaspersoft.jasperserver.api.security.externalAuth.db.ExternalJDBCUserDetailsService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

/**
 * User: dlitvak
 * Date: 10/19/12
 */
public class CasJDBCUserDetailsService extends ExternalJDBCUserDetailsService {
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		try {
			return super.loadUserByUsername(username);
		} catch (UsernameNotFoundException unfe) {
			logger.warn("Could not find " + username + " in external DB.");  //To change body of catch statement use File | Settings | File Templates.
		}
		return createUserDetails(username, new User(username, "", true, new GrantedAuthority[0]), new GrantedAuthority[0]);
	}
}
