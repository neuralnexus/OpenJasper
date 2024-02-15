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
package com.jaspersoft.jasperserver.search.service;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.mode.SearchMode;

import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: RepositorySearchCriteria.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface RepositorySearchCriteria extends Cloneable {
    SearchMode getSearchMode();
    void setSearchMode(SearchMode searchMode);
    String getSortBy();
    void setSortBy(String sortBy);
    int getStartIndex();
    void setStartIndex(int startIndex);
    int getMaxCount();
    void setMaxCount(int maxCount);
    String getSearchText();
    void setSearchText(String searchText);
    String getFolderUri();
    void setFolderUri(String folderUri);
    List<String> getResourceTypes();
    void setResourceTypes(List<String> resourceTypes);
    List<SearchFilter> getCustomFilters();
    void setShowHidden(boolean show);
    boolean isShowHidden();
    void setExcludeFolders(boolean withFolders);
    boolean isExcludeFolders();
    void setCustomFilters(List<SearchFilter> customFilters);
    void addCustomFilter(SearchFilter customFilter);
    void setUser(User user);
    User getUser();
    AccessType getAccessType();
    void setAccessType(AccessType mod);
    void setExcludeRelativePaths(List<String> strings);
    List<String> getExcludeRelativePaths();
    void setForceFullPage(boolean forceFullPage);
    boolean isForceFullPage();
    RepositorySearchCriteria clone() throws CloneNotSupportedException;
}
