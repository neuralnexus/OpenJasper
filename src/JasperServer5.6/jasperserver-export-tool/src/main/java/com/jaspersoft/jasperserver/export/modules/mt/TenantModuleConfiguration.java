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
package com.jaspersoft.jasperserver.export.modules.mt;

import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: Configuration.java 15533 2009-01-14 18:34:04Z lucian $
 */
public class TenantModuleConfiguration
{

	private String tenantsArgument;
	private String tenantsDirectory;
	private String tenantIndexElement;
	
	private DefaultTenantExportConfiguration tenantExportConfiguration;
	private TenantService tenantService;
	private ObjectSerializer tenantSerializer;

	public ObjectSerializer getTenantSerializer()
	{
		return tenantSerializer;
	}

	public void setTenantSerializer(ObjectSerializer tenantSerializer)
	{
		this.tenantSerializer = tenantSerializer;
	}

	public String getTenantsDirectory()
	{
		return tenantsDirectory;
	}

	public void setTenantsDirectory(String tenantsDirectory)
	{
		this.tenantsDirectory = tenantsDirectory;
	}

	public String getTenantIndexElement()
	{
		return tenantIndexElement;
	}

	public void setTenantIndexElement(String tenantIndexElement)
	{
		this.tenantIndexElement = tenantIndexElement;
	}

	public TenantService getTenantService()
	{
		return tenantService;
	}

	public void setTenantService(TenantService tenantService)
	{
		this.tenantService = tenantService;
	}

    public DefaultTenantExportConfiguration getTenantExportConfiguration() {
        return tenantExportConfiguration;
    }

    public void setTenantExportConfiguration(DefaultTenantExportConfiguration tenantExportConfiguration) {
        this.tenantExportConfiguration = tenantExportConfiguration;
    }

    public String getTenantsArgument()
	{
		return tenantsArgument;
	}

	public void setTenantsArgument(String tenantsArgument)
	{
		this.tenantsArgument = tenantsArgument;
	}

}
