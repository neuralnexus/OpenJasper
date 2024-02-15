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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;
import com.jaspersoft.jasperserver.ws.axis2.ResourceDataSource;
import com.jaspersoft.jasperserver.ws.axis2.ResultAttachments;
import com.jaspersoft.jasperserver.ws.axis2.WSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ContentResourceHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ContentResourceHandler extends RepositoryResourceHandler {
	
	private static final Log log = LogFactory.getLog(ContentResourceHandler.class);
	
	private static final String MAIN_ATTACHMENT_ID = "attachment";

	public Class getResourceType() {
		return ContentResource.class;
	}

	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext)
			throws WSException {
		ContentResource contentRes = (ContentResource) resource;
		descriptor.setWsType(ResourceDescriptor.TYPE_CONTENT_RESOURCE);
		descriptor.setResourceProperty(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE, 
				contentRes.getFileType());
	}

	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor,
			RepositoryServiceContext serviceContext) throws WSException {
		ContentResource contentRes = (ContentResource) resource;
		
		String fileType = 
			descriptor.getResourcePropertyValue(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE);
		if (fileType != null) {
			contentRes.setFileType(fileType);
		}
		
		if (descriptor.getHasData()) {
			try {
				AttachmentPart attachment = locateDataAttachment(descriptor, serviceContext);
				readData(contentRes, attachment);
			} catch (SOAPException e) {
				throw new WSException(e);
			}
		}
		
		List children = descriptor.getChildren();
		List subresources = new ArrayList(children == null ? 0 : children.size());
		if (children != null)
		{
			for (Iterator it = children.iterator(); it.hasNext();) {
				ResourceDescriptor subdescriptor = (ResourceDescriptor) it.next();
				ContentResource subresource = (ContentResource) toChildResource(subdescriptor, serviceContext);
				subresources.add(subresource);
			}
		}
		contentRes.setResources(subresources);
	}

	protected AttachmentPart locateDataAttachment(ResourceDescriptor resourceDescriptor,
			RepositoryServiceContext serviceContext) throws WSException, SOAPException {
		String contentName = resourceDescriptor.getResourcePropertyValue(
				ResourceDescriptor.PROP_DATA_ATTACHMENT_ID);
		if (contentName == null) {
			contentName = resourceDescriptor.getName();
		}
		
		AttachmentPart[] attachments = serviceContext.getMessageAttachments();
		AttachmentPart attachment = null;
		if (attachments != null) {
			for (int i = 0; i < attachments.length; i++) {
				String attachmentId = attachments[i].getContentId();
				if (attachmentId != null && attachmentId.equals(contentName)) {
					attachment = attachments[i];
					break;
				}
			}
		}
		
		if (attachment == null) {
			throw new WSException(WSException.GENERAL_ERROR2,
					serviceContext.getMessage("webservices.error.content.resource.data.not.present", 
							new Object[]{contentName}));
		}
		
		return attachment;
	}

	protected String getAttachmentContentID(ContentResource contentRes) {
		return contentRes.getName();
	}

	protected void readData(ContentResource resource, AttachmentPart attachment) throws WSException, SOAPException {
		boolean close = true;
		InputStream dataStream = null;
		try {
			dataStream = attachment.getDataHandler().getInputStream();
			resource.readData(dataStream);
			
			close = false;
			dataStream.close();
		} catch (IOException e) {
			throw new WSException(e);
		} finally {
			if (close && dataStream != null) {
				try {
					dataStream.close();
				} catch (IOException e) {
					log.error("Error closing attachment stream", e);
				}
			}
		}
	}
	
	public void getAttachments(Resource resource, Map arguments,
			ResourceDescriptor descriptor, ResultAttachments attachments,
			RepositoryServiceContext serviceContext) {
		super.getAttachments(resource, arguments, descriptor, attachments,
				serviceContext);

		ContentResource contentRes = (ContentResource) resource;

		if (arguments.containsKey(Argument.NO_RESOURCE_DATA_ATTACHMENT)) {
			descriptor.setHasData(false);
		} else {
			// Retrieve resource data...
			descriptor.setHasData(true);

			addMainAttachment(descriptor, attachments, serviceContext);

			if (!arguments.containsKey(Argument.NO_SUBRESOURCE_DATA_ATTACHMENTS)) {
				addSubAttachments(contentRes, attachments, serviceContext);
			}
		}
	}

	protected void addMainAttachment(ResourceDescriptor descriptor,
			ResultAttachments attachments,
			RepositoryServiceContext serviceContext) {
		FileResourceData fileData = serviceContext.getRepository()
				.getContentResourceData(null, descriptor.getUriString());
		ResourceDataSource attachmentSource = new ResourceDataSource("",
				fileData);
		attachments.addAttachment(MAIN_ATTACHMENT_ID, attachmentSource);
		descriptor.setResourceProperty(ResourceDescriptor.PROP_DATA_ATTACHMENT_ID, MAIN_ATTACHMENT_ID);
	}

	protected void addSubAttachments(ContentResource contentRes,
			ResultAttachments attachments,
			RepositoryServiceContext serviceContext) {
		if (contentRes.getFileType().equals(ContentResource.TYPE_HTML)) {
			List resources = contentRes.getResources();
			if (resources != null && !resources.isEmpty()) {
				for (Iterator it = resources.iterator(); it.hasNext();) {
					ContentResource subResource = (ContentResource) it.next();
					FileResourceData subData = serviceContext.getRepository()
							.getContentResourceData(null,
									subResource.getURIString());
					ResourceDataSource subAttachment = new ResourceDataSource(
							"", subData);
					attachments.addAttachment(subResource.getName(),
							subAttachment);
				}
			}
		}
	}

}
