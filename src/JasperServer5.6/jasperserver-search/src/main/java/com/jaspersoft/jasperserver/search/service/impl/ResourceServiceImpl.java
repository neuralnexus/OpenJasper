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

package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.search.service.ResourceService;
import com.jaspersoft.jasperserver.search.service.ChildrenLoaderService;
import com.jaspersoft.jasperserver.search.common.ResourceDetails;
import com.jaspersoft.jasperserver.search.common.SchedulingChecker;

import java.util.*;

import com.jaspersoft.jasperserver.search.service.ResourceTypeResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resources management service.
 *
 * @author Stas Chubar
 * @author Yuriy Plakosh
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ResourceServiceImpl extends BaseService implements ResourceService {

    private static final Log log = LogFactory.getLog(ResourceServiceImpl.class);

    private SessionFactory sessionFactory;

    private RepositorySecurityChecker securityChecker;
    protected ObjectPermissionService permissionService;
    private SchedulingChecker schedulingChecker;
    private ResourceTypeResolver typeResolver;
    private Map<String, ChildrenLoaderService> childrenLoaders;
    private List<String> deleteOrder;
    private List<String> checkDependentResourcesFor;
    private SearchCriteriaFactory searchCriteriaFactory;
    private int maxDependentReports = 20;
    private String tempFolderName;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public ResourceDetails update(String uri, String label, String description) {
        String parentUri = uri.substring(0, uri.lastIndexOf("/"));

        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentUri));

        Resource resource = repositoryService.getResource(null, uri);
        if (!label.equals(resource.getLabel()) || !description.equals(resource.getDescription())) {
            resource.setLabel(label);
            resource.setDescription(description);

            repositoryService.saveResource(null, resource);
        }

        return getResourceDetails(repositoryService.getResource(null, uri));
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(final List<Resource> resources) {
        Collections.sort(resources, new DeleteOrderComparator());

        for (Resource resource : resources) {
            deleteTempDependencies(resource);
            repositoryService.deleteResource(null, resource.getURIString());
        }
    }

    /**
     * Delete dependencies from temporary folder
     *
     * @param resource
     */
    private void deleteTempDependencies(Resource resource) {
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext();
        List<ResourceLookup> dependentResources = repositoryService.getDependentResources(context, resource.getURIString(), searchCriteriaFactory, 0, 0); //from zero index, unlimited count
        if(dependentResources == null) {
            return;
        }
        for(ResourceLookup depResource: dependentResources) {
            if(depResource.getURIString().startsWith(tempFolderName)) {
                deleteTempDependencies(depResource);
                repositoryService.deleteResource(context, depResource.getURIString());
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<ResourceDetails> check(ExecutionContext context, final List<Resource> resources) {
        Collections.sort(resources, new DeleteOrderComparator());

        List<ResourceDetails> result = new ArrayList<ResourceDetails>();
        for (Resource resource : resources) {
            List<ResourceDetails> dependentResources = getDependentResources(context, resource.getURIString());
            if (dependentResources != null) {
                result.addAll(dependentResources);
            }

            if (result.size() >= maxDependentReports) {
                break;
            }
        }

        if (!result.isEmpty()) {
            if (result.size() > maxDependentReports) {
                result = result.subList(0, maxDependentReports);
            }

            Collections.sort(result, new Comparator<ResourceLookup>() {
                @Override
                public int compare(ResourceLookup o1, ResourceLookup o2) {
                    return o1.getURIString().compareTo(o2.getURIString());
                }
            });
        }

        return result;
    }

    /**
     * Get list of dependent non-temporary resources
     *
     * @param context
     * @param resourceUri
     * @return
     */
    public List<ResourceDetails> getDependentResources(ExecutionContext context, String resourceUri) {
        List<ResourceDetails> result = null;
        final Resource resource = repositoryService.getResource(context, resourceUri);

        if (resource != null && CollectionUtils.isNotEmpty(checkDependentResourcesFor) &&
                checkDependentResourcesFor.contains(resource.getResourceType())) {

            List<ResourceLookup> dependentResources = repositoryService.getDependentResources(context, resourceUri, searchCriteriaFactory, 0, maxDependentReports);
            if(dependentResources == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Can't check for dependent reports of resource type!");
                }
                return result;
            }
            result = new ArrayList<ResourceDetails>();
            for (ResourceLookup lookup : dependentResources) {
                if(!lookup.getParentFolder().equals(tempFolderName)) {
                    result.add(new ResourceDetails(lookup));
                }
            }
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void copy(Set<String> resourceUris, String destinationFolderUri) {
        Map<String, Resource> resourceMap = getResourceMap(resourceUris);

        if (!isLabelsUnique(resourceMap)) {
            throw new JSException("jsexception.search.duplicate.label", new Object[]{"", destinationFolderUri});
        }

        ensureObjectLabelsNew(destinationFolderUri, getLabels(resourceMap));

        while (getResourcesWithUniqueName(resourceMap).size() > 0) {
            Map<String, Resource> resources = getResourcesWithUniqueName(resourceMap);
            Set<String> uris = resources.keySet();
            repositoryService.copyResources(null, uris.toArray(new String[uris.size()]), destinationFolderUri);

            for (String key : resources.keySet()) {
                resourceMap.remove(key);
            }
        }
    }

    private Set<String> getLabels(Map<String, Resource> resourceMap) {
        Set<String> result = new HashSet<String>();

        for (Resource resource : resourceMap.values()) {
            result.add(resource.getLabel());
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void move(Set<String> resourceUris, String destinationFolderUri) {
        Map<String, Resource> resourceMap = getResourceMap(resourceUris);

        if (!isLabelsUnique(resourceMap) || isObjectsLabelsExist(destinationFolderUri, getLabels(resourceMap))) {
            throw new JSException("jsexception.search.duplicate.label", new Object[]{"", destinationFolderUri});
        }

        if (resourceMap.size() != getResourcesWithUniqueName(resourceMap).size()) {
            throw new JSException("jsexception.search.duplicate.name", new Object[]{"", destinationFolderUri});
        }
        
        for (String uri : resourceMap.keySet()) {
            repositoryService.moveResource(null, uri, destinationFolderUri);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceDetails getResourceDetails(Resource resource) {
        ResourceDetails resourceDetails = new ResourceDetails(resource);

        if (securityChecker != null) {
//            resourceDetails.setReadable(securityChecker.isResourceReadable(resource.getURIString()));
            resourceDetails.setReadable(true);
            resourceDetails.setEditable(securityChecker.isEditable(resource));
            resourceDetails.setRemovable(securityChecker.isRemovable(resource));
            resourceDetails.setAdministrable(permissionService.isObjectAdministrable(null, resource));

            resourceDetails.setScheduled(schedulingChecker.isScheduled(null, resource));
        } else {
            if (log.isDebugEnabled()) { log.debug("Repository security checker is null!"); }
        }

        if (schedulingChecker != null) {
            resourceDetails.setScheduled(schedulingChecker.isScheduled(null, resource));
        } else {
            if (log.isDebugEnabled()) { log.debug("Scheduling checker is null!"); }
        }

        ChildrenLoaderService childrenLoaderService = childrenLoaders.get(resource.getResourceType());

        if (childrenLoaderService != null) {
            resourceDetails.setHasChildren(childrenLoaderService.hasChildren(resourceDetails.getURIString()));
        } else {
            resourceDetails.setHasChildren(false);
        }

        if (typeResolver != null) {
            resourceDetails.setResourceType(typeResolver.getResourceType(resource));
        }

        return resourceDetails;
    }

    public void setSecurityChecker(RepositorySecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }

    public void setDeleteOrder(List<String> deleteOrder) {
        this.deleteOrder = deleteOrder;
    }

    public void setPermissionService(ObjectPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setSchedulingChecker(SchedulingChecker schedulingChecker) {
        this.schedulingChecker = schedulingChecker;
    }

    public void setChildrenLoaders(Map<String, ChildrenLoaderService> childrenLoaders) {
        this.childrenLoaders = childrenLoaders;
    }

    public void setSearchCriteriaFactory(SearchCriteriaFactory searchCriteriaFactory) {
        this.searchCriteriaFactory = searchCriteriaFactory;
    }

    public void setMaxDependentReports(int maxDependentReports) {
        this.maxDependentReports = maxDependentReports;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setCheckDependentResourcesFor(List<String> checkDependentResourcesFor) {
        this.checkDependentResourcesFor = checkDependentResourcesFor;
    }

    public void setTempFolderName(String tempFolderName) {
        this.tempFolderName = tempFolderName;
    }

    public void setResourceTypeResolver(ResourceTypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    class DeleteOrderComparator implements Comparator<Resource> {
        public int compare(Resource o1, Resource o2) {
            int index1 = deleteOrder.indexOf(o1.getResourceType());
            int index2 = deleteOrder.indexOf(o2.getResourceType());
            return (index1 < index2 ? -1 : (index1 == index2 ? 0 : 1));
        }
    }
}
