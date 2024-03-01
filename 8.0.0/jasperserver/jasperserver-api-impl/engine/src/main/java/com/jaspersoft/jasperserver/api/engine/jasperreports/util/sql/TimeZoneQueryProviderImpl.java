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

import java.util.Map;
import java.util.TimeZone;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
public class TimeZoneQueryProviderImpl implements TimeZoneQueryProvider {
    private Map<String, String> productNameToQuery;
    private String timeZonePlaceholder;
    private boolean defaultSetTimeZoneInSQL;


    @Override
    public String getAlterQuery(String productName, TimeZone timeZone) {
        if (productName == null) throw new IllegalArgumentException("productName is null");
        if (timeZone == null) throw new IllegalArgumentException("timeZone is null");

        String query = productNameToQuery.get(productName);
        if (query == null) return null;
        if (!query.contains(timeZonePlaceholder)) {
            throw new IllegalArgumentException(String.format(
                    "Query \"%s\" has an invalid time zone placeholder, right placeholder should be \"%s\"",
                    query,
                    timeZonePlaceholder
            ));
        }

        return query.replace(timeZonePlaceholder, timeZone.getID());
    }
    
    /**
     * return default setting from property
     */
    @Override
    public boolean shouldSetLocalTimeZoneInSQL() {
        return isDefaultSetTimeZoneInSQL();
    }

    public void setTimeZonePlaceholder(String timeZonePlaceholder) {
        this.timeZonePlaceholder = timeZonePlaceholder;
    }

    public void setProductNameToQuery(Map<String, String> productNameToQuery) {
        this.productNameToQuery = productNameToQuery;
    }

	public boolean isDefaultSetTimeZoneInSQL() {
		return defaultSetTimeZoneInSQL;
	}

	public void setDefaultSetTimeZoneInSQL(boolean defaultSetTimeZoneInSQL) {
		this.defaultSetTimeZoneInSQL = defaultSetTimeZoneInSQL;
	}
}
