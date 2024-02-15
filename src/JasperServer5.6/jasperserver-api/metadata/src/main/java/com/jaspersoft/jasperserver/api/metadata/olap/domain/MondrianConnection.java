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
package com.jaspersoft.jasperserver.api.metadata.olap.domain;

/**
 * @author swood
 *
 */
import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

@JasperServerAPI
public interface MondrianConnection extends OlapClientConnection, ReportDataSource {

    public ResourceReference getSchema();

    public void setSchema( FileResource f );

    public void setSchema(ResourceReference schemaReference);
	
    public void setSchemaReference(String referenceURI);

    // this should be JdbcDataSource, once that interface is refactored
    public ResourceReference getDataSource();
	
    public void setDataSource(ResourceReference dataSourceReference);
	
    public void setDataSource(ReportDataSource dataSource);
	
    public void setDataSourceReference(String referenceURI);

}

