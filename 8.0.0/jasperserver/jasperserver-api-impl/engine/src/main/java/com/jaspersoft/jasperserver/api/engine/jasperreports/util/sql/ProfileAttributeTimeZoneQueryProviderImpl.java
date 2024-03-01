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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util.sql;

import static com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory.SET_LOCAL_TIME_ZONE_IN_SQL;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.jaspersoft.jasperserver.api.common.util.StaticExecutionContextProvider;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchResult;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCategory;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;

public class ProfileAttributeTimeZoneQueryProviderImpl extends TimeZoneQueryProviderImpl {
    private ProfileAttributeService profileAttributeService;
    private ResourceFactory resourceFactory;

    @Override
    public boolean shouldSetLocalTimeZoneInSQL() {
        Object userPrincipal = ProfileAttributeCategory.USER.getPrincipal(getResourceFactory());
        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
                .setEffective(true)
                .setRecursive(true)
                .setNames(Collections.singleton(SET_LOCAL_TIME_ZONE_IN_SQL))
                .build();
        AttributesSearchResult<ProfileAttribute> result = getProfileAttributeService().getProfileAttributesForPrincipal(
                StaticExecutionContextProvider.getExecutionContext(),
                userPrincipal,
                searchCriteria
        );
        Optional<ProfileAttribute> profileAttribute = result.getList().stream()
                .filter(attr -> attr.getAttrName().equals(SET_LOCAL_TIME_ZONE_IN_SQL))
                .findFirst();
        return profileAttribute.map(attribute -> Boolean.valueOf(attribute.getAttrValue())).orElse(false);
    }

	public ProfileAttributeService getProfileAttributeService() {
		return profileAttributeService;
	}

	public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
		this.profileAttributeService = profileAttributeService;
	}

	public ResourceFactory getResourceFactory() {
		return resourceFactory;
	}

	public void setResourceFactory(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}
}
