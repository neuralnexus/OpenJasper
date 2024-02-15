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
package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.common.ResourceLocation;

import javax.xml.bind.annotation.XmlRootElement;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "reference")
public class ClientReference implements ClientReferenceableDataSource, ClientReferenceableDataType,
        ClientReferenceableFile, ClientReferenceableQuery, ClientReferenceableInputControl, ClientReferenceableListOfValues,
        ClientReferenceableMondrianConnection, ClientReferenciableOlapConnection, ResourceLocation {

    private String uri;

    public ClientReference() {
    }

    public ClientReference(String uri) {
        this.uri = uri;
    }

    public ClientReference(ClientReference other) {
        checkNotNull(other);

        this.uri = other.getUri();
    }

    public String getUri() {
        return uri;
    }

    public ClientReference setUri(String referenceUri) {
        this.uri = referenceUri;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientReference that = (ClientReference) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientReference{" +
                "uri='" + uri + '\'' +
                '}';
    }

    /*
     * DeepCloneable
     */

    @Override
    public ClientReference deepClone() {
        return new ClientReference(this);
    }
}
