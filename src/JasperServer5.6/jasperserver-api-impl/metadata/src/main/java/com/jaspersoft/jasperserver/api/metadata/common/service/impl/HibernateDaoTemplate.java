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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateDaoTemplate.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class HibernateDaoTemplate extends HibernateTemplate {

	private final ThreadLocal<Integer> localFlushMode = new ThreadLocal<Integer>();
	
	public HibernateDaoTemplate() {
	}

	public HibernateDaoTemplate(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public HibernateDaoTemplate(SessionFactory sessionFactory, boolean allowCreate) {
		super(sessionFactory, allowCreate);
	}

	protected void setLocalFlushMode(Integer flushMode) {
		localFlushMode.set(flushMode);
	}

	protected Integer getLocalFlushMode() {
		return localFlushMode.get();
	}
	
	@Override
	public int getFlushMode() {
		Integer localMode = getLocalFlushMode();
		if (localMode != null) {
			return localMode;
		}
		
		return super.getFlushMode();
	}

}
