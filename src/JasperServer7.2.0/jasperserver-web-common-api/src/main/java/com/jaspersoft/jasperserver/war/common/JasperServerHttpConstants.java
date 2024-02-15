/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.war.common;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public interface JasperServerHttpConstants {
    //Forwarded parameter was added 'cos WebSphere erase parameters from request on forward which results in bug JRS-10031
    //Do not remove this to avoid possible issues with WebSphere
    String FORWARDED_PARAMETERS = "forwardedParameters";

    // HEADERS
    String HEADER_ACCEPT_TIMEZONE = "Accept-Timezone";
    String HEADER_RESPONSE_TIMEZONE = "Timezone";
}
