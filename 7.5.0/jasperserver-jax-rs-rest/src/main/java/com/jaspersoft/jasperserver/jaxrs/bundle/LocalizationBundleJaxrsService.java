/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.jaxrs.bundle;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.war.ResourceForwardingServlet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.jaspersoft.jasperserver.war.common.JasperServerHttpConstants.FORWARDED_PARAMETERS;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

/**
 * JAX-RS service "localization bundles" implementation.
 * <p/>
 * The caching works in next way:
 * <p/>
 * 1. Specified two headers: ETag and Vary (see HTTP 1.1 spec.)
 * 2. In browser response is cached, but since Vary header set to "Accept-Language",
 * cache invalidated on change of the Accept-Language header
 *
 * @author Igor.Nesterenko, Zahar.Tomchenko
 * @version $Id$
 */
@Service
@Path("/bundles")
@Scope("prototype")
public class LocalizationBundleJaxrsService {

    protected List<String> bundleNames;

    @Resource(name = "exposedMessageSource")
    private ExposedResourceBundleMessageSource messageSource;

    @Context
    private Providers providers;

    @Resource(name = "bundlePathsList")
    public void setBundleNames(List<String> bundlePathsList) {
        bundleNames = new ArrayList<String>(bundlePathsList.size());
        for (String bundlePath : bundlePathsList) {
            bundleNames.add(bundlePath.substring(bundlePath.lastIndexOf(Folder.SEPARATOR) + Folder.SEPARATOR_LENGTH));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBundles(@QueryParam(RestConstants.QUERY_PARAM_EXPANDED) Boolean expanded,
                               @Context HttpHeaders headers, @Context Request request, @Context HttpServletRequest httpRequest) {
        if (httpRequest != null && expanded == null) {
            Map<String, String[]> forwardedParameters = (Map<String, String[]>)httpRequest
                    .getAttribute(FORWARDED_PARAMETERS);
            if (forwardedParameters != null && forwardedParameters.get(RestConstants.QUERY_PARAM_EXPANDED) != null) {
                expanded = Boolean.valueOf(forwardedParameters.get(RestConstants.QUERY_PARAM_EXPANDED)[0]);
            }
        }
        if (Boolean.TRUE.equals(expanded)) {
            Locale locale = headers.getAcceptableLanguages().get(0);

            ObjectNode json = JsonNodeFactory.instance.objectNode();
            for (String currentBundle : bundleNames) {
                final Map<String, String> messages = messageSource.getAllMessagesForBaseName(currentBundle, locale);
                ObjectNode currentMessages = JsonNodeFactory.instance.objectNode();
                for (String key : messages.keySet()) {
                    currentMessages.put(key, messages.get(key));
                }
                json.put(currentBundle, currentMessages);
            }

            EntityTag etag = generateETag(json);
            Response.ResponseBuilder response = request.evaluatePreconditions(etag);

            if (response == null) {
                response = Response.ok(json).tag(etag);
            }

            return response.build();
        } else {
            return Response.ok(bundleNames).build();
        }
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBundle(@PathParam("name") String bundleName, @Context HttpHeaders headers, @Context Request request) {
        Locale locale = headers.getAcceptableLanguages().get(0);

        Map<String, String> messages = messageSource.getAllMessagesForBaseName(bundleName, locale);

        EntityTag etag = generateETag(toJson(messages));
        Response.ResponseBuilder response = request.evaluatePreconditions(etag);

        if (response == null) {
            response = messages.isEmpty() ? Response.noContent() : Response.ok(messages);
        }

        return response.tag(etag).build();
    }

    public byte[] toJson(Map<String, String> messages) {
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

        ByteArrayOutputStream entityStream = new ByteArrayOutputStream();
        MessageBodyWriter<Map> messageBodyWriter = providers.getMessageBodyWriter(Map.class, Map.class, new Annotation[0], mediaType);

        if (messageBodyWriter != null) {
            try {
                messageBodyWriter.writeTo(messages, Map.class, Map.class, new Annotation[0], mediaType, null, entityStream);
                return entityStream.toByteArray();
            } catch (IOException e) {  }
        }
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        for (String key : messages.keySet()) {
            messages.put(key, messages.get(key));
        }

        return json.toString().getBytes();
    }

    protected EntityTag generateETag(ObjectNode messagesJson) {
        if (messagesJson == null) {
            return null;
        } else {
            return generateETag(messagesJson.toString().getBytes());
        }
    }

    protected EntityTag generateETag(byte[] bytes) {
        return new EntityTag(md5DigestAsHex(bytes));
    }
}
