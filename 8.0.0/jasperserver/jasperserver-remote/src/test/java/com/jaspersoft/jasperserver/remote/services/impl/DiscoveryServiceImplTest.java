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

package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.core.util.type.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.dto.discovery.Parameter;
import com.jaspersoft.jasperserver.dto.discovery.VisualizationMetadata;
import com.jaspersoft.jasperserver.remote.discovery.DiscoveryServiceImpl;
import com.jaspersoft.jasperserver.remote.discovery.DiscoveryStrategy;
import com.jaspersoft.jasperserver.remote.discovery.DiscoveryStrategyProvider;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: $
 */
public class DiscoveryServiceImplTest {
    @InjectMocks
    private DiscoveryServiceImpl service;
    @Mock
    private DiscoveryStrategyProvider provider;
    @Mock
    protected RepositoryService repositoryService;
    @Mock
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    private ResourceLookup l1 = new ResourceLookupImpl();
    private ResourceLookup l2 = new ResourceLookupImpl();
    private ResourceLookup l3 = new ResourceLookupImpl();
    private ResourceLookup l4 = new ResourceLookupImpl();
    private ResourceLookup l5 = new ResourceLookupImpl();

    private Resource resource = new ResourceImpl() {
        @Override
        protected Class getImplementingItf() {
            return null;
        }
    };

    private DiscoveryStrategy strategy = new DiscoveryStrategy() {
        @Override
        public List<Parameter> discoverParameters(Object resource) {
            return null;
        }

        @Override
        public List<Parameter> discoverOutputParameters(Object resource) {
            return null;
        }

        @Override
        public Class<? extends Resource> getSupportedResourceType() {
            return null;
        }
    };

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);

        l1.setURIString("/resource");
        l2.setURIString("/folder/resource");
        l3.setURIString("/organisations/organization_1/folder/resource");
        l4.setURIString("/organisations/organization_1/resource");
        l5.setURIString("/organisations/organization_1/other/folder/resource");
    }

    @BeforeMethod
    public void beforeEach(){
        resource.setName("resource");
        resource.setParentFolder("/folder");
    }

    @AfterMethod
    public void afterEach(){
        reset(repositoryService, provider);
    }

    @Test
    public void discover_normal_OK() throws Exception {
        when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        when(repositoryService.findResource(any(ExecutionContext.class), any(FilterCriteria.class))).thenReturn(new ResourceLookup[]{});
        when(provider.getDiscoveryStrategyFor(any(Resource.class))).thenReturn(strategy);

        VisualizationMetadata metadata = service.discover(resource.getURIString());

        assertEquals(metadata.getUri(), resource.getURIString());
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void discover_normal_wrong_url() throws Exception {
        when(repositoryService.getResource(nullable(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        when(repositoryService.findResource(nullable(ExecutionContext.class), nullable(FilterCriteria.class))).thenReturn(new ResourceLookup[]{});
        when(provider.getDiscoveryStrategyFor(nullable(Resource.class))).thenReturn(strategy);

        service.discover("/tttt");
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void discover_externalURL_doesNotExist() throws Exception {
        when(repositoryService.findResource(nullable(ExecutionContext.class), nullable(FilterCriteria.class))).thenReturn(new ResourceLookup[]{});

        VisualizationMetadata metadata = service.discover(resource.getURIString());
    }

    @Test()
    public void discover_externalURL_existOne() throws Exception {
        when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        when(repositoryService.findResource(any(ExecutionContext.class), any(FilterCriteria.class))).thenReturn(new ResourceLookup[]{l2});
        when(provider.getDiscoveryStrategyFor(any(Resource.class))).thenReturn(strategy);

        VisualizationMetadata metadata = service.discover(resource.getURIString());

        assertEquals(metadata.getUri(), l2.getURIString());
    }

    @Test
    public void discover_externalURL_existManyOneCorrect() throws Exception {
        when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        when(repositoryService.findResource(any(ExecutionContext.class), any(FilterCriteria.class))).thenReturn(new ResourceLookup[]{l1, l2, l4});
        when(provider.getDiscoveryStrategyFor(any(Resource.class))).thenReturn(strategy);

        VisualizationMetadata metadata = service.discover(resource.getURIString());

        assertEquals(metadata.getUri(), l2.getURIString());
    }

    @Test
    public void discover_externalURL_existManyManyCorrect() throws Exception {
        resource.setURIString(l3.getURIString());

        when(repositoryService.getResource(any(ExecutionContext.class), eq(l3.getURIString()))).thenReturn(resource);
        when(repositoryService.findResource(any(ExecutionContext.class), any(FilterCriteria.class))).thenReturn(new ResourceLookup[]{l1, l5, l3, l4});
        when(provider.getDiscoveryStrategyFor(any(Resource.class))).thenReturn(strategy);

        VisualizationMetadata metadata = service.discover(resource.getURIString());

        assertEquals(metadata.getUri(), l3.getURIString());
    }

    @Test
    public void discover_externalURL_existManyManyCorrectReverse() throws Exception {
        when(repositoryService.getResource(nullable(ExecutionContext.class), eq(l2.getURIString()))).thenReturn(resource);
        when(repositoryService.findResource(nullable(ExecutionContext.class), any(FilterCriteria.class))).thenReturn(new ResourceLookup[]{l1, l5, l2, l4});
        when(provider.getDiscoveryStrategyFor(any(Resource.class))).thenReturn(strategy);

        VisualizationMetadata metadata = service.discover(l3.getURIString());

        assertEquals(metadata.getUri(), l2.getURIString());
    }


}
