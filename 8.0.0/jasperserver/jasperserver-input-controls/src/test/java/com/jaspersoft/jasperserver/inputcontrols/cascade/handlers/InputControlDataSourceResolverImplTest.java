package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.token.FilterResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class InputControlDataSourceResolverImplTest {
    @InjectMocks
    private InputControlDataSourceResolverImpl resolver;
    @Mock
    private CachedRepositoryService cachedRepositoryService;
    @Mock
    private FilterResolver filterResolver;
    @Mock
    private CachedEngineService cachedEngineService;

    private InputControl inputControl = mock(InputControl.class);

    private ResourceReference dataSourceReference = mock(ResourceReference.class);

    private ResourceReference queryReference = mock(ResourceReference.class);

    private Query query = mock(Query.class);

    private ReportDataSource reportDataSource = mock(ReportDataSource.class);

    @Test
    public void resolveDatasource_nullQuery_dataSourceReference() throws CascadeResourceNotFoundException {
        ResourceReference result = resolver.resolveDatasource(inputControl, dataSourceReference);
        assertEquals(dataSourceReference, result);
    }

    @Test
    public void resolveDatasource_nullQueryResource_dataSourceReference() throws CascadeResourceNotFoundException {
        doReturn(queryReference).when(inputControl).getQuery();

        ResourceReference result = resolver.resolveDatasource(inputControl, dataSourceReference);
        assertEquals(dataSourceReference, result);
    }

    @Test
    public void resolveDatasource_nullDataSource_dataSourceReference() throws CascadeResourceNotFoundException {
        doReturn(queryReference).when(inputControl).getQuery();
        doReturn(query).when(cachedRepositoryService).getResource(eq(Resource.class), eq(queryReference));
        ResourceReference result = resolver.resolveDatasource(inputControl, dataSourceReference);
        assertEquals(dataSourceReference, result);
    }

    @Test
    public void resolveDatasource_queryResourceWithDataSourceReference_queryDataSourceReference() throws CascadeResourceNotFoundException {
        ResourceReference queryDataSource = mock(ResourceReference.class);
        doReturn(queryReference).when(inputControl).getQuery();
        doReturn(query).when(cachedRepositoryService).getResource(eq(Resource.class), eq(queryReference));
        doReturn(queryDataSource).when(query).getDataSource();
        ResourceReference result = resolver.resolveDatasource(inputControl, dataSourceReference);
        assertEquals(queryDataSource, result);
    }

    @Test
    public void prepareDomainDataSource_doNotRequiresInit_emptyParameters() throws CascadeResourceNotFoundException {
        doReturn(reportDataSource).when(cachedRepositoryService).getResource(eq(Resource.class), eq(dataSourceReference));
        Map<String, Object> result = resolver.prepareDomainDataSourceParameters(dataSourceReference);
        assertTrue(result.isEmpty());
    }

    @Test
    public void prepareDomainDataSource_requiresInit_parameters() throws CascadeResourceNotFoundException {
        Map<String, Object> parameters = Collections.singletonMap("dsParamKey", "dsParamValue");
        doReturn(reportDataSource).when(cachedRepositoryService).getResource(eq(Resource.class), eq(dataSourceReference));
        doReturn(true).when(filterResolver).paramTestNeedsDataSourceInit(eq(reportDataSource));
        doReturn(parameters).when(cachedEngineService).getSLParameters(eq(reportDataSource));
        Map<String, Object> result = resolver.prepareDomainDataSourceParameters(dataSourceReference);
        assertEquals(parameters, result);
    }

}