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
package com.jaspersoft.jasperserver.api.logging.audit.domain;

import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;

import java.util.Date;
import java.util.Set;

/**
 * @author Sergey Prilukin
 * @version $Id: AuditEvent.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface AuditEvent extends LoggableEvent {

    public String getUsername();

    public void setUsername(String username);

    public String getTenantId();

    public void setTenantId(String tenantId);

    public Date getEventDate();

    public void setEventDate(Date eventDate);

    public String getEventType();

    public void setEventType(String eventType);

    public String getRequestType();

    public void setRequestType(String name);

    public String getResourceUri();

    public void setResourceUri(String uri);

    public String getResourceType();

    public void setResourceType(String resourceType);

    public Set<AuditEventProperty> getAuditEventProperties();

    public void setAuditEventProperties(Set<AuditEventProperty> auditEventProperties);
}
