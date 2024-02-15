/*
* Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
* http://www.jaspersoft.com.
*
* Unless you have purchased  a commercial license agreement from Jaspersoft,
* the following license terms  apply:
*
* This program is free software: you can redistribute it and/or  modify
* it under the terms of the GNU Affero General Public License  as
* published by the Free Software Foundation, either version 3 of  the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero  General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public  License
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.dto.common.validations.DateTimeFormatValidationRule;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.common.validations.MandatoryValidationRule;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.dto.common.validations.ValidationRule;
import com.jaspersoft.jasperserver.war.util.CalendarFormatProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class BasicInputControlHandlerTest  extends InputControlHandlerBaseTest {
    @TestedObject
    private BasicInputControlHandler handler;

    private PartialMock<BasicInputControlHandler> handlerPartialMock;

    @InjectInto(property = "cachedRepositoryService")
    private Mock<CachedRepositoryService> cachedRepositoryServiceMock;

    @InjectInto(property = "messageSource")
    private Mock<MessageSource> messageSourceMock;

    @InjectInto(property = "calendarFormatProvider")
    private Mock<CalendarFormatProvider> calendarFormatProviderMock;

    @Before
    public void init() throws Exception{
        messageSourceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                // returns message code.
                return proxyInvocation.getArguments().get(0);
            }
        }).getMessage(null, null, null);
    }

    @Test
    public void getValidationRules_mandatory_nonmandatory() throws Exception{
        InputControl inputControl = new InputControlImpl();
        inputControl.setMandatory(true);
        List<ValidationRule> validationRules = handler.getValidationRules(inputControl);
        Assert.assertNotNull(validationRules);
        Assert.assertFalse(validationRules.isEmpty());
        MandatoryValidationRule mandatoryValidationRule = getValidationRule(validationRules, MandatoryValidationRule.class);
        Assert.assertNotNull(mandatoryValidationRule);
        // mocked message source returns error code as result message
        Assert.assertEquals(MandatoryValidationRule.ERROR_KEY, mandatoryValidationRule.getErrorMessage());
        // check case for nonmandatory
        inputControl.setMandatory(false);
        validationRules = handler.getValidationRules(inputControl);
        mandatoryValidationRule = getValidationRule(validationRules, MandatoryValidationRule.class);
        Assert.assertNull(mandatoryValidationRule);
    }

    private <T extends ValidationRule> T getValidationRule(List<ValidationRule> rules, Class<T> concreteRuleType){
        T result = null;
        if(rules != null && !rules.isEmpty()){
            for(ValidationRule currentRule : rules){
                if(concreteRuleType.isAssignableFrom(currentRule.getClass())){
                    result = (T) currentRule;
                    break;
                }
            }
        }
        return result;
    }

    private <T extends ValidationRule> List<T> getValidationRules(List<ValidationRule> rules, Class<T> concreteRuleType){
        List<T> result = new ArrayList<T>();
        if(rules != null && !rules.isEmpty()){
            for(ValidationRule currentRule : rules){
                if(concreteRuleType.isAssignableFrom(currentRule.getClass())){
                    result.add((T) currentRule);
                }
            }
        }
        return result;
    }

    @Test
    public void getValidationRules_date_datetime() throws Exception{
        InputControl inputControl = new InputControlImpl();
        inputControl.setDataType(new ResourceReference((String)null));
        DataType dataType = new DataTypeImpl();
        cachedRepositoryServiceMock.returns(dataType).getResource(null, (ResourceReference) null);
        final String expectedDatePattern = "expectedDatePattern";
        final String expectedDateTimePattern = "expectedDateTimePattern";
        final String expectedTimePattern = "expectedTimePattern";
        calendarFormatProviderMock.returns(expectedDatePattern).getDatePattern();
        calendarFormatProviderMock.returns(expectedDateTimePattern).getDatetimePattern();
        calendarFormatProviderMock.returns(expectedTimePattern).getTimePattern();
        // case with type date
        dataType.setType(DataType.TYPE_DATE);
        List<ValidationRule> validationRules = handler.getValidationRules(inputControl);
        List<DateTimeFormatValidationRule> rules = getValidationRules(validationRules, DateTimeFormatValidationRule.class);
        Assert.assertNotNull(rules);
        Assert.assertTrue(rules.size() == 1);
        DateTimeFormatValidationRule rule = rules.get(0);
        Assert.assertNotNull(rule);
        Assert.assertEquals(DateTimeFormatValidationRule.INVALID_DATE, rule.getErrorMessage());
        Assert.assertEquals(expectedDatePattern, rule.getFormat());
        // case with type datetime
        dataType.setType(DataType.TYPE_DATE_TIME);
        validationRules = handler.getValidationRules(inputControl);
        rules = getValidationRules(validationRules, DateTimeFormatValidationRule.class);
        Assert.assertNotNull(rules);
        Assert.assertTrue(rules.size() == 1);
        rule = rules.get(0);
        Assert.assertNotNull(rule);
        Assert.assertEquals(DateTimeFormatValidationRule.INVALID_DATE_TIME, rule.getErrorMessage());
        Assert.assertEquals(expectedDateTimePattern, rule.getFormat());
        // case with type time
        dataType.setType(DataType.TYPE_TIME);
        validationRules = handler.getValidationRules(inputControl);
        rules = getValidationRules(validationRules, DateTimeFormatValidationRule.class);
        Assert.assertNotNull(rules);
        Assert.assertTrue(rules.size() == 1);
        rule = rules.get(0);
        Assert.assertNotNull(rule);
        Assert.assertEquals(DateTimeFormatValidationRule.INVALID_TIME, rule.getErrorMessage());
        Assert.assertEquals(expectedTimePattern, rule.getFormat());
    }

    @Test
    public void buildReportInputControl_getValidationRules_getType_invoked() throws Exception{
        final String expectedType = "expectedType";
        final InputControl inputControl = new InputControlImpl();
        inputControl.setURIString("/test/uri/string");
        final String uiType = "testUiType";
        final List<ValidationRule> validationRules = new ArrayList<ValidationRule>();
        validationRules.add(new MandatoryValidationRule());
        handlerPartialMock.returns(expectedType).getType(inputControl, uiType);
        handlerPartialMock.returns(validationRules).getValidationRules(inputControl);
        final ReportInputControl reportInputControl = handlerPartialMock.getMock().buildReportInputControl(inputControl, uiType, null);
        Assert.assertNotNull(reportInputControl);
        Assert.assertSame(validationRules, reportInputControl.getValidationRules());
        Assert.assertEquals(expectedType, reportInputControl.getType());
        Assert.assertEquals(inputControl.getURI(), reportInputControl.getUri());
    }

    @Test
    public void convertParameterValueFromRawDataNullAndDefaultNull() throws Exception {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/basicInputControlHandler/" +
                "verifyNullSubstitutionWhenNoInputParamsAndDefaultIsNull.xml");

        String testCaseName = "verifyNullSubstitutionWhenNoInputParamsAndDefaultIsNull";

        InputControl control = getControl("String", testCaseName);
        ReportInputControlInformation info = getControlInfo("String", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_SINGLE_VALUE);
        String[] rawData = getRawDataArray(testCaseName);

        Object typedValues = inputControlHandler.convertParameterValueFromRawData(rawData, control, info);

        assertTypedValue("verifyNullSubstitutionWhenNoInputParamsAndDefaultIsNull", typedValues);
    }

    @Test
    public void getValueNullSubstitutionTypedParamsNullDefaultNull() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/basicInputControlHandler/" +
                "getValueWhenTypedParamNullDefaultNull.xml");

        String testCaseName = "getValueWhenTypedParamNullDefaultNull";

        InputControl control = getControl("String", testCaseName);
        ReportInputControlInformation info = getControlInfo("String", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_SINGLE_VALUE);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }
}
