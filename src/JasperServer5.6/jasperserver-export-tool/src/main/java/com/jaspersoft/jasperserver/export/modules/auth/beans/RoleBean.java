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
package com.jaspersoft.jasperserver.export.modules.auth.beans;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;

/**
 * @author tkavanagh
 * @version $Id: RoleBean.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RoleBean {

	private String roleName;
	private String tenantId;
	private boolean externallyDefined = false;

	public void copyFrom(Role role) {
		setRoleName(role.getRoleName());
		setTenantId(role.getTenantId());
		setExternallyDefined(role.isExternallyDefined());
	}

	public void copyTo(Role role) {
		role.setRoleName(getRoleName());
		role.setTenantId(getTenantId());
		role.setExternallyDefined(isExternallyDefined());
	}
	
	public boolean isExternallyDefined() {
		return externallyDefined;
	}
	
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
}
