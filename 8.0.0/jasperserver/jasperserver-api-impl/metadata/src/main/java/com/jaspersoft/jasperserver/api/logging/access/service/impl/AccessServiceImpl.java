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

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEventImpl;
import com.jaspersoft.jasperserver.api.logging.access.domain.hibernate.RepoAccessEvent;
import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
@Transactional(value = "auditTransactionManager", propagation = Propagation.SUPPORTS, readOnly = true)
public class AccessServiceImpl extends HibernateDaoImpl implements AccessService, PersistentObjectResolver {

    private HibernateRepositoryService hibernateRepositoryService;
    private PersistentObjectResolver persistentUserResolver;
    private int maxAccessEventAge;
    private ResourceFactory persistentClassFactory;
    private ResourceFactory clientClassFactory;


    public static final String COMMAND_OUT_LOGGER = "com.jaspersoft.jasperserver.export.command";

    public void setMaxAccessEventAge(int maxAccessEventAge) {
        this.maxAccessEventAge = maxAccessEventAge;
    }

    public void setHibernateRepositoryService(HibernateRepositoryService hibernateRepositoryService) {
        this.hibernateRepositoryService = hibernateRepositoryService;
    }

    public void setPersistentUserResolver(PersistentObjectResolver persistentUserResolver) {
        this.persistentUserResolver = persistentUserResolver;
    }

    public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
        this.persistentClassFactory = persistentClassFactory;
    }

    public void setClientClassFactory(ResourceFactory clientClassFactory) {
        this.clientClassFactory = clientClassFactory;
    }

    public Object getPersistentObject(Object clientObject) {
        if (clientObject == null) {
            return null;
        } else if (clientObject instanceof Resource) {
            return hibernateRepositoryService.getRepoResource((Resource)clientObject);
        } else if (clientObject instanceof User) {
            return persistentUserResolver.getPersistentObject(clientObject);
        }

        return null;
    }

    private Resource getResourceFromAccessEvent_helper(final AccessEvent accessEvent){
        return hibernateRepositoryService.getResource(null,accessEvent.getResourceUri());
    }

    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void saveEvent(LoggableEvent loggableEvent) {
        AccessEvent accessEvent = (AccessEvent) loggableEvent;
        Resource accessEventResource = getResourceFromAccessEvent_helper(accessEvent);
        if (
            accessEventResource != null
        ) {
            RepoAccessEvent repoAccessEvent = new RepoAccessEvent();
            repoAccessEvent.copyFromClient(loggableEvent, this);
            getHibernateTemplate().save(repoAccessEvent);
        }
    }

    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void saveEvents(List<LoggableEvent> loggableEvents) {
        if (loggableEvents == null || loggableEvents.isEmpty()) {
            return;
        }
        //This code will collapse all events for a specific user and uri into one event
        //to reduce the number of database call`s that are caused by multiple events for
        //same resource in the same http request
        //see Bug 35570 - [case 42185] Hibernate generating thousands of queries from dashboard 
        Map<List<String>,AccessEventImpl> map = new HashMap<List<String>,AccessEventImpl>();
        for (LoggableEvent loggableEvent: loggableEvents) {
        	if (loggableEvent instanceof AccessEventImpl) {
        		AccessEventImpl e = (AccessEventImpl)loggableEvent;
        		List<String> key = new ArrayList<String>(2);
        		key.add(e.getUserId()); // accessEvent userId is a concatenation fo user id, |, and tenantId
        		key.add(e.getResourceUri());
        		AccessEventImpl v = map.get(key);
        		if (v==null) {
        			map.put(key, e.clone());
        		} else {
        			if (e.isUpdating())
        				v.setUpdating(true);
        		}
        	} else {
        		saveEvent(loggableEvent);
        	}
        }

        for (AccessEventImpl e : map.values()) {
        	saveEvent(e);
        }
    }

    public List<AccessEvent> getAllEvents(int firstResult, int maxResults) {
        DetachedCriteria criteria =
                DetachedCriteria.forClass(persistentClassFactory.getImplementationClass(AccessEvent.class));
        criteria.addOrder(Order.asc("eventDate"));

        List results = getHibernateTemplate().findByCriteria(criteria, firstResult, maxResults);

        if (results != null && !results.isEmpty()) {
            List<AccessEvent> clientEventsList = new ArrayList<AccessEvent>(results.size());
            Log logger = LogFactory.getLog(COMMAND_OUT_LOGGER);
            for (Object event: results) {
                try {
                AccessEvent accessEvent = (AccessEvent) ((IdedObject) event).toClient(clientClassFactory);
                clientEventsList.add(accessEvent);
                } catch (AccessEventImpl.TranslateException aex) {
                    logger.debug("Unable to translate access event:  resourceUri = " + aex.getResourceUri() + ", factory = " + aex.getResourceFactory().getClass(), aex.getOriginalException());
                } catch (Exception ex) {
                    logger.debug("Unable to translate access event", ex);
                }
            }
            return clientEventsList;
        } else {
            return Collections.emptyList();
        }
    }

    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void updateAccessEventsByResourceURI(String oldURI, String newURI) {
        final String className = persistentClassFactory.getImplementationClassName(AccessEvent.class);
        final String queryString = "update " + className + " set resource_uri=:newURI where resource_uri=:oldURI";
        getHibernateTemplate().execute(new HibernateCallback<Void>() {
            public Void doInHibernate(Session session) throws HibernateException {
              Query query = session.createQuery(queryString).
                      setParameter("newURI",newURI).
                      setParameter("oldURI",oldURI);
                query.executeUpdate();
                return null;
            }
        });
    }

    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void deleteAccessEvent(String uri, boolean isFolder){
        DetachedCriteria criteria =
            DetachedCriteria.forClass(persistentClassFactory.getImplementationClass(AccessEvent.class));
        if(isFolder)
            criteria.add(Restrictions.like("resourceUri", uri, MatchMode.START));
        else
            criteria.add(Restrictions.eq("resourceUri", uri));

        List results = getHibernateTemplate().findByCriteria(criteria);
        if(results != null && !results.isEmpty()){
            getHibernateTemplate().deleteAll(results);
        }
    }
    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void deleteAccessEventsByUser(String userId){
        DetachedCriteria criteria =
                DetachedCriteria.forClass(persistentClassFactory.getImplementationClass(AccessEvent.class));
        criteria.add(Restrictions.eq("userId", userId));

        List results = getHibernateTemplate().findByCriteria(criteria);
        if(results != null && !results.isEmpty()){
            getHibernateTemplate().deleteAll(results);
        }
    }

    public int getAccessEventsCount() {
        DetachedCriteria criteria =
                DetachedCriteria.forClass(persistentClassFactory.getImplementationClass(AccessEvent.class));
        criteria.setProjection(Projections.rowCount());
        List result = getHibernateTemplate().findByCriteria(criteria);
        Long rowCount= (Long)result.get(0);
        return rowCount.intValue();
    }

    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRES_NEW, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void purgeAccessEvents() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -maxAccessEventAge);
        Date last = cal.getTime();
        getHibernateTemplate().bulkUpdate(
                "delete RepoAccessEvent e " +
                "  where e.eventDate < ? ", last);
        // added this on 2019-9-23 to manually clean up orphans
        // disabled cascade delete to improve performance on mass delete operations 
        // this includes import (JS-34322) and delete organization (JS-35283)
/*
        getHibernateTemplate().bulkUpdate(
                "delete from RepoAccessEvent " +
                "  where id in (" +
                "    select e.id from RepoAccessEvent e " +
                "    left join RepoResource r on (r.id = e.resourceId) " +
                "    left join RepoUser u on (u.id = e.user) " +
                "    where r is null " +
                "    or u is null" +
                "  )");
 */
    }
    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void importAccessEvent(AccessEvent accessEvent) {
        RepoAccessEvent repoAccessEvent = new RepoAccessEvent();
        repoAccessEvent.copyFromClient(accessEvent, this);
        getHibernateTemplate().save(repoAccessEvent);
    }
    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
    @Qualifier("auditTransactionManager")
    public void importAccessEvents(List<AccessEvent> accessEvents) {
        for (AccessEvent accessEvent : accessEvents) {
            importAccessEvent(accessEvent);
        }
    }
    @Transactional(value="auditTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Qualifier("auditTransactionManager")
    public List getResourceURIs(DetachedCriteria criteria, int max){
        List results = getHibernateTemplate().findByCriteria(criteria, -1, max);

        if(results == null || results.isEmpty()){
            return Collections.emptyList();
        }
        return results;
    }
}
