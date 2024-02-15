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

package com.jaspersoft.jasperserver.api.metadata.security;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.Authentication;
import org.springframework.security.acl.AclManager;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link BasicAclEntry} ACLs based method argument voter that works with
 * collections and arrays of objects. 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BasicMethodCollectionArgumentVoter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class BasicMethodCollectionArgumentVoter extends
		BasicMethodArgumentVoter {

	private static final Log log = LogFactory.getLog(BasicMethodCollectionArgumentVoter.class);
	
	@Override
	protected boolean accessPermitted(Authentication authentication,
			AclManager aclManager, Object secureObject) {
		boolean permitted = true;
		for (Iterator it = getObjectsIterator(secureObject); it.hasNext();) {
			Object object = (Object) it.next();
			if (!super.accessPermitted(authentication, aclManager, object)) {
				permitted = false;
				break;
			}
		}
		return permitted;
	}
	
	protected Iterator getObjectsIterator(Object secureObject) {
		Iterator<?> iterator;
		if (secureObject instanceof Collection) {
			iterator = ((Collection) secureObject).iterator();
		} else if (secureObject != null && secureObject.getClass().isArray()) {
			iterator = new ArrayIterator(secureObject);
		} else {
			iterator = new SingletonIterator(secureObject);
		}
		return iterator;
	}
	
}
