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

package com.jaspersoft.jasperserver.ws.axis2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: MapResourceHandlerRegistry.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class MapResourceHandlerRegistry implements ResourceHandlerRegistry, InitializingBean {

	private Map typeHandlers;
	private Map handlers;
	private Map resourceTypes;
    private ResourceActionResolver resourceActionResolver;

    public void setResourceActionResolver(ResourceActionResolver resourceActionResolver) {
        this.resourceActionResolver = resourceActionResolver;
    }

    public void afterPropertiesSet() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		handlers = new HashMap();
		resourceTypes = new HashMap();
		
		for (Iterator it = typeHandlers.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String wsType = (String) entry.getKey();
            ResourceHandler handler = null;
            if(entry.getValue() instanceof String) {
                String handlerClassName = (String) entry.getValue();

                Class handlerClass = Class.forName(handlerClassName);
                if (!ResourceHandler.class.isAssignableFrom(handlerClass)) {
                    throw new JSException("Resource handler class " + handlerClassName + " does not implement com.jaspersoft.jasperserver.ws.axis2.ResourceHandler");
                }

                handler = (ResourceHandler) handlerClass.newInstance();
            } else {
                handler = (ResourceHandler)entry.getValue();
            }
			handlers.put(wsType, handler);
			
			Class resourceType = handler.getResourceType();
			resourceTypes.put(resourceType.getName(), wsType);

            ResourceResolverAdapter adapter = (ResourceResolverAdapter)handler;
            adapter.setResourceActionHandler(resourceActionResolver);
		}
	}
	
	public ResourceHandler getHandler(String wsType) {
		ResourceHandler handler = (ResourceHandler) handlers.get(wsType);
		return handler;
	}

	public ResourceHandler getHandler(Resource resource) {
		String resourceType = resource.getResourceType();
		String wsType = (String) resourceTypes.get(resourceType);
		if (wsType == null) {
			synchronized (resourceTypes) {
				//determine a (more generic) type for the resource
				wsType = determineWSType(resource);
				//cache the type
				resourceTypes.put(resourceType, wsType);
			}
		}
		return getHandler(wsType);
	}

	protected String determineWSType(Resource resource) {
		String wsType = null;
		
		LinkedList itfQueue = new LinkedList();
		Class[] itfs = resource.getClass().getInterfaces();
		for (int i = 0; i < itfs.length; i++) {
			itfQueue.add(itfs[i]);
		}
		
		while (!itfQueue.isEmpty()) {
			Class itf = (Class) itfQueue.removeFirst();
			wsType = (String) resourceTypes.get(itf.getName());
			if (wsType != null) {
				break;
			}
			
			Class[] interfaces = itf.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				itfQueue.add(interfaces[i]);
			}
		}
		
		return wsType;
	}

	public boolean typeExtends(String type, String parentType) {
		ResourceHandler handler = (ResourceHandler) handlers.get(type);
		Class resourceType = handler.getResourceType();
		ResourceHandler parentHandler = (ResourceHandler) handlers.get(parentType);
		Class parentResourceType = parentHandler.getResourceType();
		return parentResourceType.isAssignableFrom(resourceType);
	}

	public Map getTypeHandlers() {
		return typeHandlers;
	}

	public void setTypeHandlers(Map typeHandlers) {
		this.typeHandlers = typeHandlers;
	}

}
