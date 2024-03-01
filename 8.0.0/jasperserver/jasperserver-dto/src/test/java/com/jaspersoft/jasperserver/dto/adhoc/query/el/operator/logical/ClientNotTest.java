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
 */package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitorAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot.not;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientNotTest extends BaseDTOJSONPresentableTest<ClientNot> {

    private static ClientExpression BOOLEAN_TRUE = new ClientBoolean(Boolean.TRUE);
    private static ClientExpression BOOLEAN_FALSE = new ClientBoolean(Boolean.FALSE);

    private static ClientExpression AND_TRUE_FALSE = new ClientAnd(asList(BOOLEAN_TRUE, BOOLEAN_FALSE));

    protected static final int TEST_INTEGER_VALUE = 100;
    protected static final int TEST_ALT_VALUE = 200;
    protected static final ClientExpression TEST_CLIENT_EXPRESSION = new ClientNumber(TEST_INTEGER_VALUE);
    protected static final ClientExpression TEST_ALT_CLIENT_EXPRESSION = new ClientNumber(TEST_ALT_VALUE);

    @Override
    protected List<ClientNot> prepareInstancesWithAlternativeParameters() {
        return asList(
                new ClientNot(TEST_ALT_CLIENT_EXPRESSION).unsetParen(),
                new ClientNot(BOOLEAN_TRUE),
                new ClientNot(BOOLEAN_TRUE).setParen(),
                new ClientNot(AND_TRUE_FALSE),
                new ClientNot(AND_TRUE_FALSE).setParen()
        );
    }

    @Override
    protected ClientNot createFullyConfiguredInstance() {
        return new ClientNot(TEST_CLIENT_EXPRESSION);
    }

    @Override
    protected ClientNot createInstanceWithDefaultParameters() {
        return new ClientNot();
    }

    @Override
    protected ClientNot createInstanceFromOther(ClientNot other) {
        return new ClientNot(other);
    }

    /*
     * toString
     */

    @Override
    public void generatedStringBeginsWithClassName() {
        // we don't want the test to be run
    }

    @Test
    public void toString_WithoutOperands() {
        ClientOperator instance = new ClientNot();
        assertEquals("(not " + ClientExpressions.MISSING_REPRESENTATION + ")", instance.toString());
    }

    @Test
    public void toString_WithSomeSingleOperand() {
        ClientOperator instance = new ClientNot(TEST_CLIENT_EXPRESSION);
        assertEquals("(not " + TEST_INTEGER_VALUE + ")", instance.toString());
    }

    /*
     * accept
     */

    @Test
    public void accept_withoutOperands() {
        ClientOperator instance = new ClientNot();
        instance.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientNot expression) {
                assertEquals(ClientOperation.NOT ,expression.getOperator());
                assertEquals(Collections.emptyList(), expression.getOperands());
            }
        });
    }

    /*
     * static builder
     */

    @Test
    public void or_nullExpression_throws() {
        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        not(null);
                    }
                }
        );
    }

    @Test
    public void or_notOperator_throws() {
        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        not(new ClientNumber());
                    }
                }
        );
    }

}