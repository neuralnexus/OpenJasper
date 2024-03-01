/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.remote.validation;

import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.QUERY_DATASOURCE_ACCESS_DENIED;
import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.QUERY_DATASOURCE_NOT_FOUND;
import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.QUERY_DATASOURCE_TYPE_NOT_SUPPORTED;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.executions.AbstractClientExecution;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceable;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.builders.DefaultMessageApplier;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;

/**
 * <p></p>
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
@SuppressWarnings("rawtypes")
@Service
public class BaseClientExecutionValidator implements ClientValidator<AbstractClientExecution> {
    @javax.annotation.Resource(name = "concreteRepository")
    private RepositoryService repositoryService;

    @javax.annotation.Resource(name = "resourceConverterProvider")
    private ResourceConverterProvider resourceConverterProvider;

    @javax.annotation.Resource
    private DefaultMessageApplier defaultMessageApplier;

    @Override
    public List<Exception> validate(AbstractClientExecution value) {
        List<Exception> exceptions = new ArrayList<Exception>();

        if (value.getDataSource() != null && value.getDataSource() instanceof ClientReference) {
            validate(value.getDataSource().getUri(), exceptions);
        }

        return exceptions;
    }

    private void validate(String dataSourceUri, List<Exception> exceptions) {
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext();

        if (StringUtils.isEmpty(dataSourceUri)) {
            exceptions.add(new MandatoryParameterNotFoundException("reference.uri"));
        } else {
            try {
                Resource resource;

                // Do not allow folders
                if (repositoryService.folderExists(context, dataSourceUri)) {
                    exceptions.add(badRequestException(QUERY_DATASOURCE_TYPE_NOT_SUPPORTED.createDescriptor(dataSourceUri)));

                    // Used "getResource()" call instead of "resourceExists()" because it
                    // throws AccessDeniedException
                } else if ((resource = repositoryService.getResource(context, dataSourceUri)) == null) {
                    exceptions.add(badRequestException(QUERY_DATASOURCE_NOT_FOUND.createDescriptor(dataSourceUri)));
                } else {
                    boolean canBeConverted = false;

                    ToClientConverter<?, ?, ?> converter = resourceConverterProvider.getToClientConverter(resource);
                    if (converter != null) {
                        Class<?> clientTypeClass = resourceConverterProvider.getClientTypeClass(
                                converter.getClientResourceType()
                        );
                        canBeConverted = ClientReferenceable.class.isAssignableFrom(clientTypeClass);
                    }

                    if (!canBeConverted) {
                        exceptions.add(badRequestException(QUERY_DATASOURCE_TYPE_NOT_SUPPORTED.createDescriptor(dataSourceUri)));
                    }
                }
            } catch (org.springframework.security.access.AccessDeniedException e) {
                exceptions.add(badRequestException(QUERY_DATASOURCE_ACCESS_DENIED.createDescriptor(dataSourceUri)));
            }
        }
    }

    private ErrorDescriptorException badRequestException(ErrorDescriptor errorDescriptor) {
        return new ErrorDescriptorException(defaultMessageApplier.applyDefaultMessageIfNotSet(errorDescriptor));
    }
}