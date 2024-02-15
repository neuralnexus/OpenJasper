/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.security.externalAuth.cas;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutHandler;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * This CasSessionFixationProtectionStrategy is linked with CAS SessionMappingStorage in applicationContext.
 * Upon authentication and new session creation, CasSessionFixationProtectionStrategy adds the new session
 * SessionMappingStorage as a CAS ticket-session id pair.  Upon CAS sign-out request, SingleSignOutFilter
 * in JasperReport Server locates the correct session in SessionMappingStorage and invalidates it.
 *
 * SessionMappingStorage should be the same singleton object for CasSessionFixationProtectionStrategy and
 * CAS SingleSignOutFilter.
 *
 * @author dlitvak
 * @version $Id$
 * @since 6.0.1
 */
public class CasSessionFixationProtectionStrategy extends SessionFixationProtectionStrategy implements InitializingBean {
	private static final Logger logger = LogManager.getLogger(CasSessionFixationProtectionStrategy.class);

	/** Mapping of token IDs and session IDs to HTTP sessions */
	private SessionMappingStorage sessionMappingStorage;

	private String artifactParameterName = SingleSignOutHandler.DEFAULT_ARTIFACT_PARAMETER_NAME;
	private String logoutParameterName = SingleSignOutHandler.DEFAULT_LOGOUT_PARAMETER_NAME;
	private boolean artifactParameterOverPost = false;
	private List<String> safeParameters;

	/**
	 * Overriding super.onAuthentication() in order to enter a new session into SessionMappingStorage after the
	 * old session is invalidate.
	 *
	 * @param authentication
	 * @param request
	 * @param response
	 */
	@Override
	public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
		super.onAuthentication(authentication, request, response);

		final HttpSession newSession = request.getSession();
		final String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
		logger.debug("Recording the new session after the previous one was destroyed to prevent session fixation (token " + token + ").");
		if (token != null && !token.trim().isEmpty())
			sessionMappingStorage.addSessionById(token, newSession);
	}

	public void setSessionMappingStorage(SessionMappingStorage casSessionMappingStorage) {
		this.sessionMappingStorage = casSessionMappingStorage;
	}

	public void setArtifactParameterName(String artifactParameterName) {
		this.artifactParameterName = artifactParameterName;
	}

	public void setArtifactParameterOverPost(boolean artifactParameterOverPost) {
		this.artifactParameterOverPost = artifactParameterOverPost;
	}

	public void setLogoutParameterName(String logoutParameterName) {
		this.logoutParameterName = logoutParameterName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.sessionMappingStorage, "sessionMappingStorage property must be specified.  " +
				"It should be the same sessionMappingStorage as that used by CAS SingleSignOutFilter");

		//copied from org.jasig.cas.client.session.SingleSignOutHandler.init()
		if (this.artifactParameterOverPost) {
			this.safeParameters = Arrays.asList(this.logoutParameterName, this.artifactParameterName);
		} else {
			this.safeParameters = Arrays.asList(this.logoutParameterName);
		}
	}
}
