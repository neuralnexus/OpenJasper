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

import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 08.07.2016
 */
public class ClientMultiAxisAggregationLevel extends ClientMultiAxisDatasetLevel {
    private List<ClientDatasetFieldReference> fields = new ArrayList<ClientDatasetFieldReference>();

    public ClientMultiAxisAggregationLevel() {
        super();
    }

    public ClientMultiAxisAggregationLevel(ClientMultiAxisAggregationLevel level) {
        super(level);
        this.fields = copyOf(level.getFields());
    }

    public List<ClientDatasetFieldReference> getFields() {
        return fields;
    }

    public ClientMultiAxisAggregationLevel setFields(List<ClientDatasetFieldReference> fields) {
        if (fields == null) {
            this.fields = new ArrayList<ClientDatasetFieldReference>();
        } else {
            this.fields = fields;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientMultiAxisAggregationLevel that = (ClientMultiAxisAggregationLevel) o;

        return fields.equals(that.fields);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + fields.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ClientMultiAxisAggregationLevel{" +
                "fields=" + fields +
                ", members='" + getMembers() + '\'' +
                '}';
    }

    @Override
    public ClientMultiAxisAggregationLevel deepClone() {
        return new ClientMultiAxisAggregationLevel(this);
    }
}
