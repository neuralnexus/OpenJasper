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

import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.ws.axis2.*;
import com.jaspersoft.jasperserver.ws.axis2.util.ResourceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.UniversalValidationErrorFilter;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Request;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

/**
 * @author gtoffoli
 * @version $Id: FolderHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class FolderHandler implements ResourceHandler, ResourceResolverAdapter {

	private static final Log log = LogFactory.getLog(FolderHandler.class);
	
	protected static final String OBJECT_NAME = "actualFolder";
	
    private ResourceActionResolver resourceActionResolver;

    public void setResourceActionHandler(ResourceActionResolver resourceActionResolver) {
        this.resourceActionResolver = resourceActionResolver;
    }

	public Class getResourceType() {
		return Folder.class;
	}

	public ResourceDescriptor describe(Resource resource, Map arguments,
			RepositoryServiceContext serviceContext) {
		Folder folder = (Folder) resource;
		
		ResourceDescriptor descriptor = new ResourceDescriptor();
		descriptor.setWsType(ResourceDescriptor.TYPE_FOLDER);
		descriptor.setHasData(false);
		descriptor.setUriString(folder.getURIString());
		descriptor.setDescription(folder.getDescription());
		descriptor.setLabel(folder.getLabel());
		descriptor.setName(folder.getName());
		descriptor.setResourceType(folder.getResourceType());
		descriptor.setParentFolder(folder.getParentFolder());
		descriptor.setVersion(folder.getVersion());
		descriptor.setCreationDate(folder.getCreationDate());

		return descriptor;
	}

	public void put(ServiceRequest request) throws WSException {
		ResourceDescriptor descriptor = request.getRequestDescriptor();
		RepositoryService repository = request.getContext().getRepository();
        RepositoryServiceContext context = request.getContext();

		if (descriptor.getIsNew()) {
            Folder folder = new FolderImpl();
            folder.setName( descriptor.getName() );
            folder.setLabel( descriptor.getLabel() );
            folder.setDescription( descriptor.getDescription());
            folder.setParentFolder(descriptor.getParentFolder());
            folder.setVersion(Resource.VERSION_NEW);
            saveValidated(repository, folder, context);
            request.getResult().getResourceDescriptors().add(
            		request.getContext().createResourceDescriptor(folder.getURIString()));
		} else {
            Folder res = repository.getFolder(null, descriptor.getUriString());
            
            if (res != null)
            {
                if (res.getLabel() == null || !res.getLabel().equals( descriptor.getLabel()))
                {
                    res.setLabel( descriptor.getLabel() );
                }

                if (res.getDescription() == null ||  !res.getDescription().equals( descriptor.getDescription()))
                {
                    res.setDescription( descriptor.getDescription());
                }
                
                saveValidated(repository, res, context);
                request.getResult().addResourceDescriptor(
                		request.getContext().createResourceDescriptor(res));
            }
            else
            {
            	request.getResult().setReturnCode(2);
            	request.getResult().setMessage(request.getContext().getMessage("webservices.error.folderNotFound", null));
            }
		}
	}

	protected void saveValidated(RepositoryService repository, Folder folder, RepositoryServiceContext serviceContext)
            throws WSException {
		ValidationErrorFilter filter = folder.isNew() ? 
				UniversalValidationErrorFilter.getInstance() : null;
		ValidationErrors errors = repository.validateFolder(null, folder, 
				filter);
		if (errors.isError()) {
			throw new WSValidationException(OBJECT_NAME, errors);
		}
        if (!resourceActionResolver.canCreateResource(folder.getParentFolder())) {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.cannotCreateResource",
                            new String[]{folder.getParentFolder()}));
        }

		repository.saveFolder(null, folder);
	}

	public void getAttachments(Resource resource, Map arguments,
			ResourceDescriptor descriptor,
			ResultAttachments attachments, RepositoryServiceContext serviceContext) {
		// nothing
	}

	public void delete(ResourceDescriptor descriptor, RepositoryServiceContext serviceContext) throws WSException {
        if (resourceActionResolver.isResourceDeletable(descriptor.getUriString())) {
            serviceContext.getRepository().deleteFolder(null, descriptor.getUriString());
        } else {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.notDeletableResource", null));
        }
	}

	public void move(Request request, RepositoryServiceContext serviceContext) throws WSException {
		String sourceURI = request.getResourceDescriptor().getUriString();
		String destinationURI = getDestinationURI(request, serviceContext);
		
		if (log.isDebugEnabled()) {
			log.debug("Moving folder " + sourceURI + " to folder " + destinationURI);
		}

        if (!resourceActionResolver.canCreateResource(destinationURI)) {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.cannotCreateResource", new String[]{destinationURI}));
        }

		serviceContext.getRepository().moveFolder(null, sourceURI, destinationURI);
	}

	public ResourceDescriptor copy(Request request, RepositoryServiceContext serviceContext) throws WSException {
		String sourceURI = request.getResourceDescriptor().getUriString();
		String destinationURI = getDestinationURI(request, serviceContext);
		
		if (log.isDebugEnabled()) {
			log.debug("Copying folder " + sourceURI + " to URI " + destinationURI);
		}

        if (!resourceActionResolver.canCreateResource(ResourceUtils.getParentFolder(destinationURI))) {
            throw new WSException(WSException.GENERAL_ERROR,
                    serviceContext.getMessage("webservices.error.cannotCreateResource",
                            new String[]{ResourceUtils.getParentFolder(destinationURI)}));
        }

		Folder copy = serviceContext.getRepository().copyFolder(null, sourceURI, destinationURI);
		return describe(copy, null, serviceContext);
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
		throw new JSException("Listing folders with the " 
				+ Argument.LIST_RESOURCES + " argument is not supporteed");
	}

}
