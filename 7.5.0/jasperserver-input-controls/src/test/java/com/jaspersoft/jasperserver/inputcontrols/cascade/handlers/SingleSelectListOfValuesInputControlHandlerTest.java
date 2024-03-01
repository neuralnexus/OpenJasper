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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createCalendarFormatProvider;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createEngineService;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createFilterResolver;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createMessageSource;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createParameterTypeLookup;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.setUpApplicationContext;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * @author Anton Fomin
 * @version $Id$
 */
public class SingleSelectListOfValuesInputControlHandlerTest {

    private String DEPARTMENT_INT = "department_int";
    private String DEPARTMENT_DOUBLE = "department_double";

    private CachedRepositoryService cachedRepositoryService = mock(CachedRepositoryService.class);

    private ResourceReference dataSource = mock(ResourceReference.class);

    private ParametersHelper ph;

    private InputControlHandler getHandler() {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        mockedServices.put("cachedRepositoryService", cachedRepositoryService);
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("parameterTypeCompositeLookup", createParameterTypeLookup());
        mockedServices.put("cachedEngineService", mock(CachedEngineService.class));
        mockedServices.put("engineService", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", createMessageSource());

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/applicationContext-cascade-test.xml");

        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>)context.getBean("inputControlTypeNewConfiguration");
        return (InputControlHandler)config.get("3").get("handler");
    }

    /**
     * We should not send ~NOTHING~ substitution anymore - this will be treated as number format validation error
     * @throws CascadeResourceNotFoundException
     */
    @Test
    public void convertParameterValueFromRawDataNothingInputNotMandatoryReturnValue() throws CascadeResourceNotFoundException {

        String[] rawData = new String[] {InputControlHandler.NOTHING_SUBSTITUTION_VALUE};
        Object defaultValue = new Double(10.1);

        ph.setParameterType(DEPARTMENT_DOUBLE, Double.class, null);
        ph.setDefaultParameterValue(DEPARTMENT_DOUBLE, defaultValue);
        ph.setMandatory(DEPARTMENT_DOUBLE, false);

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(DEPARTMENT_DOUBLE),
                    ph.getInputControlInfo().getInputControlInformation(DEPARTMENT_DOUBLE));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.invalidValueForType", exception.getValidationError().getErrorCode());
        assertNull(exception.getValidationError().getInvalidValue());
    }

    @Test
    public void getValuesIntegerNotMandatory() throws CascadeResourceNotFoundException {

        ph.setArgumentParameterValue(DEPARTMENT_INT, 3);

        InputControlState actualState = getHandler().getState(ph.getInputControl(DEPARTMENT_INT), dataSource,
                ph.getAllArgumentParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(DEPARTMENT_INT));

        InputControlState expectedState = new InputControlState();
        expectedState.setId(DEPARTMENT_INT);
        expectedState.setUri("/" + DEPARTMENT_INT);
        List<InputControlOption> options = new ArrayList<InputControlOption>();
        options.add(new InputControlOption(InputControlHandler.NOTHING_SUBSTITUTION_VALUE, InputControlHandler.NOTHING_SUBSTITUTION_LABEL));
        options.add(new InputControlOption("1", "Store Permanent Butchers"));
        options.add(new InputControlOption("2", "HQ Finance and Accounting"));
        options.add(new InputControlOption("3", "Store Temporary Stockers", true));
        expectedState.setOptions(options);

        assertEquals(expectedState, actualState);

    }

    @Test
    public void getValuesIntegerNotMandatoryNothingInput() throws CascadeResourceNotFoundException {

        ph.setArgumentParameterValue(DEPARTMENT_INT, InputControlHandler.NOTHING_SUBSTITUTION_VALUE);

        InputControlState actualState = getHandler().getState(ph.getInputControl(DEPARTMENT_INT), dataSource,
                ph.getAllArgumentParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(DEPARTMENT_INT));

        InputControlState expectedState = new InputControlState();
        expectedState.setId(DEPARTMENT_INT);
        expectedState.setUri("/" + DEPARTMENT_INT);
        List<InputControlOption> options = new ArrayList<InputControlOption>();
        options.add(new InputControlOption(InputControlHandler.NOTHING_SUBSTITUTION_VALUE, InputControlHandler.NOTHING_SUBSTITUTION_LABEL, true));
        options.add(new InputControlOption("1", "Store Permanent Butchers"));
        options.add(new InputControlOption("2", "HQ Finance and Accounting"));
        options.add(new InputControlOption("3", "Store Temporary Stockers"));
        expectedState.setOptions(options);

        assertEquals(expectedState, actualState);

    }

    @Before
    public void setupTest() throws CascadeResourceNotFoundException {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo(DEPARTMENT_INT, "Department", InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES,
                Integer.class, null, null, false);

        Map<String, Object> valuesInt = new LinkedHashMap<String, Object>(3);
        valuesInt.put("Store Permanent Butchers", 1);
        valuesInt.put("HQ Finance and Accounting", 2);
        valuesInt.put("Store Temporary Stockers", 3);
        ph.addListOfValues(DEPARTMENT_INT, valuesInt);

        ph.addParameterAndControlInfo(DEPARTMENT_DOUBLE, "Department", InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES,
                Double.class, null, null, false);

        Map<String, Object> valuesDouble = new LinkedHashMap<String, Object>(3);
        valuesDouble.put("Store Permanent Butchers", 1.1);
        valuesDouble.put("HQ Finance and Accounting", 2.3);
        valuesDouble.put("Store Temporary Stockers", 3.11);
        ph.addListOfValues(DEPARTMENT_DOUBLE, valuesDouble);

        reset(cachedRepositoryService);
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return  args[1] != null ? ((ResourceReference) args[1]).getLocalResource() : null;
        }).when(cachedRepositoryService).getResource(any(), any(ResourceReference.class));
    }
}
