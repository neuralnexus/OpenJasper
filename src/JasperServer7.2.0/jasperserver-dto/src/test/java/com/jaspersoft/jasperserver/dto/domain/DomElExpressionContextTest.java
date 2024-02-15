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

package com.jaspersoft.jasperserver.dto.domain;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DomElExpressionContextTest extends BaseDTOJSONPresentableTest<DomElExpressionContext> {

    private static final ClientExpressionContainer TEST_EXPRESSIONS = new ClientExpressionContainer().setObject(new ClientNumber(10));
    private static final ClientExpressionContainer TEST_EXPRESSIONS_1 = new ClientExpressionContainer().setObject(new ClientNumber(101));

    private static final Boolean TEST_AGGREGATE = true;
    private static final Boolean TEST_AGGREGATE_1 = false;

    private static final String TEST_FORBIDDEN_VARIABLE_NAME = "TEST_FORBIDDEN_VARIABLE_NAME";
    private static final String TEST_FORBIDDEN_VARIABLE_NAME_1 = "TEST_FORBIDDEN_VARIABLE_NAME_1";

    private static final List<String> TEST_FORBIDDEN_VARIABLE_NAMES = new ArrayList<String>(Collections.singletonList(TEST_FORBIDDEN_VARIABLE_NAME));
    private static final List<String> TEST_FORBIDDEN_VARIABLE_NAMES_1 = Collections.singletonList(TEST_FORBIDDEN_VARIABLE_NAME_1);
    private static final List<String> TEST_FORBIDDEN_VARIABLE_NAMES_EMPTY = new ArrayList<String>();

    private static final String TEST_RESULT_TYPE = "TEST_RESULT_TYPE";
    private static final String TEST_RESULT_TYPE_1 = "TEST_RESULT_TYPE_1";

    private static final Boolean TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION = true;
    private static final Boolean TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION_1 = false;

    private static final List<DomElVariable> TEST_VARIABLES = Collections.singletonList(new DomElVariable().setName("TEST_NAME"));
    private static final List<DomElVariable> TEST_VARIABLES_1 = Collections.singletonList(new DomElVariable().setName("TEST_NAME_1"));
    private static final List<DomElVariable> TEST_VARIABLES_EMPTY = new ArrayList<DomElVariable>();


    @Test
    public void testConstructor() {
        BaseDomElContext context = createFullyConfiguredInstance();
        ClientExpressionContainer container = new ClientExpressionContainer();
        DomElExpressionContext instance = new DomElExpressionContext(context, container);

        assertSame(instance.getExpression(), container);

        assertEquals(TEST_AGGREGATE, instance.getAggregate());
        assertEquals(TEST_FORBIDDEN_VARIABLE_NAMES, instance.getForbiddenVariableNames());
        assertEquals(TEST_RESULT_TYPE, instance.getResultType());
        assertEquals(TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION, instance.getNonStrictMode());
        assertEquals(TEST_VARIABLES, instance.getVariables());
    }

    @Test
    public void test_addForbiddenVariableNames_withoutNames() {
        BaseDomElContext context = createInstanceWithDefaultParameters();

        assertNull(context.getForbiddenVariableNames());
        context.addForbiddenVariableNames("SomeName");

        assertTrue(context.getForbiddenVariableNames().contains("SomeName"));
    }

    @Test
    public void test_addForbiddenVariableNames_withSomeNames() {
        BaseDomElContext context = createFullyConfiguredInstance();

        assertNotNull(context.getForbiddenVariableNames());
        context.addForbiddenVariableNames("SomeName");

        assertTrue(context.getForbiddenVariableNames().contains("SomeName"));
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(DomElExpressionContext expected, DomElExpressionContext actual) {
        assertNotSame(expected.getExpression(), actual.getExpression());

        assertNotSame(expected.getForbiddenVariableNames(), actual.getForbiddenVariableNames());

        assertNotSame(expected.getVariables(), actual.getVariables());
        assertNotSame(expected.getVariables().get(0), actual.getVariables().get(0));
    }

    /*
     * Preparing
     */

    @Override
    protected List<DomElExpressionContext> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setExpression(TEST_EXPRESSIONS_1),
                createFullyConfiguredInstance().setAggregate(TEST_AGGREGATE_1),
                createFullyConfiguredInstance().setForbiddenVariableNames(TEST_FORBIDDEN_VARIABLE_NAMES_1),
                createFullyConfiguredInstance().setForbiddenVariableNames(TEST_FORBIDDEN_VARIABLE_NAMES_EMPTY),
                createFullyConfiguredInstance().setResultType(TEST_RESULT_TYPE_1),
                createFullyConfiguredInstance().setNonStrictMode(TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION_1),
                createFullyConfiguredInstance().setVariables(TEST_VARIABLES_1),
                createFullyConfiguredInstance().setVariables(TEST_VARIABLES_EMPTY),
                // null values
                createFullyConfiguredInstance().setExpression(null),
                createFullyConfiguredInstance().setAggregate(null),
                createFullyConfiguredInstance().setForbiddenVariableNames(null),
                createFullyConfiguredInstance().setResultType(null),
                createFullyConfiguredInstance().setNonStrictMode(null),
                createFullyConfiguredInstance().setVariables(null)
        );
    }

    @Override
    protected DomElExpressionContext createFullyConfiguredInstance() {
        // parent properties
        DomElExpressionContext instance = new DomElExpressionContext()
                .setAggregate(TEST_AGGREGATE)
                .setForbiddenVariableNames(TEST_FORBIDDEN_VARIABLE_NAMES)
                .setResultType(TEST_RESULT_TYPE)
                .setNonStrictMode(TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION)
                .setSkipTypeAndFunctionsValidation(TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION)
                .setSkipUndefinedVariablesCheck(TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION)
                .setVariables(TEST_VARIABLES);
        return instance
                .setExpression(TEST_EXPRESSIONS);
    }

    @Override
    protected DomElExpressionContext createInstanceWithDefaultParameters() {
        return new DomElExpressionContext();
    }

    @Override
    protected DomElExpressionContext createInstanceFromOther(DomElExpressionContext other) {
        return new DomElExpressionContext(other);
    }
}
