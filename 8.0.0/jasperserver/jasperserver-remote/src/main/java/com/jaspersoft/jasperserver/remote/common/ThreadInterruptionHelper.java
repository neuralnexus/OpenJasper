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
package com.jaspersoft.jasperserver.remote.common;

import com.jaspersoft.jasperserver.remote.exception.OperationCancelledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ThreadInterruptionHelper {
    private final static Log log = LogFactory.getLog(ThreadInterruptionHelper.class);
    public static void checkInterrupted(){
        final Thread currentThread = Thread.currentThread();
        if(currentThread.isInterrupted()){
            if(log.isDebugEnabled()){
                final StackTraceElement stackTraceElement = currentThread.getStackTrace()[2];
                log.debug("Thread '" + currentThread.getName() + "' is interrupted. Execution of " +
                        stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + " is cancelled");
            }
            throw new OperationCancelledException(new InterruptedException());
        }
    }
}
