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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import net.sf.jasperreports.repo.PersistenceService;
import net.sf.jasperreports.repo.RepositoryService;
import net.sf.jasperreports.repo.Resource;
import net.sf.jasperreports.repo.StreamRepositoryService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepoRepositoryService.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RepoRepositoryService implements RepositoryService, StreamRepositoryService {

	private static final Log log = LogFactory.getLog(RepoRepositoryService.class);
	
	private Map<Class<?>, PersistenceService> persistenceServices;
			
	@Override
	public Resource getResource(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveResource(String uri, Resource resource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getInputStream(String uri) {
		RepositoryContext repositoryContext = RepositoryUtil.getThreadRepositoryContext();
		if (repositoryContext == null || !isReportContext(repositoryContext)) {
			if (log.isDebugEnabled()) {
				log.debug("No repository context for resource " + uri);
			}
			return null;
		}
		
		// filtering cases when the uri is (almost) obviously not a repository path.
		// we could use the repository resource names patters as validation but I'm not sure 
		// that the validation is enforced everywhere.
		if (uri.startsWith("repo:")// for now we are not handling repo: URLs here, at some point we might want to get rid of RepositoryConnection. 
				|| uri.startsWith("file:") || uri.startsWith("http:") || uri.startsWith("https:")) {
			return null;
		}

		//FIXME should we look at repositoryContext.getReportUnit()?  it seems to be no longer used.
		String path = RepositoryUtils.resolveRelativePath(repositoryContext.getContextURI(), uri);
		ExecutionContext executionContext = repositoryContext.getExecutionContext();
		com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService repository = 
				repositoryContext.getRepository();
		
		if (log.isDebugEnabled()) {
			log.debug("loading repository resource " + path 
					+ ", context path " + repositoryContext.getContextURI()
					+ ", uri " + uri);
		}
		
		FileResource resource = (FileResource) repository.getResource(executionContext, 
				path, FileResource.class);
		while (resource != null && resource.isReference()) {
			if (log.isDebugEnabled()) {
				log.debug("loading repository resource " + resource.getReferenceURI());
			}
			
			resource = (FileResource) repository.getResource( executionContext, 
					resource.getReferenceURI(), FileResource.class);
		}
			
		if (resource == null) {
    		if (log.isDebugEnabled()) {
				log.debug("Resource \"" + path + "\" not found in the repository");
			}
			return null;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("loaded resource of type " + resource.getFileType());
		}
			
       	InputStream data;
		if (resource.getFileType().equals(FileResource.TYPE_JRXML)) {
			// this would conceptually belong to the persistence service, but we don't have the context there
			data = repositoryContext.getCompiledReportProvider().getCompiledReport(executionContext, path);
		} else {
			try {
				FileResourceData resourceData = repository.getResourceData(executionContext, path);
				data = resourceData.getDataStream();
			} catch (JSResourceNotFoundException e) {
				throw e;
			}
		}

   		return data;
	}
	
	protected boolean isReportContext(RepositoryContext repositoryContext) {
		// some places (e.g. MessageSourceLoader.setupThreadRepositoryContext()) 
		// set a thread repository context with no context URI, and do not clear the thread.
		// we can only use reports related repository contexts (as created by 
		// DefaultRepositoryContextManager.createRepositoryContext()).
		return repositoryContext.getContextURI() != null 
				&& repositoryContext.getCompiledReportProvider() != null;
	}

	@Override
	public <K extends Resource> K getResource(String uri, Class<K> resourceType) {
		PersistenceService persistenceService = persistenceServices.get(resourceType);
		if (persistenceService == null) {
			if (log.isDebugEnabled()) {
				log.debug("Unknown resource type " + resourceType.getClass().getName() + " requested");
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		K resource = (K) persistenceService.load(uri, this);
		return resource;
	}

	@Override
	public OutputStream getOutputStream(String uri) {
		throw new UnsupportedOperationException();
	}

	public Map<Class<?>, PersistenceService> getPersistenceServices() {
		return persistenceServices;
	}

	public void setPersistenceServices(
			Map<Class<?>, PersistenceService> persistenceServices) {
		this.persistenceServices = persistenceServices;
	}

}
