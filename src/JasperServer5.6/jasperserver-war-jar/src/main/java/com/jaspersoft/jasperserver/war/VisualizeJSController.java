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
package com.jaspersoft.jasperserver.war;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This controller intended to create initialized copy of Visualize.js
 *
 * @author Zakhar Tomchenko
 * @version $Id: VisualizeJSController.java 47331 2014-07-18 09:13:06Z kklein $
 */

public class VisualizeJSController implements Controller {
    private final String LOG_ENABLED_PARAMETER = "logEnabled";
    private final String LOG_LEVEL_PARAMETER = "logLevel";
    private final String BASE_URL_PARAMETER = "baseUrl";
    private String defaultUrl;
    @Resource
    private Map<String, String> clientLogging;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String baseUrl = defaultUrl != null && !defaultUrl.isEmpty()
                ? defaultUrl
                : request.getRequestURL().toString().replace("/client/visualize.js", "");
        final String baseUrlRequestParameter = request.getParameter(BASE_URL_PARAMETER);
        if(baseUrlRequestParameter != null && !baseUrlRequestParameter.isEmpty()){
            baseUrl = baseUrlRequestParameter;
        }
        final String logEnabled = "false".equalsIgnoreCase(request.getParameter(LOG_ENABLED_PARAMETER))
                ? "false" : clientLogging.get("enabled");
        final String logLevelRequestParameter = request.getParameter(LOG_LEVEL_PARAMETER);
        final String logLevel = logLevelRequestParameter != null && !logLevelRequestParameter.isEmpty()
                ? logLevelRequestParameter : clientLogging.get("level");

        final ModelAndView result = new ModelAndView("modules/visualize");
        result.addObject("baseUrl", baseUrl);
        result.addObject("logEnabled", logEnabled);
        result.addObject("logLevel", logLevel);

        return result;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }
}
