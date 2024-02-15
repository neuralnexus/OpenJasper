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
import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientExpandable;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientLevelExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientMemberExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientPathOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class MultiAxisQueryBuilderTest {
    private static final ClientQueryField CLIENT_QUERY_FIELD = new ClientQueryField().setId("string");
    private static final ClientQueryField CLIENT_QUERY_FIELD_ALTERNATIVE = new ClientQueryField().setId("string2");

    private static final ClientQueryAggregatedField CLIENT_QUERY_AGGREGATED_FIELD = new ClientQueryAggregatedField().setId("id");
    private static final ClientQueryAggregatedField CLIENT_QUERY_AGGREGATED_FIELD_ALTERNATIVE = new ClientQueryAggregatedField().setId("id2");
    private static final List<ClientQueryAggregatedField> CLIENT_QUERY_AGGREGATED_FIELD_LIST = Arrays.asList(
            CLIENT_QUERY_AGGREGATED_FIELD,
            CLIENT_QUERY_AGGREGATED_FIELD_ALTERNATIVE);


    private static final ClientPathOrder CLIENT_PATH_ORDER = new ClientPathOrder().setAscending(false);
    private static final ClientGenericOrder CLIENT_GENERIC_ORDER = new ClientGenericOrder().setAscending(true);
    private static final List<ClientOrder> CLIENT_ORDER_LIST = Arrays.asList(CLIENT_GENERIC_ORDER, CLIENT_PATH_ORDER);

    private static final ClientQueryLevel CLIENT_QUERY_LEVEL = new ClientQueryLevel().setId("id");
    private static final ClientQueryLevel CLIENT_QUERY_LEVEL_ALTERNATIVE = new ClientQueryLevel().setId("id2");
    private static final List<ClientQueryLevel> CLIENT_QUERY_LEVEL_LIST = Arrays.asList(CLIENT_QUERY_LEVEL, CLIENT_QUERY_LEVEL_ALTERNATIVE);

    private static final Integer CLIENT_LIMIT = 23;
    private static final String CLIENT_NAME = "name";
    private static final String CLIENT_ID = "id";
    private static final String CLIENT_CATEGORIZER = "categorizer";
    private static final String CLIENT_DIMENSION = "dimensionName";
    private static final List<String> CLIENT_PATH = Collections.singletonList(CLIENT_ID);

    private static final ClientDataSourceField CLIENT_DATA_SOURCE_FIELD = new ClientDataSourceField().setName(CLIENT_NAME);
    private static final ClientDataSourceField CLIENT_DATA_SOURCE_FIELD_ALTERNATIVE = new ClientDataSourceField().setName("name2");

    private static final ClientLevelExpansion CLIENT_LEVEL_EXPANSION = new ClientLevelExpansion()
            .setLevelReference("string")
            .setExpanded(true);
    private static final ClientLevelExpansion CLIENT_LEVEL_EXPANSION_ALTERNATIVE = new ClientLevelExpansion()
            .setLevelReference("string2")
            .setExpanded(true);
    private static final List<ClientLevelExpansion> CLIENT_LEVEL_EXPANSION_LIST = Arrays.asList(CLIENT_LEVEL_EXPANSION, CLIENT_LEVEL_EXPANSION_ALTERNATIVE);

    private static final ClientLevelExpansion CLIENT_LEVEL_EXPANSION_COLLAPSED = new ClientLevelExpansion()
            .setLevelReference("string")
            .setExpanded(false);
    private static final ClientLevelExpansion CLIENT_LEVEL_EXPANSION__COLLAPSED_ALTERNATIVE = new ClientLevelExpansion()
            .setLevelReference("string2")
            .setExpanded(false);
    private static final List<ClientLevelExpansion> CLIENT_LEVEL_EXPANSION_COLLAPSED_LIST = Arrays.asList(CLIENT_LEVEL_EXPANSION_COLLAPSED, CLIENT_LEVEL_EXPANSION__COLLAPSED_ALTERNATIVE);

    private static final ClientDataSourceLevel CLIENT_DATA_SOURCE_LEVEL = new ClientDataSourceLevel().setDimensionName(CLIENT_DIMENSION);

    private MultiAxisQueryBuilder instance;

    @BeforeEach
    public void setup() {
        instance = new MultiAxisQueryBuilder();
    }

    @Test
    public void select_aggregatesList_builderWithAggregates() {
        MultiAxisQueryBuilder result = MultiAxisQueryBuilder.select(CLIENT_QUERY_AGGREGATED_FIELD_LIST);

        assertEquals(CLIENT_QUERY_AGGREGATED_FIELD_LIST, result.getAggregates());
    }

    @Test
    public void select_builderWithEmptyAggregates() {
        MultiAxisQueryBuilder result = MultiAxisQueryBuilder.select();

        assertEquals(Collections.<ClientQueryAggregatedField>emptyList(), result.getAggregates());
    }

    @Test
    public void select_aggregatesArray_builderWithAggregates() {
        MultiAxisQueryBuilder result = MultiAxisQueryBuilder.select(CLIENT_QUERY_AGGREGATED_FIELD, CLIENT_QUERY_AGGREGATED_FIELD_ALTERNATIVE);

        assertEquals(CLIENT_QUERY_AGGREGATED_FIELD_LIST, result.getAggregates());
    }

    @Test
    public void select_dataSourceFields_builderWithAggregates() {
        List<ClientQueryAggregatedField> aggregatedFields = Arrays.asList(
                new ClientQueryAggregatedField().setDataSourceField(CLIENT_DATA_SOURCE_FIELD),
                new ClientQueryAggregatedField().setDataSourceField(CLIENT_DATA_SOURCE_FIELD_ALTERNATIVE));

        MultiAxisQueryBuilder result = MultiAxisQueryBuilder.select(CLIENT_DATA_SOURCE_FIELD, CLIENT_DATA_SOURCE_FIELD_ALTERNATIVE);

        assertEquals(aggregatedFields, result.getAggregates());
    }

    @Test
    public void sourceLevel_nameAndDimension_clientDataSourceLevel() {
        ClientDataSourceLevel result = MultiAxisQueryBuilder.sourceLevel(CLIENT_NAME, CLIENT_DIMENSION);

        assertEquals(CLIENT_NAME, result.getName());
        assertEquals(CLIENT_DIMENSION, result.getDimensionName());
    }

    @Test
    public void groupByRows_clientQueryLevels_builderWithRowLevels() {
        MultiAxisQueryBuilder result = instance.groupByRows(CLIENT_QUERY_LEVEL, CLIENT_QUERY_LEVEL_ALTERNATIVE);

        assertEquals(CLIENT_QUERY_LEVEL_LIST, result.getRowLevels());
    }

    @Test
    public void groupByColumns_clientQueryLevels_builderWithColumnLevels() {
        MultiAxisQueryBuilder result = instance.groupByColumns(CLIENT_QUERY_LEVEL, CLIENT_QUERY_LEVEL_ALTERNATIVE);

        assertEquals(CLIENT_QUERY_LEVEL_LIST, result.getColumnLevels());
    }

    @Test
    public void expandInRows_clientExpandable_builderWithRowExpansion() {
        MultiAxisQueryBuilder result = instance.expandInRows(CLIENT_LEVEL_EXPANSION, CLIENT_LEVEL_EXPANSION_ALTERNATIVE);

        assertEquals(CLIENT_LEVEL_EXPANSION_LIST, result.getRowExpansions());
    }

    @Test
    public void expandInRows_ids_builderWithRowExpansion() {
        MultiAxisQueryBuilder result = instance.expandInRows("string", "string2");

        assertEquals(CLIENT_LEVEL_EXPANSION_LIST, result.getRowExpansions());
    }

    @Test
    public void expandInRows_clientIdentifiable_builderWithRowExpansion() {
        MultiAxisQueryBuilder result = instance.expandInRows(CLIENT_QUERY_FIELD, CLIENT_QUERY_FIELD_ALTERNATIVE);

        assertEquals(CLIENT_LEVEL_EXPANSION_LIST, result.getRowExpansions());
    }

    @Test
    public void expandInRows_nullClientIdentifiable_illegalArgumentException() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.expandInRows(((ClientIdentifiable[]) null));
            }
        });
    }

    @Test
    public void expandInColumns_clientExpandable_builderWithRowExpansion() {
        MultiAxisQueryBuilder result = instance.expandInColumns(CLIENT_LEVEL_EXPANSION, CLIENT_LEVEL_EXPANSION_ALTERNATIVE);

        assertEquals(CLIENT_LEVEL_EXPANSION_LIST, result.getColumnExpansions());
    }

    @Test
    public void expandInColumns_ids_builderWithRowExpansion() {
        MultiAxisQueryBuilder result = instance.expandInColumns("string", "string2");

        assertEquals(CLIENT_LEVEL_EXPANSION_LIST, result.getColumnExpansions());
    }

    @Test
    public void collapseInColumns_ids_builderWithRowExpansion() {
        MultiAxisQueryBuilder result = instance.collapseInColumns("string", "string2");

        assertEquals(CLIENT_LEVEL_EXPANSION_COLLAPSED_LIST, result.getColumnExpansions());
    }

    @Test
    public void collapseInRows_ids_builderWithRowExpansion() {
        MultiAxisQueryBuilder result = instance.collapseInRows("string", "string2");

        assertEquals(CLIENT_LEVEL_EXPANSION_COLLAPSED_LIST, result.getRowExpansions());
    }

    @Test
    public void orderBy_clientOrderArray_builderWithOrder() {
        MultiAxisQueryBuilder result = instance.orderBy(CLIENT_GENERIC_ORDER, CLIENT_PATH_ORDER);

        assertEquals(CLIENT_ORDER_LIST, result.getOrder());
    }

    @Test
    public void limit_clientLimit_builderWithGroups() {
        MultiAxisQueryBuilder result = instance.limit(CLIENT_LIMIT);

        assertEquals(CLIENT_LIMIT, result.getLimit());
    }

    @Test
    public void level_idAndClientDataSourceField_clientQueryLevel() {
        ClientQueryLevel result = MultiAxisQueryBuilder.level(CLIENT_ID, CLIENT_DATA_SOURCE_FIELD);

        assertEquals(CLIENT_ID, result.getId());
        assertEquals(CLIENT_NAME, result.getFieldName());
    }

    @Test
    public void level_idAndClientDataSourceLevel_clientQueryLevel() {
        ClientQueryLevel result = MultiAxisQueryBuilder.level(CLIENT_ID, CLIENT_DATA_SOURCE_LEVEL);

        assertEquals(CLIENT_ID, result.getId());
        assertEquals(CLIENT_DIMENSION, result.getDimension());
    }

    @Test
    public void level_idAndFieldNameAndCategorizer_clientQueryLevel() {
        ClientQueryLevel result = MultiAxisQueryBuilder.level(CLIENT_ID, CLIENT_NAME, CLIENT_CATEGORIZER);

        assertEquals(CLIENT_ID, result.getId());
        assertEquals(CLIENT_NAME, result.getFieldName());
        assertEquals(CLIENT_CATEGORIZER, result.getCategorizer());
    }

    @Test
    public void level_clientDataSourceField_clientQueryLevel() {
        ClientQueryLevel result = MultiAxisQueryBuilder.level(CLIENT_DATA_SOURCE_FIELD);

        assertEquals(CLIENT_NAME, result.getFieldName());
    }

    @Test
    public void level_clientDataSourceLevel_clientQueryLevel() {
        ClientQueryLevel result = MultiAxisQueryBuilder.level(CLIENT_DATA_SOURCE_LEVEL);

        assertEquals(CLIENT_DIMENSION, result.getDimension());
    }

    @Test
    public void allLevel_fieldNameAndDimension_clientAllLevel() {
        ClientQueryLevel result = MultiAxisQueryBuilder.allLevel(CLIENT_NAME, CLIENT_DIMENSION);

        assertEquals(ClientQueryLevel.ClientAllLevel.class, result.getClass());
        assertEquals(CLIENT_NAME, result.getFieldName());
        assertEquals(CLIENT_DIMENSION, result.getDimension());
    }

    @Test
    public void allLevel_dimension_clientAllLevel() {
        ClientQueryLevel result = MultiAxisQueryBuilder.allLevel(CLIENT_DIMENSION);

        assertEquals(ClientQueryLevel.ClientAllLevel.class, result.getClass());
        assertEquals(CLIENT_DIMENSION, result.getDimension());
    }

    @Test
    public void aggRef_ClientLevelAggregationsRef() {
        ClientQueryLevel result = MultiAxisQueryBuilder.aggRef();

        assertEquals(ClientQueryLevel.ClientLevelAggregationsRef.class, result.getClass());
    }

    @Test
    public void expand_id_clientLevelExpansion() {
        ClientLevelExpansion result = MultiAxisQueryBuilder.expand(CLIENT_ID);

        assertEquals(CLIENT_ID, result.getLevelReference());
        assertTrue(result.isExpanded());
    }

    @Test
    public void expandMember_id_clientMemberExpansion() {
        List<String> path = Collections.singletonList(CLIENT_ID);
        ClientMemberExpansion result = MultiAxisQueryBuilder.expandMember(CLIENT_ID);

        assertEquals(path, result.getPath());
        assertTrue(result.isExpanded());
    }

    @Test
    public void expandAggLevel_id_clientLevelExpansion() {
        ClientLevelExpansion result = MultiAxisQueryBuilder.expandAggLevel();

        assertTrue(result.isAggregationLevel());
        assertTrue(result.isExpanded());
    }

    @Test
    public void collapse_id_clientLevelExpansion() {
        ClientLevelExpansion result = MultiAxisQueryBuilder.collapse(CLIENT_ID);

        assertEquals(CLIENT_ID, result.getLevelReference());
        assertFalse(result.isExpanded());
    }

    @Test
    public void collapseMember_id_clientMemberExpansion() {
        ClientMemberExpansion result = MultiAxisQueryBuilder.collapseMember(CLIENT_ID);

        assertEquals(CLIENT_PATH, result.getPath());
        assertFalse(result.isExpanded());
    }

    @Test
    public void expansion_idAndExpanded_clientExpandable() {
        ClientExpandable result = MultiAxisQueryBuilder.expansion(CLIENT_ID, true);

        assertEquals(ClientLevelExpansion.class, result.getClass());
        assertEquals(CLIENT_ID, ((ClientLevelExpansion) result).getLevelReference());
        assertTrue(result.isExpanded());
    }

    @Test
    public void expansion_idWithPathSeparatorAndExpanded_clientExpandable() {
        ClientExpandable result = MultiAxisQueryBuilder.expansion(ClientMemberExpansion.PATH_SEPARATOR + CLIENT_ID, true);

        assertEquals(ClientMemberExpansion.class, result.getClass());
        assertEquals(CLIENT_PATH, ((ClientMemberExpansion) result).getPath());
        assertTrue(result.isExpanded());
    }

    @Test
    public void sanitizePathList_pathSegments_pathList() {
        List<String> result = MultiAxisQueryBuilder.sanitizePathList(CLIENT_PATH);

        assertEquals(CLIENT_PATH, result);
    }

    @Test
    public void sanitizePathList_nullPathSegments_pathList() {
        List<String> result = MultiAxisQueryBuilder.sanitizePathList(Collections.singletonList(((String) null)));

        assertTrue(result.isEmpty());
    }

    @Test
    public void sanitizePathList_emptyPathSegments_pathList() {
        List<String> result = MultiAxisQueryBuilder.sanitizePathList(Collections.singletonList(""));

        assertTrue(result.isEmpty());
    }

}
