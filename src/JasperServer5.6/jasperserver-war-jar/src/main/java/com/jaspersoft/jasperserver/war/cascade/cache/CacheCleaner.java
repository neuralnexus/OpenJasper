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
package com.jaspersoft.jasperserver.war.cascade.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

/**
 * CacheCleaner
 * @author jwhang
 * @version $Id: CacheCleaner.java 47331 2014-07-18 09:13:06Z kklein $
 */

public class CacheCleaner extends TimerTask {

    private volatile Map<String, SessionCache> userCachePool;
    private long userCacheTimeout;

    public CacheCleaner(Map<String, SessionCache> userCachePool,
            long userCacheTimeout){
        this.userCachePool = userCachePool;
        this.userCacheTimeout = userCacheTimeout;
    }

    public void run(){
        long curTime = System.currentTimeMillis();

        synchronized (userCachePool) {
            Iterator<String> keyIterator = userCachePool.keySet().iterator();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();

                SessionCache cache = userCachePool.get(key);
                if ((curTime - cache.getLastInteraction()) > userCacheTimeout) {
                    //remove this object.
                    keyIterator.remove();
                }
            }
        }
    }
}
