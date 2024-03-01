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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.from.ClientFrom;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientQueryGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup.ClientAllGroup.ALL_GROUP_ID;
import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientMultiLevelQueryTest extends BaseDTOPresentableTest<ClientMultiLevelQuery> {
    private static final List<ClientQueryGroup> GROUPS_FOR_GROUP_BY = Collections.singletonList(new ClientQueryGroup().setId("idGroupBy"));
    private static final ClientQueryGroupBy GROUP_BY = new ClientQueryGroupBy().setGroups(GROUPS_FOR_GROUP_BY);
    private static final ClientQueryField CLIENT_QUERY_FIELD = new ClientQueryField().setId("id");
    private static final ClientSelect SELECT = new ClientSelect().setFields(Collections.singletonList(CLIENT_QUERY_FIELD));
    private static final List<ClientGenericOrder> ORDER_BY = Arrays.asList(new ClientGenericOrder().setAggregation(true), new ClientGenericOrder().setAscending(true));
    private static final int LIMIT = 23;
    private static final ClientFrom FROM = new ClientFrom().setDataSource("dataSource");
    private static final ClientWhere WHERE = new ClientWhere().setFilterExpression(new ClientExpressionContainer());

    private static final ClientQueryGroupBy GROUP_BY_ALTERNATIVE = new ClientQueryGroupBy().setGroups(Collections.singletonList(new ClientQueryGroup().setId("idGroupByAlternative")));
    private static final ClientSelect SELECT_ALTERNATIVE = new ClientSelect().setFields(Collections.singletonList(new ClientQueryField().setId("idAlternative")));
    private static final List<ClientGenericOrder> ORDER_BY_ALTERNATIVE = Collections.singletonList(new ClientGenericOrder().setAggregation(false));
    private static final int LIMIT_ALTERNATIVE = 24;
    private static final ClientFrom FROM_ALTERNATIVE = new ClientFrom().setDataSource("dataSourceAlternative");
    private static final ClientWhere WHERE_ALTERNATIVE = new ClientWhere().setParameters(new HashMap<String, ClientExpressionContainer>());

    @Override
    protected List<ClientMultiLevelQuery> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setGroupBy(GROUP_BY_ALTERNATIVE),
                createFullyConfiguredInstance().setSelect(SELECT_ALTERNATIVE),
                createFullyConfiguredInstance().setOrderBy(ORDER_BY_ALTERNATIVE),
                ((ClientMultiLevelQuery) createFullyConfiguredInstance().setFrom(FROM_ALTERNATIVE)),
                ((ClientMultiLevelQuery) createFullyConfiguredInstance().setLimit(LIMIT_ALTERNATIVE)),
                ((ClientMultiLevelQuery) createFullyConfiguredInstance().setWhere(WHERE_ALTERNATIVE)),
                // with null values
                createFullyConfiguredInstance().setGroupBy(null),
                createFullyConfiguredInstance().setSelect(null),
                createFullyConfiguredInstance().setOrderBy(null),
                ((ClientMultiLevelQuery) createFullyConfiguredInstance().setFrom(null)),
                ((ClientMultiLevelQuery) createFullyConfiguredInstance().setLimit(null)),
                ((ClientMultiLevelQuery) createFullyConfiguredInstance().setWhere(null))
        );
    }

    @Override
    protected ClientMultiLevelQuery createFullyConfiguredInstance() {
        return (ClientMultiLevelQuery) new ClientMultiLevelQuery()
                .setGroupBy(GROUP_BY)
                .setSelect(SELECT)
                .setOrderBy(ORDER_BY)
                .setFrom(FROM)
                .setLimit(LIMIT)
                .setWhere(WHERE);
    }

    @Override
    protected ClientMultiLevelQuery createInstanceWithDefaultParameters() {
        return new ClientMultiLevelQuery();
    }

    @Override
    protected ClientMultiLevelQuery createInstanceFromOther(ClientMultiLevelQuery other) {
        return new ClientMultiLevelQuery(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiLevelQuery expected, ClientMultiLevelQuery actual) {
        assertNotSame(expected.getSelect(), actual.getSelect());
        assertNotSame(expected.getGroupBy(), actual.getGroupBy());
        assertNotSame(expected.getWhere(), actual.getWhere());
        assertNotSame(expected.getFrom(), actual.getFrom());
        assertNotSameCollection(expected.getOrderBy(), actual.getOrderBy());
    }

    @Test
    public void getSelectedFields_withFieldsInSelect_containFields() {
        ClientMultiLevelQuery instance = createFullyConfiguredInstance();

        assertTrue(instance.getSelectedFields().contains(CLIENT_QUERY_FIELD));
    }

    @Test
    public void checkFieldStringConstructorsInQuery() {
        ClientMultiLevelQuery query = new ClientMultiLevelQuery();
        query.setSelect(
                new ClientSelect()
                    .setFieldsStr(Arrays.asList(new String[] {"field1 AS f1"}))
                    .setAggregationsStr(Arrays.asList(new String[] {"aggField1 AS agf1"}))
        );
        query.setGroupBy(
                new ClientQueryGroupBy()
                    .setGroupsStr(Arrays.asList(new String[] {
                            ALL_GROUP_ID,
                            "group1 AS g1"
                    }))
        );
        query.setOrderByStr(Arrays.asList(new String[] {
            "f1 DESC", "f2 ASC"
        }));

        ClientQueryField f1 = new ClientQueryField().setFieldName("field1").setId("f1");
        assertTrue(query.getSelectedFields().contains(f1));

        ClientQueryGroup.ClientAllGroup allGroup = new ClientQueryGroup.ClientAllGroup();
        assertTrue(query.getGroups().contains(allGroup));
        ClientQueryGroup g1 = new ClientQueryGroup().setFieldName("group1").setId("g1");
        assertTrue(query.getGroups().contains(g1));

        ClientGenericOrder o1 = new ClientGenericOrder().setFieldReference("f1").setAscending(false);
        assertTrue(query.getOrderBy().contains(o1));
        ClientGenericOrder o2 = new ClientGenericOrder().setFieldReference("f2").setAscending(true);
        assertTrue(query.getOrderBy().contains(o2));
    }
}
