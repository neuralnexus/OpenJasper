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
import com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationToken;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Base class of JRS AuthenticationProcessingFilter.  Starts external user/data synchronization
 * on successful authentication.
 *
 * User: dlitvak
 * Date: 8/27/12
 */
@JasperServerAPI
public class BaseAuthenticationProcessingFilter extends AuthenticationProcessingFilter {
	private ExternalDataSynchronizer externalDataSynchronizer;
    private String internalAuthenticationFailureUrl;
	private String passwdParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		Assert.notNull(externalDataSynchronizer, "externalDataSynchronizer cannot be null");
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
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
	 *
	 * @param externalDataSynchronizer
	 */
	public void setExternalDataSynchronizer(ExternalDataSynchronizer externalDataSynchronizer) {
		this.externalDataSynchronizer = externalDataSynchronizer;
	}

    public void setInternalAuthenticationFailureUrl(String internalAuthenticationFailureUrl) {
        setAuthenticationFailureUrl(internalAuthenticationFailureUrl);
    }

	/**
	 * Try to obtain the decrypted password from request attribute.  If attribute is empty, the password was
	 * most likely not encrypted; use request parameter.
	 *
	 * @param request so that request attributes can be retrieved
	 * @return the password that will be presented in the <code>Authentication</code> request token to the
	 *         <code>AuthenticationManager</code>
	 */
	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String pwdParamValue = super.obtainPassword(request);

		Object decrPwd = request.getAttribute("DECRYPTED." + passwdParameter);
		if (decrPwd != null && decrPwd instanceof List)  {
			List decryptedValues = (List) decrPwd;
			return (String)decryptedValues.get(0);
		}

		return pwdParamValue;
	}

	/**
	 * Sets the parameter name which will be used to obtain the password from the login request..
	 *
	 * @param passwordParameter the parameter name. Defaults to "j_password".
	 */
	@Override
	public void setPasswordParameter(String passwordParameter) {
		super.setPasswordParameter(passwordParameter);
		this.passwdParameter = passwordParameter;
	}
}
