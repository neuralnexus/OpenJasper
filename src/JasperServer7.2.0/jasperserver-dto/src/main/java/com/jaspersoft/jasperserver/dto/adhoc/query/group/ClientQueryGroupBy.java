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
package com.jaspersoft.jasperserver.dto.adhoc.query.group;

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckAllGroupPosition;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author Andriy Godovanets
 */
@XmlRootElement
public class ClientQueryGroupBy implements ClientGroupBy<ClientGroupAxis> {
    @Valid
    @CheckAllGroupPosition
    private List<ClientQueryGroup> groups;

    public ClientQueryGroupBy() {
        groups = new ArrayList<ClientQueryGroup>();
    }

    public ClientQueryGroupBy(ClientQueryGroupBy source) {
        checkNotNull(source);

        groups = (List<ClientQueryGroup>) copyOf(source.getGroups());
    }

    @Override
    public ClientQueryGroupBy deepClone() {
        return new ClientQueryGroupBy(this);
    }

    @XmlTransient
    public ClientGroupAxis getGroupAxis() {
        return new ClientGroupAxis(this.groups);
    }

    @Override
    public ClientGroupAxis getAxis(ClientGroupAxisEnum name) {
        if (!ClientGroupAxisEnum.GROUPS.equals(name)) {
            throw new IllegalArgumentException(String.format("unsupported axis %s for FlatGroupBy", name.toString()));
        }
        return getGroupAxis();
    }

    @Override
    public ClientGroupAxis getAxis(int index) {
        if (index != 0) {
            throw new IllegalArgumentException(String.format("unsupported axis index: %d", index));
        }
        return getGroupAxis();
    }

    @XmlTransient
    @Override
    public List<ClientGroupAxis> getAxes() {
        return Collections.singletonList(getGroupAxis());
    }

    public ClientQueryGroupBy setGroups(List<? extends ClientQueryGroup> groups) {
        this.groups = groups != null ? (List<ClientQueryGroup>) groups : new ArrayList<ClientQueryGroup>();
        return this;
    }

    public List<? extends ClientQueryGroup> getGroups() {
        return groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientQueryGroupBy that = (ClientQueryGroupBy) o;

        return groups.equals(that.groups);

    }

    @Override
    public int hashCode() {
        return groups.hashCode();
    }

    @Override
    public String toString() {
        return "ClientQueryGroupBy{" +
                "groups=" + groups +
                '}';
    }
}
