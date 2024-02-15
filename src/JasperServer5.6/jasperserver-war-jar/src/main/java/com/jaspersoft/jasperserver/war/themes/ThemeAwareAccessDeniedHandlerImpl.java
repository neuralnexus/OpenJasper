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

package com.jaspersoft.jasperserver.war.themes;

import org.springframework.security.AccessDeniedException;
import org.springframework.security.ui.AccessDeniedHandlerImpl;
import org.springframework.ui.context.ThemeSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ThemeResolver;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * This class extends AccessDeniedHandlerImpl to make it theme aware.
 * Normal servlet dispatching is done by DispatchServlet which takes care of ThemeResolver.
 * However in case of flow errors (like, access to a view is denied), it happens before DispatchServlet.
 * To make error page be able to use themes, this class injects ThemeResolver info into request
 *
 * User: asokolnikov
 */
public class ThemeAwareAccessDeniedHandlerImpl extends AccessDeniedHandlerImpl {

    private ThemeResolver themeResolver;
    private ThemeSource themeSource;

    @Override
    public void handle(ServletRequest request, ServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        request.setAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE, themeResolver);
        request.setAttribute(DispatcherServlet.THEME_SOURCE_ATTRIBUTE, themeSource);
        super.handle(request, response, accessDeniedException);
    }

    public ThemeResolver getThemeResolver() {
        return themeResolver;
    }

    public void setThemeResolver(ThemeResolver themeResolver) {
        this.themeResolver = themeResolver;
    }

    public ThemeSource getThemeSource() {
        return themeSource;
    }

    public void setThemeSource(ThemeSource themeSource) {
        this.themeSource = themeSource;
    }
}
