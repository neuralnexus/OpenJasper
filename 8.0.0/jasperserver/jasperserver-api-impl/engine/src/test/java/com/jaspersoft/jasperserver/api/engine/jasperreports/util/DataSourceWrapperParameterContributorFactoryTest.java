package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.PermissionOverride;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ParameterContributorContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.EXECUTE_OVERRIDE;
import static com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataSourceWrapperParameterContributorFactory.PROPERTY_DATA_SOURCE_LOCATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class DataSourceWrapperParameterContributorFactoryTest {

    @InjectMocks
    private DataSourceWrapperParameterContributorFactory factory;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private EngineService engineService;

    @Captor
    private ArgumentCaptor<ExecutionContext> contextArgumentCaptor;

    private ParameterContributorContext contributorContext;

    private final JasperReportsContext jasperReportsContext = mock(JasperReportsContext.class);

    private final JRDataset dataset = mock(JRDataset.class);

    @Before
    public void setUp() {
        contributorContext = new ParameterContributorContext(jasperReportsContext, dataset, new HashMap<>());
    }

    @Test
    public void getContributorsShouldGetResourceWithContext() throws JRException {
        doReturn(true).when(dataset).hasProperties();
        JRPropertiesMap propertiesMap = mock(JRPropertiesMap.class);
        doReturn(propertiesMap).when(dataset).getPropertiesMap();
        String dataSourceUri = "dataSourceUri";
        doReturn(dataSourceUri).when(propertiesMap).getProperty(eq(PROPERTY_DATA_SOURCE_LOCATION));
        doReturn(mock(ReportDataSource.class)).when(repositoryService).getResource(contextArgumentCaptor.capture(),
                eq(dataSourceUri), eq(Resource.class));

        factory.getContributors(contributorContext);

        ExecutionContext context = contextArgumentCaptor.getValue();
        assertNotNull(context);
        PermissionOverride override = (PermissionOverride) context.getAttributes().get(0);
        assertEquals(EXECUTE_OVERRIDE, override.getOverrideId());
    }
}