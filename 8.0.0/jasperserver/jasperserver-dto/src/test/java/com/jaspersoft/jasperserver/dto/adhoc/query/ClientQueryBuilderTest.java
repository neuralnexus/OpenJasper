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

package com.jaspersoft.jasperserver.dto.adhoc.query;

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientPathOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientTopOrBottomNOrder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientQueryBuilderTest {
    private static final Integer CLIENT_LIMIT = 23;
    private static final String CLIENT_NAME = "name";
    private static final String CLIENT_TYPE = "type";
    private static final String CLIENT_ID = "id";
    private static final String CLIENT_CATEGORIZER = "categorizer";
    private static final String CLIENT_AGGREGATE_FUNCTION = "aggregateExpression";

    private static final String CLIENT_EXPRESSION_STRING = "string";
    private static final ClientExpression CLIENT_EXPRESSION = new ClientString(CLIENT_EXPRESSION_STRING);

    private static final List<String> CLIENT_PATH = Arrays.asList("path1", "path2");
    private static final ClientQueryGroup CLIENT_QUERY_GROUP = new ClientQueryGroup().setId(CLIENT_ID);
    private static final ClientDataSourceField CLIENT_DATA_SOURCE_FIELD = new ClientDataSourceField().setName(CLIENT_NAME);

    @Test
    public void source_name_clientDataSourceField() {
        ClientDataSourceField result = ClientQueryBuilder.source(CLIENT_NAME);

        assertEquals(CLIENT_NAME, result.getName());
    }

    @Test
    public void source_nameAndType_clientDataSourceField() {
        ClientDataSourceField result = ClientQueryBuilder.source(CLIENT_NAME, CLIENT_TYPE);

        assertEquals(CLIENT_NAME, result.getName());
        assertEquals(CLIENT_TYPE, result.getType());
    }

    @Test
    public void field_ClientDataSourceField_clientQueryField() {
        ClientQueryField result = ClientQueryBuilder.field(CLIENT_DATA_SOURCE_FIELD);

        assertEquals(CLIENT_NAME, result.getName());
    }

    @Test
    public void field_IdAndClientDataSourceField_clientQueryField() {
        ClientQueryField result = ClientQueryBuilder.field(CLIENT_ID, CLIENT_DATA_SOURCE_FIELD);

        assertEquals(CLIENT_NAME, result.getName());
        assertEquals(CLIENT_ID, result.getId());
    }

    @Test
    public void field_name_clientQueryField() {
        ClientQueryField result = ClientQueryBuilder.field(CLIENT_NAME);

        assertEquals(CLIENT_NAME, result.getName());
    }

    @Test
    public void aggregatedField_fieldAndExpression_clientQueryAggregatedField() {
        ClientQueryAggregatedField result = ClientQueryBuilder.aggregatedField(CLIENT_DATA_SOURCE_FIELD, CLIENT_EXPRESSION_STRING);

        assertEquals(CLIENT_NAME, result.getFieldReference());
        assertEquals(CLIENT_EXPRESSION_STRING, result.getAggregateExpression());
    }

    @Test
    public void aggregatedField_fieldAndClientExpression_clientQueryAggregatedField() {
        ClientQueryAggregatedField result = ClientQueryBuilder.aggregatedField(CLIENT_DATA_SOURCE_FIELD, CLIENT_EXPRESSION);

        assertEquals(CLIENT_NAME, result.getFieldReference());
        assertEquals(CLIENT_EXPRESSION, result.getExpressionContainer().getObject());
    }

    @Test
    public void aggregatedField_idAndFieldAndAggregateFunction_clientQueryAggregatedField() {
        ClientQueryAggregatedField result = ClientQueryBuilder.aggregatedField(CLIENT_ID, CLIENT_NAME, CLIENT_AGGREGATE_FUNCTION);

        assertEquals(CLIENT_ID, result.getId());
        assertEquals(CLIENT_NAME, result.getFieldReference());
        assertEquals(CLIENT_AGGREGATE_FUNCTION, result.getAggregateFunction());
    }

    @Test
    public void asc_field_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.asc(CLIENT_DATA_SOURCE_FIELD);

        assertEquals(true, result.isAscending());
        assertEquals(CLIENT_NAME, ((ClientGenericOrder) result).getFieldReference());
    }

    @Test
    public void asc_group_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.asc(CLIENT_QUERY_GROUP);

        assertEquals(true, result.isAscending());
        assertEquals(CLIENT_ID, ((ClientGenericOrder) result).getFieldReference());
    }

    @Test
    public void asc_name_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.asc(CLIENT_NAME);

        assertEquals(true, result.isAscending());
        assertEquals(CLIENT_NAME, ((ClientGenericOrder) result).getFieldReference());
    }

    @Test
    public void desc_field_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.desc(CLIENT_DATA_SOURCE_FIELD);

        assertEquals(false, result.isAscending());
        assertEquals(CLIENT_NAME, ((ClientGenericOrder) result).getFieldReference());
    }

    @Test
    public void desc_group_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.desc(CLIENT_QUERY_GROUP);

        assertEquals(false, result.isAscending());
        assertEquals(CLIENT_ID, ((ClientGenericOrder) result).getFieldReference());
    }

    @Test
    public void desc_name_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.desc(CLIENT_NAME);

        assertEquals(false, result.isAscending());
        assertEquals(CLIENT_NAME, ((ClientGenericOrder) result).getFieldReference());
    }

    @Test
    public void descAggLevel_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.descAggLevel();

        assertEquals(false, result.isAscending());
        assertEquals(true, ((ClientGenericOrder) result).isAggregation());
    }

    @Test
    public void ascAggLevel_clientGenericOrder() {
        ClientOrder result = ClientQueryBuilder.ascAggLevel();

        assertEquals(true, result.isAscending());
        assertEquals(true, ((ClientGenericOrder) result).isAggregation());
    }

    @Test
    public void ascByMember_path_clientPathOrder() {
        ClientOrder result = ClientQueryBuilder.ascByMember(CLIENT_PATH);

        assertEquals(true, result.isAscending());
        assertEquals(CLIENT_PATH, ((ClientPathOrder) result).getPath());
    }

    @Test
    public void descByMember_path_clientPathOrder() {
        ClientOrder result = ClientQueryBuilder.descByMember(CLIENT_PATH);

        assertEquals(false, result.isAscending());
        assertEquals(CLIENT_PATH, ((ClientPathOrder) result).getPath());
    }

    @Test
    public void topN_pathAndLimit_clientTopOrBottomNOrder() {
        ClientOrder result = ClientQueryBuilder.topN(CLIENT_PATH, CLIENT_LIMIT);

        assertEquals(CLIENT_LIMIT, ((ClientTopOrBottomNOrder) result).getLimit());
        assertEquals(CLIENT_PATH, ((ClientTopOrBottomNOrder) result).getPath());
    }

    @Test
    public void bottomN_pathAndLimit_clientTopOrBottomNOrder() {
        ClientOrder result = ClientQueryBuilder.bottomN(CLIENT_PATH, CLIENT_LIMIT);

        assertEquals(CLIENT_LIMIT, ((ClientTopOrBottomNOrder) result).getLimit());
        assertEquals(CLIENT_PATH, ((ClientTopOrBottomNOrder) result).getPath());
    }

    @Test
    public void group_idAndCategorizerAndField_ClientQueryGroup() {
        ClientQueryGroup result = ClientQueryBuilder.group(CLIENT_ID, CLIENT_CATEGORIZER, CLIENT_DATA_SOURCE_FIELD);

        assertEquals(CLIENT_ID, result.getId());
        assertEquals(CLIENT_CATEGORIZER, result.getCategorizer());
        assertEquals(CLIENT_NAME, result.getFieldName());
    }

    @Test
    public void group_field_ClientQueryGroup() {
        ClientQueryGroup result = ClientQueryBuilder.group(CLIENT_DATA_SOURCE_FIELD);

        assertEquals(CLIENT_NAME, result.getFieldName());
    }

    @Test
    public void group_idAndAndField_ClientQueryGroup() {
        ClientQueryGroup result = ClientQueryBuilder.group(CLIENT_ID, CLIENT_DATA_SOURCE_FIELD);

        assertEquals(CLIENT_ID, result.getId());
        assertEquals(CLIENT_NAME, result.getFieldName());
    }

    @Test
    public void group_idAndCategorizer_ClientQueryGroup() {
        ClientQueryGroup result = ClientQueryBuilder.group(CLIENT_NAME, CLIENT_CATEGORIZER);

        assertEquals(null, result.getId());
        assertEquals(CLIENT_NAME, result.getName());
        assertEquals(CLIENT_CATEGORIZER, result.getCategorizer());
    }

    @Test
    public void allGroup_ClientQueryGroup() {
        ClientQueryGroup result = ClientQueryBuilder.allGroup();

        assertEquals(ClientQueryGroup.ClientAllGroup.class, result.getClass());
    }
}
