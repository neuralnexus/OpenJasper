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

import java.io.IOException;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Request;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;
import com.jaspersoft.jasperserver.ws.axis2.ResourceDataSource;
import com.jaspersoft.jasperserver.ws.axis2.ResultAttachments;
import com.jaspersoft.jasperserver.ws.axis2.WSException;

/**
 * @author gtoffoli
 * @version $Id: FileResourceHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class FileResourceHandler extends RepositoryResourceHandler {

	private static final Log log = LogFactory.getLog(FileResourceHandler.class);
	
	public Class getResourceType() {
		return FileResource.class;
	}

	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext) {
		FileResource fileResource = (FileResource) resource;
		descriptor.setWsType(fileResource.getFileType());
		descriptor.setHasData(fileResource.hasData());
		descriptor.setIsReference(fileResource.isReference());
		if (descriptor.getIsReference()) {
			descriptor.setReferenceUri(fileResource.getReferenceURI());
		}

		String resourceType = fileResource.getFileType();

		if (descriptor.getIsReference()) {
			descriptor.setWsType(ResourceDescriptor.TYPE_REFERENCE);
		} else if (resourceType == null)
			descriptor.setWsType(ResourceDescriptor.TYPE_UNKNOW);
		else if (resourceType.equals(FileResource.TYPE_JRXML))
			descriptor.setWsType(ResourceDescriptor.TYPE_JRXML);
		else if (resourceType.equals(FileResource.TYPE_IMAGE))
			descriptor.setWsType(ResourceDescriptor.TYPE_IMAGE);
		else if (resourceType.equals(FileResource.TYPE_FONT))
			descriptor.setWsType(ResourceDescriptor.TYPE_FONT);
		else if (resourceType.equals(FileResource.TYPE_JAR))
			descriptor.setWsType(ResourceDescriptor.TYPE_CLASS_JAR);
		else if (resourceType.equals(FileResource.TYPE_RESOURCE_BUNDLE))
			descriptor.setWsType(ResourceDescriptor.TYPE_RESOURCE_BUNDLE);
		else if (resourceType.equals(FileResource.TYPE_STYLE_TEMPLATE))
			descriptor.setWsType(ResourceDescriptor.TYPE_STYLE_TEMPLATE);
		else if (resourceType.equals(FileResource.TYPE_XML))
			descriptor.setWsType(ResourceDescriptor.TYPE_XML_FILE);
	}

	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor, RepositoryServiceContext serviceContext) throws WSException {
		FileResource fileResource = (FileResource) resource;

		String wsType = descriptor.getWsType();

		if (wsType.equals(ResourceDescriptor.TYPE_REFERENCE)
				|| descriptor.getIsReference()) {
			// check if the reference uri is valid...
			String referenceUri = descriptor.getReferenceUri();

			try {
				serviceContext.getRepository().getResource(null,
						referenceUri);
			} catch (Exception ex) {
				throw new WSException(WSException.GENERAL_ERROR2,
						serviceContext.getMessage(
								"webservices.error.resourceNotFoundOrInvalid",
								new Object[] { referenceUri }));
			}

			fileResource.setReferenceURI(referenceUri);
		} else {
			String fileType = getFileType(wsType);
			if (fileType != null) {
				fileResource.setFileType(fileType);
			}

			if (descriptor.getHasData()) {
				try {
					// Save the temporary file....
					AttachmentPart[] parts = serviceContext
							.getMessageAttachments();
					if (parts.length >= 1) {
						DataHandler actualDH = parts[0].getDataHandler();

						// Save the content in the file...
						fileResource.readData(actualDH.getInputStream());
					}
				} catch (SOAPException e) {
					throw new WSException(e);
				} catch (IOException e) {
					throw new WSException(e);
				}
			}
		}
	}

	protected String getFileType(String wsType) {
		String type = null;
		if (wsType.equals(ResourceDescriptor.TYPE_IMAGE))
			type = FileResource.TYPE_IMAGE;
		else if (wsType.equals(ResourceDescriptor.TYPE_FONT))
			type = FileResource.TYPE_FONT;
		else if (wsType.equals(ResourceDescriptor.TYPE_CLASS_JAR))
			type = FileResource.TYPE_JAR;
		else if (wsType.equals(ResourceDescriptor.TYPE_JRXML))
			type = FileResource.TYPE_JRXML;
		else if (wsType.equals(ResourceDescriptor.TYPE_RESOURCE_BUNDLE))
			type = FileResource.TYPE_RESOURCE_BUNDLE;
		else if (wsType.equals(ResourceDescriptor.TYPE_STYLE_TEMPLATE))
			type = FileResource.TYPE_STYLE_TEMPLATE;
		else if (wsType.equals(ResourceDescriptor.TYPE_XML_FILE))
			type = FileResource.TYPE_XML;
		return type;
	}

	public void getAttachments(Resource resource, Map arguments,
			ResourceDescriptor descriptor,
			ResultAttachments attachments, RepositoryServiceContext serviceContext) {
		super.getAttachments(resource, arguments, descriptor, attachments, serviceContext);
		
        if (arguments.containsKey(Argument.NO_RESOURCE_DATA_ATTACHMENT))
        {
            descriptor.setHasData(false);
        }
        else
        {
            //Retrieve resource data...
        	descriptor.setHasData(true);
        	FileResourceData fileData = serviceContext.getRepository().getResourceData(null, descriptor.getUriString());
        	ResourceDataSource attachmentSource = new ResourceDataSource("", fileData);
        	attachments.addAttachment("attachment", attachmentSource);
        }
	}

	protected FilterElement additionalListResourcesFilterCriteria(Request request) {
		String wsType = request.getArgumentValue(Argument.RESOURCE_TYPE);
		String fileType = getFileType(wsType);
		if (fileType == null) {
			if (log.isDebugEnabled()) {
				log.debug("Not able to determine file type for WS type " + wsType
						+ ", listing all file resources");
			}
			return null;
		}
		
		return FilterCriteria.createPropertyEqualsFilter("fileType", fileType);
	}
	
}
