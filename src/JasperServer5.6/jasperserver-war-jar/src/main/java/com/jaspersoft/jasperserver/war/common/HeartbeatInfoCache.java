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
package com.jaspersoft.jasperserver.war.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HeartbeatInfoCache implements HeartbeatContributor, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map infoCache = new HashMap();
	

	public void update(HeartbeatInfo info)
	{
		HeartbeatInfo oldInfo = (HeartbeatInfo)infoCache.get(info);
		info = oldInfo == null ? info : oldInfo;
		info.incrementCount();
		infoCache.put(info, info);
	}
	
	public void contributeToHttpCall(PostMethod post)
	{
		for(Iterator it = infoCache.keySet().iterator(); it.hasNext();)
		{
			HeartbeatInfo info = (HeartbeatInfo)it.next();
			
			info.contributeToHttpCall(post);
		}
	}
	
	public int size()
	{
		return infoCache.size();
	}
	
}
