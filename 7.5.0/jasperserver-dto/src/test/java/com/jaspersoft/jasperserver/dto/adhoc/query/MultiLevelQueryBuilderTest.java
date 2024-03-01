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

package com.jaspersoft.jasperserver.dto.adhoc.query;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientPathOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class MultiLevelQueryBuilderTest {
    private static final ClientQueryField CLIENT_QUERY_FIELD = new ClientQueryField().setId("id");
    private static final ClientQueryField CLIENT_QUERY_FIELD_ALTERNATIVE = new ClientQueryField().setId("id2");
    private static final List<ClientQueryField> CLIENT_QUERY_FIELD_LIST = Arrays.asList(
            CLIENT_QUERY_FIELD,
            CLIENT_QUERY_FIELD_ALTERNATIVE);

    private static final ClientQueryAggregatedField CLIENT_QUERY_AGGREGATED_FIELD = new ClientQueryAggregatedField().setId("id");
    private static final ClientQueryAggregatedField CLIENT_QUERY_AGGREGATED_FIELD_ALTERNATIVE = new ClientQueryAggregatedField().setId("id2");
    private static final List<ClientQueryAggregatedField> CLIENT_QUERY_AGGREGATED_FIELD_LIST = Arrays.asList(
            CLIENT_QUERY_AGGREGATED_FIELD,
            CLIENT_QUERY_AGGREGATED_FIELD_ALTERNATIVE);

    private static final String CLIENT_EXPRESSION_STRING = "string";
    private static final ClientExpression CLIENT_EXPRESSION = new ClientString(CLIENT_EXPRESSION_STRING);
    private static final HashMap<String, ClientExpressionContainer> CLIENT_PARAMETERS = new HashMap<String, ClientExpressionContainer>() {{
        put("key", new ClientExpressionContainer().setString("string"));
    }};

    private static final ClientPathOrder CLIENT_PATH_ORDER = new ClientPathOrder().setAscending(false);
    private static final ClientGenericOrder CLIENT_GENERIC_ORDER = new ClientGenericOrder().setAscending(true);
    private static final List<ClientOrder> CLIENT_ORDER_LIST = Arrays.asList(CLIENT_GENERIC_ORDER, CLIENT_PATH_ORDER);

    private static final ClientQueryLevel CLIENT_QUERY_LEVEL = new ClientQueryLevel().setId("id");
    private static final ClientQueryGroup CLIENT_QUERY_GROUP = new ClientQueryGroup().setId("id2");
    private static final List<ClientQueryGroup> CLIENT_QUERY_GROUP_LIST = Arrays.asList(CLIENT_QUERY_LEVEL, CLIENT_QUERY_GROUP);

    private static final Integer CLIENT_LIMIT = 23;

    private MultiLevelQueryBuilder instance;

    @BeforeEach
    public void setup() {
        instance = new MultiLevelQueryBuilder();
    }

    @Test
    public void select_fieldsList_builderWithFields() {
        MultiLevelQueryBuilder result = MultiLevelQueryBuilder.select(CLIENT_QUERY_FIELD_LIST);

        assertEquals(CLIENT_QUERY_FIELD_LIST, result.getFields());
    }

    @Test
    public void select_fieldsArray_builderWithFields() {
        MultiLevelQueryBuilder result = MultiLevelQueryBuilder.select(CLIENT_QUERY_FIELD, CLIENT_QUERY_FIELD_ALTERNATIVE);

        assertEquals(CLIENT_QUERY_FIELD_LIST, result.getFields());
    }

    @Test
    public void select_fieldsListAndAggregates_builderWithFieldsAndAggregates() {
        MultiLevelQueryBuilder result = MultiLevelQueryBuilder.select(CLIENT_QUERY_FIELD_LIST, CLIENT_QUERY_AGGREGATED_FIELD_LIST);

        assertEquals(CLIENT_QUERY_FIELD_LIST, result.getFields());
        assertEquals(CLIENT_QUERY_AGGREGATED_FIELD_LIST, result.getAggregates());
    }

    @Test
    public void where_clientExpression_builderWithFilters() {
        MultiLevelQueryBuilder result = instance.where(CLIENT_EXPRESSION);

        assertEquals(CLIENT_EXPRESSION, result.getFilters());
    }

    @Test
    public void where_expression_builderWithFilterExpression() {
        MultiLevelQueryBuilder result = instance.where(CLIENT_EXPRESSION_STRING);

        assertEquals(CLIENT_EXPRESSION_STRING, result.getFilterExpression());
    }

    @Test
    public void where_parameters_builderWithParameters() {
        MultiLevelQueryBuilder result = instance.where(CLIENT_PARAMETERS);

        assertEquals(CLIENT_PARAMETERS, result.getParameters());
    }

    @Test
    public void where_parametersAndClientExpression_builderWithParametersAndFilters() {
        MultiLevelQueryBuilder result = instance.where(CLIENT_EXPRESSION, CLIENT_PARAMETERS);

        assertEquals(CLIENT_PARAMETERS, result.getParameters());
    }

    @Test
    public void orderBy_clientOrderArray_builderWithOrder() {
        MultiLevelQueryBuilder result = instance.orderBy(CLIENT_GENERIC_ORDER, CLIENT_PATH_ORDER);

        assertEquals(CLIENT_ORDER_LIST, result.getOrder());
    }

    @Test
    public void aggregates_clientAggregates_builderWithAggregates() {
        MultiLevelQueryBuilder result = instance.aggregates(CLIENT_QUERY_AGGREGATED_FIELD, CLIENT_QUERY_AGGREGATED_FIELD_ALTERNATIVE);

        assertEquals(CLIENT_QUERY_AGGREGATED_FIELD_LIST, result.getAggregates());
    }

    @Test
    public void groupBy_clientQueryGroup_builderWithGroups() {
        MultiLevelQueryBuilder result = instance.groupBy(CLIENT_QUERY_LEVEL, CLIENT_QUERY_GROUP);

        assertEquals(CLIENT_QUERY_GROUP_LIST, result.getGroups());
    }

    @Test
    public void limit_clientLimit_builderWithGroups() {
        ClientQueryBuilder result = instance.limit(CLIENT_LIMIT);

        assertEquals(CLIENT_LIMIT, result.getLimit());
    }

    @Test
    public void setGroups_groups() {
        instance.setGroups(CLIENT_QUERY_GROUP_LIST);

        assertEquals(CLIENT_QUERY_GROUP_LIST, instance.getGroups());
    }

    @Test
    public void setAggregates_aggregates() {
        instance.setAggregates(CLIENT_QUERY_AGGREGATED_FIELD_LIST);

        assertEquals(CLIENT_QUERY_AGGREGATED_FIELD_LIST, instance.getAggregates());
    }

    @Test
    public void setFields_fields() {
        instance.setFields(CLIENT_QUERY_FIELD_LIST);

        assertEquals(CLIENT_QUERY_FIELD_LIST, instance.getFields());
    }

    @Test
    public void setFilters_filters() {
        instance.setFilters(CLIENT_EXPRESSION);

        assertEquals(CLIENT_EXPRESSION, instance.getFilters());
    }

    @Test
    public void setFilterExpression_filterExpression() {
        instance.setFilterExpression(CLIENT_EXPRESSION_STRING);

        assertEquals(CLIENT_EXPRESSION_STRING, instance.getFilterExpression());
    }

    @Test
    public void setParameters_parameters() {
        instance.setParameters(CLIENT_PARAMETERS);

        assertEquals(CLIENT_PARAMETERS, instance.getParameters());
    }

    @Test
    public void setOrder_order() {
        instance.setOrder(CLIENT_ORDER_LIST);

        assertEquals(CLIENT_ORDER_LIST, instance.getOrder());
    }

    @Test
    public void setLimit_limit() {
        instance.setLimit(CLIENT_LIMIT);

        assertEquals(CLIENT_LIMIT, instance.getLimit());
    }

    @Test
    public void build_nullClientWhere_whereWithParameters() {
        ClientWhere expected = new ClientWhere(CLIENT_PARAMETERS);
        instance.setParameters(CLIENT_PARAMETERS);

        ClientMultiLevelQuery result = instance.build();

        assertEquals(expected, result.getWhere());
    }

    @Test
    public void build_notNullClientWhere_whereWithParameters() {
        ClientWhere expected = new ClientWhere(CLIENT_EXPRESSION).setParameters(CLIENT_PARAMETERS);
        instance.setParameters(CLIENT_PARAMETERS);
        instance.setFilters(CLIENT_EXPRESSION);

        ClientMultiLevelQuery result = instance.build();

        assertEquals(expected, result.getWhere());
    }
}
