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
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.war.common.RoleAccessUrisResolver;
import com.jaspersoft.jasperserver.war.common.UriDescriptor;
import org.hibernate.criterion.Restrictions;

/**
 * Folder filter.
 *
 * @author Stas Shubar
 * @version $Id: FolderFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class FolderFilter extends BaseSearchFilter implements Serializable {
    private RoleAccessUrisResolver roleAccessUrisResolver;

    public void setRoleAccessUrisResolver(RoleAccessUrisResolver roleAccessUrisResolver) {
        this.roleAccessUrisResolver = roleAccessUrisResolver;
    }

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        SearchMode searchMode = null;
        String folderUri = null;
        SearchAttributes searchAttributes = getSearchAttributes(context);
        if(searchAttributes != null){
            searchMode = searchAttributes.getMode();
            folderUri = searchAttributes.getState() != null ? searchAttributes.getState().getFolderUri() : null;
        }else{
            RepositorySearchCriteria searchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);
            if(searchCriteria != null){
                searchMode = searchCriteria.getSearchMode();
                folderUri = searchCriteria.getFolderUri();
            }
        }

        if (folderUri != null) {
            String absoluteUri = roleAccessUrisResolver.getAbsoluteUri(folderUri);

            String alias = criteria.getAlias("parent", "p");
            if (SearchMode.BROWSE == searchMode) {
                criteria.add(Restrictions.eq(alias + ".URI", absoluteUri));
            } else { // Other modes are search like at the moment.
                addSearchModeFolderRestrictions(criteria, absoluteUri, alias);
            }

            addRoleAccessUrlsRestrictions(criteria);
        }
    }

    protected String createSubFoldersLikeUri(String folderUri) {
        return (folderUri.endsWith(Folder.SEPARATOR)) ? folderUri + "%" : folderUri + Folder.SEPARATOR + "%";
    }

    protected void addSearchModeFolderRestrictions(SearchCriteria criteria, String folderUri, String alias) {
        criteria.add(Restrictions.or(Restrictions.like(alias + ".URI", createSubFoldersLikeUri(folderUri)),
                Restrictions.eq(alias + ".URI", folderUri)));
    }

    private void addRoleAccessUrlsRestrictions(SearchCriteria criteria) {
        String alias = criteria.getAlias("parent", "p");

        for (UriDescriptor uriDescriptor : roleAccessUrisResolver.getRestrictedUris()) {
            String uri = (uriDescriptor.isAbsolute()) ? uriDescriptor.getUri() :
                   roleAccessUrisResolver.getAbsoluteUri(uriDescriptor.getUri());
            // Filter uri
            criteria.add(Restrictions.ne(alias + ".URI", uri));

            // Filter children uris
            String childrenUrlsExp = (uri.endsWith(Folder.SEPARATOR)) ? uri + "%" : uri + Folder.SEPARATOR + "%";
            criteria.add(Restrictions.not(Restrictions.like(alias + ".URI", childrenUrlsExp)));
        }
    }
}
