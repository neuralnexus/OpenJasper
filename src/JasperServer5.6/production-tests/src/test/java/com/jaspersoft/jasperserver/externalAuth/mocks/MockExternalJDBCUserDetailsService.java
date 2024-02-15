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
 *  You should have received a copy of the GNU Affero General Public  License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.externalAuth.mocks;

import com.jaspersoft.jasperserver.api.security.externalAuth.db.ExternalJDBCUserDetailsService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: dlitvak
 * Date: 10/4/12
 */
public class MockExternalJDBCUserDetailsService extends ExternalJDBCUserDetailsService {

	private List<String> externalUserRoles = new LinkedList<String>();

	public boolean doesUserHaveExternalRoles() {
		return externalUserRoles != null && externalUserRoles.size() > 0;
	}

	public void setExternalUserRoles(List<String> roles) {
		this.externalUserRoles = roles;
	}

	public List<GrantedAuthority> loadAuthoritiesByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		if (!doesUserHaveExternalRoles()) {
			logger.debug("This test is setup for a user with no external roles.");
			return Collections.emptyList();
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String s : this.externalUserRoles)
			authorities.add(new GrantedAuthorityImpl(s));
		return authorities;
	}

	public void cleanup() {
		externalUserRoles.clear();
	}
}
