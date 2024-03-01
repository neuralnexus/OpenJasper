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

public class ClientNumberTest extends BaseDTOPresentableTest<ClientNumber> {
    private static final String INTEGER_VALUE_STRING = "23";
    
    private static final Integer INTEGER_VALUE = 23;
    private static final Integer INTEGER_VALUE_ALTERNATIVE = 24;

    private ClientELVisitor clientELVisitor;

    @BeforeEach
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientNumber> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setValue(INTEGER_VALUE_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setValue(null)
        );
    }

    @Override
    protected ClientNumber createFullyConfiguredInstance() {
        return new ClientNumber()
                .setValue(INTEGER_VALUE);
    }

    @Override
    protected ClientNumber createInstanceWithDefaultParameters() {
        return new ClientNumber();
    }

    @Override
    protected ClientNumber createInstanceFromOther(ClientNumber other) {
        return new ClientNumber(other);
    }

    @Test
    @Override
    public void generatedStringBeginsWithClassName() {
        assertEquals(INTEGER_VALUE_STRING, fullyConfiguredTestInstance.toString());
    }

    @Test
    public void instanceCanBeCreatedWithIntegerValue() {
        ClientNumber instance = new ClientNumber(INTEGER_VALUE);

        assertEquals(INTEGER_VALUE, instance.getValue());
    }

    @Test
    public void clientELVisitorCanBeAccepted() {
        ClientNumber instance = new ClientNumber();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }
}
