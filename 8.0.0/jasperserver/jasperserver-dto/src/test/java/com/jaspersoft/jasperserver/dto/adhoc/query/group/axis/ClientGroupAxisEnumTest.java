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

package com.jaspersoft.jasperserver.dto.adhoc.query.group.axis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class ClientGroupAxisEnumTest {
    private final static String COLUMNS_STRING = "columns";
    private final static String ROWS_STRING = "rows";
    private final static String GROUPS_STRING = "groups";

    @Test
    public void fromString_nullString_nullClientGroupAxisEnum() {
        ClientGroupAxisEnum instance = ClientGroupAxisEnum.fromString(null);
        assertNull(instance);
    }

    @Test
    public void fromString_columnsString_columnsClientGroupAxisEnum() {
        ClientGroupAxisEnum instance = ClientGroupAxisEnum.fromString(COLUMNS_STRING);
        assertEquals(ClientGroupAxisEnum.COLUMNS, instance);
    }

    @Test
    public void fromString_rowsString_rowsClientGroupAxisEnum() {
        ClientGroupAxisEnum instance = ClientGroupAxisEnum.fromString(ROWS_STRING);
        assertEquals(ClientGroupAxisEnum.ROWS, instance);
    }

    @Test
    public void fromString_groupsString_groupsClientGroupAxisEnum() {
        ClientGroupAxisEnum instance = ClientGroupAxisEnum.fromString(GROUPS_STRING);
        assertEquals(ClientGroupAxisEnum.GROUPS, instance);
    }

    @Test
    public void fromString_unsupportedString_illegalArgumentException() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                ClientGroupAxisEnum.fromString("unsupported");
            }
        });
    }

    @Test
    public void fromIndex_columnsIndex_columnsClientGroupAxisEnum() {
        ClientGroupAxisEnum instance = ClientGroupAxisEnum.fromIndex(0);
        assertEquals(ClientGroupAxisEnum.COLUMNS, instance);
    }

    @Test
    public void fromIndex_rowsIndex_rowsClientGroupAxisEnum() {
        ClientGroupAxisEnum instance = ClientGroupAxisEnum.fromIndex(1);
        assertEquals(ClientGroupAxisEnum.ROWS, instance);
    }

    @Test
    public void fromIndex_groupsIndex_groupsClientGroupAxisEnum() {
        ClientGroupAxisEnum instance = ClientGroupAxisEnum.fromIndex(2);
        assertEquals(ClientGroupAxisEnum.GROUPS, instance);
    }

    @Test
    public void fromIndex_unsupportedIndex_illegalArgumentException() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                ClientGroupAxisEnum.fromIndex(4);
            }
        });
    }

    @Test
    public void toString_enumName() {
        String toString = ClientGroupAxisEnum.COLUMNS.toString();
        assertEquals(COLUMNS_STRING, toString);
    }
}
