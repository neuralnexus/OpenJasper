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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientList;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRangeBoundary;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientInTest extends BaseDTOTest<ClientIn> {

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
    private static ClientExpression OR_FALSE_TRUE = new ClientAnd(asList(BOOLEAN_TRUE, BOOLEAN_FALSE));

    private static ClientExpression LIST_WITH_TWO_LISTS = new ClientList(asList(LIST_WITH_TWO_ELEMENTS, LIST_WITH_TWO_ELEMENTS_ALT));
    private static ClientExpression LIST_WITH_TWO_ANDs = new ClientList(
            asList(
                    (ClientExpression) new ClientAnd(asList(BOOLEAN_TRUE, OR_FALSE_TRUE)),
                    new ClientAnd(asList(AND_TRUE_FALSE, BOOLEAN_FALSE))
            )
    );
    private static ClientExpression LIST_WITH_TWO_ORs = new ClientList(
            asList(
                    (ClientExpression) new ClientOr(asList(AND_TRUE_FALSE, BOOLEAN_FALSE)),
                    new ClientOr(asList(BOOLEAN_TRUE, OR_FALSE_TRUE))
            )
    );

    private static final String TEST_VARIABLE_NAME = "TEST_VARIABLE_NAME";
    private static final String TEST_VARIABLE_NAME_1 = "TEST_VARIABLE_NAME_1";

    private static final ClientVariable TEST_CLIENT_VARIABLE = new ClientVariable(TEST_VARIABLE_NAME);
    private static final ClientVariable TEST_CLIENT_VARIABLE_1 = new ClientVariable(TEST_VARIABLE_NAME_1);

    private static final List<ClientExpression> TEST_CLIENT_TWO_VARIABLES = Arrays.asList(
            (ClientExpression)TEST_CLIENT_VARIABLE,
            (ClientExpression)TEST_CLIENT_VARIABLE_1
    );

    private static final ClientList TEST_CLIENT_LIST = new ClientList(TEST_CLIENT_TWO_VARIABLES);
    private static final ClientRange TEST_CLIENT_RANGE = new ClientRange(new ClientRangeBoundary(TEST_CLIENT_VARIABLE), new ClientRangeBoundary(TEST_CLIENT_VARIABLE_1));

    @Override
    protected List<ClientIn> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().unsetParen(),
                new ClientIn(VARIABLE_A, LIST_WITH_ONE_ELEMENT),
                new ClientIn(VARIABLE_A, LIST_WITH_ONE_ELEMENT).setParen(),
                new ClientIn(VARIABLE_A, LIST_WITH_TWO_LISTS),
                new ClientIn(VARIABLE_A, LIST_WITH_TWO_LISTS).setParen(),
                new ClientIn(VARIABLE_A, LIST_WITH_TWO_ANDs),
                new ClientIn(VARIABLE_A, LIST_WITH_TWO_ANDs).setParen(),
                new ClientIn(VARIABLE_A, LIST_WITH_TWO_ORs),
                new ClientIn(VARIABLE_A, LIST_WITH_TWO_ORs).setParen()
        );
    }

    @Override
    protected ClientIn createFullyConfiguredInstance() {
        return new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_TWO_VARIABLES).setParen();
    }

    @Override
    protected ClientIn createInstanceWithDefaultParameters() {
        return new ClientIn();
    }

    @Override
    protected ClientIn createInstanceFromOther(ClientIn other) {
        return new ClientIn(other);
    }


    /*
     * override some base tests because they can't be applied
     */

    @Test
    public void copiedInstanceEqualsToOriginalInstanceWithDefaultParameters() {

    }

    @Test
    public void deepClonedInstanceEqualsToOriginalInstanceWithDefaultParameters() {

    }

    /*
     * toString
     */

    @Test
    public void generatedStringBeginsWithClassName() {
        // TOOD: discuss how better to skip the test because we have other tests for verifying toString functionality
    }

    @Test
    public void toStringInstanceWithVariableAndList() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_TWO_VARIABLES);
        assertEquals(TEST_VARIABLE_NAME + " in (" + TEST_VARIABLE_NAME + ", " + TEST_VARIABLE_NAME_1 + ")", instance.toString());
    }

    @Test
    public void toStringInstanceWithVariableAndRange() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_RANGE);
        assertEquals(TEST_VARIABLE_NAME + " in (" + TEST_VARIABLE_NAME + ":" + TEST_VARIABLE_NAME_1 + ")", instance.toString());
    }

    @Test
    public void toStringInstanceWithVariableAndClientList() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_LIST);
        assertEquals(TEST_VARIABLE_NAME + " in (" + TEST_VARIABLE_NAME + ", " + TEST_VARIABLE_NAME_1 + ")", instance.toString());
    }

    @Test
    public void toStringInstanceWithTwoVariables() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_VARIABLE_1);
        assertEquals(TEST_VARIABLE_NAME + " in " + TEST_VARIABLE_NAME_1, instance.toString());
    }

    /*
     * Constructors
     */

    @Test
    public void constructorWithVariableAndList() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_TWO_VARIABLES);
        assertTrue(instance.getRhs() instanceof ClientList);
    }

    @Test
    public void constructorWithVariableAndRange() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_RANGE);
        assertTrue(instance.getRhs() instanceof ClientRange);
    }

    @Test
    public void constructorWithVariableAndClientList() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_LIST);
        assertTrue(instance.getRhs() instanceof ClientList);
    }

    @Test
    public void constructorWithTwoVariables() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_VARIABLE_1);
        assertTrue(instance.getRhs() instanceof ClientVariable);
    }

    @Test
    public void copyConstructorWithVariableAndList() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_TWO_VARIABLES);
        ClientIn copiedInstance = new ClientIn(instance);
        assertEquals(instance, copiedInstance);
        assertTrue(copiedInstance.getRhs() instanceof ClientList);
    }

    @Test
    public void copyConstructorWithVariableAndRange() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_RANGE);
        ClientIn copiedInstance = new ClientIn(instance);
        assertEquals(instance, copiedInstance);
        assertTrue(copiedInstance.getRhs() instanceof ClientRange);
    }

    @Test
    public void copyConstructorWithTwoVariables() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_VARIABLE_1);
        ClientIn copiedInstance = new ClientIn(instance);
        assertEquals(instance, copiedInstance);
        assertTrue(instance.getRhs() instanceof ClientVariable);
    }

    @Test
    public void getRhsRange_instanceWithVariableAndRange_notNull() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_RANGE);
        assertNotNull(instance.getRhs());
    }

    @Test
    public void getRhsRange_instanceWithTwoVariables_null() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_VARIABLE_1);
        assertEquals(TEST_CLIENT_VARIABLE_1, instance.getRhs());
    }

    /*
     * getRhsList
     */

    @Test
    public void getRhsList_instanceWithVariableAndList_notNull() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_TWO_VARIABLES);
        assertNotNull(instance.getRhs());
    }

    @Test
    public void getRhsList_instanceWithVariableAndRange_null() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_RANGE);
        assertEquals(TEST_CLIENT_RANGE, instance.getRhs());
    }

    @Test
    public void getRhsList_instanceWithVariableAndClientList_notNull() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_LIST);
        assertNotNull(instance.getRhs());
    }

    @Test
    public void getRhsList_instanceWithTwoVariables_null() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_VARIABLE_1);
        assertEquals(TEST_CLIENT_VARIABLE_1, instance.getRhs());
    }


    @Test
    public void getRhsVariable_instanceWithTwoVariables_notNull() {
        ClientIn instance = new ClientIn(TEST_CLIENT_VARIABLE, TEST_CLIENT_VARIABLE_1);
        assertNotNull(instance.getRhs());
    }
}