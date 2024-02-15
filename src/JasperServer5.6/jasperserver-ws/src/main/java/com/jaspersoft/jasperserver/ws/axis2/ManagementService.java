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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PdfExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequest;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.*;
import com.jaspersoft.jasperserver.ws.axis2.WSExporter;
import com.jaspersoft.jasperserver.ws.axis2.repository.ReportUnitHandler;
import com.jaspersoft.jasperserver.ws.axis2.repository.SubResourceHandler;
import com.jaspersoft.jasperserver.ws.xml.ByteArrayDataSource;
import com.jaspersoft.jasperserver.ws.xml.Marshaller;
import com.jaspersoft.jasperserver.ws.xml.Unmarshaller;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.*;
import java.util.*;


public class ManagementService extends ServletEndpointSupport implements RepositoryServiceContext {
    protected static final String BEAN_NAME_MANAGEMENT_SERVICE_IMPL_NAME = "managementServiceImpl";

    private RepositoryServiceContext managementService;

    protected void onInit() {
        managementService = getManagementServiceImpl();
        managementService.getRepositoryHelper();
    }

    public String list(String requestXmlString) {
        return managementService.list(requestXmlString);
    }

    /**
     * Return a list of ResourceDescriptor(s)
     * @throws WSException
     */
    public List listResources(String uri) throws WSException {
        return managementService.listResources(uri);
    }

    /**
         * This method get a resource identified by an URI.
         * If the resource is a FileResource, the data is attached to the response
         * as attachment.
         * Only one attachment is permitted by now.
         *
         * WS Input:
         *  A resourceDescriptor in XML format. Only the uri is really used.
         *
         * WS output:
         *  The WS returns the complete descriptor for the resource (incapsulated in
         *  an OperationResult).
         *  The resource data can be in attachment. In this case the resourceDescriptor
         *  has the attrobute HasData set to true.
         *
         * To get a control filled with the query data, call this WS with the argument
         * IC_GET_QUERY_DATA set to the datasource URI used to get the data.
         *
         * Operation result Codes:
         * 0 - Success
         * 1 - Generic error
         * 2 - Resource not found
         *
         */
	public String get(String requestXmlString) {
        return managementService.get(requestXmlString);
    }

     /**
     * This method uses the repo.getResource(null, uri) to get
     * a reference to the resource, and then uses createResourceDescriptor to
     * return the descriptor.
     * In the resource is a ReportUnit, all resources of this ReportUnit
     * will be present as well.
     *
     * If res is not found, the method returns null.
     * @throws WSException
     */
    public ResourceDescriptor createResourceDescriptor(String uri) throws WSException {
        return managementService.createResourceDescriptor(uri);
    }

    /**
     * the same as createResourceDescriptor( resource, false)
     * @throws WSException
     */
    public ResourceDescriptor createResourceDescriptor( Resource resource) throws WSException {
        return managementService.createResourceDescriptor(resource);
    }

    /**
     * Create a ResourceDescriptor from a Resource.
     * The real type of this resource is saved in WsType
     * @throws WSException
     *
     */
    public ResourceDescriptor createResourceDescriptor( Resource resource, Map specialOptions) throws WSException {
       return managementService.createResourceDescriptor(resource, specialOptions);
    }

    /**
         * This method put a resource identified by an URI.
         * If the resource is a FileResource, the data should be attached to the response
         * as attachment (if hasData is set to true).
         * Only one attachment is permitted by now.
         *
         * If the resource is new, the isNew flag must be set.
         * otherwise the put is trated as a modify.
         * To modify a resource that belong to REPORT_UNIT, you need to specify the argument
         * Argument.MODIFY_REPORTUNIT with the URI of the reportUnit parent.
         * If the resource is not local to the reportUnit, this URI is ignored but still
         * mandatory.
         *
         * WS Input:
         *  A resourceDescriptor in XML format.
         *  The modify affect by now only label, description and file data if present.
         *
         * WS output:
         *  The WS returns the complete new descriptor for the resource (incapsulated in
         *  an OperationResult).
         *  No data is attachment.
         *
         * Operation result Codes:
         * 0 - Success
         * 1 - Generic error
         * 2 - Resource not found (in case of a modify)
         *
         */
	public String put(String requestXmlString) {
        return managementService.put(requestXmlString);
    }

    /**
         * This method delete a resource identified by an URI.
         *
         * To delete a resource that belongs to a REPORT_UNIT, you need to specify the argument
         * Argument.MODIFY_REPORTUNIT with the URI of the reportUnit parent.
         * If the resource is not local to the reportUnit, this URI is ignored but still
         * mandatory.
         * You can not delete a MainReport
         *
         *
         * Operation result Codes:
         * 0 - Success
         * 1 - Generic error
         *
         */
	public String delete(String requestXmlString) {
        return managementService.delete(requestXmlString);
    }

    /**
         * This method run a report. The return is an OperationResult.
         * If the result is successful, the message contains a set of strings
         * (one for each row) with the list of files attached complete of the
         * relative path. I.e.
         *
         * main_report.html
         * images/logo1.jpg
         * images/chartxyz.jpg
         *
         * Arguments:
         *
         *
         *
         * The request must contains the descriptor of the report to execute
         * (only the URI is used).
         * Arguments can be attached to the descriptor as children. Each argument
         * is a ListItem, with the parameter name as Name and the object
         * representing the value as Value.
         *
         * Operation result Codes:
         * 0 - Success
         * 1 - Generic error
         *
         */
	public String runReport(String requestXmlString)  {
        return managementService.runReport(requestXmlString);
    }

    public JRExporter getExporter(String type, Map exportParameters) {
        return managementService.getExporter(type, exportParameters);
    }

    public String getContentType(String type) {
        return managementService.getContentType(type);
    }

    /**
     * Function to get attachments from an Axis message
     *
     */
    public AttachmentPart[] getMessageAttachments() {
        return managementService.getMessageAttachments();
    }

    public String move(String requestXmlString) {
        return managementService.move(requestXmlString);
    }

    public String copy(String requestXmlString) {
        return managementService.copy(requestXmlString);
    }

    public Locale getLocale() {
        return managementService.getLocale();
    }

    public String getMessage(String messageCode, Object[] args) {
		return managementService.getMessage(messageCode, args);
	}

	public RepositoryService getRepository() {
		return managementService.getRepository();
	}

	public EngineService getEngine() {
		return managementService.getEngine();
	}

	public ResourceHandlerRegistry getHandlerRegistry() {
		return managementService.getHandlerRegistry();
	}

	public RepositoryHelper getRepositoryHelper() {
		return managementService.getRepositoryHelper();
	}

	public ManagementServiceConfiguration getServiceConfiguration() {
		return managementService.getServiceConfiguration();
	}

    protected RepositoryServiceContext getManagementServiceImpl() {
        return (RepositoryServiceContext) getApplicationContext().getBean(BEAN_NAME_MANAGEMENT_SERVICE_IMPL_NAME);
    }
}
