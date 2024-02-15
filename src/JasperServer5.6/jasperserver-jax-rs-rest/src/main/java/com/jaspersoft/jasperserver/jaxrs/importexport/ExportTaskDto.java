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
package com.jaspersoft.jasperserver.jaxrs.importexport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * @author: Zakhar.Tomchenco
 */
@XmlRootElement(name = "export")
public class ExportTaskDto {
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<String> exportParams;

    @XmlElementWrapper(name = "uris")
    @XmlElement(name = "uri")
    private List<String> urisOfResources;

    @XmlTransient
    private List<String> urisOfScheduledJobs;

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<String> rolesToExport;

    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    private List<String> usersToExport;

    public List<String> getParameters() {
        return exportParams;
    }

    public void setParameters(List<String> exportParams) {
        this.exportParams = exportParams;
    }

    public List<String> getUris() {
        return urisOfResources;
    }

    public void setUris(List<String> urisOfResources) {
        this.urisOfResources = urisOfResources;
    }

    public List<String> getScheduledJobs() {
        return urisOfScheduledJobs;
    }

    public void setScheduledJobs(List<String> urisOfScheduledJobs) {
        this.urisOfScheduledJobs = urisOfScheduledJobs;
    }

    public List<String> getRoles() {
        return rolesToExport;
    }

    public void setRoles(List<String> rolesToExport) {
        this.rolesToExport = rolesToExport;
    }

    public List<String> getUsers() {
        return usersToExport;
    }

    public void setUsers(List<String> usersToExport) {
        this.usersToExport = usersToExport;
    }
}
