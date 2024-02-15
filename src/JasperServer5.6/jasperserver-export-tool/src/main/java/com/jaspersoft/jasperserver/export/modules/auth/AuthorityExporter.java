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

package com.jaspersoft.jasperserver.export.modules.auth;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.auth.beans.RoleBean;
import com.jaspersoft.jasperserver.export.modules.auth.beans.UserBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AuthorityExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class AuthorityExporter extends BaseExporterModule {

	private String usersArgument;
	private String rolesArgument;
	private String includeRoleUsersArgument;
	private String includeUsersRolesArgument;
	private AuthorityModuleConfiguration configuration;

	private Map users;
	private Map roles;
	private boolean includeRoleUsers;
	private boolean includeUsersRoles;

	public String getIncludeRoleUsersArgument() {
		return includeRoleUsersArgument;
	}

	public void setIncludeRoleUsersArgument(String includeRoleUserArgument) {
		this.includeRoleUsersArgument = includeRoleUserArgument;
	}

	public String getRolesArgument() {
		return rolesArgument;
	}

	public void setRolesArgument(String rolesArgument) {
		this.rolesArgument = rolesArgument;
	}

	public String getUsersArgument() {
		return usersArgument;
	}

	public void setUsersArgument(String usersArgument) {
		this.usersArgument = usersArgument;
	}

	public AuthorityModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AuthorityModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public String getIncludeUsersRolesArgument() {
        return includeUsersRolesArgument;
    }

    public void setIncludeUsersRolesArgument(String includeUsersRolesArgument) {
        this.includeUsersRolesArgument = includeUsersRolesArgument;
    }

    public void init(ExporterModuleContext moduleContext) {
		super.init(moduleContext);

        roles = new LinkedHashMap();
        users = new LinkedHashMap();

		initUsers();
		initRoles();
	}

	protected void initUsers() {
        includeUsersRoles = hasParameter(getIncludeUsersRolesArgument());

		if (exportEverything || hasParameter(getUsersArgument())) {
			String[] userNames = getParameterValues(getUsersArgument());
			if (exportEverything || userNames == null) {
				List usersList = configuration.getAuthorityService().getUsers(executionContext, null);
				for (Iterator it = usersList.iterator(); it.hasNext();) {
					User user = (User) it.next();
					addUser(user);
				}
			} else {
				for (int i = 0; i < userNames.length; i++) {
					addUser(userNames[i]);
				}
			}
		}
	}

	protected void addUser(User user) {
		//fetch again the user so that its profile attributes are set
		addUser(user.getUsername());
	}
	
	protected void addUser(String userName) {
		User user = getUser(userName);
		users.put(userName, user);
        if (includeUsersRoles){
            for (Object role : user.getRoles()){
                roles.put(getName((Role) role), role);
            }
        }
	}

	protected User getUser(String userName) {
		User user = configuration.getAuthorityService().getUser(executionContext, userName);
		if (user == null) {
			throw new JSException("jsexception.no.such.user", new Object[] {userName});
		}
		return user;
	}

	protected void initRoles() {
		includeRoleUsers = hasParameter(getIncludeRoleUsersArgument());

		if (exportEverything || hasParameter(getRolesArgument())) {
			String[] roleNames = getParameterValues(getRolesArgument());
			if (exportEverything || roleNames == null) {
				List rolesList = configuration.getAuthorityService().getRoles(executionContext, null);
				for (Iterator it = rolesList.iterator(); it.hasNext();) {
					Role role = (Role) it.next();
					addRole(role);
				}
			} else {
				for (int i = 0; i < roleNames.length; i++) {
					addRole(roleNames[i]);
				}
			}
		}
	}
	
	protected void addRole(Role role) {
		if (roles.put(getName(role), role) == null) {
			addRoleUsers(role);
		}
	}

	protected String getName(Role role) {
		return role.getRoleName();
	}
	
	protected void addRole(String name) {
		if (!roles.containsKey(name)) {
			Role role = getRole(name);
			roles.put(name, role);
			addRoleUsers(role);
		}		
	}

	protected Role getRole(String name) {
		Role role = configuration.getAuthorityService().getRole(executionContext, name);
		if (role == null) {
			throw new JSException("jsexception.no.such.role", new Object[] {name});
		}
		return role;
	}

	protected void addRoleUsers(Role role) {
		if (includeRoleUsers) {
			String roleName = getName(role);
			List usersInRole = configuration.getAuthorityService().getUsersInRole(executionContext, roleName);
			if (usersInRole != null && !usersInRole.isEmpty()) {
				for (Iterator userIt = usersInRole.iterator(); userIt.hasNext();) {
					User user = (User) userIt.next();
					//fetch again the user so that its attributes are set
					addUser(user);
				}
			}
		}		
	}

	protected boolean hasUsers() {
		return !users.isEmpty();
	}
	
	protected boolean hasRoles() {
		return !roles.isEmpty();
	}

	protected boolean isToProcess() {
		return hasRoles() || hasUsers();
	}
	
	public void process() {
		if (hasUsers()) {
			exportUsers();
		}
		
		if (hasRoles()) {
			exportRoles();
		}
	}

	protected void exportRoles() {
		mkdir(configuration.getRolesDirName());
		
		for (Iterator it = roles.values().iterator(); it.hasNext();) {
			Role role = (Role) it.next();
			exportRole(role);
		}
	}

	protected void exportRole(Role role) {
		commandOut.info("Exporting role " + role.getRoleName());

		RoleBean roleBean = new RoleBean();
		roleBean.copyFrom(role);
		
		serialize(roleBean, configuration.getRolesDirName(), getRoleFile(role), configuration.getSerializer());
		
		addIndexElement(role);
	}

	protected Element addIndexElement(Role role) {
		Element roleElement = getIndexElement().addElement(configuration.getRoleIndexElementName());
		roleElement.setText(role.getRoleName());
		return roleElement;
	}

	protected void exportUsers() {
		mkdir(configuration.getUsersDirName());
		
		for (Iterator it = users.values().iterator(); it.hasNext();) {
			User user = (User) it.next();
			export(user);
		}
	}

	protected void export(User user) {
		commandOut.info("Exporting user " + user.getUsername());

		UserBean userBean = new UserBean();
		userBean.copyFrom(user);
		
		serialize(userBean, configuration.getUsersDirName(), getUserFile(user), configuration.getSerializer());
		
		addIndexElement(user);
	}

	protected Element addIndexElement(User user) {
		Element userElement = getIndexElement().addElement(configuration.getUserIndexElementName());
		userElement.setText(user.getUsername());
		return userElement;
	}

	protected String getUserFile(User user) {
		return user.getUsername() + ".xml";
	}

	protected String getRoleFile(Role role) {
		return role.getRoleName() + ".xml";
	}

}
