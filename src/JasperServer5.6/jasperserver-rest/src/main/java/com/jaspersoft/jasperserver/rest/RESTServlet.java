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


import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.ServicesUtils;
import com.jaspersoft.jasperserver.rest.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.AccessDeniedException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/**
 *
 *
 * @author gtoffoli
 * @version $Id: RESTServlet.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RESTServlet extends HttpServlet {

    private final static Log log = LogFactory.getLog(RESTServlet.class);

    private final static String BEAN_NAME_REST_SERVICE_REGISTRY = "restServiceRegistry";
    private final static String BEAN_NAME_REST_UTILS = "restUtils";

    private static ServicesUtils servicesUtils = null;
    private static ApplicationContext applicationContext = null;
    private static RESTServicRegistry registry = null;
    protected Utils utils;


    /**
     * our initialize routine; subclasses should call this if they override it
     */
    @Override
    public void init() throws javax.servlet.ServletException {

        applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        servicesUtils = applicationContext.getBean(ServicesUtils.class);
        registry = (RESTServicRegistry)applicationContext.getBean(BEAN_NAME_REST_SERVICE_REGISTRY);
        utils = (Utils)applicationContext.getBean(BEAN_NAME_REST_UTILS);
    }

    /**
     * The REST servlet check for the resource type that has been requested.<br>
     * The resource is represented by the url i.e. /rest/<b>resource</b> or /rest/<b>report</b>
     * The resource logic is implemented by extending a RESTService.<br>
     * The RESTService handled for a specific request is configured in the file shared-config\applicationContext-rest-services.xml. It is a map which
     * binds urls to RESTServices. The RESTService implementation is in charge to accept or not the specific method type.
     *
     * @param req  HttpServletRequest
     * @param resp  HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("-------------------------------------------------------------------------------- ");
        }

        // This print writer is used only in case of exception, but in general exception should be always
        // handled inside the RESTService implementation, if available.
        PrintWriter pw = null;
        String serviceName = null;
        String path = null;

        try {

            // Get the name of the service requested by the user
            path = req.getPathInfo();
            if (path == null)
            {
               // error...
               log.error("request params: Path: "+path);
               utils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, "Service not valid");
               return;
            }

            serviceName = utils.extractServiceName(path);

            // If the service name is null or empty, return an error.
            if (serviceName == null || serviceName.length() == 0)
            {
                log.error("request params: Service"+serviceName+" Path: "+path);
                utils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, "Service not valid");
                return;
            }

            RESTService service = registry.getService(serviceName);
            if (service == null)
            {
                log.error("request params: Path: "+path+" Requested Service: "+serviceName);
                utils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, "Service not valid");
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("request params: Path: "+path+" Requested Service: "+serviceName+" Service from registry: "+service.getClass());
            }

            resp.setContentType("text/xml; charset=UTF-8");
            service.execute(req, resp);
        }
        catch (ServiceException ex)
        {
            if (log.isDebugEnabled())
                log.debug("Error executing a REST service: " + ex.getErrorCode(), ex);
            // The servlet is able to map common ServiceException errors with most appropriate REST (HTTP) error codes...
            // This should be really just the latest case, when a more specific error is not available.
            // In general the service implementation should take care to send to the client the proper response.
            switch (ex.getErrorCode())
            {
                case ServiceException.RESOURCE_NOT_FOUND:
                {
                    log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+ HttpServletResponse.SC_NOT_FOUND + " error: "+ex.getMessage(),ex);
                    utils.setStatusAndBody(HttpServletResponse.SC_NOT_FOUND, resp, "");
                    break;
                }
                case ServiceException.RESOURCE_BAD_REQUEST:
                {
                    log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+ HttpServletResponse.SC_BAD_REQUEST + " error: "+ex.getMessage(),ex);
                    utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, ex.getMessage());
                    break;
                }
                case ServiceException.FORBIDDEN:
                {
                    log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+ HttpServletResponse.SC_FORBIDDEN + " error: "+ex.getMessage(),ex);
                    utils.setStatusAndBody(HttpServletResponse.SC_FORBIDDEN, resp, ex.getMessage());
                    break;
                }
                default:
                {
                    log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_BAD_REQUEST+ " error: "+ex.getMessage(),ex);
                    utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "");
                }
            }
        }

        catch (UnsupportedOperationException ex)
        {
            log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_NOT_IMPLEMENTED+ " error: "+ex.getMessage(),ex);
            utils.setStatusAndBody(HttpServletResponse.SC_NOT_IMPLEMENTED, resp, "");
        }

        catch (ReportJobNotFoundException ex)
        {
            log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_BAD_REQUEST+ " error: "+ex.getMessage(),ex);
            utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "");
        }

        catch (JSExceptionWrapper ex){
            Exception e = ex.getOriginalException();
            if (e instanceof ConstraintViolationException || e instanceof DataIntegrityViolationException){
                log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_FORBIDDEN+ " error: "+ex.getMessage(),ex);
                utils.setStatusAndBody(HttpServletResponse.SC_FORBIDDEN, resp, "");
            }
            else{
                log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_BAD_REQUEST+ " error: "+ex.getMessage(),ex);
                utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "");
            }
        }

        catch (JSValidationException ex){
                log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_BAD_REQUEST+ " error: "+ex.getMessage(),ex);
                utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "");
        }

        catch (AccessDeniedException ex){
            log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_FORBIDDEN+ " error: "+ex.getMessage(),ex);
            utils.setStatusAndBody(HttpServletResponse.SC_FORBIDDEN, resp, ex.getMessage());

        }

        catch (IllegalStateException ex){
            log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_BAD_REQUEST+ " error: "+ex.getMessage(),ex);
            utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "");

        }

        catch (IllegalArgumentException ex){
            log.error("request params: Path: "+path+" Requested Service: "+serviceName+" httpStatus: "+HttpServletResponse.SC_BAD_REQUEST+" Error: "+ ex.getMessage(),ex);
            utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "");

        }
        catch (Exception ex)
        {
            // This should Never happen. If it does, it's probably an implementation bug, or the remote service
            // implementation did not provide a better or specific error.
            log.error("Error executing a REST service", ex);
            utils.setStatusAndBody(HttpServletResponse.SC_BAD_REQUEST, resp, "");
        }
    }

    
}
