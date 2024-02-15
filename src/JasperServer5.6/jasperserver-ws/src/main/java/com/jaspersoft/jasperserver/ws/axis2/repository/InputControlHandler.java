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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.InputControlQueryDataRow;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;
import com.jaspersoft.jasperserver.ws.axis2.WSException;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author gtoffoli
 * @version $Id: InputControlHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class InputControlHandler extends RepositoryResourceHandler {

	public Class getResourceType() {
		return InputControl.class;
	}

	protected static final Log log = LogFactory
			.getLog(InputControlHandler.class);

	protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
			Map arguments, RepositoryServiceContext serviceContext) throws WSException {
		InputControl fileResource = (InputControl) resource;
		descriptor.setWsType(ResourceDescriptor.TYPE_INPUT_CONTROL);
		descriptor.setHasData(false);
		descriptor.setIsReference(false);

		descriptor.setMandatory(fileResource.isMandatory());
		descriptor.setReadOnly(fileResource.isReadOnly());
        descriptor.setVisible(fileResource.isVisible());

		descriptor.setControlType(fileResource.getType());

		ResourceReference childReference = null;
		if (descriptor.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_VALUE) {
			childReference = fileResource.getDataType();
		} else if (descriptor.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES
				|| descriptor.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO
				|| descriptor.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES
				|| descriptor.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX) {
			childReference = fileResource.getListOfValues();
		} else if (descriptor.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_QUERY
				|| descriptor.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_QUERY_RADIO
				|| descriptor.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY
				|| descriptor.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY_CHECKBOX) {
			log.info("Adding query and columns: "
					+ fileResource.getQueryValueColumn());
			childReference = fileResource.getQuery();
			descriptor.setQueryValueColumn(fileResource.getQueryValueColumn());
			descriptor.setQueryVisibleColumns(fileResource
					.getQueryVisibleColumns());
		}

		if (childReference != null) {
			ResourceDescriptor childRd = null;

			if (childReference.isLocal()) {
				childRd = serviceContext
						.createResourceDescriptor(childReference
								.getLocalResource());
			} else {
				// childRd =
				// getResourceDescriptor(childReference.getReferenceLookup().getURIString());
				childRd = new ResourceDescriptor();
				childRd.setWsType(ResourceDescriptor.TYPE_REFERENCE);
				childRd.setReferenceUri(childReference.getReferenceURI());
			}

			if (childRd != null) {
				descriptor.getChildren().add(childRd);
			}
		}

		if (arguments != null
				&& arguments.containsKey(Argument.IC_GET_QUERY_DATA)) {
			// get the datasource against wich get the query data...
			String dsUri = (String) arguments.get(Argument.IC_GET_QUERY_DATA);

			java.util.List data = getInputControlItems(fileResource, dsUri,
					serviceContext, (Map) arguments.get(Argument.PARAMS_ARG));

			descriptor.setQueryData(data);
		}
	}

	/*
	 * execute a query against a named datasource to get the data for an
	 * InputControl. Returns a list of InputControlQueryDataRow
	 * 
	 */
	protected java.util.List getInputControlItems(InputControl control,
			String datasourceUri, RepositoryServiceContext serviceContext, Map params)
			throws JSException {

		ResourceReference fallbackDataSource = null;
		if (datasourceUri != null && datasourceUri.trim().length() > 0) {
			fallbackDataSource = new ResourceReference(datasourceUri);
		}
		
		ResourceReference queryRef = control.getQuery();
		
		String valueColumn = control.getQueryValueColumn();
		String[] visibleColumns = control.getQueryVisibleColumns();
		
        Map parameters = (params == null) ? new HashMap() : params;
        // TODO : read REPORT_MAX_COUNT from configuration
        parameters.put(JRParameter.REPORT_MAX_COUNT, new Integer(100000));

		OrderedMap results = serviceContext.getEngine().executeQuery(null, 
				queryRef, valueColumn, visibleColumns, fallbackDataSource,
				parameters);
		
		List rows;
		if (results == null || results.isEmpty()) {
			rows = new ArrayList(0);
		} else {
			rows = new ArrayList(results.size());
			for (Iterator it = results.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Object keyValue = entry.getKey();
				String[] columnValues = (String[]) entry.getValue();
				
                InputControlQueryDataRow ic = new InputControlQueryDataRow();
                ic.setValue(keyValue);
                
                for (int i = 0; i < columnValues.length; i++) {
					String value = columnValues[i];
                    ic.getColumnValues().add( (value != null) ? value : "" );
				}
                rows.add(ic);
			}
		}

        return rows;
	}

	protected void updateResource(Resource resource,
			ResourceDescriptor descriptor, RepositoryServiceContext serviceContext) throws WSException {
		InputControl inputControl = (InputControl) resource;

		inputControl.setMandatory(descriptor.isMandatory());
		inputControl.setReadOnly(descriptor.isReadOnly());
        inputControl.setVisible(descriptor.isVisible());
        
		inputControl.setType(descriptor.getControlType());

		// Look in children...
		if (inputControl.getType() == ResourceDescriptor.IC_TYPE_SINGLE_VALUE) {
			// We have to set the datatype...
			if (descriptor.getChildren().size() == 0) {
				throw new WSException(WSException.GENERAL_ERROR2,
						serviceContext.getMessage(
								"webservices.error.missingDataType", null));
			}
			ResourceDescriptor rd = (ResourceDescriptor) descriptor
					.getChildren().get(0);
			if (rd.getWsType().equals(ResourceDescriptor.TYPE_REFERENCE)) {
				inputControl.setDataTypeReference(rd.getReferenceUri());
			} else {
				DataType dataType = (DataType) toChildResource(rd, serviceContext);
				inputControl.setDataType(dataType);
			}
		}
		// Look in children...
		else if (inputControl.getType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES
				|| inputControl.getType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO
				|| inputControl.getType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES
				|| inputControl.getType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX) {

			// We have to set the datatype...
			if (descriptor.getChildren().size() == 0) {
				throw new WSException(WSException.GENERAL_ERROR2,
						serviceContext.getMessage(
								"webservices.error.missingLOV", null));
			}
			ResourceDescriptor rd = (ResourceDescriptor) descriptor
					.getChildren().get(0);
			if (rd.getWsType().equals(ResourceDescriptor.TYPE_REFERENCE)) {
				inputControl.setListOfValuesReference(rd.getReferenceUri());
			} else {
				ListOfValues lovResource = (ListOfValues) toChildResource(rd, serviceContext);
				inputControl.setListOfValues(lovResource);
			}
		} else if (inputControl.getType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_QUERY
				|| inputControl.getType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_QUERY_RADIO
				|| inputControl.getType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY
				|| inputControl.getType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY_CHECKBOX) {

			// We have to set the datatype...
			if (descriptor.getChildren().size() == 0) {
				throw new WSException(WSException.GENERAL_ERROR2,
						serviceContext.getMessage(
								"webservices.error.missingQuery", null));
			}
			ResourceDescriptor rd = (ResourceDescriptor) descriptor
					.getChildren().get(0);

			inputControl.setQueryValueColumn(descriptor.getQueryValueColumn());
			String[] visibleColumns = descriptor.getQueryVisibleColumns();

			// remove all old visible columns first...
			String[] oldColumns = inputControl.getQueryVisibleColumns();
			if (oldColumns != null) {
				for (int i = 0; i < oldColumns.length; ++i) {
					inputControl.removeQueryVisibleColumn(oldColumns[i]);
				}
			}

			for (int i = 0; i < visibleColumns.length; ++i) {
				inputControl.addQueryVisibleColumn(visibleColumns[i]);
			}

			if (rd.getWsType().equals(ResourceDescriptor.TYPE_REFERENCE)) {
				inputControl.setQueryReference(rd.getReferenceUri());
			} else {
				Query queryResource = (Query) toChildResource(rd, serviceContext);
				inputControl.setQuery(queryResource);
			}
		}
	}

}
