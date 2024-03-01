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

import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.from.ClientFrom;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientMultiAxisGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientLevelAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientMultiAxisQueryTest extends BaseDTOPresentableTest<ClientMultiAxisQuery> {
    private static final ClientQueryLevel CLIENT_QUERY_LEVEL = new ClientQueryLevel().setId("idLevel");
    private static final List<ClientQueryLevel> CLIENT_QUERY_LEVELS = Collections.singletonList(CLIENT_QUERY_LEVEL);
    private static final ClientMultiAxisGroupBy GROUP_BY = new ClientMultiAxisGroupBy().setColumns((ClientLevelAxis) new ClientLevelAxis().setItems(CLIENT_QUERY_LEVELS));
    private static final ClientQueryField CLIENT_QUERY_FIELD = new ClientQueryField().setId("id");
    private static final ClientSelect SELECT = new ClientSelect().setFields(Collections.singletonList(CLIENT_QUERY_FIELD));
    private static final List<ClientGenericOrder> ORDER_BY = Arrays.asList(new ClientGenericOrder().setAggregation(true), new ClientGenericOrder().setAscending(true));
    private static final int LIMIT = 23;
    private static final ClientFrom FROM = new ClientFrom().setDataSource("dataSource");
    private static final ClientWhere WHERE = new ClientWhere().setFilterExpression(new ClientExpressionContainer());

    private static final ClientMultiAxisGroupBy GROUP_BY_ALTERNATIVE = new ClientMultiAxisGroupBy().setColumns(new ClientLevelAxis());
    private static final ClientSelect SELECT_ALTERNATIVE = new ClientSelect().setFields(Collections.singletonList(new ClientQueryField().setId("idAlternative")));
    private static final List<ClientGenericOrder> ORDER_BY_ALTERNATIVE = Collections.singletonList(new ClientGenericOrder().setAggregation(false));
    private static final int LIMIT_ALTERNATIVE = 24;
    private static final ClientFrom FROM_ALTERNATIVE = new ClientFrom().setDataSource("dataSourceAlternative");
    private static final ClientWhere WHERE_ALTERNATIVE = new ClientWhere().setParameters(new HashMap<String, ClientExpressionContainer>());

    private ClientQueryVisitor clientQueryVisitor;

    @BeforeEach
    public void init() {
        clientQueryVisitor = Mockito.mock(ClientQueryVisitor.class);
    }

    @Override
    protected List<ClientMultiAxisQuery> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setGroupBy(GROUP_BY_ALTERNATIVE),
                createFullyConfiguredInstance().setSelect(SELECT_ALTERNATIVE),
                createFullyConfiguredInstance().setOrderBy(ORDER_BY_ALTERNATIVE),
                ((ClientMultiAxisQuery) createFullyConfiguredInstance().setFrom(FROM_ALTERNATIVE)),
                ((ClientMultiAxisQuery) createFullyConfiguredInstance().setLimit(LIMIT_ALTERNATIVE)),
                ((ClientMultiAxisQuery) createFullyConfiguredInstance().setWhere(WHERE_ALTERNATIVE)),
                // with null values
                createFullyConfiguredInstance().setGroupBy(null),
                createFullyConfiguredInstance().setSelect(null),
                createFullyConfiguredInstance().setOrderBy(null),
                ((ClientMultiAxisQuery) createFullyConfiguredInstance().setFrom(null)),
                ((ClientMultiAxisQuery) createFullyConfiguredInstance().setLimit(null)),
                ((ClientMultiAxisQuery) createFullyConfiguredInstance().setWhere(null))
        );
    }

    @Override
    protected ClientMultiAxisQuery createFullyConfiguredInstance() {
        return (ClientMultiAxisQuery) new ClientMultiAxisQuery()
                .setGroupBy(GROUP_BY)
                .setSelect(SELECT)
                .setOrderBy(ORDER_BY)
                .setFrom(FROM)
                .setLimit(LIMIT)
                .setWhere(WHERE);
    }

    @Override
    protected ClientMultiAxisQuery createInstanceWithDefaultParameters() {
        return new ClientMultiAxisQuery();
    }

    @Override
    protected ClientMultiAxisQuery createInstanceFromOther(ClientMultiAxisQuery other) {
        return new ClientMultiAxisQuery(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiAxisQuery expected, ClientMultiAxisQuery actual) {
        assertNotSame(expected.getSelect(), actual.getSelect());
        assertNotSame(expected.getGroupBy(), actual.getGroupBy());
        assertNotSame(expected.getWhere(), actual.getWhere());
        assertNotSame(expected.getFrom(), actual.getFrom());
        assertNotSameCollection(expected.getOrderBy(), actual.getOrderBy());
    }

    @Test
    public void getSelectedFields_withFieldsInSelect_containFields() {
        ClientMultiAxisQuery instance = createFullyConfiguredInstance();

        assertTrue(instance.getSelectedFields().contains(CLIENT_QUERY_FIELD));
    }

    @Test
    public void getSelectedFields_withNoFieldsInSelect_notContainFields() {
        ClientMultiAxisQuery instance = createInstanceWithDefaultParameters().setSelect(new ClientSelect());

        assertFalse(instance.getSelectedFields().contains(CLIENT_QUERY_FIELD));
    }

    @Test
    public void getSelectedFields_withNullSelect_notContainFields() {
        ClientMultiAxisQuery instance = createInstanceWithDefaultParameters();

        assertFalse(instance.getSelectedFields().contains(CLIENT_QUERY_FIELD));
    }

    @Test
    public void getSelectedFields_withAxesInGroupBy_containFields() {
        ClientMultiAxisQuery instance = createFullyConfiguredInstance();

        assertTrue(instance.getSelectedFields().contains(CLIENT_QUERY_LEVEL));
    }

    @Test
    public void getSelectedFields_withNoAxesInGroupBy_notContainFields() {
        ClientMultiAxisQuery instance = createInstanceWithDefaultParameters().setGroupBy(new ClientMultiAxisGroupBy());

        assertFalse(instance.getSelectedFields().contains(CLIENT_QUERY_LEVEL));
    }

    @Test
    public void getSelectedFields_withNullGroupBy_notContainFields() {
        ClientMultiAxisQuery instance = createInstanceWithDefaultParameters();

        assertFalse(instance.getSelectedFields().contains(CLIENT_QUERY_LEVEL));
    }

    @Test
    public void clientQueryVisitorCanBeAccepted() {
        ClientMultiAxisQuery instance = createFullyConfiguredInstance();
        instance.accept(clientQueryVisitor);

        verify(clientQueryVisitor).visit(SELECT);
        verify(clientQueryVisitor).visit(WHERE);
    }

    @Test
    public void nullClientQueryVisitorCanNonBeAccepted() {
        ClientMultiAxisQuery instance = createFullyConfiguredInstance();
        instance.accept(null);

        verify(clientQueryVisitor, never()).visit(SELECT);
        verify(clientQueryVisitor, never()).visit(WHERE);
    }

    @Test
    public void clientQueryVisitorCanBeAcceptedForSelectOnly() {
        ClientMultiAxisQuery instance = createInstanceWithDefaultParameters().setSelect(SELECT);
        instance.accept(clientQueryVisitor);

        verify(clientQueryVisitor).visit(SELECT);
        verify(clientQueryVisitor, never()).visit(WHERE);
    }

    @Test
    public void clientQueryVisitorCanBeAcceptedForWhereOnly() {
        ClientMultiAxisQuery instance = (ClientMultiAxisQuery) createInstanceWithDefaultParameters().setWhere(WHERE);
        instance.accept(clientQueryVisitor);

        verify(clientQueryVisitor, never()).visit(SELECT);
        verify(clientQueryVisitor).visit(WHERE);
    }
}
