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

import com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationToken;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.basicauth.BasicProcessingFilter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: dlitvak
 * Date: 4/24/13
 */
public class ExternalAuthBasicProcessingFilter extends BasicProcessingFilter {
	private ExternalDataSynchronizer externalDataSynchronizer;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		Assert.notNull(externalDataSynchronizer, "externalDataSynchronizer cannot be null");
	}

	@Override
	public void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											  Authentication authResult) throws IOException {
		try {
			if (!(authResult instanceof InternalAuthenticationToken))
				externalDataSynchronizer.synchronize();
		} catch (RuntimeException e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			throw e;
		}
	}


	protected ExternalDataSynchronizer getExternalDataSynchronizer() {
		return externalDataSynchronizer;
	}

	/**
	 * Method injecting post-authentication synchronizer
	 *
	 * @param externalDataSynchronizer
	 */
	public void setExternalDataSynchronizer(ExternalDataSynchronizer externalDataSynchronizer) {
		this.externalDataSynchronizer = externalDataSynchronizer;
	}
}
