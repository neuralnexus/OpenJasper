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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.root;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.filter.BaseSearchFilter;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */

public class ExcludeFolderFilter extends BaseSearchFilter {

    @Override
    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {

        String alias = criteria.getAlias("parent", "p");

        for (String uri : toListOfUrisToExclude(context, criteria)) {

            // Filter uri
            criteria.add(Restrictions.ne(alias + ".URI", uri));

            // Filter children uris
            String childrenUrlsExp = (uri.endsWith(Folder.SEPARATOR)) ? uri + "%" : uri + Folder.SEPARATOR + "%";
            criteria.add(Restrictions.not(Restrictions.like(alias + ".URI", childrenUrlsExp)));
        }
    }

    public List<String> toListOfUrisToExclude(ExecutionContext context, SearchCriteria criteria) {
        RepositorySearchCriteria repositorySearchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);
        List<String> excludeRelativePaths = repositorySearchCriteria.getExcludeRelativePaths();
        return (excludeRelativePaths == null) ? new ArrayList<String>() : excludeRelativePaths;
    }
}
