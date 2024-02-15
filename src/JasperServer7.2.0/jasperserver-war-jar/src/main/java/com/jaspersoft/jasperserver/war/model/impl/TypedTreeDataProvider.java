/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.war.model.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElementDisjunction;
import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * @author Alexander Povshik
 * @author Andriy Godovanets
 */
public class TypedTreeDataProvider extends BaseTreeDataProvider {
    public static final Logger log = Logger.getLogger(TypedTreeDataProvider.class);
    private List<String> supportedClasses;
    private List<String> fileTypes;
    private TreeDataFilter filter;
    private List<String> supportedCustomReportDataSourceServices;
    private final String customReportDataSourceClass = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource";
    private EngineService engineService;
    private boolean lazy = false;

    public TreeDataFilter getFilter() {
        return filter;
    }

    public void setFilter(TreeDataFilter filter) {
        this.filter = filter;
    }

    /**
     * The method creates the TreeNode hierarchy started from the root.
     * The hierarchy will only have the requested subtree, filled to the requested depth.
     * @param executionContext
     * @return
     */
    @Override
    protected TreeNode createRoot(ExecutionContext executionContext) {
        if (lazy) {
            return createRootLazy(executionContext);
        } else {
            return createRootAggressive(executionContext);
        }
    }

    /**
     * Implementation which loads all resources at once
     * @param executionContext
     * @return
     */
    protected TreeNode createRootAggressive(ExecutionContext executionContext) {
      List<ResourceLookup> lookups = new ArrayList<ResourceLookup>();
      for (String clazz : supportedClasses) {
        try {
          FilterCriteria criteria = FilterCriteria.createFilter(Class.forName(clazz));
          // File types are applicable only for FileResource
          if (FileResource.class.getName().equals(clazz) && fileTypes != null && fileTypes.size() > 0) {
            if (fileTypes.size() == 1) {
              criteria
                  .addFilterElement(
                      FilterCriteria.createPropertyEqualsFilter("fileType", fileTypes.get(0)));
            } else {
              FilterElementDisjunction filterElementDisjunction = criteria.addDisjunction();
              for (String fileType : fileTypes) {
                filterElementDisjunction.addFilterElement(FilterCriteria.createPropertyEqualsFilter("fileType", fileType));
              }
            }
          }
          // fetch
          ResourceLookup lookupsArray[] = getRepositoryService().findResource(executionContext, criteria);
          if (lookupsArray != null) {
            // not all custom data sources are supported in all cases
            // need to determine by the custom report data source service
            if ((supportedCustomReportDataSourceServices != null) && (supportedCustomReportDataSourceServices.size() > 0) &&  (supportedClasses.contains(customReportDataSourceClass))) {
              for (ResourceLookup resourceLookup : lookupsArray) {
                if (resourceLookup.getResourceType().equals(customReportDataSourceClass)) {
                  CustomReportDataSource customReportDataSource = (CustomReportDataSource) getRepositoryService().getResource(executionContext, resourceLookup.getURI());
                  if (supportedCustomReportDataSourceServices.contains(customReportDataSource.getServiceClass()) ||
                      isCustomDomainMetadataProvider(customReportDataSource)) {
                    lookups.add(resourceLookup);
                  }
                } else {
                  lookups.add(resourceLookup);
                }
              }
            } else {
              lookups.addAll(Arrays.asList(lookupsArray));
            }
          }
        } catch (ClassNotFoundException e) {
          log.warn("TypedTreeDateProvider supported class not found: ", e);
        }
      }

      Folder rootFolder = getRepositoryService().getFolder(executionContext, "/");
      TreeNode root = new TreeNodeImpl(this, rootFolder.getName(), rootFolder.getLabel(), rootFolder.getResourceType(), rootFolder.getURIString(), 1);

      for (ResourceLookup lookup : lookups) {
        TreeNode parent = getParentFolderNode(root, lookup.getURIString());
        parent.getChildren().add(
            new TreeNodeImpl(this, lookup.getName(), lookup.getLabel(), lookup.getResourceType(), lookup.getURIString()));
      }
      return filterTree(root);
    }

  /**
   * Implementation which loads resources lazily, processing only requested level at requested depth
   * @param executionContext
   * @return
   */
    protected TreeNode createRootLazy(ExecutionContext executionContext) {
        /**
         * Passing parameters via executionContext is not the best way to pass parameters to a method..
         * It is done to preserve the method signature for the old time interface which is widely used,
         * and might be a point of a customization.
         */
        Parameters parameters = getParameters(executionContext);
        if (parameters == null) {
            parameters = new Parameters("/", 1);
        }
        List<ResourceLookup> lookups = new ArrayList<ResourceLookup>();
        for (String clazz : supportedClasses) {
            try {

                lookups.addAll( fetchAndFilter(executionContext, clazz, parameters) );

            } catch (ClassNotFoundException e) {
                log.warn("TypedTreeDateProvider supported class not found: ", e);
            }
        }

        Folder rootFolder = getRepositoryService().getFolder(executionContext, "/");
        TreeNode root = new TreeNodeImpl(this, rootFolder.getName(), rootFolder.getLabel(), rootFolder.getResourceType(), rootFolder.getURIString(), 1);

        for (ResourceLookup lookup : lookups) {
            TreeNode parent = getParentFolderNode(root, lookup.getURIString());
            parent.getChildren().add(
                    new TreeNodeImpl(this, lookup.getName(), lookup.getLabel(), lookup.getResourceType(), lookup.getURIString()));
        }

        // add folders
        List<Folder> folders = getRepositoryService().getSubFolders(null, parameters.uri);
        if (folders != null) {
            for (Folder folder : folders) {
                // add folder if does not exist
                findOrBuildFolderNode(root, folder.getURIString());
            }
        }

        return filterTree(root);
    }

    private List fetchAndFilter(ExecutionContext executionContext, String clazz, Parameters parameters) throws ClassNotFoundException {
        List<ResourceLookup> lookups = new ArrayList<ResourceLookup>();

        FilterCriteria criteria = FilterCriteria.createFilter(Class.forName(clazz));
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parameters.uri));
        // File types are applicable only for FileResource
        if (FileResource.class.getName().equals(clazz) && fileTypes != null && fileTypes.size() > 0) {
            if (fileTypes.size() == 1) {
                criteria
                    .addFilterElement(
                        FilterCriteria.createPropertyEqualsFilter("fileType", fileTypes.get(0)));
            } else {
                FilterElementDisjunction filterElementDisjunction = criteria.addDisjunction();
                for (String fileType : fileTypes) {
                    filterElementDisjunction.addFilterElement(FilterCriteria.createPropertyEqualsFilter("fileType", fileType));
                }
            }
        }
        // fetch resources
        ResourceLookup lookupsArray[] = getRepositoryService().findResource(executionContext, criteria);
        if (lookupsArray != null) {
            // not all custom data sources are supported in all cases
            // need to determine by the custom report data source service
            if ((supportedCustomReportDataSourceServices != null) && (supportedCustomReportDataSourceServices.size() > 0) &&  (supportedClasses.contains(customReportDataSourceClass))) {
                for (ResourceLookup resourceLookup : lookupsArray) {
                    if (resourceLookup.getResourceType().equals(customReportDataSourceClass)) {
                        CustomReportDataSource customReportDataSource = (CustomReportDataSource) getRepositoryService().getResource(executionContext, resourceLookup.getURI());
                        if (supportedCustomReportDataSourceServices.contains(customReportDataSource.getServiceClass()) ||
                            isCustomDomainMetadataProvider(customReportDataSource)) {
                            lookups.add(resourceLookup);
                        }
                    } else {
                        lookups.add(resourceLookup);
                    }
                }
            } else {
                lookups.addAll(Arrays.asList(lookupsArray));
            }
        }

        if (parameters.depth > 1) {
            List<Folder> folders = getRepositoryService().getSubFolders(executionContext, parameters.uri);
            for (Folder folder : folders) {
                Parameters newParams = new Parameters(folder.getURI(), parameters.depth - 1);
                lookups.addAll( fetchAndFilter(executionContext, clazz, newParams) );
            }
        }

        return lookups;
    }

    /**
     * Checks if the datasource provides metadata.
     */
    private boolean isCustomDomainMetadataProvider(CustomReportDataSource customReportDataSource) {
        if (engineService == null) {
            return false;
        }
        try {
            return engineService.isCustomDomainMetadataProvider(customReportDataSource);
        } catch (Exception ex) {
            log.error(ex);
            return false;
        }
    }

    private TreeNode filterTree(TreeNode tree) {
        if(filter == null) {
            return tree;
        }
        List<TreeNode> children = tree.getChildren();
        if(children == null) {
            return tree;
        }
        Iterator<TreeNode> i = children.iterator();
        while(i.hasNext()) {
            TreeNode child = i.next();
            if(!filter.filter(child)) {
                i.remove();
            } else {
                filterTree(child);
            }
        }
        return tree;
    }

    public void setSupportedClasses(List<String> supportedClasses) {
        this.supportedClasses = supportedClasses;
    }

    public void setSupportedCustomReportDataSourceServices(List<String> supportedCustomReportDataSourceServices) {
        this.supportedCustomReportDataSourceServices = supportedCustomReportDataSourceServices;
    }

    public void setEngineService(EngineService engineService) {
        this.engineService = engineService;
    }

    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
}