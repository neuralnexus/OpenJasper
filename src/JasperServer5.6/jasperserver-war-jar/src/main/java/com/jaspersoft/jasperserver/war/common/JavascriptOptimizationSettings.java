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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Sergey Prilukin
 * @version $Id: JavascriptOptimizationSettings.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JavascriptOptimizationSettings {
    public static final String OPTIMIZE_JAVASCRIPT_SESSION_PARAM = "optimizeJavascript";
    private static final Logger log = LogManager.getLogger(JavascriptOptimizationSettings.class);

    private Boolean useOptimizedJavascript = false;
    private String optimizedJavascriptPath = "optimized-scripts";

    public Boolean getUseOptimizedJavascript() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        Boolean optimize = (Boolean)session.getAttribute(OPTIMIZE_JAVASCRIPT_SESSION_PARAM);

        if (optimize == null) {
            session.setAttribute(OPTIMIZE_JAVASCRIPT_SESSION_PARAM, useOptimizedJavascript);

            if (useOptimizedJavascript) {
                ServletContext servletContext = request.getSession().getServletContext();

                if (servletContext != null) {
                    String path = servletContext.getRealPath(optimizedJavascriptPath);
                    if (path == null) {
                        log.warn("Can not check whether optimized-scripts folder exists");
                    } else if (!(new java.io.File(path).exists())) {
                        //javascript optimization is enabled
                        //but optimized script folder is absent
                        //we should fallback to not optimized version
                        session.setAttribute(OPTIMIZE_JAVASCRIPT_SESSION_PARAM, false);
                        log.warn("javascript optimization is enabled but optimized-script folder is absent. Fallback to not optimized version");
                    }
                } else {
                    log.warn("Can not check whether optimized-scripts folder exists");
                }
            }
        }

        return (Boolean)session.getAttribute(OPTIMIZE_JAVASCRIPT_SESSION_PARAM);
    }

    public void setUseOptimizedJavascript(Boolean useOptimizedJavascript) {
        this.useOptimizedJavascript = useOptimizedJavascript;
    }

    public void setOptimizedJavascriptPath(String optimizedJavascriptPath) {
        this.optimizedJavascriptPath = optimizedJavascriptPath;
    }

    public String getOptimizedJavascriptPath() {
        return optimizedJavascriptPath;
    }
}
