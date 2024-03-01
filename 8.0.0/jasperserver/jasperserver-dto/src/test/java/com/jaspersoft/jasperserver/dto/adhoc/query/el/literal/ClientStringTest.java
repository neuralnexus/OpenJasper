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
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientStringTest extends BaseDTOPresentableTest<ClientString> {
    private static final String STRING_VALUE = "value";
    private static final String STRING_VALUE_ALTERNATIVE = "valueAlternative";

    private static final String TO_STRING_PATTERN = "'%s'";
    private static final String FORMATTED_STRING = String.format(TO_STRING_PATTERN, STRING_VALUE);

    private ClientELVisitor clientELVisitor;

    @BeforeEach
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientString> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setValue(STRING_VALUE_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setValue(null)
        );
    }

    @Override
    protected ClientString createFullyConfiguredInstance() {
        return new ClientString()
                .setValue(STRING_VALUE);
    }

    @Override
    protected ClientString createInstanceWithDefaultParameters() {
        return new ClientString();
    }

    @Override
    protected ClientString createInstanceFromOther(ClientString other) {
        return new ClientString(other);
    }

    @Test
    @Override
    public void generatedStringBeginsWithClassName() {
        assertEquals(FORMATTED_STRING, fullyConfiguredTestInstance.toString());
    }

    @Test
    public void instanceCanBeCreatedWithStringValue() {
        ClientString instance = new ClientString(STRING_VALUE);

        assertEquals(STRING_VALUE, instance.getValue());
    }

    @Test
    public void valueOfForCharacterParameterReturnsInstance() {
        ClientString instance = ClientString.valueOf('c');

        assertEquals("c", instance.getValue());
    }

    @Test
    public void clientELVisitorCanBeAccepted() {
        ClientString instance = new ClientString();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }
}
