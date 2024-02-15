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
package com.jaspersoft.jasperserver.search.common;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.search.QueryModificationEvaluator;
import com.jaspersoft.jasperserver.search.filter.AccessTypeFilter;
import com.jaspersoft.jasperserver.search.filter.FolderFilter;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

/**
 * Implementation of {@link QueryModificationEvaluator}.
 *
 * @author Chaim Arbiv
 * @author Yuriy Plakosh
 * @version $Id: QueryModificationEvaluatorImpl.java 47331 2014-07-18 09:13:06Z kklein $
 * @since 4.7
 *
 * @see com.jaspersoft.jasperserver.api.search.QueryModificationEvaluator;
 */
public class QueryModificationEvaluatorImpl implements QueryModificationEvaluator {
    @javax.annotation.Resource
    private FolderFilter folderFilter;

    /**
     * In case of filtering by access event we are going from the access events table to the resource table so we will
     * have duplicates. In this case we need to add distinct projection to the query.
     *
     * @param context the execution context.
     * @return <code>true</code> if distinct projection should be applied, <code>false</code> otherwise.
     */
    @Override
    public boolean useFullResource(ExecutionContext context) {
        boolean use = context != null && context.getAttributes() != null && context.getAttributes().contains(FORCE_REPO_RESOURCE);

        if (!use){
            SearchAttributes searchAttributes = getTypedAttribute(context, SearchAttributes.class);
            use = searchAttributes != null &&
                    searchAttributes.getState() != null &&
                    searchAttributes.getState().getCustomFiltersMap() != null &&
                    searchAttributes.getState().getCustomFiltersMap().
                            get(AccessTypeFilter.ACCESS_TYPE_FILTER_NAME) != null &&
                    !searchAttributes.getState().getCustomFiltersMap().get(AccessTypeFilter.ACCESS_TYPE_FILTER_NAME).equals(
                            AccessTypeFilter.ACCESS_TYPE_FILTER_ALL_OPTION);
        }

        if (!use){
            RepositorySearchCriteria searchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);
            use = searchCriteria != null && searchCriteria.getCustomFilters() != null && (!searchCriteria.getCustomFilters().isEmpty()
                    // this code is a trick, done on order to not refactor search itself to give it ability to work both on UI and Web services properly
                    // but this should be done in future  ... I hope
                    && !(searchCriteria.getCustomFilters().size() == 1 && searchCriteria.getCustomFilters().contains(folderFilter)));
        }
        return use;
    }


    protected <T> T getTypedAttribute(ExecutionContext context, Class<T> attributeClass) {
        T result = null;
        if (context != null && context.getAttributes() != null && !context.getAttributes().isEmpty())
            for (Object currentAttribute : context.getAttributes())
                if (attributeClass.isAssignableFrom(currentAttribute.getClass())) {
                    // casting safety is checked above
                    @SuppressWarnings("unchecked")
                    final T typedAttribute = (T) currentAttribute;
                    result = typedAttribute;
                    break;
                }
        return result;
    }
}
