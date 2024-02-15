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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.CustomDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.VirtualDataSourceQueryService;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JdbcDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JndiDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.CustomDataSourceImpl;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.JdbcDataSourceImpl;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.JndiDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceAcessDeniedException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import org.springframework.security.AccessDeniedException;

import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: VirtualDataSourceHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class VirtualDataSourceHandler {

    RepositoryService repositoryService;
    VirtualDataSourceQueryService virtualDataSourceQueryService;
    private static String dataSourceSchemaSeparator = "_";

    /*
     * get the separator between data source id and schema name
     */
    public static String getDataSourceSchemaSeparator() {
        return dataSourceSchemaSeparator;
    }

    /*
     * set the separator between data source id and schema name
     */
    public void setDataSourceSchemaSeparator(String dataSourceSchemaSeparator) {
        this.dataSourceSchemaSeparator = dataSourceSchemaSeparator;
    }

    /*
     * get a handle of virtual data source query service
     */
    public VirtualDataSourceQueryService getVirtualDataSourceQueryService() {
        return virtualDataSourceQueryService;
    }

    /*
     * a hook to replace teiid with other virtual data source service (like JDBCUnity)
     * set it through spring injection
     */
    public void setVirtualDataSourceQueryService(VirtualDataSourceQueryService virtualDataSourceQueryService) {
        this.virtualDataSourceQueryService = virtualDataSourceQueryService;
    }

    /*
     * get a handle of repository service
     */
    public RepositoryService getRepositoryService()	{
		return repositoryService;
	}

     /*
     * set repository service
     */
	public void setRepositoryService(RepositoryService repository) {
		this.repositoryService = repository;
	}

    /*
     * generate sql data source for virtual data source
     */
    public javax.sql.DataSource getSqlDataSource(ExecutionContext context, VirtualReportDataSource jsDataSource) throws Exception {
        Collection<DataSource> subDataSourceList = new ArrayList<DataSource>();

        for (Map.Entry<String, ResourceReference> entry : jsDataSource.getDataSourceUriMap().entrySet()) {
            try {
                Object reportDataSource = getResource(context, entry.getValue());
                // create sub data source list
                if (reportDataSource instanceof JdbcReportDataSource) {
                    JdbcDataSource jdbcDataSource = new JdbcDataSourceImpl((JdbcReportDataSource) reportDataSource, findSchemas(jsDataSource.getSchemas(), entry.getKey()), entry.getKey(),
                            jsDataSource);
                    subDataSourceList.add(jdbcDataSource);
                }
                if (reportDataSource instanceof JndiJdbcReportDataSource) {
                    JndiDataSource jndiDataSource = new JndiDataSourceImpl((JndiJdbcReportDataSource) reportDataSource, findSchemas(jsDataSource.getSchemas(), entry.getKey()), entry.getKey(),
                            jsDataSource);
                    subDataSourceList.add(jndiDataSource);
                }
                if (reportDataSource instanceof CustomReportDataSource) {
                    CustomDataSource customDataSource = new CustomDataSourceImpl((CustomReportDataSource) reportDataSource, findSchemas(jsDataSource.getSchemas(), entry.getKey()), entry.getKey(),
                            jsDataSource);
                    subDataSourceList.add(customDataSource);
                }

            } catch (AccessDeniedException accessDeniedEx) {
                throw new JSResourceAcessDeniedException(entry.getValue(), "Data Source Access Denied: " + entry.getValue().getReferenceURI());
            }
        }
        // get the connection factory using virtual data source query service
        ConnectionFactory connectionFactory = virtualDataSourceQueryService.getConnectionFactory(subDataSourceList, jsDataSource.getURIString());
        // create javax.sql.DataSource from connection factory
        VirtualSQLDataSource virtualSQLDataSource = new VirtualSQLDataSource(connectionFactory);
        return virtualSQLDataSource;
    }

    private Object getResource(ExecutionContext context, ResourceReference resourceReference) {
        if (resourceReference.isLocal()) return resourceReference.getLocalResource();
         return repositoryService.getResource(context, resourceReference.getReferenceURI());
    }

    /*
     * get the schema list for specific sub data source
     */
    private Set<String> findSchemas(Set<String> virtualSchemaList, String dataSourceName) {
        if (virtualSchemaList == null) return null;
        Set<String> schemaList = new LinkedHashSet<String>();
        String schemaPrefix = (dataSourceName + getDataSourceSchemaSeparator()).toLowerCase();
        int beginIndex = schemaPrefix.length();
        // loop through all the schema in virtual data source
        for (String schemaName : virtualSchemaList) {
            // only search for schema with specific data source id
            if (schemaName.toLowerCase().startsWith(schemaPrefix)) schemaList.add(schemaName.substring(beginIndex));
        }
        return schemaList;
    }


}
