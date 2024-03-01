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
package com.jaspersoft.jasperserver.api.logging.access.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateBeforeDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import org.hibernate.event.spi.DeleteEvent;


/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class HibernateAccessEventDeleteListener implements HibernateBeforeDeleteListener {
    private AccessService accessService;

    public AccessService getAccessService() {
        return accessService;
    }

    public void setAccessService(AccessService accessService) {
        this.accessService = accessService;
    }

    public void beforeDelete(DeleteEvent event) {
        Object o = event.getObject();
        if (o instanceof RepoUser) {
            deleteAccessEventsByUser((RepoUser) o);
        }
    }

    protected void deleteAccessEventsByUser(RepoUser user) {
        String tenantId = user.getTenantId();
        String userId = user.getUsername() + ( tenantId != null && !tenantId.equals(TenantService.ORGANIZATIONS) ? "|" + tenantId : "");
        accessService.deleteAccessEventsByUser(userId);
    }

}
