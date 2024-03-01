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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.inputcontrols.action.EngineServiceCascadeTestQueryExecutor;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.*;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ParametersHelper.*;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.*;
import static junit.framework.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * @author Anton Fomin
 * @version $Id: SingleSelectQueryInputControlHandlerTest.java 22715 2012-03-21 19:04:33Z afomin $
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleSelectQueryInputControlHandlerTest {

    @Mock
    private CachedRepositoryService cachedRepositoryService; //= mock(CachedRepositoryService.class);

    private CachedEngineService cachedEngineService = mock(CachedEngineService.class);

    private MessageSource messageSource = mock(MessageSource.class);

    private ParametersHelper ph;

    private ResourceReference dataSource = mock(ResourceReference.class);

    @Test
    public void getStateNullDefaultValueNullMandatory() throws CascadeResourceNotFoundException {
        Object defaultValue = null;
        Object expectedValue = "fillParameters.error.mandatoryField";

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, true);
        ph.setArgumentParameterValue(CASCADE_INDUSTRY, null);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(expectedValue, state.getError());
    }

    @Test
    public void convertParameterValueFromRawDataNullDefaultValueNullNotMandatory() throws CascadeResourceNotFoundException, InputControlValidationException {
        String[] rawData = null;
        Object defaultValue = null;

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, false);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_INDUSTRY),
                    ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertNull(convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNullString() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = null;
        Object defaultValue = "DefaultValue";
        Object expectedValue = "DefaultValue";

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNullNumberDouble() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = null;
        Object defaultValue = new Double(10.1);
        Object expectedValue = new Double(10.1);

        ph.setParameterType(NUMBER, Double.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNullNumberBigDecimal() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = null;
        Object defaultValue = new BigDecimal("10.1");
        Object expectedValue = new BigDecimal("10.1");

        ph.setParameterType(NUMBER, BigDecimal.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNullNumberInteger() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = null;
        Object defaultValue = new Integer(10);
        Object expectedValue = new Integer(10);

        ph.setParameterType(NUMBER, Integer.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNumberDoubleDotSeparator() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {"10.12345"};
        Object defaultValue = null;
        Object expectedValue = new Double(10.12345);

        ph.setParameterType(NUMBER, Double.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNumberDoubleCommaSeparator() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {"10,12345"};
        Object defaultValue = null;
        Object expectedValue = new Double(10.12345);

        ph.setParameterType(NUMBER, Double.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNumberDoubleWrongValue() throws CascadeResourceNotFoundException {


        String[] rawData = new String[] {"ololo111"};
        Object defaultValue = null;
        Object expectedValue = "fillParameters.error.invalidValueForType";

        ph.setParameterType(NUMBER, Double.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, true);

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                    ph.getInputControlInfo().getInputControlInformation(NUMBER));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(expectedValue, exception.getValidationError().getErrorCode());
    }

    @Test
    public void convertParameterValueFromRawDataDefaultNull() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {"RequestValue"};
        Object defaultValue = null;
        Object expectedValue = "RequestValue";

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataMoreValues() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {"RequestValue", "RequestValue1", "RequestValue2"};
        Object defaultValue = "DefaultValue";
        Object expectedValue = "RequestValue";

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void getStateNullSubstitutionMoreValuesMandatory() throws CascadeResourceNotFoundException {
        Object defaultValue = "DefaultValue";
        Object expectedValue = "fillParameters.error.mandatoryField";

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, true);
        ph.setArgumentParameterValue(CASCADE_INDUSTRY, NULL_SUBSTITUTION_VALUE);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(expectedValue, state.getError());
    }

    @Test
    public void convertParameterValueFromRawDataNullSubstitutionMoreValuesNotMandatory() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {NULL_SUBSTITUTION_VALUE, "RequestValue1", "RequestValue2"};
        Object defaultValue = "DefaultValue";
        Object expectedValue = null;

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, false);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDontCatchNullSubstitutionLabelMoreValues() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {NULL_SUBSTITUTION_LABEL, "RequestValue1", "RequestValue2"};
        Object defaultValue = "DefaultValue";
        Object expectedValue = NULL_SUBSTITUTION_LABEL;

        ph.setDefaultParameterValue(CASCADE_INDUSTRY, defaultValue);
        ph.setMandatory(CASCADE_INDUSTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void getStateEmptyNumberMandatory() throws CascadeResourceNotFoundException {

        Object defaultValue = new Double(10.1);
        Object expectedValue = "fillParameters.error.mandatoryField";
        Object parameterValue = "";

        ph.setParameterType(NUMBER, Double.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, true);
        ph.setArgumentParameterValue(NUMBER, parameterValue);
        ph.setInputControlQuery(NUMBER, null);

        final InputControlState state = getHandler().getState(ph.getInputControl(NUMBER), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, state.getError());
    }

    @Test
    public void convertParameterValueFromRawDataEmptyNumberNotMandatory() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {""};
        Object defaultValue = new Double(10.1);

        ph.setParameterType(NUMBER, Double.class, null);
        ph.setDefaultParameterValue(NUMBER, defaultValue);
        ph.setMandatory(NUMBER, false);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertNull(convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNumber() throws CascadeResourceNotFoundException, InputControlValidationException {
        String[] rawData =  {"123"};

        Object actualValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER), ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals((double) 123, actualValue);
    }

    @Test
    public void convertParameterValueFromRawDataWrongNumberCannotParse() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123a"};

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER), ph.getInputControlInfo().getInputControlInformation(NUMBER));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.invalidValueForType", exception.getValidationError().getErrorCode());
        assertNull(exception.getValidationError().getInvalidValue());
    }

    @Test
    public void convertParameterValueFromRawDataWrongNumberCannotValidateMaxValueString() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123"};

        DataType type = new DataTypeImpl();
        type.setDataTypeType(DataType.TYPE_NUMBER);
        type.setMaxValue("30");
        ph.setDataType(NUMBER, type);

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER), ph.getInputControlInfo().getInputControlInformation(NUMBER));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.greaterThan", exception.getValidationError().getErrorCode());
        assertNull(exception.getValidationError().getInvalidValue());
    }

    @Test
    public void convertParameterValueFromRawDataWrongNumberCannotValidateMaxValueNumber() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123"};

        DataType type = new DataTypeImpl();
        type.setDataTypeType(DataType.TYPE_NUMBER);
        type.setMaxValue((double) 30);
        ph.setDataType(NUMBER, type);

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER), ph.getInputControlInfo().getInputControlInformation(NUMBER));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.greaterThan", exception.getValidationError().getErrorCode());
        assertNull(exception.getValidationError().getInvalidValue());
    }

    @Test
    public void convertParameterValueFromRawDataWrongDateCannotParse() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123a"};

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(DATE), ph.getInputControlInfo().getInputControlInformation(DATE));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.invalidValueForType", exception.getValidationError().getErrorCode());
        assertNull(exception.getValidationError().getInvalidValue());
    }

    @Test
    public void convertParameterValueFromRawDate() throws CascadeResourceNotFoundException, InputControlValidationException {
        String[] rawData =  {"2012-07-28"};

        Object actualValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(DATE), ph.getInputControlInfo().getInputControlInformation(DATE));

        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gregorianCalendar.setTimeInMillis(1343458800000L); //gregorianCalendar.set(2012, Calendar.JULY, 28);
        assertEquals(gregorianCalendar.getTime(), actualValue);
    }

    @Test
    public void getValueDefault() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", null, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_INDUSTRY, true);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertMultiSelectEnvelope(list("Communications", "Telecommunications"), list(false, true), state);
    }

    @Test
    public void getValueMandatory() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", null, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "OLOLO", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_INDUSTRY, true);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertMultiSelectEnvelope(list("Communications", "Telecommunications"), list(true, false), state);
    }

    @Test
    public void getValueNotMandatory() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", null, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "OLOLO", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_INDUSTRY, false);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));


        assertMultiSelectEnvelope(listOfArrays(
                array(NOTHING_SUBSTITUTION_VALUE, NOTHING_SUBSTITUTION_LABEL, true),
                array("Communications", "Communications", false),
                array("Telecommunications", "Telecommunications", false)),
        state);
    }

    @Test
    public void getValueNotMandatoryInputNothing() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", NOTHING_SUBSTITUTION_VALUE, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "OLOLO", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_INDUSTRY, false);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));


        assertMultiSelectEnvelope(listOfArrays(
                array(NOTHING_SUBSTITUTION_VALUE, NOTHING_SUBSTITUTION_LABEL, true),
                array("Communications", "Communications", false),
                array("Telecommunications", "Telecommunications", false)),
        state);
    }

    @Test
    public void getValueNotMandatoryInputNothingWithDefault() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", NOTHING_SUBSTITUTION_VALUE, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Communications", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_INDUSTRY, false);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));


        assertMultiSelectEnvelope(listOfArrays(
                array(NOTHING_SUBSTITUTION_VALUE, NOTHING_SUBSTITUTION_LABEL, false),
                array("Communications", "Communications", true),
                array("Telecommunications", "Telecommunications", false)),
        state);
    }

    @Test
    public void getValueMandatoryInputNothing() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", NOTHING_SUBSTITUTION_VALUE, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "OLOLO", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_INDUSTRY, true);

        InputControlState state = getHandler().getState(ph.getInputControl(CASCADE_INDUSTRY), dataSource,
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));


        assertMultiSelectEnvelope(listOfArrays(
                array("Communications", "Communications", true),
                array("Telecommunications", "Telecommunications", false)),
        state);
    }

    @Test
    public void formatValueString() throws CascadeResourceNotFoundException {

        String typedValue = "Synchrophasotron";

        ph.setParameterType(CASCADE_INDUSTRY, String.class, null);
        ph.setDataType(CASCADE_INDUSTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(1, formattedValues.length);
        assertEquals("Synchrophasotron", formattedValues[0]);
    }

    @Test
    public void formatValueDouble() throws CascadeResourceNotFoundException {

        Double typedValue = 4560.12345623;

        ph.setParameterType(CASCADE_INDUSTRY, Double.class, null);
        ph.setDataType(CASCADE_INDUSTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(1, formattedValues.length);
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(LocaleContextHolder.getLocale());
        String separator = Character.toString(df.getDecimalFormatSymbols().getDecimalSeparator());
        assertEquals("4560"+separator+"12345623", formattedValues[0]);
    }

    @Test
    public void formatValueStringNull() throws CascadeResourceNotFoundException {

        String typedValue = null;

        ph.setParameterType(CASCADE_INDUSTRY, String.class, null);
        ph.setDataType(CASCADE_INDUSTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(1, formattedValues.length);
        assertEquals(NULL_SUBSTITUTION_VALUE, formattedValues[0]);
    }

    @Test
    public void formatValueDoubleNull() throws CascadeResourceNotFoundException {

        Double typedValue = null;

        ph.setParameterType(CASCADE_INDUSTRY, Double.class, null);
        ph.setDataType(CASCADE_INDUSTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_INDUSTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_INDUSTRY));

        assertEquals(1, formattedValues.length);
        assertEquals("", formattedValues[0]);
    }

    @Before
    public void before() throws CascadeResourceNotFoundException {
        createParameters();
        mockExecuteQuery();
        mockMessageSource();
        mockGetResource();
    }

    /**
     * Create Parameters helper object
     */
    private void createParameters() {
        if (ph == null) {
            ph = new ParametersHelper();

            ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "/country", "country");
            ph.setInputControlQuery(CASCADE_COUNTRY, "");

            ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "/state", "state");
            ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

            ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type", InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "/accountType", "accountType");
            ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).toString());

            ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry", InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "/industry", "industry");
            ph.setInputControlQuery(CASCADE_INDUSTRY, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).toString());

            ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name", InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "/name", "name");
            ph.setInputControlQuery(CASCADE_NAME, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).append(",").append(CASCADE_INDUSTRY).toString());

            ph.addParameterAndControlInfo(NUMBER, "Decimal Number", InputControl.TYPE_SINGLE_SELECT_QUERY, Double.class, null, 10.1, true, "/number", "number");

            ph.addParameterAndControlInfo(DATE, "Date", InputControl.TYPE_SINGLE_SELECT_QUERY, java.util.Date.class, null, new GregorianCalendar(2011, 6, 28).getTime(), true, "/date", "date");
        }
    }

    private InputControlHandler getHandler() throws CascadeResourceNotFoundException {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        mockedServices.put("cachedRepositoryService", cachedRepositoryService);
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("parameterTypeCompositeLookup", createParameterTypeLookup());
        mockedServices.put("cachedEngineService", cachedEngineService);
        mockedServices.put("engineService", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", messageSource);

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/applicationContext-cascade-test.xml");

        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>)context.getBean("inputControlTypeNewConfiguration");
        return (InputControlHandler)config.get("4").get("handler");
    }

    private void mockExecuteQuery() throws CascadeResourceNotFoundException {
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            String controlName = (String) args[7];
            return EngineServiceCascadeTestQueryExecutor.executeOrderedMap(controlName, (Map<String, Object>) args[5]);
        }).when(cachedEngineService).executeQuery(
                any(ExecutionContext.class), any(ResourceReference.class), anyString(), any(),
                any(ResourceReference.class), anyMap(), anyMap(), anyString()
        );
    }

    private void mockMessageSource() {
        doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0])
                .when(messageSource).getMessage(anyString(), any(), any(Locale.class));
    }

    private void mockGetResource() throws CascadeResourceNotFoundException {
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            if (Resource.class.isAssignableFrom((Class) args[0])) {
                return ((ResourceReference) args[1]).getLocalResource();
            }
            return null;
        }).when(cachedRepositoryService).getResource(any(), any(ResourceReference.class));
    }

    private void assertMultiSelectEnvelope(List<String> expectedOptionNames, List<Boolean> expectedSelectedOptions, InputControlState actualEnvelope) {
        if (expectedOptionNames.size() != expectedSelectedOptions.size()) fail("Size of expected names list and expected selection list do ot match!");

        List<Object[]> expectedOptions = new ArrayList<Object[]>(expectedOptionNames.size());
        for (int i = 0; i < expectedOptionNames.size(); i++) {
            expectedOptions.add(new Object[]{expectedOptionNames.get(i), expectedOptionNames.get(i), expectedSelectedOptions.get(i)});
        }
        assertMultiSelectEnvelope(expectedOptions, actualEnvelope);
    }

    private <T> void assertMultiSelectEnvelope(List<T[]> expectedOptions, InputControlState actualEnvelope) {

        assertNull(actualEnvelope.getValue());

        assertEquals("Number of expected and actual options don't match.", expectedOptions.size(), actualEnvelope.getOptions().size());

        for (int i = 0; i < expectedOptions.size(); i++) {
            assertEquals(expectedOptions.get(i)[0], actualEnvelope.getOptions().get(i).getValue());
            assertEquals(expectedOptions.get(i)[1], actualEnvelope.getOptions().get(i).getLabel());
            assertEquals(expectedOptions.get(i)[2], actualEnvelope.getOptions().get(i).isSelected());
            i++;
        }
    }
}
