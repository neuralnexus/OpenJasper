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
package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.dto.executions.ExecutionStatus;
import com.jaspersoft.jasperserver.remote.services.ReportExecution;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.ReportContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static java.lang.String.format;


/**
 * Configuring Run Report cache after cache manager is ready.
 *
 * @author esytnik, schubar
 * @version $ Id$
 */
@Component
public class RunReportServiceCacheFactoryBean implements FactoryBean<Cache>, InitializingBean {
    private static final Log log = LogFactory.getLog(RunReportServiceCacheFactoryBean.class);

    private static final String DEFAULT_CACHE_NAME = "RRSCache";

    @Resource(name = "cacheManager")
    private CacheManager manager;

    private String cacheName = DEFAULT_CACHE_NAME;

    private Cache cache;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public Cache getObject() throws CacheException {
        if (cache == null) {
            throw new CacheException(format("Cache '%s' is not configured.", getCacheName()));
        }

        return cache;
    }

    @Override
    public Class<?> getObjectType() {
        return this.cache != null ? this.cache.getClass() : Cache.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws CacheException {
        final String FAIL_MSG = format("Failed to configure %s cache.", getCacheName());

        if (manager == null) {
            throw new CacheException(format("%s Missing 'cacheManager' cache manger.", FAIL_MSG));
        }

        if (log.isDebugEnabled()) {
            log.debug(format("************ RRS *********: manager: %s", this.manager.getName()));
        }

        if (manager.cacheExists(getCacheName())) {
            cache = manager.getCache(getCacheName());

            if (cache.getCacheEventNotificationService() == null) {
                throw new CacheException(format("%s Cache is not initialized.", FAIL_MSG));
            }

            if (log.isDebugEnabled()) {
                log.debug(format("************ RRS *********: Cache: %s", cache.getName()));
            }

            final RegisteredEventListeners cacheEventNotificationService = cache.getCacheEventNotificationService();

            cacheEventNotificationService.getCacheEventListeners().clear();
            cacheEventNotificationService.registerListener(new RunReportCacheEventListener());

        } else {
            throw new CacheException(format("%s Cache '%s' doesn't exist.", FAIL_MSG, getCacheName()));
        }
    }

    private static class RunReportCacheEventListener implements CacheEventListener {
        // freaking Oracle edge case
        public Object clone() {
            return this.clone();
        }

        @Override
        public void notifyRemoveAll(Ehcache arg0) {
        }

        @Override
        public void notifyElementUpdated(Ehcache arg0, Element arg1) throws CacheException {
        }

        @Override
        public void notifyElementRemoved(Ehcache arg0, Element element) throws CacheException {
            if (log.isDebugEnabled()) {
                log.debug("33816 DEBUG: remove element: " + element.getObjectKey());
            }
        }

        @Override
        public void notifyElementPut(Ehcache arg0, Element element) throws CacheException {
            if (log.isDebugEnabled()) {
                log.debug("33816 DEBUG: put element: " + element.getObjectKey());
            }
        }

        @Override
        public void notifyElementExpired(Ehcache arg0, Element arg1) {
        }

        @Override
        public void notifyElementEvicted(Ehcache arg0, Element element) {
            String requestId = (String) element.getObjectKey();
            ReportExecution execution = (ReportExecution) element.getObjectValue();
            try {
                // cancelReportExecution((String)requestId);
                if (execution.getStatus() == ExecutionStatus.ready
                        || execution.getStatus() == ExecutionStatus.cancelled) {
                    cleanupRUR(execution.getFinalReportUnitResult());
                }
            } catch (RuntimeException ex) {
                log.warn("Report execution cleanup failed: ", ex);
            }
            if (log.isDebugEnabled()) {
                log.debug("33816 DEBUG: evicted element: " + requestId);
            }
        }

        @Override
        public void dispose() {
        }

        private void cleanupRUR(ReportUnitResult rur) {
            if (rur == null) {
                return;
            }
            // and virtualizer
            JRVirtualizer v = rur.getVirtualizer();
            if (v != null) {
                v.cleanup();
            }
            // clear context
            ReportContext ctx = rur.getReportContext();
            if (ctx != null) {
                ctx.clearParameterValues();
            }
            rur.setReportContext(null);
            // go through pages and clear circular references too
            JasperPrint jp = rur.getJasperPrint();
            if (jp != null) {
                if (jp.getPages() != null) {
                    for (JRPrintPage page : jp.getPages()) {
                        if (page.getElements() != null) {
                            page.getElements().clear();
                        }
                    }
                    jp.getPages().clear();
                }
            }
            // cleanup printer
            rur.setJasperPrintAccessor(null);
        }
    }
}
