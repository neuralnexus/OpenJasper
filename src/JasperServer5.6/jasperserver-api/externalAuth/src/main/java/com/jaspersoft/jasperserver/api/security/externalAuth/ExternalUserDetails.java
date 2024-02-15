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
package com.jaspersoft.jasperserver.api.security.externalAuth;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Java object class used to pass user details parsed during authentication to Synchronizer/Processor.
 *
 * User: dlitvak
 * Date: 8/30/12
 */
@JasperServerAPI
public class ExternalUserDetails extends User {
	public static final String PROFILE_ATTRIBUTES_ADDITIONAL_MAP_KEY = "PROF.ATTRIBS";
	public static final String UNMAPPED_PARAMS_MAP_KEY = "UNMAPPED.PARAMS";
	public static final String PARENT_TENANT_HIERARCHY_MAP_KEY = "PARENT_TENANT_HIERARCHY_MAP_KEY";

	private static final String EMPTY_PASSWORD = "";
	private Map<String,Object> additionalDetailsMap = new HashMap<String, Object>();

	public ExternalUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
							   boolean credentialsNonExpired, boolean accountNonLocked, GrantedAuthority[] authorities)
			throws IllegalArgumentException {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public ExternalUserDetails(String username, String password, GrantedAuthority[] authorities)
			throws IllegalArgumentException {
		super(username, password, true, true, true, true, authorities);
	}

	public ExternalUserDetails(String username, GrantedAuthority[] authorities)
			throws IllegalArgumentException {
		super(username, EMPTY_PASSWORD, true, true, true, true, authorities);
	}

	public ExternalUserDetails(String username)
			throws IllegalArgumentException {
		super(username, EMPTY_PASSWORD, true, true, true, true, new GrantedAuthority[0]);
	}

	public Map<String, Object> getAdditionalDetailsMap() {
		return additionalDetailsMap;
	}

	public void setAdditionalDetailsMap(Map<String, Object> additionalDetailMap) {
		this.additionalDetailsMap = additionalDetailMap;
	}

	@Override
	public void setAuthorities(GrantedAuthority[] grantedAuthorities) {
		super.setAuthorities(grantedAuthorities);
	}
}
