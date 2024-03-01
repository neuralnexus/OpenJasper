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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreater;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLess;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class CommonClientComparisonTest {

    protected static final int TEST_INTEGER_VALUE = 100;
    protected static final ClientExpression TEST_CLIENT_EXPRESSION = new ClientNumber(TEST_INTEGER_VALUE);
    protected static final List<ClientExpression> TEST_CLIENT_TWO_EXPRESSIONS = Arrays.asList(TEST_CLIENT_EXPRESSION, TEST_CLIENT_EXPRESSION);

    private ClientELVisitor mockVisitor;

    @BeforeEach
    public void init() {
        mockVisitor = mock(ClientELVisitor.class);
    }

    @Test
    public void equalOperationsShouldBeEqual() {
        ClientEquals equals = new ClientEquals();
        ClientEquals otherEquals = new ClientEquals();
        assertEquals(equals, otherEquals);
    }

    @Test
    public void differentOperationsShouldNotBeEqual() {
        ClientEquals equals = new ClientEquals();
        ClientGreater greater = new ClientGreater();
        assertNotEquals(equals, greater);
    }

    @Test
    public void setOperands_null_null() {
        ClientComparison instance = new ClientEquals();
        instance.setOperands(null);
        assertNull(instance.getOperands());
    }

    @Test
    public void setOperands_emptyList_emptyList() {
        ClientComparison instance = new ClientEquals();
        instance.setOperands(Collections.<ClientExpression>emptyList());
        assertEquals(Collections.emptyList(), instance.getOperands());
    }

    @Test
    public void setOperands_listWithSomeValues_otherListWithTheSameValues() {
        ClientComparison instance = new ClientEquals();
        instance.setOperands(TEST_CLIENT_TWO_EXPRESSIONS);
        assertNotSame(TEST_CLIENT_TWO_EXPRESSIONS, instance.getOperands());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, instance.getOperands());
    }

    @Test
    public void createComparison_unsupported_null() {
        assertNull(ClientComparison.createComparison("unsupported", null));
    }

    @Test
    public void createComparison_equals() {
        ClientComparison expected = new ClientEquals();
        ClientComparison actual = ClientComparison.createComparison(ClientOperation.EQUALS.getName(), null);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void createComparison_greater() {
        ClientComparison expected = new ClientGreater();
        ClientComparison actual = ClientComparison.createComparison(ClientOperation.GREATER.getName(), null);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void createComparison_greaterOrEqual() {
        ClientComparison expected = new ClientGreaterOrEqual();
        ClientComparison actual = ClientComparison.createComparison(ClientOperation.GREATER_OR_EQUAL.getName(), null);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void createComparison_less() {
        ClientComparison expected = new ClientLess();
        ClientComparison actual = ClientComparison.createComparison(ClientOperation.LESS.getName(), null);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void createComparison_lessOrEqual() {
        ClientComparison expected = new ClientLessOrEqual();
        ClientComparison actual = ClientComparison.createComparison(ClientOperation.LESS_OR_EQUAL.getName(), null);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void createComparison_notEqual() {
        ClientComparison expected = new ClientNotEqual();
        ClientComparison actual = ClientComparison.createComparison(ClientOperation.NOT_EQUAL.getName(), null);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void clientELVisitorCanBeAcceptedWithLeftAndRightExpression() {
        ClientExpression lhs = mock(ClientExpression.class);
        ClientExpression rhs = mock(ClientExpression.class);

        ClientComparison instance = new ClientEquals(
                Arrays.asList(
                        lhs,
                        rhs
                )
        );
        instance.accept(mockVisitor);

        verify(lhs).accept(mockVisitor);
        verify(rhs).accept(mockVisitor);
    }

    @Test
    void clientELVisitorWillNotBeAcceptedWithoutLeftAndRightExpression() {
        ClientComparison instance = new ClientEquals();

        instance.accept(mockVisitor);

        verify(mockVisitor).visit(any(ClientEquals.class));
    }


}
