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

package com.jaspersoft.jasperserver.ws.axis2.repository;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.axis2.RepositoryServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author Vladimir Sabadosh
 * @version $Id$
 */
public class OlapUnitHandler extends RepositoryResourceHandler {

    private final static Log log = LogFactory.getLog(OlapUnitHandler.class);

    public Class getResourceType() {
        return OlapUnit.class;
    }

    protected void doDescribe(Resource resource, ResourceDescriptor descriptor,
            Map arguments, RepositoryServiceContext serviceContext) {
        descriptor.setWsType(ResourceDescriptor.TYPE_OLAPUNIT);
    }


    protected void updateResource(Resource resource,
            ResourceDescriptor descriptor, RepositoryServiceContext newParam) {
        // nothing
    }

}
