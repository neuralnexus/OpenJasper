/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class PooledObjectCache {
    public static final String ATHENA_JDBC_DS = "athena-jdbc-ds";
    public static final String JDBC_AWSATHENA = "jdbc:awsathena";
    public static final String ARN_AWS_IAM = "arn:aws:iam";
    final Map cache;
    PooledObjectEntry first, last;
    PooledObjectCacheLog log;

    public PooledObjectCache() {
        cache = new HashMap();
        first = last = null;
    }

    public PooledObjectCacheLog getLog() {
        return log;
    }

    public void setLog(PooledObjectCacheLog log) {
        this.log = log;
    }

    public PooledObjectEntry get(Object key, long now) {
        PooledObjectEntry entry = (PooledObjectEntry) cache.get(key);
        if (entry == null) {
            return null;
        }
        moveFirst(entry);
        entry.access(now);
        return entry;
    }

    protected void moveFirst(PooledObjectEntry entry) {
        if (first != entry) {
            removeFromSequence(entry);
            addFirst(entry);
        }
    }

    protected void addFirst(PooledObjectEntry entry) {
        entry.next = first;
        entry.prev = null;
        if (first != null) {
            first.prev = entry;
        }
        first = entry;
        if (last == null) {
            last = entry;
        }
    }

    public void put(Object key, PooledObjectEntry entry, long now) {
        addFirst(entry);
        entry.access(now);
        entry.created(now);
        cache.put(key, entry);
    }

    public List<PooledObjectEntry> removeExpired(long now, int timeout, int athenaTimeOut) {
        List expired = new ArrayList();
        long expTime = now - timeout * 1000;
        long athenaExpTime = now - athenaTimeOut*1000;

        PooledObjectEntry entry = last;
        while (entry != null ) {
            String entryKey = entry.getKey().toString();
            if (entry.lastAccess < expTime ||
                ((entryKey.contains(ATHENA_JDBC_DS) || (entryKey.contains(JDBC_AWSATHENA) && entryKey.contains(ARN_AWS_IAM))) && entry.created < athenaExpTime)) {
                if (entry.isActive()) {
                    if (log != null) log.debug(entry.key, PooledObjectCacheLog.DebugCode.STILL_ACTIVE);
                } else {
                    if (log != null) log.debug(entry.key, PooledObjectCacheLog.DebugCode.EXPIRING);
                    expired.add(entry);
                    remove(entry);
                }
            }
            entry = entry.prev;
        }
        return expired;
    }

    protected void remove(PooledObjectEntry entry) {
        cache.remove(entry.key);
        removeFromSequence(entry);
    }

    protected void removeFromSequence(PooledObjectEntry entry) {
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        }
        if (entry.next != null) {
            entry.next.prev = entry.prev;
        }
        if (first == entry) {
            first = entry.next;
        }
        if (last == entry) {
            last = entry.prev;
        }
    }

    public interface PooledObjectCacheLog {
        public static enum DebugCode {
            STILL_ACTIVE,
            EXPIRING;
        }

        public void debug(Object key, DebugCode code);
    }

}
