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
package com.jaspersoft.jasperserver.war.model;


/**
 * The base interface for Tree data provider factory.
 * Implementing class returns data provider instances by requested type,
 * according to the configuration
 * 
 * @author asokolnikov
 */
public interface TreeDataProviderFactory {

    /**
     * Returns an instance of TreeDataProvider by requested type
     * @param type data provider type
     * @return new instance of TreeDataProvider implementor
     */
    public TreeDataProvider getDataProvider(String type);
    
}
