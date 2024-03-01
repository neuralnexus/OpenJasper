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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import com.jaspersoft.jasperserver.dto.utils.CustomAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public abstract class AbstractClientOperatorTest<T extends ClientOperator> extends BaseDTOJSONPresentableTest<T> {

    protected static final int TEST_INTEGER_VALUE = 100;
    protected static final ClientExpression TEST_CLIENT_EXPRESSION = new ClientNumber(TEST_INTEGER_VALUE);
    protected static final List<ClientExpression> TEST_CLIENT_ONE_EXPRESSION = Collections.singletonList(TEST_CLIENT_EXPRESSION);
    protected static final List<ClientExpression> TEST_CLIENT_TWO_EXPRESSIONS = Arrays.asList(TEST_CLIENT_EXPRESSION, TEST_CLIENT_EXPRESSION);

    @Override
    protected void assertFieldsHaveUniqueReferences(T expected, T actual) {
        CustomAssertions.assertNotSameCollection(expected.operands, actual.operands);
    }

    /*
     * Constructors
     */

    @Test
    public void instanceCanBeCreatedByDefaultConstructor() {
        ClientOperator instance = createInstanceWithDefaultParameters();
        assertEquals(Collections.emptyList(), instance.getOperands());
    }

    @Test
    public void instanceCanBeCreatedWithSomeOperands() {
        ClientOperator instance = createInstanceWithOperands(TEST_CLIENT_TWO_EXPRESSIONS);
        assertEquals(operatorId(), instance.getOperator().getName());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, instance.getOperands());
    }

    abstract protected T createInstanceWithOperands(List<ClientExpression> operands);
    abstract protected String operatorId();

    /*
     * toString
     */

    @Override
    public void generatedStringBeginsWithClassName() {
        // The annotation @Test was replaced with @Override to prevent the test running.
    }

    @Test
    public void toString_ParenSet_WithoutOperands() {
        ClientOperator instance = createInstanceWithDefaultParameters().setParen();
        assertEquals("(" + ClientExpressions.MISSING_REPRESENTATION +" " + separator() + " " + ClientExpressions.MISSING_REPRESENTATION + ")", instance.toString());
    }

    @Test
    public void toString_ParenSet_WithSomeSingleOperand() {
        ClientOperator instance = createInstanceWithOperands(TEST_CLIENT_ONE_EXPRESSION).setParen();
        assertEquals("(" + TEST_INTEGER_VALUE + " " + separator() + " " + ClientExpressions.MISSING_REPRESENTATION + ")", instance.toString());
    }

    @Test
    public void toString_ParenSet_WithSomeMultipleOperands() {
        ClientOperator instance = createInstanceWithOperands(TEST_CLIENT_TWO_EXPRESSIONS).setParen();
        assertEquals("(" + TEST_INTEGER_VALUE + " " + separator() + " " + TEST_INTEGER_VALUE + ")", instance.toString());
    }

    @Test
    public void toString_ParenUnSet_WithoutOperands() {
        ClientOperator instance = createInstanceWithDefaultParameters();
        assertEquals(ClientExpressions.MISSING_REPRESENTATION + " " + separator() + " " + ClientExpressions.MISSING_REPRESENTATION, instance.toString());
    }

    @Test
    public void toString_ParenUnSet_WithSomeSingleOperand() {
        ClientOperator instance = createInstanceWithOperands(TEST_CLIENT_ONE_EXPRESSION);
        assertEquals(TEST_INTEGER_VALUE + " " + separator() + " " + ClientExpressions.MISSING_REPRESENTATION, instance.toString());
    }

    @Test
    public void toString_ParenUnSet_WithSomeMultipleOperands() {
        ClientOperator instance = createInstanceWithOperands(TEST_CLIENT_TWO_EXPRESSIONS);
        assertEquals(TEST_INTEGER_VALUE + " " + separator() + " " + TEST_INTEGER_VALUE, instance.toString());
    }

    abstract protected String separator();

}