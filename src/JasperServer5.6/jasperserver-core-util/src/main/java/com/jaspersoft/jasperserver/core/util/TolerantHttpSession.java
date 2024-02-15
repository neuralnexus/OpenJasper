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
package com.jaspersoft.jasperserver.core.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.SessionAttribMissingException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Wrapper class for the HttpSession.
 *
 * Created by nthapa on 6/28/13.
 */

public class TolerantHttpSession implements HttpSession
{

    protected final Logger log = Logger.getLogger(getClass());

    private HttpSession httpSession;

    public TolerantHttpSession(HttpSession obj)
    {
        httpSession=obj;
    }

    @Override
    public long getCreationTime() {
        return httpSession.getCreationTime();
    }

    @Override
    public String getId() {
        return httpSession.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return httpSession.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return httpSession.getServletContext();

    }

    @Override
    public void setMaxInactiveInterval(int i) {
        httpSession.setMaxInactiveInterval(i);
    }

    @Override
    public int getMaxInactiveInterval() {
        return httpSession.getMaxInactiveInterval();
    }

    /**
     * @deprecated
     */
    @Override
    public HttpSessionContext getSessionContext() {
        return httpSession.getSessionContext();
    }



    /*
    *Overrides the getAttribute method of HttpSession class. If the object type is MissingObject throws SessionAttribMissingException
    *
    * @param name name of the attribute
    * @return Object associated with the name
    *
    *
     */
    @Override
    public Object getAttribute(String name) throws SessionAttribMissingException{
        Object obj= httpSession.getAttribute(name);

        if (obj instanceof TolerantObjectWrapper) {
            Object obw = ((TolerantObjectWrapper) obj).getObject();
            if (obw instanceof  MissingObject) {
                if (log.isDebugEnabled())
                    log.debug("Missing value found on session for key "+name);
                removeAllMissingObjects();
                throw new SessionAttribMissingException("cluster.exception.session.attribute.missing");
            }
            return obw;
        } else {
            return obj;
        }
    }

    private List<String> removeAllMissingObjects() {
        List<String> missingAttributeNames = new ArrayList<String>();
        Enumeration<String> sessionAttributes = httpSession.getAttributeNames();
        while(sessionAttributes.hasMoreElements()){
            String attributeName = sessionAttributes.nextElement();
            Object obj = httpSession.getAttribute(attributeName);
            if (obj instanceof TolerantObjectWrapper && ((TolerantObjectWrapper) obj).getObject() instanceof MissingObject) {
                missingAttributeNames.add(attributeName);
                httpSession.removeAttribute(attributeName);
            }
        }
        return missingAttributeNames;
    }

    /**
     * @deprecated
     */
    @Override
    public Object getValue(String s) {
        return this.getAttribute(s);
    }

    @Override
    public Enumeration getAttributeNames() {
        return httpSession.getAttributeNames();
    }

    /**
     * @deprecated
     */
    @Override
    public String[] getValueNames() {
        return httpSession.getValueNames();
    }


    /*
    *Overrides the setAttribute method. For all the objects that are to be stored in the session, a wrapper object is created and then
    * stored in the session by calling the httpsession's setAttribute
    *
    * @param name key for the attribute
    * @param obj object to be stored in the session
    * @return void
    *
     */
    @Override
    public void setAttribute(String name, Object obj) {

        TolerantObjectWrapper ObjWrap= new TolerantObjectWrapper(obj);
        httpSession.setAttribute(name,ObjWrap);

    }

    /**
     * @deprecated
     */
    @Override
    public void putValue(String name, Object obj) {
        this.setAttribute(name, obj);

    }

    @Override
    public void removeAttribute(String s) {
        httpSession.removeAttribute(s);
    }

    /**
     * @deprecated
     */
    @Override
    public void removeValue(String s) {
        httpSession.removeValue(s);

    }

    @Override
    public void invalidate() {
        httpSession.invalidate();

    }

    @Override
    public boolean isNew() {
        return httpSession.isNew();
    }

}