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

package com.jaspersoft.jasperserver.dto.executions;

import com.fasterxml.jackson.databind.JsonNode;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiLevelQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.MultiLevelQueryBuilder;
import com.jaspersoft.jasperserver.dto.adhoc.query.QueryExecutionRequestTest;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 1/27/16 3:47PM
 */
public class QueryExecutionRequestSerializationToJSONTest extends QueryExecutionRequestTest {
    private static final String FIXTURES_PATH = "query/request/serialization/";

    @Test
    public void ensureSelectZeroField() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select()
                .build();
        JsonNode json = toJsonObject(fixture(FIXTURES_PATH + "ensureSelectZeroFieldQuery.json"));

        assertThat(toJsonObject(request(cq)), is(json));
    }

    @Test
    public void ensureSelectOneField() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .build();
        JsonNode json = toJsonObject(fixture(FIXTURES_PATH + "ensureSelectOneFieldQuery.json"));

        assertThat(toJsonObject(request(cq)), is(json));
    }


    @Test
    public void ensureSelectTwoFields() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"),
                        new ClientQueryField().setFieldName("city").setId("fieldName2"))
                .build();
        JsonNode json = toJsonObject(fixture(FIXTURES_PATH + "ensureSelectTwoFieldsQuery.json"));

        assertThat(toJsonObject(request(cq)), is(json));
    }

    @Test
    public void ensureSelectOneField_groupByCity() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                .build();
        JsonNode json = toJsonObject(fixture(FIXTURES_PATH + "ensureSelectOneField_groupByCityQuery.json"));

        assertThat(toJsonObject(request((ClientMultiLevelQuery) cq)), is(json));
    }

    @Test
    public void ensureWhereParameterIsRelativeDateRange() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .build();

        ClientWhere where = new ClientWhere();
        Map<String, ClientExpressionContainer> params = new HashMap<String, ClientExpressionContainer>(1);
        params.put("DateP", new ClientExpressionContainer(new ClientRelativeDateRange().setValue("WEEK-1")));
        where.setParameters(params);
        cq.setWhere(where);

        JsonNode expectedJson = toJsonObject(fixture(FIXTURES_PATH + "ensureWhereParameterIsRelativeDateRangeQuery.json"));
        JsonNode actualJson = toJsonObject(request((ClientMultiLevelQuery) cq));

        assertThat(actualJson, is(expectedJson));
    }
}