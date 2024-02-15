/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.dto.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk, vsabadosh
 * @version $Id: ClientMondrianXmlaDefinition.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = ResourceMediaType.MONDRIAN_XMLA_DEFINITION_CLIENT_TYPE)
public class ClientMondrianXmlaDefinition extends ClientResource<ClientMondrianXmlaDefinition> implements ClientReferenceableMondrianConnection{
    private String catalog;
    private ClientReferenceableMondrianConnection mondrianConnection;

    public ClientMondrianXmlaDefinition(ClientMondrianXmlaDefinition other) {
        super(other);
        this.catalog = other.catalog;
        ClientReferenceableMondrianConnection srcMondrianConnection = other.getMondrianConnection();
        if (srcMondrianConnection != null) {
            if (srcMondrianConnection instanceof ClientMondrianConnection){
                mondrianConnection = new ClientMondrianConnection((ClientMondrianConnection) srcMondrianConnection);
            } else if (srcMondrianConnection instanceof  ClientMondrianXmlaDefinition){
                mondrianConnection = new ClientMondrianXmlaDefinition((ClientMondrianXmlaDefinition) srcMondrianConnection);
            } else if (srcMondrianConnection instanceof ClientReference){
                mondrianConnection = new ClientReference((ClientReference) srcMondrianConnection);
            } else if (srcMondrianConnection instanceof ClientSecureMondrianConnection){
                mondrianConnection = new ClientSecureMondrianConnection((ClientSecureMondrianConnection) srcMondrianConnection);
            }
        }
    }

    public ClientMondrianXmlaDefinition() {
    }

    @XmlElement(name = "catalog")
    public String getCatalog() {
        return catalog;
    }

    public ClientMondrianXmlaDefinition setCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    @XmlElements({
            @XmlElement(name = "mondrianConnectionReference", type = ClientReference.class),
            @XmlElement(name = "mondrianConnection", type = ClientMondrianConnection.class),
            @XmlElement(name = "secureMondrianConnection", type = ClientSecureMondrianConnection.class)
    })
    public ClientReferenceableMondrianConnection getMondrianConnection() {
        return mondrianConnection;
    }

    public ClientMondrianXmlaDefinition setMondrianConnection(ClientReferenceableMondrianConnection mondrianConnection) {
        this.mondrianConnection = mondrianConnection;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientMondrianXmlaDefinition that = (ClientMondrianXmlaDefinition) o;

        if (catalog != null ? !catalog.equals(that.catalog) : that.catalog != null) return false;
        if (mondrianConnection != null ? !mondrianConnection.equals(that.mondrianConnection) : that.mondrianConnection != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (catalog != null ? catalog.hashCode() : 0);
        result = 31 * result + (mondrianConnection != null ? mondrianConnection.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientMondrianXmlaDefinition{" +
                "version=" + getVersion() +
                ", permissionMask=" + getPermissionMask() +
                ", uri='" + getUri() + '\'' +
                ", label='" + getLabel() + '\'' +
                '}';
    }
}
