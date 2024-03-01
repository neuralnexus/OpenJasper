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
package com.jaspersoft.jasperserver.remote.validation;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataSource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.executions.AbstractClientExecution;
import com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode;
import com.jaspersoft.jasperserver.dto.resources.ClientDashboard;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.builders.DefaultMessageApplier;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.QUERY_DATASOURCE_ACCESS_DENIED;
import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.QUERY_DATASOURCE_NOT_FOUND;
import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.QUERY_DATASOURCE_TYPE_NOT_SUPPORTED;


/**
 * @author Alexei Skorodumov askorodu@tibco.com
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseClientExecutionValidatorTest {
    private static final String DATA_SOURCE_URI = "/data/source/uri";
    private static final String CLIENT_RESOURCE_TYPE = "clientType";

    @Spy
    @InjectMocks
    private BaseClientExecutionValidator validator;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private DefaultMessageApplier defaultMessageApplier;

    @Mock
    private ResourceConverterProvider resourceConverterProvider;

    private AbstractClientExecution execution = mock(AbstractClientExecution.class);

    private ClientReference dataSourceReference = mock(ClientReference.class);

    private ToClientConverter<?, ?, ?> toClientConverter = mock(ToClientConverter.class);
    final ExecutionContext ctx = ExecutionContextImpl.getRuntimeExecutionContext();

    @Before
    public void setUp() throws Exception {
        when(execution.getDataSource()).thenReturn(dataSourceReference);
        when(dataSourceReference.getUri()).thenReturn(DATA_SOURCE_URI);
        when(toClientConverter.getClientResourceType()).thenReturn(CLIENT_RESOURCE_TYPE);

        reset(defaultMessageApplier);
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(any(Resource.class));
    }

    @Test
    public void validate_uriIsNull_notValid() {
        when(dataSourceReference.getUri()).thenReturn(null);

        List<Exception> exceptions = validator.validate(ctx, execution);

        assertListContainsMandatoryParameterNotFoundException(exceptions);
    }

    @Test
    public void validate_dataSourceUriPointsToFolder_notValid() {
        mockErrorDescriptorBuilder(QUERY_DATASOURCE_TYPE_NOT_SUPPORTED);
        when(repositoryService.folderExists(any(ExecutionContext.class), eq(DATA_SOURCE_URI))).thenReturn(true);

        List<Exception> exceptions = validator.validate(ctx, execution);

        assertExceptionsContainErrorCode(exceptions, QUERY_DATASOURCE_TYPE_NOT_SUPPORTED.toString());
    }

    @Test
    public void validate_dataSourceNotFound_notValid() {
        mockErrorDescriptorBuilder(QUERY_DATASOURCE_NOT_FOUND);
        when(repositoryService.getResource(any(ExecutionContext.class), eq(DATA_SOURCE_URI))).thenReturn(null);

        List<Exception> exceptions = validator.validate(ctx, execution);

        assertExceptionsContainErrorCode(exceptions, QUERY_DATASOURCE_NOT_FOUND.toString());
    }

    @Test
    public void validate_repositoryServiceThrowsAccessDeniedException_notValid() {
        mockErrorDescriptorBuilder(QUERY_DATASOURCE_ACCESS_DENIED);
        when(repositoryService.getResource(any(ExecutionContext.class), eq(DATA_SOURCE_URI)))
                .thenThrow(mock(AccessDeniedException.class));

        List<Exception> exceptions = validator.validate(ctx, execution);

        assertExceptionsContainErrorCode(exceptions, QUERY_DATASOURCE_ACCESS_DENIED.toString());
    }

    @Test
    public void validate_resourceConverterNotFound_invalid() {
        mockErrorDescriptorBuilder(QUERY_DATASOURCE_TYPE_NOT_SUPPORTED);
        when(repositoryService
                .getResource(any(ExecutionContext.class), eq(DATA_SOURCE_URI)))
                .thenReturn(mock(DataSource.class));
        when(resourceConverterProvider.getToClientConverter(any(Resource.class))).thenReturn(null);

        List<Exception> exceptions = validator.validate(ctx, execution);

        assertExceptionsContainErrorCode(exceptions, QUERY_DATASOURCE_TYPE_NOT_SUPPORTED.toString());
    }

    @Test
    public void validate_notClientReferenceableType_invalid() {
        mockErrorDescriptorBuilder(QUERY_DATASOURCE_TYPE_NOT_SUPPORTED);
        when(repositoryService
                .getResource(any(ExecutionContext.class), eq(DATA_SOURCE_URI)))
                .thenReturn(mock(DataSource.class));
        doReturn(ClientDashboard.class).when(resourceConverterProvider).getClientTypeClass(CLIENT_RESOURCE_TYPE);

        List<Exception> exceptions = validator.validate(ctx, execution);

        assertExceptionsContainErrorCode(exceptions, QUERY_DATASOURCE_TYPE_NOT_SUPPORTED.toString());
    }

    @Test
    public void validate_dataSourceFound_valid() {
        when(repositoryService
                .getResource(any(ExecutionContext.class), eq(DATA_SOURCE_URI)))
                .thenReturn(mock(DataSource.class));
        doReturn(ClientDomain.class).when(resourceConverterProvider).getClientTypeClass(CLIENT_RESOURCE_TYPE);

        List<Exception> exceptions = validator.validate(ctx, execution);

        assertTrue(exceptions.isEmpty());
    }

    @Test(expected = Exception.class)
    public void validate_repositoryThrowsUnhandledException_exceptionExpected() {
        when(repositoryService
                .getResource(any(ExecutionContext.class), eq(DATA_SOURCE_URI)))
                .thenThrow(new Exception("Some Unexpected Exception"));

        validator.validate(ctx, execution);
    }

    private void assertListContainsMandatoryParameterNotFoundException(List<Exception> exceptions) {
        assertFalse(exceptions.isEmpty());
        assertTrue(Iterables.any(exceptions, new Predicate<Exception>() {
            public boolean apply(Exception e) {
                return e instanceof MandatoryParameterNotFoundException;
            }
        }));
    }

    private void assertExceptionsContainErrorCode(List<Exception> exceptions, final String errorCode) {
        assertFalse(exceptions.isEmpty());
        assertTrue(Iterables.any(exceptions, new Predicate<Exception>() {
            public boolean apply(Exception e) {
                if (!(e instanceof ErrorDescriptorException)) return false;
                ErrorDescriptorException remoteException = (ErrorDescriptorException) e;
                if (remoteException.getErrorDescriptor() == null) return false;
                return errorCode.equals(remoteException.getErrorDescriptor().getErrorCode());
            }
        }));
    }

    private void mockErrorDescriptorBuilder(QueryExecutionsErrorCode errorCode) {
        ErrorDescriptor errorDescriptor = errorCode.createDescriptor(DATA_SOURCE_URI);
        when(defaultMessageApplier.applyDefaultMessageIfNotSet(errorCode.createDescriptor(DATA_SOURCE_URI)))
                .thenReturn(errorDescriptor);
    }
}
