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

package com.jaspersoft.jasperserver.dto.executions;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 19.05.2016
 */
public class QueryResultDataMediaType {
    public final static String FLAT_DATA = "flatData";
    public final static String MULTI_LEVEL_DATA = "multiLevelData";
    public final static String MULTI_AXIS_DATA = "multiAxisData";

    public final static String APPLICATION = "application";
    public final static String JSON = "+json";
    public final static String XML = "+xml";

    public final static String FLAT_DATA_JSON = APPLICATION + "/" + FLAT_DATA + JSON;
    public final static String MULTI_LEVEL_DATA_JSON = APPLICATION + "/" + MULTI_LEVEL_DATA + JSON;
    public final static String MULTI_AXIS_DATA_JSON = APPLICATION + "/" + MULTI_AXIS_DATA + JSON;

    public final static String FLAT_DATA_XML = APPLICATION + "/" + FLAT_DATA + XML;
    public final static String MULTI_LEVEL_DATA_XML = APPLICATION + "/" + MULTI_LEVEL_DATA + XML;
    public final static String MULTI_AXIS_DATA_XML = APPLICATION + "/" + MULTI_AXIS_DATA + XML;

}
