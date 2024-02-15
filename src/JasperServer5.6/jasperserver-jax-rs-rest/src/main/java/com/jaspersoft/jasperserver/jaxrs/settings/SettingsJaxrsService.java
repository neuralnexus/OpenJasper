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
package com.jaspersoft.jasperserver.jaxrs.settings;

import com.jaspersoft.jasperserver.jaxrs.common.JacksonMapperProvider;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.cache.CacheControlHelper;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.settings.SettingsProvider;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: SettingsJaxrsService.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component
@Path("/settings")
public class SettingsJaxrsService {
    @Resource
    private Map<String, Object> settingsGroups;
    @Resource
    private JacksonMapperProvider jacksonMapperProvider;

    @GET
    @Path("/{groupId}")
    @Produces("application/json")
    public Response getSettingsGroup(@PathParam("groupId") String groupId) throws IOException {
        Object settingsGroup = settingsGroups.get(groupId);
        if(settingsGroup instanceof SettingsProvider){
            settingsGroup = ((SettingsProvider) settingsGroup).getSettings();
        }
        if (settingsGroup == null) {
            throw new ResourceNotFoundException(groupId);
        }
        return CacheControlHelper.enableLocaleAwareStaticCache(Response.ok(
                jacksonMapperProvider.getContext(settingsGroup.getClass()).writer().writeValueAsString(settingsGroup))
        ).build();
    }

}
