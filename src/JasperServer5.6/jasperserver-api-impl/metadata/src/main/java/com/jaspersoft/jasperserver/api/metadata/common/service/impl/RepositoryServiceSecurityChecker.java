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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;


/**
 * This class adds security/permission checking utilities for RepositoryService
 */
public class RepositoryServiceSecurityChecker extends BaseRepositorySecurityChecker {
	
	private static final Log log = LogFactory.getLog(RepositoryServiceSecurityChecker.class);
	private RepositoryService securityChecker;
	
	public RepositoryServiceSecurityChecker() {
		
	}
    
    /** Checks whether the given resource can be edited */
    public boolean isEditable(Resource resource) {
    	try {
			securityChecker.saveResource(null,resource);
			return true;
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
                log.debug("No UPDATE permission for < " + resource.getURIString() + ">:" +  e.getMessage());
            }
			return false;
		}
    }
    
    /** Checks whether the given resource can be deleted */
    public boolean isRemovable(Resource resource) {
    	try {
			securityChecker.deleteResource(null,resource.getURI());
			return true;
		} catch (Exception e) {
            if(log.isDebugEnabled()) {
    			log.debug("No DELETE permission for < " + resource.getURIString() + ">:" +  e.getMessage());
            }
			return false;
		}	
    }
    
    public boolean isResourceReadable(String uri) {
    	try {
			securityChecker.getResource(null, uri);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    public boolean isFolderReadable(String uri) {
    	try {
			securityChecker.getFolder(null, uri);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

	public RepositoryService getSecurityChecker() {
		return securityChecker;
	}

	public void setSecurityChecker(RepositoryService securityChecker) {
		this.securityChecker = securityChecker;
	}
}
