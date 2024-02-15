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

import java.util.Map;

import org.springframework.security.providers.AbstractAuthenticationToken;

/**
 * @author swood
 *
 */
public class RequestAuthenticationToken extends AbstractAuthenticationToken {

	private Map requestParameters;
	private Object principal;
	private Object credentials;
	
	public RequestAuthenticationToken() {
		super(null);
	}
	
	/**
	 * 
	 */
	public RequestAuthenticationToken(Map requestParameters) {
		super(null);
		this.requestParameters = requestParameters;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.Authentication#getCredentials()
	 */
	public Object getCredentials() {
		if (credentials != null)
			return credentials;
		else
			return requestParameters;
	}

	/**
	 * @param credentials The credentials to set.
	 */
	public void setCredentials(Object credentials) {
		this.credentials = credentials;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.Authentication#getPrincipal()
	 */
	public Object getPrincipal() {
		return principal;
	}

	/**
	 * @param principal The principal to set.
	 */
	public void setPrincipal(Object principal) {
		this.principal = principal;
	}

}
