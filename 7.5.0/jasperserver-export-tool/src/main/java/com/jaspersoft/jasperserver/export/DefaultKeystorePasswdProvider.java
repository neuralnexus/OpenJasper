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
