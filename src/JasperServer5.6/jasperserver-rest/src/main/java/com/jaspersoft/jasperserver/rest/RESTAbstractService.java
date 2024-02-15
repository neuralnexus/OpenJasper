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
package com.jaspersoft.jasperserver.rest;

import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.rest.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gtoffoli
 * @version $Id: RESTAbstractService.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class RESTAbstractService implements RESTService {

    private final static Log log = LogFactory.getLog(RESTAbstractService.class);
    @Resource(name = "concreteRestUtils")
    protected Utils restUtils;

    public Utils getRestUtils() {
        return restUtils;
    }

    public void setRestUtils(Utils restUtils) {
        this.restUtils = restUtils;
    }

    public static final String HTTP_GET = "get";
    public static final String HTTP_PUT = "put";
    public static final String HTTP_POST = "post";
    public static final String HTTP_DELETE = "delete";
    @Autowired
    protected ApplicationContext applicationContext;

    /**
     * Invoked by the REST Servlet when the class is initialized.
     *
     * @param ac
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = ac;
    }

    
    /**
     * Check for the request method, and dispatch the code to the right class method
     * PUT and DELETE methods can be overridden by using the X-Method-Override header or
     * the special parameter X-Method-Override when using a POST.
     *
     * @param req
     * @param resp
     * @throws ServiceException
     */
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {

        String method = req.getMethod().toLowerCase();

        // Tunnels a PUT or DELETE request over the HTTP POST request.
        String methodOverride = req.getHeader("X-Method-Override");
        if (methodOverride == null)
        {
            methodOverride = req.getParameter("X-Method-Override");
        }
        if (methodOverride != null && HTTP_POST.equals(method))
        {
            methodOverride = methodOverride.toLowerCase();
            if (HTTP_DELETE.equals(methodOverride) || HTTP_PUT.equals(methodOverride))
            {
                method = methodOverride;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("execute: Resource="+req.getPathInfo()+" Method="+ method);
        }

        if (HTTP_GET.equals(method))
        {
            doGet(req, resp);
        }
        else if (HTTP_POST.equals(method))
        {
            doPost(req, resp);
        }
        else if (HTTP_PUT.equals(method))
        {
            doPut(req, resp);
        }
        else if (HTTP_DELETE.equals(method))
        {
            doDelete(req, resp);
        }
        else
        {
            restUtils.setStatusAndBody(HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp, "Method not supported for this object type");
        }
        if (log.isDebugEnabled()) {
            log.debug("finished: Resource="+req.getPathInfo()+" Method="+ method );
        }

    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        log.error("Method GET is not supported for this object type - request params: Path: "+req.getPathInfo());
        restUtils.setStatusAndBody(HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp, "Method not supported for this object type");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        log.error("Method POST is not supported for this object type - request params: Path: "+req.getPathInfo());
        restUtils.setStatusAndBody(HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp, "Method not supported for this object type");
    }
    
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        log.error("Method PUT is not supported for this object type - request params: Path: "+req.getPathInfo());
        restUtils.setStatusAndBody(HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp, "Method not supported for this object type");
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        log.error("Method DELETE is not supported for this object type - request params: Path: "+req.getPathInfo());
        restUtils.setStatusAndBody(HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp, "Method not supported for this object type");
    }

    void setAllowedRequestParameters(){
    }
    
}
