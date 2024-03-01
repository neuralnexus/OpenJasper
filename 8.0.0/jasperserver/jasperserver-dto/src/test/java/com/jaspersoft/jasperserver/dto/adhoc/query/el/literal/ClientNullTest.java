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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.literal;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientNullTest {
    private static final String NULL_VALUE_STRING = "NULL";

    private static final Object ANY_VALUE = 23L;

    private ClientELVisitor clientELVisitor;
    private ClientNull instance;

    @BeforeEach
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
        instance = new ClientNull();
    }

    @Test
    public void generatedStringBeginsWithClassName() {
        assertEquals(NULL_VALUE_STRING, instance.toString());
    }

    @Test
    public void clientELVisitorCanBeAccepted() {
        ClientNull instance = new ClientNull();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }

    @Test
    public void deepClonedInstanceEqualsToOriginalInstanceWithDefaultParameters() {
        ClientNull copied = instance.deepClone();

        assertEquals(copied, instance);
    }

    @Test
    public void copiedInstanceEqualsToOriginalInstanceWithDefaultParameters() {
        ClientNull copied = new ClientNull(instance);

        assertEquals(copied, instance);
    }

    @Test
    public void instanceHaveNullValueAfterValueSet() {
        instance.setValue(ANY_VALUE);

        assertNull(instance.getValue());
    }
}
