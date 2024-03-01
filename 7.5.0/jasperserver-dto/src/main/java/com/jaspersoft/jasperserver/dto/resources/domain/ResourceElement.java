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
package com.jaspersoft.jasperserver.dto.resources.domain;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceElement<T extends ResourceElement<T>> extends SchemaElement<T> {
    private String sourceName;

    public ResourceElement(){}
    public ResourceElement(ResourceElement<T> source) {
        super(source);
        this.sourceName = source.getSourceName();
    }

    @Override
    public T deepClone() {
        return (T) new ResourceElement(this);
    }

    public String getSourceName() {
        return sourceName;
    }

    public T setSourceName(String sourceName) {
        this.sourceName = sourceName;
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceElement)) return false;
        if (!super.equals(o)) return false;

        ResourceElement that = (ResourceElement) o;

        if (sourceName != null ? !sourceName.equals(that.sourceName) : that.sourceName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sourceName != null ? sourceName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceElement{" +
                "sourceName='" + sourceName + '\'' +
                "} " + super.toString();
    }
}
