/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.dto.permissions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@XmlRootElement(name = "permissions")
public class RepositoryPermissionListWrapper {
    private List<RepositoryPermission> permissions;

    public RepositoryPermissionListWrapper(){}

    public RepositoryPermissionListWrapper(List<RepositoryPermission> permissions){
        this.permissions = permissions;
    }

    public RepositoryPermissionListWrapper(RepositoryPermissionListWrapper other) {
        final List<RepositoryPermission> repositoryPermissions = other.getPermissions();
        if(repositoryPermissions != null){
            permissions = new ArrayList<RepositoryPermission>(other.getPermissions().size());
            for(RepositoryPermission attribute : repositoryPermissions){
                permissions.add(new RepositoryPermission(attribute));
            }
        }
    }

    @XmlElement(name = "permission")
    public List<RepositoryPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RepositoryPermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepositoryPermissionListWrapper that = (RepositoryPermissionListWrapper) o;

        if (permissions != null ? !permissions.equals(that.permissions) : that.permissions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return permissions != null ? permissions.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RepositoryPermissionListWrapper{" +
                "permissions=" + permissions +
                '}';
    }
}
