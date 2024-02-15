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

package com.jaspersoft.jasperserver.api.engine.common.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityPersistenceService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: StandardUserPersistenceHandler.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
public class StandardUserPersistenceHandler implements UserPersistenceHandler {
	
	private final static Log log = LogFactory.getLog(StandardUserPersistenceHandler.class);
	
	private SecurityContextProvider securityContextProvider;
	private UserAuthorityPersistenceService userPersistenceService;

	public SecurityContextProvider getSecurityContextProvider() {
		return securityContextProvider;
	}

	public void setSecurityContextProvider(SecurityContextProvider securityContextProvider) {
		this.securityContextProvider = securityContextProvider;
	}

	public UserAuthorityPersistenceService getUserPersistenceService() {
		return userPersistenceService;
	}

	public void setUserPersistenceService(
			UserAuthorityPersistenceService userPersistenceService) {
		this.userPersistenceService = userPersistenceService;
	}

	public String getClientUsername(RepoUser user) {
		return user == null ? null : user.getUsername();
	}

	public RepoUser getPersistentUserFromUsername(String username) {
		return loadPersistentUser(username);
	}

	public RepoUser getPersistentUserFromContext() {
		String contextUsername = getContextUsername();
		
		if (contextUsername == null) {
			if (log.isDebugEnabled()) {
				log.debug("No context user found");
			}

			return null;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Found context user " + contextUsername);
		}
		return loadPersistentUser(contextUsername);
	}

	protected String getContextUsername() {
		return getSecurityContextProvider().getContextUsername();
	}
	
	protected RepoUser loadPersistentUser(String username) {
		RepoUser persistentUser = userPersistenceService.getPersistentUser(username);
		if (persistentUser == null) {
			throw new JSException("jsexception.no.such.user", new Object[]{username});
		}
		return persistentUser;
	}

}
