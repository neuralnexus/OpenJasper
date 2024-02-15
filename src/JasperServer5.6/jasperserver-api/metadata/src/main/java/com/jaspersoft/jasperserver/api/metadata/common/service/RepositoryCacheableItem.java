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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;

/**
 * Cache instance used by the cache service to identify and operate a type of
 * cacheable items.
 * 
 *  <p>
 *  A cache instance contains a callback used to create the data that should be
 *  cached for a repository resource.
 *  </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryCacheableItem.java 47331 2014-07-18 09:13:06Z kklein $
 * @see RepositoryCache
 * @since 1.0
 */
@JasperServerAPI
public interface RepositoryCacheableItem {
	
	/**
	 * Returns the name of the cache instance.
	 * 
	 * <p>
	 * The cache name is used to identify the cache instance to which cached 
	 * items belong.
	 * </p>
	 * 
	 * @return the name of the cache
	 */
	String getCacheName();

	/**
	 * Returns the data that will be cached for a repository resource.
	 * 
     * @param context the caller execution context
	 * @param resource the resource for which data should be created
	 * @return the data that will be cached for the resource
	 */
	byte[] getData(ExecutionContext context, FileResource resource);
}
