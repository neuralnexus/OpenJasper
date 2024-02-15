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
package com.jaspersoft.jasperserver.api.security.externalAuth.preauth;

import com.jaspersoft.jasperserver.api.common.crypto.CipherI;
import com.jaspersoft.jasperserver.api.common.crypto.PlainTextNonCipher;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalDataSynchronizer;
import org.springframework.security.Authentication;
import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter class extends AbstractPreAuthenticatedProcessingFilter to
 * call externalDataSynchronizer on successful authentication.
 *
 * User: dlitvak
 * Date: 10/1/13
 */
public class BasePreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
	private String principalParameter = "pp";
	private Boolean tokenInRequestParam = null;

	private CipherI tokenDecryptor = new PlainTextNonCipher();

	private ExternalDataSynchronizer externalDataSynchronizer;

	/**
	 * Override to extract the principal information from the current request
	 */
	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String principal;

		if (logger.isDebugEnabled())
			logger.debug("Looking for token by principalParameter name '" + principalParameter + "'");

		if (tokenInRequestParam == null) {
			principal = request.getHeader(principalParameter);
			if (principal == null) {
				logger.debug("Token not found in the header, looking in the request parameters.");
				principal = request.getParameter(principalParameter);
			}
		}
		else if (tokenInRequestParam) {
			principal = request.getParameter(principalParameter);
		}
		else {
			principal = request.getHeader(principalParameter);
		}


		if (principal == null) {
			if (logger.isDebugEnabled())
				logger.debug(principalParameter + " was not found. Skipping pre-auth authentication.");
			return principal;
		}

		if (logger.isDebugEnabled())
			logger.debug("Found " + principalParameter + ". Starting pre-auth authentication.");
		return tokenDecryptor.decrypt(principal);
	}

	/**
	 * Override to extract the credentials (if applicable) from the current request. Some implementations
	 * may return a dummy value.
	 */
	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "N/A";
	}

	/**
	 * Return the order value of this object, with a
	 * higher value meaning greater in terms of sorting.
	 * <p>Normally starting with 0, with <code>Integer.MAX_VALUE</code>
	 * indicating the greatest value. Same order values will result
	 * in arbitrary positions for the affected objects.
	 * <p>Higher values can be interpreted as lower priority. As a
	 * consequence, the object with the lowest value has highest priority
	 * (somewhat analogous to Servlet "load-on-startup" values).
	 *
	 * @return the order value
	 */
	@Override
	public int getOrder() {
		return FilterChainOrder.PRE_AUTH_FILTER;
	}

	/**
	 *
	 * @param principalParameter - parameter that contains pre-authenticated user details
	 */
	public void setPrincipalParameter(String principalParameter) {
		this.principalParameter = principalParameter;
	}

	/**
	 * Configuration determining whether principalParameter is read from header (default) or
	 * from request parameter.
	 *
	 * @param tokenInRequestParam - default is true
	 */
	public void setTokenInRequestParam(boolean tokenInRequestParam) {
		this.tokenInRequestParam = tokenInRequestParam;
	}

	public boolean isTokenInRequestParam() {
		return tokenInRequestParam;
	}
	/**
	 *
	 * @param externalDataSynchronizer - object that invokes post-auth Processors
	 */
	public void setExternalDataSynchronizer(ExternalDataSynchronizer externalDataSynchronizer) {
		this.externalDataSynchronizer = externalDataSynchronizer;
	}

	/**
	 * The decryptor applied to the text in principalParameter.
	 * Default: PlainTextSymmetricNonCypher - returns principalParameter unencrypted.
	 * @param tokenDecryptor
	 */
	public void setTokenDecryptor(CipherI tokenDecryptor) {
		this.tokenDecryptor = tokenDecryptor;
	}

	/**
	 * Puts the <code>Authentication</code> instance returned by the
	 * authentication manager into the secure context.
	 *
	 * Calls Synchronizer to create a user mirror image in Jasper database
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
		super.successfulAuthentication(request, response, authResult);
		externalDataSynchronizer.synchronize();
	}

}
