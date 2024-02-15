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
package com.jaspersoft.jasperserver.export.modules.logging.access.beans;

import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;
import com.jaspersoft.jasperserver.export.modules.logging.access.AccessEventsImportHandler;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;

import java.util.Date;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessEventBean {
    private String userName;
    private String tenantId;
    private Date eventDate;
    private boolean updating;
    private String resourceUri;

    public void copyFrom(AccessEvent accessEvent) {
        setUserName(accessEvent.getUser().getUsername());
        String tenantId = accessEvent.getUser().getTenantId();
        setTenantId(tenantId != null ? tenantId : "");
        setEventDate(accessEvent.getEventDate());
        setUpdating(accessEvent.isUpdating());
        setResourceUri(accessEvent.getResource().getURI());
    }

    public void copyTo(AccessEvent accessEvent, AccessEventsImportHandler accessImportHandler) {
        accessEvent.setUser(accessImportHandler.resolveUser(new TenantQualifiedName(getTenantId(), getUserName())));
        accessEvent.setEventDate(getEventDate());
        accessEvent.setUpdating(isUpdating());
        accessEvent.setResource(accessImportHandler.resolveResource(getResourceUri()));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
}
