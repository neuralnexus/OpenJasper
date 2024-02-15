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

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.auth.beans.RoleBean;
import com.jaspersoft.jasperserver.export.modules.auth.beans.UserBean;
import com.jaspersoft.jasperserver.export.modules.common.ProfileAttributeBean;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AuthorityImporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class AuthorityImporter extends BaseImporterModule {
	
	private AuthorityModuleConfiguration configuration;
	private String updateArg;
    private String updateCoreUsersArg;
	private String skipUpdateArg;
	private boolean update;
    private boolean updateCoreUsers;

	protected static class ImportHandler implements AuthorityImportHandler {

		private final Map roles;
		
		protected ImportHandler(List roles) {
			this.roles = new HashMap();

			for (Iterator it = roles.iterator(); it.hasNext();) {
				Role role = (Role) it.next();
				TenantQualifiedName roleQName = new TenantQualifiedName(
						role.getTenantId(), role.getRoleName());
				this.roles.put(roleQName, role);
			}
		}
		
		public Role resolveRole(TenantQualifiedName rolename) {
			Role role = (Role) roles.get(rolename);
			return role;
		}
		
	}
	
	private ImportHandler importHandler;
	
	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
		update = getUpdateFlag();
        updateCoreUsers = getUpdateCoreUsersFlag();
	}

	public List<String> process() {
		importRoles();
		
		// init the import handler after the roles are imported
		initImportHandler();
		
		importUsers();
        return null;
	}

	protected void importRoles() {
		for (Iterator it = indexElement.elementIterator(configuration.getRoleIndexElementName()); it.hasNext(); ) {
			Element roleElement = (Element) it.next();
			processRole(roleElement);
		}
	}

	protected void processRole(Element roleElement) {
		String roleName = roleElement.getText();
		String file = getRoleFile(roleName);
		RoleBean roleBean = (RoleBean) deserialize(
				configuration.getRolesDirName(), file, 
				configuration.getSerializer());
		if (alreadyExists(roleBean)) {
			commandOut.warn("Role " + roleBean.getRoleName() + " already exists, skipping.");
		} else {
			Role role = createRole(roleBean);
			saveRole(role);
			
			commandOut.info("Created role " + role.getRoleName());
		}
	}
	
	protected boolean alreadyExists(RoleBean roleBean) {
		return configuration.getAuthorityService().getRole(executionContext, roleBean.getRoleName()) != null;
	}

	protected Role createRole(RoleBean roleBean) {
		Role role = configuration.getAuthorityService().newRole(executionContext);
		roleBean.copyTo(role);
		return role;
	}

	protected void saveRole(Role role) {
		configuration.getAuthorityService().putRole(executionContext, role);
	}
	
	protected void initImportHandler() {
		List allRoles = configuration.getAuthorityService().getRoles(executionContext, null);
		importHandler = new ImportHandler(allRoles);
	}

	protected void importUsers() {
		for (Iterator it = indexElement.elementIterator(configuration.getUserIndexElementName()); it.hasNext(); ) {
			Element userElement = (Element) it.next();
			processUser(userElement);
		}
	}

    protected void processUser(Element userElement) {
		String username = userElement.getText();
		String file = getUserFile(username);
		UserBean userBean = (UserBean) deserialize(configuration.getUsersDirName(), file, 
				configuration.getSerializer());

		if (!alreadyExists(userBean)) {
            User user = createUser(userBean);
            saveUser(user);
            saveUserAttributes(user, userBean.getAttributes());

            commandOut.info("Created user " + userBean.getUsername());
        } else if (update || (updateCoreUsers && isCoreUser(userBean))) {
			User user = createUser(userBean);
			updateUser(userBean.getUsername(),user);
			saveUserAttributes(user, userBean.getAttributes());

            commandOut.info("Updated user " + userBean.getUsername());

        } else {
            commandOut.warn("User " + userBean.getUsername() + " already exists, skipping.");
		}
	}

    protected boolean isCoreUser(UserBean userBean) {
        if (userBean.getUsername().equals("jasperadmin")) return true;
        return false;
    }


	protected boolean alreadyExists(UserBean userBean) {
		return configuration.getAuthorityService().getUser(executionContext, userBean.getUsername()) != null;
	}

	protected User createUser(UserBean userBean) {
		User user = configuration.getAuthorityService().newUser(executionContext);
		userBean.copyTo(user, importHandler);		
		return user;
	}
	
	protected void saveUser(User user) {
		configuration.getAuthorityService().putUser(executionContext, user);
	}
	
	protected void updateUser(String userName, User aUser) {
		configuration.getAuthorityService().updateUser(executionContext, userName, aUser);
	}

	protected void saveUserAttributes(User user, ProfileAttributeBean[] attributes) {
		if (attributes != null && attributes.length > 0) {
			for (int i = 0; i < attributes.length; i++) {
				ProfileAttributeBean profileAttributeBean = attributes[i];
				saveUserAttribute(user, profileAttributeBean);
			}
		}
	}

	protected void saveUserAttribute(User user,
			ProfileAttributeBean attributeBean) {
		ProfileAttributeService attributeService = configuration.getAttributeService();
		ProfileAttribute attribute = attributeService.newProfileAttribute(executionContext);
		attribute.setPrincipal(user);
		attributeBean.copyTo(attribute);
		attributeService.putProfileAttribute(executionContext, attribute);
	}

	protected boolean getUpdateFlag() {
		return hasParameter(getUpdateArg()) && !hasParameter((getSkipUpdateArg()));
	}

    protected boolean getUpdateCoreUsersFlag() {
        return hasParameter(getUpdateCoreUsersArg());
    }

	protected boolean isUpdate() {
		return update;
	}

	protected boolean isUpdateCoreUsers() {
		return updateCoreUsers;
	}


	protected String getUserFile(String username) {
		return username + ".xml";
	}

	protected String getRoleFile(String roleName) {
		return roleName + ".xml";
	}

	public AuthorityModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AuthorityModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public String getUpdateArg() {
        return updateArg;
    }

    public void setUpdateArg(String updateArg) {
        this.updateArg = updateArg;
    }

    public String getSkipUpdateArg() {
        return skipUpdateArg;
    }

    public void setSkipUpdateArg(String skipUpdateArg) {
        this.skipUpdateArg = skipUpdateArg;
    }

    public String getUpdateCoreUsersArg() {
        return updateCoreUsersArg;
    }

    public void setUpdateCoreUsersArg(String updateCoreUsersArg) {
        this.updateCoreUsersArg = updateCoreUsersArg;
    }
}
