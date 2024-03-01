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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

/**
 * @author Anton Fomin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportLoadingServiceTest {

    @InjectMocks
    private ReportLoadingService reportLoadingService;

    @Mock
    private RepositoryService repositoryServiceMock;

    @Mock
    private InputControlsContainer containerDataSourceMock;

    @Test
    public void getInputControlReferencesNull() {
        boolean fail = false;
        try {
            reportLoadingService.getInputControlReferences(null, null);
        } catch (NullPointerException e) {
            fail = true;
        }

        assertTrue(fail);
    }

    @Test
    public void getInputControlReferencesOnlyContainerControls() {
        List<ResourceReference> expectedRefs = new ArrayList<>();
        expectedRefs.add(createLocalRef("Control0"));
        expectedRefs.add(createLocalRef("Control1"));
        expectedRefs.add(createRepositoryRef("Control2"));
        expectedRefs.add(createRepositoryRef("Control3"));

        InputControlsContainer containerMock = mock(InputControlsContainer.class);
        when(containerMock.getInputControls()).thenReturn(expectedRefs);

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock);

        assertEquals(expectedRefs, actualRefs);
    }

    @Test
    public void getInputControlReferencesOnlyDataSourceControls() {
        List<ResourceReference> expectedRefs = new ArrayList<>();
        expectedRefs.add(createLocalRef("Control0"));
        expectedRefs.add(createLocalRef("Control1"));
        expectedRefs.add(createRepositoryRef("Control2"));
        expectedRefs.add(createRepositoryRef("Control3"));

        prepareDataSource(expectedRefs);
        prepareRepositoryService();

        InputControlsContainer containerMock = mock(InputControlsContainer.class);
        when(containerMock.getDataSource()).thenReturn(prepareDataSourceReference());

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock);

        assertEquals(expectedRefs, actualRefs);
    }

    @Test
    public void getInputControlReferencesContainerAndDataSourceControls() {
        List<ResourceReference> containerRefs = new ArrayList<>();
        containerRefs.add(createLocalRef("Control0"));
        containerRefs.add(createRepositoryRef("Control1"));
        List<ResourceReference> dataSourceRefs = new ArrayList<>();
        dataSourceRefs.add(createLocalRef("Control2"));
        dataSourceRefs.add(createRepositoryRef("Control3"));

        prepareDataSource(dataSourceRefs);
        prepareRepositoryService();

        InputControlsContainer containerMock = mock(InputControlsContainer.class);
        when(containerMock.getInputControls()).thenReturn(containerRefs);
        when(containerMock.getDataSource()).thenReturn(prepareDataSourceReference());

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock);

        List<ResourceReference> expectedRefs = new ArrayList<>();
        expectedRefs.addAll(containerRefs);
        expectedRefs.addAll(dataSourceRefs);

        assertEquals(expectedRefs, actualRefs);
    }

    @Test
    public void getInputControlReferencesContainerAndDataSourceControlsMergedLocal() {
        List<ResourceReference> containerRefs = new ArrayList<>();
        containerRefs.add(createLocalRef("Control0"));
        containerRefs.add(createRepositoryRef("Control1"));
        List<ResourceReference> dataSourceRefs = new ArrayList<>();
        dataSourceRefs.add(createLocalRef("Control0"));
        dataSourceRefs.add(createRepositoryRef("Control3"));

        prepareDataSource(dataSourceRefs);
        prepareRepositoryService();

        InputControlsContainer containerMock = mock(InputControlsContainer.class);
        when(containerMock.getInputControls()).thenReturn(containerRefs);
        when(containerMock.getDataSource()).thenReturn(prepareDataSourceReference());

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock);

        List<ResourceReference> expectedRefs = new ArrayList<>();
        expectedRefs.addAll(containerRefs);
        expectedRefs.addAll(dataSourceRefs);
        expectedRefs.remove(2);

        assertEquals(expectedRefs, actualRefs);
    }

    @Test
    public void isDummyReport_noData_returnsFalse() {
        ResourceReference queryDataSourceReference = mock(ResourceReference.class, "queryDataSourceReference");
        boolean dummyReport = reportLoadingService.isDummyReport(null,queryDataSourceReference);
        assertFalse(dummyReport);
    }

    @Test
    public void getInputControlReferencesContainerAndDataSourceControlsMergedAll() {
        List<ResourceReference> containerRefs = new ArrayList<>();
        containerRefs.add(createLocalRef("Control0"));
        containerRefs.add(createRepositoryRef("Control1"));
        List<ResourceReference> dataSourceRefs = new ArrayList<>();
        dataSourceRefs.add(createLocalRef("Control1"));
        dataSourceRefs.add(createRepositoryRef("Control0"));

        prepareDataSource(dataSourceRefs);
        prepareRepositoryService();

        InputControlsContainer containerMock = mock(InputControlsContainer.class);
        when(containerMock.getInputControls()).thenReturn(containerRefs);
        when(containerMock.getDataSource()).thenReturn(prepareDataSourceReference());

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock);

        List<ResourceReference> expectedRefs = new ArrayList<>();
        expectedRefs.addAll(containerRefs);
        expectedRefs.addAll(dataSourceRefs);
        expectedRefs.remove(2);
        expectedRefs.remove(2);

        assertEquals(expectedRefs, actualRefs);
    }

    @Test
    public void isDummyReport_Without_Data_Returns_False() {
        ResourceReference queryDataSourceReference = mock(ResourceReference.class);
        ExecutionContext executionContext = mock(ExecutionContext.class);
        boolean dummyReport = reportLoadingService.isDummyReport(executionContext,queryDataSourceReference);
        assertFalse(dummyReport);
    }

    @Test
    public void isDummyReport_With_Data_Returns_False() {
        final String uri = "/public/test_resource";
        ResourceReference resource = mock(ResourceReference.class);
        ExecutionContext executionContext = mock(ExecutionContext.class);
        boolean dummyReport = reportLoadingService.isDummyReport(executionContext,resource);
        assertFalse(dummyReport);
    }

    private ResourceReference createLocalRef(String name) {
        InputControl mockIC1 = mock(InputControl.class);
        when(mockIC1.getName()).thenReturn(name);
        return new ResourceReference(mockIC1);
    }

    private ResourceReference createRepositoryRef(String name) {
        return new ResourceReference("/organizations/organization_1/datatypes/" + name);
    }

    private void prepareRepositoryService() {
        reset(repositoryServiceMock);
        when(repositoryServiceMock.getResource(nullable(ExecutionContext.class), anyString(), nullable(Class.class)))
                .thenReturn(containerDataSourceMock);
    }

    private void prepareDataSource(List<ResourceReference> refs) {
        reset(containerDataSourceMock);
        when(containerDataSourceMock.getInputControls()).thenReturn(refs);
    }

    private ResourceReference prepareDataSourceReference() {
        return new ResourceReference("/organizations/organization_1/dataSources/myDataSource");
    }
}
