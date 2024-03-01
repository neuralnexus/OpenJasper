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
package com.jaspersoft.jasperserver.inputcontrols.cascade;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: CascadeResourceNotFoundException.java 22694 2012-03-21 13:08:28Z ykovalchyk $
 */
public class CascadeResourceNotFoundException extends Exception{
    final private String resourceUri;
    final private String resourceType;

    public CascadeResourceNotFoundException(String resourceUri, String resourceType){
        super("Resource " + resourceUri + " of type " + resourceType + " not found");
        this.resourceUri = resourceUri;
        this.resourceType = resourceType;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public String getResourceType() {
        return resourceType;
    }
}
