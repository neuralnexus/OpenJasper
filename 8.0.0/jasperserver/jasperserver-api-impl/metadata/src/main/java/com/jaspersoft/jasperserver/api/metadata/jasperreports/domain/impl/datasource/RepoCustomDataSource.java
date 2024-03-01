/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author swood
 * 
 * @hibernate.joined-subclass table="BeanDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoCustomDataSource extends RepoDataSource implements
		RepoReportDataSource, Serializable {
	/**
	 * Thanks, Eclipse
	 */
	private static final long serialVersionUID = 1L;
	public static final String CDS_NAME_PROPERTY = "_cds_name";
	public static final String PASSWORD_DS_PARAM = "password";

	private String serviceClass;

	private Set<RepoCustomDataSourceProperty> properties;

	private Map<String, RepoResource> resources;

	public RepoCustomDataSource() {
		super();
	}

	public Set<RepoCustomDataSourceProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<RepoCustomDataSourceProperty> properties) {
		this.properties = properties;
	}

	@SuppressWarnings("rawtypes")
	protected Class getClientItf() {
		return CustomReportDataSource.class;
	}

	/**
	 * Unwind property set into property map and return it.
	 * This method is needed to comply with other Repo resources.
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getPropertyMap() {
		// if there are no properties return empty map
		if(getProperties()==null){
			return Collections.EMPTY_MAP;
		}
		// otherwise do a quicky
		HashMap map = new HashMap(getProperties().size());
		for(RepoCustomDataSourceProperty p:getProperties()){
			// JRS-16983 DD: Oracle: Mongo DB datasource cannot be imported
			// property map value should not be NULL, reset it back to empty string
			// This is an issue with Oracle hibernate: It treats empty string to null.
			map.put(p.getName(), p.getValue() != null ? p.getValue() : "");
		}
		return map;
	}

	public Map<String, RepoResource> getResources() {
		return resources;
	}

	public void setResources(Map<String, RepoResource> resources) {
		this.resources = resources;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);

		CustomReportDataSource ds = (CustomReportDataSource) clientRes;

		Map aPropertyMap = new HashMap(getPropertyMap());

            String password = (String) aPropertyMap.get(PASSWORD_DS_PARAM);
            if (password != null && password.trim().length() > 0) {
                aPropertyMap.put(PASSWORD_DS_PARAM, PasswordCipherer.getInstance().decodePassword(password));
            }
            // set ds name from property
            ds.setDataSourceName((String) aPropertyMap.get(CDS_NAME_PROPERTY));
            ds.setPropertyMap(aPropertyMap);
		ds.setServiceClass(getServiceClass());
		final Map<String, RepoResource> resourcesMap = getResources();
		if(resourcesMap != null && !resourcesMap.isEmpty()){
			final HashMap<String, ResourceReference> clientResources = new HashMap<String, ResourceReference>(resourcesMap.size());
			for(String key : resourcesMap.keySet()){
				clientResources.put(key, getClientReference(resourcesMap.get(key), resourceFactory));
			}
			ds.setResources(clientResources);
		}
	}

	/**
	 * Construct Set of properties from property map. We use this method internally in copyFrom method
	 * because other resources have property map and this one doesn't have map, instead it has set of properties
	 * @param propertyMap
	 */
	private void setPropertiesFromMap(Map<Object, Object> propertyMap){
		// holder of processed properties
		Set<String> processed = new HashSet<String>();
		
		// if there is an existing property map
		if(properties!=null){
			// interate through properties
			for(RepoCustomDataSourceProperty property:properties){
				Object existingValue = propertyMap==null? null: propertyMap.get(property.getName());
				if(existingValue != null){
					// and assign new value for the existing property
					property.setValue((String)existingValue);
					// mark this property as processed (need this mark below)
					processed.add(property.getName());
				} else {
					// or remove property if it doesn't exist in new property map
					properties.remove(property);
				}
			}
		} else if( propertyMap!=null && !propertyMap.isEmpty()){
			// otherwise construct new property set if we have are processing non empty property map
			properties = new HashSet<RepoCustomDataSourceProperty>(propertyMap.size());
		} else { // otherwise (we do not have existing properties and we are processing empty property map) we're done
			return;
		}
		
		if(propertyMap != null){
			// iterate through remaining propertyMap
			for(Map.Entry<Object, Object> entry: propertyMap.entrySet()){
				// if we haven't processed this property above (i.e. if it isn't pre-existing)
				if(!processed.contains(entry.getKey())){
					// construct new property
					RepoCustomDataSourceProperty newProp = RepoCustomDataSourceProperty.newProperty(this);
					newProp.setName(entry.getKey().toString());
					newProp.setValue(entry.getValue().toString());
					// and add it to the set
					properties.add(newProp);
				}
			}
		}
		
		
		// we're done. out property set member is constructed.
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void copyFrom(Resource clientRes,
			ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		CustomReportDataSource ds = (CustomReportDataSource) clientRes;

		Map properties = ds.getPropertyMap() == null ? new HashMap() : ds.getPropertyMap();
		
		String password = (String) properties.get(PASSWORD_DS_PARAM);
		if (password != null && password.trim().length() > 0) {
			properties.put(PASSWORD_DS_PARAM,	PasswordCipherer.getInstance().encodePassword(password));
		}
		// store ds name as property
		String dsName = ds.getDataSourceName();
		if (dsName != null && dsName.trim().length() > 0) {
            properties.put(CDS_NAME_PROPERTY, dsName);
		}

		setPropertiesFromMap(properties);
		setServiceClass(ds.getServiceClass());
		final Map<String, ResourceReference> clientResources = ds.getResources();
		if(clientResources != null && !clientResources.isEmpty()){
			Map<String, RepoResource> serverResources = new HashMap<String, RepoResource>(clientResources.size());
			for(String key : clientResources.keySet()){
				serverResources.put(key, getReference(clientResources.get(key), RepoResource.class, referenceResolver));
			}
			setResources(serverResources);
		}
	}
}
