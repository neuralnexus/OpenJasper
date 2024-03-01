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

package com.jaspersoft.jasperserver.dto.adhoc.datasource;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientDataSourceFieldTest extends BaseDTOJSONPresentableTest<ClientDataSourceField> {

    private static final String TEST_NAME = "TEST_NAME";
    private static final String TEST_NAME_ALT = "TEST_NAME_ALT";

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_ALT = "TEST_TYPE_ALT";

    private static final String TEST_FORMAT = "TEST_FORMAT";
    private static final String TEST_FORMAT_ALT = "TEST_FORMAT_ALT";

    private static final String TEST_HIERARCHY_NAME = "TEST_HIERARCHY_NAME";
    private static final String TEST_HIERARCHY_NAME_ALT = "TEST_HIERARCHY_NAME_ALT";

    private static final String TEST_AGGREGATE_FUNCTION = "TEST_AGGREGATE_FUNCTION";
    private static final String TEST_AGGREGATE_FUNCTION_ALT = "TEST_AGGREGATE_FUNCTION_ALT";

    private static final String TEST_AGGREGATE_EXPRESSION = "TEST_AGGREGATE_EXPRESSION";
    private static final String TEST_AGGREGATE_EXPRESSION_ALT = "TEST_AGGREGATE_EXPRESSION_ALT";

    private static final String TEST_AGGREGATE_FIRST_LEVEL_FUNCTION = "TEST_AGGREGATE_FIRST_LEVEL_FUNCTION";
    private static final String TEST_AGGREGATE_FIRST_LEVEL_FUNCTION_ALT = "TEST_AGGREGATE_FIRST_LEVEL_FUNCTION_ALT";

    private static final String TEST_AGGREGATE_ARG = "TEST_AGGREGATE_ARG";
    private static final String TEST_AGGREGATE_ARG_ALT = "TEST_AGGREGATE_ARG_ALT";

    private static final String TEST_AGGREGATE_TYPE = "TEST_AGGREGATE_TYPE";
    private static final String TEST_AGGREGATE_TYPE_ALT = "TEST_AGGREGATE_TYPE_ALT";

    @Override
    protected List<ClientDataSourceField> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(TEST_NAME_ALT),
                createFullyConfiguredInstance().setType(TEST_TYPE_ALT),
                createFullyConfiguredInstance().setFormat(TEST_FORMAT_ALT),
                createFullyConfiguredInstance().setHierarchyName(TEST_HIERARCHY_NAME_ALT),
                createFullyConfiguredInstance().setAggregateFunction(TEST_AGGREGATE_FUNCTION_ALT),
                createFullyConfiguredInstance().setAggregateExpression(TEST_AGGREGATE_EXPRESSION_ALT),
                createFullyConfiguredInstance().setAggregateFirstLevelFunction(TEST_AGGREGATE_FIRST_LEVEL_FUNCTION_ALT),
                createFullyConfiguredInstance().setAggregateArg(TEST_AGGREGATE_ARG_ALT),
                createFullyConfiguredInstance().setAggregateType(TEST_AGGREGATE_TYPE_ALT),
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setFormat(null),
                createFullyConfiguredInstance().setHierarchyName(null),
                createFullyConfiguredInstance().setAggregateFunction(null),
                createFullyConfiguredInstance().setAggregateExpression(null),
                createFullyConfiguredInstance().setAggregateFirstLevelFunction(null),
                createFullyConfiguredInstance().setAggregateArg(null),
                createFullyConfiguredInstance().setAggregateType(null)
        );
    }

    @Override
    protected ClientDataSourceField createFullyConfiguredInstance() {
        return new ClientDataSourceField()
                .setName(TEST_NAME)
                .setType(TEST_TYPE)
                .setFormat(TEST_FORMAT)
                .setHierarchyName(TEST_HIERARCHY_NAME)
                .setAggregateFunction(TEST_AGGREGATE_FUNCTION)
                .setAggregateExpression(TEST_AGGREGATE_EXPRESSION)
                .setAggregateFirstLevelFunction(TEST_AGGREGATE_FIRST_LEVEL_FUNCTION)
                .setAggregateArg(TEST_AGGREGATE_ARG)
                .setAggregateType(TEST_AGGREGATE_TYPE);
    }

    @Override
    protected ClientDataSourceField createInstanceWithDefaultParameters() {
        return new ClientDataSourceField();
    }

    @Override
    protected ClientDataSourceField createInstanceFromOther(ClientDataSourceField other) {
        return new ClientDataSourceField(other);
    }
}