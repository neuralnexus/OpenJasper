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

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsLogicService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException;
import com.jaspersoft.jasperserver.remote.exception.ErrorDescriptorBuildingService;
import com.jaspersoft.jasperserver.remote.reports.HtmlExportStrategy;
import com.jaspersoft.jasperserver.remote.services.ReportExecution;
import com.jaspersoft.jasperserver.remote.services.ReportExecutor;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NOTHING_SUBSTITUTION_VALUE;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NULL_SUBSTITUTION_VALUE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@ContextConfiguration(locations = {
        "classpath:applicationContext*.xml",
})
@ActiveProfiles("test")
public class RunReportServiceImplTest extends AbstractTestNGSpringContextTests {

    private static class TestClass extends RunReportServiceImpl {
        /**
         * Thanks, Eclipse!
         */
        private static final long serialVersionUID = 1L;

        public void verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(List<ReportInputControl> inputControlsForReport, Map<String, String[]> rawInputParameters, Map<String, String[]> inputControlFormattedValues) throws InputControlsValidationException {
            this.verifyCorrectParameterValuesForNonCascadingControls(inputControlsForReport, rawInputParameters, inputControlFormattedValues);
        }
    }

    @InjectMocks
    TestClass service = new TestClass();

    @Mock
    private AuditHelper auditHelper;
    @Mock
    private EngineService engine;
    @Mock
    private EngineService unsecuredEngine;
    @Mock
    private Executor asyncExecutor;
    @Mock
    private InputControlsLogicService inputControlsLogicService;
    @Mock
    private ReportExecutor reportExecutor;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private ErrorDescriptorBuildingService errorDescriptorBuildingService;
    @Mock
    private Map<String, HtmlExportStrategy> htmlExportStrategies;
    @Mock
    private HtmlExportStrategy defaultHtmlExportStrategy;
    @Mock
    private VirtualizerFactory virtualizerFactory;
    @Mock
    private SecureExceptionHandler secureExceptionHandler;

    List<ReportInputControl> controlsCascade = new ArrayList<ReportInputControl>();

    @BeforeClass( )
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod()
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        controlsCascade = new ArrayList<ReportInputControl>();

        controlsCascade.add(new ReportInputControl().setId("a"));
        controlsCascade.add(new ReportInputControl().setId("b").setMasterDependencies(Arrays.asList("a")));
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_SingleNothing() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});

        processed.put("a", new String[]{"a 1"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = {InputControlsValidationException.class})
    public void verifyCorrectParameterValuesForNonCascadingControls_SingleChanged_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});

        processed.put("a", new String[]{"a 2"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = {InputControlsValidationException.class})
    public void verifyCorrectParameterValuesForNonCascadingControls_SingleChangedChild_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("b", new String[]{"b 1"});

        processed.put("b", new String[]{"b 2"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = {InputControlsValidationException.class})
    public void verifyCorrectParameterValuesForNonCascadingControls_SingleChangedByAdding_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});

        processed.put("a", new String[]{"a 1", "a 2"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test( groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = {InputControlsValidationException.class})
    public void verifyCorrectParameterValuesForNonCascadingControls_SingleChangedByRemoving_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});

        processed.put("a", new String[]{});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_SingleUnknown() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("c", new String[]{"c 1"});

        processed.put("c", new String[]{"c 1"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_CascadeNothing() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});
        initial.put("b", new String[]{"b 1"});

        processed.put("a", new String[]{"a 1"});
        processed.put("b", new String[]{"b 1"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = {InputControlsValidationException.class})
    public void verifyCorrectParameterValuesForNonCascadingControls_CascadeChangedParent_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});
        initial.put("b", new String[]{"b 1"});

        processed.put("a", new String[]{"a 2"});
        processed.put("b", new String[]{"b 1"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_CascadeChangedChild() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});
        initial.put("b", new String[]{"b 1"});

        processed.put("a", new String[]{"a 1"});
        processed.put("b", new String[]{"b 2"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = {InputControlsValidationException.class})
    public void verifyCorrectParameterValuesForNonCascadingControls_CascadeChangedParentAndChild_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{"a 1"});
        initial.put("b", new String[]{"b 1"});

        processed.put("a", new String[]{"a 2"});
        processed.put("b", new String[]{"b 2"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_Nothing() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{NOTHING_SUBSTITUTION_VALUE});

        processed.put("a", new String[]{});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_Null() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("a", new String[]{NULL_SUBSTITUTION_VALUE});

        processed.put("a", new String[]{NOTHING_SUBSTITUTION_VALUE});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_SingleRandomized() throws Exception {
        Map<String, String[]> initial = new HashMap<String, String[]>(), processed = new HashMap<String, String[]>();

        initial.put("c", new String[]{"c 1", "c 2"});

        processed.put("c", new String[]{"c 2", "c 1"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = InputControlsValidationException.class)
    public void verifyCorrectParameterValuesForNonCascadingControls_emptyProcessed_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<>(), processed = new HashMap<>();

        initial.put("a", new String[]{"a 1", "a 2"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_nothingSubstitutionAndEmptyProcessed() throws Exception {
        Map<String, String[]> initial = new HashMap<>(), processed = new HashMap<>();

        initial.put("a", new String[]{NOTHING_SUBSTITUTION_VALUE});
        initial.put("b", new String[]{NOTHING_SUBSTITUTION_VALUE});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"})
    public void verifyCorrectParameterValuesForNonCascadingControls_singleWithNothingSubstitution() throws Exception {
        Map<String, String[]> initial = new HashMap<>(), processed = new HashMap<>();

        initial.put("a", new String[]{NOTHING_SUBSTITUTION_VALUE});
        initial.put("b", new String[]{"b 1"});

        processed.put("b", new String[]{"b 1"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test(groups = {"verifyCorrectParameterValuesForNonCascadingControls"}, expectedExceptions = InputControlsValidationException.class)
    public void verifyCorrectParameterValuesForNonCascadingControls_singleWithNothingSubstitutionAndValue_Exception() throws Exception {
        Map<String, String[]> initial = new HashMap<>(), processed = new HashMap<>();

        initial.put("a", new String[]{NOTHING_SUBSTITUTION_VALUE, "a 1"});

        service.verifyCorrectParameterValuesForNonCascadingControlsEntryPoint(controlsCascade, initial, processed);
    }

    @Test
    public void verifyRequestAttributesArePropagatedToReportExecutionRunnable() {
        // Arrange
        final RequestAttributes attributes1 = mock(RequestAttributes.class);
        final RequestAttributes attributes2 = mock(RequestAttributes.class);
        // Set 1-st version of attributes
        RequestContextHolder.setRequestAttributes(attributes1);

        // Act
        service.startReportExecution(new ReportExecution());
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(asyncExecutor).execute(argumentCaptor.capture());
        // Override original attributes with 2-nd version
        RequestContextHolder.setRequestAttributes(attributes2);
        Runnable runnable = argumentCaptor.getValue();
        runnable.run();

        // Assert
        // Ensure that runnable is using 1-st version of attributes
        assertEquals(attributes1, RequestContextHolder.getRequestAttributes());
    }

}
