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

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import com.jaspersoft.jasperserver.inputcontrols.util.MessagesCalendarFormatProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.dto.common.validations.MandatoryValidationRule;
import com.jaspersoft.jasperserver.dto.common.validations.ValidationRule;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.NULL_SUBSTITUTION_VALUE;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createEngineService;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createFilterResolver;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.setUpApplicationContext;
import static junit.framework.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Anton Fomin
 * @version $Id$
 */
public class SingleValueInputControlHandlerTest extends UnitilsJUnit4 {

    private Mock<CachedRepositoryService> cachedRepositoryService = MockUnitils.createMock(CachedRepositoryService.class);

    private Mock<CachedEngineService> cachedEngineService = MockUnitils.createMock(CachedEngineService.class);

    private Mock<MessageSource> messageSource = MockUnitils.createMock(MessageSource.class);

    private CalendarFormatProvider calendarFormatProvider;

    private ParametersHelper ph;

    private InputControlHandler getHandler() {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        mockedServices.put("cachedRepositoryService", cachedRepositoryService.getMock());
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("cachedEngineService", cachedEngineService.getMock());
        mockedServices.put("engineService", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", calendarFormatProvider);
        mockedServices.put("messageSource", messageSource.getMock());

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/applicationContext-cascade-test.xml");

        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>)context.getBean("inputControlTypeNewConfiguration");
        return (InputControlHandler)config.get("2").get("handler");
    }

    @Test
    public void mandatoryValidationRulePresentInMandatoryControlStructure() throws CascadeResourceNotFoundException {
        ph.setMandatory(ParametersHelper.STRING, true);
        final ReportInputControl control = getHandler().buildReportInputControl(ph.getInputControl(ParametersHelper.STRING), "2", null);
        final List<ValidationRule> validationRules = control.getValidationRules();
        assertFalse(validationRules == null);
        assertFalse(validationRules.isEmpty());
        List<ValidationRule> mandatoryRules = new ArrayList<ValidationRule>();
        for(ValidationRule rule : validationRules)
            if(rule instanceof MandatoryValidationRule)
                mandatoryRules.add(rule);
        assertEquals(1, mandatoryRules.size());
    }

    @Test
    public void mandatoryValidationRuleIsNotPresentInNonMandatoryControlStructure() throws CascadeResourceNotFoundException {
        ph.setMandatory(ParametersHelper.STRING, false);
        final ReportInputControl control = getHandler().buildReportInputControl(ph.getInputControl(ParametersHelper.STRING), "2", null);
        final List<ValidationRule> validationRules = control.getValidationRules();
        List<ValidationRule> mandatoryRules = new ArrayList<ValidationRule>();
        if (validationRules != null)
            for (ValidationRule rule : validationRules)
                if (rule instanceof MandatoryValidationRule)
                    mandatoryRules.add(rule);
        assertTrue(mandatoryRules.isEmpty());
    }

    @Test
    public void convertParameterValueFromRawDataNumber() throws CascadeResourceNotFoundException, InputControlValidationException {
        String[] rawData =  {"123"};

        Object actualValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(ParametersHelper.NUMBER), ph.getInputControlInfo().getInputControlInformation(ParametersHelper.NUMBER));

        assertEquals((double) 123, actualValue);
    }

    @Test
    public void convertParameterValueFromRawDataWrongNumberCannotParse() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123a"};

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(ParametersHelper.NUMBER), ph.getInputControlInfo().getInputControlInformation(ParametersHelper.NUMBER));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.invalidValueForType", exception.getValidationError().getErrorCode());
        assertEquals("123a", exception.getValidationError().getInvalidValue());
    }

    @Test
    public void convertParameterValueFromRawDataWrongNumberCannotValidateMaxValueString() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123"};

        DataType type = new DataTypeImpl();
        type.setDataTypeType(DataType.TYPE_NUMBER);
        type.setMaxValue("30");
        ph.setDataType(ParametersHelper.NUMBER, type);

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(ParametersHelper.NUMBER), ph.getInputControlInfo().getInputControlInformation(ParametersHelper.NUMBER));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.greaterThan", exception.getValidationError().getErrorCode());
        assertEquals("123", exception.getValidationError().getInvalidValue());
    }

    @Test
    public void convertParameterValueFromRawDataWrongNumberCannotValidateMaxValueNumber() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123"};

        DataType type = new DataTypeImpl();
        type.setDataTypeType(DataType.TYPE_NUMBER);
        type.setMaxValue((double) 30);
        ph.setDataType(ParametersHelper.NUMBER, type);

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(ParametersHelper.NUMBER), ph.getInputControlInfo().getInputControlInformation(ParametersHelper.NUMBER));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.greaterThan", exception.getValidationError().getErrorCode());
        assertEquals("123", exception.getValidationError().getInvalidValue());
    }

    @Test
    public void convertParameterValueFromRawDataWrongDateCannotParse() throws CascadeResourceNotFoundException {
        String[] rawData =  {"123a"};

        InputControlValidationException exception = null;
        try {
            getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(ParametersHelper.DATE), ph.getInputControlInfo().getInputControlInformation(ParametersHelper.DATE));
        } catch (InputControlValidationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("fillParameters.error.invalidValueForType", exception.getValidationError().getErrorCode());
        assertEquals("123a", exception.getValidationError().getInvalidValue());
    }

    @Test
    public void formatValueString() throws CascadeResourceNotFoundException {


        String typedValue = "Synchrophasotron";

        ph.setParameterType(ParametersHelper.STRING, String.class, null);
        ph.setDataType(ParametersHelper.STRING, DataType.TYPE_TEXT);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.STRING),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.STRING));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("Synchrophasotron", formattedValues[0]);
    }

    @Test
    public void formatValueDouble() throws CascadeResourceNotFoundException {


        Double typedValue = 4560.12345623;

        ph.setParameterType(ParametersHelper.NUMBER, Double.class, null);
        ph.setDataType(ParametersHelper.NUMBER, DataType.TYPE_NUMBER);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.NUMBER),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.NUMBER));

        assertEquals(1, formattedValues.length);
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(LocaleContextHolder.getLocale());
        String separator = Character.toString(df.getDecimalFormatSymbols().getDecimalSeparator());
        assertReflectionEquals("4560"+separator+"12345623", formattedValues[0]);
    }

    @Test
    public void formatValueDate() throws CascadeResourceNotFoundException {


        java.util.Date typedValue = new GregorianCalendar(2011, GregorianCalendar.JULY, 28).getTime();

        ph.setParameterType(ParametersHelper.DATE, java.util.Date.class, null);
        ph.setDataType(ParametersHelper.DATE, DataType.TYPE_DATE);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.DATE),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.DATE));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("2011-07-28", formattedValues[0]);
    }

    @Test
    public void formatValueDateTime() throws CascadeResourceNotFoundException {


        java.sql.Timestamp typedValue = new java.sql.Timestamp(new GregorianCalendar(2011, GregorianCalendar.JULY, 28, 18, 22).getTimeInMillis());

        ph.setParameterType(ParametersHelper.DATE_TIME, java.sql.Timestamp.class, null);
        ph.setDataType(ParametersHelper.DATE_TIME, DataType.TYPE_DATE_TIME);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.DATE_TIME),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.DATE_TIME));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("2011-07-28 18:22", formattedValues[0]);
    }

    @Test
    public void formatValueTime() throws CascadeResourceNotFoundException {


        java.sql.Time typedValue = new java.sql.Time(new GregorianCalendar(2011, GregorianCalendar.JULY, 28, 18, 22).getTimeInMillis());

        ph.setParameterType(ParametersHelper.TIME, java.sql.Time.class, null);
        ph.setDataType(ParametersHelper.TIME, DataType.TYPE_TIME);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.TIME),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.TIME));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("18:22", formattedValues[0]);
    }

    @Test
    public void formatValueStringNull() throws CascadeResourceNotFoundException {


        String typedValue = null;

        ph.setParameterType(ParametersHelper.STRING, String.class, null);
        ph.setDataType(ParametersHelper.STRING, DataType.TYPE_TEXT);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.STRING),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.STRING));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals(NULL_SUBSTITUTION_VALUE, formattedValues[0]);
    }

    @Test
    public void formatValueDoubleNull() throws CascadeResourceNotFoundException {


        Double typedValue = null;

        ph.setParameterType(ParametersHelper.NUMBER, Double.class, null);
        ph.setDataType(ParametersHelper.NUMBER, DataType.TYPE_NUMBER);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.NUMBER),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.NUMBER));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("", formattedValues[0]);
    }

    @Test
    public void formatValueDateNull() throws CascadeResourceNotFoundException {


        java.util.Date typedValue = null;

        ph.setParameterType(ParametersHelper.DATE, java.util.Date.class, null);
        ph.setDataType(ParametersHelper.DATE, DataType.TYPE_DATE);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.DATE),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.DATE));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("", formattedValues[0]);
    }

    @Test
    public void formatValueDateTimeNull() throws CascadeResourceNotFoundException {


        java.sql.Timestamp typedValue = null;

        ph.setParameterType(ParametersHelper.DATE_TIME, java.sql.Timestamp.class, null);
        ph.setDataType(ParametersHelper.DATE_TIME, DataType.TYPE_DATE_TIME);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.DATE_TIME),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.DATE_TIME));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("", formattedValues[0]);
    }

    @Test
    public void formatValueTimeNull() throws CascadeResourceNotFoundException {


        java.sql.Time typedValue = null;

        ph.setParameterType(ParametersHelper.TIME, java.sql.Time.class, null);
        ph.setDataType(ParametersHelper.TIME, DataType.TYPE_TIME);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(ParametersHelper.TIME),
                ph.getInputControlInfo().getInputControlInformation(ParametersHelper.TIME));

        assertEquals(1, formattedValues.length);
        assertReflectionEquals("", formattedValues[0]);
    }

    @Before
    public void before() throws CascadeResourceNotFoundException {
        createParameters();
        mockGetDataType();
        mockMessageSource();
        mockCalendarFormatProvider();
    }

    /**
     * Create Parameters helper object
     */
    private void createParameters() {
        if (ph == null) {
            ph = new ParametersHelper();
            ph.addParameterAndControlInfo(ParametersHelper.STRING, "String Parameter", InputControl.TYPE_SINGLE_VALUE, String.class, DataType.TYPE_TEXT, "defaultValue", true);
            ph.addParameterAndControlInfo(ParametersHelper.NUMBER, "Decimal Number", InputControl.TYPE_SINGLE_VALUE, Double.class, null, 10.1, true);
            ph.addParameterAndControlInfo(ParametersHelper.DATE, "Order Date", InputControl.TYPE_SINGLE_VALUE, java.util.Date.class, DataType.TYPE_DATE, new GregorianCalendar(2011, 6, 28).getTime(), true);
            ph.addParameterAndControlInfo(ParametersHelper.DATE_TIME, "Order DateTime", InputControl.TYPE_SINGLE_VALUE, java.sql.Timestamp.class, DataType.TYPE_DATE_TIME,
                    new java.sql.Timestamp(new GregorianCalendar(2011, 6, 28, 18, 22).getTimeInMillis()), true);
            ph.addParameterAndControlInfo(ParametersHelper.TIME, "Order Time", InputControl.TYPE_SINGLE_VALUE, java.sql.Time.class, DataType.TYPE_TIME,
                    new java.sql.Time(new GregorianCalendar(2011, 6, 28, 18, 22).getTimeInMillis()), true);

        }
    }

    private void mockMessageSource() {
        messageSource.performs(new MockBehavior() {
            public String execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                if ("date.format".equals(args.get(0))) {
                    return "yyyy-MM-dd";
                } else if ("datetime.format".equals(args.get(0))) {
                    return "yyyy-MM-dd HH:mm";
                } else if ("time.format".equals(args.get(0))) {
                    return "HH:mm";
                }
                return (String) args.get(0);
            }
        }).getMessage(null, null, null);
    }

    private void mockGetDataType() throws CascadeResourceNotFoundException {
        cachedRepositoryService.performs(new MockBehavior() {
            @Override
            public DataType execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                if (DataType.class.isAssignableFrom((Class) args.get(0))) {
                    return (DataType) ((ResourceReference) args.get(1)).getLocalResource();
                }
                return null;
            }
        }).getResource(null, (ResourceReference) null);
    }

    private void mockCalendarFormatProvider() {
        calendarFormatProvider = new MessagesCalendarFormatProvider();
        ((MessagesCalendarFormatProvider) calendarFormatProvider).setDatePatternKey("date.format");
        ((MessagesCalendarFormatProvider) calendarFormatProvider).setDatetimePatternKey("datetime.format");
        ((MessagesCalendarFormatProvider) calendarFormatProvider).setTimePatternKey("time.format");
        ((MessagesCalendarFormatProvider) calendarFormatProvider).setMessages(messageSource.getMock());
    }
}
