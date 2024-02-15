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
package com.jaspersoft.jasperserver.api.common.util;

/**
 * <p>The general format interface which have to be used in Jasper Report Server
 * to format different data type to string representation.
 *
 * <p>We have to use our own interface instead of using default java <code>java.text.Format</code>
 * because there are cases, that requires having <code>null</code> as formatted value. Unfortunately Java <code>java.text.Format</code>
 * can not return <code>null</code> value even in the case when the input is null.
 *
 * @author Vasyl Spachynskyi
 * @since 30.07.2018
 */
public interface Formatter {

    /**
     * Formats an object to produce a string.
     *
     * @param     obj The object to format
     * @return    Formatted string or <code>null</code>.
     */
    String format(Object obj);
}