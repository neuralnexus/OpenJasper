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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientFunctionTest extends BaseDTOJSONPresentableTest<ClientFunction> {

    private static final String TEST_FUNCTION_NAME = "TEST_FUNCTION_NAME";
    private static final String TEST_FUNCTION_NAME_1 = "TEST_FUNCTION_NAME_1";

    private static final int TEST_INTEGER_VALUE = 100;
    private static final ClientExpression TEST_CLIENT_EXPRESSION = new ClientNumber(TEST_INTEGER_VALUE);
    private static final List<ClientExpression> TEST_CLIENT_EXPRESSIONS = Collections.singletonList(TEST_CLIENT_EXPRESSION);

    @Override
    protected List<ClientFunction> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFunctionName(TEST_FUNCTION_NAME_1),
                createFullyConfiguredInstance().setFunctionName(null),
                createFullyConfiguredInstance().unsetParen()
        );
    }

    @Override
    protected ClientFunction createFullyConfiguredInstance() {
        ClientFunction instance = createInstanceWithDefaultParameters()
                .setFunctionName(TEST_FUNCTION_NAME);
        return instance
                .setParen();
    }

    @Override
    protected ClientFunction createInstanceWithDefaultParameters() {
        return new ClientFunction();
    }

    @Override
    protected ClientFunction createInstanceFromOther(ClientFunction other) {
        return new ClientFunction(other);
    }

    @Test
    public void instanceCanBeCreatedWithFunctionName() {
        ClientFunction instance = new ClientFunction(TEST_FUNCTION_NAME);
        assertEquals(TEST_FUNCTION_NAME, instance.getFunctionName());
        assertEquals(ClientOperation.FUNCTION, instance.getOperator());
        assertEquals(Collections.emptyList(), instance.getOperands());
    }

    @Test
    public void instanceHasCorrectOperator() {
        ClientFunction instance = new ClientFunction();
        assertEquals(ClientOperation.FUNCTION, instance.getOperator());
    }

    @Test
    public void instanceCanBeCreatedByDefaultConstructor() {
        ClientFunction instance = new ClientFunction();
        assertNull(instance.getFunctionName());
        assertEquals(Collections.emptyList(), instance.getOperands());
    }

    @Test
    public void instanceCanBeCreatedWithSomeOperands() {
        ClientFunction instance = new ClientFunction(TEST_FUNCTION_NAME, TEST_CLIENT_EXPRESSIONS);
        assertEquals(ClientOperation.FUNCTION, instance.getOperator());
        assertEquals(TEST_CLIENT_EXPRESSIONS, instance.getOperands());
    }

    @Test
    public void instanceCanBeCreatedWithNullOperands() {
        ClientFunction instance = new ClientFunction(TEST_FUNCTION_NAME, null);
        assertEquals(ClientOperation.FUNCTION, instance.getOperator());
        assertEquals(new ArrayList<ClientFunction>(), instance.getOperands());
    }
    
    @Test
    public void addArgument() {
        ClientFunction instance = createFullyConfiguredInstance();
        instance.addArgument(TEST_CLIENT_EXPRESSION);
        assertEquals(TEST_CLIENT_EXPRESSIONS, instance.getOperands());
    }

    /*
     * toString
     */

    @Override
    public void generatedStringBeginsWithClassName() {
        // we want to skip the test.
    }

    @Test
    public void toString_ParenSet_WithoutOperands() {
        ClientFunction instance = createFullyConfiguredInstance();
        assertEquals(TEST_FUNCTION_NAME + "()", instance.toString());
    }

    @Test
    public void toString_ParenSet_WithSomeSingleOperand() {
        ClientFunction instance = createFullyConfiguredInstance();
        instance.addOperand(TEST_CLIENT_EXPRESSION);
        assertEquals(TEST_FUNCTION_NAME + "(" + TEST_INTEGER_VALUE + ")", instance.toString());
    }

    @Test
    public void toString_ParenSet_WithSomeMultipleOperands() {
        ClientFunction instance = createFullyConfiguredInstance();
        instance.addOperand(TEST_CLIENT_EXPRESSION);
        instance.addOperand(TEST_CLIENT_EXPRESSION);
        assertEquals(TEST_FUNCTION_NAME + "(" + TEST_INTEGER_VALUE + ", " + TEST_INTEGER_VALUE + ")", instance.toString());
    }

    @Test
    public void toString_ParenUnSet_WithoutOperands() {
        ClientFunction instance = new ClientFunction(TEST_FUNCTION_NAME);
        assertEquals(TEST_FUNCTION_NAME + "()", instance.toString());
    }

    @Test
    public void toString_ParenUnSet_WithSomeSingleOperand() {
        ClientFunction instance = new ClientFunction(TEST_FUNCTION_NAME);
        instance.addOperand(TEST_CLIENT_EXPRESSION);
        assertEquals(TEST_FUNCTION_NAME + "(" + TEST_INTEGER_VALUE + ")", instance.toString());
    }

    @Test
    public void toString_ParenUnSet_WithSomeMultipleOperands() {
        ClientFunction instance = new ClientFunction(TEST_FUNCTION_NAME);
        instance.addOperand(TEST_CLIENT_EXPRESSION);
        instance.addOperand(TEST_CLIENT_EXPRESSION);
        assertEquals(TEST_FUNCTION_NAME + "(" + TEST_INTEGER_VALUE + ", " + TEST_INTEGER_VALUE + ")", instance.toString());
    }
}