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

package com.jaspersoft.jasperserver.inputcontrols.cascade;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlsInformationImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.SrcSets;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.*;
import com.jaspersoft.jasperserver.core.util.type.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.inputcontrols.action.EngineServiceCascadeTestQueryExecutor;
import com.jaspersoft.jasperserver.inputcontrols.cascade.cache.ControlLogicCacheManager;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValueFormattingUtils;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters.DataConverter;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters.DataConverterServiceImpl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters.TimeZoneConverter;
import com.jaspersoft.jasperserver.inputcontrols.cascade.token.FilterResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.util.*;

import static com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver.SKIP_PROFILE_ATTRIBUTES_RESOLVING;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.*;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.SKIP_FETCHING_IC_VALUES_FROM_DB;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link com.jaspersoft.jasperserver.inputcontrols.cascade.GenericInputControlLogic}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */

@RunWith(MockitoJUnitRunner.class)
public class GenericInputControlLogicNewTest  {

    @InjectMocks
    private GenericInputControlLogic genericInputControlLogic;

    @Mock
    private CachedRepositoryService cachedRepositoryService;

    @Mock
    private CachedEngineService cachedEngineService;

    @Mock
    private ControlLogicCacheManager controlLogicCacheManager;

    @Mock
    private FilterResolver filterResolver;

    private DataConverterServiceImpl dataConverterService = new DataConverterServiceImpl();

    private GenericTypeProcessorRegistry genericTypeProcessorRegistry = new GenericTypeProcessorRegistry();

    private ValueFormattingUtils valueFormattingUtils = new ValueFormattingUtils();

    private ParametersHelper ph;

    @Test
    public void testSimpleMasterAndSlaveDependencies() throws Exception {
        Map<String, String> controls = new LinkedHashMap<String, String>();
        controls.put("A", null);
        controls.put("B", "A");
        controls.put("C", "A, B");
        createParametersForDependencyTest(controls);
        setupDependencyTest();

        // Actual call of method to test
        List<ReportInputControl> list = genericInputControlLogic.getInputControlsStructure("/testUri", set("A", "B", "C"));

        assertEquals(3, list.size());
        assertTrue(list.get(0).getMasterDependencies().isEmpty());
        assertThat(list.get(0).getSlaveDependencies(), containsInAnyOrder("B", "C"));
        assertThat(list.get(1).getMasterDependencies(), containsInAnyOrder("A"));
        assertThat(list.get(1).getSlaveDependencies(), containsInAnyOrder("C"));
        assertThat(list.get(2).getMasterDependencies(), containsInAnyOrder("A", "B"));
        assertTrue(list.get(2).getSlaveDependencies().isEmpty());
    }

    @Test
    public void testNotFullSetOfControlsMasterAndSlaveDependenciesOfExcludedInputControlIgnored() throws Exception {
        Map<String, String> controls = new LinkedHashMap<String, String>();
        controls.put("A", null);
        controls.put("B", "A");
        controls.put("C", "A, B");
        createParametersForDependencyTest(controls);
        setupDependencyTest();

        // Actual call of method to test
        List<ReportInputControl> list = genericInputControlLogic.getInputControlsStructure("/testUri", set("A", "C"));

        assertEquals(2, list.size());
        assertTrue(list.get(0).getMasterDependencies().isEmpty());

        assertThat(list.get(0).getSlaveDependencies(), containsInAnyOrder("C"));
        assertThat(list.get(1).getMasterDependencies(), containsInAnyOrder("A"));
        assertTrue(list.get(1).getSlaveDependencies().isEmpty());
    }

    @Test
    public void testComplexMasterAndSlaveDependencies() throws Exception {
        Map<String, String> controls = new LinkedHashMap<String, String>();
        controls.put("A", null);
        controls.put("B", "A, F");
        controls.put("C", "A, B");
        controls.put("D", "A, B, C");
        controls.put("E", "C, F");
        controls.put("F", null);
        createParametersForDependencyTest(controls);
        setupDependencyTest();

        // Actual call of method to test
        List<ReportInputControl> list = genericInputControlLogic.getInputControlsStructure("/testUri", set("A", "B", "C", "D", "E", "F"));

        assertEquals(6, list.size());

        //A
        assertTrue(list.get(0).getMasterDependencies().isEmpty());
        assertThat(list.get(0).getSlaveDependencies(), containsInAnyOrder("B", "C", "D"));

        //B
        assertThat(list.get(1).getMasterDependencies(), containsInAnyOrder("A", "F"));
        assertThat(list.get(1).getSlaveDependencies(), containsInAnyOrder("D", "C"));

        //C
        assertThat(list.get(2).getMasterDependencies(), containsInAnyOrder("A", "B"));
        assertThat(list.get(2).getSlaveDependencies(), containsInAnyOrder("E", "D"));

        //D
        assertThat(list.get(3).getMasterDependencies(), containsInAnyOrder("A", "B", "C"));
        assertTrue(list.get(3).getSlaveDependencies().isEmpty());

        //E
        assertThat(list.get(4).getMasterDependencies(), containsInAnyOrder("C", "F"));
        assertTrue(list.get(4).getSlaveDependencies().isEmpty());

        //F
        assertTrue(list.get(5).getMasterDependencies().isEmpty());
        assertThat(list.get(5).getSlaveDependencies(), containsInAnyOrder("B", "E"));
    }

    /*
    *  Should resolve default values for input controls which are not exist in a request parameters
    */
    @Test
    public void getTypedParametersWithNotCompleteOrMessyReportParams() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnit(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        // fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("_flowId", new String[]{"er53234"});
        requestParameters.put("reportOptionURI", new String[]{"/ba/ba/ba"});
        requestParameters.put("customerUriParameter", new String[]{"cup32341"});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();
        Map<String, Object> defaultValues = ph.getDefaultParameterValues();

        Map actualResult  = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        assertThat(defaultValues, equalTo(actualResult));

        actualResult  = genericInputControlLogic.getTypedParameters(inputControls, null, infos, new ValidationErrorsImpl());

        assertThat(defaultValues, equalTo(actualResult));
    }

   /**
    *  Should resolve default values for input controls which are not exist in a request parameters
    */
    @Test
    public void getTypedParametersNothing() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnit(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        // fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("_flowId", new String[]{"er53234"});
        requestParameters.put("reportOptionURI", new String[]{"/ba/ba/ba"});
        requestParameters.put("customerUriParameter", new String[]{"cup32341"});
        requestParameters.put(CASCADE_ACCOUNT_TYPE, new String[]{"~NOTHING~"});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();

        Map actualResult  = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list("USA")), entry(CASCADE_STATE, list("CA")),
                entry(CASCADE_INDUSTRY, "Engineering"), entry(CASCADE_NAME, "EngBureau, Ltd"));

        assertThat(expected, equalTo(actualResult));
    }

   /**
    *  Ensure that empty array ([]) sent from UI for single select control doesn't treated as nothing selection.
    *  And instead it is treated like no raw value for this control sent from UI
    */
    @Test
    public void getTypedParametersUsingEmptyArrayForSingleSelectControl() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnit(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        // fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put(CASCADE_ACCOUNT_TYPE, new String[]{});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();

        // Actual call of test method
        Map actualResult = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list("USA")), entry(CASCADE_STATE, list("CA")),
                entry(CASCADE_INDUSTRY, "Engineering"), entry(CASCADE_NAME, "EngBureau, Ltd"), entry(CASCADE_ACCOUNT_TYPE, null));

        assertThat(expected, equalTo(actualResult));
    }

   /**
    *  Ensure that nothing substitution array ([~NOTHING~]) sent from UI for multiselect select control is threated same as
    *  empty array ([]) and means nothing selection.
    */
    @Test
    public void getTypedParametersUsingNothingSubstitusionForMultiSelectControl() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnit(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        // fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put(CASCADE_COUNTRY, new String[]{InputControlHandler.NOTHING_SUBSTITUTION_VALUE});
        requestParameters.put(CASCADE_STATE, new String[]{});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();

        // Actual call of test method
        Map actualResult = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list()), entry(CASCADE_STATE, list()),
                entry(CASCADE_INDUSTRY, "Engineering"), entry(CASCADE_NAME, "EngBureau, Ltd"), entry(CASCADE_ACCOUNT_TYPE, null));

        assertThat(expected, equalTo(expected));
    }

    @Test
    public void getTypedParametersContainsSkipFetchingICValuesParameter() throws Exception {
        Map<String, String[]> requestParameters = Collections.singletonMap(
                SKIP_FETCHING_IC_VALUES_FROM_DB,
                new String[]{Boolean.TRUE.toString()}
        );
        Map<String, Object> result = genericInputControlLogic.getTypedParameters((InputControlsContainer) null, requestParameters, false);
        assertTrue(result.containsKey(SKIP_FETCHING_IC_VALUES_FROM_DB));
        assertEquals(Boolean.TRUE.toString(), result.get(SKIP_FETCHING_IC_VALUES_FROM_DB));
    }

    /**
     *  Should resolve to NULL values for input controls which are not exist in a request parameters
     */
    @Test
    public void ensureNothingIsCompletedToNullInPublicMethod() throws Exception {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnit(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        //fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("_flowId", new String[]{"er53234"});
        requestParameters.put("reportOptionURI", new String[]{"/ba/ba/ba"});
        requestParameters.put("customerUriParameter", new String[]{"cup32341"});
        requestParameters.put(CASCADE_ACCOUNT_TYPE, new String[]{InputControlHandler.NOTHING_SUBSTITUTION_VALUE});
        requestParameters.put(CASCADE_NAME, new String[]{InputControlHandler.NOTHING_SUBSTITUTION_VALUE});

        //Actual call of tested method
        Map<String, Object> actualResult  = genericInputControlLogic.getTypedParameters((InputControlsContainer)null, requestParameters, false);

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list("USA")), entry(CASCADE_STATE, list("CA")),
                entry(CASCADE_INDUSTRY, "Engineering"),
                // NOTE - this is the difference between this test and "getTypedParametersNothing" test:
                //in previous test nothing means just removing key from typedParams, while here nothing is just replacement with null.
                entry(CASCADE_NAME, null),
                entry(CASCADE_ACCOUNT_TYPE, null));

        assertThat(expected, equalTo(actualResult));
    }

    /**
     * Test cases:
     *
     * Report opened with default values
     *
     * Cascade fired when report run using parameters from url:
     *
     *      Not all parameters passed, those which are not present should be replaced by defaults and most relevant ones should be resolved.
     *
     * Cascade fired by change in control:
     *
     * Cascade fired by selecting report options:
     *
     */


    /**
     * Simplest case, all by default.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsSimplestCase() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("CA|true", "OR", "WA"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Engineering|true", "Machinery"));
        expectedOptions.add(list("EngBureau, Ltd|true", "BestEngineering, Inc", "SuperSoft, LLC"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Incoming values are not correct: country does not have specified state.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsWrongSlaveControlValue() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("CA"), "Distribution", "Communications", "B & X Rybolt Electronics, Inc");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("Zakarpatska|true", "Kyivska", "Kharkivska"));
        expectedOptions.add(list("Consulting", "Distribution|true", "Manufactoring"));
        expectedOptions.add(list("Machinery|true", "Telecommunications"));
        expectedOptions.add(list("B & X Rybolt Electronics, Inc|true", "H & G Van Antwerp Machinery Corp"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Ensure that query executor receives Null for single select when Nothing is selected.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsWithNothing() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("CA"), "~NOTHING~", "Communications", "B & X Rybolt Electronics, Inc");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, false, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        Map<String, Class<?>> countryTypes = ph.getParameterTypes();
        countryTypes.keySet().retainAll(list(CASCADE_COUNTRY));
        verify(cachedEngineService).executeQuery(any(), any(), any(), any(), any(), any(), eq(countryTypes), eq(CASCADE_STATE));

        Map<String, Class<?>> accountTypes = ph.getParameterTypes();
        accountTypes.keySet().retainAll(list(CASCADE_COUNTRY, CASCADE_STATE));
        Map<String, Object> expectedParametersForAccountQuery = map(entry(CASCADE_COUNTRY, list("Ukraine")), entry(CASCADE_STATE, list("Zakarpatska")));
        verify(cachedEngineService).executeQuery(any(), any(), any(), any(), any(),
                eq(expectedParametersForAccountQuery), eq(accountTypes), eq(CASCADE_ACCOUNT_TYPE));

        Map<String, Class<?>> industryTypes = ph.getParameterTypes();
        industryTypes.keySet().retainAll(list(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE));
        Map<String, Object> expectedParametersForIndustryQuery = map(entry(CASCADE_COUNTRY, list("Ukraine")), entry(CASCADE_STATE, list("Zakarpatska")), entry(CASCADE_ACCOUNT_TYPE, null));
        verify(cachedEngineService).executeQuery(any(), any(), any(), any(), any(),
                eq(expectedParametersForIndustryQuery), eq(industryTypes), eq(CASCADE_INDUSTRY));

        Map<String, Class<?>> nameTypes = ph.getParameterTypes();
        nameTypes.keySet().retainAll(list(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY));
        verify(cachedEngineService).executeQuery(any(), any(), any(), any(), any(), any(), eq(nameTypes), eq(CASCADE_NAME));

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("Zakarpatska|true", "Kyivska", "Kharkivska"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true", "Consulting", "Distribution", "Manufactoring"));
        expectedOptions.add(list());
        expectedOptions.add(list());

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Incoming values empty selection, mandatory.
     * Second control in cascade was clicked.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsEmptyNotMandatory() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(null, list(), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, false, true, true, true);

        List<InputControlState> actualStates =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentParameterValueAsArray(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME));

        List<InputControlState> expectedStates = prepareStatesForEmptySelectionTest();

        assertEquals(expectedStates, actualStates);
    }

    private List<InputControlState> prepareStatesForEmptySelectionTest() {
        List<InputControlState> expectedStates = new ArrayList<InputControlState>();

        InputControlState accountType = new InputControlState();
        List<InputControlOption> accountTypeOptions = new ArrayList<InputControlOption>();
        accountTypeOptions.add(new InputControlOption("Consulting", "Consulting", true));
        accountTypeOptions.add(new InputControlOption("Distribution", "Distribution", false));
        accountTypeOptions.add(new InputControlOption("Manufactoring", "Manufactoring", false));
        accountType.setOptions(accountTypeOptions);
        accountType.setId(CASCADE_ACCOUNT_TYPE);
        accountType.setUri(ph.getInputControlUriString(CASCADE_ACCOUNT_TYPE));
        expectedStates.add(accountType);

        InputControlState industry = new InputControlState();
        List<InputControlOption> industryOptions = new ArrayList<InputControlOption>();
        industryOptions.add(new InputControlOption("Engineering", "Engineering", true));
        industryOptions.add(new InputControlOption("Machinery", "Machinery", false));
        industryOptions.add(new InputControlOption("Construction", "Construction", false));
        industryOptions.add(new InputControlOption("Communications", "Communications", false));
        industryOptions.add(new InputControlOption("Telecommunications", "Telecommunications", false));
        industry.setOptions(industryOptions);
        industry.setId(CASCADE_INDUSTRY);
        industry.setUri(ph.getInputControlUriString(CASCADE_INDUSTRY));
        expectedStates.add(industry);

        InputControlState name = new InputControlState();
        List<InputControlOption> nameOptions = new ArrayList<InputControlOption>();
        nameOptions.add(new InputControlOption("EngBureau, Ltd", "EngBureau, Ltd", true));
        nameOptions.add(new InputControlOption("BestEngineering, Inc", "BestEngineering, Inc", false));
        nameOptions.add(new InputControlOption("SuperSoft, LLC", "SuperSoft, LLC", false));
        nameOptions.add(new InputControlOption("Detwiler-Biltoft Transportation Corp", "Detwiler-Biltoft Transportation Corp", false));
        nameOptions.add(new InputControlOption("F & M Detwiler Transportation Corp", "F & M Detwiler Transportation Corp", false));
        nameOptions.add(new InputControlOption("D & D Barrera Transportation, Ltd", "D & D Barrera Transportation, Ltd", false));
        nameOptions.add(new InputControlOption("Infinity Communication Calls, Ltd", "Infinity Communication Calls, Ltd", false));
        name.setOptions(nameOptions);
        name.setId(CASCADE_NAME);
        name.setUri(ph.getInputControlUriString(CASCADE_NAME));
        expectedStates.add(name);

        return expectedStates;
    }

    /**
     * Wrong state, no default, mandatory.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsNoDefaultMandatory() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Canada"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(null, null, null, null, null);

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("BC|true"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Construction|true", "Machinery", "Telecommunications"));
        expectedOptions.add(list("Q & Q Lopez Telecommunications Associates|true", "X & N Eichorn Construction Corp"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Wrong state, no default, mandatory. Onw of two selections is incorrect.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsNoDefaultMandatoryOneValueWrong() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("CA", "Kyivska"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(null, null, null, null, null);

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("Zakarpatska", "Kyivska|true", "Kharkivska"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Construction", "Engineering|true", "Machinery", "Telecommunications"));
        expectedOptions.add(list("EngBureau, Ltd|true", "Infinity Communication Calls, Ltd", "SuperSoft, LLC"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Run report without request parameters. Default for state has two values.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsDefaultTwoValues() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(null, null, null, null, null);

        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(), ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA|true", "Ukraine", "Canada"));
        expectedOptions.add(list("CA|true", "OR", "WA|true"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Engineering|true", "Machinery", "Communications"));
        expectedOptions.add(list("EngBureau, Ltd|true", "BestEngineering, Inc", "SuperSoft, LLC",
                "D & D Barrera Transportation, Ltd", "Detwiler-Biltoft Transportation Corp", "F & M Detwiler Transportation Corp"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     *
     * Run report without request parameters. Three of five parameters passed through request.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsThreeRequestParameters() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("USA", "Ukraine"), list("Kyivska"), "Distribution", "Engineering", null);

        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, false, false, false);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(), ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA|true", "Ukraine|true", "Canada"));
        expectedOptions.add(list("CA", "OR", "WA", "Zakarpatska", "Kyivska|true", "Kharkivska"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE, "Consulting", "Distribution|true", "Manufactoring"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE, "Engineering|true", "Telecommunications"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true", "C & Y Difatta Machinery Partners", "Mills-Reed Electronics Associates"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    @Test
    public void getSelectedValuesListWrapper_withValidValues() {
        List<Map<String,List<String>>> values = new ArrayList<>();
        Map<String,List<String>> state = new HashMap<>();
        List<String> fieldList = new ArrayList<>();
        fieldList.add("USA");
        state.put("Country", fieldList);

        values.add(state);
        SelectedValuesListWrapper selectedValuesListWrapper = genericInputControlLogic.getSelectedValuesListWrapper(values);
        String actualValue = selectedValuesListWrapper.getSelectedValues().get(0).getId();
        assertEquals("Country", actualValue);

    }

    /**
     *
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsNotMandatory() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("USA", "Ukraine"), list("Kyivska"), null, "Machinery", null);

        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(), ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA|true", "Ukraine|true", "Canada"));
        expectedOptions.add(list("CA", "OR", "WA", "Zakarpatska", "Kyivska|true", "Kharkivska"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Construction", "Engineering", "Machinery|true", "Telecommunications"));
        expectedOptions.add(list("SmartMachines, LLC|true", "Rage Against The Machines, LLC"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Run report without request parameters. All null.
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsAllNull() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setDefaultParameterValues(null, null, null, null, null);

        ph.setMandatoryCascade(false, false, false, false, false);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(),
                        Collections.<String, String[]>emptyMap());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA", "Ukraine", "Canada"));
        expectedOptions.add(list("CA", "OR", "WA", "Zakarpatska", "Kyivska", "Kharkivska", "BC"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true", "Consulting", "Distribution", "Manufactoring"));
        // TODO not sure how control logic and query executor should behave here.
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    @Test
    public void ensureSkipAttributesResolvingParameterPropagatesToInputControlContainer() throws CascadeResourceNotFoundException {
        String uri = "/testUri";
        ReportUnit reportUnit = mock(ReportUnit.class);
        doReturn(uri).when(reportUnit).getURI();

        setupGetValuesTest();
        setUpCachedRepositoryService(reportUnit);

        Map<String, String[]> parameters = Collections.singletonMap(SKIP_PROFILE_ATTRIBUTES_RESOLVING, new String[]{Boolean.toString(true)});

        genericInputControlLogic.getValuesForInputControlsFromRawData(uri, ph.getInputControlsSet(), parameters);

        verify(reportUnit).setAttributes(eq(singletonList(SKIP_PROFILE_ATTRIBUTES_RESOLVING)));
    }

    /**
     * See JS-34780
     * @throws CascadeResourceNotFoundException
     */
    @Test
    public void checkExtraParametersHandling() throws CascadeResourceNotFoundException {

        Map<Class<?>, Object> dataConverters = new HashMap<>();
        dataConverters.put(TimeZone.getDefault().getClass(), new TimeZoneConverter());
        Map<Class<?>, Map<Class<?>, Object>> processors = new HashMap<>();
        processors.put(DataConverter.class, dataConverters);
        injectDependencyToPrivateField(genericTypeProcessorRegistry, "processors", processors);
        injectDependencyToPrivateField(dataConverterService, "genericTypeProcessorRegistry", genericTypeProcessorRegistry);
        injectDependencyToPrivateField(valueFormattingUtils, "dataConverterService", dataConverterService);
        genericInputControlLogic.valueFormattingUtils = valueFormattingUtils;

        String reportTZParam = "REPORT_TIME_ZONE";
        String timeZoneID = "America/Los_Angeles";

        InputControlsContainer container = nullable(InputControlsContainer.class);
        Map<String, Object> params = new HashMap<>();
        params.put(reportTZParam, TimeZone.getTimeZone(timeZoneID));

        Map<String, String[]> formattedValues = genericInputControlLogic.formatTypedParameters(container, params);
        // extra params are ignored by default
        assertEquals(0, formattedValues.size());

        genericInputControlLogic.allowExtraReportParameters = true;

        formattedValues = genericInputControlLogic.formatTypedParameters(container, params);
        // Now extra params are kept
        assertEquals(1, formattedValues.size());
        assertEquals(timeZoneID, formattedValues.get(reportTZParam)[0]);

        genericInputControlLogic.allowExtraReportParameters = false;

    }

    @Test
    public void testValueFormattingUtils() {

        BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);

        String targetPackage = DataConverter.class.getPackage().getName();
        TypeFilter tf = new AssignableTypeFilter(DataConverter.class);
        s.addIncludeFilter(tf);
        s.scan(targetPackage);
        String[] beans = bdr.getBeanDefinitionNames();

        ApplicationContext context = mock(ApplicationContext.class);
        doReturn(beans).when(context).getBeanNamesForType(DataConverter.class);

        when(context.getBean(anyString(), any(Class.class)))
                .thenAnswer(invocation ->
                        Class.forName(bdr.getBeanDefinition(invocation.getArgument(0)).getBeanClassName())
                                .newInstance());

        injectDependencyToPrivateField(genericTypeProcessorRegistry, "context", context);
        injectDependencyToPrivateField(dataConverterService, "genericTypeProcessorRegistry", genericTypeProcessorRegistry);
        injectDependencyToPrivateField(valueFormattingUtils, "dataConverterService", dataConverterService);

        assertEquals("1", valueFormattingUtils.formatSingleValue(new Integer(1)));
        assertEquals(Boolean.TRUE.toString(), valueFormattingUtils.formatSingleValue(Boolean.TRUE));

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("Customers", new String[] { "Customer1", "Customer2" });
        Map<String, String[]> result = valueFormattingUtils.formatTypedParameters(parameters);
        assertEquals("Customer1", result.get("Customers")[0] );
        assertEquals("Customer2", result.get("Customers")[1]);

    }

    private void assertQueryEnvelopes(List<List<String>> expectedEnvelopeOptions, List<InputControlState> actualEnvelopes) {
        assertEquals(expectedEnvelopeOptions.size(), actualEnvelopes.size());

        for (int i = 0; i < expectedEnvelopeOptions.size(); i++) {
            assertQueryEnvelope(expectedEnvelopeOptions.get(i), actualEnvelopes.get(i));
        }
    }

    private void assertQueryEnvelope(List<String> expectedOptions, InputControlState actualEnvelope) {
        List<String> actualOptions = convertOptionsList(actualEnvelope.getOptions());

        assertEquals(expectedOptions, actualOptions);
    }

    private List<String> convertOptionsList(List<InputControlOption> optionsList) {
        List<String> options = new ArrayList<String>();
        for (InputControlOption option : optionsList) {
            if (option.isSelected() != null && option.isSelected()) {
                options.add(option.getValue() + "|true");
            } else {
                options.add(option.getValue());
            }
        }
        return options;
    }

    private void setupGetValuesTest() throws CascadeResourceNotFoundException {
        createParameters();

        String reportUri = "/testUri";

        ReportUnit reportUnit = createReportUnit(reportUri);

        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();
        setUpFilterResolver();

        for (InputControl ic : ph.getInputControls()) {
            SrcSets.resolveAll(ic.getSources());
        }
    }

    private void setupDependencyTest() throws CascadeResourceNotFoundException {
        String reportUri = "/testUri";

        ReportUnit reportUnit = createReportUnit(reportUri);

        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();
    }

    private ReportUnit createReportUnit(String uri) {
        ReportUnit reportUnit = mock(ReportUnit.class);
        doReturn(uri).when(reportUnit).getURI();

        ResourceReference dataSource = mock(ResourceReference.class);
        doReturn(dataSource).when(reportUnit).getDataSource();

        return reportUnit;
    }

    /**
     * Create Parameters helper object
     */
    private void createParameters() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "test_report/country", "country");
        ph.setInputControlQuery(CASCADE_COUNTRY, "");

        ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "test_report/state", "state");
        ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

        ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "test_report/accountType", "account_type");
        ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).toString());

        ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "test_report/industry", "industry");
        ph.setInputControlQuery(CASCADE_INDUSTRY, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).toString());

        ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "test_report/name", "name");
        ph.setInputControlQuery(CASCADE_NAME, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).append(",").append(CASCADE_INDUSTRY).toString());
    }

    /**
     * Create Parameters helper object with circular deps
     */
    private void createParametersWithDirectCircularDepedency() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "test_report/country", "country");
        ph.setInputControlQuery(CASCADE_COUNTRY, CASCADE_STATE);

        ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "test_report/state", "state");
        ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

        ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "test_report/accountType", "account_type");
        ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).toString());

        ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "test_report/industry", "industry");
        ph.setInputControlQuery(CASCADE_INDUSTRY, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).toString());

        ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "test_report/name", "name");
        ph.setInputControlQuery(CASCADE_NAME, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).append(",").append(CASCADE_INDUSTRY).toString());
    }

    private void createParametersWithInDirectCircularDepedency() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "test_report/country", "country");
        ph.setInputControlQuery(CASCADE_COUNTRY, CASCADE_NAME);

        ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "test_report/state", "state");
        ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

        ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "test_report/accountType", "account_type");
        ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, CASCADE_STATE);

        ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "test_report/industry", "industry");
        ph.setInputControlQuery(CASCADE_INDUSTRY, CASCADE_ACCOUNT_TYPE);

        ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "test_report/name", "name");
        ph.setInputControlQuery(CASCADE_NAME, CASCADE_INDUSTRY);
    }

    private void createParametersForDependencyTest(Map<String, String> nameAndQuery) {
        ph = new ParametersHelper();
        for (Map.Entry<String, String> entry : nameAndQuery.entrySet()) {
            ph.addParameterAndControlInfo(entry.getKey(), entry.getKey(),
                    InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, null, false, "/" + entry.getKey(), entry.getKey());
            ph.setInputControlQuery(entry.getKey(), entry.getValue());
        }
    }

    private void setUpCachedEngineService() throws CascadeResourceNotFoundException {
        Mockito.reset(cachedEngineService);

        doReturn(ph.getInputControlInfo()).when(cachedEngineService).getReportInputControlsInformation(
                nullable(ExecutionContext.class), nullable(InputControlsContainer.class), nullable(Map.class)
        );

        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            InputControl control = ph.getInputControlByQueryValueColumn((String) args[2]);
            return EngineServiceCascadeTestQueryExecutor.executeOrderedMap(control.getName(), (Map<String, Object>) args[5]);
        }).when(cachedEngineService).executeQuery(
                nullable(ExecutionContext.class), nullable(ResourceReference.class),
                anyString(), any(), nullable(ResourceReference.class), nullable(Map.class), nullable(Map.class), anyString()
        );

        doReturn(ph.getInputControls()).when(cachedEngineService).getInputControls(nullable(InputControlsContainer.class));
    }

    private void setUpCachedRepositoryService(final ReportUnit reportUnit) throws CascadeResourceNotFoundException {
        doAnswer(invocationOnMock -> {
            String uri = (String) invocationOnMock.getArguments()[1];
            if (uri.equals(reportUnit.getURI())) {
                return reportUnit;
            } else {
                return ph.getInputControlByUri(uri);
            }
        }).when(cachedRepositoryService).getResource(nullable(Class.class), anyString());

        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            if (Query.class.isAssignableFrom((Class) args[0])) {
                return ((ResourceReference) args[1]).getLocalResource();
            } else {
                return null;
            }
        }).when(cachedRepositoryService).getResource(any(), any(ResourceReference.class));
    }

    private void setUpFilterResolver() {
        initFilterResolver(filterResolver);
    }

    private void setUpICHandlers() {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        mockedServices.put("cachedRepositoryService", cachedRepositoryService);
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("parameterTypeCompositeLookup", createParameterTypeLookup());
        mockedServices.put("cachedEngineService", cachedEngineService);
        mockedServices.put("cachedEngine", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", createMessageSource());

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/applicationContext-cascade-test.xml");

        injectDependencyToPrivateField(genericInputControlLogic, "inputControlTypeConfiguration", context.getBean("inputControlTypeNewConfiguration"));
    }

}
