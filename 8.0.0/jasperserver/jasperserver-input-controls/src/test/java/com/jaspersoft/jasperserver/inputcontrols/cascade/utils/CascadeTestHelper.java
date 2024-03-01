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

package com.jaspersoft.jasperserver.inputcontrols.cascade.utils;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlsInformationImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.token.FilterResolver;
import com.jaspersoft.jasperserver.inputcontrols.cascade.token.ParameterTypeLookup;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Helper class for cascade tests
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class CascadeTestHelper {
    public static final String QUERY_EXECUTOR_KEY_NAME = "queryExecutor";
    public static final String QUERY_EXECUTOR_KEY_GENERATOR_KEY_NAME = "keyGenerator";
    public static final String CONTROLS_KEY_NAME = "controls";
    public static final String PARAMETER_TYPES = "parameterTypes";
    public static final String INPUT_CONTROL_KEY_NAME = "inputControl";
    public static final String REPORT_INPUT_CONTROL_INFORMATION_KEY_NAME = "controlsInformation";
    public static final String TYPED_PARAMS_MAP_KEY_NAME = "typedParametersMap";
    public static final String PAW_PARAMS_ARRAY_KEY_NAME = "rawParametersArray";
    public static final String EXPECTED_RESULT_KEY_NAME = "expectedResult";

    public static void injectDependencyToPrivateField(Object holder, String name, Object value) {
        Field field = getField(holder.getClass(), name);
        if (field == null) {
            throw new RuntimeException(String.format("Field %s for class %s not found", name, holder.getClass()));
        }
        try {
            field.setAccessible(true);
            field.set(holder, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getField(Class<?> clazz, String name) {
        if (clazz != null && !clazz.equals(Object.class)) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException exception) {
                return getField(clazz.getSuperclass(), name);
            }
        }

        return null;
    }

    /**
     * Define mock for filterresolver so we can use following string instead of correct SQL:
     * "Country_multi_select, Cascading_state_multi_select" and mock ordering cascade dependency.
     */
    public static FilterResolver initFilterResolver(FilterResolver filterResolver) {
        lenient().doAnswer(invocationOnMock -> {
            String params = (String) invocationOnMock.getArguments()[0];
            return (params == null || params.isEmpty()) ? Collections.emptySet() : new HashSet<>(Arrays.asList(params.split("[,;\\s]+")));
        }).when(filterResolver).getParameterNames(anyString(), anyMap());

        lenient().doAnswer(invocationOnMock -> {
            // We know the signature of resolveCascadingOrder method
            @SuppressWarnings("unchecked")
            HashMap<String, Set<String>> unorderedMasterDependencies = (HashMap<String, Set<String>>) invocationOnMock.getArguments()[0];
            return new LinkedHashSet<>(unorderedMasterDependencies.keySet());
        }).when(filterResolver).resolveCascadingOrder(anyMap());

        return filterResolver;
    }

    public static FilterResolver createFilterResolver() {
        return initFilterResolver(mock(FilterResolver.class));
    }

    public static ParameterTypeLookup createParameterTypeLookup() {
        return mock(ParameterTypeLookup.class);
    }

    public static CalendarFormatProvider createCalendarFormatProvider() {
        CalendarFormatProvider calendarFormatProvider = mock(CalendarFormatProvider.class);

        lenient().doReturn("%m-%d-%Y").when(calendarFormatProvider).getCalendarDatePattern();
        lenient().doReturn("%m-%d-%Y %H:%M").when(calendarFormatProvider).getCalendarDatetimePattern();
        lenient().doReturn("%H:%M").when(calendarFormatProvider).getCalendarTimePattern();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        lenient().doReturn(simpleDateFormat).when(calendarFormatProvider).getDateFormat();

        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        lenient().doReturn(simpleDateTimeFormat).when(calendarFormatProvider).getDatetimeFormat();

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        lenient().doReturn(simpleTimeFormat).when(calendarFormatProvider).getTimeFormat();

        return calendarFormatProvider;
    }

    public static MessageSource createMessageSource() {
        return new MessageSource() {
            @Override
            public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
                return code;
            }

            @Override
            public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
                return code;
            }

            @Override
            public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
                return resolvable.getDefaultMessage();
            }
        };
    }

    public static ApplicationContext setUpApplicationContext(Map<String, Object> mockedServices, String... contextPath) {
        GenericApplicationContext parentApplicationContext = new GenericApplicationContext();

        if (mockedServices != null && !mockedServices.isEmpty()) {
            if (!mockedServices.containsKey("dataTypeResourceConverter")){
                final ToClientConverter toClientConverter = mock(ToClientConverter.class);
                mockedServices.put("dataTypeResourceConverter", toClientConverter);
            }
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) parentApplicationContext.getBeanFactory();
            for (Map.Entry<String, Object> entry : mockedServices.entrySet()) {
                beanFactory.registerSingleton(entry.getKey(), entry.getValue());
            }
            parentApplicationContext.refresh();

            return new ClassPathXmlApplicationContext(contextPath, true, parentApplicationContext);
        } else {
            return new ClassPathXmlApplicationContext(contextPath, true);
        }
    }

    /**
     * Set up CachedEngineService with properly mocked methods "executeQuery" and "getReportInputControlsInformation".
     * It get its values from configured sprin g context using testCaseName
     * @param cachedEngineService
     * @param applicationContext
     * @param testCaseName
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void setUpCachedEngineService(CachedEngineService cachedEngineService, final ApplicationContext applicationContext, final String testCaseName) throws Exception {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        final Map<String, Map<String, OrderedMap>> queryExecutorMap = (Map<String, Map<String, OrderedMap>>)testCaseMap.get(QUERY_EXECUTOR_KEY_NAME);
        final QueryExecutorTestCaseKeyGenerator keyGenerator = (QueryExecutorTestCaseKeyGenerator)testCaseMap.get(QUERY_EXECUTOR_KEY_GENERATOR_KEY_NAME);
        Map<String, Map<String, Object>> controls = (Map<String, Map<String, Object>>)testCaseMap.get(CONTROLS_KEY_NAME);

        lenient().doAnswer(invocationOnMock -> {
            String controlName = (String) invocationOnMock.getArguments()[7];
            Map<String, Object> paramValues = (Map<String, Object>) invocationOnMock.getArguments()[5];

            Map<String, OrderedMap> queryResultsMapForControlName = queryExecutorMap.get(controlName);
            String key = keyGenerator.generateKey(controlName, paramValues);
            OrderedMap result = queryResultsMapForControlName.get(key);
            if (result == null) {
                result = new LinkedMap(1);
            }

            return result;
        }).when(cachedEngineService).executeQuery(
                nullable(ExecutionContext.class), nullable(ResourceReference.class),
                anyString(), any(), nullable(ResourceReference.class), nullable(Map.class), nullable(Map.class), anyString()
        );

        ReportInputControlsInformationImpl rici = new ReportInputControlsInformationImpl();
        for (Map.Entry<String, Map<String, Object>> entry: controls.entrySet()) {
            String controlName = entry.getKey();
            rici.setInputControlInformation(controlName, (ReportInputControlInformation)entry.getValue().get(REPORT_INPUT_CONTROL_INFORMATION_KEY_NAME));
        }

        lenient().doReturn(rici).when(cachedEngineService).getReportInputControlsInformation(
                any(ExecutionContext.class), any(InputControlsContainer.class), anyMap()
        );

        List<InputControl> inputControls = new ArrayList<InputControl>();
        for (Map.Entry<String, Map<String, Object>> entry: controls.entrySet()) {
            inputControls.add((InputControl) entry.getValue().get("inputControl"));
        }
        lenient().doReturn(inputControls).when(cachedEngineService).getInputControls(any(InputControlsContainer.class));
    }

    public static CachedEngineService setUpCachedEngineService(ApplicationContext applicationContext, String testCaseName) throws Exception {
        CachedEngineService cachedEngineService = mock(CachedEngineService.class);
        setUpCachedEngineService(cachedEngineService, applicationContext, testCaseName);
        return cachedEngineService;
    }

    public static EngineService createEngineService() {
        return mock(EngineService.class);
    }

    public static void setUpCachedRepositoryService(CachedRepositoryService cachedRepositoryService, ApplicationContext applicationContext, String testCaseName) throws Exception {
        lenient().doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            if (Resource.class.isAssignableFrom((Class) args[0])) {
                return args[1] != null ? ((ResourceReference) args[1]).getLocalResource() : null;
            } else {
                return null;
            }
        }).when(cachedRepositoryService).getResource(any(), nullable(ResourceReference.class));
    }

    public static CachedRepositoryService setUpCachedRepositoryService(ApplicationContext applicationContext, String testCaseName) throws Exception {
        CachedRepositoryService cachedRepositoryService = mock(CachedRepositoryService.class);
        setUpCachedRepositoryService(cachedRepositoryService, applicationContext, testCaseName);
        return cachedRepositoryService;
    }

    @SuppressWarnings("unchecked")
    public static InputControl getInputControl(String name, String testCaseName, ApplicationContext applicationContext) {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        Map<String, Map<String, Object>> controls = (Map<String, Map<String, Object>>)testCaseMap.get(CONTROLS_KEY_NAME);
        return (InputControl)controls.get(name).get(INPUT_CONTROL_KEY_NAME);
    }

    @SuppressWarnings("unchecked")
    public static ReportInputControlInformation getInputControlInfo(String name, String testCaseName, ApplicationContext applicationContext) {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        Map<String, Map<String, Object>> controls = (Map<String, Map<String, Object>>)testCaseMap.get(CONTROLS_KEY_NAME);
        return (ReportInputControlInformation)controls.get(name).get(REPORT_INPUT_CONTROL_INFORMATION_KEY_NAME);
    }

    public static void assertResult(String testCaseName, Object actualResult, ApplicationContext applicationContext) {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        Object expectedResult = testCaseMap.get(EXPECTED_RESULT_KEY_NAME);
        assertThat(expectedResult, equalTo(actualResult));
    }
}
