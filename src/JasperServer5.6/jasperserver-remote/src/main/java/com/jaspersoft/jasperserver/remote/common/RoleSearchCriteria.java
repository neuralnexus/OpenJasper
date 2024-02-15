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
package com.jaspersoft.jasperserver.remote.common;

import java.util.List;

/**
 * @author Volodya Sabadosh (vsabadosh@jaspersoft.com)
 * @version $Id $
 */
public class RoleSearchCriteria {

    private java.lang.String roleName;

    private java.lang.String tenantId;

    private java.lang.Boolean includeSubOrgs;

    private java.lang.Boolean hasAllUsers;

    private int maxRecords;

    private List<String> usersNames;

    public RoleSearchCriteria() {
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

    public Boolean getIncludeSubOrgs() {
        return includeSubOrgs;
    }

    public void setIncludeSubOrgs(Boolean includeSubOrgs) {
        this.includeSubOrgs = includeSubOrgs;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public Boolean getHasAllUsers() {
        return hasAllUsers;
    }

    public void setHasAllUsers(Boolean hasAllUsers) {
        this.hasAllUsers = hasAllUsers;
    }

    public List<String> getUsersNames() {
        return usersNames;
    }

    public void setUsersNames(List<String> usersNames) {
        this.usersNames = usersNames;
    }
}
