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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: RepoVirtualDataSource.java 47331 2014-07-18 09:13:06Z kklein $
 * 
 * @hibernate.joined-subclass table="VirtualDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoVirtualDataSource extends RepoDataSource implements RepoReportDataSource {

	private String timezone;
    private Map<String, RepoResource> dataSourceUriMap;

	public RepoVirtualDataSource() {
        super();
	}

    public Map<String, RepoResource> getDataSourceUriMap() {
        return dataSourceUriMap;
    }

    public void setDataSourceUriMap(Map<String, RepoResource> dataSourceUriMap) {
        this.dataSourceUriMap = dataSourceUriMap;
    }

    public String getTimezone()
	{
		return timezone;
	}

	public void setTimezone(String timezone)
	{
		this.timezone = timezone;
	}

	protected Class getClientItf() {
		return VirtualReportDataSource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		VirtualReportDataSource ds = (VirtualReportDataSource) clientRes;
        if (dataSourceUriMap != null) {
            Map clientDataSourceUriMap = new HashMap<String, ResourceReference>();
            for (Map.Entry<String, RepoResource> entry : dataSourceUriMap.entrySet()) {
                // convert repo resource to resource reference
                ResourceReference resourceReference = getClientReference(entry.getValue(), resourceFactory);
                clientDataSourceUriMap.put(entry.getKey(), resourceReference);
            }
            ds.setDataSourceUriMap(clientDataSourceUriMap);
        }
		ds.setTimezone(getTimezone());
    }

	protected void copyFrom(Resource clientRes,
			ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		VirtualReportDataSource ds = (VirtualReportDataSource) clientRes;
        if (ds.getDataSourceUriMap() == null) {
            setDataSourceUriMap(null);
        } else {
            Map repoDataSourceUriMap = new HashMap<String, RepoResource>();
            for (Map.Entry<String, ResourceReference> entry : ds.getDataSourceUriMap().entrySet()) {
                // get resource from reference
                RepoResource repoResource = getReference(entry.getValue(), RepoResource.class, referenceResolver);
                repoDataSourceUriMap.put(entry.getKey(), repoResource);

            }
            setDataSourceUriMap(repoDataSourceUriMap);
        }
		setTimezone(ds.getTimezone());
	}
}
