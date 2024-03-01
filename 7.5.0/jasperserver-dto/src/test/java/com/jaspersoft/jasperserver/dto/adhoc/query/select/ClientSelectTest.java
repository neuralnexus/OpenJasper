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

package com.jaspersoft.jasperserver.dto.adhoc.query.select;

import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientSelectTest extends BaseDTOTest<ClientSelect> {
    private static final String FIELD_ID = "id";

    private static final ClientQueryField FIELD = new ClientQueryField().setId(FIELD_ID).setFieldName(FIELD_ID);
    private static final ClientQueryAggregatedField AGGREGATION = new ClientQueryAggregatedField().setType("type");

    private static final List<ClientQueryField> FIELD_LIST = Collections.singletonList(FIELD);
    private static final List<ClientQueryAggregatedField> AGGREGATIONS = Collections.singletonList(AGGREGATION);

    private static final List<ClientQueryField> FIELD_LIST_2 = Collections.singletonList(new ClientQueryField().setId("id2"));
    private static final List<ClientQueryAggregatedField> AGGREGATIONS_2 = Collections.singletonList(new ClientQueryAggregatedField().setType("type2"));

    @Override
    protected List<ClientSelect> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFields(FIELD_LIST_2),
                createFullyConfiguredInstance().setAggregations(AGGREGATIONS_2),
                // with null values
                createFullyConfiguredInstance().setFields(null),
                createFullyConfiguredInstance().setAggregations(null)
        );
    }

    @Override
    protected ClientSelect createFullyConfiguredInstance() {
        return new ClientSelect()
                .setFields(FIELD_LIST)
                .setAggregations(AGGREGATIONS);
    }

    @Override
    protected ClientSelect createInstanceWithDefaultParameters() {
        return new ClientSelect();
    }

    @Override
    protected ClientSelect createInstanceFromOther(ClientSelect other) {
        return new ClientSelect(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientSelect expected, ClientSelect actual) {
        assertNotSameCollection(expected.getFields(), actual.getFields());
        assertNotSameCollection(expected.getAggregations(), actual.getAggregations());
    }

    @Test
    public void fieldCanBeGotByIndex() {
        assertEquals(FIELD, fullyConfiguredTestInstance.getField(0));
    }

    @Test
    public void fieldCanBeGotById() {
        assertEquals(FIELD, fullyConfiguredTestInstance.getField(FIELD_ID));
    }

    @Test
    public void fieldCanNotBeGotByNotExistingId() {
        assertNull(fullyConfiguredTestInstance.getField("notExistingId"));
    }

    @Test
    public void exceptionWillBeThrownWhenGettingFieldByNullId() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                fullyConfiguredTestInstance.getField(null);
            }
        });
    }

    @Test
    public void instanceCanBeAcceptedByVisitor() {
        ClientQueryVisitor visitor = Mockito.mock(ClientQueryVisitor.class);
        fullyConfiguredTestInstance.accept(visitor);

        verify(visitor).visit(fullyConfiguredTestInstance);
        verify(visitor).visit(FIELD);
        verify(visitor).visit(AGGREGATION);
    }
}
