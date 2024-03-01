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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateBeforeDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoTenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.EventSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Cascade deletion of roles changed to listener because of bug #18501:
 * it seems that cascade works not properly.
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class HibernateRoleDeleteListener implements HibernateBeforeDeleteListener {

    @Transactional
    public void beforeDelete(DeleteEvent event) {
        Object o = event.getObject();
        if (o instanceof RepoTenant) {
            deleteRolesByTenant((RepoTenant) o, event.getSession());
        }
    }

    private void deleteRoleFromUsers(RepoRole role) {
		// Get all users that have this role and remove the role from them
		Set userList = role.getUsers();
		for (Iterator it = userList.iterator(); it.hasNext(); ) {
			RepoUser u = (RepoUser) it.next();
			u.removeRole(role);
		}
    }
    protected void deleteRolesByTenant(RepoTenant tenant, EventSource session) {
            for (Object roleObject: tenant.getRoles()) {
                RepoRole role = (RepoRole) roleObject;
                deleteRoleFromUsers(role);
                session.delete(role);
            }
    }

}
