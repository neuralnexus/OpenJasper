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

/**
 * @author Fedir Sajbert
 */

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequest;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider.SnapshotSaveStatus;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportLoadingService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.HtmlExportUtil;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.*;
import com.jaspersoft.jasperserver.ws.axis2.repository.ReportUnitHandler;
import com.jaspersoft.jasperserver.ws.axis2.repository.SubResourceHandler;
import com.jaspersoft.jasperserver.ws.xml.ByteArrayDataSource;
import com.jaspersoft.jasperserver.ws.xml.Marshaller;
import com.jaspersoft.jasperserver.ws.xml.Unmarshaller;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.validation.MessageCodesResolver;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.*;
import java.util.*;


public class ManagementServiceImpl implements RepositoryServiceContext, BeanFactoryAware {

    protected static final Log log = LogFactory.getLog(ManagementServiceImpl.class);
    public static final String JAVA_IO_TMPDIR_PLACEHOLDER = "{java.io.tmpdir}";
    protected static final String START_TOKEN = "img src=\"images/";
    protected static final String END_TOKEN = "\"";
    protected static final String REPORT_UNIT = "ReportUnit";

    protected static final String WS_VERSION = "2.0.1";

    protected static final String BEAN_NAME_REPOSITORY_SERVICE = "repositoryService";
    protected static final String BEAN_NAME_ENGINE_SERVICE = "engineService";
    protected static final String BEAN_NAME_RUN_REPORT_ENGINE_SERVICE = "runReportEngineService";
    protected static final String BEAN_NAME_HANDLER_REGISTRY = "resourceHandlerRegistry";
    protected static final String BEAN_NAME_MESSAGE_CODES_RESOLVER = "wsMessageCodesResolver";
    protected static final String BEAN_NAME_SERVICE_CONFIGURATION = "managementServiceConfiguration";
    protected static final String BEAN_NAME_AUDIT_CONTEXT = "auditContext";
    public static final String SPRING_PROPERTIES_BEAN_NAME = "springConfiguration";
    protected static final String BEAN_NAME_REPOSITORY_SERVICE_PROPERTY_NAME = "bean.repositoryService";
    protected static final String BEAN_NAME_AUDIT_CONTEXT_PROPERTY_NAME = "bean.auditContext";

    private static final String ADHOC_TOPICS = "/adhoc/topics";

    private Locale locale = null; // Default locale....

    private RepositoryService repository;
    private EngineService engine;
    private EngineService runReportEngine;
    private RepositoryHelper repositoryHelper;
    private ResourceHandlerRegistry handlerRegistry;
    private ManagementServiceConfiguration serviceConfiguration;
    private AuditContext auditContext;
    private JasperReportsContext jasperReportsContext;
    private String attachmentsTempFolder;

    @javax.annotation.Resource(name = "messageSource")
    private MessageSource messageSource;

    @javax.annotation.Resource(name = "reportLoadingService")
    private ReportLoadingService reportLoadingService;

    @javax.annotation.Resource(name = "dataSnapshotService")
    private DataSnapshotService dataSnapshotService;

    @javax.annotation.Resource(name = "engineServiceDataCacheProvider")
    private DataCacheProvider dataCacheProvider;

    private BeanFactory beanFactory;

    public void setAttachmentsTempFolder(String attachmentsTempFolder) {
        this.attachmentsTempFolder = attachmentsTempFolder;
    }

    protected void createAuditEvent(final String auditEventType) {
        auditContext.doInAuditContext(
                new AuditContext.AuditContextCallback() {
                    public void execute() {
                        auditContext.createAuditEvent(auditEventType);
                    }
                });
    }

    protected void addPropertyToAuditEvent(
            final String auditEventType, final String propertyName, final Object property) {
        auditContext.doInAuditContext(auditEventType,
                new AuditContext.AuditContextCallbackWithEvent() {
                    public void execute(AuditEvent auditEvent) {
                        auditContext.addPropertyToAuditEvent(propertyName, property, auditEvent);
                    }
                });
    }

    protected void addExceptionToAllAuditEvents(
            final Throwable exception) {
        auditContext.doInAuditContext(
                new String[] {"saveResource", "updateResource", "deleteResource", "copyResource", "moveResource",
                        "createFolder", "updateFolder", "deleteFolder", "copyFolder", "moveFolder",
                        "runReport"},
                new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent("exception", exception, auditEvent);
            }
        });
    }

    private static String marshalResponse( OperationResult or)
    {
        return marshalResponse( or, new HashMap(), false);
    }

    protected static String marshalResponse(OperationResult or, ResultAttachments attachments)
    {
        return marshalResponse(or, attachments.getAttachmentsData(), attachments.isEncapsulationDime());
    }

    private static String marshalResponse( OperationResult or, Map datasources, boolean isEncapsulationDime)
    {

        String result = "";

        // First of all attach the attachments...
        if (datasources != null)
        {
            MessageContext msgContext= MessageContext.getCurrentContext();
            Message responseMessage = msgContext.getResponseMessage();

            log.debug("Encapsulation DIME? : " + isEncapsulationDime);

            if (isEncapsulationDime)
            {
                responseMessage.getAttachmentsImpl().setSendType(org.apache.axis.attachments.Attachments.SEND_TYPE_DIME);
            }

            for (Iterator it = datasources.entrySet().iterator(); it.hasNext(); )
            {
                 try {
                     Map.Entry entry = (Map.Entry) it.next();
                     String name = (String) entry.getKey();
                     DataSource datasource = (DataSource) entry.getValue();

                     log.debug("Adding attachment: " + name + ", type: " + datasource.getContentType());

                     DataHandler expectedDH = new DataHandler( datasource );

                     javax.xml.soap.AttachmentPart attachPart = null;
                     attachPart = responseMessage.createAttachmentPart(expectedDH);

                     //javax.xml.soap.AttachmentPart ap2 = responseMessage.createAttachmentPart();
                     //ap2.setContent( datasource.getInputStream(),  datasource.getContentType());
                     attachPart.setContentId(name);
                     responseMessage.addAttachmentPart(attachPart);


                } catch (Exception ex)
                {
                    log.error("caught exception marshalling an OperationResult: " + ex.getMessage(), ex);
                    // What to do?
                    or.setReturnCode(1);
                    or.setMessage("Error attaching a resource to the SOAP message: " + ex.getMessage());
                }
            }

        }

        try {
            StringWriter xmlStringWriter = new StringWriter();
            Marshaller.marshal(or, xmlStringWriter);
            if (log.isDebugEnabled()) {
                log.debug("Has descriptors: " +
                        ((or.getResourceDescriptors() == null || or.getResourceDescriptors().size() == 0) ?
                                0 : or.getResourceDescriptors().size())
                        );
                log.debug("marshalled response");
                log.debug(xmlStringWriter.toString());
            }
            result = xmlStringWriter.toString();

        } catch (Exception ex)
        {
            log.error("caught exception marshalling an OperationResult: " + ex.getMessage(), ex);
            // What to do?
        }

        return result;
    }
    /* Axis 2 old version
    private static String marshalResponse( OperationResult or, Map datasources)
    {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace namespace = fac.createOMNamespace("http://jasperserver/ws2/namespace1","ns1");

        OMElement resultElemList = fac.createOMElement("list",namespace);
        OMElement resultElem = fac.createOMElement("result",namespace);
        resultElemList.addChild(resultElem);

        try {
            StringWriter xmlStringWriter = new StringWriter();
            Marshaller.marshal(or, xmlStringWriter);
            if (log.isDebugEnabled()) {
                log.debug("Has descriptors: " +
                        ((or.getResourceDescriptors() == null || or.getResourceDescriptors().size() == 0) ?
                                0 : or.getResourceDescriptors().size())
                        );
                log.debug("marshalled response");
                log.debug(xmlStringWriter.toString());
            }
            resultElem.setText(xmlStringWriter.toString());

        } catch (Exception ex)
        {
            log.error("caught exception marshalling an OperationResult: " + ex.getMessage(), ex);
            // What to do?
        }

        if (datasources != null)
        {
            for (Iterator it = datasources.entrySet().iterator(); it.hasNext(); )
            {
                 try {
                     Map.Entry entry = (Map.Entry) it.next();
                     String name = (String) entry.getKey();
                     DataSource datasource = (DataSource) entry.getValue();

                     log.debug("Adding attachment: " + name + ", type: " + datasource.getContentType());

                    DataHandler expectedDH = new DataHandler( datasource );
                    OMText textData = fac.createOMText(expectedDH, true);
                    OMElement dataElement = fac.createOMElement(name, namespace);
                    textData.setOptimize(true);
                    dataElement.addChild(textData);
                    resultElem.addChild(dataElement);

                } catch (Exception ex)
                {
                    log.error("caught exception marshalling an OperationResult: " + ex.getMessage(), ex);
                    // What to do?
                }
            }
        }
        return resultElemList;
    }
    */

	public String list(String requestXmlString) {

		// The incoming parameter will be a
		// com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Request
		//
		// The outgoing response object will be a
		// com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.OperationResult
		//

		// The task of each of these methods is to look inside the incoming
		// Request object in order to see what needs to be done. For instance,
		// in this method (list), the client will typically want a list of the
		// contents of a particular directory (uri) path. The Request object
		// will contain inside of it a ResourceDescriptor that would of type
		// "folder". This folder would have a uri such as /reports/samples.

		// So, the method needs to contact the repository and get a list
		// of files and folders inside the given directory.


		// In order to build the OperationResult response, there are the
		// ResourceDescriptor and OperationResult classes. These are java beans.
		// The first is specifically designed to allow for the collection of
		// all relevant information about a Resource that JasperServer (currently)
		// can provide. The ResourceDescriptor is quite flat, so it has different
		// information updated depending on which type of JS Resource object is
		// being populated. In the case of files and folders in the context of
		// a "list" request, it only contains the level of detail one would expect
		// from an "ls -l" sort of command. Ie. file names, files types, etc.

		OperationResult or = new OperationResult();
                or.setVersion( ManagementServiceImpl.WS_VERSION);
                or.setReturnCode( OperationResult.SUCCESS );
                try {

                    //Iterator iter = element.getChildElements();
                    //String requestXmlString = ((OMElement) iter.next()).getText();

                    // Unmarshall xmlDescriptor request...
                    StringReader xmlStringReader = new StringReader(requestXmlString);
                    Request request = (Request)Unmarshaller.unmarshal(Request.class, xmlStringReader);

                    setLocale(request.getLocale());
                    //repositoryHelper.getResourceBundle().getMessage("flow.button.next",null, locale)

                    List list = null;

                    if (request.getResourceDescriptor() == null)
                    {
                        list = new java.util.ArrayList();
                        log.debug("Null resourceDescriptor");
                        // Look for specific list requests...
                        if (getArgumentValue(Argument.LIST_DATASOURCES, request.getArguments()) != null &&
                            getArgumentValue(Argument.LIST_DATASOURCES, request.getArguments()).equals(Argument.VALUE_TRUE))
                        {
                            // List all datasources...
                            FilterCriteria criteria = FilterCriteria.createFilter(ReportDataSource.class);
                            log.debug("Listing datasources...");

                        	// This filters with object level security
                            // Will only get resources the user has access to

                        	List lookups = repository.loadClientResources(criteria);
                        	if (lookups != null && !lookups.isEmpty()) {

                                for (Iterator it = lookups.iterator(); it.hasNext(); ) {
                                	list.add( createResourceDescriptor( (Resource) it.next()) );
                                }
                            }
                        } else if (request.hasArgument(Argument.LIST_RESOURCES) &&
                                getArgumentValue(Argument.RESOURCE_TYPE, request.getArguments()).equals(Argument.REPORT_TYPE)) {
                        	// get list of reports from a certain directory recursively
                            // List all datasources...
                        	log.debug("Listing all reports...");

                        	String parentFolder = getArgumentValue(Argument.START_FROM_DIRECTORY, request.getArguments());
                        	if ((parentFolder == null) || (!(parentFolder.startsWith("/")))) {
                        		parentFolder = "/";
                        	}
             /*
                        	// all reports
                            FilterCriteria criteria = FilterCriteria.createFilter(ReportUnit.class);
                        	List lookups = repository.loadClientResources(criteria);
                        	if (lookups != null && !lookups.isEmpty()) {

                                for (Iterator it = lookups.iterator(); it.hasNext(); ) {
                                    Resource rs = (Resource)it.next();
                                    if ("/".equals(parentFolder)) {
                                        if ((!(ADHOC_TOPICS.equals(rs.getParentFolder()))) && (!(ADHOC_TEMP.equals(rs.getParentFolder())))) {
                                     	   list.add( createResourceDescriptor(rs));
                                        }
                                    } else if ((rs.getURIString() + "/").startsWith(parentFolder + "/")) {
                                        if ((!(ADHOC_TOPICS.equals(rs.getParentFolder()))) && (!(ADHOC_TEMP.equals(rs.getParentFolder())))) {
                                    	   list.add( createResourceDescriptor(rs));
                                        }
                                    }
                                }
                            }
              */
                        	// all options
                        	List allSubFolders = repository.getAllFolders(null);
                        	for (int i=0; i<allSubFolders.size(); i++) {
                        		String currentFolder = ((Folder)allSubFolders.get(i)).getURIString();
                        		FilterCriteria filterCriteria = new FilterCriteria();
                        		filterCriteria.addFilterElement( FilterCriteria.createParentFolderFilter(currentFolder) );
                        		List units = repository.loadClientResources(filterCriteria);
                        		for (int j=0; j<units.size(); j++) {
                        			Resource currentRs = (Resource) units.get(j);
                        			if ((currentRs.getResourceType() != null) && ((currentRs.getResourceType().contains("ReportOptions")) || (currentRs.getResourceType().contains("ReportUnit")) || (currentRs.getResourceType().contains("AdhocReportUnit")) )) {

                                       String temp = serviceConfiguration.getTempFolder();
                                       if ("/".equals(parentFolder)) {
                                           if ((!(ADHOC_TOPICS.equals(currentRs.getParentFolder()))) && (!(temp.equals(currentRs.getParentFolder())))) {
                                        	   list.add( createResourceDescriptor(currentRs));
                                           }
                                       } else if ((currentRs.getURIString() + "/").startsWith(parentFolder + "/")) {
                                           if ((!(ADHOC_TOPICS.equals(currentRs.getParentFolder()))) && (!(temp.equals(currentRs.getParentFolder())))) {
                                       	   list.add( createResourceDescriptor(currentRs));
                                           }
                                       }
                        		   }
                        		}
                        	}
                        } else if (request.hasArgument(Argument.LIST_RESOURCES)) {
                        	String resourceType = request.getArgumentValue(Argument.RESOURCE_TYPE);

                        	if (resourceType == null) {
                        		if (log.isDebugEnabled()) {
                        			log.debug("No " + Argument.RESOURCE_TYPE + " argument, nothing to list");
                        		}
                        	} else {
                            	ResourceHandler handler = handlerRegistry.getHandler(resourceType);
                            	if (handler == null) {
                            		throw new JSException("No resource hander found for type " + resourceType);
                            	}

                            	List resources = handler.listResources(request, this);
                            	if (resources != null) {
                            		list.addAll(resources);
                            	}
                        	}
                        }
                    }
                    else if (request.getResourceDescriptor().getWsType().equals( ResourceDescriptor.TYPE_FOLDER))
                    {
                        log.debug("List folders");
                        list = listResources( request.getResourceDescriptor().getUriString() );
                    }
                    else if (request.getResourceDescriptor().getWsType().equals( ResourceDescriptor.TYPE_REPORTUNIT))
                    {
                        log.debug("List report units");
                        list = createResourceDescriptor( request.getResourceDescriptor().getUriString()).getChildren();
                    } else {
                        log.debug("Listed nothing");
                    }

                    if (log.isDebugEnabled()) {
                    	log.debug("Found " + list.size() + " things");
                        for (Iterator it = list.iterator(); it.hasNext(); ) {
                        	ResourceDescriptor rd = (ResourceDescriptor) it.next();
                        	log.debug( rd != null ? rd.getName() : "rd was null");
                        }
                    }

                    or.setResourceDescriptors( list );

                    log.debug("Marshalling response");

                } catch (Exception e) {
                    log.error("caught exception: " + e.getMessage(), e);
                    or.setReturnCode( 1 );
                    or.setMessage(e.getMessage());
                } catch (Throwable e)
                {
                    log.error("caught exception: " + e.getMessage(), e);
                }

                return marshalResponse( or );

	}

    private Set<String> getCurrentUserRoles() {
        Set<String> roleNames = new HashSet<String>();

        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationToken == null) {
            return roleNames;
        }

        if (authenticationToken.getPrincipal() instanceof UserDetails) {
            UserDetails contextUserDetails = (UserDetails) authenticationToken.getPrincipal();
            for (GrantedAuthority authority: contextUserDetails.getAuthorities()) {
                roleNames.add(authority.getAuthority());
            }

        }

        return roleNames;
    }

    private void filterFolderList(List folderList) {
        if (folderList == null || folderList.size() == 0) {
            return;
        }

        Set<String> roles = getCurrentUserRoles();
        for (Iterator i = folderList.iterator(); i.hasNext(); ) {
            Folder folder = (Folder)i.next();
            if (serviceConfiguration.getTempFolder().equals(folder.getURIString())) {
                boolean accessDenied = true;
                if (roles != null && roles.size() > 0) {
                    for (String role: roles) {
                        if (serviceConfiguration.getRoleToAccessTempFolder().equals(role)) {
                            accessDenied = false;
                            break;
                        }
                    }
                }

                if (accessDenied) {
                    i.remove();
                }
            }
        }
    }

    /**
     * Return a list of ResourceDescriptor(s)
     * @throws WSException
     */
    public List listResources(String uri) throws WSException
    {
    	log.debug("list for uri: " + uri);

        List returnedMaps = new ArrayList();


    	// This filters with object level security.
        // Will only get folders the user has access to

        List folders = getRepository().getSubFolders(null, uri);
        filterFolderList(folders);

        if (folders == null) return returnedMaps;

        for (int i=0; i <folders.size(); ++i)
        {
            Resource folderRes = (Resource)folders.get(i);
            returnedMaps.add( createResourceDescriptor(folderRes) );
        }

        // create a criteria for finding things with a common parent folder.
	FilterCriteria filterCriteria = new FilterCriteria();
	filterCriteria.addFilterElement( FilterCriteria.createParentFolderFilter(uri) );

	// This filters with object level security
	// Will only get resources the user has access to

	List units = getRepository().loadClientResources(filterCriteria);

        if (units == null) return returnedMaps;

        for (Iterator it = units.iterator(); units != null && it.hasNext(); )
        {
            Resource fileRes = (Resource) it.next();
            try {
            	returnedMaps.add( createResourceDescriptor(fileRes));
            } catch (Exception ex)
            {
                log.error(ex);
            }
        }

        return returnedMaps;
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
	public String get(String requestXmlString)  {

                OperationResult or = new OperationResult();
                or.setVersion( ManagementServiceImpl.WS_VERSION);
                or.setReturnCode( OperationResult.SUCCESS );


                try {

                    //Iterator iter = element.getChildElements();
                    //String requestXmlString = ((OMElement) iter.next()).getText();

                    // Unmarshall xmlDescriptor request...
                    StringReader xmlStringReader = new StringReader(requestXmlString);
                    Request request = (Request)Unmarshaller.unmarshal(Request.class, xmlStringReader);
                    createAuditEvent(request.getOperationName(), request.getResourceDescriptor().getWsType(),
                            request.getResourceDescriptor().getIsNew());

                    setLocale(request.getLocale());


                    List args = request.getArguments();

                    List params = Collections.EMPTY_LIST;
                    if (request.getResourceDescriptor() != null && request.getResourceDescriptor().getParameters() != null) {
                        params = request.getResourceDescriptor().getParameters();
                    }

                    HashMap specialOptions = new HashMap();
                    if (args != null)
                    {
                        for (int i=0; i<args.size(); ++i)
                        {
                            Argument arg = null;

                            if (args.get(i) instanceof Argument)
                            {
                                arg = (Argument)args.get(i);
                            }
                            if (arg != null)
                            {
                                specialOptions.put(arg.getName(), arg.getValue());
                            }
                        }
                    }
                    if (params.size() > 0 && specialOptions.containsKey(Argument.RU_REF_URI)) {
                        ResourceDescriptor reportDescriptior = createResourceDescriptor((String) specialOptions.get(Argument.RU_REF_URI));
                        specialOptions.put(Argument.PARAMS_ARG, buildParameterMap(params, reportDescriptior));
                    }

                    String resourceURI = request.getResourceDescriptor().getUriString();
                	Resource resource = locateResource(resourceURI);
                    if (resource == null)
                    {
                    	log.warn("Get: null resourceDescriptor for " + resourceURI);
                        or.setReturnCode(2);
                        or.setMessage(messageSource.getMessage("webservices.error.resourceNotFound",null, getLocale()));
                    }
                    else
                    {
    					ResourceDescriptor rd = createResourceDescriptor(resource, processDescriptorOptions(specialOptions));

                    	log.debug("Get: " + resourceURI +
                    			", wsType: " + rd.getWsType() + ", resourceType: " + rd.getResourceType());
                        or.getResourceDescriptors().add(rd);

                        ResultAttachments attachments = new ResultAttachments();
                        attachments.setEncapsulationDime(getArgumentValue("USE_DIME_ATTACHMENTS", request.getArguments()) != null);
                        ResourceHandler handler = getHandlerRegistry().getHandler(rd.getWsType());
                        handler.getAttachments(resource, specialOptions, rd, attachments, this);
                        if (or.getReturnCode() != 0) {
                            addExceptionToAllAuditEvents(new Exception(or.getMessage()));
                        }

                        return marshalResponse(or, attachments);
                    }
                } catch (Exception e) {

                    log.error("caught exception: " + e.getMessage(), e);
                    or.setReturnCode( 1 );
                    or.setMessage(e.getMessage());
                    addExceptionToAllAuditEvents(e);
                }
                log.debug("Marshalling response");

                return marshalResponse( or );
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
    public ResourceDescriptor createResourceDescriptor(String uri) throws WSException
    {
        return createResourceDescriptor(uri, null);
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
    protected ResourceDescriptor createResourceDescriptor(String uri, Map specialOptions) throws WSException
    {
    	Resource res = locateResource(uri);
    	if (res == null) {
            throw new WSException(WSException.GENERAL_ERROR2,
                    messageSource.getMessage(
                            "webservices.error.resourceNotFoundOrInvalid",
                            new Object[]{uri}, getLocale()));
    	}

        specialOptions = processDescriptorOptions(specialOptions);

        return createResourceDescriptor( res, specialOptions);
    }

	protected Map processDescriptorOptions(Map specialOptions) {
		if (specialOptions == null) {
        	specialOptions = new HashMap();
        }
        if (!specialOptions.containsKey(ReportUnitHandler.OPTION_REPORT_UNIT_CONTENTS)) {
        	specialOptions.put(ReportUnitHandler.OPTION_REPORT_UNIT_CONTENTS, Boolean.TRUE);
        }
		return specialOptions;
	}

	protected Resource locateResource(String uri) throws WSException {
		Resource res = null;

        String name;
        String folderName;

        int sep = uri.lastIndexOf(Folder.SEPARATOR);
        if (sep >= 0) {
                name = uri.substring(sep + Folder.SEPARATOR_LENGTH);
                folderName = uri.substring(0,sep);
        } else {
                // No separator: error
                throw new WSException(WSException.GENERAL_REQUEST_ERROR, messageSource.getMessage("jsexception.invalid.uri",new Object[] {uri}, getLocale()));
        }

        // Check if the folder is a RU first...
        if (folderName.endsWith("_files"))
        {
            String parentUri = folderName.substring( 0, folderName.length() - "_files".length() );
            log.warn("Loading uri: " + parentUri);
            Resource parentRes = getRepository().getResource(null, parentUri);
            if (parentRes != null)
            {
                // The parent folder is a RU...
                // Get the resource (quick way to check accessibility....)
                ResourceDescriptor ruRd = createResourceDescriptor(folderName);

                log.warn("Loaded RU " + res);
                if (ruRd == null) {
                    // The user can not access to this RU...
                    return null;
                }

                res = getRepository().getResource(null, uri);
                log.warn("Loaded resource " + uri + " " + res);
            }
        }

        if (res == null)
        {
            if (folderName.length() == 0) folderName = "/";

            FilterCriteria filterCriteria = new FilterCriteria();
            filterCriteria.addFilterElement( FilterCriteria.createParentFolderFilter(folderName) );
            filterCriteria.addFilterElement(FilterCriteria.createPropertyEqualsFilter("name", name));

            // This filters with object level security
            // Will only get resources the user has access to

            List resources = getRepository().loadClientResources(filterCriteria);
            if (resources != null && !resources.isEmpty()) {
                    res = (Resource) resources.get(0);
            }
        }

        if (res == null) // try to look for a folder...
        {
            Folder folder = getRepository().getFolder(null, uri);
            if (folder != null)
            {
                res = folder;
            }
            else
            {
                return null;
            }
        }
		return res;
	}

    /**
     * the same as createResourceDescriptor( resource, false)
     * @throws WSException
     */
    public ResourceDescriptor createResourceDescriptor( Resource resource) throws WSException
    {
        return createResourceDescriptor( resource, null);
    }

    /**
     * Create a ResourceDescriptor from a Resource.
     * The real type of this resource is saved in WsType
     * @throws WSException
     *
     */
    public ResourceDescriptor createResourceDescriptor( Resource resource, Map specialOptions) throws WSException
    {
    	if (resource instanceof ResourceLookup) {
    		throw new JSException("jsexception.resourcelookup.not.a.resource", new Object[] {resource.getClass().getName()});
    	}

    	ResourceHandler resourceHandler = getHandlerRegistry().getHandler(resource);
    	if (resourceHandler == null)
    	{
    		throw new JRRuntimeException("No resource handler found for class " + resource.getClass().getName());
    	}

        return resourceHandler.describe(resource, specialOptions, this);
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
	public String put(String requestXmlString)  {

		OperationResult or = new OperationResult();
        or.setVersion( ManagementServiceImpl.WS_VERSION);
        or.setReturnCode( OperationResult.SUCCESS );

        try {

            //Iterator iter = element.getChildElements();
            //OMElement requestElement = (OMElement) iter.next();
            //String requestXmlString = requestElement.getText();
/*
            if (log.isDebugEnabled()) {
                log.debug("Put XML");
                log.debug(requestXmlString);
            }
*/
            // Unmarshall xmlDescriptor request...
            StringReader xmlStringReader = new StringReader(requestXmlString);
            Request request = (Request)Unmarshaller.unmarshal(Request.class, xmlStringReader);
            createAuditEvent(request.getOperationName(), request.getResourceDescriptor().getWsType(), request.getResourceDescriptor().getIsNew());

            setLocale(request.getLocale());

            ResourceDescriptor newDescriptor = request.getResourceDescriptor();

            log.debug("Put: for " + newDescriptor.getUriString());

            if (newDescriptor.getUriString() == null || newDescriptor.getUriString().length() == 0) {
                throw new WSException(WSException.GENERAL_REQUEST_ERROR, messageSource.getMessage("webservices.error.noUriGiven",null, getLocale()) );
            }

            if (newDescriptor.getWsType() == null || newDescriptor.getWsType().length() == 0) {
                throw new WSException(WSException.GENERAL_REQUEST_ERROR, messageSource.getMessage("webservices.error.noTypeGiven",null, getLocale()) );
            }

            String wsType = newDescriptor.getWsType();
            ResourceHandler handler = getHandlerRegistry().getHandler(wsType);
            handler.put(makeServiceRequest(request, or));
            if (or.getReturnCode() != 0) {
                addExceptionToAllAuditEvents(new Exception(or.getMessage()));
            }

        } catch (WSValidationException e)
        {
            log.error("caught validation exception: " + e.getMessage(), e);

            or.setReturnCode( e.getErrorCode() );
            or.setMessage(getValidationErrorMessage(e.getObjectName(), e.getErrors()));
            addExceptionToAllAuditEvents(e);
        } catch (WSException e){
            or.setReturnCode( e.getErrorCode() );
            or.setMessage(e.getMessage());
            addExceptionToAllAuditEvents(e);
        } catch (Exception e) {

            //e.printStackTrace();
            log.error("caught exception: " + e.getMessage(), e);

            or.setReturnCode( 1 );
            or.setMessage(e.getMessage());
            addExceptionToAllAuditEvents(e);
        }
        log.debug("Marshalling response");

        return marshalResponse( or );
	}

	protected String getValidationErrorMessage(String objectName, ValidationErrors errors) {
		StringBuffer message = new StringBuffer();
		List errorList = errors.getErrors();
		message.append(getMessage("webservices.error.validation.errors.prefix",
				new Object[]{new Integer(errorList.size())}));
		message.append("\n");
		for (Iterator it = errorList.iterator(); it.hasNext();) {
			ValidationError error = (ValidationError) it.next();
			message.append(getValidationErrorMessage(objectName, error));
			message.append("\n");
		}
		return message.toString();
	}

	protected Object getValidationErrorMessage(String objectName, ValidationError error) {
		MessageCodesResolver codesResolver = getMessageCodesResolver();
		String[] codes;
		if (error.getField() == null) {
			codes = codesResolver.resolveMessageCodes(error.getErrorCode(), objectName);
		} else {
			codes = codesResolver.resolveMessageCodes(error.getErrorCode(), objectName,
					error.getField(), null);
		}
		MessageSourceResolvable messageResolvable = new DefaultMessageSourceResolvable(
				codes, error.getErrorArguments(), error.getDefaultMessage());
		String message = messageSource.getMessage(messageResolvable, getLocale());
		return message;
	}

	protected MessageCodesResolver getMessageCodesResolver() {
		return (MessageCodesResolver) beanFactory.getBean(BEAN_NAME_MESSAGE_CODES_RESOLVER);
	}

	protected ServiceRequest makeServiceRequest(final Request request, final OperationResult operationResult) {
		return new ServiceRequest() {
			public RepositoryServiceContext getContext() {
				return ManagementServiceImpl.this;
			}

			public ResourceDescriptor getRequestDescriptor() {
				return request.getResourceDescriptor();
			}

			public String getRequestArgument(String name) {
				return getArgumentValue(name, request.getArguments());
			}

			public OperationResult getResult() {
				return operationResult;
			}
    	};
	}

        /*
	public String checkForRequiredResources(String element) throws XMLStreamException {

		return element;
	}
        */


        protected String getArgumentValue(String argumentName, List arguments)
        {
            for (int i=0; i < arguments.size(); ++i)
            {
                Argument a = (Argument) arguments.get(i);
                if (a.getName() == null ? a.getName() == argumentName : a.getName().equals(argumentName))
                {
                    return a.getValue();
                }
            }

            return null;
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

                OperationResult or = new OperationResult();
                or.setVersion( ManagementServiceImpl.WS_VERSION);
                or.setReturnCode( OperationResult.SUCCESS );

                try {

                    //Iterator iter = element.getChildElements();
                    //String requestXmlString = ((OMElement) iter.next()).getText();

                    // Unmarshall xmlDescriptor request...
                    StringReader xmlStringReader = new StringReader(requestXmlString);
                    Request request = (Request)Unmarshaller.unmarshal(Request.class, xmlStringReader);
                    createAuditEvent(request.getOperationName(), request.getResourceDescriptor().getWsType(),
                            request.getResourceDescriptor().getIsNew());

                    setLocale(request.getLocale());

                    String reportUnitUrl = getArgumentValue(Argument.MODIFY_REPORTUNIT, request.getArguments());

                    if (reportUnitUrl != null && reportUnitUrl.length() > 0)
                    {
                    	log.debug("Delete: resource in reportUnit: " + reportUnitUrl);

                        // Report unit modification....
                        ReportUnit reportUnit = (ReportUnit)repository.getResource(null,reportUnitUrl);
                        if (reportUnit == null)
                        {
                            throw new WSException( WSException.REFERENCED_RESOURCE_NOT_FOUND,
                            		messageSource.getMessage("webservices.error.reportUnitNotFound",null, getLocale())
                                    );
                        }

                        SubResourceHandler handler = (SubResourceHandler) getHandlerRegistry().getHandler(reportUnit);
                        handler.deleteSubResource(reportUnit, request.getResourceDescriptor(), this);
                    }
                    else
                    {
                        ResourceDescriptor rd = createResourceDescriptor( request.getResourceDescriptor().getUriString() );
                        if (rd == null)
                        {
                        	log.error("Delete: no resource : " + request.getResourceDescriptor().getUriString());
                            //or.setReturnCode(2);
                            //or.setMessage("Resource not found");
                        }
                        else
                        {
                        	String wsType = rd.getWsType();
							log.debug("Delete: resource : " + wsType);
							ResourceHandler handler = getHandlerRegistry().getHandler(wsType);
							handler.delete(rd, this);
                        }
                    }

                    if (or.getReturnCode() != 0) {
                        addExceptionToAllAuditEvents(new Exception(or.getMessage()));
                    }
                } catch (WSException e) {
                    or.setReturnCode( e.getErrorCode() );
                    or.setMessage( e.getMessage() );
                    addExceptionToAllAuditEvents(e);
                } catch (Exception e) {
                    e.printStackTrace();
			        log.error("caught exception: " + e.getMessage(), e);

                    or.setReturnCode( 1 );
                    or.setMessage(e.getMessage());
                    addExceptionToAllAuditEvents(e);
		        }

                log.debug("Marshalling response");
                return marshalResponse( or );
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
            long currentTime = System.currentTimeMillis();
            createAuditEvent("runReport");
            OperationResult or = new OperationResult();
            or.setVersion( ManagementServiceImpl.WS_VERSION);
            or.setReturnCode( OperationResult.SUCCESS );
            Map attachments = new HashMap();

            boolean isEncapsulationDime = false;

            try {

                //Iterator iter = element.getChildElements();
                //String requestXmlString = ((OMElement) iter.next()).getText();

                // Unmarshall xmlDescriptor request...
                StringReader xmlStringReader = new StringReader(requestXmlString);
                Request request = (Request) Unmarshaller.unmarshal(Request.class, xmlStringReader);
                setLocale(request.getLocale());

                isEncapsulationDime = (getArgumentValue("USE_DIME_ATTACHMENTS", request.getArguments()) != null);

                ResourceDescriptor reportUnit = request.getResourceDescriptor();
            	log.debug("Run report: resource : " + reportUnit.getUriString());

                String format = getArgumentValue(Argument.RUN_OUTPUT_FORMAT, request.getArguments());
                if (format == null) format = Argument.RUN_OUTPUT_FORMAT_PDF;
                format = format.toUpperCase();
                Resource reportResource = repository.getResource(null, reportUnit.getUriString());

                if (reportResource == null || !(reportResource instanceof ReportUnit)) {
                	throw new WSException(2,
                			messageSource.getMessage("webservices.error.notValidReportUnit",
                                new Object[]{reportUnit.getUriString()}, getLocale())
                                );
                }
                ReportUnit reportUnitResource = (ReportUnit) reportResource;

                // build parameters
                Map parameters = buildParameterMap(reportUnit.getParameters(), reportUnit);

                boolean freshData = Boolean.parseBoolean(getArgumentValue(
                		Argument.FRESH_DATA, request.getArguments()));
                boolean saveDataSnapshot = Boolean.parseBoolean(getArgumentValue(
                		Argument.SAVE_DATA_SNAPSHOT, request.getArguments()));
                
                // we need a report context for snapshots
                SimpleReportContext reportContext = new SimpleReportContext();
                parameters.put(JRParameter.REPORT_CONTEXT, reportContext);
                
                ReportUnitRequest reportRequest = new ReportUnitRequest(reportUnit.getUriString(), parameters);
        		reportRequest.setReportContext(reportContext);
        		reportRequest.setJasperReportsContext(getJasperReportsContext());
                
                // recording is enabled for first-time saves
        		reportRequest.setRecordDataSnapshot(dataSnapshotService.isSnapshotPersistenceEnabled());
            	// fresh data if requested or saving
        		reportRequest.setUseDataSnapshot(!(freshData || saveDataSnapshot));
                
				ExecutionContext executionContext = createExecutionContext();
                // run the report
				ReportUnitResult result = (ReportUnitResult)runReportEngine.execute(
                                executionContext,
                                reportRequest);


                if (result == null) {

                		throw new WSException(WSException.FILL_ERROR,
                				messageSource.getMessage("webservices.error.errorExecutingReportUnit",
                                            new Object[]{reportUnit.getUriString()}, getLocale())

                                        );

                } else {
                	// save snapshot if necessary
                	persistDataSnapshot(executionContext, saveDataSnapshot, reportUnitResource, reportContext);
                	
                	JasperPrint jasperPrint = result.getJasperPrint();

                    // Export...
        			ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ByteArrayDataSource bads = null;

                    if (format.equals(Argument.RUN_OUTPUT_FORMAT_JRPRINT))
                    {
                    	log.debug("Returning JasperPrint");

                    	String transformerKey = request.getArgumentValue(
                    			Argument.RUN_TRANSFORMER_KEY);
                    	if (transformerKey != null)
                    	{
                    		if (log.isDebugEnabled())
                    		{
                    			log.debug("Transforming JasperPrint generic element for key "
                    					+ transformerKey);
                    		}

                    		GenericElementReportTransformer.transformGenericElements(
                    				jasperPrint, transformerKey);
                    	}

                        JRSaver.saveObject(jasperPrint, bos);
                        bads = new ByteArrayDataSource(bos.toByteArray());
                        attachments.put("jasperPrint", bads);
                    }
                    else
                    {


                	//List jasperPrintList = new ArrayList();
        		//jasperPrintList.add(jasperPrint);

                        HashMap exportParameters = new HashMap();

                        String value = getArgumentValue(Argument.RUN_OUTPUT_PAGE, request.getArguments());
                        if (value != null) exportParameters.put(Argument.RUN_OUTPUT_PAGE, value);

                        value = getArgumentValue(Argument.RUN_OUTPUT_IMAGES_URI, request.getArguments());
                        if (value != null) exportParameters.put(Argument.RUN_OUTPUT_IMAGES_URI, value);

                        Map exporterParams;
                        try {
                        	exporterParams = exportReport(reportUnit.getUriString(), jasperPrint,
                            		format, bos, exportParameters);
                       } catch (Exception e) {
            				log.error("Error exporting report", e);
            				throw new WSException(WSException.EXPORT_ERROR, messageSource
            						.getMessage("webservices.error.errorExportingReportUnit",
            								new Object[] { e.getMessage() }, getLocale()));
                        } finally {
                			if (bos != null) {
                				try {
                					bos.close();
                				} catch (IOException ex) {
                				}
                			}
                        }

                        bads = new ByteArrayDataSource(bos.toByteArray(), getContentType(format));
                        attachments.put("report", bads);
                        addAdditionalAttachmentsForReport(jasperPrint, attachments, format, exporterParams);
                    }

                }

                if (or.getReturnCode() != 0) {
                    addExceptionToAllAuditEvents(new Exception(or.getMessage()));
                }
            } catch (WSException e) {
                or.setReturnCode( e.getErrorCode() );
                or.setMessage(e.getMessage());
                addExceptionToAllAuditEvents(e);

            } catch (Throwable e) {
            	log.error("caught Throwable exception: " + e.getMessage(), e);
                e.printStackTrace(System.out);
                System.out.flush();
                or.setReturnCode( 1 );
                or.setMessage(e.getMessage());
                addExceptionToAllAuditEvents(e);
            }

            addPropertyToAuditEvent("runReport", "reportExecutionStartTime", new Date(currentTime));
            addPropertyToAuditEvent("runReport", "reportExecutionTime", System.currentTimeMillis() - currentTime);
            return marshalResponse( or,  attachments, isEncapsulationDime);

        }

	protected void persistDataSnapshot(ExecutionContext executionContext, boolean updateSnapshot, 
			ReportUnit reportUnit, ReportContext reportContext) {
		SnapshotSaveStatus snapshotSaveStatus = dataCacheProvider.getSnapshotSaveStatus(reportContext);
		switch (snapshotSaveStatus) {
		case NEW:
			// automatic save
			if (log.isDebugEnabled()) {
				log.debug("saving initial data snapshot for " + reportUnit.getURIString());
			}
			
			saveAutoDataSnapshot(executionContext, reportUnit, reportContext);
			break;
		case UPDATED:
			if (updateSnapshot) {
				// requested save
				if (log.isDebugEnabled()) {
					log.debug("saving updated data snapshot for " + reportUnit.getURIString());
				}
				
				saveDataSnapshot(executionContext, reportUnit, reportContext);
			}
			break;
		case NO_CHANGE:
		default:
			//NOP
			break;
		};
	}
	
	protected void saveAutoDataSnapshot(ExecutionContext executionContext, ReportUnit reportUnit, 
			ReportContext reportContext) {
		try {
			dataSnapshotService.saveAutoReportDataSnapshot(executionContext, reportContext, reportUnit);
		} catch (Exception e) {
			// catching any exceptions for automatic and requested save
			log.error("Error while saving data snapshot for " + reportUnit.getURIString(), e);
		}
	}
	
	protected void saveDataSnapshot(ExecutionContext executionContext, ReportUnit reportUnit, 
			ReportContext reportContext) {
		try {
			dataSnapshotService.saveReportDataSnapshot(executionContext, reportContext, reportUnit);
		} catch (Exception e) {
			// catching any exceptions for automatic and requested save
			log.error("Error while saving data snapshot for " + reportUnit.getURIString(), e);
		}
	}

    /**
     * Builds parameter map before report running, query executing, etc.
     * @param paramList list of ListItems
     * @param reportUnit report unit which contains parameter declaration (including their types)
     * @return a map of parameters ready for query execution
     */
    private Map buildParameterMap(List paramList, ResourceDescriptor reportUnit) {
        // Load parameters...
        Map parameters = new HashMap();

        for (int i = 0; i < paramList.size(); ++i)
        {
            ListItem param = (ListItem) paramList.get(i);
            if (log.isDebugEnabled()) {
                log.debug("Parameter: " + param.getLabel() + ", value: " + param.getValue());
            }

            if (param.isIsListItem())
            {
                // Look if this is the first item for this collection...
                java.util.Collection col = new ArrayList();
                Object currentValue = parameters.get(param.getLabel());
                if (currentValue != null && currentValue instanceof java.util.Collection)
                {
                    col = (java.util.Collection)currentValue;
                }

                col.add(param.getValue());
                parameters.put(param.getLabel(), col);
            }
            else
            {
                parameters.put(param.getLabel(), param.getValue());
            }
        }


        parameters = repositoryHelper.convertParameterValues(getReportJRParameters(reportUnit), parameters);
        return parameters;
    }

    private Map<String, JRParameter> getReportJRParameters(ResourceDescriptor reportReference) {
        JRReport report = engine.getMainJasperReport(null, reportReference.getUriString());
        Map parametersMap = getReportJRParameters(report);
        parametersMap.putAll(getJRParametersFromDatasource(reportReference));

        return parametersMap;
    }

    //TODO this is really needed only in PRO, but moving it there would require significant application context rethinking
    private Map<String, JRParameter> getJRParametersFromDatasource(ResourceDescriptor reportReference) {
        Map parametersMap = new HashMap();
        ReportUnit reportUnit = (ReportUnit)repository.getResource(null, reportReference.getUriString(), ReportUnit.class);
        if(reportUnit.getDataSource() == null || reportUnit.getDataSource().getReferenceURI() == null) {
            return parametersMap;
        }

        String jrxmlURI = StringUtils.stripEnd(reportUnit.getDataSource().getReferenceURI(), "/") + "_files/topicJRXML";
        FileResource jrxmlResource = (FileResource)reportLoadingService.getRepositoryService().getResource(ExecutionContextImpl.getRuntimeExecutionContext(), jrxmlURI, FileResource.class);
        if(jrxmlResource == null) {
            return parametersMap;
        }
        JRReport report = reportLoadingService.getJasperReport(null, jrxmlResource, false);
        Map dsParametersMap = getReportJRParameters(report);
        parametersMap.putAll(dsParametersMap);
        return parametersMap;
    }

    private Map<String, JRParameter> getReportJRParameters(JRReport report) {
        Map parametersMap = new HashMap();

        JRParameter[] jrParameters = report.getParameters();
        if (jrParameters == null)
        {
            return parametersMap;
        }
        for (JRParameter parameter: jrParameters)
        {
            parametersMap.put(parameter.getName(), parameter);
        }
        return parametersMap;
    }

    protected Map exportReport(
    		String reportUnitURI,
    		JasperPrint jasperPrint,
			String format,
			OutputStream output,
			HashMap exportParameters) throws Exception
	{
    	WSExporter we = getServiceConfiguration().getExporter(format.toLowerCase());

    	return getServiceConfiguration().getExporter(format.toLowerCase()).exportReport(
    			jasperPrint,
    			output,
    			getEngine(),
    			exportParameters,
    			createExecutionContext(),
    			reportUnitURI

    	);
	}

		protected ExecutionContext createExecutionContext() {
			ExecutionContextImpl context = new ExecutionContextImpl();
			context.setLocale(getLocale());
			context.setTimeZone(TimeZone.getDefault());
			return context;
		}

	public JRExporter getExporter(String type, Map exportParameters) {
		JRExporter exporter = null;
		if (type.equals(Argument.RUN_OUTPUT_FORMAT_HTML)) {
			exporter = HtmlExportUtil.getHtmlExporter(getJasperReportsContext());
			if (exportParameters.get(Argument.RUN_OUTPUT_IMAGES_URI) != null)
			{
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,"" + exportParameters.get(Argument.RUN_OUTPUT_IMAGES_URI));
			}
			else
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "images/");
	        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.TRUE);
	        //exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, reportContent);

	        // collecting the images into a map
	        exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, new LinkedHashMap());
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_XLS)) {
			exporter =  new JExcelApiExporter();
			exporter.setParameter(JRXlsAbstractExporterParameter .IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
			exporter.setParameter(JRXlsAbstractExporterParameter .IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_CSV)) {
			exporter =  new JRCsvExporter();
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_XML)) {
			exporter =  new JRXmlExporter();
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_RTF)) {
			exporter =  new JRRtfExporter();
		}
		if (exporter != null &&(exportParameters.get(Argument.RUN_OUTPUT_PAGE) != null)) {
                        exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer("" + exportParameters.get(Argument.RUN_OUTPUT_PAGE)));
		}

		return exporter;

	}

	public String getContentType(String type) {
		String contentType = null;
		if (type.equals(Argument.RUN_OUTPUT_FORMAT_PDF)) {
			contentType = "application/pdf";
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_HTML)) {
			contentType = "text/html";
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_XLS)) {
			contentType = "application/xls";
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_CSV)) {
			contentType = "application/vnd.ms-excel";
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_XML)) {
			contentType = "text/xml";
		} else if (type.equals(Argument.RUN_OUTPUT_FORMAT_RTF)) {
			contentType =  "application/rtf";
		}

		return contentType;

	}

	/**
	 * Create additional Web Services attachments for the content. At this stage, HTML reports
	 * have their images as attachments
	 *
	 * @param jasperPrint
	 * @param attachments
	 * @param format
	 * @param exportParameters
	 * @throws WSException
	 */
	private void addAdditionalAttachmentsForReport(JasperPrint jasperPrint,
			Map attachments, String format, Map exportParameters) throws WSException {

		if (!format.equals(Argument.RUN_OUTPUT_FORMAT_HTML)) {
			return;
		}


		try {
			Map imagesMap = (Map) exportParameters.get(JRHtmlExporterParameter.IMAGES_MAP);
			for (Iterator it = imagesMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String name = (String) entry.getKey();
				byte[] data = (byte[]) entry.getValue();
				byte imageType = JRTypeSniffer.getImageType(data);
				String mimeType = JRTypeSniffer.getImageMimeType(imageType);

				if (log.isDebugEnabled()) {
					log.debug("Adding image for HTML: " + name
							+ ", type: " + mimeType);
				}

                ByteArrayDataSource bads = new ByteArrayDataSource(data, mimeType);
                attachments.put(name, bads);
			}
		} catch (Throwable e) {
			log.error(e);
			throw new WSException(WSException.EXPORT_ERROR,
                                messageSource.getMessage("webservices.error.errorAddingImage",
                                            new Object[]{e.getMessage()}, getLocale())
                                );
		}
	}


    /**
     * Function to get attachments from an Axis message
     *
     */
    public AttachmentPart[] getMessageAttachments() {
        try {
            MessageContext msgContext = MessageContext.getCurrentContext();
            final String attachmentsPath = attachmentsTempFolder.replace(JAVA_IO_TMPDIR_PLACEHOLDER, System.getProperty("java.io.tmpdir"));
            File file = new File(attachmentsPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            msgContext.setProperty(MessageContext.ATTACHMENTS_DIR, attachmentsPath);
            Message reqMsg = msgContext.getRequestMessage();
            Attachments messageAttachments = reqMsg.getAttachmentsImpl();
            if (null == messageAttachments) {
                log.error("no attachment support");
                return new AttachmentPart[0];
            }
            int attachmentCount = messageAttachments.getAttachmentCount();
            AttachmentPart attachments[] =
                    new AttachmentPart[attachmentCount];

            Iterator it = messageAttachments.getAttachments().iterator();
            int count = 0;
            while (it.hasNext()) {
                AttachmentPart part = (AttachmentPart) it.next();
                attachments[count++] = part;
            }
            return attachments;
        } catch (AxisFault e) {
            if (e.getFaultString().startsWith("java.io.IOException")) {
                throw new JSExceptionWrapper(new WSException(WSException.GENERAL_ERROR,
                        messageSource.getMessage("webservices.error.attachments.folder", null, getLocale())));
            }
            throw new JSExceptionWrapper(e);
        }
    }

    /**
     * This method takes the Locale requested by the client.
     * If the requested locale is null, the default locale
     * is returned.
     *
     * A locale code can be in the form:
     *
     * langagecode[_countrycode]
     *
     * Ex: en_US, it_IT, it, en_UK
     *
     */
    private void setLocale(String requestedLocale)
    {
        try {
            if (requestedLocale != null)
            {
                String language = requestedLocale;
                String country = "";
                if (requestedLocale.indexOf("_") > 0)
                {
                    language = requestedLocale.substring(0, requestedLocale.indexOf("_"));
                    country = requestedLocale.substring(requestedLocale.indexOf("_")+1);
                    setLocale(new Locale(language, country));
                }
                else
                {
                    setLocale(new Locale(language));
                }
            }
        } catch (Exception ex)
        {
            log.error("Unable to get requested locale (" + requestedLocale + ")");
            setLocale(Locale.getDefault());
        }
   }

    public String move(String requestXmlString) {
		OperationResult or = new OperationResult();
		or.setVersion(ManagementServiceImpl.WS_VERSION);
		or.setReturnCode(OperationResult.SUCCESS);

		try {
			Request request = (Request) Unmarshaller.unmarshalXml(requestXmlString);
            createAuditEvent(request.getOperationName(), request.getResourceDescriptor().getWsType(),
                    request.getResourceDescriptor().getIsNew());
			setLocale(request.getLocale());

			ResourceDescriptor resource = createRequestResourceDescriptor(request);
        	String wsType = resource.getWsType();
        	if (log.isDebugEnabled()) {
    			log.debug("Move WS type: " + wsType);
        	}
			ResourceHandler handler = getHandlerRegistry().getHandler(wsType);
			handler.move(request, this);
            if (or.getReturnCode() != 0) {
                addExceptionToAllAuditEvents(new Exception(or.getMessage()));
            }
		} catch (WSException e) {
			or.setReturnCode(e.getErrorCode());
			or.setMessage(e.getMessage());
            addExceptionToAllAuditEvents(e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			or.setReturnCode(1);
			or.setMessage(e.getMessage());
            addExceptionToAllAuditEvents(e);
		}

		return marshalResponse(or);
	}

    private void createAuditEvent(String operation, String wsType, boolean isNew) {
        if ("copy".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("copyFolder");
            } else {
                createAuditEvent("copyResource");
            }
        } else if ("move".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("moveFolder");
            } else {
                createAuditEvent("moveResource");
            }
        } else if ("delete".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("deleteFolder");
            } else {
                createAuditEvent("deleteResource");
            }
        } else if ("put".equals(operation)) {
            if ("folder".equals(wsType)) {
                if (isNew) {
                    createAuditEvent("createFolder");
                } else {
                    createAuditEvent("updateFolder");
                }
            } else {
                if (isNew) {
                    createAuditEvent("saveResource");
                } else {
                    createAuditEvent("updateResource");
                }
            }
        } else if ("get".equals(operation)) {
            if (!"folder".equals(wsType)) {
                createAuditEvent("accessResource");
            }
        }
    }

    public String copy(String requestXmlString) {
		OperationResult or = new OperationResult();
		or.setVersion(ManagementServiceImpl.WS_VERSION);
		or.setReturnCode(OperationResult.SUCCESS);

		try {
			Request request = (Request) Unmarshaller.unmarshalXml(requestXmlString);
            createAuditEvent(request.getOperationName(), request.getResourceDescriptor().getWsType(),
                    request.getResourceDescriptor().getIsNew());
			setLocale(request.getLocale());

			ResourceDescriptor resource = createRequestResourceDescriptor(request);
        	String wsType = resource.getWsType();
        	if (log.isDebugEnabled()) {
    			log.debug("Copy WS type: " + wsType);
        	}
			ResourceHandler handler = getHandlerRegistry().getHandler(wsType);
			ResourceDescriptor copyDescriptor = handler.copy(request, this);
			or.addResourceDescriptor(copyDescriptor);
            if (or.getReturnCode() != 0) {
                addExceptionToAllAuditEvents(new Exception(or.getMessage()));
            }

		} catch (WSException e) {
			or.setReturnCode(e.getErrorCode());
			or.setMessage(e.getMessage());
            addExceptionToAllAuditEvents(e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			or.setReturnCode(1);
			or.setMessage(e.getMessage());
            addExceptionToAllAuditEvents(e);
		}

		return marshalResponse(or);
	}

	protected ResourceDescriptor createRequestResourceDescriptor(Request request)
			throws WSException {
		ResourceDescriptor requestResource = getRequestResource(request);
		String sourceURI = getResourceURI(requestResource);

		ResourceDescriptor resource = createResourceDescriptor(sourceURI);
		if (resource == null) {
			throw new WSException(WSException.GENERAL_REQUEST_ERROR,
					getMessage("webservices.error.request.URI.not.found", new Object[]{sourceURI}));
		}
		return resource;
	}

	protected String getResourceURI(ResourceDescriptor descriptor) throws WSException {
		String sourceURI = descriptor.getUriString();
		if (sourceURI == null || sourceURI.length() == 0) {
			throw new WSException(WSException.GENERAL_REQUEST_ERROR,
					getMessage("webservices.error.request.no.resource.URI", null));
		}
		return sourceURI;
	}

	protected ResourceDescriptor getRequestResource(Request request) throws WSException {
		ResourceDescriptor descriptor = request.getResourceDescriptor();
		if (descriptor == null) {
			throw new WSException(WSException.GENERAL_REQUEST_ERROR,
					getMessage("webservices.error.request.no.resource.descriptor", null));
		}
		return descriptor;
	}

    public Locale getLocale() {
        return locale;
    }

    //TODO fix
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

	public String getMessage(String messageCode, Object[] args) {
		return messageSource.getMessage(messageCode, args, getLocale());
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public EngineService getEngine() {
		return engine;
	}

	public ResourceHandlerRegistry getHandlerRegistry() {
		return handlerRegistry;
	}

	public RepositoryHelper getRepositoryHelper() {
        if (repositoryHelper == null) {
            repositoryHelper = new RepositoryHelper(engine);
        }
		return repositoryHelper;
	}

	public ManagementServiceConfiguration getServiceConfiguration() {
		return serviceConfiguration;
	}

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

    public void setJasperReportsContext(JasperReportsContext jasperReportsContext) {
        this.jasperReportsContext = jasperReportsContext;
    }

    public void setEngine(EngineService engine) {
        this.engine = engine;
    }

    public void setHandlerRegistry(ResourceHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    public void setRepository(RepositoryService repository) {
        this.repository = repository;
    }

    public void setRunReportEngine(EngineService runReportEngine) {
        this.runReportEngine = runReportEngine;
    }

    public void setServiceConfiguration(ManagementServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
