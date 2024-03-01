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

package com.jaspersoft.jasperserver.api.security.csrf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;

public class JSCorsConfiguration extends CorsConfiguration {

    private ProfileAttributeService profileAttributeService;
    private DomainWhitelistProvider whitelistProvider;

    public DomainWhitelistProvider getWhitelistProvider() {
        return whitelistProvider;
    }

    public ProfileAttributeService getProfileAttributeService() {
        return profileAttributeService;
    }

    public void setWhitelistProvider(DomainWhitelistProvider whitelistProvider) {
        this.whitelistProvider = whitelistProvider;
    }

    private Object principal;
    private static final String CROSS_DOMAIN_WHITELIST_ATTRIB_NAME = "domainWhitelist";

    private List<String> additionalWhitelistAttributes = new ArrayList<String>() {
        {
            add(CROSS_DOMAIN_WHITELIST_ATTRIB_NAME);
        }
    };

    public void setAdditionalWhitelistAttributes(List<String> additionalWhitelistAttributes) {
        if (additionalWhitelistAttributes != null)
            this.additionalWhitelistAttributes.addAll(additionalWhitelistAttributes);
    }

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }

    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    @Override
    public String checkOrigin(String requestOrigin) {

        Pattern whitelistPattern = whitelistProvider.getWhitelistPattern();
        if (!StringUtils.hasText(requestOrigin)) {
            return null;
        }
        if (ObjectUtils.isEmpty(additionalWhitelistAttributes)) {
            return null;
        }

        if (additionalWhitelistAttributes.contains(ALL)) {
            if (this.getAllowCredentials() != Boolean.TRUE) {
                return ALL;
            } else {
                return requestOrigin;
            }
        }
        Matcher matcher = whitelistPattern.matcher(requestOrigin);
        if (matcher.matches()) {
            return requestOrigin;
        }
        return null;

    }

}
