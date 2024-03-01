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
package com.jaspersoft.jasperserver.remote.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class ContextExecutorService {
    private final static Log log = LogFactory.getLog(ContextExecutorService.class);
    private ExecutorService executor = Executors.newCachedThreadPool();
    private volatile Map<UUID, List<Future<?>>> tasks = new HashMap<UUID, List<Future<?>>>();

    public <T> Future<T> runWithContext(UUID contextUuid, final Callable<T> callable) {
        final SecurityContext context = SecurityContextHolder.getContext();
        // we are wrapping source callable in order to forward security context to a target thread.
        final Future<T> future = executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                SecurityContextHolder.setContext(context);
                return callable.call();
            }
        });
        attachToContext(contextUuid, future);
        return future;
    }

    public void cancelContext(UUID contextUuid){
        final ArrayList<Future<?>> toCancel = new ArrayList<Future<?>>(getFuturesOfContext(contextUuid));
        if(!toCancel.isEmpty()) {
            if(log.isDebugEnabled())log.debug("About to cancel " + toCancel.size() + " tasks of context " + contextUuid);
            for (Future<?> future : toCancel) {
                future.cancel(true);
            }
            if(log.isDebugEnabled())log.debug("Cancelled " + toCancel.size() + " tasks of context " + contextUuid);
        }
    }

    protected void attachToContext(final UUID contextUuid, final Future<?> future) {
        final List<Future<?>> futures = getFuturesOfContext(contextUuid);
        futures.add(future);
        if(log.isDebugEnabled())log.debug("Task is attached to context " + contextUuid);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    future.get();
                } catch (Exception e) {
                    if(log.isDebugEnabled()) log.debug("Task execution is interrupted. Context " + contextUuid);
                    // do nothing. This future should be removed from a list in finally block. Exception doesn't matter
                } finally {
                    futures.remove(future);
                    if(log.isDebugEnabled())log.debug("Task is detached from a context " + contextUuid);
                }
                if(futures.isEmpty()){
                    synchronized (tasks){
                        if(futures.isEmpty()){
                            tasks.remove(contextUuid);
                        }
                    }
                }
            }
        });
    }

    protected List<Future<?>> getFuturesOfContext(UUID contextUuid) {
        List<Future<?>> futures = tasks.get(contextUuid);
        if (futures == null) {
            synchronized (tasks) {
                futures = tasks.get(contextUuid);
                if (futures == null) {
                    futures = new ArrayList<Future<?>>();
                    tasks.put(contextUuid, futures);
                }
            }
        }
        return futures;
    }
}
