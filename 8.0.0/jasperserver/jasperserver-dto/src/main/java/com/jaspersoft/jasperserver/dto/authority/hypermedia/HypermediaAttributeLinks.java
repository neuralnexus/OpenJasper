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
package com.jaspersoft.jasperserver.dto.authority.hypermedia;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;


/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id $
 */
@XmlRootElement(name = "_links")
public class HypermediaAttributeLinks implements DeepCloneable<HypermediaAttributeLinks> {
    private Link permission;

    public HypermediaAttributeLinks() {
    }

    public HypermediaAttributeLinks(HypermediaAttributeLinks other) {
        checkNotNull(other);

        permission = copyOf(other.getPermission());
    }

    @Override
    public HypermediaAttributeLinks deepClone() {
        return new HypermediaAttributeLinks(this);
    }

    @XmlElement(name = "permission")
    public Link getPermission() {
        return permission;
    }

    public HypermediaAttributeLinks setPermission(Link permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HypermediaAttributeLinks)) return false;

        HypermediaAttributeLinks that = (HypermediaAttributeLinks) o;

        return permission != null ? permission.equals(that.permission) : that.permission == null;
    }

    @Override
    public int hashCode() {
        return permission != null ? permission.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "permission=" + permission +
                '}';
    }
}
