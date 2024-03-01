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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitorAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class CommonClientLogicalTest {

    protected static final int TEST_INTEGER_VALUE = 100;
    protected static final ClientExpression TEST_CLIENT_EXPRESSION = new ClientNumber(TEST_INTEGER_VALUE);
    protected static final List<ClientExpression> TEST_CLIENT_ONE_EXPRESSION = Collections.singletonList(TEST_CLIENT_EXPRESSION);
    protected static final List<ClientExpression> TEST_CLIENT_TWO_EXPRESSIONS = Arrays.asList(TEST_CLIENT_EXPRESSION, TEST_CLIENT_EXPRESSION);
    protected static final List<ClientExpression> TEST_CLIENT_THREE_EXPRESSIONS = Arrays.asList(TEST_CLIENT_EXPRESSION, TEST_CLIENT_EXPRESSION, TEST_CLIENT_EXPRESSION);

    /*
     * createLogical
     */

    @Test
    public void createLogical_and_success() {
        ClientAnd and = ClientLogical.createLogical(ClientOperation.AND.getName(), TEST_CLIENT_TWO_EXPRESSIONS);
        assertNotNull(and);
        assertEquals(ClientOperation.AND, and.getOperator());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, and.getOperands());
    }

    @Test
    public void createLogical_or_success() {
        ClientOr or = ClientLogical.createLogical(ClientOperation.OR.getName(), TEST_CLIENT_TWO_EXPRESSIONS);
        assertNotNull(or);
        assertEquals(ClientOperation.OR, or.getOperator());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, or.getOperands());
    }

    @Test
    public void createLogical_not_null() {
        ClientOr or = ClientLogical.createLogical(ClientOperation.NOT.getName(), TEST_CLIENT_TWO_EXPRESSIONS);
        assertNull(or);
    }

    @Test
    public void createLogical_notSupported_null() {
        ClientOr or = ClientLogical.createLogical("not_supported", TEST_CLIENT_TWO_EXPRESSIONS);
        assertNull(or);
    }

    @Test
    public void createLogical_and_paren_true_success() {
        ClientAnd and = ClientLogical.createLogical(ClientOperation.AND.getName(), TEST_CLIENT_TWO_EXPRESSIONS, true);
        assertNotNull(and);
        assertEquals(ClientOperation.AND, and.getOperator());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, and.getOperands());
    }

    @Test
    public void createLogical_and_paren_false_success() {
        ClientAnd and = ClientLogical.createLogical(ClientOperation.AND.getName(), TEST_CLIENT_TWO_EXPRESSIONS, false);
        assertNotNull(and);
        assertEquals(ClientOperation.AND, and.getOperator());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, and.getOperands());
    }

    @Test
    public void createLogical_or_paren_true_success() {
        ClientOr or = ClientLogical.createLogical(ClientOperation.OR.getName(), TEST_CLIENT_TWO_EXPRESSIONS, true);
        assertNotNull(or);
        assertEquals(ClientOperation.OR, or.getOperator());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, or.getOperands());
    }

    @Test
    public void createLogical_or_paren_false_success() {
        ClientOr or = ClientLogical.createLogical(ClientOperation.OR.getName(), TEST_CLIENT_TWO_EXPRESSIONS, false);
        assertNotNull(or);
        assertEquals(ClientOperation.OR, or.getOperator());
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, or.getOperands());
    }

    @Test
    public void createLogical_not_paren_true_null() {
        ClientOr or = ClientLogical.createLogical(ClientOperation.NOT.getName(), TEST_CLIENT_TWO_EXPRESSIONS, true);
        assertNull(or);
    }

    @Test
    public void createLogical_not_paren_false_null() {
        ClientOr or = ClientLogical.createLogical(ClientOperation.NOT.getName(), TEST_CLIENT_TWO_EXPRESSIONS, false);
        assertNull(or);
    }

    @Test
    public void createLogical_notSupported_paren_true_null() {
        ClientOr or = ClientLogical.createLogical("not_supported", TEST_CLIENT_TWO_EXPRESSIONS, true);
        assertNull(or);
    }

    @Test
    public void createLogical_notSupported_paren_false_null() {
        ClientOr or = ClientLogical.createLogical("not_supported", TEST_CLIENT_TWO_EXPRESSIONS, false);
        assertNull(or);
    }

    /*
     * checkArguments
     */

    @Test
    public void checkArguments_withoutArgs_throws() {
        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        ClientLogical.checkArguments();
                    }
                }
        );
    }

    @Test
    public void checkArguments_oneArg_throws() {
        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        ClientLogical.checkArguments(TEST_CLIENT_EXPRESSION);
                    }
                }
        );
    }

    @Test
    public void checkArguments_twoArgs_success() {
        ClientLogical.checkArguments(TEST_CLIENT_EXPRESSION, TEST_CLIENT_EXPRESSION);
        // There are no interactions
    }

    /*
     * Accessors
     */

    @Test
    public void setOperands_null_nullOperands() {
        ClientAnd and = new ClientAnd(TEST_CLIENT_TWO_EXPRESSIONS);
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, and.getOperands());
        and.setOperands(null);
        assertNull(and.getOperands());
    }

    @Test
    public void setOperands_twoOperands_success() {
        ClientAnd and = new ClientAnd();
        assertEquals(Collections.emptyList(), and.getOperands());
        and.setOperands(TEST_CLIENT_TWO_EXPRESSIONS);
        assertEquals(TEST_CLIENT_TWO_EXPRESSIONS, and.getOperands());
    }

    @Test
    public void setOperands_oneOperand_success() {
        ClientAnd and = new ClientAnd();
        assertEquals(Collections.emptyList(), and.getOperands());
        and.setOperands(TEST_CLIENT_ONE_EXPRESSION);
        assertEquals(TEST_CLIENT_ONE_EXPRESSION, and.getOperands());
    }

    @Test
    public void setOperands_threeOperand_unsupportedOperationException() {
        final ClientAnd and = new ClientAnd();
        assertEquals(Collections.emptyList(), and.getOperands());

        assertThrows(
                UnsupportedOperationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        and.setOperands(TEST_CLIENT_THREE_EXPRESSIONS);
                    }
                }
        );
    }

    /*
     * accept
     */

    @Test
    public void accept_lhs_null() {
        ClientLogical instance = new ClientAnd();
        instance.setOperands(Arrays.asList(null, TEST_CLIENT_EXPRESSION));
        instance.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientAnd expression) {
                assertNull(expression.getOperands().get(0));
            }
        });
    }

    @Test
    public void accept_rhs_null() {
        ClientLogical instance = new ClientAnd();
        instance.setOperands(Arrays.asList(TEST_CLIENT_EXPRESSION, null));
        instance.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientAnd expression) {
                assertNull(expression.getOperands().get(1));
            }
        });
    }
}