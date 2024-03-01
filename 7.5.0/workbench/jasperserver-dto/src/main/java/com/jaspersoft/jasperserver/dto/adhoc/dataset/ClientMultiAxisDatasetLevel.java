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

package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 08.04.2016
 */
public abstract class ClientMultiAxisDatasetLevel implements DeepCloneable<ClientMultiAxisDatasetLevel> {

    private List<String> members = new ArrayList<String>();

    public ClientMultiAxisDatasetLevel() {
    }

    public ClientMultiAxisDatasetLevel(ClientMultiAxisDatasetLevel source) {
        checkNotNull(source);

        this.members = copyOf(source.getMembers());
    }

    @XmlElementWrapper(name = "members")
    @XmlElement(name = "member")
    public List<String> getMembers() {
        return members;
    }

    public ClientMultiAxisDatasetLevel setMembers(List<String> members) {
        if (members == null) {
            this.members = new ArrayList<String>();
        } else {
            this.members = members;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientMultiAxisDatasetLevel that = (ClientMultiAxisDatasetLevel) o;

        return members.equals(that.members);
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }

}
