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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryCacheIndicator;
import net.sf.jasperreports.engine.util.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryCache;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryCacheableItem;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.CachedItem;
import com.jaspersoft.jasperserver.api.metadata.common.util.LocalLockManager;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockHandle;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateRepositoryCache.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class HibernateRepositoryCache extends HibernateDaoImpl implements RepositoryCache {
	private static final Log log = LogFactory.getLog(HibernateRepositoryCache.class);

	private RepositoryService repository;
	private LockManager lockManager = new LocalLockManager();
    private boolean setFindByCriteriaToReadOnly = false;
    private AbstractPlatformTransactionManager transactionManager;

	public HibernateRepositoryCache() {
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

    public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public boolean getFindByCriteriaToReadOnly() {
        return setFindByCriteriaToReadOnly;
    }

    public void setFindByCriteriaToReadOnly(boolean setFindByCriteriaToReadOnly) {
        this.setFindByCriteriaToReadOnly = setFindByCriteriaToReadOnly;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public InputStream cache(ExecutionContext context, FileResource resource, RepositoryCacheableItem cacheableItem) {
        if (isCachingOn()) {
            CachedItem cachedItem = getCachedItem(context, resource, cacheableItem);
            while(cachedItem.isItemReference()) {
                cachedItem = cachedItem.getReference();
            }
            return new ByteArrayInputStream(cachedItem.getDataBytes());
        } else {
            return new ByteArrayInputStream(cacheableItem.getData(context, resource));
        }
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public InputStream cache(ExecutionContext context, String uri, RepositoryCacheableItem cacheableItem) {
		FileResource resource = (FileResource) repository.getResource(context, uri);
		return cache(context, resource, cacheableItem);
	}

	protected LockHandle lock(FileResource resource, RepositoryCacheableItem cacheableItem) {
		return lockManager.lock(cacheableItem.getCacheName(), resource.getURIString());
	}

	protected void unlock(LockHandle lock) {
		lockManager.unlock(lock);
	}

	protected Pair getLockKey(FileResource resource, RepositoryCacheableItem cacheableItem) {
		return new Pair(resource.getURIString(), cacheableItem.getCacheName());
	}

	protected CachedItem getCachedItem(ExecutionContext context, FileResource resource, RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Looking in repository cache \"" + cacheableItem.getCacheName() + "\" for resource \"" + resource.getURIString() +
					"\", version " + resource.getVersion() + "\", version date " + resource.getCreationDate());
		}

		LockHandle lock = lock(resource, cacheableItem);

        //Because of bug #25373 we need to manually manipulate with transaction:
        //open it AFTER thread acquires lock
        //and commit BEFORE thread releases the lock.
        TransactionStatus transaction = null;

		try {
            transaction = transactionManager.getTransaction(
                    new DefaultTransactionDefinition(
                            TransactionDefinition.PROPAGATION_REQUIRES_NEW));

			CachedItem cachedItem = getCachedItem(resource.getURIString(), cacheableItem, true);
			if (cachedItem == null
					|| cachedItem.getVersion() < resource.getVersion()
					|| cachedItem.getVersionDate() == null
					|| cachedItem.getVersionDate().before(resource.getCreationDate())) {
				if (resource.isReference()) {
					cachedItem = saveRefence(context, resource, cachedItem, cacheableItem);
				} else {
					cachedItem = saveData(context, resource, cachedItem, cacheableItem);
				}
			} else if (resource.isReference()) {
				//load the reference to force updates
				FileResource ref = (FileResource) repository.getResource(context, resource.getReferenceURI());
				CachedItem refItem = getCachedItem(context, ref, cacheableItem);
				if (!refItem.equals(cachedItem.getReference())) {
					updateReference(cachedItem, refItem);
				}
			}

            transactionManager.commit(transaction);
			return cachedItem;
		} finally {
            if (transaction != null && !transaction.isCompleted()) {
                try {
                    transactionManager.rollback(transaction);
                } catch (Exception e) {
                    // suppress exception throwing
                    log.error(e.getMessage(), e);
                }
            }

			unlock(lock);
		}
	}
    protected CachedItem getCachedItem(String uri, RepositoryCacheableItem cacheableItem) {
        return getCachedItem(uri, cacheableItem, false, null);
    }
    protected CachedItem getCachedItem(String uri, RepositoryCacheableItem cacheableItem, Session session) {
           return getCachedItem(uri, cacheableItem, false, session);
    }
	protected CachedItem getCachedItem(String uri, RepositoryCacheableItem cacheableItem, boolean clearPendingSavesCreatedByFindByCriteria) {
        return getCachedItem(uri, cacheableItem, clearPendingSavesCreatedByFindByCriteria, null);
	}

	protected CachedItem getCachedItem(String uri, RepositoryCacheableItem cacheableItem, boolean clearPendingSavesCreatedByFindByCriteria, Session session) {
        if (log.isDebugEnabled()) {
			log.debug("HibernateRepositoryCache:  Looking in repository cache \"" + cacheableItem.getCacheName() + "\" for resource \"" + uri);
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(CachedItem.class);
		criteria.add(Restrictions.naturalId().set("cacheName", cacheableItem.getCacheName()).set("uri", uri));
		criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List list = null;

        if (setFindByCriteriaToReadOnly) {
            // flush all pending saves, updates and deletes to the database (However, it may slows down the performance, since we have to synchronize the method
            list = findByCritera(criteria, clearPendingSavesCreatedByFindByCriteria);
        } else {
            // no synchronization, but spring hibernate template creates an extra update statement when calling findByCriteria
        	if (session==null)
        		list = getHibernateTemplate().findByCriteria(criteria);
        	else
        		list = criteria.getExecutableCriteria(session).list();
        }

		CachedItem item = null;
		if (list.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("did not find \"" + uri + "\" in cache " +
                        cacheableItem.getCacheName());
            }
			item = null;
		} else {
			item = (CachedItem) list.get(0);
            if (log.isDebugEnabled()) {
                log.debug("found \"" + uri + "\" in cache " +
                        cacheableItem.getCacheName() +
                        ". isReference: " + item.isItemReference());
            }
		}
		return item;
	}

   /**
    * whenever we made a hibernate call, findByCriteria(...) for JIRepositoryCache table, it would actually execute 2 SQL statements.
    * One is a SELECT statement and the other one is an UPDATE statement
    * SOLUTION:  1) FLUSH ALL THE PENDING SAVES FIRST
    *            2) SET FLUSH MODE TO NEVER AND CALL FINDBYCRITERIA()
    *            3) CLEAR ALL THE PENDING SAVES CREATED BY FINDBYCRITERIA()
    *            4) RESET THE FLUSH MODE
    * @param criteria searching criteria
    * @param clearPendingSavesCreatedByFindByCriteria remove the "extra" update statement that caused by spring findByCriteria() method
    * @return search result
    */
    private synchronized List findByCritera(DetachedCriteria criteria, boolean clearPendingSavesCreatedByFindByCriteria) {
        if (clearPendingSavesCreatedByFindByCriteria) {
        	throw new RuntimeException ("Unsafe hibernate code blocked");
        	/*
        // flush all pending saves, updates and deletes to the database
            getHibernateTemplate().flush();
            // take a snapshot of original flush mode
            int flushMode = getHibernateTemplate().getFlushMode();
            // set flush mode to read only:  to avoid unnecessary write or update that is called by findByCriteria(...)
            getHibernateTemplate().setFlushMode(HibernateAccessor.FLUSH_NEVER);
            List list = getHibernateTemplate().findByCriteria(criteria);
            try {
                // remove all objects from the session cache, and cancel all pending saves, updates, and deletes
               getHibernateTemplate().clear();
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("HibernateRepositoryCache:  Fail to cancel all pending saves, updates, and deletes");
                }
            }
            // set it back to original flush mode
            getHibernateTemplate().setFlushMode(flushMode);
            return list;*/
        } else {
             return getHibernateTemplate().findByCriteria(criteria);
        }
    }

	protected CachedItem saveRefence(ExecutionContext context, FileResource resource, CachedItem item, RepositoryCacheableItem cacheableItem) {
		FileResource ref = (FileResource) repository.getResource(context, resource.getReferenceURI());
		CachedItem refItem = getCachedItem(context, ref, cacheableItem);

		CachedItem saveItem;
		if (item == null) {
			saveItem = new CachedItem();
		} else {
			saveItem = item;
		}
		saveItem.setCacheName(cacheableItem.getCacheName());
		saveItem.setData(null);
		saveItem.setReference(refItem);
		saveItem.setUri(resource.getURIString());
		saveItem.setVersion(resource.getVersion());
		saveItem.setVersionDate(resource.getCreationDate());

		if (item == null) {
			getHibernateTemplate().save(saveItem);
		} else {
			getHibernateTemplate().update(saveItem);
		}

		return saveItem;
	}

	protected CachedItem saveData(ExecutionContext context, FileResource resource, CachedItem item, RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Saving repository cache \"" + cacheableItem.getCacheName() + "\" for resource \"" + resource.getURIString() +
					"\", version " + resource.getVersion() + "\", version date " + resource.getCreationDate());
		}

		byte[] data = cacheableItem.getData(context, resource);

		CachedItem saveItem;
		if (item == null) {
			saveItem = new CachedItem();
		} else {
			saveItem = item;
		}
		saveItem.setCacheName(cacheableItem.getCacheName());
		saveItem.setDataBytes(data);
		saveItem.setReference(null);
		saveItem.setUri(resource.getURIString());
		saveItem.setVersion(resource.getVersion());
		saveItem.setVersionDate(resource.getCreationDate());

		if (item == null) {
			getHibernateTemplate().save(saveItem);
		} else {
			getHibernateTemplate().update(saveItem);
		}

		return saveItem;
	}

	protected void updateReference(CachedItem item, CachedItem refItem) {
		item.setReference(refItem);
		getHibernateTemplate().update(item);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void clearCache(final String uri, final RepositoryCacheableItem cacheableItem) {
        if (isCachingOn()) {
            executeWriteCallback(new DaoCallback() {
                public Object execute() {
                    removeCached(uri, cacheableItem);
                    return null;
                }
            }, false);
        }
    }

	protected void removeCached(String uri, RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Clearing cache " + cacheableItem.getCacheName() + " for resource " + uri);
		}

		getHibernateTemplate().executeWithNewSession(new RemoveCachedCallback (uri, cacheableItem));
	}
	
	class RemoveCachedCallback implements HibernateCallback<Object> {
		String uri;
		RepositoryCacheableItem cacheableItem;
		public RemoveCachedCallback(String uri, RepositoryCacheableItem cacheableItem) {
			this.uri=uri;
			this.cacheableItem = cacheableItem;
		}
		public Object doInHibernate(Session session) { 
			CachedItem cachedItem = getCachedItem(uri, cacheableItem, session);
			if (cachedItem != null) {
				session.delete(cachedItem);
			}
			return null;
		 }
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void clearCache(final RepositoryCacheableItem cacheableItem) {
        if (isCachingOn()) {
            executeWriteCallback(new DaoCallback() {
                public Object execute() {
                    removeCached(cacheableItem);
                    return null;
                }
            }, false);
        }
    }

	protected void removeCached(RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Clearing entire cache " + cacheableItem.getCacheName());
		}

		getHibernateTemplate().bulkUpdate(
				"delete CachedItem where cacheName = ?", 
				cacheableItem.getCacheName());
	}

	public LockManager getLockManager() {
		return lockManager;
	}

	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}

    private boolean isCachingOn() {
        return RepositoryCacheIndicator.isOn();
    }
}
