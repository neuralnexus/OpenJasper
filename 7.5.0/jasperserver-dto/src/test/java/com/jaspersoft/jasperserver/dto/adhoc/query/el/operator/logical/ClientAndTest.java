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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.AbstractClientOperatorTest;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientList;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientAndTest extends AbstractClientOperatorTest<ClientAnd> {

    private static ClientExpression VARIABLE_A = new ClientVariable("A");
    private static ClientExpression VARIABLE_B = new ClientVariable("B");
    private static ClientExpression VARIABLE_C = new ClientVariable("C");
    private static ClientExpression VARIABLE_D = new ClientVariable("D");

    private static ClientExpression LIST_WITH_ONE_ELEMENT = new ClientList(Collections.singletonList(VARIABLE_A));
    private static ClientExpression LIST_WITH_TWO_ELEMENTS = new ClientList(asList(VARIABLE_A, VARIABLE_B));
    private static ClientExpression LIST_WITH_TWO_ELEMENTS_ALT = new ClientList(asList(VARIABLE_C, VARIABLE_D));

    private static ClientExpression BOOLEAN_TRUE = new ClientBoolean(Boolean.TRUE);
    private static ClientExpression BOOLEAN_FALSE = new ClientBoolean(Boolean.FALSE);


    private static ClientExpression AND_TRUE_FALSE = new ClientAnd(asList(BOOLEAN_TRUE, BOOLEAN_FALSE));
    private static ClientExpression OR_FALSE_TRUE = new ClientOr(asList(BOOLEAN_TRUE, BOOLEAN_FALSE));

    private static ClientExpression LIST_WITH_TWO_LISTS = new ClientList(asList(LIST_WITH_TWO_ELEMENTS, LIST_WITH_TWO_ELEMENTS_ALT));
    private static ClientExpression LIST_WITH_TWO_ANDs = new ClientList(
            asList(
                    (ClientExpression) new ClientAnd(asList(BOOLEAN_TRUE, OR_FALSE_TRUE)),
                    new ClientAnd(asList(AND_TRUE_FALSE, BOOLEAN_FALSE)).setParen()
            )
    );
    private static ClientExpression LIST_WITH_TWO_ORs = new ClientList(
            asList(
                    (ClientExpression) new ClientOr(asList(AND_TRUE_FALSE, BOOLEAN_FALSE)),
                    new ClientOr(asList(BOOLEAN_TRUE, OR_FALSE_TRUE)).setParen()
            )
    );

    @Override
    protected ClientAnd createInstanceWithOperands(List<ClientExpression> operands) {
        return new ClientAnd(operands);
    }

    @Override
    protected String operatorId() {
        return ClientOperation.AND.getName();
    }

    @Override
    protected String separator() {
        return ClientOperation.AND.getDomelOperator();
    }

    @Override
    protected List<ClientAnd> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().unsetParen(),
                new ClientAnd(Collections.singletonList(VARIABLE_A)),
                new ClientAnd(Collections.singletonList(VARIABLE_A)).setParen(),
                new ClientAnd(asList(VARIABLE_A, VARIABLE_B)),
                new ClientAnd(asList(VARIABLE_A, VARIABLE_B)).setParen(),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_ONE_ELEMENT)),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_ONE_ELEMENT)).setParen(),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_ELEMENTS)),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_ELEMENTS)).setParen(),
                new ClientAnd(asList(LIST_WITH_ONE_ELEMENT, VARIABLE_A)),
                new ClientAnd(asList(LIST_WITH_ONE_ELEMENT, VARIABLE_A)).setParen(),
                new ClientAnd(asList(LIST_WITH_TWO_ELEMENTS, VARIABLE_A)),
                new ClientAnd(asList(LIST_WITH_TWO_ELEMENTS, VARIABLE_A)).setParen(),
                new ClientAnd(asList(LIST_WITH_TWO_ELEMENTS, LIST_WITH_TWO_ELEMENTS_ALT)),
                new ClientAnd(asList(LIST_WITH_TWO_ELEMENTS, LIST_WITH_TWO_ELEMENTS_ALT)).setParen(),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_LISTS)),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_LISTS)).setParen(),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_ANDs)),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_ANDs)).setParen(),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_ORs)),
                new ClientAnd(asList(VARIABLE_A, LIST_WITH_TWO_ORs)).setParen()
        );
    }

    @Override
    protected ClientAnd createFullyConfiguredInstance() {
        return new ClientAnd(TEST_CLIENT_TWO_EXPRESSIONS).setParen();
    }

    @Override
    protected ClientAnd createInstanceWithDefaultParameters() {
        return new ClientAnd();
    }

    @Override
    protected ClientAnd createInstanceFromOther(ClientAnd other) {
        return new ClientAnd(other);
    }

    @Test
    public void andOperationWithSomeExpressionProducesNewAndExpression() {
        ClientAnd logicalExpression = new ClientAnd();
        ClientAnd result = logicalExpression.and(TEST_CLIENT_EXPRESSION);

        assertEquals(logicalExpression, result.getOperands().get(0));
        assertEquals(TEST_CLIENT_EXPRESSION, result.getOperands().get(1));
    }
}