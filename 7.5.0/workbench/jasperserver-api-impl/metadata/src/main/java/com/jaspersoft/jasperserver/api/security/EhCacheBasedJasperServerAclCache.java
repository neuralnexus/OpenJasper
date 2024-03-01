/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.security;

import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclImpl;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.util.FieldUtils;
import org.springframework.util.Assert;

/**
 * @author Oleg.Gavavka
 */

public class EhCacheBasedJasperServerAclCache implements NonMutableAclCache {
    private static final Log log = LogFactory.getLog(EhCacheBasedJasperServerAclCache.class);
    private final Ehcache cache;
    private PermissionGrantingStrategy permissionGrantingStrategy;

    public EhCacheBasedJasperServerAclCache(Ehcache cache, PermissionGrantingStrategy permissionGrantingStrategy) {
        Assert.notNull(cache, "Cache required");
        Assert.notNull(permissionGrantingStrategy, "PermissionGrantingStrategy required");
        this.cache = cache;
        this.permissionGrantingStrategy = permissionGrantingStrategy;

    }

    @Override
    public void evictFromCache(ObjectIdentity objectIdentity) {
        Assert.notNull(objectIdentity, "ObjectIdentity required");
        Boolean result=cache.remove(objectIdentity.getIdentifier().toString());

        // Alarm when we try to evict root permissions - this means we was changing them
        if (result && (objectIdentity.getIdentifier().toString().equals("/") ||
                objectIdentity.getIdentifier().toString().equals("repo:/"))) {
            log.warn("Clearing permission cache for root !!!");
        }

    }

    @Override
    public Acl getFromCache(ObjectIdentity objectIdentity) {
        Assert.notNull(objectIdentity, "ObjectIdentity required");

        Element element = null;

        try {
            element = cache.get(objectIdentity.getIdentifier().toString());
        } catch (CacheException ignored) {
            log.error("**** Error getting ACL from cache for " + objectIdentity.getIdentifier().toString(), ignored);
        }

        if (element == null) {
            return null;
        }



        Acl toreturn = initializeTransientFields((Acl)element.getValue());
        return toreturn;
    }

    @Override
    public void putInCache(Acl acl) {
        Assert.notNull(acl, "Acl required");
        Assert.notNull(acl.getObjectIdentity(), "ObjectIdentity required");

        if ((acl.getParentAcl() != null) && (acl.getParentAcl() instanceof Acl)) {
            putInCache(acl.getParentAcl());
        }
        if (acl instanceof JasperServerAclImpl) {
            if (((JasperServerAclImpl) acl).getCacheMarker()) {
                //Acl origin is from cache - skip caching
                return;
            }
        }
        //Alarm when we try to update permission in cache for root folder
        if ((acl.getObjectIdentity().getIdentifier().toString().equals("/") ||
                acl.getObjectIdentity().getIdentifier().toString().equals("repo:/")) &&
                getFromCache(acl.getObjectIdentity())!=null) {
            if (acl.getEntries().size()<3) {
                log.error("EhCacheBasedJasperServerAclCache: permission on root have less than 3 records !!!");
                Thread.dumpStack();
            } else {
                log.info("EhCacheBasedJasperServerAclCache: Updating permissions for root !!!".concat(acl.toString()));
            }
        }

        cache.put(new Element(acl.getObjectIdentity().getIdentifier().toString(), acl));


    }

    private Acl initializeTransientFields(Acl value) {
        if (value instanceof JasperServerAclImpl) {
            FieldUtils.setProtectedFieldValue("permissionGrantingStrategy", value, this.permissionGrantingStrategy);
            FieldUtils.setProtectedFieldValue("cacheMarker", value, true);
            if (value.isEntriesInheriting()) {
                //seeking parent in cache
                Acl parentAcl = getFromCache(((JasperServerAclImpl) value).getParentOid());
                // parent is missing in cache - invalidate this Acl
                if (parentAcl==null) {
                    evictFromCache(value.getObjectIdentity());
                    return null;
                }
                FieldUtils.setProtectedFieldValue("parentAcl", value, parentAcl);

            }
        }

        if (value.getParentAcl() != null) {
            if(null==initializeTransientFields(value.getParentAcl())){
            	evictFromCache(value.getObjectIdentity());
            	return null;
            };
        }
        return value;
    }

    @Override
    public void clearCache() {
        cache.removeAll();

    }
}
