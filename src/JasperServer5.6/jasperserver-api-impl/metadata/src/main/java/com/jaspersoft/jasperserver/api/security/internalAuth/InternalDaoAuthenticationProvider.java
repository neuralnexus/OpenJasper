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
package com.jaspersoft.jasperserver.api.security.internalAuth;

import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserIsNotInternalException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.DaoAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;

/**
 *  InternalDaoAuthenticationProvider overrides successful authentication token creation to distinguish
 *  our internal authentication against jaspersoft database versus external authentication against any other
 *  source.
 *
 * User: dlitvak
 * Date: 11/14/12
 */
public class InternalDaoAuthenticationProvider extends DaoAuthenticationProvider {
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
												  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (!(userDetails instanceof MetadataUserDetails) || ((MetadataUserDetails)userDetails).isExternallyDefined())
			throw new UserIsNotInternalException("User needs to be internal to be authenticated by " + this.getClass());

		super.additionalAuthenticationChecks(userDetails, authentication);
	}

	/**
	 * Create InternalAuthenticationToken after successful authentication against JRS database
	 *
	 * @param principal that should be the principal in the returned object (defined by the {@link
	 *        #isForcePrincipalAsString()} method)
	 * @param authentication that was presented to the provider for validation
	 * @param user that was loaded by the implementation
	 *
	 * @return the successful authentication token
	 */
	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
														 UserDetails user) {
		InternalAuthenticationTokenImpl result = new InternalAuthenticationTokenImpl(principal,
				authentication.getCredentials(), user.getAuthorities());
		result.setDetails(authentication.getDetails());

		return result;
	}

}
