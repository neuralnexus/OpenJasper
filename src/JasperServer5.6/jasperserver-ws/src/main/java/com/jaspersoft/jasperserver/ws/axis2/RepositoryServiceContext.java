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

package com.jaspersoft.jasperserver.ws.axis2;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import org.apache.axis.attachments.AttachmentPart;

import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryServiceContext.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface RepositoryServiceContext {

	RepositoryService getRepository();
	
	EngineService getEngine();
	
	ResourceHandlerRegistry getHandlerRegistry();
	
	String getMessage(String messageCode, Object[] args);

	ResourceDescriptor createResourceDescriptor(String referenceURI) throws WSException;
	
	ResourceDescriptor createResourceDescriptor(Resource resource) throws WSException;

	ResourceDescriptor createResourceDescriptor(Resource resource, Map options) throws WSException;

	AttachmentPart[] getMessageAttachments();

	RepositoryHelper getRepositoryHelper();
	
	/**
	 * Returns the service configuration object.
	 * 
	 * @return the service configuration object
	 */
	ManagementServiceConfiguration getServiceConfiguration();

    String runReport(String requestXmlString);

    String list(String requestXmlString);

    List listResources(String uri) throws WSException;

    String get(String requestXmlString);

    String put(String requestXmlString);

    String delete(String requestXmlString);

    JRExporter getExporter(String type, Map exportParameters);

    String getContentType(String type);

    String move(String requestXmlString);

    String copy(String requestXmlString);

    Locale getLocale();

    

}
