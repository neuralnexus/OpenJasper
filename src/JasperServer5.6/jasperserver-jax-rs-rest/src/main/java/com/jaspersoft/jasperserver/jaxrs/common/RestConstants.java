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

package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.war.common.JasperServerHttpConstants;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: RestConstants.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface RestConstants {
    String HEADER_START_INDEX = "Start-Index";
    String HEADER_NEXT_OFFSET = "Next-Offset";
    String HEADER_RESULT_COUNT = "Result-Count";
    String HEADER_TOTAL_COUNT = "Total-Count";

    String HEADER_ACCEPT_TIMEZONE = JasperServerHttpConstants.HEADER_ACCEPT_TIMEZONE;

    String QUERY_PARAM_SEARCH_QUERY = "q";
    String QUERY_PARAM_OFFSET = "offset";
    String QUERY_PARAM_LIMIT = "limit";
    String QUERY_PARAM_SORT_BY = "sortBy";
    String QUERY_PARAM_EXPANDED = "expanded";

    String QUERY_PARAM_CREATE_FOLDERS = "createFolders";
}
