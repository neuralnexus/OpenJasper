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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreater;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLess;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class OperationsTest {

    private VariableOperations clientVariable;

    @BeforeEach
    public void setup() {
        clientVariable = new ClientVariable("name");
    }

    @Test
    public void eq_clientExpression_clientEquals() {
        ClientExpression clientExpression = new ClientNumber(23F);

        ClientComparison result = clientVariable.eq(clientExpression);

        assertEquals(ClientEquals.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientExpression, result.getRhs());
    }

    @Test
    public void eq_string_clientEquals() {
        String value = "string";

        ClientComparison result = clientVariable.eq(value);

        assertEquals(ClientEquals.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientString) result.getRhs()).getValue());
    }

    @Test
    public void eq_byte_clientEquals() {
        Byte value = 23;

        ClientComparison result = clientVariable.eq(value);

        assertEquals(ClientEquals.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientNumber) result.getRhs()).getValue());
    }

    @Test
    public void notEq_clientExpression_clientNotEqual() {
        ClientExpression clientExpression = new ClientNumber(23F);

        ClientComparison result = clientVariable.notEq(clientExpression);

        assertEquals(ClientNotEqual.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientExpression, result.getRhs());
    }

    @Test
    public void notEq_string_clientNotEqual() {
        String value = "string";

        ClientComparison result = clientVariable.notEq(value);

        assertEquals(ClientNotEqual.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientString) result.getRhs()).getValue());
    }

    @Test
    public void notEq_short_clientNotEqual() {
        Short value = 23;

        ClientComparison result = clientVariable.notEq(value);

        assertEquals(ClientNotEqual.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientNumber) result.getRhs()).getValue());
    }

    @Test
    public void startsWith_string_clientFunction() {
        String value = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(value));

        ClientFunction result = clientVariable.startsWith(value);

        assertEquals(ClientExpressions.STARTS_WITH_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void startsWith_variable_clientFunction() {
        ClientVariable value = new ClientVariable("variable");
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, value);

        ClientFunction result = clientVariable.startsWith(value);

        assertEquals(ClientExpressions.STARTS_WITH_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void endsWith_string_clientFunction() {
        String value = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(value));

        ClientFunction result = clientVariable.endsWith(value);

        assertEquals(ClientExpressions.ENDS_WITH_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void contains_string_clientFunction() {
        String value = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(value));

        ClientFunction result = clientVariable.contains(value);

        assertEquals(ClientExpressions.CONTAINS_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void gtOrEq_clientExpression_clientGreaterOrEqual() {
        ClientExpression clientExpression = new ClientNumber(23F);

        ClientComparison result = clientVariable.gtOrEq(clientExpression);

        assertEquals(ClientGreaterOrEqual.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientExpression, result.getRhs());
    }

    @Test
    public void gtOrEq_long_clientGreaterOrEqual() {
        Long value = 23L;

        ClientComparison result = clientVariable.gtOrEq(value);

        assertEquals(ClientGreaterOrEqual.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientNumber) result.getRhs()).getValue());
    }


    @Test
    public void ltOrEq_clientExpression_clientLessOrEqual() {
        ClientExpression clientExpression = new ClientNumber(23F);

        ClientComparison result = clientVariable.ltOrEq(clientExpression);

        assertEquals(ClientLessOrEqual.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientExpression, result.getRhs());
    }

    @Test
    public void ltOrEq_double_clientLessOrEqual() {
        Double value = 23D;

        ClientComparison result = clientVariable.ltOrEq(value);

        assertEquals(ClientLessOrEqual.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientNumber) result.getRhs()).getValue());
    }

    @Test
    public void gt_clientExpression_clientGreater() {
        ClientExpression clientExpression = new ClientNumber(23F);

        ClientComparison result = clientVariable.gt(clientExpression);

        assertEquals(ClientGreater.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientExpression, result.getRhs());
    }

    @Test
    public void gt_bigDecimal_clientGreater() {
        BigDecimal value = BigDecimal.valueOf(23D);

        ClientComparison result = clientVariable.gt(value);

        assertEquals(ClientGreater.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientNumber) result.getRhs()).getValue());
    }

    @Test
    public void lt_clientExpression_clientLess() {
        ClientExpression clientExpression = new ClientNumber(23F);

        ClientComparison result = clientVariable.lt(clientExpression);

        assertEquals(ClientLess.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientExpression, result.getRhs());
    }

    @Test
    public void lt_short_clientLess() {
        Short value = 23;

        ClientComparison result = clientVariable.lt(value);

        assertEquals(ClientLess.class, result.getClass());
        assertEquals(clientVariable, result.getLhs());
        assertEquals(value, ((ClientNumber) result.getRhs()).getValue());
    }

    @Test
    public void equalsDate_date_clientFunction() {
        Date date = new Date(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientDate(date));

        ClientFunction result = clientVariable.equalsDate(date);

        assertEquals(ClientExpressions.EQUALS_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void beforeDate_date_clientFunction() {
        Date date = new Date(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientDate(date));

        ClientFunction result = clientVariable.beforeDate(date);

        assertEquals(ClientExpressions.BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void afterDate_date_clientFunction() {
        Date date = new Date(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientDate(date));

        ClientFunction result = clientVariable.afterDate(date);

        assertEquals(ClientExpressions.AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrBeforeDate_date_clientFunction() {
        Date date = new Date(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientDate(date));

        ClientFunction result = clientVariable.isOnOrBeforeDate(date);

        assertEquals(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrAfterDate_date_clientFunction() {
        Date date = new Date(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientDate(date));

        ClientFunction result = clientVariable.isOnOrAfterDate(date);

        assertEquals(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void betweenDates_date_clientFunction() {
        Date begin = new Date(11111);
        Date end = new Date(22222);

        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientDate(begin), new ClientDate(end));

        ClientFunction result = clientVariable.betweenDates(begin, end);

        assertEquals(ClientExpressions.BETWEEN_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void equalsDate_timestamp_clientFunction() {
        Timestamp timestamp = new Timestamp(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTimestamp(timestamp));

        ClientFunction result = clientVariable.equalsDate(timestamp);

        assertEquals(ClientExpressions.EQUALS_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void beforeDate_timestamp_clientFunction() {
        Timestamp timestamp = new Timestamp(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTimestamp(timestamp));

        ClientFunction result = clientVariable.beforeDate(timestamp);

        assertEquals(ClientExpressions.BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void afterDate_timestamp_clientFunction() {
        Timestamp timestamp = new Timestamp(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTimestamp(timestamp));

        ClientFunction result = clientVariable.afterDate(timestamp);

        assertEquals(ClientExpressions.AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrBeforeDate_timestamp_clientFunction() {
        Timestamp timestamp = new Timestamp(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTimestamp(timestamp));

        ClientFunction result = clientVariable.isOnOrBeforeDate(timestamp);

        assertEquals(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrAfterDate_timestamp_clientFunction() {
        Timestamp timestamp = new Timestamp(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTimestamp(timestamp));

        ClientFunction result = clientVariable.isOnOrAfterDate(timestamp);

        assertEquals(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void betweenDates_timestamp_clientFunction() {
        Timestamp begin = new Timestamp(11111);
        Timestamp end = new Timestamp(22222);

        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTimestamp(begin), new ClientTimestamp(end));

        ClientFunction result = clientVariable.betweenDates(begin, end);

        assertEquals(ClientExpressions.BETWEEN_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void equalsDate_time_clientFunction() {
        Time time = new Time(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTime(time));

        ClientFunction result = clientVariable.equalsDate(time);

        assertEquals(ClientExpressions.EQUALS_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void beforeDate_time_clientFunction() {
        Time time = new Time(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTime(time));

        ClientFunction result = clientVariable.beforeDate(time);

        assertEquals(ClientExpressions.BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void afterDate_time_clientFunction() {
        Time time = new Time(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTime(time));

        ClientFunction result = clientVariable.afterDate(time);

        assertEquals(ClientExpressions.AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrBeforeDate_time_clientFunction() {
        Time time = new Time(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTime(time));

        ClientFunction result = clientVariable.isOnOrBeforeDate(time);

        assertEquals(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrAfterDate_time_clientFunction() {
        Time time = new Time(11111);
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTime(time));

        ClientFunction result = clientVariable.isOnOrAfterDate(time);

        assertEquals(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void betweenDates_time_clientFunction() {
        Time begin = new Time(11111);
        Time end = new Time(22222);

        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientTime(begin), new ClientTime(end));

        ClientFunction result = clientVariable.betweenDates(begin, end);

        assertEquals(ClientExpressions.BETWEEN_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }
    @Test
    public void equalsDate_string_clientFunction() {
        String string = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(string));

        ClientFunction result = clientVariable.equalsDate(string);

        assertEquals(ClientExpressions.EQUALS_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void beforeDate_string_clientFunction() {
        String string = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(string));

        ClientFunction result = clientVariable.beforeDate(string);

        assertEquals(ClientExpressions.BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void afterDate_string_clientFunction() {
        String string = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(string));

        ClientFunction result = clientVariable.afterDate(string);

        assertEquals(ClientExpressions.AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrBeforeDate_string_clientFunction() {
        String string = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(string));

        ClientFunction result = clientVariable.isOnOrBeforeDate(string);

        assertEquals(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void isOnOrAfterDate_string_clientFunction() {
        String string = "string";
        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(string));

        ClientFunction result = clientVariable.isOnOrAfterDate(string);

        assertEquals(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

    @Test
    public void betweenDates_string_clientFunction() {
        String begin = "string";
        String end = "string2";

        List<ClientExpression> operands = Arrays.asList((ClientExpression) clientVariable, new ClientString(begin), new ClientString(end));

        ClientFunction result = clientVariable.betweenDates(begin, end);

        assertEquals(ClientExpressions.BETWEEN_DATE_FUN, result.getFunctionName());
        assertEquals(operands, result.getOperands());
    }

}
