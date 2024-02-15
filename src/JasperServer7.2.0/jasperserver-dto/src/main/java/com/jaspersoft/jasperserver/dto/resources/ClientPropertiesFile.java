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

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author askorodumov
 * @version $Id$
 */
@XmlRootElement(name = ResourceMediaType.PROPERTIES_FILE_CLIENT_TYPE)
public class ClientPropertiesFile extends ClientResource<ClientPropertiesFile> implements DeepCloneable<ClientPropertiesFile> {
    private LinkedList<ClientProperty> properties;

    public ClientPropertiesFile() {
    }

    public ClientPropertiesFile(ClientPropertiesFile other) {
        super(other);
        this.properties = copyOf(other.getProperties());
    }

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public LinkedList<ClientProperty> getProperties() {
        return properties;
    }

    public ClientPropertiesFile setProperties(LinkedList<ClientProperty> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientPropertiesFile)) return false;
        if (!super.equals(o)) return false;

        ClientPropertiesFile that = (ClientPropertiesFile) o;

        return !(properties != null ? !properties.equals(that.properties) : that.properties != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientPropertiesFile{" + super.toString() + "}";
    }

    /*
     * DeepCloneable
     */

    @Override
    public ClientPropertiesFile deepClone() {
        return new ClientPropertiesFile(this);
    }
}
