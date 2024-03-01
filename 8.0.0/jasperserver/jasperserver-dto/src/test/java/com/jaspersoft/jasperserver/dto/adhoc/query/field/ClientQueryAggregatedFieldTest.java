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

package com.jaspersoft.jasperserver.dto.adhoc.query.field;

import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.ClientQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientQueryAggregatedFieldTest extends BaseDTOJSONPresentableTest<ClientQueryAggregatedField> {

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_ALT = "TEST_ID_ALT";

    private static final String TEST_AGGREGATE_FUNCTION = "TEST_AGGREGATE_FUNCTION";
    private static final String TEST_AGGREGATE_FUNCTION_ALT = "TEST_AGGREGATE_FUNCTION_ALT";

    private static final String TEST_AGGREGATE_FIRST_LEVEL_FUNCTION = "TEST_AGGREGATE_FIRST_LEVEL_FUNCTION";
    private static final String TEST_AGGREGATE_FIRST_LEVEL_FUNCTION_ALT = "TEST_AGGREGATE_FIRST_LEVEL_FUNCTION_ALT";

    private static final ClientExpressionContainer TEST_EXPRESSION_CONTAINER = new ClientExpressionContainer().setObject(new ClientAdd());
    private static final ClientExpressionContainer TEST_EXPRESSION_CONTAINER_ALT = new ClientExpressionContainer().setString("TEST_STRING");

    private static final String TEST_AGGREGATE_TYPE = "TEST_AGGREGATE_TYPE";
    private static final String TEST_AGGREGATE_TYPE_ALT = "TEST_AGGREGATE_TYPE_ALT";

    private static final String TEST_AGGREGATE_ARG = "TEST_AGGREGATE_ARG";
    private static final String TEST_AGGREGATE_ARG_ALT = "TEST_AGGREGATE_ARG_ALT";

    private static final String TEST_FIELD_REFERENCE = "TEST_FIELD_REFERENCE";
    private static final String TEST_FIELD_REFERENCE_ALT = "TEST_FIELD_REFERENCE_ALT";

    private ClientQueryVisitor clientELVisitor;

    @BeforeAll
    public void init() {
        clientELVisitor = Mockito.mock(ClientQueryVisitor.class);
    }


    @Override
    protected List<ClientQueryAggregatedField> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_ALT),
                createFullyConfiguredInstance().setAggregateFunction(TEST_AGGREGATE_FUNCTION_ALT),
                createFullyConfiguredInstance().setAggregateFirstLevelFunction(TEST_AGGREGATE_FIRST_LEVEL_FUNCTION_ALT),
                createFullyConfiguredInstance().setExpressionContainer(TEST_EXPRESSION_CONTAINER_ALT),
                createFullyConfiguredInstance().setAggregateType(TEST_AGGREGATE_TYPE_ALT),
                createFullyConfiguredInstance().setAggregateArg(TEST_AGGREGATE_ARG_ALT),
                createFullyConfiguredInstance().setFieldReference(TEST_FIELD_REFERENCE_ALT),
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setAggregateFunction(null),
                createFullyConfiguredInstance().setAggregateFirstLevelFunction(null),
                createFullyConfiguredInstance().setExpressionContainer(null),
                createFullyConfiguredInstance().setAggregateType(null),
                createFullyConfiguredInstance().setAggregateArg(null),
                createFullyConfiguredInstance().setFieldReference(null)
        );
    }

    @Override
    protected ClientQueryAggregatedField createFullyConfiguredInstance() {
        return new ClientQueryAggregatedField()
                .setId(TEST_ID)
                .setAggregateFunction(TEST_AGGREGATE_FUNCTION)
                .setAggregateFirstLevelFunction(TEST_AGGREGATE_FIRST_LEVEL_FUNCTION)
                .setExpressionContainer(TEST_EXPRESSION_CONTAINER)
                .setAggregateType(TEST_AGGREGATE_TYPE)
                .setAggregateArg(TEST_AGGREGATE_ARG)
                .setFieldReference(TEST_FIELD_REFERENCE);
    }

    @Override
    protected ClientQueryAggregatedField createInstanceWithDefaultParameters() {
        return new ClientQueryAggregatedField();
    }

    @Override
    protected ClientQueryAggregatedField createInstanceFromOther(ClientQueryAggregatedField other) {
        return new ClientQueryAggregatedField(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientQueryAggregatedField expected, ClientQueryAggregatedField actual) {
        assertNotSame(expected.getExpressionContainer(), actual.getExpressionContainer());
    }

    /*
     * accept
     */

    @Test
    void accept_without_ExpressionContainer() {
        ClientQueryAggregatedField instance = new ClientQueryAggregatedField();

        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }

    @Test
    void accept_with_ExpressionContainer() {
        ClientExpression mockObject = mock(ClientExpression.class);
        ClientQueryAggregatedField instance = new ClientQueryAggregatedField()
                .setExpressionContainer(new ClientExpressionContainer().setObject(mockObject));

        instance.accept(clientELVisitor);

        verify(mockObject).accept(clientELVisitor);
        verify(clientELVisitor).visit(instance);
    }

    @org.junit.Test
    public void nullTest() {
        Exception ex = null;
        try {
            ClientQueryAggregatedField clientQueryAggregatedField = new ClientQueryAggregatedField((ClientQueryAggregatedField) null);
        } catch (Exception ex2) {
            ex = ex2;
        }
        assertTrue(ex != null);
    }
}