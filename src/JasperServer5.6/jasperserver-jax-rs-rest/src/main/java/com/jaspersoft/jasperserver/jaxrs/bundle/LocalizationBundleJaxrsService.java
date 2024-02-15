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

package com.jaspersoft.jasperserver.jaxrs.bundle;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.jaxrs.common.RestConstants;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.cache.CacheControlHelper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
 * @version $Id: LocalizationBundleJaxrsService.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
@Path("/bundles")
public class LocalizationBundleJaxrsService {

    protected List<String> bundleNames;

    @Resource(name = "exposedMessageSource")
    private ExposedResourceBundleMessageSource messageSource;

    @Resource(name = "bundlePathsList")
    public void setBundleNames(List<String> bundlePathsList) {
        bundleNames = new ArrayList<String>(bundlePathsList.size());
        for (String bundlePath : bundlePathsList) {
            bundleNames.add(bundlePath.substring(bundlePath.lastIndexOf(Folder.SEPARATOR) + Folder.SEPARATOR_LENGTH));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBundles(@QueryParam(RestConstants.QUERY_PARAM_EXPANDED) Boolean expanded, @Context HttpHeaders headers) throws JSONException {
        if (Boolean.TRUE.equals(expanded)) {
            Locale locale = headers.getAcceptableLanguages().get(0);
            JSONObject json = new JSONObject();
            for (String currentBundle : bundleNames) {
                final Map<String, String> messages = messageSource.getAllMessagesForBaseName(currentBundle, locale);
                JSONObject currentMessages = new JSONObject();
                for (String key : messages.keySet()) {
                    currentMessages.put(key, messages.get(key));
                }
                json.put(currentBundle, currentMessages);
            }
            return CacheControlHelper.enableLocaleAwareStaticCache(Response.ok(json)).build();
        } else {
            return Response.ok(bundleNames).build();
        }
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBundle(@PathParam("name") String bundleName, @Context HttpHeaders headers) {

        Locale locale = headers.getAcceptableLanguages().get(0);

        Map<String, String> messages = messageSource.getAllMessagesForBaseName(bundleName, locale);

        return CacheControlHelper
                .enableLocaleAwareStaticCache(
                        messages.isEmpty() ? Response.noContent() : Response.ok(messages)
                )
                .build();
    }
}
