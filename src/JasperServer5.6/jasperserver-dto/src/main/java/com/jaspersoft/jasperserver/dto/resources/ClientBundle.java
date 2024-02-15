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
 * @author Zakhar.Tomchenco
 * @version $Id: ClientBundle.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = "bundle")
public class ClientBundle {
    private String locale;
    private ClientReferenceableFile file;

    public ClientBundle() {
    }

    public ClientBundle(String locale, ClientReferenceableFile file) {
        this.locale = locale;
        this.file = file;
    }

    public ClientBundle(ClientBundle other) {
        this.locale = other.getLocale();

        ClientReferenceableFile srcFile = other.getFile();
        if (srcFile != null) {
            if (srcFile instanceof ClientReference){
                file = new ClientReference((ClientReference) srcFile);
            } else if (srcFile instanceof ClientFile){
                file = new ClientFile((ClientFile) srcFile);
            }
        }
    }


    public String getLocale() {
        return locale;
    }

    public ClientBundle setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    @XmlElements({
            @XmlElement(name = "fileReference", type = ClientReference.class),
            @XmlElement(name = "file", type = ClientFile.class)
    })
    public ClientReferenceableFile getFile() {
        return file;
    }

    public ClientBundle setFile(ClientReferenceableFile file) {
        this.file = file;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientBundle that = (ClientBundle) o;

        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locale != null ? locale.hashCode() : 0;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientBundle{" +
                "locale='" + locale + '\'' +
                ", file=" + file.getUri() +
                '}';
    }
}
