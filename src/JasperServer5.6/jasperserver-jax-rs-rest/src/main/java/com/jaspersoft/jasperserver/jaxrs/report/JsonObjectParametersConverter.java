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
package com.jaspersoft.jasperserver.jaxrs.report;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: JsonObjectParametersConverter.java 22731 2012-03-22 15:32:23Z ykovalchyk $
 */
public class JsonObjectParametersConverter {
    /**
     *
     * @param jsonParameters - JSON object
     * @return map of parameters raw data
     * @throws WebApplicationException in case if not able to parse JSON
     */
    public static Map<String, String[]> getParameterMapFromJson(JSONObject jsonParameters) {
        Map<String, String[]> parameterMap = null;
        if (jsonParameters != null)
            try {
                JSONArray keys = jsonParameters.names();
                String key;
                String[] value;
                for (int i = 0; i < keys.length(); i++) {
                    key = (String) keys.get(i);
                    value = null;
                    JSONArray array = jsonParameters.getJSONArray(key);
                    if (array != null) {
                        value = new String[array.length()];
                        for (int j = 0; j < array.length(); j++)
                            value[j] = (String) array.get(j);
                    }
                    if (key != null && value != null) {
                        if (parameterMap == null)
                            parameterMap = new HashMap<String, String[]>();
                        parameterMap.put(key, value);
                    }
                }
            } catch (Exception e) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("invalid JSON").build());
            }
        return parameterMap;
    }
}
