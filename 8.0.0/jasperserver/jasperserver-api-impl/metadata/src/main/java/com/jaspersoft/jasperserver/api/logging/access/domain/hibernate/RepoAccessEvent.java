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
package com.jaspersoft.jasperserver.api.logging.access.domain.hibernate;

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEventImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;

import java.util.Date;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class RepoAccessEvent implements IdedObject {
    private long id;
    private String userId; // user|tenantId
    private Date eventDate;
    private String resourceUri;
    private String resourceType;
    private boolean hidden;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private boolean updating;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {return userId; }

    public void setUserId(String user) {
        this.userId = user;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(RepoResource resource) {
        this.resourceUri = resource.getResourceURI();
    }

    public void setResourceUri(String resourceUri) {this.resourceUri = resourceUri; }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public Object toClient(ResourceFactory clientMappingFactory) {
        AccessEvent accessEvent = (AccessEvent)clientMappingFactory.newObject(AccessEvent.class);
        accessEvent.setUserId(getUserId());
        accessEvent.setEventDate(getEventDate());
        accessEvent.setResourceUri(getResourceUri());
        accessEvent.setUpdating(isUpdating());
        accessEvent.setResourceType(getResourceType());
        accessEvent.setHidden(isHidden());
        return accessEvent;
    }



    public void copyFromClient(Object objIdent, PersistentObjectResolver resolver) {
        AccessEvent accessEvent = (AccessEvent)objIdent;
        setUserId(accessEvent.getUserId());
        setEventDate(accessEvent.getEventDate());
        setResourceUri(accessEvent.getResourceUri());
        setUpdating(accessEvent.isUpdating());
        setResourceType(accessEvent.getResourceType());
        setHidden(accessEvent.isHidden());
    }
}
