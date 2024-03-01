/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class MondrianConnectionResourceValidator<ConnectionType extends MondrianConnection> extends GenericResourceValidator<ConnectionType> {
    @Override
    protected void internalValidate(ExecutionContext ctx, ConnectionType resource, List<Exception> errors, Map<String, String[]> additionalParameters) {
        if (resource.getDataSource() == null || empty(resource.getDataSource().getTargetURI())) {
            errors.add(new MandatoryParameterNotFoundException("dataSource"));
        }

        final ResourceReference schema = resource.getSchema();
        if (schema == null || empty(schema.getTargetURI())) {
            errors.add(new MandatoryParameterNotFoundException("schema"));
        } else if(schema.isLocal() && FileResource.VERSION_NEW == schema.getLocalResource().getVersion()
                && ((FileResource)schema.getLocalResource()).getData() == null){
            // if file resource doesn't exist yet, then it is local file creation.
            // in this case file content is mandatory
            errors.add(new MandatoryParameterNotFoundException("schema.content"));

        }
    }
}
