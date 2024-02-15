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
package com.jaspersoft.jasperserver.remote.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id: BeanDataSourceHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class BeanDataSourceHandler extends RepositoryResourceHandler {

    public Class getResourceType() {
        return BeanReportDataSource.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException
    {
        BeanReportDataSource dsResource = (BeanReportDataSource) resource;
        descriptor.setBeanName(dsResource.getBeanName());
        descriptor.setBeanMethod(dsResource.getBeanMethod());
        descriptor.setWsType(ResourceDescriptor.TYPE_DATASOURCE_BEAN);
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options)
    {
        super.updateResource(resource, descriptor, options);

        BeanReportDataSource beanReportDataSource = (BeanReportDataSource) resource;
        beanReportDataSource.setBeanName(descriptor.getBeanName());
        beanReportDataSource.setBeanMethod((descriptor.getBeanMethod() != null && descriptor.getBeanMethod().trim().length() > 0) ? descriptor.getBeanMethod() : null);
    }

    
}
