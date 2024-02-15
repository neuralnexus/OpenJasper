/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.war.common;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Random;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class JavascriptOptimizationSettings implements JavascriptOptimizationSettingsMBean {
    public static final String OPTIMIZE_JAVASCRIPT_SESSION_PARAM = "optimizeJavascript";
    private static final Logger log = LogManager.getLogger(JavascriptOptimizationSettings.class);

    private Boolean useOptimizedJavascript;
    private String optimizedJavascriptPath;

    // random string per server lifecycle
    private static String runtimeHash = Integer.toHexString(new Random().nextInt()).toUpperCase();

    public Boolean getUseOptimizedJavascript() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes==null) {
            return useOptimizedJavascript;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
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

    public String getRuntimeHash() {
        return runtimeHash;
    }

    public void reGenerateRuntimeHash() {
        runtimeHash = Integer.toHexString(new Random().nextInt()).toUpperCase();
    }
}
