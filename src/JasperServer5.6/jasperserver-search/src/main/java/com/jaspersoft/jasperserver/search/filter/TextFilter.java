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

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.ResourceCriterionUtils;
import com.jaspersoft.jasperserver.api.metadata.common.util.DatabaseCharactersEscapeResolver;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

/**
 * Text filter.
 *
 * @author Yuriy Plakosh
 * @version $Id: TextFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class TextFilter extends BaseSearchFilter implements Serializable {
    private DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver;

    public void setDatabaseCharactersEscapeResolver(DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver) {
        this.databaseCharactersEscapeResolver = databaseCharactersEscapeResolver;
    }

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {

        criteria.add(ResourceCriterionUtils.getTextCriterion(getText(context)));
    }

    protected String getText(ExecutionContext context) {
        SearchAttributes searchAttributes = getSearchAttributes(context);
        RepositorySearchCriteria repositorySearchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);

        String text = searchAttributes != null && searchAttributes.getState() != null ?
                searchAttributes.getState().getText() :
                repositorySearchCriteria != null ? repositorySearchCriteria.getSearchText() : null;

        return text == null ? "" : databaseCharactersEscapeResolver.getEscapedText(text);
    }

}
