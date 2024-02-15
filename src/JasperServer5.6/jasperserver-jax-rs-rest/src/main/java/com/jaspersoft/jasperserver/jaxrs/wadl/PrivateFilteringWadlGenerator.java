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

package com.jaspersoft.jasperserver.jaxrs.wadl;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class PrivateFilteringWadlGenerator implements WadlGenerator {
    private WadlGenerator delegate;

    @Override
    public void setWadlGeneratorDelegate(WadlGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public void init() throws Exception {
        delegate.init();
    }

    @Override
    public String getRequiredJaxbContextPath() {
        return delegate.getRequiredJaxbContextPath();
    }

    @Override
    public Application createApplication() {
        return delegate.createApplication();
    }

    @Override
    public Resources createResources() {
        return delegate.createResources();
    }

    @Override
    public Resource createResource(AbstractResource r, String path) {
        Resource res = null;
        if (r.getAnnotation(PrivateApi.class) != null) {
            r.getFields().clear();
            r.getComponents().clear();
            r.getResourceMethods().clear();
            r.getSubResourceLocators().clear();
            r.getSubResourceMethods().clear();
        } else {
            res = delegate.createResource(r, path);
        }
        return res;
    }

    @Override
    public Method createMethod(AbstractResource r, AbstractResourceMethod m) {
        Method res = null;
        if (m.getAnnotation(PrivateApi.class) != null) {
            m.getParameters().clear();
        } else {
            res = delegate.createMethod(r, m);
        }
        return res;
    }

    @Override
    public Request createRequest(AbstractResource r, AbstractResourceMethod m) {
        return delegate.createRequest(r, m);
    }

    @Override
    public Representation createRequestRepresentation(AbstractResource r, AbstractResourceMethod m, MediaType mediaType) {
        return delegate.createRequestRepresentation(r, m, mediaType);
    }

    @Override
    public List<Response> createResponses(AbstractResource r, AbstractResourceMethod m) {
        if (m.getAnnotation(PrivateApi.class) != null){
            return null;
        }
        return delegate.createResponses(r, m);
    }

    @Override
    public Param createParam(AbstractResource r, AbstractMethod m, Parameter p) {
        return delegate.createParam(r, m, p);
    }

    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        return delegate.createExternalGrammar();
    }

    @Override
    public void attachTypes(ApplicationDescription description) {
        delegate.attachTypes(description);
    }
}
