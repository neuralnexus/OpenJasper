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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DomElExpressionCollectionContextTest extends BaseDTOJSONPresentableTest<DomElExpressionCollectionContext> {

    private static final List<DomElExpressionContext> TEST_EXPRESSION_CONTEXTS = Collections.singletonList(new DomElExpressionContext().setExpression(new ClientExpressionContainer().setObject(new ClientNumber(10))));
    private static final List<DomElExpressionContext> TEST_EXPRESSION_CONTEXTS_1 = Collections.singletonList(new DomElExpressionContext().setExpression(new ClientExpressionContainer().setObject(new ClientNumber(101))));
    private static final List<DomElExpressionContext> TEST_EXPRESSION_CONTEXTS_EMPTY = new ArrayList<DomElExpressionContext>();

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

    @Override
    protected void assertFieldsHaveUniqueReferences(DomElExpressionCollectionContext expected, DomElExpressionCollectionContext actual) {
        assertNotSameCollection(expected.getExpressionContexts(), actual.getExpressionContexts());
    }

    /*
     * Preparing
     */

    @Override
    protected List<DomElExpressionCollectionContext> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setExpressionContexts(TEST_EXPRESSION_CONTEXTS_1),
                createFullyConfiguredInstance().setExpressionContexts(TEST_EXPRESSION_CONTEXTS_EMPTY),
                createFullyConfiguredInstance().setAggregate(TEST_AGGREGATE_1),
                createFullyConfiguredInstance().setForbiddenVariableNames(TEST_FORBIDDEN_VARIABLE_NAMES_1),
                createFullyConfiguredInstance().setForbiddenVariableNames(TEST_FORBIDDEN_VARIABLE_NAMES_EMPTY),
                createFullyConfiguredInstance().setResultType(TEST_RESULT_TYPE_1),
                createFullyConfiguredInstance().setSkipTypeAndFunctionsValidation(TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION_1),
                createFullyConfiguredInstance().setVariables(TEST_VARIABLES_1),
                createFullyConfiguredInstance().setVariables(TEST_VARIABLES_EMPTY),
                // null values
                createFullyConfiguredInstance().setExpressionContexts(null),
                createFullyConfiguredInstance().setAggregate(null),
                createFullyConfiguredInstance().setForbiddenVariableNames(null),
                createFullyConfiguredInstance().setResultType(null),
                createFullyConfiguredInstance().setSkipTypeAndFunctionsValidation(null),
                createFullyConfiguredInstance().setVariables(null)
        );
    }

    @Override
    protected DomElExpressionCollectionContext createFullyConfiguredInstance() {
        return new DomElExpressionCollectionContext()
                .setExpressionContexts(TEST_EXPRESSION_CONTEXTS)
                .setAggregate(TEST_AGGREGATE)
                .setForbiddenVariableNames(TEST_FORBIDDEN_VARIABLE_NAMES)
                .setResultType(TEST_RESULT_TYPE)
                .setSkipTypeAndFunctionsValidation(TEST_SKIP_TYPE_AND_FUNCTIONS_VALIDATION)
                .setVariables(TEST_VARIABLES);
    }

    @Override
    protected DomElExpressionCollectionContext createInstanceWithDefaultParameters() {
        return new DomElExpressionCollectionContext();
    }

    @Override
    protected DomElExpressionCollectionContext createInstanceFromOther(DomElExpressionCollectionContext other) {
        return new DomElExpressionCollectionContext(other);
    }
}
