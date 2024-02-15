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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: TenantImporter.java 16193 2009-03-11 08:18:19Z andy21ca $
 */
public class TenantImporter extends BaseImporterModule
{
	private static final Log log = LogFactory.getLog(TenantImporter.class);
	
	private TenantModuleConfiguration moduleConfiguration;
    private String updateArg;

    public List<String> process()
	{
		for (Iterator it = indexElement.elementIterator(moduleConfiguration.getTenantIndexElement());
				it.hasNext(); ) {
			Element tenantElement = (Element) it.next();
			String tenantId = tenantElement.getText();
            process(tenantId);
		}
        return null;
	}

    protected Boolean isMultitenancyFeatureSupported() {
        /* For CE deciding to make it true or false? */
        return true;
    }

    protected void process(String tenantId) {
        Tenant existingTenant = getTenantService().getTenant(executionContext, tenantId);
        if (existingTenant != null) {
            if (hasParameter(getUpdateArg())) {
                Tenant tenant = (Tenant) deserialize(getModuleConfiguration().getTenantsDirectory(),
                        getTenantFileName(tenantId),
                        getModuleConfiguration().getTenantSerializer());
                updateTenant(existingTenant, tenant);
            } else {
                commandOut.info("Tenant " + tenantId + " already exists, skipping");
            }
        } else {
            /* Checking if now we have a multi tenant environment. */
            Integer tenantsCount = getTenantService().getNumberOfTenants(executionContext);

            Boolean canImportOrganization = true;

            if (tenantsCount >= 1) {
                /* We already have organizations and at least tenant under it. */
                /* Should check if multitenancy is enabled. */
                if (!isMultitenancyFeatureSupported()) {
                    canImportOrganization = false;
                }
            }

            if (canImportOrganization) {
                if (log.isDebugEnabled()) {
                    log.debug("Deserializing tenant " + tenantId);
                }

                Tenant tenant = (Tenant) deserialize(getModuleConfiguration().getTenantsDirectory(),
                        getTenantFileName(tenantId),
                        getModuleConfiguration().getTenantSerializer());
                // In previous versions of JasperServer we did not have tenant alias and theme.
                // So, setting alias with tenant id value and default theme for such versions.
                if (tenant.getAlias() == null) {
                    tenant.setAlias(tenant.getId());
                }
                if (tenant.getTheme() == null) {
                    tenant.setTheme(getModuleConfiguration().getTenantExportConfiguration().getDefaultThemeName());
                }
                getTenantService().putTenant(executionContext, tenant);

                commandOut.info("Imported tenant " + tenantId);
            } else {
                commandOut.warn("This feature is unavailable under the current license. " +
                        "Please contact support at Jaspersoft.com for help.");
            }
        }
    }

    protected String getTenantFileName(String tenantId) {
        return tenantId + ".xml";
    }

    protected TenantService getTenantService() {
        return getModuleConfiguration().getTenantService();
    }

    private void updateTenant(Tenant existing, Tenant newTenant) {
        existing.setParentId(newTenant.getParentId());
        existing.setTenantDesc(newTenant.getTenantDesc());
        existing.setTenantFolderUri(newTenant.getTenantFolderUri());
        existing.setTenantName(newTenant.getTenantName());
        existing.setTenantNote(newTenant.getTenantNote());
        existing.setTenantUri(existing.getTenantUri());
        existing.setAttributes(newTenant.getAttributes());

        if (newTenant.getAlias() != null) {
            existing.setAlias(newTenant.getAlias());
        }
        if (newTenant.getTheme() != null) {
            existing.setTheme(newTenant.getTheme());
        }
        getTenantService().putTenant(executionContext, existing);

        commandOut.info("Updated tenant " + existing.getId());
    }

    public TenantModuleConfiguration getModuleConfiguration() {
        return moduleConfiguration;
    }

    public void setModuleConfiguration(TenantModuleConfiguration moduleConfiguration) {
        this.moduleConfiguration = moduleConfiguration;
    }

    public String getUpdateArg() {
        return updateArg;
    }

    public void setUpdateArg(String updateArg) {
        this.updateArg = updateArg;
    }
}
