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

package com.jaspersoft.jasperserver.ws.axis2.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.ws.axis2.*;
import com.jaspersoft.jasperserver.ws.axis2.util.ResourceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.UniversalValidationErrorFilter;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.OperationResult;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Request;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

/**
 * @author gtoffoli
 * @version $Id: RepositoryResourceHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class RepositoryResourceHandler implements ResourceHandler, ResourceResolverAdapter {

	protected static final String OBJECT_NAME = "resource";
	
	private static final Log log = LogFactory
			.getLog(RepositoryResourceHandler.class);

    private ResourceActionResolver resourceActionResolver;

    public void setResourceActionHandler(ResourceActionResolver resourceActionResolver) {
        this.resourceActionResolver = resourceActionResolver;
    }

    public ResourceDescriptor describe(Resource resource, Map arguments,
			RepositoryServiceContext serviceContext) throws WSException {
		ResourceDescriptor descriptor = new ResourceDescriptor();
		setCommonAttributes(resource, descriptor);

		doDescribe(resource, descriptor, arguments, serviceContext);
		return descriptor;
	}

	protected void setCommonAttributes(Resource resource,
			ResourceDescriptor descriptor) {
		descriptor.setUriString(resource.getURIString());
		descriptor.setDescription(resource.getDescription());
		descriptor.setLabel(resource.getLabel());
		descriptor.setName(resource.getName());
		descriptor.setResourceType(resource.getResourceType());
		descriptor.setParentFolder(resource.getParentFolder());
		descriptor.setVersion(resource.getVersion());
		descriptor.setCreationDate(resource.getCreationDate());
	}

	protected abstract void doDescribe(Resource resource,
			ResourceDescriptor descriptor, Map arguments,
			RepositoryServiceContext serviceContext) throws WSException;

	protected void saveValidated(RepositoryService repository, Resource resource,
            RepositoryServiceContext serviceContext) throws WSException {
		ValidationErrorFilter filter = resource.isNew() ? 
				UniversalValidationErrorFilter.getInstance() : null;
		ValidationErrors errors = repository.validateResource(null, resource, 
				filter);
		if (errors.isError()) {
			throw new WSValidationException(getObjectName(), errors);
		}
        if (!resourceActionResolver.canCreateResource(resource.getParentFolder())) {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.cannotCreateResource",
                            new String[]{resource.getParentFolder()}));
        }

		repository.saveResource(null, resource);
	}
	
	protected String getObjectName() {
		return OBJECT_NAME;
	}

	public void put(ServiceRequest request) throws WSException {
		ResourceDescriptor descriptor = request.getRequestDescriptor();
		RepositoryServiceContext context = request.getContext();
		RepositoryService repository = context.getRepository();
		OperationResult result = request.getResult();

		Resource parentResource = getModifyingParentResource(request);

		if (descriptor.getIsNew()) {
			if (parentResource == null) {
				Resource resource = toNewResource(descriptor, context);
				saveValidated(repository, resource, context);
				result.getResourceDescriptors().add(
						context.createResourceDescriptor(resource
								.getURIString()));
			} else {
				SubResourceHandler parentHandler = (SubResourceHandler) context
						.getHandlerRegistry().getHandler(parentResource);
				Resource subResource = parentHandler.setSubResource(parentResource,
						request);
				if (subResource != null) {
					saveValidated(repository, parentResource, context);
					result.getResourceDescriptors().add(
							context.createResourceDescriptor(subResource));
				}
			}
		} else {
			if (parentResource == null) {
				log.debug("Put: modifying " + descriptor.getWsType());

				Class resourceType = getResourceType();
				Resource resource = repository.getResource(null, descriptor
						.getUriString(), resourceType);

				if (resource == null) {
					result.setReturnCode(2);
					result.setMessage(context.getMessage(
							"webservices.error.resourceNotFound", null));
				} else {
					copyToResource(resource, descriptor, context);
					saveValidated(repository, resource, context);
					result.getResourceDescriptors().add(
							context.createResourceDescriptor(resource));
				}
			} else {
				// Report unit modification....
				SubResourceHandler parentHandler = (SubResourceHandler) context
						.getHandlerRegistry().getHandler(parentResource);
				Resource subResource = parentHandler.setSubResource(parentResource,
						request);
				if (subResource != null) {
					saveValidated(repository, parentResource, context);

					Map options = new HashMap();
					options.put(ReportUnitHandler.OPTION_REPORT_UNIT_CONTENTS,
							Boolean.TRUE);
					result.getResourceDescriptors().add(
							context.createResourceDescriptor(parentResource,
									options));
				}
			}
		}
	}

	protected Resource getModifyingParentResource(ServiceRequest request)
			throws WSException {
		Resource resource = null;
		// for now, only report units can act as parent resources
		String reportUnitUrl = request
				.getRequestArgument(Argument.MODIFY_REPORTUNIT);
		if (reportUnitUrl != null && reportUnitUrl.length() > 0) {
			log.debug("Put: adding/modifying resource in reportUnit "
					+ reportUnitUrl);
			ReportUnit reportUnit = (ReportUnit) request.getContext().getRepository()
					.getResource(null, reportUnitUrl);
			if (reportUnit == null) {
				throw new WSException(
						WSException.REFERENCED_RESOURCE_NOT_FOUND, request
								.getContext().getMessage(
										"webservices.error.reportUnitNotFound",
										null));
			}
			resource = reportUnit;
		}
		return resource;
	}

	protected Resource toNewResource(ResourceDescriptor descriptor,
			RepositoryServiceContext serviceContext) throws WSException {
		Class resourceType = getResourceType();
		Resource resource = serviceContext.getRepository().newResource(null,
				resourceType);
		resource.setParentFolder(descriptor.getParentFolder());
		resource.setVersion(Resource.VERSION_NEW);
		resource.setName(descriptor.getName());
		copyToResource(resource, descriptor, serviceContext);

		return resource;
	}

	public void copyToResource(Resource resource,
			ResourceDescriptor descriptor,
			RepositoryServiceContext serviceContext) throws WSException {
		resource.setLabel(descriptor.getLabel());
		resource.setDescription(descriptor.getDescription());

		updateResource(resource, descriptor, serviceContext);
	}

	protected abstract void updateResource(Resource resource,
			ResourceDescriptor descriptor,
			RepositoryServiceContext serviceContext) throws WSException;

	protected Resource toChildResource(ResourceDescriptor descriptor,
			RepositoryServiceContext context) throws WSException {
		String childType = descriptor.getWsType();
		RepositoryResourceHandler childHandler = (RepositoryResourceHandler) context
				.getHandlerRegistry().getHandler(childType);
		return childHandler.toNewResource(descriptor, context);
	}

	public void getAttachments(Resource resource, Map arguments,
			ResourceDescriptor descriptor,
			ResultAttachments attachments, RepositoryServiceContext serviceContext) {
		// default implementation does nothing
	}

	public void delete(ResourceDescriptor descriptor,
			RepositoryServiceContext serviceContext) throws WSException {
        if (resourceActionResolver.isResourceDeletable(descriptor.getUriString())) {
            serviceContext.getRepository().deleteResource(null, descriptor.getUriString());
        } else {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.notDeletableResource", null));
        }
	}

	public void move(Request request, RepositoryServiceContext serviceContext) throws WSException {
		String sourceURI = request.getResourceDescriptor().getUriString();
		String destinationURI = getDestinationURI(request, serviceContext);
		
		if (log.isDebugEnabled()) {
			log.debug("Moving resource " + sourceURI + " to folder " + destinationURI);
		}

        if (!resourceActionResolver.canCreateResource(destinationURI)) {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.cannotCreateResource", new String[]{destinationURI}));
        }

		serviceContext.getRepository().moveResource(null, sourceURI, destinationURI);
	}

	public ResourceDescriptor copy(Request request, RepositoryServiceContext serviceContext) throws WSException {
		String sourceURI = request.getResourceDescriptor().getUriString();
		String destinationURI = getDestinationURI(request, serviceContext);
		
		if (log.isDebugEnabled()) {
			log.debug("Copying resource " + sourceURI + " to URI " + destinationURI);
		}

        if (!resourceActionResolver.canCreateResource(ResourceUtils.getParentFolder(destinationURI))) {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.cannotCreateResource",
                            new String[]{ResourceUtils.getParentFolder(destinationURI)}));
        }

		// TODO multi resource copy?
		Resource copy = serviceContext.getRepository().copyResource(null, sourceURI, destinationURI);
		return describe(copy, getDefaultDescribeArguments(), serviceContext);
	}

	protected Map getDefaultDescribeArguments() {
		return null;
	}
	
	protected String getDestinationURI(Request request, 
			RepositoryServiceContext serviceContext) throws WSException {
		String destinationURI = request.getArgumentValue(Argument.DESTINATION_URI);
		if (destinationURI == null) {
			throw new WSException(WSException.GENERAL_REQUEST_ERROR,
					serviceContext.getMessage("webservices.error.request.no.destination.URI", null));
		}
		return destinationURI;
	}

	public List listResources(Request request, 
			RepositoryServiceContext serviceContext) throws WSException {
		Class resourceType = getResourceType();
		
		String parentFolder = request.getArgumentValue(Argument.PARENT_DIRECTORY);
    	String ancestorFolder = request.getArgumentValue(Argument.START_FROM_DIRECTORY);
		
    	if (log.isDebugEnabled()) {
			log.debug("Listing resources of type " + resourceType 
					+ (parentFolder != null 
							? ("from folder " + parentFolder) 
							: (ancestorFolder != null 
									? (" starting from folder " + ancestorFolder) 
									: "")));
    	}
    	
    	FilterCriteria filter = FilterCriteria.createFilter(resourceType);
    	
    	if (parentFolder != null) {
    		filter.addFilterElement(FilterCriteria.createParentFolderFilter(parentFolder));
    	} else  if (ancestorFolder != null 
    			// do not set ancestor folder filter if root
    			&& !Folder.SEPARATOR.equals(ancestorFolder)) {
    		filter.addFilterElement(FilterCriteria.createAncestorFolderFilter(ancestorFolder));
    	}
    	
    	FilterElement additionalCriteria = additionalListResourcesFilterCriteria(request);
    	if (additionalCriteria != null) {
    		filter.addFilterElement(additionalCriteria);
    	}
    	
    	List resources = serviceContext.getRepository().loadClientResources(filter);
    	List descriptors;
    	if (resources == null || resources.isEmpty()) {
    		descriptors = new ArrayList(0);
    	} else {
    		descriptors = new ArrayList(resources.size());
    		for (Iterator it = resources.iterator(); it.hasNext();) {
				Resource resource = (Resource) it.next();
				descriptors.add(serviceContext.createResourceDescriptor(resource));
			}
    	}
    	return descriptors;
	}

	protected FilterElement additionalListResourcesFilterCriteria(Request request) {
		return null;
	}
}
