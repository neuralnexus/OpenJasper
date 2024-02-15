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

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
//import com.jaspersoft.jasperserver.multipleTenancy.MultiTenancyConfiguration;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: TenantExporter.java 15533 2009-01-14 18:34:04Z lucian $
 */
public class TenantExporter extends BaseExporterModule
{
	
	private static final Log log = LogFactory.getLog(TenantExporter.class);
	
	private TenantModuleConfiguration moduleConfiguration;
	
	private boolean exportTenants;
	
	protected Queue<String> tenantIdQueue;

    public void init(ExporterModuleContext moduleContext)
	{
		super.init(moduleContext);
		
		exportTenants = exportEverything || hasParameter(moduleConfiguration.getTenantsArgument());
		
		if (log.isDebugEnabled())
		{
			log.debug("Exporting tenants: " + exportTenants);
		}
		
		tenantIdQueue = new LinkedList<String>();
	}

/*
	protected String getRootTenantId()
	{
		return getMultiTenancyConfiguration().getRootTenantId();
	}

*/
/*
	protected DefaultTenantExportConfiguration getMultiTenancyConfiguration()
	{
		return moduleConfiguration.getTenantExportConfiguration();
	}
	
*/
	protected TenantService getTenantService()
	{
		return moduleConfiguration.getTenantService();
	}
	
	@Override
	protected boolean isToProcess()
	{
		return exportTenants;
	}

	public void process()
	{
		mkdir(moduleConfiguration.getTenantsDirectory());
	
		String rootTenantId = moduleConfiguration.getTenantExportConfiguration().getRootTenantId();
		Tenant rootTenant = getTenantService().getTenant(executionContext, rootTenantId);
		if (rootTenant == null)
		{
			commandOut.info("Root tenant \"" + rootTenantId + "\" not found, skipping tenants");
		}
		else
		{
			process(rootTenant);
			
/* asd
			// process subtenants
			while(!tenantIdQueue.isEmpty())
			{
				String tenantId = tenantIdQueue.poll();
				processSubTenants(tenantId);
			}
*/
		}
	}

	protected void process(Tenant tenant)
	{
		commandOut.info("Exporting tenant " + tenant.getId() 
				+ " (" + tenant.getTenantUri() + ")");
		
		// serialize the tenant to XML
		serialize(tenant, 
				moduleConfiguration.getTenantsDirectory(),
				getTenantFileName(tenant), 
				moduleConfiguration.getTenantSerializer());
		
		// add an entry to the export index.xml
		addTenantIndexEntry(tenant);
		
		// add the tenant to the queue to process subtenants
		tenantIdQueue.add(tenant.getId());
	}

	protected String getTenantFileName(Tenant tenant)
	{
		return tenant.getId() + ".xml";
	}

	protected void addTenantIndexEntry(Tenant tenant)
	{
		Element indexElement = getIndexElement();
		Element tenantElement = indexElement.addElement(
				moduleConfiguration.getTenantIndexElement());
		tenantElement.addText(tenant.getId());
	}
	
/* asd
	protected void processSubTenants(String tenantId)
	{
		List subTenants = getModuleConfiguration().getTenantService().getSubTenantList(
				executionContext, tenantId);
		for (Iterator it = subTenants.iterator(); it.hasNext();)
		{
			Tenant subTenant = (Tenant) it.next();
			process(subTenant);
		}
	}

*/
	public TenantModuleConfiguration getModuleConfiguration()
	{
		return moduleConfiguration;
	}

	public void setModuleConfiguration(TenantModuleConfiguration moduleConfiguration)
	{
		this.moduleConfiguration = moduleConfiguration;
	}

}
