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

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchResult;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCategory;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author dlitvak
 * @version $Id$
 */
public class DomainWhitelistProviderImpl implements DomainWhitelistProvider {
    private static final Logger log = LogManager.getLogger(DomainWhitelistProviderImpl.class);

    private static final String CROSS_DOMAIN_WHITELIST_ATTRIB_NAME = "domainWhitelist";

    private final List<String> additionalWhitelistAttributes;

    private ResourceFactory resourceFactory;

    public DomainWhitelistProviderImpl() {
        additionalWhitelistAttributes = new ArrayList<>();
        additionalWhitelistAttributes.add(CROSS_DOMAIN_WHITELIST_ATTRIB_NAME);
    }

    public void setAdditionalWhitelistAttributes(List<String> additionalWhitelistAttributes) {
        if (additionalWhitelistAttributes != null)
            this.additionalWhitelistAttributes.addAll(additionalWhitelistAttributes);
    }

    private ProfileAttributeService profileAttributeService;

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }

    @Override
    public Pattern getWhitelistPattern() {
        ExecutionContextImpl context = new ExecutionContextImpl();

        Object principal = getPrincipal();

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
                .setNames(new HashSet<>(additionalWhitelistAttributes))
                .setEffective(true).build();
        AttributesSearchResult<ProfileAttribute> result =
                profileAttributeService.getProfileAttributesForPrincipal(context, principal, searchCriteria);

        return convertListToPattern(result.getList());
    }

    private Pattern convertListToPattern(List<ProfileAttribute> whitelist) {
        if (whitelist == null)
            return null;

        StringBuilder buf = new StringBuilder();
        for (ProfileAttribute pa : whitelist) {
            if (buf.length() > 0)
                buf.append("|");
            String paVal = pa.getAttrValue();
            if (!(paVal.startsWith("^") && paVal.endsWith("$"))) {
                buf.append("^").append(paVal.replace(".", "\\.").replace("*", ".*")).append("$");
            }
            else {
                buf.append(paVal);
            }
        }

        return Pattern.compile(buf.toString(), Pattern.CASE_INSENSITIVE);
    }

    private Object getPrincipal() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        Object principal = null;

        if (authenticationToken != null) {
            principal = authenticationToken.getPrincipal();
        }
        if (principal == null) {
            principal = ProfileAttributeCategory.SERVER.getPrincipal(resourceFactory);
            log.debug("Principal is null, trying to get SERVER principal");
        }
        log.debug("Obtained {} principal", principal);

        return principal;
    }

    public void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }
}
