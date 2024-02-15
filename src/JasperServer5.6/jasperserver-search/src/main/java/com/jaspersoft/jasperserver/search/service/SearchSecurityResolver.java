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

package com.jaspersoft.jasperserver.search.service;

import com.jaspersoft.jasperserver.search.common.RoleAccess;

/**
 * Security role resolver.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public interface SearchSecurityResolver {

    /**
     * Detects if current user has specified role access.
     *
     * @param roleAccess the role access.
     *
     * @return <code>true</code> if current user has specified role access, <code>false</code> otherwise.
     */
    boolean hasAccess(RoleAccess roleAccess);
}
