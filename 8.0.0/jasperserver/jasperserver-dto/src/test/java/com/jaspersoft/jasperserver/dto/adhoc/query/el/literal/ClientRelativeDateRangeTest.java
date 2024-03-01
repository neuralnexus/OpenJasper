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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientRelativeDateRangeTest extends BaseDTOPresentableTest<ClientRelativeDateRange> {
    private static final String DATE_RANGE_VALUE = "YEAR";
    private static final String DATE_RANGE_VALUE_ALTERNATIVE = "DAY";
    private static final String NOT_DATE_RANGE_VALUE = "NOT_DATE_RANGE";

    private ClientELVisitor clientELVisitor;

    @BeforeEach
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientRelativeDateRange> prepareInstancesWithAlternativeParameters() {
        return Collections.singletonList(
                createFullyConfiguredInstance().setValue(DATE_RANGE_VALUE_ALTERNATIVE)
        );
    }

    @Override
    protected ClientRelativeDateRange createFullyConfiguredInstance() {
        return new ClientRelativeDateRange()
                .setValue(DATE_RANGE_VALUE);
    }

    @Override
    protected ClientRelativeDateRange createInstanceWithDefaultParameters() {
        return new ClientRelativeDateRange();
    }

    @Override
    protected ClientRelativeDateRange createInstanceFromOther(ClientRelativeDateRange other) {
        return new ClientRelativeDateRange(other);
    }

    @Test
    @Override
    public void generatedStringBeginsWithClassName() {
        assertEquals(DATE_RANGE_VALUE, fullyConfiguredTestInstance.toString());
    }

    @Test
    public void instanceCanBeCreatedWithStringValue() {
        ClientRelativeDateRange instance = new ClientRelativeDateRange(DATE_RANGE_VALUE);

        assertEquals(DATE_RANGE_VALUE, instance.getValue());
    }

    @Test
    public void clientELVisitorCanBeAccepted() {
        ClientRelativeDateRange instance = new ClientRelativeDateRange();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }

    @Test
    @Disabled("FIXME: This is now handled by an annotation validation")
    public void nullValueCanNotBeSet() {
        final ClientRelativeDateRange instance = new ClientRelativeDateRange(DATE_RANGE_VALUE);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.setValue(null);
            }
        });
    }

    @Test
    @Disabled("FIXME: This is now handled by an annotation validation")
    public void incorrectDateRangeValueCanNotBeSet() {
        final ClientRelativeDateRange instance = new ClientRelativeDateRange(DATE_RANGE_VALUE);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.setValue(NOT_DATE_RANGE_VALUE);
            }
        });
    }
}
