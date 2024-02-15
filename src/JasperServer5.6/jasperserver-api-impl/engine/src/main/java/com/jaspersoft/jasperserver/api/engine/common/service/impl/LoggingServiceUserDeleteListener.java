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

package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.EventSource;

import com.jaspersoft.jasperserver.api.common.domain.impl.RepoLogEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateBeforeDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LoggingServiceUserDeleteListener.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class LoggingServiceUserDeleteListener implements HibernateBeforeDeleteListener {

	public void beforeDelete(DeleteEvent event) {
		Object o = event.getObject();
		if (o instanceof RepoUser) {
			deleteLogEvents((RepoUser) o, event.getSession());
		}
	}

	protected void deleteLogEvents(RepoUser user, EventSource session) {
		Criteria criteria = session.createCriteria(RepoLogEvent.class);
		criteria.add(Restrictions.eq("user", user));
		List items = criteria.list();
		if (items != null && !items.isEmpty()) {
			for (Iterator it = items.iterator(); it.hasNext();) {
				RepoLogEvent item = (RepoLogEvent) it.next();
				session.delete(item);
			}
		}
	}

}
