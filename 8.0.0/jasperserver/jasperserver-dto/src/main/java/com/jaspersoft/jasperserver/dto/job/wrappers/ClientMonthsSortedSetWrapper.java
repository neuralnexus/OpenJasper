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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * This class is needed because of bug in JAXB.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 * @XmlElementWrapper doesn't support @XmlJavaTypeAdapter
 */
@XmlRootElement(name = "months")
public class ClientMonthsSortedSetWrapper implements DeepCloneable<ClientMonthsSortedSetWrapper> {

    private SortedSet<String> mongths;

    public ClientMonthsSortedSetWrapper() {
    }

    public ClientMonthsSortedSetWrapper(SortedSet<String> months) {
        if (months == null) {
            return;
        }
        this.mongths = new TreeSet<String>(months);
    }

    public ClientMonthsSortedSetWrapper(ClientMonthsSortedSetWrapper other) {
        checkNotNull(other);

        mongths = copyOf(other.getMongths());
    }

    @XmlElement(name = "month")
    public SortedSet<String> getMongths() {
        return mongths;
    }

    public ClientMonthsSortedSetWrapper setMongths(SortedSet<String> months) {
        this.mongths = months;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientMonthsSortedSetWrapper)) return false;

        ClientMonthsSortedSetWrapper that = (ClientMonthsSortedSetWrapper) o;

        return !(getMongths() != null ? !getMongths().equals(that.getMongths()) : that.getMongths() != null);

    }

    @Override
    public int hashCode() {
        return getMongths() != null ? getMongths().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientMonthsSortedSetWrapper{" +
                "mongths=" + mongths +
                '}';
    }

    @Override
    public ClientMonthsSortedSetWrapper deepClone() {
        return new ClientMonthsSortedSetWrapper(this);
    }
}
