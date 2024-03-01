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
package com.jaspersoft.jasperserver.api.logging.access.domain;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;

import java.util.Date;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessEventImpl implements AccessEvent, Cloneable {
    private String userId;
    private Date eventDate;
    private boolean updating;
    private String resourceUri;
    private String resourceType;
    private boolean hidden;

    public String getUserId() {
        return userId;
    }

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

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    @Override
    public String extractUserName() {
        if(userId==null||userId.indexOf(UID_DELIMITER)<0){
            return userId;
        } else {
            return userId.substring(0,userId.indexOf(UID_DELIMITER));
        }
    }

    @Override
    public String extractTenantId() {
        if(userId==null||userId.indexOf(UID_DELIMITER)<0){
            return null;
        } else {
            return userId.substring(userId.indexOf(UID_DELIMITER)+1);
        }
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public boolean isHidden() {  return hidden;  }

    @Override
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    public static class TranslateException extends IllegalArgumentException {

        private AccessEvent accessEvent;
        private String resourceUri;
        private ResourceFactory resourceFactory;
        private Exception exception;

        public TranslateException(AccessEvent accessEvent, Resource resource, ResourceFactory resourceFactory, Exception exception) {
            super(exception);
            this.accessEvent = accessEvent;
            this.resourceUri = resource.getURIString();
            this.resourceFactory = resourceFactory;
            this.exception = exception;
        }

        public AccessEvent getAccessEvent() {
            return accessEvent;
        }

        public ResourceFactory getResourceFactory() {
            return resourceFactory;
        }

        public String getResourceUri() {
            return resourceUri;
        }

        public Exception getOriginalException() {
            return exception;
        }
    }

    public AccessEventImpl clone() { 
    	try {
    		return (AccessEventImpl)super.clone();
    	} catch (CloneNotSupportedException e) {        
            throw new RuntimeException("Cant clone AccessEventImpl",e);
    	}
    }
    
    public String toString() {
        return "AccessEventImpl["+ userId +","+eventDate+","+updating+","+resourceUri+","+resourceType+","+hidden+"]";
    }
}
