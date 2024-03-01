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

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientAggregatesTest {
    private static final String FUN_NAME_FOR_COUNT_ALL = "CountAll";
    private static final String FUN_NAME_FOR_SUM = "Sum";
    private static final String FUN_NAME_FOR_AGGREGATE_FORMULA = "AggregateFormula";
    private static final String FUN_NAME_FOR_COUNT_DISTINCT = "CountDistinct";

    private static final String ID = "id";
    private static final String FIELD_ID = "fieldId";
    private static final String FIELD_NAME = "fieldName";
    private static final String FUNC_NAME = "funcName";
    private static final String EXPRESSION = "expression";
    private static final String ARGUMENTS = "arguments";

    @Test
    public void getFunctionName_countAll() {
        ClientAggregates clientAggregates = ClientAggregates.COUNT_ALL;

        String result = clientAggregates.getFunName();

        assertEquals(FUN_NAME_FOR_COUNT_ALL, result);
    }

    @Test
    public void countAll_clientQueryFiled_countAllFunctionName() {
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);
        ClientQueryAggregatedField result = ClientAggregates.countAll(clientQueryField);

        assertEquals(FUN_NAME_FOR_COUNT_ALL, result.getAggregateFunction());
        assertEquals(FIELD_ID, result.getFieldReference());
    }

    @Test
    public void countAll_clientDataSourceField_countAllFunctionName() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField().setName(FIELD_NAME);
        ClientQueryAggregatedField result = ClientAggregates.countAll(clientDataSourceField);

        assertEquals(FUN_NAME_FOR_COUNT_ALL, result.getAggregateFunction());
        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void sum_clientQueryFiled_countAllFunctionName() {
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);
        ClientQueryAggregatedField result = ClientAggregates.sum(clientQueryField);

        assertEquals(FUN_NAME_FOR_SUM, result.getAggregateFunction());
        assertEquals(FIELD_ID, result.getFieldReference());
    }

    @Test
    public void sum_clientQueryFiledAndId_countAllFunctionName() {
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);
        ClientQueryAggregatedField result = ClientAggregates.sum(ID, clientQueryField);

        assertEquals(FUN_NAME_FOR_SUM, result.getAggregateFunction());
        assertEquals(ID, result.getId());
        assertEquals(FIELD_ID, result.getFieldReference());
    }

    @Test
    public void sum_clientDataSourceField_countAllFunctionName() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField().setName(FIELD_NAME);
        ClientQueryAggregatedField result = ClientAggregates.sum(clientDataSourceField);

        assertEquals(FUN_NAME_FOR_SUM, result.getAggregateFunction());
        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void countDistinct_clientQueryFiled_countAllFunctionName() {
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);
        ClientQueryAggregatedField result = ClientAggregates.countDistinct(clientQueryField);

        assertEquals(FUN_NAME_FOR_COUNT_DISTINCT, result.getAggregateFunction());
        assertEquals(FIELD_ID, result.getFieldReference());
    }

    @Test
    public void aggregateFormula_clientDataSourceField_countAllFunctionName() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField().setName(FIELD_NAME);
        ClientQueryAggregatedField result = ClientAggregates.aggregateFormula(clientDataSourceField);

        assertEquals(FUN_NAME_FOR_AGGREGATE_FORMULA, result.getAggregateFunction());
        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void aggregateFormula_fieldNameString_countAllFunctionName() {
        ClientQueryAggregatedField result = ClientAggregates.aggregateFormula(FIELD_NAME);

        assertEquals(FUN_NAME_FOR_AGGREGATE_FORMULA, result.getAggregateFunction());
        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void aggregate_clientQueryField_clientQueryAggregatedField() {
        ClientExpressionContainer clientExpressionContainer = new ClientExpressionContainer(EXPRESSION);
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);

        ClientQueryAggregatedField result = ClientAggregates.aggregate(ID, clientQueryField, FUNC_NAME, EXPRESSION, ARGUMENTS);

        assertEquals(ID, result.getId());
        assertEquals(FIELD_ID, result.getFieldReference());
        assertEquals(FUNC_NAME, result.getAggregateFunction());
        assertEquals(clientExpressionContainer, result.getExpressionContainer());
        assertEquals(ARGUMENTS, result.getAggregateArg());
    }

    @Test
    public void aggregate_clientQueryFieldAndNullId_fieldNameInFieldReference() {
        ClientQueryField clientQueryField = new ClientQueryField().setFieldName(FIELD_NAME);

        ClientQueryAggregatedField result = ClientAggregates.aggregate(ID, clientQueryField, FUNC_NAME, EXPRESSION, ARGUMENTS);

        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void aggregate_clientQueryFieldAndNullExpr_nullExpressionContainer() {
        ClientQueryField clientQueryField = new ClientQueryField();

        ClientQueryAggregatedField result = ClientAggregates.aggregate(ID, clientQueryField, FUNC_NAME, null, ARGUMENTS);

        assertNull(result.getExpressionContainer());
    }

    @Test
    public void aggregate_clientQueryFieldWithoutExpr_nullExpressionContainer() {
        ClientQueryField clientQueryField = new ClientQueryField();

        ClientQueryAggregatedField result = ClientAggregates.aggregate(ID, clientQueryField, FUNC_NAME, ARGUMENTS);

        assertNull(result.getExpressionContainer());
    }

    @Test
    public void aggregate_clientQueryFieldWithoutExprWithoutId_nullExpressionContainerNullId() {
        ClientQueryField clientQueryField = new ClientQueryField();

        ClientQueryAggregatedField result = ClientAggregates.aggregate(clientQueryField, FUNC_NAME, ARGUMENTS);

        assertNull(result.getExpressionContainer());
        assertNull(result.getId());
    }

    @Test
    public void aggregate_clientDataSourceField_clientQueryAggregatedField() {
        ClientExpressionContainer clientExpressionContainer = new ClientExpressionContainer(EXPRESSION);
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField().setName(FIELD_NAME);

        ClientQueryAggregatedField result = ClientAggregates.aggregate(ID, clientDataSourceField, FUNC_NAME, EXPRESSION, ARGUMENTS);

        assertEquals(FIELD_NAME, result.getFieldReference());
        assertEquals(FUNC_NAME, result.getAggregateFunction());
        assertEquals(clientExpressionContainer, result.getExpressionContainer());
        assertEquals(ARGUMENTS, result.getAggregateArg());
    }

    @Test
    public void aggregate_clientDataSourceFieldAndNullExpr_nullExpressionContainer() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField();

        ClientQueryAggregatedField result = ClientAggregates.aggregate(ID, clientDataSourceField, FUNC_NAME, null, ARGUMENTS);

        assertNull(result.getExpressionContainer());
    }

    @Test
    public void aggregate_clientDataSourceFieldWithoutId_nullId() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField();

        ClientQueryAggregatedField result = ClientAggregates.aggregate(clientDataSourceField, FUNC_NAME, EXPRESSION, ARGUMENTS);

        assertNull(result.getId());
    }

    @Test
    public void aggregate_clientDataSourceFieldWithoutExprWithoutId_nullExpressionContainerNullId() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField();

        ClientQueryAggregatedField result = ClientAggregates.aggregate(clientDataSourceField, FUNC_NAME, ARGUMENTS);

        assertNull(result.getExpressionContainer());
        assertNull(result.getId());
    }

    @Test
    public void aggregate_clientDataSourceFieldOnly_clientQueryAggregatedField() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField().setName(FIELD_NAME);

        ClientQueryAggregatedField result = ClientAggregates.aggregate(clientDataSourceField);

        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void aggregate_clientQueryFieldOnly_clientQueryAggregatedField() {
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);

        ClientQueryAggregatedField result = ClientAggregates.aggregate(clientQueryField);

        assertEquals(FIELD_ID, result.getFieldReference());
    }

    @Test
    public void aggregate_clientQueryFieldOnlyAndNullId_fieldNameInFieldReference() {
        ClientQueryField clientQueryField = new ClientQueryField().setFieldName(FIELD_NAME);

        ClientQueryAggregatedField result = ClientAggregates.aggregate(clientQueryField);

        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void aggregate_stringField_clientQueryAggregatedField() {
        ClientQueryAggregatedField result = ClientAggregates.aggregate(FIELD_NAME);

        assertEquals(FIELD_NAME, result.getFieldReference());
    }

    @Test
    public void custom_clientQueryField_clientQueryAggregatedField() {
        ClientExpressionContainer clientExpressionContainer = new ClientExpressionContainer(EXPRESSION);
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);

        ClientQueryAggregatedField result = ClientAggregates.custom(clientQueryField, EXPRESSION);

        assertEquals(FIELD_ID, result.getFieldReference());
        assertEquals(clientExpressionContainer, result.getExpressionContainer());
    }

    @Test
    public void custom_clientQueryFieldAndNullExpr_nullExpressionContainer() {
        ClientQueryField clientQueryField = new ClientQueryField();

        ClientQueryAggregatedField result = ClientAggregates.custom(clientQueryField, ((String) null));

        assertNull(result.getExpressionContainer());
    }

    @Test
    public void custom_clientDataSourceField_clientQueryAggregatedField() {
        ClientExpressionContainer clientExpressionContainer = new ClientExpressionContainer(EXPRESSION);
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField().setName(FIELD_NAME);

        ClientQueryAggregatedField result = ClientAggregates.custom(clientDataSourceField, EXPRESSION);

        assertEquals(FIELD_NAME, result.getFieldReference());
        assertEquals(clientExpressionContainer, result.getExpressionContainer());
    }

    @Test
    public void custom_clientDataSourceFieldAndNullExpr_nullExpressionContainer() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField();

        ClientQueryAggregatedField result = ClientAggregates.custom(clientDataSourceField, ((String) null));

        assertNull(result.getExpressionContainer());
    }

    @Test
    public void custom_clientQueryFieldAndClientExpression_clientQueryAggregatedField() {
        ClientExpression clientExpression = new ClientNull();
        ClientExpressionContainer clientExpressionContainer = new ClientExpressionContainer(clientExpression);
        ClientQueryField clientQueryField = new ClientQueryField().setId(FIELD_ID);

        ClientQueryAggregatedField result = ClientAggregates.custom(clientQueryField, clientExpression);

        assertEquals(FIELD_ID, result.getFieldReference());
        assertEquals(clientExpressionContainer, result.getExpressionContainer());
    }

    @Test
    public void custom_clientQueryFieldAndNullClientExpr_nullExpressionContainer() {
        ClientQueryField clientQueryField = new ClientQueryField();

        ClientQueryAggregatedField result = ClientAggregates.custom(clientQueryField, ((ClientExpression) null));

        assertNull(result.getExpressionContainer());
    }

    @Test
    public void custom_clientDataSourceFieldAndClientExpression_clientQueryAggregatedField() {
        ClientExpression clientExpression = new ClientNull();
        ClientExpressionContainer clientExpressionContainer = new ClientExpressionContainer(clientExpression);
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField().setName(FIELD_NAME);

        ClientQueryAggregatedField result = ClientAggregates.custom(clientDataSourceField, clientExpression);

        assertEquals(FIELD_NAME, result.getFieldReference());
        assertEquals(clientExpressionContainer, result.getExpressionContainer());
    }

    @Test
    public void custom_clientDataSourceFieldAndNullClientExpr_nullExpressionContainer() {
        ClientDataSourceField clientDataSourceField = new ClientDataSourceField();

        ClientQueryAggregatedField result = ClientAggregates.custom(clientDataSourceField, ((ClientExpression) null));

        assertNull(result.getExpressionContainer());
    }

    @Test
    public void custom_idStringAndClientExpression_clientQueryAggregatedField() {
        ClientExpression clientExpression = new ClientNull();
        ClientExpressionContainer clientExpressionContainer = new ClientExpressionContainer(clientExpression);

        ClientQueryAggregatedField result = ClientAggregates.custom(ID, clientExpression);

        assertEquals(ID, result.getId());
        assertEquals(clientExpressionContainer, result.getExpressionContainer());
    }

    @Test
    public void custom_idStringAndNullClientExpression_nullExpressionContainer() {
        ClientQueryAggregatedField result = ClientAggregates.custom(ID, null);

        assertNull(result.getExpressionContainer());
    }

}
