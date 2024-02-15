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

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportLoadingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;
import com.jaspersoft.jasperserver.ws.axis2.ServiceRequest;
import com.jaspersoft.jasperserver.ws.axis2.WSException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id: ReportUnitHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportUnitHandler extends RepositoryResourceHandler implements
		SubResourceHandler {

	private static final Log log = LogFactory.getLog(ReportUnitHandler.class);

	public static final String OPTION_REPORT_UNIT_CONTENTS = "ReportUnitContents";

    private ReportLoadingService reportLoadingService;

    public void setReportLoadingService(ReportLoadingService reportLoadingService) {
        this.reportLoadingService = reportLoadingService;
    }

    public Class getResourceType() {
		return ReportUnit.class;
	}

	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext) throws WSException {
		ReportUnit reportUnit = (ReportUnit) resource;
		descriptor.setWsType(ResourceDescriptor.TYPE_REPORTUNIT);

		descriptor.setResourceProperty(ResourceDescriptor.PROP_RU_ALWAYS_PROPMT_CONTROLS, 
				reportUnit.isAlwaysPromptControls());
		descriptor.setResourceProperty(ResourceDescriptor.PROP_RU_CONTROLS_LAYOUT, 
				reportUnit.getControlsLayout());
		
		if (reportUnit.getInputControlRenderingView() != null) {
			descriptor.setResourceProperty(
					ResourceDescriptor.PROP_RU_INPUTCONTROL_RENDERING_VIEW,
					reportUnit.getInputControlRenderingView());
		}

		if (reportUnit.getReportRenderingView() != null) {
			descriptor.setResourceProperty(
					ResourceDescriptor.PROP_RU_REPORT_RENDERING_VIEW,
					reportUnit.getReportRenderingView());
		}

		Boolean reportUnitContent = arguments == null ? null
				: (Boolean) arguments.get(OPTION_REPORT_UNIT_CONTENTS);
		if (reportUnitContent != null && reportUnitContent.booleanValue()) {
			// Get datasource...
			ResourceReference ruDataSource = reportUnit.getDataSource();
			if (ruDataSource != null) {
				if (ruDataSource.isLocal()) {
					ResourceDescriptor dsDescriptor = serviceContext
							.createResourceDescriptor(ruDataSource.getLocalResource());
					descriptor.getChildren().add(dsDescriptor);
				} else {
					ResourceDescriptor rdDs = new ResourceDescriptor();
					rdDs.setWsType(ResourceDescriptor.TYPE_DATASOURCE);
					rdDs.setReferenceUri(ruDataSource.getReferenceURI());
					rdDs.setIsReference(true);
					descriptor.getChildren().add(rdDs);
				}
			}

			// Get the main jrxml...
			ResourceReference ruMainReportResourceResourceReference = reportUnit
					.getMainReport();

			ResourceDescriptor ruMainReportResourceDescriptor = null;

//			if (ruMainReportResourceResourceReference != null) {
//				if (ruMainReportResourceResourceReference.isLocal()) {
//					ruMainReportResourceDescriptor = serviceContext
//							.createResourceDescriptor(ruMainReportResourceResourceReference
//									.getLocalResource());
//					// ruMainReportResourceDescriptor.setLocal(true);
//				} else {
//					ruMainReportResourceDescriptor = serviceContext
//							.createResourceDescriptor(ruMainReportResourceResourceReference
//									.getReferenceURI());
//					// This is a trick to say that this is really a reference,
//					// and the resource is not local!
//					ruMainReportResourceDescriptor
//							.setReferenceUri(ruMainReportResourceResourceReference
//									.getReferenceURI());
//					ruMainReportResourceDescriptor.setIsReference(true);
//					// ruMainReportResourceDescriptor.setLocal(false);
//				}
//
//				ruMainReportResourceDescriptor.setMainReport(true);
//				descriptor.getChildren().add(ruMainReportResourceDescriptor);
//			}

			if (ruMainReportResourceResourceReference != null) {
				if (ruMainReportResourceResourceReference.isLocal()) {
					ruMainReportResourceDescriptor = serviceContext
							.createResourceDescriptor(ruMainReportResourceResourceReference
									.getLocalResource());
					// ruMainReportResourceDescriptor.setLocal(true);
				} else {
					ruMainReportResourceDescriptor = serviceContext
							.createResourceDescriptor(ruMainReportResourceResourceReference
									.getReferenceURI());
					// ruMainReportResourceDescriptor.setLocal(false);
				}

				ruMainReportResourceDescriptor.setMainReport(true);
				descriptor.getChildren().add(ruMainReportResourceDescriptor);
			}

			List inputControls = null;
            if(reportLoadingService != null) {
                //TODO consider placing runtime ICs in separate place in order to distinguish them from report's own ICs
                inputControls = reportLoadingService.getInputControlReferences(ExecutionContextImpl.getRuntimeExecutionContext(), reportUnit);
            } else {
                inputControls = reportUnit.getInputControls();
            }
			if (inputControls != null && !inputControls.isEmpty()) {
				for (Iterator it = inputControls.iterator(); it.hasNext();) {

					ResourceReference resRef = (ResourceReference) it.next();
					ResourceDescriptor ruResourceDescriptor = null;
					if (resRef.isLocal()) {
						ruResourceDescriptor = serviceContext
								.createResourceDescriptor(resRef
										.getLocalResource());
						// ruMainReportResourceDescriptor.setLocal(true);
					} else {
						ruResourceDescriptor = serviceContext
								.createResourceDescriptor(resRef
										.getReferenceURI());
						// ruMainReportResourceDescriptor.setLocal(false);
					}

					if (ruResourceDescriptor != null) {
						descriptor.getChildren().add(ruResourceDescriptor);
					}
				}
			}

			List resources = reportUnit.getResources();
			if (resources != null && !resources.isEmpty()) {
				for (Iterator it = resources.iterator(); it.hasNext();) {

					ResourceReference resRef = (ResourceReference) it.next();
					ResourceDescriptor ruResourceDescriptor = null;
					if (resRef.isLocal()) {
						ruResourceDescriptor = serviceContext
								.createResourceDescriptor(resRef
										.getLocalResource());
						// ruMainReportResourceDescriptor.setLocal(true);
					} else {
						ruResourceDescriptor = serviceContext
								.createResourceDescriptor(resRef
										.getReferenceURI());
						// ruMainReportResourceDescriptor.setLocal(false);
					}

					if (ruResourceDescriptor != null
							&& !ruMainReportResourceDescriptor
									.getUriString()
									.equals(ruResourceDescriptor.getUriString())) {
						descriptor.getChildren().add(ruResourceDescriptor);
					}
				}
			}
		}
	}

	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor,
			RepositoryServiceContext serviceContext) throws WSException {
		ReportUnit reportUnit = (ReportUnit) resource;

		Boolean alwaysPrompt = descriptor.getResourcePropertyValueAsBoolean(
				ResourceDescriptor.PROP_RU_ALWAYS_PROPMT_CONTROLS);
		if (alwaysPrompt != null) {
			reportUnit.setAlwaysPromptControls(alwaysPrompt.booleanValue());
		}
		
		Integer controlsLayout = descriptor.getResourcePropertyValueAsInteger(
				ResourceDescriptor.PROP_RU_CONTROLS_LAYOUT);
		if (controlsLayout != null) {
			reportUnit.setControlsLayout(controlsLayout.byteValue());
		}
		
		String controlsView = descriptor
				.getResourcePropertyValue(ResourceDescriptor.PROP_RU_INPUTCONTROL_RENDERING_VIEW);
		if (controlsView != null)
			reportUnit.setInputControlRenderingView(controlsView);

		String renderingView = descriptor
				.getResourcePropertyValue(ResourceDescriptor.PROP_RU_REPORT_RENDERING_VIEW);
		if (renderingView != null)
			reportUnit.setReportRenderingView(renderingView);

		List children = descriptor.getChildren();
		for (int i = 0; i < children.size(); ++i) {
			ResourceDescriptor childResource = (ResourceDescriptor) children
					.get(i);
			if (serviceContext.getHandlerRegistry().typeExtends(
					childResource.getWsType(),
					ResourceDescriptor.TYPE_DATASOURCE)) {
				if (childResource.getIsReference()) {
					reportUnit.setDataSourceReference(childResource
							.getReferenceUri());
				} else {
					ReportDataSource dataSource = (ReportDataSource) toChildResource(
							childResource, serviceContext);
					reportUnit.setDataSource(dataSource);
				}
			} else if (childResource.getWsType().equals(ResourceDescriptor.TYPE_JRXML) && childResource.isMainReport()) {
				String referenceURI = childResource.getReferenceUri();
				if (referenceURI != null && referenceURI.trim().length() > 0) {
					reportUnit.setMainReportReference(referenceURI);
				} else {
					FileResource fileResource = (FileResource) toChildResource(
							childResource, serviceContext);
					fileResource.setFileType(FileResource.TYPE_JRXML);

					reportUnit.setMainReport(fileResource);
				}
			}
		}
	}

	public Resource setSubResource(Resource parent, ServiceRequest request)
			throws WSException {
		ReportUnit reportUnit = (ReportUnit) parent;
		ResourceDescriptor descriptor = request.getRequestDescriptor();

		Resource subResource = null;
		if (descriptor.getIsNew()) {
			String wsType = descriptor.getWsType();
			if (wsType.equals(ResourceDescriptor.TYPE_REPORTUNIT)) {
				// nothing
			} else if (request.getContext().getHandlerRegistry().typeExtends(
					wsType, ResourceDescriptor.TYPE_DATASOURCE)) {
				ReportDataSource datasource = (ReportDataSource) toChildResource(
						descriptor, request.getContext());
				reportUnit.setDataSource(datasource);

				subResource = datasource;
			} else if (wsType.equals(ResourceDescriptor.TYPE_DATA_TYPE)) {
				throw new WSException(WSException.GENERAL_ERROR2, request
						.getContext().getMessage(
								"webservices.error.addingDTtoRU", null));
			} else if (wsType.equals(ResourceDescriptor.TYPE_LOV)) {
				throw new WSException(WSException.GENERAL_ERROR2, request
						.getContext().getMessage(
								"webservices.error.addingLOVtoRU", null));
			} else if (wsType.equals(ResourceDescriptor.TYPE_QUERY)) {
				throw new WSException(WSException.GENERAL_ERROR2, request
						.getContext().getMessage(
								"webservices.error.addingQuerytoRU", null));
			} else if (wsType
					.equals(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION)) {
				XMLAConnection datasource = (XMLAConnection) toChildResource(
						descriptor, request.getContext());
				reportUnit.setDataSource(datasource);

				subResource = datasource;
			} else if (wsType.equals(ResourceDescriptor.TYPE_REFERENCE)
					|| wsType.equals(ResourceDescriptor.TYPE_IMAGE)
					|| wsType.equals(ResourceDescriptor.TYPE_FONT)
					|| wsType.equals(ResourceDescriptor.TYPE_CLASS_JAR)
					|| wsType.equals(ResourceDescriptor.TYPE_JRXML)
					|| wsType.equals(ResourceDescriptor.TYPE_RESOURCE_BUNDLE)
					|| wsType.equals(ResourceDescriptor.TYPE_STYLE_TEMPLATE)) {
				FileResource fileResource = (FileResource) toChildResource(
						descriptor, request.getContext());
				reportUnit.addResource(fileResource);

				subResource = fileResource;
			} else if (wsType.equals(ResourceDescriptor.TYPE_INPUT_CONTROL)) {
				if (descriptor.getIsReference()) {
					String referenceUri = descriptor.getReferenceUri();
					log.info("Adding control to RU: " + referenceUri);

					InputControl inputControl = (InputControl) request
							.getContext().getRepository().getResource(null,
									referenceUri, InputControl.class);
					if (inputControl == null) {
						throw new WSException(
								WSException.REFERENCED_RESOURCE_NOT_FOUND,
								request
										.getContext()
										.getMessage(
												"webservices.error.resourceNotFoundOrInvalid",
												new Object[] { referenceUri }));
					}

					reportUnit.addInputControlReference(referenceUri);

					subResource = inputControl;
				} else {
					InputControl inputControl = (InputControl) toChildResource(
							descriptor, request.getContext());
					reportUnit.addInputControl(inputControl);

					subResource = inputControl;
				}
			} else {
				throw new WSException(
						WSException.GENERAL_REQUEST_ERROR,
						request
								.getContext()
								.getMessage(
										"webservices.error.generic.not.supported",
										null));
			}
		} else {
			// We assume the resource is a FileResource...
			FileResource resource = (FileResource) getReportUnitResource(
					descriptor, reportUnit, request.getContext());

			if (resource == null) {
				throw new WSException(WSException.GENERAL_ERROR2,
						"webservices.error.resourceNotFound");
			}

			RepositoryResourceHandler handler = (RepositoryResourceHandler) request
					.getContext().getHandlerRegistry().getHandler(
							descriptor.getWsType());
			handler.copyToResource(resource, descriptor, request.getContext());

			if (descriptor.isMainReport()
					&& reportUnit.getMainReport().isLocal()) {
				reportUnit.setMainReport(resource);
			}

			subResource = resource;
		}

		return subResource;
	}

	/**
	 * Look for a resource pointed by the ResourceDescriptor in a report unit.
	 * 
	 */
	private Resource getReportUnitResource(ResourceDescriptor descriptor,
			ReportUnit reportUnit, RepositoryServiceContext serviceContext) {
		Resource resource = null;
		if (descriptor.isMainReport()) {
			ResourceReference resRef = reportUnit.getMainReport();
			Resource jrxmlResource = null;
			if (resRef.isLocal()) {
				jrxmlResource = resRef.getLocalResource();
			} else {
				jrxmlResource = serviceContext.getRepository().getResource(
						null, resRef.getReferenceURI());
			}

			resource = jrxmlResource;
		} else {
			List resources = reportUnit.getResources();
			for (int i = 0; i < resources.size(); ++i) {
				ResourceReference resRef = (ResourceReference) resources.get(i);
				Resource genericResource = null;
				if (resRef.isLocal()) {
					genericResource = resRef.getLocalResource();
				} else {
					genericResource = serviceContext.getRepository()
							.getResource(null, resRef.getReferenceURI());
				}
				if (genericResource.getURIString().equals(
						descriptor.getUriString())) {
					resource = genericResource;
					break;
				}
			}
		}

		return resource;
	}

	public void deleteSubResource(Resource parent,
			ResourceDescriptor childDescriptor,
			RepositoryServiceContext serviceContext) throws WSException {
		ReportUnit reportUnit = (ReportUnit) parent;

		if (childDescriptor.isMainReport()) {
			throw new WSException(WSException.GENERAL_ERROR2, serviceContext
					.getMessage("webservices.error.deletingMainJrxml", null));
		}

		log.debug("Delete: resource in reportUnit: "
				+ childDescriptor.getWsType());
		if (childDescriptor.getWsType().equals(
				ResourceDescriptor.TYPE_INPUT_CONTROL)) {
			List resources = reportUnit.getInputControls();
			for (int i = 0; i < resources.size(); ++i) {
				ResourceReference resRef = (ResourceReference) resources.get(i);
				if (resRef.isLocal()
						&& childDescriptor.getName().equals(
								resRef.getLocalResource().getName())) {
					reportUnit.removeInputControl(i);
					break;
				}
				else if (!resRef.isLocal()
				        && childDescriptor.getUriString().equals( resRef.getReferenceURI() ))
				{
	  			reportUnit.removeInputControl(i);
					break;
				}
			}
		} else {
			List resources = reportUnit.getResources();
			for (int i = 0; i < resources.size(); ++i) {
				ResourceReference resRef = (ResourceReference) resources.get(i);
				if (resRef.isLocal()
						&& childDescriptor.getName().equals(
								resRef.getLocalResource().getName())) {
					reportUnit.removeResourceLocal(resRef.getLocalResource()
							.getName());
					break;
				} else if (!resRef.isLocal()
						&& childDescriptor.getUriString().equals(
								resRef.getReferenceURI() )) {
							reportUnit.removeResourceReference(childDescriptor.getUriString());
					break;
				}
			}
		}

		serviceContext.getRepository().saveResource(null, reportUnit);
	}

}
