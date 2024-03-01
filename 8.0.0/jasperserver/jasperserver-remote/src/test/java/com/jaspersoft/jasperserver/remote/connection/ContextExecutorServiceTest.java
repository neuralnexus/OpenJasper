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

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ContextExecutorServiceTest {
    @InjectMocks
    private ContextExecutorService contextExecutorService = new ContextExecutorService();
    private ContextExecutorService spy;
    @Mock
    private ExecutorService executor;
    @Mock
    private volatile Map<UUID, List<Future<?>>> tasks;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        spy = spy(contextExecutorService);
    }
    @BeforeMethod
    public void refresh(){
        reset(spy, executor, tasks);
    }

    @Test
    public void runWithContext() throws Exception {
        final Callable callable = mock(Callable.class);
        final Object expectedCallableResult = new Object();
        doReturn(expectedCallableResult).when(callable).call();
        final UUID contextUuid = UUID.randomUUID();
        final Future expectedFuture = mock(Future.class);
        final ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        when(executor.submit(callableArgumentCaptor.capture())).thenReturn(expectedFuture);
        final SecurityContext initialContext = SecurityContextHolder.getContext();

        final SecurityContext securityContextMock = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContextMock);

        final Future result = spy.runWithContext(contextUuid, callable);
        assertSame(result, expectedFuture);
        verify(spy).attachToContext(contextUuid, expectedFuture);
        final Callable callableWrapper = callableArgumentCaptor.getValue();

        // clearing context to emulate separate thread
        SecurityContextHolder.clearContext();
        // ensure, that security context has been changed
        assertNotSame(SecurityContextHolder.getContext(), securityContextMock);

        // execute callable
        final Object actualCallableResult = callableWrapper.call();
        // ensure, that correct security context instance has been set to a holder when callable has been called
        assertSame(SecurityContextHolder.getContext(), securityContextMock);
        assertSame(actualCallableResult, expectedCallableResult);

        // restore initial security context
        SecurityContextHolder.setContext(initialContext);
    }
    
    @Test
    public void cancelContext(){
        final UUID contextUuid = UUID.randomUUID();
        final List<Future> futures = Arrays.asList(mock(Future.class), mock(Future.class), mock(Future.class));
        doReturn(futures).when(spy).getFuturesOfContext(contextUuid);
        spy.cancelContext(contextUuid);
        for (Future future : futures) {
            verify(future).cancel(true);
        }
    }

    @Test
    public void attachToContext() throws ExecutionException, InterruptedException {
        final UUID contextUuid = UUID.randomUUID();
        final Future future = mock(Future.class);
        final ArrayList<Future<?>> futures = new ArrayList<Future<?>>();
        doReturn(futures).when(spy).getFuturesOfContext(contextUuid);
        final ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        doReturn(null).when(executor).submit(runnableArgumentCaptor.capture());

        spy.attachToContext(contextUuid, future);

        assertEquals(futures.size(), 1);
        assertSame(futures.get(0), future);
        final Runnable runnable = runnableArgumentCaptor.getValue();
        assertNotNull(runnable);
        runnable.run();
        assertTrue(futures.isEmpty());
        verify(tasks).remove(contextUuid);
    }

    @Test
    public void getFuturesOfContext_contextExist_expectedList(){
        final UUID contextUuid = UUID.randomUUID();
        final ArrayList<Future<?>> expectedRsult = new ArrayList<Future<?>>();
        doReturn(expectedRsult).when(tasks).get(contextUuid);
        final List<Future<?>> result = spy.getFuturesOfContext(contextUuid);
        assertSame(result, expectedRsult);
    }
    
    @Test
    public void getFuturesOfContext_noContext_newList(){
        final UUID contextUuid = UUID.randomUUID();
        doReturn(null).when(tasks).get(contextUuid);
        final List<Future<?>> result = spy.getFuturesOfContext(contextUuid);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tasks).put(contextUuid, result);
    }
}
