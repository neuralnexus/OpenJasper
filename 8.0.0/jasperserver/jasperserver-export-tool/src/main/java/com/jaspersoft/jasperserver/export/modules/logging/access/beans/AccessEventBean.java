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
package com.jaspersoft.jasperserver.export.modules.logging.access.beans;

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.modules.logging.access.AccessEventsImportHandler;

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
        setUserName(accessEvent.extractUserName());
        setTenantId(accessEvent.extractTenantId());
        if(tenantId==null){
            tenantId="";
        }
        setEventDate(accessEvent.getEventDate());
        setUpdating(accessEvent.isUpdating());
        setResourceUri(Resource.URI_PROTOCOL+":"+accessEvent.getResourceUri());
    }

    public void copyTo(AccessEvent accessEvent,
                       AccessEventsImportHandler accessImportHandler,
                       ImporterModuleContext context) {
        String tenantId = getTenantId();
        String resourceUri = getResourceUri();
        Resource resource = accessImportHandler.resolveResource(resourceUri);
        if(resource==null)  {
            return;
        }
        if (!context.getNewGeneratedTenantIds().isEmpty()) {
            tenantId = TenantStrHolderPattern.TENANT_ID
                    .replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), tenantId);
            resourceUri = TenantStrHolderPattern.TENANT_FOLDER_URI
                    .replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), resourceUri);
        }

        User user = accessImportHandler.resolveUser(new TenantQualifiedName(tenantId, getUserName()));

        String userId = user.getUsername();
        if(tenantId!=null&&!tenantId.isEmpty()){
            userId=userId+AccessEvent.UID_DELIMITER+tenantId;
        }
        accessEvent.setUserId(userId);
        accessEvent.setEventDate(getEventDate());
        accessEvent.setUpdating(isUpdating());
        accessEvent.setResourceType(resource.getType());
        String resourceURI = resource.getURIString();
        accessEvent.setResourceUri(resourceURI);
        int index = resourceURI.lastIndexOf(Folder.SEPARATOR);
        if (index > 0) {
            accessEvent.setHidden(accessImportHandler.isLocalFolder(resourceURI.substring(0, index)));
        }
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
