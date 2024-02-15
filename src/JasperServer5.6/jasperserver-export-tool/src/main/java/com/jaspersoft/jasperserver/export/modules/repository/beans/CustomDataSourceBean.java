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

package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoCustomDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CustomDataSourceBean.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class CustomDataSourceBean extends ResourceBean {

	private String serviceClass;
	private Map propertyMap;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		CustomReportDataSource ds = (CustomReportDataSource) res;
		setServiceClass(ds.getServiceClass());

		Map dsProperties = ds.getPropertyMap();
		if (dsProperties == null) {
			setPropertyMap(new HashMap());
		} else {
			Map<String, String> copyDsPropertyMap = new HashMap<String, String>(dsProperties);

			//encrypt for export
			//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
			//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
			String pwd = copyDsPropertyMap.get(RepoCustomDataSource.PASSWORD_DS_PARAM);
			if (pwd != null && pwd.trim().length() > 0)
				copyDsPropertyMap.put(RepoCustomDataSource.PASSWORD_DS_PARAM,
						ENCRYPTION_PREFIX + importExportCipher.encode(pwd) + ENCRYPTION_SUFFIX);
			setPropertyMap(copyDsPropertyMap);
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		CustomReportDataSource ds = (CustomReportDataSource) res;
		ds.setServiceClass(getServiceClass());

		final Map dsPropertyMap = getPropertyMap();
		if (dsPropertyMap == null) {
			ds.setPropertyMap(new HashMap());
		} else {
			Map<String, String> copyDsPropertyMap = new HashMap<String, String>(dsPropertyMap);

			//decrypt pwd for import. if decryption fails, set password as is; this is probably due to legacy import
			//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
			//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
			String pwd = copyDsPropertyMap.get(RepoCustomDataSource.PASSWORD_DS_PARAM);
			if (pwd != null && pwd.trim().length() > 0)
				copyDsPropertyMap.put(RepoCustomDataSource.PASSWORD_DS_PARAM,
						(pwd.startsWith(ENCRYPTION_PREFIX) && pwd.endsWith(ENCRYPTION_SUFFIX)) ?
							importExportCipher.decode(pwd.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : pwd);

			ds.setPropertyMap(copyDsPropertyMap);
		}
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public Map getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}

}
