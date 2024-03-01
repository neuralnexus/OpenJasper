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
package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;

import java.io.Serializable;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;


/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AdhocFilterAvailableValues implements DeepCloneable<AdhocFilterAvailableValues>, Serializable {
    private Integer total;
    private List<InputControlOption> data;

    public AdhocFilterAvailableValues() {
    }

    public AdhocFilterAvailableValues(Integer total, List<InputControlOption> data) {
        this.total = total;
        this.data = data;
    }

    public AdhocFilterAvailableValues(AdhocFilterAvailableValues source) {
        checkNotNull(source);

        this.total = source.total;
        this.data = copyOf(source.data);
    }

    public Integer getTotal() {
        return total;
    }

    public AdhocFilterAvailableValues setTotal(Integer total) {
        this.total = total;
        return this;
    }

    public List<InputControlOption> getData() {
        return data;
    }

    public AdhocFilterAvailableValues setData(List<InputControlOption> data) {
        this.data = data;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdhocFilterAvailableValues that = (AdhocFilterAvailableValues) o;

        if (total != null ? !total.equals(that.total) : that.total != null) return false;
        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        int result = total != null ? total.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public AdhocFilterAvailableValues deepClone() {
        return new AdhocFilterAvailableValues(this);
    }

    @Override
    public String toString() {
        return "AdhocFilterAvailableValues{" +
                "total=" + total +
                ", data=" + data +
                '}';
    }
}
