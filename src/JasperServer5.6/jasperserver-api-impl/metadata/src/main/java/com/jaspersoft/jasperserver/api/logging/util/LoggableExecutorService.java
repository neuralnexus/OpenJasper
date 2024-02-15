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
package com.jaspersoft.jasperserver.api.logging.util;

import com.jaspersoft.jasperserver.api.logging.audit.context.RequestType;
import com.jaspersoft.jasperserver.api.logging.audit.context.RequestTypeListener;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A special {@link Executor} service that asynchronously execute the tasks in which logging events are setup.
 *
 * @author vsabadosh
 * @version $Id: LoggableExecutorService.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class LoggableExecutorService implements Executor {
    private LoggingContextProvider loggingContextProvider;
    private RequestTypeListener requestTypeListener;

    private ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
        this.loggingContextProvider = loggingContextProvider;
    }

    public void setRequestTypeListener(RequestTypeListener requestTypeListener) {
        this.requestTypeListener = requestTypeListener;
    }

    @Override
    public void execute(final Runnable command) {
        //As requestType is thread local then we have to move it from main thread to the new thread.
        final RequestType requestType = requestTypeListener.getRequestType();
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                requestTypeListener.setRequestType(requestType);
                command.run();
                //flushing logging context after task is executed.
                loggingContextProvider.flushContext();
            }
        });
    }

}
