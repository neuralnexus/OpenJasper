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

package com.jaspersoft.jasperserver.dto.adhoc.query.group;

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientLevelAxis;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientMultiAxisGroupByTest extends BaseDTOPresentableTest<ClientMultiAxisGroupBy> {
    private static final ClientLevelAxis COLUMNS = ((ClientLevelAxis) new ClientLevelAxis().setItems(
            Collections.singletonList(new ClientQueryLevel().setId("idColumns"))
    ));
    private static final ClientLevelAxis ROWS = ((ClientLevelAxis) new ClientLevelAxis().setItems(
            Collections.singletonList(new ClientQueryLevel().setId("idRows"))
    ));

    private static final ClientLevelAxis COLUMNS_ALTERNATIVE = ((ClientLevelAxis) new ClientLevelAxis().setItems(
            Collections.singletonList(new ClientQueryLevel().setId("idColumnsAlternative"))
    ));
    private static final ClientLevelAxis ROWS_ALTERNATIVE = ((ClientLevelAxis) new ClientLevelAxis().setItems(
            Collections.singletonList(new ClientQueryLevel().setId("idRowsAlternative"))
    ));

    @Override
    protected List<ClientMultiAxisGroupBy> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setColumns(COLUMNS_ALTERNATIVE),
                createFullyConfiguredInstance().setRows(ROWS_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setColumns(null),
                createFullyConfiguredInstance().setRows(null)
        );
    }

    @Override
    protected ClientMultiAxisGroupBy createFullyConfiguredInstance() {
        return new ClientMultiAxisGroupBy()
                .setColumns(COLUMNS)
                .setRows(ROWS);
    }

    @Override
    protected ClientMultiAxisGroupBy createInstanceWithDefaultParameters() {
        return new ClientMultiAxisGroupBy();
    }

    @Override
    protected ClientMultiAxisGroupBy createInstanceFromOther(ClientMultiAxisGroupBy other) {
        return new ClientMultiAxisGroupBy(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiAxisGroupBy expected, ClientMultiAxisGroupBy actual) {
        assertNotSame(expected.getColumns(), actual.getColumns());
        assertNotSame(expected.getRows(), actual.getRows());
    }

    @Test
    public void keySetReturnsColumnsAndRowsKeysWhenColumnsAndRowsAreSet() {
        Set<String> expected = new HashSet<String>(Arrays.asList("columns", "rows"));

        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        Set<String> result = instance.keySet();
        assertEquals(expected, result);
    }

    @Test
    public void keySetReturnsEmptySetWhenColumnsAndRowsAreNotSet() {
        Set<String> expected = new HashSet<String>();

        ClientMultiAxisGroupBy instance = createInstanceWithDefaultParameters();
        Set<String> result = instance.keySet();
        assertEquals(expected, result);
    }

    @Test
    public void getAxisReturnsColumnsFor0Index() {
        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        int axisIndex = 0;

        ClientLevelAxis result = instance.getAxis(axisIndex);

        assertEquals(COLUMNS, result);
    }

    @Test
    public void getAxisReturnsRowsFor1Index() {
        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        int axisIndex = 1;

        ClientLevelAxis result = instance.getAxis(axisIndex);

        assertEquals(ROWS, result);
    }

    @Test
    public void getAxisThrowExceptionFor2Index() {
        final ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        final int axisIndex = 2;

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.getAxis(axisIndex);
            }
        });
    }

    @Test
    public void getReturnsColumnsForColumnsString() {
        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        ClientLevelAxis result = instance.get("columns");
        assertEquals(COLUMNS, result);
    }

    @Test
    public void getReturnsRowsForRowsString() {
        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        ClientLevelAxis result = instance.get("rows");
        assertEquals(ROWS, result);
    }

    @Test
    public void getReturnsNullForOtherString() {
        final ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        ClientLevelAxis result = instance.get("other");
        assertNull(result);
    }

    @Test
    public void getAxesReturnsRowsAndColumnsList() {
        List<ClientLevelAxis> expected = Arrays.asList(ROWS, COLUMNS);

        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        List<ClientLevelAxis> result = instance.getAxes();
        assertEquals(expected, result);
    }

    @Test
    public void addAxisSetsAxisAsColumnsForColumnsClientGroupAxisEnum() {
        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        instance.addAxis(ClientGroupAxisEnum.COLUMNS, COLUMNS_ALTERNATIVE);
        assertEquals(COLUMNS_ALTERNATIVE, instance.getColumns());
    }

    @Test
    public void addAxisSetsAxisAsRowsForRowsClientGroupAxisEnum() {
        ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        instance.addAxis(ClientGroupAxisEnum.ROWS, ROWS_ALTERNATIVE);
        assertEquals(ROWS_ALTERNATIVE, instance.getRows());
    }

    @Test
    public void addAxisThrowExceptionForOtherClientGroupAxisEnum() {
        final ClientMultiAxisGroupBy instance = createFullyConfiguredInstance();
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.addAxis(ClientGroupAxisEnum.GROUPS, null);
            }
        });
    }
}
