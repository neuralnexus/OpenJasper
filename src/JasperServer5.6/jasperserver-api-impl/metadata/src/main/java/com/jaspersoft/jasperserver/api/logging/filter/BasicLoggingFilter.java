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
package com.jaspersoft.jasperserver.api.logging.filter;

import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;
import com.jaspersoft.jasperserver.api.JSException;

import javax.servlet.*;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.springframework.security.AccessDeniedException;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public abstract class BasicLoggingFilter implements Filter {

    private LoggingContextProvider loggingContextProvider;
    private Exception originalException = null;

    public void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
        this.loggingContextProvider = loggingContextProvider;
    }

    protected abstract void beforeChain(ServletRequest request) throws ServletException;

    protected abstract void logException(ServletRequest request, Exception filterException) throws ServletException;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            beforeChain(request);
            chain.doFilter(request, response);
        } catch (IOException e) {
            originalException = e;
            throw e;
        } catch (ServletException e) {
            originalException = e;
            throw e;
        } catch (RuntimeException e) {
            originalException = e;
            throw e;
        } finally {
            logException(request, originalException);
            originalException = null;
            loggingContextProvider.flushContext();
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}
