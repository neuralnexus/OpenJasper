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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.acl.basic.BasicAclEntryCache;
import org.springframework.security.acl.basic.SimpleAclEntry;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.ObjectPermissionServiceImpl.URIObjectIdentity;

/**
 * this interceptor is put in the chain for the repo service
 * it should be between the security interceptor and the actual repo service, 
 * so that the return value of getSubFolders() gets processed in this order:
 * - base repo service returns list of folders
 * - this guy gets called, prepopulates acl cache
 * - security interceptor gets called
 * - obj permission service gets called to get acl's 
 * - lo and behold, iterms are found in cache
 * @author bob
 *
 */
public class PermissionsPrefetcher extends HibernateDaoImpl implements MethodInterceptor {
	private static Logger log = Logger.getLogger(PermissionsPrefetcher.class);
	
	private int minimumPrefetch = 10; 
    private RepositoryService repositoryService;
    private ObjectPermissionServiceImpl objectPermissionService;
	private ResourceFactory objectMappingFactory;
	private ResourceFactory persistentClassFactory;


	public Object invoke(MethodInvocation call) throws Throwable {
		if (call.getMethod().getName().equals("getSubFolders")) {
			List<?> subFolderList = (List<?>) call.proceed();
			String folderURI = (String) call.getArguments()[1];
			preloadSubFolderPerms(folderURI, subFolderList);
			return subFolderList;
		} else {
			return call.proceed();
		}
	}
	
	private void preloadSubFolderPerms(String folderURI, List<?> subFolderList) {
		// don't prefetch root
		if (folderURI.equals("/")) {
			return;
		}
		BasicAclEntryCache cache = objectPermissionService.getBasicAclEntryCache();
		Set<String> missingEntries = new HashSet<String>();
		for (Object subFolderObj : subFolderList) {
			Folder subFolder = (Folder) subFolderObj;
			String uri = subFolder.getURI();
			if (cache.getEntriesFromCache(new URIObjectIdentity(uri)) == null) {
				missingEntries.add(uri);
			}
		}
			
		if (missingEntries.size() < minimumPrefetch) {
			return;
		}
		// do query on all users to push into the cache
		List<?> userList = getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query query = session.createQuery("from RepoUser u left join fetch u.roles");
                return query.list();
            }
        });
		final String objPermissionClassName = getPersistentClassFactory().getImplementationClassName(ObjectPermission.class);
		final String queryString = "from " + objPermissionClassName + 
			" where URI like 'repo:" + folderURI + "%'" + 
		 	" order by URI";

		List<?> permList = getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                List<ObjectPermission> perms = session.createQuery(queryString).list();
                for (ObjectPermission perm : perms){
                    IdedObject recepient = (IdedObject)perm.getPermissionRecipient();
                    recepient.toClient(objectMappingFactory); // initialize all fields while it persistent
                }
                return perms;
            }
        });
		String currentURI = null;
		List<BasicAclEntry> permsForURI = new ArrayList<BasicAclEntry>();
		for (Object permObj : permList) {
			RepoObjectPermission perm = (RepoObjectPermission) permObj;
			// only handle perms that match one of the returned folders
			// which is not cached yet
			if (! missingEntries.contains(perm.getURI())) {
				continue;
			}
			// we are sorting by uri; if we've hit a new uri, then we need to 
			// put the previously gathered up perms for the last uri into the cache
			if (! perm.getURI().equals(currentURI)) {
				if (permsForURI.size() > 0) {
					BasicAclEntry[] aclArray = permsForURI.toArray(new BasicAclEntry[permsForURI.size()]);
					cache.putEntriesInCache(aclArray);
					permsForURI.clear();
				}
				// remove old version from missingEntries
				missingEntries.remove(currentURI);
				// set currentURI to new value
				currentURI = perm.getURI();
			}
			// create aclEntry from the repo perm object, and save it in list
			ObjectPermission clientPermission = (ObjectPermission) perm.toClient(objectMappingFactory);
			BasicAclEntry aclEntry = objectPermissionService.createBasicAclEntry(currentURI, clientPermission);
			permsForURI.add(aclEntry);
		}
		// put any perms that haven't been put yet 
		if (permsForURI.size() > 0) {
			BasicAclEntry[] aclArray = permsForURI.toArray(new BasicAclEntry[permsForURI.size()]);
			cache.putEntriesInCache(aclArray);
			missingEntries.remove(currentURI);
		}
		// put in empty perms entry for folders in returned list that don't have any perms records
		for (String uriWithNoPerms : missingEntries) {
			BasicAclEntry[] emptyAclEntries = {
            		objectPermissionService.createBasicAclEntry(uriWithNoPerms, null)};
            cache.putEntriesInCache(emptyAclEntries);
		}
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setObjectPermissionService(ObjectPermissionServiceImpl objectPermissionService) {
		this.objectPermissionService = objectPermissionService;
	}

	public ObjectPermissionServiceImpl getObjectPermissionService() {
		return objectPermissionService;
	}

	public void setMinimumPrefetch(int minimumPrefetch) {
		this.minimumPrefetch = minimumPrefetch;
	}

	public int getMinimumPrefetch() {
		return minimumPrefetch;
	}

	public void setObjectMappingFactory(ResourceFactory objectFactory) {
		this.objectMappingFactory = objectFactory;
	}

	public ResourceFactory getObjectMappingFactory() {
		return objectMappingFactory;
	}

	public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
		this.persistentClassFactory = persistentClassFactory;
	}

	public ResourceFactory getPersistentClassFactory() {
		return persistentClassFactory;
	}

}
