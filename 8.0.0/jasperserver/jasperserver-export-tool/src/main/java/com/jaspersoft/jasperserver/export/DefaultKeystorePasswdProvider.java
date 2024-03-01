/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.export.modules.auth.AuthorityModuleConfiguration;

import java.util.List;
import java.util.Locale;

public class DefaultKeystorePasswdProvider {
    private AuthorityModuleConfiguration authorityConfiguration;

    private String adminRole;
    private String adminUser;
    private String password;

    public void setAuthorityConfiguration(AuthorityModuleConfiguration authorityConfiguration) {
        this.authorityConfiguration = authorityConfiguration;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultPassword(){
        final UserAuthorityService authority = authorityConfiguration.getAuthorityService();
        ExecutionContextImpl context = new ExecutionContextImpl();
        context.setLocale(Locale.getDefault());
        if (adminUser != null) {
            final User user = authority.getUser(context, adminUser);
            return user == null ? null : user.getPassword();

        } else if (adminRole != null) {
            final List<User> users = authority.getUsersInRole(context, adminRole);
            return users.isEmpty() ? null : users.iterator().next().getPassword();

        } else {

            return password;
        }
    }
}
