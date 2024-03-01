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
package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.common.timezone.ClientTimezoneFormattingRulesResolver;
import com.jaspersoft.jasperserver.api.common.timezone.TimeZoneTransformer;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JasperReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.core.util.type.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.DateRangeBuilder;
import net.sf.jasperreports.types.date.TimestampRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createCalendarFormatProvider;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.getInputControl;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.getInputControlInfo;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.injectDependencyToPrivateField;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.setUpApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

/**
 * Tests for {@link DataConverterServiceImpl}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class DataConverterServiceTest {

    @InjectMocks
    private DataConverterServiceImpl dataConverterService;

    @Mock
    private CachedRepositoryService cachedRepositoryService;

    @Mock
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    @Mock
    private MessageSource messageSource;

    @Test
    public void conversionOfParameterValueOfClassNumberIsSupportedAndConvertedToBigDecimal() throws Exception{
        doReturn(new NumberDataConverter()).when(genericTypeProcessorRegistry).getTypeProcessor(Number.class, DataConverter.class);
        JasperReportInputControlInformation reportInputControlInformation = new JasperReportInputControlInformation();
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setValueClass(Number.class);
        reportInputControlInformation.setReportParameter(parameter);
        final Object typedValue = dataConverterService.convertSingleValue("2", null, reportInputControlInformation);
        assertTrue(typedValue instanceof BigDecimal);
        assertTrue(BigDecimal.valueOf(2).equals(typedValue));
    }

    @Test
    public void conversionOfParameterValueOfNestedTypeNumberIsSupportedAndConvertedToBigDecimal() throws Exception{
        doReturn(new NumberDataConverter()).when(genericTypeProcessorRegistry).getTypeProcessor(Number.class, DataConverter.class);
        JasperReportInputControlInformation reportInputControlInformation = new JasperReportInputControlInformation();
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setValueClass(Collection.class);
        parameter.setNestedType(Number.class);
        reportInputControlInformation.setReportParameter(parameter);
        final Object typedValue = dataConverterService.convertSingleValue("2", null, reportInputControlInformation);
        assertTrue(typedValue instanceof BigDecimal);
        assertTrue(BigDecimal.valueOf(2).equals(typedValue));
    }

    /**
     * Next three tests check that DataConverterService should chose data converter accordingly to class
     * of the supplied value, when control is multi select query which hasn't data type
     * and JRParameter has class java.util.Collection without nested type.
     * @throws Exception
     */
    @Test
    public void ensureDataConverterAccordinglyToValueClassDate() throws Exception {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.set(2012, Calendar.NOVEMBER, 4, 19, 41, 17);

        Date value = new Date(calendar.getTimeInMillis());
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("2012-11-04", formattedValue);
    }

    @Test
    public void ensureDataConverterAccordinglyToValueClassTimestamp() throws Exception {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(2012, Calendar.NOVEMBER, 4, 19, 41, 17);

        Timestamp value = new Timestamp(calendar.getTimeInMillis());
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("2012-11-04 19:41", formattedValue);
    }

    @Test
    public void ensureDataConverterAccordinglyToValueClassTime() throws Exception {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(2012, Calendar.NOVEMBER, 4, 19, 41, 17);

        Time value = new Time(calendar.getTimeInMillis());
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("19:41", formattedValue);
    }

    /**
     * Next three tests check that null is properly formatted fot all three currently supported date types:
     * Date, Timestamp and Time. The result is empty string for all types.
     * @throws Exception
     */
    @Test
    public void ensureNullValueConvertedToEmptyStringForDate() throws Exception {
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "correctSingleValueDateICAndICInfo", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "correctSingleValueDateICAndICInfo", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(null, inputControl, info);

        assertEquals("", formattedValue);
    }

    @Test
    public void ensureNullValueConvertedToEmptyStringForTimestamp() throws Exception {
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "correctSingleValueTimestampICAndICInfo", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "correctSingleValueTimestampICAndICInfo", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(null, inputControl, info);

        assertEquals("", formattedValue);
    }

    @Test
    public void ensureNullValueConvertedToEmptyStringForTime() throws Exception {
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "correctSingleValueTimeICAndICInfo", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "correctSingleValueTimeICAndICInfo", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(null, inputControl, info);

        assertEquals("", formattedValue);
    }

    /**
     * Next two tests check case when IC has data type Date but JRParameter has class java.sql.Timestamp.
     * @throws Exception
     */
    @Test
    public void ensureUseTimestampParserForICDateAndICInfoTimestampForFormatting() throws Exception {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(2012, Calendar.NOVEMBER, 7, 2, 10, 10);

        Timestamp value = new Timestamp(calendar.getTimeInMillis());

        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "singleValueDateAndICTimestamp", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "singleValueDateAndICTimestamp", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("2012-11-07", formattedValue);
    }

    @Test
    public void ensureUseTimestampParserForICDateAndICInfoTimestampForParsing() throws Exception {

        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "singleValueDateAndICTimestamp", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "singleValueDateAndICTimestamp", applicationContext);

        Object convertedValue = dataConverterService.convertSingleValue("2012-11-07", inputControl, info);

        GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.NOVEMBER, 7, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getDefault());
        Timestamp expected = new Timestamp(calendar.getTimeInMillis());

        assertEquals(expected, convertedValue);
    }

    /**
     * This case was introduced with RelativeDates feature, DataConverter should choose class of JRParameter
     * over class of supplied value if the JRParameter class is assignable from the value class.
     * @throws Exception
     */
    @Test
    public void ensureThatJRParameterClassIsChosenIfItsAssignableFromValueClass() throws Exception {

        TimestampRange dateRange = (TimestampRange) new DateRangeBuilder("DAY").set(Timestamp.class).toDateRange();

        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "jrParameterClassIsAssignableFromValueClass", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "jrParameterClassIsAssignableFromValueClass", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(dateRange, inputControl, info);

        assertEquals("DAY", formattedValue);
    }

    @Test
    public void shouldConvertSingleValueToTime_WhenClientTimezoneIsDefault_returnTimeInClientTimezone() throws Exception {
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        DataTypeImpl timeDataType = new DataTypeImpl();
        timeDataType.setDataTypeType(DataType.TYPE_TIME);

        assertEquals(dataConverterService.convertSingleValue("11:11:00", timeDataType), Time.valueOf("11:11:00"));
    }

    @Test
    public void shouldFormatSingleValue_WhenClientTimezoneIsDefault_returnTimeStringInClientTimezone() throws Exception {
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        DataTypeImpl timeDataType = new DataTypeImpl();
        timeDataType.setDataTypeType(DataType.TYPE_TIME);

        assertEquals(dataConverterService.formatSingleValue(Time.valueOf("11:11:00")), "11:11");
    }

    private void setUpGenericTypeProcessorRegistry() throws Exception {
        final CalendarFormatProvider calendarFormatProvider = createCalendarFormatProvider();

        ClientTimezoneFormattingRulesResolver rulesHolder = Mockito.mock(ClientTimezoneFormattingRulesResolver.class);

        DataConverter<Date> dateConverter = new DateDataConverter();
        TimestampDataConverter timestampDataConverter = new TimestampDataConverter();
        DataConverter<Timestamp> dateTimeConverter = timestampDataConverter;
        DateParser<Timestamp> dateTimeParser = timestampDataConverter;
        DataConverter<Time> timeDataConverter = new TimeDataConverter();
        DataConverter<DateRange> dateRangeDataConverter = new DateRangeDataConverter();

        TimeZoneTransformer timeZoneTransformer = new TimeZoneTransformer(rulesHolder);

        injectDependencyToPrivateField(dateConverter, "calendarFormatProvider", calendarFormatProvider);

        injectDependencyToPrivateField(timestampDataConverter, "clientTimezoneFormattingRulesResolver", rulesHolder);
        injectDependencyToPrivateField(timestampDataConverter, "calendarFormatProvider", calendarFormatProvider);

        injectDependencyToPrivateField(timeDataConverter, "calendarFormatProvider", calendarFormatProvider);
        injectDependencyToPrivateField(timeDataConverter, "clientTimezoneFormattingRulesResolver", rulesHolder);
        injectDependencyToPrivateField(timeDataConverter, "timeZoneTransformer", timeZoneTransformer);

        injectDependencyToPrivateField(dateRangeDataConverter, "calendarFormatProvider", calendarFormatProvider);

        doReturn(dateConverter).when(genericTypeProcessorRegistry).getTypeProcessor(Date.class, DataConverter.class);
        doReturn(dateTimeConverter).when(genericTypeProcessorRegistry).getTypeProcessor(Timestamp.class, DataConverter.class);
        doReturn(dateTimeParser).when(genericTypeProcessorRegistry).getTypeProcessor(Timestamp.class, DateParser.class, false);
        doReturn(timeDataConverter).when(genericTypeProcessorRegistry).getTypeProcessor(Time.class, DataConverter.class);
        doReturn(dateRangeDataConverter).when(genericTypeProcessorRegistry).getTypeProcessor(DateRange.class, DataConverter.class);
    }

    private void setUpCachedRepositoryService() throws Exception {
        doAnswer(invocationOnMock -> {
            ResourceReference dataTypeRef = (ResourceReference) invocationOnMock.getArguments()[1];
            return dataTypeRef.getLocalResource();
        }).when(cachedRepositoryService).getResource(eq(DataType.class), any(ResourceReference.class));
    }
}
