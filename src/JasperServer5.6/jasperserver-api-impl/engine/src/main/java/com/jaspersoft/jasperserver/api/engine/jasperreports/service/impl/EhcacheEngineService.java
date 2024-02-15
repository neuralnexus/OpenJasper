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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.OrderedMap;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;

public class EhcacheEngineService extends EngineBaseDecorator {

	final public static String IC_REFRESH_KEY = "com.jaspersoft.cascade.refreshIC";
	final public static String IC_CACHE_KEY = "com.jaspersoft.cascade.ICcacheKey";

	private Ehcache cache;

	public Ehcache getCache() {
		return cache;
	}

	public void setCache(Ehcache cache) {
		this.cache = cache;
	}

	public void setEngineService(EngineService engine) {
		setDecoratedEngine(engine);
	}

	public EngineService getEngineService() {
		return getDecoratedEngine();
	}
		
    public OrderedMap executeQuery(ExecutionContext context,
		ResourceReference queryReference, String keyColumn, String[] resultColumns,
		ResourceReference defaultDataSourceReference,
		Map parameterValues, Map<String, Class<?>> parameterTypes, boolean formatValueColumns) 
    {
		String key = (String)parameterValues.get(IC_CACHE_KEY);
		Element e = null;
		OrderedMap value = null;
        boolean refresh = parameterValues!=null&&parameterValues.containsKey(IC_REFRESH_KEY);
		if (key!=null) {
			if (refresh) {
				cache.remove(key);
			} else {
				e = cache.get(key);
	    		if (e!=null) {
	    			value = (OrderedMap)(e.getValue());
	    			if (value!=null) return value;
	    		}	    	
			}
		}
		value=getDecoratedEngine().executeQuery(context, queryReference, keyColumn, resultColumns, defaultDataSourceReference, parameterValues, parameterTypes, formatValueColumns);
		if (key!=null&&value!=null) {
			e = new Element(key,value);
			cache.put(e);
		}
		return value;
    }
   
    public void clear() {
    	cache.removeAll();
    }
}
