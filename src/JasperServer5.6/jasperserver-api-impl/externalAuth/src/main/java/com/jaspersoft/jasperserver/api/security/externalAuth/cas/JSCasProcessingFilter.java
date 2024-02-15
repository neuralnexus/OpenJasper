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

import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalDataSynchronizer;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.cas.CasAuthenticationToken;
import org.springframework.security.ui.cas.CasProcessingFilter;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: dlitvak
 * Date: 10/20/12
 */
public class JSCasProcessingFilter extends CasProcessingFilter {
	private static final String CAS_TICKET_PARAM_NAME = "ticket";

	private String usernameParameter = AuthenticationProcessingFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
	private String passwordParameter = AuthenticationProcessingFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;
	private ExternalDataSynchronizer externalDataSynchronizer;

    public Authentication attemptAuthentication(final HttpServletRequest request)
			throws AuthenticationException {
            String password = obtainTicket(request);
		if (password != null && password.trim().length() > 0){
			final String username = CAS_STATEFUL_IDENTIFIER;
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
			authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

			return this.getAuthenticationManager().authenticate(authRequest);
		}
		else {
			String username = obtainUsername(request);
			password = obtainPassword(request);

			UsernamePasswordAuthenticationToken authRequest =
					new UsernamePasswordAuthenticationToken(username != null ? username.trim() : "", password != null ? password : "");

			// Allow subclasses to set the "details" property
			setDetails(request, authRequest);

			return this.getAuthenticationManager().authenticate(authRequest);
		}
	}

	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											  Authentication authResult) throws IOException {
		try {
			if (authResult instanceof CasAuthenticationToken) {     //Synchronize only for external authentication
				SecurityContextHolder.getContext().setAuthentication(authResult);
				externalDataSynchronizer.synchronize();
			}
		} catch (RuntimeException e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			throw e;
		}
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.externalDataSynchronizer, "externalDataSynchronizer must not be null.");
		super.afterPropertiesSet();
	}

	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(usernameParameter);
	}

    protected String obtainTicket(HttpServletRequest request) {
        return request.getParameter(CAS_TICKET_PARAM_NAME);
    }

	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter(passwordParameter);
	}

	String getUsernameParameter() {
		return usernameParameter;
	}

	/**
	 * Sets the parameter name which will be used to obtain the username from the login request.
	 *
	 * @param usernameParameter the parameter name. Defaults to "j_username".
	 */
	public void setUsernameParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
		this.usernameParameter = usernameParameter;
	}

	String getPasswordParameter() {
		return passwordParameter;
	}

	/**
	 * Sets the parameter name which will be used to obtain the password from the login request..
	 *
	 * @param passwordParameter the parameter name. Defaults to "j_password".
	 */
	public void setPasswordParameter(String passwordParameter) {
		Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
		this.passwordParameter = passwordParameter;
	}

	/**
	 * Provided so that subclasses may configure what is put into the authentication request's details
	 * property.
	 *
	 * @param request that an authentication request is being created for
	 * @param authRequest the authentication request object that should have its details set
	 */
	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	public ExternalDataSynchronizer getExternalDataSynchronizer() {
		return externalDataSynchronizer;
	}

	public void setExternalDataSynchronizer(ExternalDataSynchronizer externalDataSynchronizer) {
		this.externalDataSynchronizer = externalDataSynchronizer;
	}

    public void setInternalAuthenticationFailureUrl(String internalAuthenticationFailureUrl) {
        setAuthenticationFailureUrl(internalAuthenticationFailureUrl);
    }

	/**
	 * [33145]
	 * Overriding this method allows single sign out. If invalidateSessionOnSuccessfulAuthentication=true in the super class method,
	 * the initial pre-authenticated session is destroyed and a new one is created.  As a result,
	 * SingleSignOutHttpSessionListener.sessionDestroyed()
	 * removes the ticket-init session mapping from SessionMappingStorage.  The new ticket-session mapping is never created.
	 * Thus, when CAS signals JRS to sign out, it never finds the new session to invalidate.
	 *
	 * This method reinstates ticket-new session mapping in SessionMappingStorage. On CAS sign out request, SingleSignOutFilter
	 * finds the session in the map and invalidates it.
	 *
	 * @param request
	 * @param response
	 * @param authResult
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
		SessionMappingStorage sessionMappingStorage = SingleSignOutFilter.getSessionMappingStorage();
		String casServiceTicket = request.getParameter(CAS_TICKET_PARAM_NAME);

		super.successfulAuthentication(request, response, authResult);

		if (sessionMappingStorage != null && casServiceTicket != null && !casServiceTicket.isEmpty()) {
			sessionMappingStorage.addSessionById(casServiceTicket, request.getSession());
		}
	}
}
