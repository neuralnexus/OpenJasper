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

import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import java.util.Date;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public interface AccessEvent extends LoggableEvent {
    public static final char UID_DELIMITER='|';

    public String getUserId();

    public void setUserId(String user);

    public Date getEventDate();

    public void setEventDate(Date eventDate);

    public String getResourceUri();

    public void setResourceUri(String resource);

    public boolean isUpdating();

    public void setUpdating(boolean updating);

    public String extractUserName();
    public String extractTenantId();

    public String getResourceType();
    public void setResourceType(String resourceType);

    public boolean isHidden();
    public void setHidden(boolean hidden);
}
