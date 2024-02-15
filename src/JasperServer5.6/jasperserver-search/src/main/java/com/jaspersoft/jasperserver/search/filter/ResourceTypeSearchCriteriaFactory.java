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

package com.jaspersoft.jasperserver.search.filter;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceItem;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;

import java.util.List;
import java.util.Map;

/**
 * FIXME!!!!
 * It's configured as a singleton, and it's assumed that the resource type will always by Resource.
 * That means you can only create criteria expressions based on Resource--no subtypes.
 * The hardcoded resourceType is then passed to various other places, but no one ever does anything with it,
 * and I can't figure out why it's even there.
 * <p/>
 * Changes I'm making are really minimal:
 * - turn private hardcoded getResourceType() into a property
 * - add a newFactory() method so we can create copies on the fly that can actually do more than just query Resource props
 * <p/>
 * Other things that should be addressed are to make it MT-aware
 * (currently, you have to manually adjust any repo path you're using in a filter)
 * @version $Id: ResourceTypeSearchCriteriaFactory.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ResourceTypeSearchCriteriaFactory implements SearchCriteriaFactory, Cloneable {
    private ResourceFactory persistentClassMappings;
    private Map<String, SearchFilter> typeSpecificFilters;
    private String resourceType;

    public SearchCriteriaFactory newFactory(String type) {
        try {
            ResourceTypeSearchCriteriaFactory newOne = (ResourceTypeSearchCriteriaFactory) clone();
            if (resourceType != null){
                Class classToReplace = Class.forName(type);
                Class oldClass = Class.forName(resourceType);

                if (!classToReplace.isAssignableFrom(oldClass)){
                    newOne.setResourceType(type);
                }
            } else {
                newOne.setResourceType(type);
            }

            return newOne;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("unexpected", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("unexpected", e);
        }
    }

    public SearchCriteria create(ExecutionContext context, List<SearchFilter> filters) {

        // Search criteria creation.
        Class clazz = resourceType == null ? RepoResourceItem.class : persistentClassMappings.getImplementationClass(resourceType);
        SearchCriteria searchCriteria = SearchCriteria.forClass(clazz);

        // TODO: remove applying of typeSpecificFilters as redundant.
        // Sub-classing of current factory can be used if required.
        // RepositoryService should be refactored to.
        if (typeSpecificFilters.get(getResourceType()) != null) {
            typeSpecificFilters.get(getResourceType()).applyRestrictions(getResourceType(), context, searchCriteria);
        }

        // Applying regular filters.
        if (filters != null) {
            for (SearchFilter filter : filters) {
                filter.applyRestrictions(getResourceType(), context, searchCriteria);
            }
        }

        return searchCriteria;
    }

    public void applySorter(ExecutionContext context, SearchCriteria searchCriteria, SearchSorter sorter) {
        String resourceType = getResourceType();
        // Applying sorters.
        if (sorter != null) {
            sorter.applyOrder(resourceType, context, searchCriteria);
        }
    }

    public void setPersistentClassMappings(ResourceFactory persistentClassMappings) {
        this.persistentClassMappings = persistentClassMappings;
    }

    public void setTypeSpecificFilters(Map<String, SearchFilter> typeSpecificFilters) {
        this.typeSpecificFilters = typeSpecificFilters;
    }

    public void setResourceType(String defaultResourceType) {
        this.resourceType = defaultResourceType;
    }

    public String getResourceType() {
        if (resourceType == null){
            return Resource.class.getName();
        }
        return resourceType;
    }

}