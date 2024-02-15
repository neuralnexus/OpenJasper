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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author swood
 * @version $Id: HibernateDaoImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class HibernateDaoImpl extends HibernateDaoSupport {
	
	private static final Log log = LogFactory.getLog(HibernateDaoImpl.class);
	
	private final ThreadLocal operationDate;
	
	public HibernateDaoImpl()
	{
		operationDate = new ThreadLocal();
	}

	protected static interface DaoCallback {
		Object execute();
	}

	protected final Object executeCallback(final DaoCallback callback) {
		try {
			Object ret = callback.execute();
			return ret;
		} catch (DataAccessException e) {
            // Logging of error was commented because it causes double error logging which is confusing.
            // Uncomment if it is still necessary for reason I do not imagine at the moment.
			//log.error("Hibernate DataAccessException", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected final Object executeWriteCallback(final DaoCallback callback) {
		return executeWriteCallback(callback, true);
	}

	protected final Object executeWriteCallback(final DaoCallback callback, boolean flush) {
		startOperation();
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		HibernateDaoFlushModeHandle flushModeHandle = createFlushModeHandle(hibernateTemplate);
		try {
			flushModeHandle.setFlushMode(HibernateAccessor.FLUSH_COMMIT);
			Object ret = callback.execute();
			if (flush) {
				hibernateTemplate.flush();
			}
			return ret;
		} catch (DataAccessException e) {
            // Logging of error was commented because it causes double error logging which is confusing.
            // Uncomment if it is still necessary for reason I do not imagine at the moment.
            //log.error("Hibernate DataAccessException", e);
			throw new JSExceptionWrapper(e);
		}
		finally {
			flushModeHandle.revert();
			endOperation();
		}
	}

	protected void startOperation() {
		operationDate.set(new Date());
	}
	
	protected Date getOperationTimestamp() {
		return (Date) operationDate.get();
	}
	
	protected void endOperation() {
		operationDate.set(null);
	}
	
	@Override
	protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
		return new HibernateDaoTemplate(sessionFactory);
	}
	
	protected HibernateDaoFlushModeHandle createFlushModeHandle(HibernateTemplate template) {
		HibernateDaoFlushModeHandle handle;
		if (template instanceof HibernateDaoTemplate) {
			HibernateDaoTemplate daoTemplate = (HibernateDaoTemplate) template;
			handle = new HibernateDaoTemplateFlushModeHandle(daoTemplate);
		} else {
			// this should not happen, all DAOs should have HibernateDaoTemplates.
			// it is not a thread safe operation, keeping it just for historical purposes.
			handle = new HibernateTemplateFlushModeHandle(template);
			
			if (log.isDebugEnabled()) {
				log.debug("unsafe template flush mode for " + this);
			}
		}
		return handle;
	}
}

interface HibernateDaoFlushModeHandle {
	
	void setFlushMode(int flushMode);
	
	void revert();
}

class HibernateDaoTemplateFlushModeHandle implements HibernateDaoFlushModeHandle {
	
	private final HibernateDaoTemplate template;
	private final Integer origFlushMode;
	
	public HibernateDaoTemplateFlushModeHandle(HibernateDaoTemplate template) {
		this.template = template;
		this.origFlushMode = template.getLocalFlushMode();
	}

	@Override
	public void setFlushMode(int flushMode) {
		template.setLocalFlushMode(flushMode);
	}

	@Override
	public void revert() {
		template.setLocalFlushMode(origFlushMode);
	}
	
}

class HibernateTemplateFlushModeHandle implements HibernateDaoFlushModeHandle {
	
	private final HibernateTemplate template;
	private final int origFlushMode;
	
	public HibernateTemplateFlushModeHandle(HibernateTemplate template) {
		this.template = template;
		this.origFlushMode = template.getFlushMode();
	}

	@Override
	public void setFlushMode(int flushMode) {
		template.setFlushMode(flushMode);
	}

	@Override
	public void revert() {
		template.setFlushMode(origFlushMode);
	}
	
}
