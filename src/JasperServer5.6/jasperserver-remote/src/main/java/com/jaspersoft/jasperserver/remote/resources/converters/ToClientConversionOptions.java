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
package com.jaspersoft.jasperserver.remote.resources.converters;

/**
 * <p>Options set for to client conversion.</p>
 * Currently it includes expanded option only, but it can be extended later.
 * For instance it can specify what exactly fields are required and what fields should be excluded.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ToClientConversionOptions.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ToClientConversionOptions {
    private boolean expanded;

    public static ToClientConversionOptions getDefault(){
        return new ToClientConversionOptions();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ToClientConversionOptions setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToClientConversionOptions options = (ToClientConversionOptions) o;

        if (expanded != options.expanded) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (expanded ? 1 : 0);
    }

    @Override
    public String toString() {
        return "ToClientConversionOptions{" +
                "expanded=" + expanded +
                '}';
    }
}
