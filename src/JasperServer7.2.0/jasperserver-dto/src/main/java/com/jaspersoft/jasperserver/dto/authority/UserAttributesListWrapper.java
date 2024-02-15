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
package com.jaspersoft.jasperserver.dto.authority;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author: Zakhar.Tomchenco
 */
@XmlRootElement(name = "attributes")
public class UserAttributesListWrapper implements DeepCloneable<UserAttributesListWrapper> {
    private List<ClientAttribute> profileAttributes;

    public UserAttributesListWrapper() {
    }

    public UserAttributesListWrapper(List<ClientAttribute> attributes) {
        this.profileAttributes = new ArrayList<ClientAttribute>(attributes);
    }

    public UserAttributesListWrapper(UserAttributesListWrapper other) {
        checkNotNull(other);

        this.profileAttributes = copyOf(other.getProfileAttributes());
    }

    @Override
    public UserAttributesListWrapper deepClone() {
        return new UserAttributesListWrapper(this);
    }

    @XmlElement(name = "attribute")
    public List<ClientAttribute> getProfileAttributes() {
        return profileAttributes;
    }

    public UserAttributesListWrapper setProfileAttributes(List<ClientAttribute> profileAttributes) {
        this.profileAttributes = profileAttributes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAttributesListWrapper that = (UserAttributesListWrapper) o;

        return profileAttributes != null ? profileAttributes.equals(that.profileAttributes) : that.profileAttributes == null;
    }

    @Override
    public int hashCode() {
        return profileAttributes != null ? profileAttributes.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserAttributesListWrapper{" +
                "profileAttributes=" + profileAttributes +
                '}';
    }
}
