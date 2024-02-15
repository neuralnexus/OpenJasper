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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util;

import java.util.Date;

/**
 * <p>Indicates if update of creationDate and updatedDate is required.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id: UpdateDatesIndicator.java 47331 2014-07-18 09:13:06Z kklein $
 * @since 4.7.0
 */
public class UpdateDatesIndicator {
    private static final ThreadLocal<Date> operationalDate = new ThreadLocal<Date>();
    private static final ThreadLocal<Boolean> useOperationalForUpdateDate = new ThreadLocal<Boolean>();

    /**
     * Initialize the indicator with date to be used.
     *
     * @param operationalDate the operational date.
     * @param useOperationalForUpdateDate indicates if we should use operational date for updateDate.
     */
    public static void required(Date operationalDate, boolean useOperationalForUpdateDate) {
        UpdateDatesIndicator.operationalDate.set(operationalDate);
        UpdateDatesIndicator.useOperationalForUpdateDate.set(useOperationalForUpdateDate);
    }

    /**
     * Cleans the indicator.
     */
    public static void clean() {
        operationalDate.set(null);
        useOperationalForUpdateDate.set(null);
    }

    /**
     * Returns <code>true</code> if creationDate and/or updateDate should be updated, <code>false</code> otherwise.
     *
     * @return <code>true</code> if creationDate and/or updateDate should be updated, <code>false</code> otherwise.
     */
    public static boolean shouldUpdate() {
        return operationalDate.get() != null;
    }

    /**
     * Returns operational date.
     *
     * @return operational date.
     */
    public static Date getOperationalDate() {
        return operationalDate.get();
    }

    /**
     * Returns <code>true</code> if we should use operational date for updateDate, <code>false</code> otherwise.
     *
     * @return <code>true</code> if we should use operational date for updateDate, <code>false</code> otherwise.
     */
    public static boolean useOperationalForUpdateDate() {
        return useOperationalForUpdateDate.get();
    }
}
