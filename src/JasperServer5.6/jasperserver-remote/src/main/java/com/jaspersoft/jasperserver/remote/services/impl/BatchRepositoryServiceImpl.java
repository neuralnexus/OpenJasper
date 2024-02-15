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

package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ResourceInUseException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.converters.LookupResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.services.BatchRepositoryService;
import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettingsResolver;
import com.jaspersoft.jasperserver.search.service.ItemProcessor;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchResult;
import com.jaspersoft.jasperserver.search.service.RepositorySearchService;
import com.jaspersoft.jasperserver.search.service.impl.RepositorySearchAccumulator;
import com.jaspersoft.jasperserver.search.service.impl.RepositorySearchCriteriaImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.JDBCException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.SpringSecurityException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class BatchRepositoryServiceImpl implements BatchRepositoryService {

    protected static final Log log = LogFactory.getLog(BatchRepositoryServiceImpl.class);


    @javax.annotation.Resource(name = "concreteRepository")
    private RepositoryService service;
    @javax.annotation.Resource
    private RepositorySearchService repositorySearchService;
    @javax.annotation.Resource
    private LookupResourceConverter lookupResourceConverter;
    @javax.annotation.Resource
    private ResourceConverterProvider resourceConverterProvider;
    @javax.annotation.Resource
    private UriHardModifyProtectionChecker uriHardModifyProtectionChecker;
    @javax.annotation.Resource
    private SearchCriteriaFactory searchCriteriaFactory;
    @javax.annotation.Resource(name = "restSearchModeSettingsResolver")
    private SearchModeSettingsResolver searchModeSettingsResolver;

    @Override
    public RepositorySearchResult<ClientResourceLookup> getResources(String q, String folderUri, List<String> type, Integer start, Integer limit, Boolean recursive, Boolean showHiddenItems, String sortBy, AccessType accessType, User user, Boolean forceFullPage) throws IllegalParameterValueException, ResourceNotFoundException {
        SearchMode mode = (recursive == null || recursive) ? SearchMode.SEARCH : SearchMode.BROWSE;
        RepositorySearchConfiguration configuration = getConfiguration(mode);

        final RepositorySearchCriteriaImpl.Builder builder = new RepositorySearchCriteriaImpl.Builder()
                .setSearchMode(mode)
                .setFolderUri(folderUri != null ? folderUri : Folder.SEPARATOR)
                .setStartIndex(start != null ? start : 0)
                .setMaxCount(limit != null ? limit : configuration.getItemsPerPage())
                .setForceFullPage(forceFullPage != null ? forceFullPage : false)
                .setShowHidden(showHiddenItems)
                .setAccessType(accessType)
                .setSortBy(sortBy)
                .setSearchText(q)
                .setUser(user)
                .setResourceTypes(type);

        return getResources(builder.getCriteria());
    }

    public RepositorySearchResult<ClientResourceLookup> getResources(RepositorySearchCriteria criteria) throws IllegalParameterValueException, ResourceNotFoundException {
        if (!validateCriteria(criteria)) {

            return RepositorySearchAccumulator.EMPTY_RESULT;
        }

        if (criteria.isForceFullPage()) {
            RepositorySearchResult<ResourceLookup> result =
                    repositorySearchService.getLookupsForFullPage(null, criteria);

            return result.transform(new ItemProcessor<ResourceLookup, ClientResourceLookup>() {
                @Override
                public ClientResourceLookup call(ResourceLookup resource) {
                    return lookupResourceConverter.toClient(resource, null);
                }
            });
        } else {
            RepositorySearchAccumulator<ClientResourceLookup> result =
                    new RepositorySearchAccumulator<ClientResourceLookup>(
                            criteria.getStartIndex(), criteria.getMaxCount(), criteria.getMaxCount());

            List<ResourceLookup> resources = repositorySearchService.getLookups(null, criteria);
            result.fill(criteria, transform(resources));

            return result;
        }

    }


    protected boolean validateCriteria(RepositorySearchCriteria searchCriteria)
            throws IllegalParameterValueException, ResourceNotFoundException {

        if (searchCriteria != null && processType(searchCriteria)){
            String folderUri = searchCriteria.getFolderUri();
            if(folderUri != null){
                validateFolderUri(folderUri);
            }

            return true;
        }

        return false;
    }

    protected List<ClientResourceLookup> transform(List<ResourceLookup> lookups) {
        List<ClientResourceLookup> result = new ArrayList<ClientResourceLookup>(lookups.size());
        if (lookups != null) {
            for (ResourceLookup lookup : lookups) {
                result.add(lookupResourceConverter.toClient(lookup, null));
            }
        }
        return result;
    }

    @Override
    public int getResourcesCount(String q, String folderUri, List<String> type, Boolean recursive, Boolean showHiddenItems, AccessType accessType, User user) throws IllegalParameterValueException, ResourceNotFoundException {

        final RepositorySearchCriteriaImpl.Builder builder = new RepositorySearchCriteriaImpl.Builder()
                .setSearchMode(recursive == null || recursive ? SearchMode.SEARCH : SearchMode.BROWSE)
                .setFolderUri(folderUri != null ? folderUri : Folder.SEPARATOR)
                .setShowHidden(showHiddenItems)
                .setAccessType(accessType)
                .setSearchText(q)
                .setUser(user)
                .setResourceTypes(type);

        return getResourcesCount(builder.getCriteria());
    }

    public int getResourcesCount(RepositorySearchCriteria searchCriteria) throws IllegalParameterValueException, ResourceNotFoundException {
        Integer result = 0;

        if (searchCriteria != null && processType(searchCriteria)) {
            String folderUri = searchCriteria.getFolderUri();
            if(folderUri != null){
                validateFolderUri(folderUri);
            }
            result = repositorySearchService.getResultsCount(null, searchCriteria);
        }
        return result;
    }

    protected boolean processType(RepositorySearchCriteria searchCriteria) {
        Boolean searchSuitable = true;
        List<String> types = searchCriteria.getResourceTypes();
        if (types != null && !types.isEmpty()) {
            if (types.contains(ClientTypeHelper.extractClientType(ClientResource.class))) {
                // ResourceLookup is a valid repository resource, but without persistence. So, search for resource lookups isn't suitable.
                searchSuitable = false;
            } else {
                List<String> serverTypes = new LinkedList<String>();
                if (types.remove(ClientTypeHelper.extractClientType(ClientFile.class))) {
                    // here is an exception from a common rule: one client object corresponds to te only server object and back
                    // Single ClientFile client object corresponds to either FileResource or ContentResource on server side.
                    // Do this trick here
                    serverTypes.add(FileResource.class.getName());
                    serverTypes.add(ContentResource.class.getName());
                }

                for (String type : types) {
                    try {
                        serverTypes.add(resourceConverterProvider.getToServerConverter(type).getServerResourceType());
                    } catch (IllegalParameterValueException e) {
                        // ignore wrong or unknown types
                    }
                }
                searchCriteria.setResourceTypes(serverTypes);
                // if all types incorrect - we should not search anything
                searchSuitable = !serverTypes.isEmpty();
            }
        }
        return searchSuitable;
    }

    /**
     * This method validates if folder URI is correct and exists
     *
     * @param folderUri - folder URI to check
     * @throws IllegalParameterValueException - in case if URI has invalid format
     * @throws ResourceNotFoundException - in case if folder with given URI doesn't exist
     */
    protected void validateFolderUri(String folderUri) throws IllegalParameterValueException, ResourceNotFoundException {
        if(!folderUri.startsWith(Folder.SEPARATOR)){
            throw new IllegalParameterValueException("folderUri", folderUri);
        } else if(service.getFolder(null, folderUri) == null){
            throw new ResourceNotFoundException(folderUri);
        }
    }


    public void deleteResources(List<String> uris) throws AccessDeniedException, ResourceNotFoundException {
        for (String uri: uris){
            if(uriHardModifyProtectionChecker.isHardModifyProtected(uri)){
                throw new AccessDeniedException(uri);
            }
            try{
                Resource resource = service.getResource(null, uri);
                if (resource != null){
                    List<ResourceLookup> dependentResources = service.getDependentResources(null, uri, searchCriteriaFactory, 0, 0);
                    if (dependentResources == null || dependentResources.isEmpty()){
                        service.deleteResource(null, uri);
                    } else {
                        throw new ResourceInUseException(dependentResources);
                    }
                } else {
                    resource = service.getFolder(null, uri);
                    if (resource != null){
                        service.deleteFolder(null, uri);
                    }
                }
            } catch (SpringSecurityException sse) {
                throw new AccessDeniedException(uri);
            } catch (JSExceptionWrapper w) {
                throw SingleRepositoryServiceImpl.getRootException(w);
            }
        }
    }


    @Override
    public int getResourcesCountByTypes(List<String> types) {

        Integer totalCount = 0;

        try {
            totalCount = getResourcesCount(null, null, types, true, false, null, null);
        } catch (IllegalParameterValueException e) {
            log.error(e);
        } catch (ResourceNotFoundException e) {
            log.error(e);
        }

        return totalCount;
    }

    protected RepositorySearchConfiguration getConfiguration(SearchMode mode) {
        return searchModeSettingsResolver.getSettings(mode).getRepositorySearchConfiguration();
    }
}
