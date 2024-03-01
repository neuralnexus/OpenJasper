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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import java.io.Serializable;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;

/**
 * @author Vasyl Spachynskyi
 * @version $Id: Id $
 * @since 08.07.2016
 */
public class ClientDatasetFieldReference implements DeepCloneable<ClientDatasetFieldReference>, Serializable {
    String reference;
    String type;

    public ClientDatasetFieldReference() {
    }

    public ClientDatasetFieldReference(ClientDatasetFieldReference ref) {
        checkNotNull(ref);

        reference = ref.getReference();
        type = ref.getType();
    }

    public String getReference() {
        return reference;
    }

    public ClientDatasetFieldReference setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getType() {
        return type;
    }

    public ClientDatasetFieldReference setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDatasetFieldReference that = (ClientDatasetFieldReference) o;

        if (reference != null ? !reference.equals(that.reference) : that.reference != null) return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = reference != null ? reference.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientDatasetFieldReference{" +
                "reference='" + reference + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public ClientDatasetFieldReference deepClone() {
        return new ClientDatasetFieldReference(this);
    }
}
