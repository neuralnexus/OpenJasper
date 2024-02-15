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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: ClientResourceListWrapper.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = "resources")
public class ClientResourceListWrapper {
    private List<ClientResourceLookup> resourceLookups;

    public ClientResourceListWrapper(){}

    public ClientResourceListWrapper(List<ClientResourceLookup> resourceLookups){
        this.resourceLookups = resourceLookups;
    }

    public ClientResourceListWrapper(ClientResourceListWrapper other) {
        final List<ClientResourceLookup> srcResourceLookups = other.getResourceLookups();
        if(srcResourceLookups != null){
            resourceLookups = new ArrayList<ClientResourceLookup>(other.getResourceLookups().size());
            for(ClientResourceLookup lookup : srcResourceLookups){
                resourceLookups.add(new ClientResourceLookup(lookup));
            }
        }
    }

    @XmlElement(name = "resourceLookup")
    public List<ClientResourceLookup> getResourceLookups() {
        return resourceLookups;
    }

    public ClientResourceListWrapper setResourceLookups(List<ClientResourceLookup> resourceLookups) {
        this.resourceLookups = resourceLookups;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientResourceListWrapper that = (ClientResourceListWrapper) o;

        if (resourceLookups != null ? !resourceLookups.equals(that.resourceLookups) : that.resourceLookups != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resourceLookups != null ? resourceLookups.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientResourceListWrapper{" +
                "resourceLookups=" + resourceLookups +

                '}';
    }
}
