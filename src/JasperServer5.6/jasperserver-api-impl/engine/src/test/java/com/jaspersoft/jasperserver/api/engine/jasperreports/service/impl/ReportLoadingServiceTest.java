package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Anton Fomin
 * @version $Id: ReportLoadingServiceTest.java 23731 2012-05-14 10:45:54Z afomin $
 */
public class ReportLoadingServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private ReportLoadingService reportLoadingService;

    @InjectIntoByType
    private Mock<RepositoryService> repositoryServiceMock;

    Mock<InputControlsContainer> containerDataSourceMock;

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
        List<ResourceReference> expectedRefs = new ArrayList<ResourceReference>();
        expectedRefs.add(createLocalRef("Control0"));
        expectedRefs.add(createLocalRef("Control1"));
        expectedRefs.add(createRepositoryRef("Control2"));
        expectedRefs.add(createRepositoryRef("Control3"));

        Mock<InputControlsContainer> containerMock = MockUnitils.createMock(InputControlsContainer.class);
        containerMock.returns(expectedRefs).getInputControls();

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock.getMock());

        assertReflectionEquals(expectedRefs, actualRefs);
    }

    @Test
    public void getInputControlReferencesOnlyDataSourceControls() {
        List<ResourceReference> expectedRefs = new ArrayList<ResourceReference>();
        expectedRefs.add(createLocalRef("Control0"));
        expectedRefs.add(createLocalRef("Control1"));
        expectedRefs.add(createRepositoryRef("Control2"));
        expectedRefs.add(createRepositoryRef("Control3"));

        prepareDataSource(expectedRefs);
        prepareRepositoryService();

        Mock<InputControlsContainer> containerMock = MockUnitils.createMock(InputControlsContainer.class);
        containerMock.returns(prepareDataSourceReference()).getDataSource();

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock.getMock());

        assertReflectionEquals(expectedRefs, actualRefs);
    }

    @Test
    public void getInputControlReferencesContainerAndDataSourceControls() {
        List<ResourceReference> containerRefs = new ArrayList<ResourceReference>();
        containerRefs.add(createLocalRef("Control0"));
        containerRefs.add(createRepositoryRef("Control1"));
        List<ResourceReference> dataSourceRefs = new ArrayList<ResourceReference>();
        dataSourceRefs.add(createLocalRef("Control2"));
        dataSourceRefs.add(createRepositoryRef("Control3"));

        prepareDataSource(dataSourceRefs);
        prepareRepositoryService();

        Mock<InputControlsContainer> containerMock = MockUnitils.createMock(InputControlsContainer.class);
        containerMock.returns(containerRefs).getInputControls();
        containerMock.returns(prepareDataSourceReference()).getDataSource();

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock.getMock());

        List<ResourceReference> expectedRefs = new ArrayList<ResourceReference>();
        expectedRefs.addAll(containerRefs);
        expectedRefs.addAll(dataSourceRefs);

        assertReflectionEquals(expectedRefs, actualRefs);
    }

    @Test
    public void getInputControlReferencesContainerAndDataSourceControlsMergedLocal() {
        List<ResourceReference> containerRefs = new ArrayList<ResourceReference>();
        containerRefs.add(createLocalRef("Control0"));
        containerRefs.add(createRepositoryRef("Control1"));
        List<ResourceReference> dataSourceRefs = new ArrayList<ResourceReference>();
        dataSourceRefs.add(createLocalRef("Control0"));
        dataSourceRefs.add(createRepositoryRef("Control3"));

        prepareDataSource(dataSourceRefs);
        prepareRepositoryService();

        Mock<InputControlsContainer> containerMock = MockUnitils.createMock(InputControlsContainer.class);
        containerMock.returns(containerRefs).getInputControls();
        containerMock.returns(prepareDataSourceReference()).getDataSource();

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock.getMock());

        List<ResourceReference> expectedRefs = new ArrayList<ResourceReference>();
        expectedRefs.addAll(containerRefs);
        expectedRefs.addAll(dataSourceRefs);
        expectedRefs.remove(2);

        assertReflectionEquals(expectedRefs, actualRefs);
    }

    @Test
    public void getInputControlReferencesContainerAndDataSourceControlsMergedAll() {
        List<ResourceReference> containerRefs = new ArrayList<ResourceReference>();
        containerRefs.add(createLocalRef("Control0"));
        containerRefs.add(createRepositoryRef("Control1"));
        List<ResourceReference> dataSourceRefs = new ArrayList<ResourceReference>();
        dataSourceRefs.add(createLocalRef("Control1"));
        dataSourceRefs.add(createRepositoryRef("Control0"));

        prepareDataSource(dataSourceRefs);
        prepareRepositoryService();

        Mock<InputControlsContainer> containerMock = MockUnitils.createMock(InputControlsContainer.class);
        containerMock.returns(containerRefs).getInputControls();
        containerMock.returns(prepareDataSourceReference()).getDataSource();

        List<ResourceReference> actualRefs = reportLoadingService.getInputControlReferences(null, containerMock.getMock());

        List<ResourceReference> expectedRefs = new ArrayList<ResourceReference>();
        expectedRefs.addAll(containerRefs);
        expectedRefs.addAll(dataSourceRefs);
        expectedRefs.remove(2);
        expectedRefs.remove(2);

        assertReflectionEquals(expectedRefs, actualRefs);
    }

    private ResourceReference createLocalRef(String name) {
        Mock<InputControl> mockIC1 = MockUnitils.createMock(InputControl.class);
        mockIC1.returns(name).getName();
        return new ResourceReference(mockIC1.getMock());
    }

    private ResourceReference createRepositoryRef(String name) {
        return new ResourceReference("/organizations/organization_1/datatypes/" + name);
    }

    private void prepareRepositoryService() {
        repositoryServiceMock.resetBehavior();
        repositoryServiceMock.returns(containerDataSourceMock.getMock()).getResource(null, null, null);
    }

    private void prepareDataSource(List<ResourceReference> refs) {
        containerDataSourceMock.resetBehavior();
        containerDataSourceMock.returns(refs).getInputControls();
    }

    private ResourceReference prepareDataSourceReference() {
        return new ResourceReference("/organizations/organization_1/dataSources/myDataSource");
    }
}
