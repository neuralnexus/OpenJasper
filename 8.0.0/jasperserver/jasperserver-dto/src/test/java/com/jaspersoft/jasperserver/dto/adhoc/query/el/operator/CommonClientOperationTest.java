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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class CommonClientOperationTest {

    /*
     * fromDomElOperator
     */

    @Test
    public void fromDomElOperator_and_success() {
        ClientOperation operation = ClientOperation.fromDomElOperator("and");
        assertEquals(ClientOperation.AND, operation);
    }

    @Test
    public void fromDomElOperator_someUnSupportedValue_null() {
        ClientOperation operation = ClientOperation.fromDomElOperator("unsupported");
        assertNull(operation);
    }

    @Test
    public void fromDomElOperator_nullValue_null() {
        ClientOperation operation = ClientOperation.fromDomElOperator(null);
        assertNull(operation);
    }

    /*
     * isSupported
     */

    @Test
    public void isSupported_and_true() {
        assertTrue(ClientOperation.isSupported("and"));
    }

    @Test
    public void isSupported_someUnSupportedValue_false() {
        assertFalse(ClientOperation.isSupported("unsupported"));
    }

    @Test
    public void isSupported_nullValue_false() {
        assertFalse(ClientOperation.isSupported(null));
    }

    /*
     * valueOf
     */

    @Test
    public void valueOf_and() {
        ClientOperation operation = ClientOperation.valueOf("AND");
        assertEquals(ClientOperation.AND, operation);
    }

    @Test
    public void valueOf_someUnSupportedValue_throws_IllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        ClientOperation.valueOf("unsupported");
                    }
                }
        );
    }

    @Test
    public void valueOf_nullValue_throws() {
        assertThrows(
                NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        ClientOperation.valueOf(null);
                    }
                }
        );
    }

}