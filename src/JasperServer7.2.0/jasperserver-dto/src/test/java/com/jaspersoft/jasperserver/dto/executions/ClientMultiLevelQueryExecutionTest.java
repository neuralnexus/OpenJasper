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

package com.jaspersoft.jasperserver.dto.executions;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiLevelQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientAggregates.countAll;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.allGroup;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.asc;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.desc;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.field;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.group;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.source;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiLevelQueryBuilder.select;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static java.util.Collections.singletonList;

/**
 * @author Alexei Skorodumov <askorodumov@tibco.com>
 * @version $Id$
 */

public class ClientMultiLevelQueryExecutionTest extends BaseDTOTest<ClientMultiLevelQueryExecution> {
    private static final String TYPE_STRING = "java.lang.String";
    private static final String SALES_STORE_REGION_SALES_CITY =
            "sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city";

    private static final ClientReferenceable DATASOURCE = new ClientReference("/public/Samples/Ad_Hoc_Views/just_a_data_source");
    private static final String STORE_SALES = "sales_fact_ALL.sales_fact_ALL__store_sales_2013";
    private static final String TYPE_DOUBLE = "java.lang.Double";
    private static final String SALES = "sales1";
    private static final Map<String, ClientExpressionContainer> params = new HashMap<String, ClientExpressionContainer>(1) {{
        put("DateP", new ClientExpressionContainer(new ClientRelativeDateRange().setValue("WEEK-1")));
    }};

    @Override
    protected List<ClientMultiLevelQueryExecution> prepareInstancesWithAlternativeParameters() {


        return Arrays.asList(
                createFullyConfiguredInstance().setDataSource(new ClientReference("DataSourceUri")),
                createFullyConfiguredInstance().setParams(new ClientQueryParams().setOffset(new int[]{5, 6})),
                // with null values
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setParams(null),
                createFullyConfiguredInstance().setQuery(null),
                // executionWithField,
                createFullyConfiguredInstance().setQuery(select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                        .build()),
                // executionWithEmptySelect ,
                createFullyConfiguredInstance().setQuery(select().build()),
                // executionSelectTwoFields"
                createFullyConfiguredInstance().setQuery(select(new ClientQueryField().setFieldName("sales").setId("fieldName"),
                        new ClientQueryField().setFieldName("city").setId("fieldName2"))
                        .build()),
                // executionWithSelectAndGroupBy,
                createFullyConfiguredInstance().setQuery(select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                        .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                        .build()),
                // executionWithSelectAndWhere,
                createFullyConfiguredInstance().setQuery(
                        (ClientMultiLevelQuery) select(
                                new ClientQueryField().setFieldName("sales").setId("fieldName"))
                                .build()
                                .setWhere(new ClientWhere().setParameters(params))),
                // executionWithSelectOrderByAndWhere,
                createFullyConfiguredInstance().setQuery(
                        select(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))
                                .where(variable(SALES).lt(new BigDecimal("0.51")))
                                .orderBy(asc(SALES))
                                .build()),
                // executionWithAllGroup,
                createFullyConfiguredInstance().setQuery(
                        select(
                                singletonList(field(SALES, source(STORE_SALES, TYPE_DOUBLE))),
                                singletonList(countAll(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))))
                                .groupBy(allGroup())
                                .build()),
                // SelectSameDSFieldsTwiceWithAggregations,
                createFullyConfiguredInstance().setQuery(
                        select(
                                field("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                field("city2", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                .aggregates(
                                        countAll(field("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING))),
                                        countAll(field("city2", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING))))
                                .build()),
                // SortAscDetailsSalesFieldGroupedByCities,
                createFullyConfiguredInstance().setQuery(
                        select(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))
                                .orderBy(asc(SALES))
                                .groupBy(group("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                .build()),
                // SELECT_StoreName_AND_AGGREGATE_SalseFact_retunrNameDetailsAndSalesFactTotal,
                createFullyConfiguredInstance().setQuery(
                        select(
                                new ClientQueryField().setId("name")
                                        .setFieldName("sales_fact_ALL.sales__store.sales__store__store_name"))
                                .aggregates(
                                        new ClientQueryAggregatedField()
                                                .setFieldReference("sales_fact_ALL.sales_fact_ALL__store_sales_98")
                                                .setAggregateFunction("Sum"))
                                .build()),
                // GroupSalesByCity,
                createFullyConfiguredInstance().setQuery(
                        select(
                                singletonList(field(SALES, source(STORE_SALES, TYPE_DOUBLE))),
                                singletonList(countAll(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))))
                                .groupBy(group(
                                        "city1",
                                        "city",
                                        source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                .build()),
                // orderByTwoFields
                createFullyConfiguredInstance().setQuery(
                        select(field("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                .orderBy(
                                        desc(source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                        asc(source(STORE_SALES, TYPE_DOUBLE))
                                )
                                .build())
        );
    }

    @Override
    protected ClientMultiLevelQueryExecution createFullyConfiguredInstance() {
        ClientMultiLevelQueryExecution clientMultiLevelQueryExecution = new ClientMultiLevelQueryExecution();
        clientMultiLevelQueryExecution.setDataSource(DATASOURCE);
        clientMultiLevelQueryExecution.setQuery(new ClientMultiLevelQuery().setSelect(new ClientSelect()));
        clientMultiLevelQueryExecution.setParams(new ClientQueryParams().setOffset(new int[]{2, 3}));
        return clientMultiLevelQueryExecution;
    }

    @Override
    protected ClientMultiLevelQueryExecution createInstanceWithDefaultParameters() {
        return new ClientMultiLevelQueryExecution();
    }

    @Override
    protected ClientMultiLevelQueryExecution createInstanceFromOther(ClientMultiLevelQueryExecution other) {
        return new ClientMultiLevelQueryExecution(other);
    }
}
