/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.inputcontrols.cascade.token;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Used to determine a concrete implementation of <b>ParameterTypeLookup</b> which depends on the dataSource type
 * </p>
 *
 * @author Vlad Zavadskyi
 */
@Service
public class ParameterTypeCompositeLookup implements ParameterTypeLookup {
    private final Map<String, ParameterTypeLookup> lookupMapping = new ConcurrentHashMap<>();

    @Override
    public Map<String, Class<?>> getParameterTypes(ExecutionContext context,
                                                   ResourceReference dataSource,
                                                   Set<String> parameterNames) throws CascadeResourceNotFoundException {
        String type = getResourceType(dataSource);

        if (type != null && lookupMapping.containsKey(type)) {
            ParameterTypeLookup lookup = lookupMapping.get(type);
            return lookup.getParameterTypes(context, dataSource, parameterNames);
        }

        return Collections.emptyMap();
    }

    String getResourceType(ResourceReference dataSource) {
        if(dataSource == null) {
            return null;
        }
        if (dataSource.isLocal()) {
            return dataSource.getLocalResource() != null
                    ? dataSource.getLocalResource().getClass().getName()
                    : null;
        } else {
            return dataSource.getReferenceLookup() != null
                    ? dataSource.getReferenceLookup().getResourceType()
                    : null;
        }
    }

    /**
     * Registers new <b>ParameterTypeLookup</b> implementation
     *
     * @param resourceClassName resource type
     * @param lookup            concrete implementation
     */
    public void registerLookup(String resourceClassName, ParameterTypeLookup lookup) {
        lookupMapping.put(resourceClassName, lookup);
    }
}
