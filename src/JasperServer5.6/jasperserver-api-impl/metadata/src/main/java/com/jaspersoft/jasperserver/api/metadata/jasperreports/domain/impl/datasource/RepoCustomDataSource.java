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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import java.util.HashMap;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

/**
 * @author swood
 * 
 * @hibernate.joined-subclass table="BeanDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoCustomDataSource extends RepoDataSource implements
		RepoReportDataSource {
	private static final String CDS_NAME_PROPERTY = "_cds_name";
	public static final String PASSWORD_DS_PARAM = "password";

	private String serviceClass;

	private Map propertyMap;

	public RepoCustomDataSource() {
		super();
	}

	protected Class getClientItf() {
		return CustomReportDataSource.class;
	}

	public Map getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

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
	}

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

		setPropertyMap(properties);
		setServiceClass(ds.getServiceClass());
	}
}
