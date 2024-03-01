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

package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientPercentRatio;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author ativodar
 */
public class ClientExpressionsTest {
    private static final String CLIENT_FUNCTION_NAME = "functionName";
    private static final ClientExpression CLIENT_EXPRESSION = new ClientNumber().setValue(23f);
    private static final ClientExpression CLIENT_EXPRESSION_TWO = new ClientString().setValue("value");
    private static final List<ClientExpression> CLIENT_EXPRESSIONS_LIST = Arrays.asList(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

    private static final String VALUE_STRING = "valueString";
    private static final Byte VALUE_BYTE = 23;
    private static final Short VALUE_SHORT = 24;
    private static final Integer VALUE_INTEGER = 25;
    private static final Long VALUE_LONG = 26L;
    private static final BigInteger VALUE_BIG_INTEGER = BigInteger.valueOf(27);
    private static final Float VALUE_FLOAT = 28.5F;
    private static final Double VALUE_DOUBLE = 29.5D;
    private static final BigDecimal VALUE_BIG_DECIMAL = BigDecimal.valueOf(23.5);
    private static final Date VALUE_DATE = new Date(1111111111111L);
    private static final Time VALUE_TIME = new Time(1111111111111L);
    private static final Timestamp VALUE_TIMESTAMP = new Timestamp(1111111111111L);
    private static final Boolean VALUE_BOOLEAN = Boolean.TRUE;
    private static final Character VALUE_CHAR = 's';

    private static final ClientVariable CLIENT_VARIABLE = new ClientVariable().setName("name");
    private static final ClientVariable CLIENT_VARIABLE_TWO = new ClientVariable().setName("nameTwo");

    private static final ClientOperator CLIENT_OPERATOR = new ClientOr();
    private static final ClientOperator CLIENT_OPERATOR_TWO = new ClientAnd();

    @Test
    public void function_nameAndParamsArray_clientFunction() {
        ClientFunction result = ClientExpressions.function(CLIENT_FUNCTION_NAME, CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_FUNCTION_NAME, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void function_nameAndParamsList_clientFunction() {
        ClientFunction result = ClientExpressions.function(CLIENT_FUNCTION_NAME, CLIENT_EXPRESSIONS_LIST);

        assertEquals(CLIENT_FUNCTION_NAME, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void equalsDate_paramsArray_clientFunction() {
        ClientFunction result = ClientExpressions.equalsDate(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientExpressions.EQUALS_DATE_FUN, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void afterDate_paramsArray_clientFunction() {
        ClientFunction result = ClientExpressions.afterDate(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientExpressions.AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void beforeDate_paramsArray_clientFunction() {
        ClientFunction result = ClientExpressions.beforeDate(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientExpressions.BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void betweenDates_paramsArray_clientFunction() {
        ClientFunction result = ClientExpressions.betweenDates(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientExpressions.BETWEEN_DATE_FUN, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void startsWith_paramsArray_clientFunction() {
        ClientFunction result = ClientExpressions.startsWith(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientExpressions.STARTS_WITH_FUN, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void endsWith_paramsArray_clientFunction() {
        ClientFunction result = ClientExpressions.endsWith(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientExpressions.ENDS_WITH_FUN, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void contains_paramsArray_clientFunction() {
        ClientFunction result = ClientExpressions.contains(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientExpressions.CONTAINS_FUN, result.getFunctionName());
        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void nullLiteral_clientNull() {
        ClientLiteral result = ClientExpressions.nullLiteral();

        assertEquals(ClientNull.class, result.getClass());
    }

    @Test
    public void literal_string_clientString() {
        ClientLiteral result = ClientExpressions.literal(VALUE_STRING);

        assertEquals(ClientString.class, result.getClass());
        assertEquals(VALUE_STRING, result.getValue());
    }

    @Test
    public void literal_float_clientFloat() {
        ClientLiteral result = ClientExpressions.literal(VALUE_FLOAT);

        assertEquals(ClientNumber.class, result.getClass());
        assertEquals(VALUE_FLOAT, result.getValue());
    }

    @Test
    public void literal_date_clientDate() {
        ClientLiteral result = ClientExpressions.literal(VALUE_DATE);

        assertEquals(ClientDate.class, result.getClass());
        assertEquals(VALUE_DATE, result.getValue());
    }

    @Test
    public void literal_time_clientTime() {
        ClientLiteral result = ClientExpressions.literal(VALUE_TIME);

        assertEquals(ClientTime.class, result.getClass());
        assertEquals(VALUE_TIME, result.getValue());
    }

    @Test
    public void literal_timestamp_clientTimestamp() {
        ClientLiteral result = ClientExpressions.literal(VALUE_TIMESTAMP);

        assertEquals(ClientTimestamp.class, result.getClass());
        assertEquals(VALUE_TIMESTAMP, result.getValue());
    }

    @Test
    public void literal_boolean_clientBoolean() {
        ClientLiteral result = ClientExpressions.literal(VALUE_BOOLEAN);

        assertEquals(ClientBoolean.class, result.getClass());
        assertEquals(VALUE_BOOLEAN, result.getValue());
    }

    @Test
    public void literal_char_clientString() {
        ClientLiteral result = ClientExpressions.literal(VALUE_CHAR);

        assertEquals(ClientString.class, result.getClass());
        assertEquals(VALUE_CHAR.toString(), result.getValue());
    }

    @Test
    public void string_string_clientString() {
        ClientLiteral result = ClientExpressions.string(VALUE_STRING);

        assertEquals(ClientString.class, result.getClass());
        assertEquals(VALUE_STRING, result.getValue());
    }

    @Test
    public void integer_integer_clientNumber() {
        ClientLiteral result = ClientExpressions.integer(VALUE_INTEGER);

        assertEquals(ClientNumber.class, result.getClass());
        assertEquals(VALUE_INTEGER, result.getValue());
    }


    @Test
    public void date_date_clientDate() {
        ClientLiteral result = ClientExpressions.date(VALUE_DATE);

        assertEquals(ClientDate.class, result.getClass());
        assertEquals(VALUE_DATE, result.getValue());
    }

    @Test
    public void time_time_clientTime() {
        ClientLiteral result = ClientExpressions.time(VALUE_TIME);

        assertEquals(ClientTime.class, result.getClass());
        assertEquals(VALUE_TIME, result.getValue());
    }

    @Test
    public void timestamp_timestamp_clientTimestamp() {
        ClientLiteral result = ClientExpressions.timestamp(VALUE_TIMESTAMP);

        assertEquals(ClientTimestamp.class, result.getClass());
        assertEquals(VALUE_TIMESTAMP, result.getValue());
    }

    @Test
    public void boolean_boolean_clientBoolean() {
        ClientLiteral result = ClientExpressions.bool(VALUE_BOOLEAN);

        assertEquals(ClientBoolean.class, result.getClass());
        assertEquals(VALUE_BOOLEAN, result.getValue());
    }

    @Test
    public void character_char_clientString() {
        ClientLiteral result = ClientExpressions.character(VALUE_CHAR);

        assertEquals(ClientString.class, result.getClass());
        assertEquals(VALUE_CHAR.toString(), result.getValue());
    }

    @Test
    public void range_clientExpressions_clientRange() {
        ClientRange result = ClientExpressions.range(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSION, result.getStart().getBoundary());
        assertEquals(CLIENT_EXPRESSION_TWO, result.getEnd().getBoundary());
    }

    @Test
    public void range_clientVariables_clientRange() {
        ClientRange result = ClientExpressions.range(CLIENT_VARIABLE, CLIENT_VARIABLE_TWO);

        assertEquals(CLIENT_VARIABLE, result.getStart().getBoundary());
        assertEquals(CLIENT_VARIABLE_TWO, result.getEnd().getBoundary());
    }

    @Test
    public void range_clientIntegers_clientRange() {
        ClientNumber clientInteger = new ClientNumber().setValue(23);
        ClientNumber clientIntegerTwo = new ClientNumber().setValue(24);

        ClientRange result = ClientExpressions.range(23, 24);

        assertEquals(clientInteger, result.getStart().getBoundary());
        assertEquals(clientIntegerTwo, result.getEnd().getBoundary());
    }

    @Test
    public void range_clientTimes_clientRange() {
        ClientTime clientTime = new ClientTime().setValue(new Time(111));
        ClientTime clientTimeTwo = new ClientTime().setValue(new Time(222));

        ClientRange result = ClientExpressions.range(new Time(111), new Time(222));

        assertEquals(clientTime, result.getStart().getBoundary());
        assertEquals(clientTimeTwo, result.getEnd().getBoundary());
    }

    @Test
    public void range_clientString_clientRange() {
        ClientString clientString = new ClientString().setValue("111");
        ClientString clientStringTwo = new ClientString().setValue("222");

        ClientRange result = ClientExpressions.range("111", "222");

        assertEquals(clientString, result.getStart().getBoundary());
        assertEquals(clientStringTwo, result.getEnd().getBoundary());
    }

    @Test
    public void variable_string_clientVariable() {
        ClientVariable result = ClientExpressions.variable(VALUE_STRING);

        assertEquals(VALUE_STRING, result.getName());
    }

    @Test
    public void not_clientExpression_clientNot() {
        ClientNot result = ClientExpressions.not(CLIENT_OPERATOR);
        //Un-setting the paren as operands(instanceof ClientOperator) of Not will not have paren flag set as it is implicit
        assertEquals(CLIENT_OPERATOR.unsetParen(), result.getOperand());
    }

    @Test
    public void and_clientExpressions_clientAnd() {
        ClientAnd result = ClientExpressions.and(CLIENT_OPERATOR, CLIENT_OPERATOR_TWO);
        //setting the paren flag as "or" expression will have its paren flag set based on precedence
        assertEquals(CLIENT_OPERATOR.setParen(), result.getOperands().get(0));
        assertEquals(CLIENT_OPERATOR_TWO, result.getOperands().get(1));
    }

    @Test
    public void or_clientExpressions_clientOr() {
        ClientOr result = ClientExpressions.or(CLIENT_OPERATOR, CLIENT_OPERATOR_TWO);

        assertEquals(CLIENT_OPERATOR, result.getOperands().get(0));
        assertEquals(CLIENT_OPERATOR_TWO, result.getOperands().get(1));
    }

    @Test
    public void comparison_operationAndExpressionsArray_clientComparison() {
        ClientComparison result = ClientExpressions.comparison(ClientOperation.GREATER.getName(), CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(ClientOperation.GREATER, result.getOperator());
        assertEquals(CLIENT_EXPRESSION, result.getOperands().get(0));
        assertEquals(CLIENT_EXPRESSION_TWO, result.getRhs());
    }

    @Test
    public void comparison_operationAndExpressionsList_clientComparison() {
        ClientComparison result = ClientExpressions.comparison(ClientOperation.GREATER.getName(), CLIENT_EXPRESSIONS_LIST);

        assertEquals(ClientOperation.GREATER, result.getOperator());
        assertEquals(CLIENT_EXPRESSION, result.getOperands().get(0));
        assertEquals(CLIENT_EXPRESSION_TWO, result.getRhs());
    }

    @Test
    public void list_expressionsArray_clientExpressionsList() {
        List<ClientExpression> result = ClientExpressions.list(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result);
    }

    @Test
    public void operands_expressionsArray_clientExpressionsList() {
        List<ClientExpression> result = ClientExpressions.operands(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result);
    }

    @Test
    public void args_expressionsArray_clientExpressionsList() {
        List<ClientExpression> result = ClientExpressions.args(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result);
    }

    @Test
    public void range_clientLiterals_clientRange() {
        ClientLiteral clientInteger = new ClientNumber().setValue(23);
        ClientLiteral clientIntegerTwo = new ClientNumber().setValue(24);

        ClientRange result = ClientExpressions.range(clientInteger, clientIntegerTwo);

        assertEquals(clientInteger, result.getStart().getBoundary());
        assertEquals(clientIntegerTwo, result.getEnd().getBoundary());
    }

    @Test
    public void add_lhrAndRhrExpressions_clientAdd() {
        ClientAdd result = ClientExpressions.add(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void multiply_lhrAndRhrExpressions_clientMultiply() {
        ClientMultiply result = ClientExpressions.multiply(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void subtract_lhrAndRhrExpressions_clientSubtract() {
        ClientSubtract result = ClientExpressions.subtract(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void divide_lhrAndRhrExpressions_clientDivide() {
        ClientDivide result = ClientExpressions.divide(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }

    @Test
    public void percentRatio_lhrAndRhrExpressions_clientPercentRatio () {
        ClientPercentRatio result = ClientExpressions.percentRatio(CLIENT_EXPRESSION, CLIENT_EXPRESSION_TWO);

        assertEquals(CLIENT_EXPRESSIONS_LIST, result.getOperands());
    }
}
