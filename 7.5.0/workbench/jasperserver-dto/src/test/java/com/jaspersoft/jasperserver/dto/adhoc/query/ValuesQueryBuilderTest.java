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
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientPathOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ValuesQueryBuilderTest {
    private static final ClientQueryField CLIENT_QUERY_FIELD = new ClientQueryField().setId("id");
    private static final ClientQueryField CLIENT_QUERY_FIELD_ALTERNATIVE = new ClientQueryField().setId("id2");
    private static final List<ClientQueryField> CLIENT_QUERY_FIELD_LIST = Arrays.asList(
            CLIENT_QUERY_FIELD,
            CLIENT_QUERY_FIELD_ALTERNATIVE);

    private static final String CLIENT_EXPRESSION_STRING = "string";
    private static final ClientExpression CLIENT_EXPRESSION = new ClientString(CLIENT_EXPRESSION_STRING);
    private static final HashMap<String, ClientExpressionContainer> CLIENT_PARAMETERS = new HashMap<String, ClientExpressionContainer>() {{
        put("key", new ClientExpressionContainer().setString("string"));
    }};

    private static final ClientPathOrder CLIENT_PATH_ORDER = new ClientPathOrder().setAscending(false);
    private static final ClientGenericOrder CLIENT_GENERIC_ORDER = new ClientGenericOrder().setAscending(true);
    private static final List<ClientOrder> CLIENT_ORDER_LIST = Arrays.asList(CLIENT_GENERIC_ORDER, CLIENT_PATH_ORDER);

    private ValuesQueryBuilder instance;

    @BeforeEach
    public void setup() {
        instance = new ValuesQueryBuilder();
    }

    @Test
    public void select_fieldsList_builderWithFields() {
        ValuesQueryBuilder result = ValuesQueryBuilder.select(CLIENT_QUERY_FIELD_LIST);

        assertEquals(CLIENT_QUERY_FIELD_LIST, result.getFields());
    }

    @Test
    public void where_clientExpression_builderWithFilters() {
        ValuesQueryBuilder result = instance.where(CLIENT_EXPRESSION);

        assertEquals(CLIENT_EXPRESSION, result.getFilters());
    }

    @Test
    public void where_expression_builderWithFilterExpression() {
        ValuesQueryBuilder result = instance.where(CLIENT_EXPRESSION_STRING);

        assertEquals(CLIENT_EXPRESSION_STRING, result.getFilterExpression());
    }

    @Test
    public void where_parametersAndClientExpression_builderWithParametersAndFilters() {
        ValuesQueryBuilder result = instance.where(CLIENT_EXPRESSION, CLIENT_PARAMETERS);

        assertEquals(CLIENT_PARAMETERS, result.getParameters());
    }

    @Test
    public void orderBy_clientOrderArray_builderWithOrder() {
        ValuesQueryBuilder result = instance.orderBy(CLIENT_GENERIC_ORDER, CLIENT_PATH_ORDER);

        assertEquals(CLIENT_ORDER_LIST, result.getOrder());
    }

    @Test
    public void build_runtimeException() {
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() {
                instance.build();
            }
        });
    }
}
