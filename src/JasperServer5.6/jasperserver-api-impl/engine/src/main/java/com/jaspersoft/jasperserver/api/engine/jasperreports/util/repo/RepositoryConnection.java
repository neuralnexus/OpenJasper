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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryConnection.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RepositoryConnection extends URLConnection
{
	private static final Log log = LogFactory.getLog(RepositoryConnection.class);
	
	private final RepositoryContext repositoryContext;
	
	public RepositoryConnection(RepositoryContext repository, URL url)
	{
		super(url);

		this.repositoryContext = repository;
	}

	public void connect() throws IOException
	{
		connected = true;
	}

    public InputStream getInputStream() throws IOException
    {
    	try {
        	InputStream data = null;
			String path = url.getPath();
    		if (!path.startsWith(Folder.SEPARATOR)) {
				ResourceContainer reportUnit = repositoryContext.getReportUnit();
				FileResource resource = null;
				if (reportUnit != null) {
					resource = reportUnit.getResourceLocal(path);
				}

				if (resource == null || !resource.hasData()) {
					path = repositoryContext.getContextURI() + Folder.SEPARATOR + path;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Loading resource \"" + resource.getName() 
								+ "\" from in-memory report unit");
					}
					data = resource.getDataStream();
					if (resource.getFileType().equals(FileResource.TYPE_JRXML)) {
						data = repositoryContext.getCompiledReportProvider().getCompiledReport(
								repositoryContext.getExecutionContext(),
								data);//FIXME not currently used, but should do autoUpdateJRXMLResource
					}
				}
    		}

    		if (data == null) {
				if (log.isDebugEnabled()) {
					log.debug("Loading resource \"" + path + "\" from repository");
				}
				
				RepositoryService repository = repositoryContext.getRepository();
				FileResource resource = (FileResource) repository.getResource(
						repositoryContext.getExecutionContext(), path, FileResource.class);
				while (resource != null && resource.isReference()) {
					resource = (FileResource) repository.getResource(
							repositoryContext.getExecutionContext(), 
							resource.getReferenceURI(), FileResource.class);
				}
				
				if (resource == null) {
					throw new IOException("Repository file resource " + path 
							+ " could not be loaded");
				}
				
				if (resource.getFileType().equals(FileResource.TYPE_JRXML)) {
					data = repositoryContext.getCompiledReportProvider().getCompiledReport(
							repositoryContext.getExecutionContext(),
							path);
				} else {
					FileResourceData resourceData = repository.getResourceData(
							repositoryContext.getExecutionContext(), path);
					data = resourceData.getDataStream();
				}
			}

    		return data;
		} catch (JSResourceNotFoundException e) {
			throw new IOException(e.getMessage());
		}
    }
}
